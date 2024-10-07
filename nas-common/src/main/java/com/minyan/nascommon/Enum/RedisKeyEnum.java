package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 缓存key
 * @author minyan.he
 * @date 2024/10/8 17:16
 */
@AllArgsConstructor
@Getter
public enum RedisKeyEnum {
  // 业务锁

  // redis锁
  ACTIVITY_SAVE_ACTIVITY_ID("activity_save:activity_id", "活动保存时活动id生成锁"),
  MODULE_SAVE_MODULE_ID("module_save:module_id", "模块保存时模块id生成锁"),
  ;

  private final String key;
  private final String desc;
}
