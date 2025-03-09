package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 领取门槛json的key标识枚举
 * @author minyan.he
 * @date 2024/12/8 11:55
 */
@AllArgsConstructor
@Getter
public enum LimitJsonKeyEnum {
  // 门槛信息
  VALUE("value", "值"),
  MAX("max", "最大值"),
  MIN("min", "最小值"),

  // 特殊配置信息
  CONSUME_TYPE("consume_type", "消耗类型"),
  CURRENCY_TYPE("currency_type", "代币类型"),
  AMOUNT("amount", "金额"),
  BEHAVIOR_CODE("behavior_code", "行为标识"),
  BEHAVIOR_DESC("behavior_desc", "行为描述"),
  ;
  private final String value;
  private final String desc;
}
