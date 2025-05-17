package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 审核操作类型枚举
 * @author minyan.he
 * @date 2025/4/5 17:42
 */
@AllArgsConstructor
@Getter
public enum AuditOperateTypeEnum {
  ACTIVITY_OPEN(1, "活动启用"),
  ACTIVITY_CLOSE(2, "活动关闭"),
  ACTIVITY_DELETE(3, "活动删除"),
  ;

  private final Integer value;
  private final String desc;
}
