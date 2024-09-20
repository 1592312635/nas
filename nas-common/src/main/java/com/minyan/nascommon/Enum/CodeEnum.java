package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 返回值统一code
 * @author minyan.he
 * @date 2024/6/24 13:23
 */
@AllArgsConstructor
@Getter
public enum CodeEnum {
  SUCCESS("200", "成功"),
  FAIL("9999", "失败"),

  IDEMPOTENT_EXIST("100000", "幂等性校验失败"),
  ACTIVITY_NOT_EXIST("100001", "活动不存在"),
  MODULE_NOT_EXIST("100002", "模块不存在"),
  EVENT_NOT_EXIST("100003", "活动事件不存在"),
  RECEIVE_LIMIT_NOT_EXIST("100004", "领取限制不存在"),
  REWARD_RULE_NOT_EXIST("100005", "奖励规则不存在")
  ;
  private final String code;
  private final String message;
}
