package com.minyan.nascapi.handler.receive.receivePipe;

import com.minyan.nascommon.dto.context.ReceivePipeContext;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/29 22:49
 */
public interface ReceivePipeDealHandler {
  Boolean handle(ReceivePipeContext context);

  void fallBack(ReceivePipeContext context);
}
