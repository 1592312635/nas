package com.minyan.nascapi.handler.receive;

import com.minyan.nascommon.vo.context.ReceiveSendContext;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/29 22:50
 */
@Service
public abstract class ReceiveAbstractHandler implements ReceiveHandler {
  @Override
  public void fallBack(ReceiveSendContext context) {}
}
