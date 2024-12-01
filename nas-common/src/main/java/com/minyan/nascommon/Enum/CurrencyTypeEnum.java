package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 默认币种枚举（注意币种不全）
 * @author minyan.he
 * @date 2024/12/1 10:49
 */
@AllArgsConstructor
@Getter
public enum CurrencyTypeEnum {
  GOLD(1, "金"),
  ;

  private final Integer value;
  private final String desc;
}
