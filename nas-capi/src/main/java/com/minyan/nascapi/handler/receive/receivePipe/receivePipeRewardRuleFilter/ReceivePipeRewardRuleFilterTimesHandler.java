package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nasdao.NasSendFlowDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品规则过滤器-次数
 * @author minyan.he
 * @date 2025/1/8 21:19
 */
@Service
@Order(20)
public class ReceivePipeRewardRuleFilterTimesHandler
    implements ReceivePipeRewardRuleFilterInterfaceHandler {

  @Autowired private NasSendFlowDAO sendFlowDAO;

  @Override
  public void handle(ReceivePipeContext context) {
    // 获取当前发奖次数匹配当前次数对应奖品规则
    
  }
}
