package com.minyan.nascapi.handler.receive.receivePipe.receivePipeConsume;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.service.HttpService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.ConsumeTypeEnum;
import com.minyan.nascommon.Enum.ReceiveLimitJsonKeyEnum;
import com.minyan.nascommon.Enum.SystemEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.httpRequest.CurrencyConfirmRequest;
import com.minyan.nascommon.httpRequest.CurrencyDeductRequest;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.utils.SnowFlakeUtil;
import java.math.BigDecimal;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @decription 领取管道前置消耗物品消耗代理处理handler
 * @author minyan.he
 * @date 2024/12/8 11:44
 */
@Service
public class ReceivePipeConsumeCurrencyHandler implements ReceivePipeConsumeInterfaceHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeConsumeCurrencyHandler.class);

  @Autowired private HttpService httpService;

  @Override
  public Boolean match(Integer consumeType) {
    return ConsumeTypeEnum.CURRENCY.getValue().equals(consumeType);
  }

  /**
   * 对代币进行扣减
   *
   * @param context
   * @param consumeInfoJson
   */
  @SneakyThrows
  @Override
  public void handle(ReceivePipeContext context, JSONObject consumeInfoJson) {
    CReceiveSendParam param = context.getParam();
    ActivityEventPO activityEventPO = context.getActivityEventPO();
    Integer currencyType = null;
    BigDecimal amount = null;
    String behaviorCode = null, behaviorDesc = null;
    try {
      currencyType = consumeInfoJson.getInteger(ReceiveLimitJsonKeyEnum.CURRENCY_TYPE.getValue());
      amount = consumeInfoJson.getBigDecimal(ReceiveLimitJsonKeyEnum.AMOUNT.getValue());
      behaviorCode = consumeInfoJson.getString(ReceiveLimitJsonKeyEnum.BEHAVIOR_CODE.getValue());
      behaviorDesc = consumeInfoJson.getString(ReceiveLimitJsonKeyEnum.BEHAVIOR_DESC.getValue());
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeConsumeCurrencyHandler][handle]前置消耗处理代币扣减时解析异常，请求参数：{}，消耗信息：{}",
          JSONObject.toJSONString(param),
          consumeInfoJson);
    }
    if (StringUtils.isEmpty(behaviorCode) || StringUtils.isEmpty(behaviorDesc)) {
      behaviorCode = activityEventPO.getEventType();
      behaviorDesc = activityEventPO.getEventName();
    }

    // 调用扣减接口
    CurrencyDeductRequest currencyDeductRequest =
        buildCurrencyDeductRequest(param, currencyType, amount, behaviorCode, behaviorDesc);
    Boolean deductResult = httpService.deductCurrency(currencyDeductRequest);
    if (!deductResult) {
      logger.info(
          "[ReceivePipeConsumeCurrencyHandler][handle]前置消耗处理代币扣减失败，请求参数：{}",
          JSONObject.toJSONString(currencyDeductRequest));
      throw new CustomException(CodeEnum.CURRENCY_DEDUCT_FAIL);
    }

    context.getTempMap().put("currencyDeductOrderNo", currencyDeductRequest.getBusinessId());
    context.getPipeResultMap().put(this.getClass().getName(), true);
  }

  /**
   * 处理失败对已扣减代币撤回
   *
   * @param context
   * @param consumeInfoJson
   */
  @SneakyThrows
  @Override
  public void fallBack(ReceivePipeContext context, JSONObject consumeInfoJson) {
    CReceiveSendParam param = context.getParam();
    // 参数获取
    Integer currencyType = null;
    String orderNo = null;
    try {
      currencyType = consumeInfoJson.getInteger(ReceiveLimitJsonKeyEnum.CURRENCY_TYPE.getValue());
      orderNo = context.getTempMap().get("currencyDeductOrderNo").toString();
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeConsumeCurrencyHandler][fallBack]前置消耗处理代币确认回退时解析异常，请求参数：{}，消耗信息：{}",
          JSONObject.toJSONString(param),
          consumeInfoJson);
      throw new CustomException(CodeEnum.CURRENCY_CONFIRM_FAIL);
    }

    // 调用确认失败接口
    CurrencyConfirmRequest currencyDeductRequest =
        buildCurrencyConfirmRequest(param, currencyType, orderNo);
    Boolean deductResult = httpService.confirmCurrency(currencyDeductRequest);
    if (!deductResult) {
      logger.info(
          "[ReceivePipeConsumeCurrencyHandler][fallBack]前置消耗处理代币确认回退失败，请求参数：{}",
          JSONObject.toJSONString(currencyDeductRequest));
      throw new CustomException(CodeEnum.CURRENCY_CONFIRM_FAIL);
    }
  }

  /**
   * 构建代币扣减参数
   *
   * @param param
   * @param currencyType
   * @param amount
   * @param behaviorCode
   * @param behaviorDesc
   * @return
   */
  CurrencyDeductRequest buildCurrencyDeductRequest(
      CReceiveSendParam param,
      Integer currencyType,
      BigDecimal amount,
      String behaviorCode,
      String behaviorDesc) {
    CurrencyDeductRequest deductRequest = new CurrencyDeductRequest();
    deductRequest.setUserId(param.getUserId());
    deductRequest.setCurrencyType(currencyType);
    deductRequest.setDeductCurrency(amount);
    deductRequest.setBusinessId(
        String.format("%s:%s", SystemEnum.NAS.getValue(), SnowFlakeUtil.getDefaultSnowFlakeId()));
    deductRequest.setBehaviorCode(behaviorCode);
    deductRequest.setBehaviorDesc(behaviorDesc);
    return deductRequest;
  }

  /**
   * 构建代币确认参数
   *
   * @param param
   * @param currencyType
   * @param orderNo
   * @return
   */
  CurrencyConfirmRequest buildCurrencyConfirmRequest(
      CReceiveSendParam param, Integer currencyType, String orderNo) {
    CurrencyConfirmRequest confirmRequest = new CurrencyConfirmRequest();
    confirmRequest.setUserId(param.getUserId());
    confirmRequest.setOrderNo(orderNo);
    confirmRequest.setCurrencyType(currencyType);
    confirmRequest.setConfirmTag(2);
    return confirmRequest;
  }
}
