package com.minyan.nascapi.handler.receive.receivePipe;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2024/11/13 21:02
 */
@Service
public abstract class ReceivePipeAbstractHandler implements ReceivePipeDealHandler {

  @Override
  public void fallBack(ReceivePipeContext context) {}
}
