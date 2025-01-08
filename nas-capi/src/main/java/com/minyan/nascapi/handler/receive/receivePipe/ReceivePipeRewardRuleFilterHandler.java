package com.minyan.nascapi.handler.receive.receivePipe;

import com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.ReceivePipeRewardRuleFilterInterfaceHandler;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品概率处理器
 * @author minyan.he
 * @date 2024/12/11 16:50
 */
@Order(40)
@Service
public class ReceivePipeRewardRuleFilterHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeRewardRuleFilterHandler.class);

  @Autowired
  private List<ReceivePipeRewardRuleFilterInterfaceHandler>
      receivePipeRewardRuleFilterInterfaceHandlers;

  @Override
  public Boolean handle(ReceivePipeContext context) {

    // 依次执行过滤逻辑
    for (ReceivePipeRewardRuleFilterInterfaceHandler receivePipeRewardRuleFilterInterfaceHandler :
        receivePipeRewardRuleFilterInterfaceHandlers) {
      receivePipeRewardRuleFilterInterfaceHandler.handle(context);
    }

    return null;
  }
}
