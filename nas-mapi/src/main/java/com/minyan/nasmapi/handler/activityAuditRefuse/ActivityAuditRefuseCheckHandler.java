package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nasdao.NasActivityInfoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动内容审核拒绝校验handler
 * @author minyan.he
 * @date 2025/5/21 16:01
 */
@Service
@Order(5)
public class ActivityAuditRefuseCheckHandler extends ActivityAuditRefuseAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ActivityAuditRefuseCheckHandler.class);
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);

    if (ObjectUtils.isEmpty(activityInfoPO)) {
      logger.info(
          "[ActivityAuditRefuseCheckHandler][handle]审核拒绝时活动不存在，请求参数：{}",
          JSONObject.toJSONString(context));
      return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
    } else if (AuditStatusEnum.PASS.getValue().equals(activityInfoPO.getAuditStatus())) {
      logger.info(
          "[ActivityAuditRefuseCheckHandler][handle]审核拒绝活动内容无变更，无需操作，请求参数：{}",
          JSONObject.toJSONString(context));
      return ApiResult.buildSuccess(null);
    }
    return null;
  }
}
