package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 奖品类型枚举
 * @author minyan.he
 * @date 2024/11/29 16:01
 */
@AllArgsConstructor
@Getter
public enum RewardTypeEnum {
  DEFAULT(0, "未中奖"),
  CURRENCY(1, "代币"),
  ;

  private final Integer value;
  private final String desc;
}
