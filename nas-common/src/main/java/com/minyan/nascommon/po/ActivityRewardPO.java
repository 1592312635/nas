package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 活动奖品表
 */
@Data
@TableName("nas_activity_reward")
public class ActivityRewardPO implements Serializable {
  /** 主键 */
  private Long id;

  /** 奖品类型(RewardTypeEnum) */
  private Integer rewardType;

  /** 奖品名称 */
  private String rewardName;

  /** 奖品批次编码 */
  private String batchCode;

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
