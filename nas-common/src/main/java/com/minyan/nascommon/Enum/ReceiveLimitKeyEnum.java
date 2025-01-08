package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 门槛标识枚举
 * @author minyan.he
 * @date 2024/11/2 11:30
 */
@Getter
@AllArgsConstructor
public enum ReceiveLimitKeyEnum {
  AMOUNT("amount", "金额门槛"),
  STRING("string", "字符串门槛"),
  EXPIRE("expire", "有效期门槛"),

  CONSUME_INFO("consume_info","前置消耗信息"),
  SEND_TYPE("send_type", "发放类型"),
  ;

  private final String value;
  private final String message;

  public static ReceiveLimitKeyEnum getByValue(String value) {
    for (ReceiveLimitKeyEnum item : ReceiveLimitKeyEnum.values()) {
      if (item.getValue().equals(value)) {
        return item;
      }
    }
    return null;
  }
}
