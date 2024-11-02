package com.minyan.nascapi.handler.receive.receiveRule;

import com.minyan.nascommon.dto.context.ReceiveSendContext;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/29 22:49
 */
public interface ReceiveRuleCheckHandler {
  Boolean handle(ReceiveSendContext context);
}
