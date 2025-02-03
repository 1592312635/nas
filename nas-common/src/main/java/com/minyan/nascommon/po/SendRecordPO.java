package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 活动奖励发放记录表
 */
@Data
@TableName("nas_send_record")
public class SendRecordPO implements Serializable {
  /** 主键 */
  private Integer id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 用户注册id */
  private String userId;

  /** 奖品规则id */
  private Integer rewardRuleId;

  /** 奖品id */
  private Integer rewardId;

  /** 奖品类型 */
  private Integer rewardType;

  /** 奖品名称 */
  private String rewardName;

  /** 图片链接 */
  private String imageUrl;

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
