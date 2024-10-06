package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityRewardRuleSaveParam;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MRewardRuleDetailVO;
import java.util.List;

/**
 * @decription 奖品规则维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:37
 */
public interface RewardRuleManager {
  List<MRewardRuleDetailVO> getRewardRuleDetailByEvents(
      List<MActivityEventDetailVO> activityEventDetailVOS);

  void saveRewardRuleTempInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityRewardRuleSaveParam> rewardRuleSaveInfos);

  void updateRewardRuleInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityRewardRuleSaveParam> rewardRuleSaveInfos);
}
