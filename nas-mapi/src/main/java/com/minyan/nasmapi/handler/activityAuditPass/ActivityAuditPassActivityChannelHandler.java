package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityChannelPO;
import com.minyan.nascommon.po.ActivityChannelTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
import com.minyan.nasdao.NasActivityChannelDAO;
import com.minyan.nasdao.NasActivityChannelTempDAO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 活动审核通过渠道信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:15
 */
@Order(40)
@Service
public class ActivityAuditPassActivityChannelHandler extends ActivityAuditPassAbstractHandler {
  private final static Logger logger = LoggerFactory.getLogger(ActivityAuditPassActivityChannelHandler.class);
  @Autowired private NasActivityChannelTempDAO activityChannelTempDAO;
  @Autowired private NasActivityChannelDAO activityChannelDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询临时表渠道信息
    QueryWrapper<ActivityChannelTempPO> activityChannelTempPOQueryWrapper = new QueryWrapper<>();
    activityChannelTempPOQueryWrapper
        .lambda()
        .eq(ActivityChannelTempPO::getActivityId, param.getActivityId())
        .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityChannelTempPO> activityChannelTempPOS =
        activityChannelTempDAO.selectList(activityChannelTempPOQueryWrapper);
    if (CollectionUtils.isEmpty(activityChannelTempPOS)) {
      logger.info(
          "[ActivityAuditPassActivityChannelHandler][handle]活动审核通过渠道审核时无临时表渠道信息，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_CHANNEL_NOT_EXIST);
    }

    // 删除主表渠道信息
    QueryWrapper<ActivityChannelPO> activityChannelPOQueryWrapper = new QueryWrapper<>();
    activityChannelPOQueryWrapper
        .lambda()
        .eq(ActivityChannelPO::getActivityId, param.getActivityId())
        .eq(ActivityChannelPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityChannelPO> activityChannelPOS =
        activityChannelDAO.selectList(activityChannelPOQueryWrapper);
    if (!CollectionUtils.isEmpty(activityChannelPOS)) {
      UpdateWrapper<ActivityChannelPO> activityChannelPOUpdateWrapper = new UpdateWrapper<>();
      List<Long> deleteIds =
          activityChannelPOS.stream().map(ActivityChannelPO::getId).collect(Collectors.toList());
      activityChannelPOUpdateWrapper
          .lambda()
          .set(ActivityChannelPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ActivityChannelPO::getId, deleteIds);
      activityChannelDAO.update(null, activityChannelPOUpdateWrapper);
    }

    // 临时表渠道同步主表
    for (ActivityChannelTempPO activityChannelTempPO : activityChannelTempPOS) {
      activityChannelDAO.insert(activityChannelTempPOToActivityChannelPO(activityChannelTempPO));
    }
    return null;
  }

  /**
   * 临时渠道信息转化渠道信息
   *
   * @param activityChannelTempPO
   * @return
   */
  ActivityChannelPO activityChannelTempPOToActivityChannelPO(
      ActivityChannelTempPO activityChannelTempPO) {
    ActivityChannelPO activityChannelPO = new ActivityChannelPO();
    activityChannelPO.setActivityId(activityChannelTempPO.getActivityId());
    activityChannelPO.setChannelCode(activityChannelTempPO.getChannelCode());
    activityChannelPO.setChannelName(activityChannelTempPO.getChannelName());
    return activityChannelPO;
  }
}
