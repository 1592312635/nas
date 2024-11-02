package com.minyan.nascapi.handler.receive;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.vo.context.ReceiveSendContext;
import com.minyan.nasdao.NasActivityEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 领取校验handler&数据补全
 * @author minyan.he
 * @date 2024/10/29 22:50
 */
@Order(10)
@Service
public class ReceiveCheckHandler extends ReceiveAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveCheckHandler.class);
  @Autowired private NasActivityEventDAO activityEventDAO;

  @Override
  public Boolean handle(ReceiveSendContext context) {
    CReceiveSendParam param = context.getParam();
    if (ObjectUtils.isEmpty(param.getEventType()) && ObjectUtils.isEmpty(param.getEventId())) {
      logger.info(
          "[ReceiveCheckHandler][handle]请求参数事件id和事件类型不能同时为空，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }

    // 数据补全
    if (ObjectUtils.isEmpty(param.getEventType())) {
      QueryWrapper<ActivityEventPO> activityEventPOQueryWrapper = new QueryWrapper<>();
      activityEventPOQueryWrapper
          .lambda()
          .eq(ActivityEventPO::getEventId, param.getEventId())
          .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      ActivityEventPO activityEventPO = activityEventDAO.selectOne(activityEventPOQueryWrapper);
      if (ObjectUtils.isEmpty(activityEventPO)) {
        logger.info(
            "[ReceiveCheckHandler][handle]奖励发放事件id传入有误，无对应事件，请求参数：{}",
            JSONObject.toJSONString(param));
        return false;
      }
      param.setEventType(activityEventPO.getEventType());
      param.setModuleId(activityEventPO.getModuleId());
    }
    if (ObjectUtils.isEmpty(param.getEventId())) {
      QueryWrapper<ActivityEventPO> activityEventPOQueryWrapper = new QueryWrapper<>();
      activityEventPOQueryWrapper
          .lambda()
          .eq(ActivityEventPO::getActivityId, param.getActivityId())
          .eq(
              !ObjectUtils.isEmpty(param.getModuleId()),
              ActivityEventPO::getModuleId,
              param.getModuleId())
          .eq(ActivityEventPO::getEventType, param.getEventType())
          .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      ActivityEventPO activityEventPO = activityEventDAO.selectOne(activityEventPOQueryWrapper);
      if (ObjectUtils.isEmpty(activityEventPO)) {
        logger.info(
            "[ReceiveCheckHandler][handle]奖励发放事件事件类型传入有误，无对应事件，请求参数：{}",
            JSONObject.toJSONString(param));
        return false;
      }
      param.setEventId(activityEventPO.getEventId());
      param.setModuleId(activityEventPO.getModuleId());
    }
    context.setParam(param);

    // 如果有需要下面可以增加一个事件类型校验handler，用于处理不同事件的不同参数校验规则
    return true;
  }
}
