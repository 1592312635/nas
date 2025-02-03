package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription nas_send_flow发放状态枚举
 * @author minyan.he
 * @date 2025/2/3 22:17
 */
@AllArgsConstructor
@Getter
public enum SendStatusEnum {
  PENDING(1, "发放中"),
  SUCCESS(2, "发放成功"),
  FAIL(3, "发放失败");

  private final Integer value;
  private final String message;
}
