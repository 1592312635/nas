package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.receiveLimitValid;

import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckAbstractHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import com.minyan.nascommon.po.ReceiveLimitPO;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
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
    return StringUtils.isNotEmpty(limitKey)
        && limitKey.startsWith(ReceiveLimitKeyEnum.STRING.getValue());
  }

  @Override
  public Boolean handle(ReceiveLimitCheckContext context) {
    ReceiveLimitPO receiveLimitPO = context.getReceiveLimitPO();
    Map<String, String> limitMap = context.getLimitMap();

    ReceiveLimitJsonDto receiveLimitJsonDto =
        analyzeReceiveLimitJsonByReceiveLimitPO(receiveLimitPO);
    if (ObjectUtils.isEmpty(receiveLimitJsonDto)) {
      String limitKey = receiveLimitPO.getLimitKey();
      return !ObjectUtils.isEmpty(limitMap)
          && limitMap.containsKey(limitKey)
          && StringUtils.isNotEmpty(limitMap.get(limitKey))
          && limitMap.get(limitKey).equals(receiveLimitJsonDto.getValue());
    }

    return false;
  }
}
