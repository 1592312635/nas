package com.minyan.nascapi.handler.receive.receivePipe.receivePipeConsume;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascommon.dto.context.ReceivePipeContext;

/**
 * @decription 领取管道前置消耗物品消耗处理handler
 * @author minyan.he
 * @date 2024/12/8 11:42
 */
public interface ReceivePipeConsumeInterfaceHandler {
  Boolean match(Integer consumeType);

  void handle(ReceivePipeContext context, JSONObject consumeInfoJson);

  void fallBack(ReceivePipeContext context, JSONObject consumeInfoJson);
}
