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
@TableName("nas_join_record")
public class JoinRecordPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Long activityId;

  /** 模块id */
  private Long moduleId;

  /** 用户注册id */
  private String userId;

  /** 参与类型(JoinTypeEnum) */
  private Integer joinType;

  /** 额外参与信息 */
  private String joinInfo;

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
