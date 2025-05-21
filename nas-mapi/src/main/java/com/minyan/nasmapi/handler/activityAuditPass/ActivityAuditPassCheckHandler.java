package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
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
 * @decription
 * @author minyan.he
 * @date 2025/5/21 15:54
 */
@Service
@Order(5)
public class ActivityAuditPassCheckHandler extends ActivityAuditPassAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ActivityAuditPassCheckHandler.class);
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);

    if (ObjectUtils.isEmpty(activityInfoPO)) {
      logger.info(
          "[ActivityAuditPassCheckHandler][handle]审核通过时活动不存在，请求参数：{}",
          JSONObject.toJSONString(context));
      return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
    } else if (AuditStatusEnum.PASS.getValue().equals(activityInfoPO.getAuditStatus())) {
      logger.info(
          "[ActivityAuditPassCheckHandler][handle]活动内容已审核，无需操作，请求参数：{}",
          JSONObject.toJSONString(context));
      return ApiResult.buildSuccess(null);
    }
    return null;
  }
}
