package com.minyan.nascommon.param;

import java.util.List;
import lombok.Data;

/**
 * @decription 活动奖品规则保存参数
 * @author minyan.he
 * @date 2024/10/2 17:50
 */
@Data
public class MActivityRewardRuleSaveParam {
  private Long rewardRuleId;
  private Long rewardId;
  private Integer rewardType;
  private List<MActivityRewardLimitSaveParam> rewardLimitSaveInfos;
}
