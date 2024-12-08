package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receivePipe.receivePipeConsume.ReceivePipeConsumeInterfaceHandler;
import com.minyan.nascommon.Enum.ReceiveLimitJsonKeyEnum;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription 事件前置消耗处理器
 * @author minyan.he
 * @date 2024/12/8 11:21
 */
@Order(50)
@Service
public class ReceivePipeConsumeHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeConsumeHandler.class);

  @Autowired private List<ReceivePipeConsumeInterfaceHandler> receivePipeConsumeInterfaceHandlers;

  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    List<ReceiveLimitPO> receiveLimitPOList = context.getReceiveLimitPOList();
    ReceiveLimitPO consumeInfoReceiveLimitPO =
        receiveLimitPOList.stream()
            .filter(
                receiveLimitPO ->
                    ReceiveLimitKeyEnum.CONSUME_INFO
                        .getValue()
                        .equals(receiveLimitPO.getLimitKey()))
            .findFirst()
            .orElse(null);
    if (ObjectUtils.isEmpty(consumeInfoReceiveLimitPO)
        || StringUtils.isEmpty(consumeInfoReceiveLimitPO.getLimitJson())) {
      logger.info(
          "[ReceivePipeConsumeHandler][handle]前置消耗信息不存在，消耗处理器通过，请求参数：{}，消耗信息：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(consumeInfoReceiveLimitPO));
      return null;
    }
    Integer consumeType = 0;
    JSONObject consumeInfoJson = null;
    try {
      // 解析消耗物品类型及具体消耗信息
      consumeInfoJson = JSONObject.parseObject(consumeInfoReceiveLimitPO.getLimitJson());
      consumeType = consumeInfoJson.getInteger(ReceiveLimitJsonKeyEnum.CONSUME_TYPE.getValue());
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeConsumeHandler][handle]前置消耗信息解析异常，消耗信息：{}",
          JSONObject.toJSONString(consumeInfoReceiveLimitPO));
      return false;
    }

    ReceivePipeConsumeInterfaceHandler targetReceivePipeConsumeInterfaceHandler = null;
    try {
      Integer finalConsumeType = consumeType;
      targetReceivePipeConsumeInterfaceHandler =
          receivePipeConsumeInterfaceHandlers.stream()
              .filter(
                  receivePipeConsumeInterfaceHandler ->
                      receivePipeConsumeInterfaceHandler.match(finalConsumeType))
              .findFirst()
              .orElse(null);
      if (!ObjectUtils.isEmpty(targetReceivePipeConsumeInterfaceHandler)) {
        targetReceivePipeConsumeInterfaceHandler.handle(context, consumeInfoJson);
      }
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeConsumeHandler][handle]前置消耗处理异常，请求参数：{}，消耗信息：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(consumeInfoReceiveLimitPO));
      if (!ObjectUtils.isEmpty(targetReceivePipeConsumeInterfaceHandler)
          && !CollectionUtils.isEmpty(context.getPipeResultMap())
          && context
              .getPipeResultMap()
              .get(targetReceivePipeConsumeInterfaceHandler.getClass().getName())) {
        targetReceivePipeConsumeInterfaceHandler.fallBack(context, consumeInfoJson);
      }
    }

    return null;
  }
}
