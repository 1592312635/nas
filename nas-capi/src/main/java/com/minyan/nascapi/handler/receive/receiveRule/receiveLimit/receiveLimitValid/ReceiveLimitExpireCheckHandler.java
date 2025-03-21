package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.receiveLimitValid;

import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckAbstractHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.utils.TimeUtil;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

/**
 * @decription 领取时间有效门槛校验处理器
 * @author minyan.he
 * @date 2024/11/2 10:13
 */
public class ReceiveLimitExpireCheckHandler extends ReceiveLimitCheckAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveLimitExpireCheckHandler.class);

  @Override
  public Boolean checkLimitKey(String limitKey) {
    return ReceiveLimitKeyEnum.EXPIRE.getValue().equals(limitKey);
  }

  @Override
  public Boolean handle(ReceiveLimitCheckContext context) {
    Date now = new Date();
    ReceiveLimitPO receiveLimitPO = context.getReceiveLimitPO();

    ReceiveLimitJsonDto receiveLimitJsonDto =
        analyzeReceiveLimitJsonByReceiveLimitPO(receiveLimitPO);
    if (ObjectUtils.isEmpty(receiveLimitJsonDto)) {
      Date maxDate = TimeUtil.analyzeDate(receiveLimitJsonDto.getMax());
      Date minDate = TimeUtil.analyzeDate(receiveLimitJsonDto.getMin());
      return (ObjectUtils.isEmpty(maxDate) || maxDate.after(now))
          && (ObjectUtils.isEmpty(minDate) || minDate.before(now));
    }

    return false;
  }
}
