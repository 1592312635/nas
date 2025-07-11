package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 活动操作审核表
 */
@Data
@TableName("nas_activity_audit_record")
public class ActivityAuditRecordPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 操作类型 */
  private Integer operateType;

  /** 审核状态 */
  private Integer auditStatus;

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
