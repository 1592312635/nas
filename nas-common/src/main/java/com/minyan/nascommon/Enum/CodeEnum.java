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
  REWARD_RULE_NOT_EXIST("100005", "奖励规则不存在"),
  ACTIVITY_CHANNEL_NOT_EXIST("100006", "活动渠道不存在"),
  ACTIVITY_TEMP_SAVE_FAIL("100007", "活动临时基本信息保存失败"),
  ACTIVITY_REWARD_TEMP_SAVE_FAIL("100008", "活动奖励临时信息保存失败"),
  ACTIVITY_MODULE_TEMP_SAVE_FAIL("100009", "活动模块临时信息保存失败"),
  ACTIVITY_CHANNEL_TEMP_SAVE_FAIL("100010", "活动渠道临时信息保存失败"),
  AUDIT_ACTIVITY_NOT_EXIST("100011", "审核时待审核活动不存在"),
  ACTIVITY_AUDIT_PASS_FAIL("100012", "审核通过失败"),
  ACTIVITY_AUDIT_REFUSE_FAIL("100013", "审核拒绝失败"),
  AUDIT_CHANNEL_NOT_EXIST("100014", "审核时渠道不存在"),
  AUDIT_REWARD_NOT_EXIST("100015", "审核时活动奖励不存在"),
  AUDIT_MODULE_NOT_EXIST("100016", "审核时活动模块不存在"),
  ;
  private final String code;
  private final String message;
}
