package com.minyan.nascommon.vo;

import lombok.Data;

/**
 * @decription 事件门槛信息出参(聚合事件规则和事件规则门槛)
 * @author minyan.he
 * @date 2024/9/19 17:00
 */
@Data
public class MReceiveLimitDetailVO {
  private Long receiveRuleId;
  private Long eventId;
  private Integer ruleType;
  private Long receiveLimitId;
  private String limitKey;
  private String limitJson;
  private Integer limitType;
}
