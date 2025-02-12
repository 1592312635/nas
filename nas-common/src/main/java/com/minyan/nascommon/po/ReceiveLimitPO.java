package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 领取门槛表
 */
@Data
@TableName("nas_receive_limit")
public class ReceiveLimitPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 事件id */
  private Long eventId;

  /** 规则id */
  private Long receiveRuleId;

  /** 门槛标识 */
  private String limitKey;

  /** 门槛数据json */
  private String limitJson;

  /** 门槛类型(0初始门槛1有效门槛2达标门槛3规则引擎有效门槛4规则引擎达标门槛) */
  private Integer limitType;

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
