package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动审核不通过活动基本信息处理
 * @author minyan.he
 * @date 2024/10/12 10:40
 */
@Order(10)
@Service
public class ActivityAuditRefuseActivityInfoHandler extends ActivityAuditRefuseAbstractHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseActivityInfoHandler.class);

  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 活动状态验证
    QueryWrapper<ActivityInfoTempPO> activityInfoTempPOQueryWrapper = new QueryWrapper<>();
    activityInfoTempPOQueryWrapper
        .lambda()
        .eq(ActivityInfoTempPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoTempPO::getAuditStatus, AuditStatusEnum.DEFAULT.getValue())
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoTempPO activityInfoTempPO =
        activityInfoTempDAO.selectOne(activityInfoTempPOQueryWrapper);
    if (ObjectUtils.isEmpty(activityInfoTempPO)) {
      logger.info(
          "[ActivityAuditRefuseActivityInfoHandler][handle]审核活动时待审核活动不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_ACTIVITY_NOT_EXIST);
    }
    if (!AuditStatusEnum.DEFAULT.getValue().equals(activityInfoTempPO.getAuditStatus())) {
      logger.info(
          "[ActivityAuditRefuseActivityInfoHandler][handle]审核活动时待审核活动状态异常，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_ACTIVITY_STATUS_ERROR);
    }

    // 删除临时表数据
    UpdateWrapper<ActivityInfoTempPO> activityInfoTempPOUpdateWrapper = new UpdateWrapper<>();
    activityInfoTempPOUpdateWrapper
        .lambda()
        .set(ActivityInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityInfoTempPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    // 主表数据填充
    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);
    if (!ObjectUtils.isEmpty(activityInfoPO)) {
      ActivityInfoTempPO activityInfoTempPONew = activityInfoPOToActivityInfoTempPO(activityInfoPO);
      activityInfoTempDAO.insert(activityInfoTempPONew);
    }

    // 临时表审核状态变更
    UpdateWrapper<ActivityInfoTempPO> activityInfoTempPOStatusUpdateWrapper = new UpdateWrapper<>();
    activityInfoTempPOStatusUpdateWrapper
            .lambda()
            .set(ActivityInfoTempPO::getAuditStatus, AuditStatusEnum.REFUSE.getValue())
            .eq(ActivityInfoTempPO::getActivityId, activityInfoTempPO.getActivityId())
            .eq(ActivityInfoTempPO::getAuditStatus, AuditStatusEnum.DEFAULT.getValue())
            .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityInfoTempDAO.update(null, activityInfoTempPOStatusUpdateWrapper);
    return null;
  }

  /**
   * 审核失败活动主表信息转化临时表信息
   *
   * @param activityInfoPO
   * @return
   */
  ActivityInfoTempPO activityInfoPOToActivityInfoTempPO(ActivityInfoPO activityInfoPO) {
    ActivityInfoTempPO activityInfoTempPO = new ActivityInfoTempPO();
    activityInfoTempPO.setActivityId(activityInfoPO.getActivityId());
    activityInfoTempPO.setActivityName(activityInfoPO.getActivityName());
    activityInfoTempPO.setStatus(activityInfoPO.getStatus());
    activityInfoTempPO.setAuditStatus(AuditStatusEnum.REFUSE.getValue());
    activityInfoTempPO.setBeginTime(activityInfoPO.getBeginTime());
    activityInfoTempPO.setEndTime(activityInfoPO.getEndTime());
    return activityInfoTempPO;
  }
}
