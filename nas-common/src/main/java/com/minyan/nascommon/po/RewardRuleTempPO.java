package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 活动奖品规则临时表
 */
@Data
@TableName("nas_reward_rule_temp")
public class RewardRuleTempPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

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
