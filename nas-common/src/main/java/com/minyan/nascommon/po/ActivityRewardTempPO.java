package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 活动奖品临时表
 */
@Data
@TableName("nas_activity_reward_temp")
public class ActivityRewardTempPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 奖品id */
  private Long rewardId;

  /** 奖品类型(RewardTypeEnum) */
  private Integer rewardType;

  /** 奖品名称 */
  private String rewardName;

  /** 奖品批次编码 */
  private String batchCode;

  /** 图片链接 */
  private String imageUrl;

  /** 创建时间 */
  private Date createTime;

  /** 更新时间 */
  private Date updateTime;

  /** 删除标识(1删除0未删除) */
  private Integer delTag;

  private static final long serialVersionUID = 1L;
}
