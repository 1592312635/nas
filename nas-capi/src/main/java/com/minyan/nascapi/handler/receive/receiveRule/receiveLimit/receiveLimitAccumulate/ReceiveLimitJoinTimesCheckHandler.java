package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.receiveLimitAccumulate;

import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckAbstractHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import org.springframework.stereotype.Service;

/**
 * @decription 参与次数校验处理handler
 * @author minyan.he
 * @date 2024/11/11 20:53
 */
@Service
public class ReceiveLimitJoinTimesCheckHandler extends ReceiveLimitCheckAbstractHandler {
  @Override
  public Boolean checkLimitKey(String limitKey) {
    return ReceiveLimitKeyEnum.STRING.getValue().equals(limitKey);
  }

  @Override
  public Boolean handle(ReceiveLimitCheckContext context) {
    return null;
  }
}
