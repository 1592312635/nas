package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.ActivityStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nasdao.NasActivityInfoDAO;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动状态验证器
 * @author minyan.he
 * @date 2024/11/13 21:10
 */
@Order(10)
@Service
public class ReceivePipeActivityCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ReceivePipeActivityCheckHandler.class);

  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @SneakyThrows
  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();

    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);

    if (ObjectUtils.isEmpty(activityInfoPO)) {
      logger.info(
          "[ReceivePipeActivityCheckDealHandler][handle]未查询到对应活动，请求参数：{}",
          JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.ACTIVITY_NOT_EXIST);
    }

    if (ObjectUtils.isEmpty(activityInfoPO.getStatus())
        || ActivityStatusEnum.STOP.getValue().equals(activityInfoPO.getStatus())) {
      logger.info(
          "[ReceivePipeActivityCheckDealHandler][handle]活动状态异常，请求参数：{}",
          JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.ACTIVITY_STATUS_NOT_RUN);
    }

    context.setActivityInfoPO(activityInfoPO);
    return null;
  }
}
