package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.receiveLimitValid;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.utils.TimeUtil;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription 领取有效门槛校验处理器
 * @author minyan.he
 * @date 2024/11/2 10:13
 */
public class ReceiveLimitExpireCheckHandler implements ReceiveLimitCheckHandler {
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

  /**
   * 通过receiveLimit解析门槛json信息
   *
   * @param receiveLimitPO
   * @return
   */
  public ReceiveLimitJsonDto analyzeReceiveLimitJsonByReceiveLimitPO(
      ReceiveLimitPO receiveLimitPO) {
    ReceiveLimitJsonDto receiveLimitJsonDto = null;
    if (ObjectUtils.isEmpty(receiveLimitPO) || StringUtils.isEmpty(receiveLimitPO.getLimitJson())) {
      return receiveLimitJsonDto;
    }
    try {
      receiveLimitJsonDto =
          JSONObject.parseObject(receiveLimitPO.getLimitJson(), ReceiveLimitJsonDto.class);
    } catch (Exception e) {
      logger.info(
          "[ReceiveRuleManagerImpl][analyzeReceiveLimitJsonByReceiveLimitPO]解析领取门槛json时发生异常，门槛信息：{}",
          JSONObject.toJSONString(receiveLimitPO),
          e);
    }
    return receiveLimitJsonDto;
  }
}
