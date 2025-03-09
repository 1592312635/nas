package com.minyan.nascapi.handler.receive.receivePipe;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 发奖管道终结
 * @author minyan.he
 * @date 2025/3/9 21:15
 */
@Order(100)
@Service
public class ReceivePipeEndHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeEndHandler.class);

  @Override
  public Boolean handle(ReceivePipeContext context) {
    logger.info("[ReceivePipeEndHandler][ReceivePipeEndHandler]发奖管道结束");
    return true;
  }
}
