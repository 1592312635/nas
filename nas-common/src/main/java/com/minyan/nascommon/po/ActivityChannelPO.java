package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.minyan.nascommon.vo.MActivityChannelDetailVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 活动渠道表
 */
@Data
@TableName("nas_activity_channel")
public class ActivityChannelPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 渠道名称 */
  private String channelName;

  /** 渠道编码 */
  private String channelCode;

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
