package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author
 */
@Data
@TableName("nas_activity_info")
public class ActivityInfoPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 活动名称 */
  private String activityName;

  /** 启用状态 */
  private Integer status;

  /** 审核状态 */
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
