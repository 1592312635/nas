package com.minyan.nascommon.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 活动奖品规则表
 */
@Data
@TableName("nas_reward_rule")
public class RewardRulePO implements Serializable {
  /** 主键 */
  private Long id;

  /** 活动id */
  private Integer activityId;

  /** 模块id */
  private Integer moduleId;

  /** 事件id */
  private Long eventId;

  /** 奖品类型 */
  private Integer rewardType;

  /** 奖品id */
  private Long rewardId;

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

  /**
   * 临时表奖品规则转化主表奖品规则
   *
   * @param rewardRuleTempPO
   * @return
   */
  public static RewardRulePO tempConvertToRewardRulePO(RewardRuleTempPO rewardRuleTempPO) {
    RewardRulePO rewardRulePO = new RewardRulePO();
    rewardRulePO.setActivityId(rewardRuleTempPO.getActivityId());
    rewardRulePO.setModuleId(rewardRuleTempPO.getModuleId());
    rewardRulePO.setEventId(rewardRuleTempPO.getEventId());
    rewardRulePO.setRewardId(rewardRuleTempPO.getRewardId());
    rewardRulePO.setRewardType(rewardRuleTempPO.getRewardType());
    return rewardRulePO;
  }
}
