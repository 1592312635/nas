package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 活动操作审核表
 */
@Data
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
  private Date createTime;

  /** 更新时间 */
  private Date updateTime;

  /** 删除标识 */
  private Integer delTag;

  private static final long serialVersionUID = 1L;
}
