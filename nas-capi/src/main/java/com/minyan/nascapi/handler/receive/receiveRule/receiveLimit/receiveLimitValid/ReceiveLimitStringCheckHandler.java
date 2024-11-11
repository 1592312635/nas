package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.receiveLimitValid;

import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckAbstractHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import com.minyan.nascommon.po.ReceiveLimitPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * @decription 领取字符串有效门槛校验处理器
 * @author minyan.he
 * @date 2024/11/2 10:13
 */
public class ReceiveLimitStringCheckHandler extends ReceiveLimitCheckAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveLimitStringCheckHandler.class);

  @Override
  public Boolean checkLimitKey(String limitKey) {
    return ReceiveLimitKeyEnum.STRING.getValue().equals(limitKey);
  }

  @Override
  public Boolean handle(ReceiveLimitCheckContext context) {
    ReceiveLimitPO receiveLimitPO = context.getReceiveLimitPO();

    ReceiveLimitJsonDto receiveLimitJsonDto =
        analyzeReceiveLimitJsonByReceiveLimitPO(receiveLimitPO);
    if (ObjectUtils.isEmpty(receiveLimitJsonDto)) {

    }

    return false;
  }
}
