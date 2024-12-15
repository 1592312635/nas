package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receivePipe.receivePipeSendReward.ReceivePipeSendRewardInterfaceHandler;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.RewardRulePO;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private List<ReceivePipeSendRewardInterfaceHandler> receivePipeSendRewardInterfaceHandlers;

  @SneakyThrows
  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    List<RewardRulePO> sendRewardRuleList = context.getFinalRewardRuleList();
    if (CollectionUtils.isEmpty(sendRewardRuleList)) {
      logger.info(
          "[ReceivePipeSendRewardHandler][handler]奖品发放管道领取项为空，请求参数：{}",
          JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.SEND_REWARD_RULE_IS_EMPTY);
    }

    for (RewardRulePO rewardRulePO : sendRewardRuleList) {
      // 记录nas_send_flow数据用于后续（注意待发放状态）

      ReceivePipeSendRewardInterfaceHandler targetReceivePipeSendRewardInterfaceHandler = receivePipeSendRewardInterfaceHandlers.stream()
              .filter(
                      receivePipeSendRewardInterfaceHandler ->
                              receivePipeSendRewardInterfaceHandler.match(rewardRulePO.getRewardType()))
              .findFirst()
              .orElse(null);

    }
    return null;
  }

  // todo 构建nasSendFlow实体创建build方法
}
