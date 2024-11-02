package com.minyan.nascapi.handler.receive;

import com.minyan.nascommon.dto.context.ReceiveSendContext;

/**
 * @decription 领取发放主流程handler
 * @author minyan.he
 * @date 2024/10/29 22:48
 */
public interface ReceiveHandler {
  Boolean handle(ReceiveSendContext context);

  void fallBack(ReceiveSendContext context);
}
