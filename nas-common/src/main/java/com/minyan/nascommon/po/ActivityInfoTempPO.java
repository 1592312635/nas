package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 活动基本信息临时表
 */
@Data
@TableName("nas_activity_info_temp")
public class ActivityInfoTempPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 活动名称 */
  private String activityName;

  /** 启用状态(0未启用1启用) */
  private Integer status;

  /** 审核状态(0待审核1审核通过2审核不通过) */
  private Integer auditStatus;

  /** 开始时间 */
  private Date beginTime;

  /** 结束时间 */
  private Date endTime;

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
