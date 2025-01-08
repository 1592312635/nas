package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.receiveRewardRuleProbabilityFilter.ReceivePipeRewardRuleProbabilityFilterInterfaceHandler;
import com.minyan.nascommon.Enum.ReceiveLimitKeyEnum;
import com.minyan.nascommon.Enum.SendTypeEnum;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription 奖品规则过滤器-概率过滤
 * @author minyan.he
 * @date 2025/1/8 21:12
 */
@Service
@Order(30)
public class ReceivePipeRewardRuleFilterProbabilityHandler
    implements ReceivePipeRewardRuleFilterInterfaceHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeRewardRuleFilterProbabilityHandler.class);

  @Autowired
  private List<ReceivePipeRewardRuleProbabilityFilterInterfaceHandler>
      receivePipeRewardRuleProbabilityFilterInterfaceHandlers;

  @Override
  public void handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    List<ReceiveRulePO> receiveRulePOList = context.getReceiveRulePOList();
    List<ReceiveLimitPO> receiveLimitPOList = context.getReceiveLimitPOList();

    // 获取发放规则对应的领取门槛
    ReceiveLimitPO sendTypeReceiveLimitPO =
        receiveLimitPOList.stream()
            .filter(
                receiveLimitPO ->
                    ReceiveLimitKeyEnum.SEND_TYPE.getValue().equals(receiveLimitPO.getLimitKey()))
            .findFirst()
            .orElse(null);
    if (ObjectUtils.isEmpty(sendTypeReceiveLimitPO)
        || StringUtils.isEmpty(sendTypeReceiveLimitPO.getLimitJson())) {
      logger.info(
          "[RewardProbabilityHandler][handle]奖品概率处理器处理时，奖品发放类型异常，请求参数：{}，奖品发放类型门槛：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(sendTypeReceiveLimitPO));
      return;
    }

    // 解析门槛获得发放类型
    Integer sendType = SendTypeEnum.PART.getValue();
    try {
      ReceiveLimitJsonDto receiveLimitJsonDto =
          JSONObject.parseObject(sendTypeReceiveLimitPO.getLimitJson(), ReceiveLimitJsonDto.class);
      sendType = Integer.valueOf(receiveLimitJsonDto.getValue());
    } catch (Exception e) {
      logger.info(
          "[RewardProbabilityHandler][handle]奖品概率处理器处理时，发放类型解析异常，请求参数：{}，奖品发放类型：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(sendTypeReceiveLimitPO));
      return;
    }

    // 应用当前领取规则下的发放奖品规则
    Integer finalSendType = sendType;
    receivePipeRewardRuleProbabilityFilterInterfaceHandlers.stream()
        .filter(handler -> handler.match(finalSendType))
        .findFirst()
        .ifPresent(handler -> handler.handle(context));
  }
}
