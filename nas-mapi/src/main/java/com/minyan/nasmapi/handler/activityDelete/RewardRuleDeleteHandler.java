package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardLimitTempPO;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nascommon.po.RewardRuleTempPO;
import com.minyan.nasdao.NasRewardLimitDAO;
import com.minyan.nasdao.NasRewardLimitTempDAO;
import com.minyan.nasdao.NasRewardRuleDAO;
import com.minyan.nasdao.NasRewardRuleTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品规则删除handler
 * @author minyan.he
 * @date 2025/5/17 18:10
 */
@Service
@Order(60)
public class RewardRuleDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;
  @Autowired private NasRewardLimitTempDAO rewardLimitTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<RewardRulePO> rewardRulePOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<RewardRuleTempPO> rewardRuleTempPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<RewardLimitPO> rewardLimitPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<RewardLimitTempPO> rewardLimitTempPOUpdateWrapper = new UpdateWrapper<>();

    rewardRulePOUpdateWrapper
        .lambda()
        .set(RewardRulePO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardRulePO::getActivityId, activityId)
        .eq(RewardRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    rewardRuleTempPOUpdateWrapper
        .lambda()
        .set(RewardRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardRuleTempPO::getActivityId, activityId)
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    rewardLimitPOUpdateWrapper
        .lambda()
        .set(RewardLimitPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardLimitPO::getActivityId, activityId)
        .eq(RewardLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    rewardLimitTempPOUpdateWrapper
        .lambda()
        .set(RewardLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardLimitTempPO::getActivityId, activityId)
        .eq(RewardLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    rewardRuleDAO.update(null, rewardRulePOUpdateWrapper);
    rewardRuleTempDAO.update(null, rewardRuleTempPOUpdateWrapper);
    rewardLimitDAO.update(null, rewardLimitPOUpdateWrapper);
    rewardLimitTempDAO.update(null, rewardLimitTempPOUpdateWrapper);
  }
}
