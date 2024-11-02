package com.minyan.nascapi.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.minyan.nascapi.handler.receive.ReceiveAbstractHandler;
import com.minyan.nascapi.service.ReceiveService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ReceiveSendContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/30 21:24
 */
public class ReceiveServiceImpl implements ReceiveService {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveServiceImpl.class);
  @Autowired private List<ReceiveAbstractHandler> receiveHandlers;

  @Override
  public ApiResult<Boolean> send(CReceiveSendParam param) {
    ReceiveSendContext receiveSendContext = new ReceiveSendContext();
    receiveSendContext.setParam(param);
    List<ReceiveAbstractHandler> fallBackHandlers = Lists.newArrayList();
    Boolean result = true;
    try {
      for (ReceiveAbstractHandler receiveHandler : receiveHandlers) {
        fallBackHandlers.add(receiveHandler);
        result = receiveHandler.handle(receiveSendContext);
        if (!result) {
          logger.info(
              "[ReceiveServiceImpl][send]奖励发放内部异常，无需回滚，请求参数：{}", JSONObject.toJSONString(param));
          return ApiResult.build(
              CodeEnum.RECEIVE_SEND_INNER_EXCEPTION.getCode(),
              CodeEnum.RECEIVE_SEND_INNER_EXCEPTION.getMessage(),
              result);
        }
      }
    } catch (Exception e) {
      logger.info(
          "[ReceiveServiceImpl][send]奖励发放异常，开始回滚，请求参数：{}", JSONObject.toJSONString(param), e);
      for (ReceiveAbstractHandler receiveFallBackHandler : fallBackHandlers) {
        receiveFallBackHandler.fallBack(receiveSendContext);
      }
    }
    return ApiResult.buildSuccess(result);
  }
}
