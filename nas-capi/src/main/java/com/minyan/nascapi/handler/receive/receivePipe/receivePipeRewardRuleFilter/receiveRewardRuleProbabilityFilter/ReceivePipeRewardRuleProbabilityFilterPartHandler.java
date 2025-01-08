package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.receiveRewardRuleProbabilityFilter;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascommon.Enum.RewardLimitKeyEnum;
import com.minyan.nascommon.Enum.SendTypeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardRulePO;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2025/1/1 11:28
 */
@Service
public class ReceivePipeRewardRuleProbabilityFilterPartHandler
    implements ReceivePipeRewardRuleProbabilityFilterInterfaceHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(ReceivePipeRewardRuleProbabilityFilterPartHandler.class);

  @Override
  public Boolean match(Integer sendType) {
    return SendTypeEnum.PART.getValue().equals(sendType);
  }

  @Override
  public void handle(ReceivePipeContext context) {
    List<RewardRulePO> rewardRulePOList = context.getRewardRulePOList();
    List<RewardLimitPO> rewardLimitPOList = context.getRewardLimitPOList();
    Map<Long, Double> probabilityMap =
        rewardLimitPOList.stream()
            .filter(
                rewardLimitPO -> {
                  return RewardLimitKeyEnum.PROBABILITY
                      .getValue()
                      .equals(rewardLimitPO.getLimitKey());
                })
            .collect(
                Collectors.toMap(
                    RewardLimitPO::getRewardRuleId,
                    rewardLimitPO -> {
                      try {
                        JSONObject jsonObject =
                            JSONObject.parseObject(rewardLimitPO.getLimitJson());
                        return jsonObject.getDouble(RewardLimitKeyEnum.PROBABILITY.getValue());
                      } catch (Exception e) {
                        logger.info(
                            "[ReceivePipeRewardRuleFilterPartHandler][handle]获取奖品规则概率时，异常：{}",
                            e.getMessage());
                        throw e;
                      }
                    }));
    // todo 确认奖品是否还有库存，无库存的奖品不计入中奖概率

    // 重新计算概率
      probabilityMap.values().stream().reduce(Double::sum).ifPresent(sum -> {
        // 对所有value除以求和
        probabilityMap.forEach((key, value) -> {
          probabilityMap.put(key, value / sum);
        });
      });

      23
  }
}
