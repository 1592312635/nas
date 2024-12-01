package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.RewardRulePO;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 奖品发放handler
 * @author minyan.he
 * @date 2024/11/29 15:50
 */
@Order(70)
@Service
public class ReceivePipeSendRewardHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeSendRewardHandler.class);

  @SneakyThrows
  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    List<RewardRulePO> sendRewardRuleList = context.getSendRewardRuleList();
    if (CollectionUtils.isEmpty(sendRewardRuleList)) {
      logger.info(
          "[ReceivePipeSendRewardHandler][handler]奖品发放管道领取项为空，请求参数：{}",
          JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.SEND_REWARD_RULE_IS_EMPTY);
    }

    return null;
  }
}
