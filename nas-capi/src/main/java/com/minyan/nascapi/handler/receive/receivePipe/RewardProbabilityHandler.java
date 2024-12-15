package com.minyan.nascapi.handler.receive.receivePipe;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品概率处理器
 * @author minyan.he
 * @date 2024/12/11 16:50
 */
@Order(40)
@Service
public class RewardProbabilityHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(RewardProbabilityHandler.class);

  @Override
  public Boolean handle(ReceivePipeContext context) {

    return null;
  }
}
