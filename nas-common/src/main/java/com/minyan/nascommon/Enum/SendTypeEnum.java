package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 奖品发放类型枚举
 * @author minyan.he
 * @date 2024/12/21 17:15
 */
@AllArgsConstructor
@Getter
public enum SendTypeEnum {
  ALL(1, "全部发放"),
  PART(2, "部分发放");

  private final Integer value;
  private final String message;

  public static SendTypeEnum getByValue(Integer value) {
    for (SendTypeEnum sendTypeEnum : SendTypeEnum.values()) {
      if (sendTypeEnum.value.equals(value)) {
        return sendTypeEnum;
      }
    }
    return null;
  }
}
