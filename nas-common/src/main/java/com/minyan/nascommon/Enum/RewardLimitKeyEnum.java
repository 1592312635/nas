package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 奖品规则门槛枚举
 * @author minyan.he
 * @date 2024/11/21 21:28
 */
@Getter
@AllArgsConstructor
public enum RewardLimitKeyEnum {
  USER_TYPE("userType", "用户类型"),
  PRIORITY("priority", "优先级"),
  AMOUNT("amount", "金额"),
  CURRENCY_TYPE("currencyType", "货币类型"),
  PROBABILITY("probability", "概率(和为1)"),
  REWARD_TIMES("rewardTimes", "发奖次数"),
  ;

  private final String value;
  private final String desc;
}
