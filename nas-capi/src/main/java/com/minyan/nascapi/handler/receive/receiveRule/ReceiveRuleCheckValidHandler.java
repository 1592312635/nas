package com.minyan.nascapi.handler.receive.receiveRule;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receiveRule.receiveLimit.ReceiveLimitCheckHandler;
import com.minyan.nascommon.Enum.ReceiveLimitTypeEnum;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.dto.context.ReceiveLimitCheckContext;
import com.minyan.nascommon.dto.context.ReceiveSendContext;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 有效门槛处理handler
 * @author minyan.he
 * @date 2024/11/2 10:36
 */
@Order(10)
@Service
public class ReceiveRuleCheckValidHandler implements ReceiveRuleCheckHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveRuleCheckValidHandler.class);
  @Autowired List<ReceiveLimitCheckHandler> receiveLimitCheckHandlers;

  @Override
  public Boolean handle(ReceiveSendContext context) {
    CReceiveSendParam param = context.getParam();
    List<ReceiveLimitPO> receiveLimitList = context.getReceiveLimitList();
    ReceiveLimitCheckContext receiveLimitCheckContext = new ReceiveLimitCheckContext();
    receiveLimitCheckContext.setParam(param);

    // 把有效门槛过滤做有效校验
    for (ReceiveLimitPO receiveLimitPO :
        receiveLimitList.stream()
            .filter(
                receiveLimitPO ->
                    ReceiveLimitTypeEnum.VALID.getValue().equals(receiveLimitPO.getLimitType()))
            .collect(Collectors.toList())) {
      // 每次循环重置当前需要验证的门槛
      receiveLimitCheckContext.setReceiveLimitPO(receiveLimitPO);
      ReceiveLimitCheckHandler limitCheckHandler =
          receiveLimitCheckHandlers.stream()
              .filter(
                  receiveLimitCheckHandler ->
                      receiveLimitCheckHandler.checkLimitKey(receiveLimitPO.getLimitKey()))
              .findFirst()
              .orElse(null);
      if (!ObjectUtils.isEmpty(limitCheckHandler)) {
        logger.info(
            "[ReceiveRuleCheckValidHandler][handler]领取门槛不存在，请求参数：{}，门槛信息：{}",
            JSONObject.toJSONString(param),
            JSONObject.toJSONString(receiveLimitPO));
      }
      Boolean limitCheckResult = limitCheckHandler.handle(receiveLimitCheckContext);
      if (!limitCheckResult) {
        logger.info(
            "[ReceiveRuleCheckValidHandler][handler]门槛校验未通过，请求参数：{}，未通过门槛：{}",
            JSONObject.toJSONString(param),
            limitCheckHandler.getClass().getName());
        return false;
      }
    }
    return true;
  }
}
