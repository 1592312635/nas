package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription
 * @author minyan.he
 * @date 2024/11/2 10:07
 */
@Getter
@AllArgsConstructor
public enum ReceiveLimitTypeEnum {
  DEFAULT(0, "初始门槛"),
  VALID(1, "有效门槛"),
  ACCUMULATE(2, "达标门槛"),
  EXPRESS_VALID(3, "规则引擎-有效门槛"),
  EXPRESS_ACCUMULATE(4, "规则引擎-达标门槛");

  private final Integer value;
  private final String message;

  public static ReceiveLimitTypeEnum getByValue(Integer value) {
    for (ReceiveLimitTypeEnum item : ReceiveLimitTypeEnum.values()) {
      if (item.getValue().equals(value)) {
        return item;
      }
    }
    return null;
  }
}
