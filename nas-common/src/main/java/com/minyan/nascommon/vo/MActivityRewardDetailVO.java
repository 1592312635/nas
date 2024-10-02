package com.minyan.nascommon.vo;

import lombok.Data;

/**
 * @decription 活动后台奖品信息出参
 * @author minyan.he
 * @date 2024/9/19 16:39
 */
@Data
public class MActivityRewardDetailVO {
  private Long rewardId;
  private Integer rewardType;
  private String rewardName;
  private String batchCode;
  private String imageUrl;
}
