package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品规则过滤器-库存
 * @author minyan.he
 * @date 2025/1/8 21:24
 */
@Service
@Order(30)
public class ReceivePipeRewardRuleFilterInventoryHandler
    implements ReceivePipeRewardRuleFilterInterfaceHandler {

  @Override
  public void handle(ReceivePipeContext context) {
    // todo 获得优先保留包括无库存奖品在内的所有奖品规则，库存优先删除无库存奖品后续重新计算概率
  }
}
