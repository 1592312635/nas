package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardLimitTempPO;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nascommon.po.RewardRuleTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasRewardLimitDAO;
import com.minyan.nasdao.NasRewardLimitTempDAO;
import com.minyan.nasdao.NasRewardRuleDAO;
import com.minyan.nasdao.NasRewardRuleTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 审核拒绝奖品规则处理
 * @author minyan.he
 * @date 2024/10/26 10:49
 */
@Order(30)
@Service
public class ActivityAuditRefuseRewardRuleHandler extends ActivityAuditRefuseAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseRewardRuleHandler.class);
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;
  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardLimitTempDAO rewardLimitTempDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 删除临时表奖品规则数据
    UpdateWrapper<RewardRuleTempPO> rewardRuleTempPODeleteWrapper = new UpdateWrapper<>();
    rewardRuleTempPODeleteWrapper
        .lambda()
        .set(RewardRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardRuleTempPO::getActivityId, param.getActivityId())
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    rewardRuleTempDAO.update(null, rewardRuleTempPODeleteWrapper);
    UpdateWrapper<RewardLimitTempPO> rewardLimitTempPODeleteWrapper = new UpdateWrapper<>();
    rewardLimitTempPODeleteWrapper
        .lambda()
        .set(RewardLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardLimitTempPO::getActivityId, param.getActivityId())
        .eq(RewardLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    rewardLimitTempDAO.update(null, rewardLimitTempPODeleteWrapper);

    // 同步主表数据到临时库
    QueryWrapper<RewardRulePO> rewardRulePOQueryWrapper = new QueryWrapper<>();
    rewardRulePOQueryWrapper
        .lambda()
        .eq(RewardRulePO::getActivityId, param.getActivityId())
        .eq(RewardRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRulePO> rewardRulePOS = rewardRuleDAO.selectList(rewardRulePOQueryWrapper);
    QueryWrapper<RewardLimitPO> rewardLimitPOQueryWrapper = new QueryWrapper<>();
    rewardLimitPOQueryWrapper
        .lambda()
        .eq(RewardLimitPO::getActivityId, param.getActivityId())
        .eq(RewardLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardLimitPO> rewardLimitPOS = rewardLimitDAO.selectList(rewardLimitPOQueryWrapper);
    for (RewardRulePO rewardRulePO : rewardRulePOS) {
      RewardRuleTempPO rewardRuleTempPO = poToRewardRuleTempPO(rewardRulePO);
      rewardRuleTempDAO.insert(rewardRuleTempPO);
      // 同步当前奖品规则下的奖品门槛信息到临时表
      rewardLimitPOS.stream()
          .filter(rewardLimitPO -> rewardRulePO.getId().equals(rewardLimitPO.getRewardRuleId()))
          .forEach(
              rewardLimitPO -> {
                rewardLimitTempDAO.insert(
                    poToRewardLimitTempPO(rewardRuleTempPO.getId(), rewardLimitPO));
              });
    }
    return null;
  }

  /**
   * 奖品规则主表转化临时表实体
   *
   * @param rewardRulePO
   * @return
   */
  RewardRuleTempPO poToRewardRuleTempPO(RewardRulePO rewardRulePO) {
    RewardRuleTempPO rewardRuleTempPO = new RewardRuleTempPO();
    rewardRuleTempPO.setActivityId(rewardRulePO.getActivityId());
    rewardRuleTempPO.setModuleId(rewardRulePO.getModuleId());
    rewardRuleTempPO.setEventId(rewardRulePO.getEventId());
    rewardRuleTempPO.setRewardRuleId(rewardRulePO.getId());
    rewardRuleTempPO.setRewardType(rewardRulePO.getRewardType());
    rewardRuleTempPO.setRewardId(rewardRulePO.getRewardId());
    return rewardRuleTempPO;
  }

  /**
   * 奖品门槛主表转化临时表实体
   *
   * @param rewardRuleId
   * @param rewardLimitPO
   * @return
   */
  RewardLimitTempPO poToRewardLimitTempPO(Long rewardRuleId, RewardLimitPO rewardLimitPO) {
    RewardLimitTempPO rewardLimitTempPO = new RewardLimitTempPO();
    rewardLimitTempPO.setActivityId(rewardLimitPO.getActivityId());
    rewardLimitTempPO.setModuleId(rewardLimitPO.getModuleId());
    rewardLimitTempPO.setEventId(rewardLimitPO.getEventId());
    rewardLimitTempPO.setRewardRuleId(rewardRuleId);
    rewardLimitTempPO.setLimitKey(rewardLimitPO.getLimitKey());
    rewardLimitTempPO.setLimitJson(rewardLimitPO.getLimitJson());
    return rewardLimitTempPO;
  }
}
