package com.minyan.nascapi.handler.receive.receivePipe.receivePipeSendReward;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.po.RewardRulePO;

/**
 * @decription 奖品发放处理handler
 * @author minyan.he
 * @date 2024/11/29 15:57
 */
public interface ReceivePipeSendRewardInterfaceHandler {
  Boolean match(Integer rewardType);

  void handle(ReceivePipeContext context, RewardRulePO rewardRulePO);
}
