package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 系统通用枚举
 * @author minyan.he
 * @date 2024/12/8 12:09
 */
@AllArgsConstructor
@Getter
public enum SystemEnum {
  NAS("nas", "系统标识"),
  ;
  private final String value;
  private final String desc;
}
