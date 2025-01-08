package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;

/**
 * @decription 顺序执行器-用于执行奖励规则过滤器
 * @author minyan.he
 * @date 2025/1/8 21:10
 */
public interface ReceivePipeRewardRuleFilterInterfaceHandler {
  void handle(ReceivePipeContext context);
}
