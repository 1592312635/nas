package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 活动奖品规则表
 */
@Data
@TableName("nas_reward_rule")
public class RewardRulePO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Long activityId;

  /** 模块id */
  private Long moduleId;

  /** 事件id */
  private Long eventId;

  /** 奖品类型 */
  private Integer rewardType;

  /** 奖品id */
  private Long rewardId;

  /** 创建时间 */
  private Date createTime;

  /** 更新时间 */
  private Date updateTime;

  /** 删除标识(1删除0未删除) */
  private Integer delTag;

  private static final long serialVersionUID = 1L;
}
