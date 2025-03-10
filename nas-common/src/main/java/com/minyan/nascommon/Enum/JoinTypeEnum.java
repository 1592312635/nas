package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 参与类型枚举
 * @author minyan.he
 * @date 2025/3/10 17:07
 */
@AllArgsConstructor
@Getter
public enum JoinTypeEnum {
  DEFAULT(0, "默认"), // 参与记录请求一次记录一次
  USER_ACTIVITY(1, "用户活动维度"), // 用户+活动维度去重参与记录
  DAY(2, "日维度"), // 日维度去重参与记录
  ;

  private final Integer value;
  private final String desc;
}
