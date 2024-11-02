package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityRewardPO;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
import com.minyan.nasdao.NasActivityRewardDAO;
import com.minyan.nasdao.NasActivityRewardTempDAO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 活动审核通过活动奖励信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:13
 */
@Order(20)
@Service
public class ActivityAuditPassActivityRewardHandler extends ActivityAuditPassAbstractHandler {
  private final static Logger logger = LoggerFactory.getLogger(ActivityAuditPassActivityRewardHandler.class);
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;
  @Autowired private NasActivityRewardDAO activityRewardDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询临时表活动奖品信息
    QueryWrapper<ActivityRewardTempPO> activityRewardTempPOQueryWrapper = new QueryWrapper<>();
    activityRewardTempPOQueryWrapper
        .lambda()
        .eq(ActivityRewardTempPO::getActivityId, param.getActivityId())
        .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityRewardTempPO> activityRewardTempPOS =
        activityRewardTempDAO.selectList(activityRewardTempPOQueryWrapper);
    if (CollectionUtils.isEmpty(activityRewardTempPOS)) {
      logger.info(
          "[ActivityAuditPassActivityRewardHandler][handle]活动审核通过活动奖励同步时无临时表活动奖励数据，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_REWARD_NOT_EXIST);
    }

    // 删除主表活动奖品信息
    QueryWrapper<ActivityRewardPO> activityRewardPOQueryWrapper = new QueryWrapper<>();
    activityRewardPOQueryWrapper
        .lambda()
        .eq(ActivityRewardPO::getActivityId, param.getActivityId())
        .eq(ActivityRewardPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityRewardPO> activityRewardPOS =
        activityRewardDAO.selectList(activityRewardPOQueryWrapper);
    if (!CollectionUtils.isEmpty(activityRewardPOS)) {
      UpdateWrapper<ActivityRewardPO> activityRewardPOUpdateWrapper = new UpdateWrapper<>();
      List<Long> deleteIds =
          activityRewardPOS.stream().map(ActivityRewardPO::getId).collect(Collectors.toList());
      activityRewardPOUpdateWrapper
          .lambda()
          .set(ActivityRewardPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ActivityRewardPO::getId, deleteIds);
      activityRewardDAO.update(null, activityRewardPOUpdateWrapper);
    }

    // 临时表活动奖励信息同步主表
    for (ActivityRewardTempPO activityRewardTempPO : activityRewardTempPOS) {
      activityRewardDAO.insert(activityRewardTempPOToActivityRewardPO(activityRewardTempPO));
    }
    return null;
  }

  /**
   * 临时表活动奖励信息转主表活动奖励信息
   *
   * @param activityRewardTempPO
   * @return
   */
  ActivityRewardPO activityRewardTempPOToActivityRewardPO(
      ActivityRewardTempPO activityRewardTempPO) {
    ActivityRewardPO activityRewardPO = new ActivityRewardPO();
    activityRewardPO.setActivityId(activityRewardTempPO.getActivityId());
    activityRewardPO.setRewardType(activityRewardTempPO.getRewardType());
    activityRewardPO.setRewardName(activityRewardTempPO.getRewardName());
    activityRewardPO.setBatchCode(activityRewardTempPO.getBatchCode());
    activityRewardPO.setImageUrl(activityRewardTempPO.getImageUrl());
    return activityRewardPO;
  }
}
