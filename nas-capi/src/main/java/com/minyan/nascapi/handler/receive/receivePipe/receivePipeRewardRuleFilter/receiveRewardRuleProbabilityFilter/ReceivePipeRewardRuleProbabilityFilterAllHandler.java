package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.receiveRewardRuleProbabilityFilter;

import com.minyan.nascommon.Enum.RewardTypeEnum;
import com.minyan.nascommon.Enum.SendTypeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2025/1/1 11:27
 */
@Service
public class ReceivePipeRewardRuleProbabilityFilterAllHandler
    implements ReceivePipeRewardRuleProbabilityFilterInterfaceHandler {
  @Override
  public Boolean match(Integer sendType) {
    return SendTypeEnum.ALL.getValue().equals(sendType);
  }

  @Override
  public void handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    context.getFinalRewardRuleList().addAll(context.getRewardRulePOList());

    // 如果只有未中奖设置为未中奖，否则去除未中奖
    if (!CollectionUtils.isEmpty(context.getFinalRewardRuleList())
        && context.getFinalRewardRuleList().size() > 1) {
      context
          .getFinalRewardRuleList()
          .removeIf(
              rewardRulePO ->
                  RewardTypeEnum.DEFAULT.getValue().equals(rewardRulePO.getRewardType()));
    }
  }
}
