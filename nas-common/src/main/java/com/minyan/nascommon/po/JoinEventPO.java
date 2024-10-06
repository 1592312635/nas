package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author
 */
@Data
@TableName("nas_join_event")
public class JoinEventPO implements Serializable {
  private Long id;

  /** 用户注册id */
  private String userId;

  /** 事件id */
  private Long eventId;

  /** 奖品规则id */
  private Long rewardRuleId;

  /** 调度时间 */
  private Date scheduleTime;

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
