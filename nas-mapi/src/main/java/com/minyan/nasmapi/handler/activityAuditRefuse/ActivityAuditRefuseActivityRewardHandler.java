package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityRewardPO;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasActivityRewardDAO;
import com.minyan.nasdao.NasActivityRewardTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核不通过活动奖励信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:16
 */
@Order(20)
@Service
public class ActivityAuditRefuseActivityRewardHandler extends ActivityAuditRefuseAbstractHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseActivityRewardHandler.class);
  @Autowired private NasActivityRewardDAO activityRewardDAO;
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    // 删除临时表活动奖品信息
    UpdateWrapper<ActivityRewardTempPO> activityRewardTempPODeleteWrapper = new UpdateWrapper<>();
    activityRewardTempPODeleteWrapper
        .lambda()
        .set(ActivityRewardTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityRewardTempPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityRewardTempDAO.update(null, activityRewardTempPODeleteWrapper);

    // 主表活动奖励信息同步临时表
    QueryWrapper<ActivityRewardPO> activityRewardPOQueryWrapper = new QueryWrapper<>();
    activityRewardPOQueryWrapper
        .lambda()
        .eq(ActivityRewardPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityRewardPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityRewardPO> activityRewardPOS =
        activityRewardDAO.selectList(activityRewardPOQueryWrapper);
    if (!CollectionUtils.isEmpty(activityRewardPOS)) {
      for (ActivityRewardPO activityRewardPO : activityRewardPOS) {
        activityRewardTempDAO.insert(activityRewardPOToActivityRewardTempPO(activityRewardPO));
      }
    }

    return null;
  }

  /**
   * 活动奖励信息转活动奖励临时表信息
   *
   * @param activityRewardPO
   * @return
   */
  ActivityRewardTempPO activityRewardPOToActivityRewardTempPO(ActivityRewardPO activityRewardPO) {
    ActivityRewardTempPO activityRewardTempPO = new ActivityRewardTempPO();
    activityRewardTempPO.setActivityId(activityRewardPO.getActivityId());
    activityRewardTempPO.setRewardId(activityRewardPO.getId());
    activityRewardTempPO.setRewardType(activityRewardPO.getRewardType());
    activityRewardTempPO.setRewardName(activityRewardPO.getRewardName());
    activityRewardTempPO.setBatchCode(activityRewardPO.getBatchCode());
    activityRewardTempPO.setImageUrl(activityRewardPO.getImageUrl());
    return activityRewardTempPO;
  }
}
