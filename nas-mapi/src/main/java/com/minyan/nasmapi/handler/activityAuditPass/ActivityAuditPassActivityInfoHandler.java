package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动审核活动基本信息同步handler
 * @author minyan.he
 * @date 2024/10/9 16:36
 */
@Order(10)
@Service
public class ActivityAuditPassActivityInfoHandler extends ActivityAuditPassAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ActivityAuditPassActivityInfoHandler.class);
  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    // 查询临时表数据
    MActivityInfoAuditParam param = context.getParam();
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
          "[ActivityAuditActivityInfoHandler][handle]审核活动时待审核活动不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_ACTIVITY_NOT_EXIST);
    }

    // 主表数据删除
    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, activityInfoTempPO.getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO oldActivityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);
    if (!ObjectUtils.isEmpty(oldActivityInfoPO)) {
      UpdateWrapper<ActivityInfoPO> activityInfoPOUpdateWrapper = new UpdateWrapper<>();
      activityInfoPOUpdateWrapper
          .lambda()
          .set(ActivityInfoPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(ActivityInfoPO::getActivityId, param.getActivityId());
      activityInfoDAO.update(null, activityInfoPOUpdateWrapper);
    }

    // 临时表数据同步主表
    activityInfoDAO.insert(activityInfoTempPOToACtivityInfoPO(activityInfoTempPO));
    return null;
  }

  /**
   * 将临时表数据转换为主表数据
   *
   * @param activityInfoTempPO
   * @return
   */
  ActivityInfoPO activityInfoTempPOToACtivityInfoPO(ActivityInfoTempPO activityInfoTempPO) {
    ActivityInfoPO activityInfoPO = new ActivityInfoPO();
    activityInfoPO.setActivityId(activityInfoTempPO.getActivityId());
    activityInfoPO.setActivityName(activityInfoTempPO.getActivityName());
    activityInfoPO.setStatus(activityInfoTempPO.getStatus());
    activityInfoPO.setAuditStatus(activityInfoTempPO.getAuditStatus());
    activityInfoPO.setBeginTime(activityInfoTempPO.getBeginTime());
    activityInfoPO.setEndTime(activityInfoTempPO.getEndTime());
    return activityInfoPO;
  }
}
