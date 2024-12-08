package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 消耗物品类型枚举
 * @author minyan.he
 * @date 2024/12/8 11:45
 */
@AllArgsConstructor
@Getter
public enum ConsumeTypeEnum {
  CURRENCY(1, "代币"),
  ;

  private final Integer value;
  private final String desc;
}
