package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 审核状态枚举
 * @author minyan.he
 * @date 2024/10/6 14:37
 */
@AllArgsConstructor
@Getter
public enum AuditStatusEnum {
  DEFAULT(0, "待审核"),
  PASS(1, "通过"),
  REFUSE(2, "拒绝");

  private Integer value;
  private String desc;
}
