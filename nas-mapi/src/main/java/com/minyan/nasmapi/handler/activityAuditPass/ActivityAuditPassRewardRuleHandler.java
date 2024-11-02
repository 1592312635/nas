package com.minyan.nasmapi.handler.activityAuditPass;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardLimitTempPO;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nascommon.po.RewardRuleTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
import com.minyan.nasdao.NasRewardLimitDAO;
import com.minyan.nasdao.NasRewardLimitTempDAO;
import com.minyan.nasdao.NasRewardRuleDAO;
import com.minyan.nasdao.NasRewardRuleTempDAO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/26 10:37
 */
@Order(30)
@Service
public class ActivityAuditPassRewardRuleHandler extends ActivityAuditPassAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditPassRewardRuleHandler.class);
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;
  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardLimitTempDAO rewardLimitTempDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询相关表数据
    QueryWrapper<RewardRuleTempPO> rewardRuleTempPOQueryWrapper = new QueryWrapper<>();
    rewardRuleTempPOQueryWrapper
        .lambda()
        .eq(RewardRuleTempPO::getActivityId, param.getActivityId())
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRuleTempPO> rewardRuleTempPOS =
        rewardRuleTempDAO.selectList(rewardRuleTempPOQueryWrapper);
    QueryWrapper<RewardRulePO> rewardRulePOQueryWrapper = new QueryWrapper<>();
    rewardRulePOQueryWrapper
        .lambda()
        .eq(RewardRulePO::getActivityId, param.getActivityId())
        .eq(RewardRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRulePO> rewardRulePOS = rewardRuleDAO.selectList(rewardRulePOQueryWrapper);
    QueryWrapper<RewardLimitTempPO> receiveLimitTempPOQueryWrapper = new QueryWrapper<>();
    receiveLimitTempPOQueryWrapper
        .lambda()
        .eq(RewardLimitTempPO::getActivityId, param.getActivityId())
        .eq(RewardLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardLimitTempPO> receiveLimitTempPOS =
        rewardLimitTempDAO.selectList(receiveLimitTempPOQueryWrapper);

    // 删除多余的rewardRule
    List<Long> deleteRewardRuleIds =
        rewardRuleTempPOS.stream()
            .filter(rewardRuleTempPO -> ObjectUtils.isEmpty(rewardRuleTempPO.getRewardRuleId()))
            .map(RewardRuleTempPO::getRewardRuleId)
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(deleteRewardRuleIds)) {
      UpdateWrapper<RewardRulePO> rewardRulePODeleteWrapper = new UpdateWrapper<>();
      rewardRulePODeleteWrapper
          .lambda()
          .set(RewardRulePO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardRulePO::getId, deleteRewardRuleIds);
      rewardRuleDAO.update(null, rewardRulePODeleteWrapper);
      UpdateWrapper<RewardLimitPO> receiveLimitPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitPODeleteWrapper
          .lambda()
          .set(RewardLimitPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardLimitPO::getRewardRuleId, deleteRewardRuleIds);
      rewardLimitDAO.update(null, receiveLimitPODeleteWrapper);
    }

    // 临时表同步主表
    List<RewardRuleTempPO> updateRewardRuleTemps =
        rewardRuleTempPOS.stream()
            .filter(rewardRuleTempPO -> !ObjectUtils.isEmpty(rewardRuleTempPO.getRewardRuleId()))
            .collect(Collectors.toList());
    List<RewardRuleTempPO> addRewardRuleTemps =
        rewardRuleTempPOS.stream()
            .filter(rewardRuleTempPO -> ObjectUtils.isEmpty(rewardRuleTempPO.getRewardRuleId()))
            .collect(Collectors.toList());

    // 临时表同步主表-更新主表已有奖品规则
    if (!CollectionUtils.isEmpty(updateRewardRuleTemps)) {
      List<Long> rewardRuleIds =
          updateRewardRuleTemps.stream()
              .map(RewardRuleTempPO::getRewardRuleId)
              .collect(Collectors.toList());
      // 删除主表receiveLimit数据
      UpdateWrapper<RewardLimitPO> receiveLimitPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitPODeleteWrapper
          .lambda()
          .set(RewardLimitPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardLimitPO::getRewardRuleId, rewardRuleIds);

      // 同步临时表receiveLimit到主表
      for (RewardRuleTempPO updateRewardRuleTemp : updateRewardRuleTemps) {
        List<RewardLimitTempPO> tempRewardLimits =
            receiveLimitTempPOS.stream()
                .filter(
                    receiveLimitTempPO ->
                        updateRewardRuleTemp.getId().equals(receiveLimitTempPO.getRewardRuleId()))
                .collect(Collectors.toList());
        for (RewardLimitTempPO tempRewardLimit : tempRewardLimits) {
          rewardLimitDAO.insert(
              buildRewardLimitByRuleAndLimit(
                  updateRewardRuleTemp.getRewardRuleId(), tempRewardLimit));
        }
      }
    }

    // 临时表同步主表-新增主表没有的新奖品规则
    if (!CollectionUtils.isEmpty(addRewardRuleTemps)) {
      for (RewardRuleTempPO addRewardRuleTemp : addRewardRuleTemps) {
        RewardRulePO addRewardRulePO = RewardRulePO.tempConvertToRewardRulePO(addRewardRuleTemp);
        rewardRuleDAO.insert(addRewardRulePO);
        List<RewardLimitTempPO> tempRewardLimits =
            receiveLimitTempPOS.stream()
                .filter(
                    receiveLimitTempPO ->
                        addRewardRuleTemp.getId().equals(receiveLimitTempPO.getRewardRuleId()))
                .collect(Collectors.toList());
        for (RewardLimitTempPO tempRewardLimit : tempRewardLimits) {
          rewardLimitDAO.insert(
              buildRewardLimitByRuleAndLimit(addRewardRulePO.getId(), tempRewardLimit));
        }
      }
    }
    return null;
  }

  /**
   * 构建奖品规则
   *
   * @param rewardRuleId
   * @param rewardLimitTempPO
   * @return
   */
  RewardLimitPO buildRewardLimitByRuleAndLimit(
      Long rewardRuleId, RewardLimitTempPO rewardLimitTempPO) {
    RewardLimitPO rewardLimitPO = new RewardLimitPO();
    rewardLimitPO.setActivityId(rewardLimitTempPO.getActivityId());
    rewardLimitPO.setModuleId(rewardLimitTempPO.getModuleId());
    rewardLimitPO.setEventId(rewardLimitTempPO.getEventId());
    rewardLimitPO.setRewardRuleId(rewardRuleId);
    rewardLimitPO.setLimitKey(rewardLimitPO.getLimitKey());
    rewardLimitPO.setLimitJson(rewardLimitTempPO.getLimitJson());
    return rewardLimitPO;
  }
}
