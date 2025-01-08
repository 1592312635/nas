package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.receiveRewardRuleProbabilityFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;

/**
 * @decription 奖品概率奖品筛选处理器
 * @author minyan.he
 * @date 2025/1/1 11:08
 */
public interface ReceivePipeRewardRuleProbabilityFilterInterfaceHandler {
  Boolean match(Integer sendType);

  void handle(ReceivePipeContext context);
}
