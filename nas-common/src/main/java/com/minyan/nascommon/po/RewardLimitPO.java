package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 活动奖励规则门槛表
 */
@TableName("nas_reward_limit")
@Data
public class RewardLimitPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 奖励规则id */
  private Long rewardRuleId;

  /** 规则类型 */
  private String limitKey;

  /** 规则详细限制内容 */
  private String limitJson;

  /** 创建时间 */
  @TableField(fill = FieldFill.INSERT)
  private Date createTime;

  /** 更新时间 */
  @TableField(fill = FieldFill.INSERT_UPDATE)
  private Date updateTime;

  /** 删除标识(1删除0未删除) */
  @TableField(fill = FieldFill.INSERT)
  private Integer delTag;

  private static final long serialVersionUID = 1L;
}
