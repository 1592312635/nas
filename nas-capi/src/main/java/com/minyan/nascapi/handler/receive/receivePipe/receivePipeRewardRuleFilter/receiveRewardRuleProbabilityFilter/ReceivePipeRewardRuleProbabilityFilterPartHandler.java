package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter.receiveRewardRuleProbabilityFilter;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.minyan.nascommon.Enum.RewardLimitKeyEnum;
import com.minyan.nascommon.Enum.SendTypeEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardRulePO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
  private static final Random random = new Random();

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

    // 重新计算概率
    probabilityMap.values().stream()
        .reduce(Double::sum)
        .ifPresent(
            sum -> {
              // 对所有value除以求和
              probabilityMap.forEach(
                  (key, value) -> {
                    probabilityMap.put(key, value / sum);
                  });
            });

    // 通过概率抽取中奖奖品规则
    Long targetRewardRuleId = getRandomKeyByProbability(probabilityMap);
    context.setFinalRewardRuleList(
        rewardRulePOList.stream()
            .filter(
                rewardRulePO -> {
                  return rewardRulePO.getId().equals(targetRewardRuleId);
                })
            .collect(Collectors.toList()));
  }

  /**
   * 随机命中map中的key
   *
   * @param probabilityMap
   * @return
   */
  Long getRandomKeyByProbability(Map<Long, Double> probabilityMap) {
    // 创建累积概率列表
    List<Double> cumulativeProbabilities = Lists.newArrayList();
    double cumulativeSum = 0.0;

    for (Double probability : probabilityMap.values()) {
      cumulativeSum += probability;
      cumulativeProbabilities.add(cumulativeSum);
    }

    // 生成一个在 [0, 1) 范围内的随机数
    double randomValue = random.nextDouble();

    // 根据随机数选择键
    for (int i = 0; i < cumulativeProbabilities.size(); i++) {
      if (randomValue < cumulativeProbabilities.get(i)) {
        return new ArrayList<>(probabilityMap.keySet()).get(i);
      }
    }

    // 如果没有找到（理论上不会发生），返回 null
    return null;
  }
}
