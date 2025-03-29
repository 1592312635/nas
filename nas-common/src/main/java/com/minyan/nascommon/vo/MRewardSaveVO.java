package com.minyan.nascommon.vo;

import lombok.Data;

/**
 * @decription 奖品保存输出参数
 * @author minyan.he
 * @date 2025/3/29 18:21
 */
@Data
public class MRewardSaveVO {
  private Long id;
  private Long rewardId;
  private Integer rewardType;
  private String rewardName;
  private String batchCode;
  private String imageUrl;
}
