package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 活动启停状态
 * @author minyan.he
 * @date 2024/10/6 17:07
 */
@AllArgsConstructor
@Getter
public enum ActivityStatusEnum {
  RUN(1, "启用"),
  STOP(2, "停用");
  private Integer value;
  private String desc;
}
