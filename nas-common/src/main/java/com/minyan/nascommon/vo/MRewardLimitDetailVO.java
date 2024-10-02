package com.minyan.nascommon.vo;

import lombok.Data;

/**
 * @decription 奖品规则门槛信息出参
 * @author minyan.he
 * @date 2024/9/19 17:00
 */
@Data
public class MRewardLimitDetailVO {
  private Long rewardRuleId;
  private String limitKey;
  private String limitJson;
}
