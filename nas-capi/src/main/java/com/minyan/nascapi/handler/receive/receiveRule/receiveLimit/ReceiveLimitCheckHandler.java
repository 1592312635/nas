package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit;

import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;

/**
 * @decription 领取门槛处理handler
 * @author minyan.he
 * @date 2024/11/2 10:06
 */
public interface ReceiveLimitCheckHandler {
  Boolean checkLimitKey(String limitKey);

  Boolean handle(ReceiveLimitCheckContext context);
}
