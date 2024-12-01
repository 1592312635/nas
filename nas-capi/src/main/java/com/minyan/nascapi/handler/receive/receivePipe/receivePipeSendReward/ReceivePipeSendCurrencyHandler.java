package com.minyan.nascapi.handler.receive.receivePipe.receivePipeSendReward;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.service.HttpService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.RewardLimitKeyEnum;
import com.minyan.nascommon.Enum.RewardTypeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.httpRequest.CurrencySendRequest;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardRulePO;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription 代币发放处理handler
 * @author minyan.he
 * @date 2024/11/29 16:00
 */
@Service
public class ReceivePipeSendCurrencyHandler implements ReceivePipeSendRewardInterfaceHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeSendCurrencyHandler.class);

  @Autowired private HttpService httpService;

  @Override
  public Boolean match(Integer rewardType) {
    return RewardTypeEnum.CURRENCY.getValue().equals(rewardType);
  }

  @SneakyThrows
  @Override
  public void handle(ReceivePipeContext context, RewardRulePO rewardRulePO) {
    CReceiveSendParam param = context.getParam();
    List<RewardLimitPO> rewardLimitPOList = context.getRewardLimitPOList();
    // 获取本次实际发放奖品项的奖品规则门槛
    rewardLimitPOList =
        rewardLimitPOList.stream()
            .filter(rewardLimitPO -> rewardRulePO.getId().equals(rewardLimitPO.getRewardRuleId()))
            .collect(Collectors.toList());

    // 解析金额门槛
    BigDecimal amount = BigDecimal.ZERO;
    RewardLimitPO amountRewardLimit =
        rewardLimitPOList.stream()
            .filter(
                rewardLimitPO ->
                    RewardLimitKeyEnum.AMOUNT.getValue().equals(rewardLimitPO.getLimitKey()))
            .findFirst()
            .orElse(null);
    if (ObjectUtils.isEmpty(amountRewardLimit)
        || StringUtils.isEmpty(amountRewardLimit.getLimitJson())) {
      logger.info(
          "[ReceivePipeSendCurrencyHandler][handle]发放代币时，奖品规则门槛无代币发放金额，请求参数：{}，奖品规则：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(rewardRulePO));
      throw new CustomException(CodeEnum.SEND_REWARD_LIMIT_IS_EMPTY);
    }
    JSONObject amountJsonObject = JSONObject.parseObject(amountRewardLimit.getLimitJson());
    try {
      amount = amountJsonObject.getBigDecimal(RewardLimitKeyEnum.AMOUNT.getValue());
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeSendCurrencyHandler][handle]发放代币时，奖品规则门槛代币发放金额解析异常，请求参数：{}，奖品规则：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(rewardRulePO));
      throw new CustomException(CodeEnum.SEND_REWARD_LIMIT_EXCEPTION);
    }

    // 解析类型门槛
    Integer currencyType = 1;
    RewardLimitPO currencyTypeRewardLimit =
        rewardLimitPOList.stream()
            .filter(
                rewardLimitPO ->
                    RewardLimitKeyEnum.CURRENCY_TYPE.getValue().equals(rewardLimitPO.getLimitKey()))
            .findFirst()
            .orElse(null);
    if (ObjectUtils.isEmpty(currencyTypeRewardLimit)
        || StringUtils.isEmpty(currencyTypeRewardLimit.getLimitJson())) {
      logger.info(
          "[ReceivePipeSendCurrencyHandler][handle]发放代币时，奖品规则门槛无代币类型，请求参数：{}，奖品规则：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(rewardRulePO));
      throw new CustomException(CodeEnum.SEND_REWARD_LIMIT_IS_EMPTY);
    }
    JSONObject currencyTypeJsonObject =
        JSONObject.parseObject(currencyTypeRewardLimit.getLimitJson());
    try {
      currencyType = currencyTypeJsonObject.getInteger(RewardLimitKeyEnum.CURRENCY_TYPE.getValue());
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeSendCurrencyHandler][handle]发放代币时，奖品规则门槛代币发放代币类型解析异常，请求参数：{}，奖品规则：{}",
          JSONObject.toJSONString(param),
          JSONObject.toJSONString(rewardRulePO));
      throw new CustomException(CodeEnum.SEND_REWARD_LIMIT_EXCEPTION);
    }

    CurrencySendRequest currencySendRequest = buildCurrencySendRequest(param, amount, currencyType);
    Boolean sendResult = httpService.sendCurrency(currencySendRequest);
    logger.info(
        "[ReceivePipeSendCurrencyHandler][handle]领取接口发放代币结束，请求参数：{}，返回结果：{}",
        JSONObject.toJSONString(param),
        sendResult);
    if (!sendResult) {
      throw new CustomException(CodeEnum.CURRENCY_SEND_FAIL);
    }
  }

  /**
   * 构建代币发放参数
   *
   * @param param
   * @param amount
   * @param currencyType
   * @return
   */
  CurrencySendRequest buildCurrencySendRequest(
      CReceiveSendParam param, BigDecimal amount, Integer currencyType) {
    CurrencySendRequest currencySendRequest = new CurrencySendRequest();
    currencySendRequest.setUserId(param.getUserId());
    currencySendRequest.setAddCurrency(amount);
    currencySendRequest.setCurrencyType(currencyType);
    currencySendRequest.setBusinessId(param.getEventId().toString());
    currencySendRequest.setBehaviorCode(param.getEventType());
    currencySendRequest.setBehaviorDesc(param.getEventType());
    return currencySendRequest;
  }
}
