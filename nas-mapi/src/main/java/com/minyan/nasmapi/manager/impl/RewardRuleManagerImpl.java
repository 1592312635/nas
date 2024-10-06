package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityRewardLimitSaveParam;
import com.minyan.nascommon.param.MActivityRewardRuleSaveParam;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardLimitTempPO;
import com.minyan.nascommon.po.RewardRuleTempPO;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MRewardLimitDetailVO;
import com.minyan.nascommon.vo.MRewardRuleDetailVO;
import com.minyan.nasdao.NasRewardLimitDAO;
import com.minyan.nasdao.NasRewardLimitTempDAO;
import com.minyan.nasdao.NasRewardRuleTempDAO;
import com.minyan.nasmapi.manager.RewardRuleManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 奖品规则维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:38
 */
@Service
public class RewardRuleManagerImpl implements RewardRuleManager {
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;
  @Autowired private NasRewardLimitTempDAO rewardLimitTempDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;

  @Override
  public List<MRewardRuleDetailVO> getRewardRuleDetailByEvents(
      List<MActivityEventDetailVO> activityEventDetailVOS) {
    List<MRewardRuleDetailVO> rewardRuleDetailVOS = Lists.newArrayList();
    for (MActivityEventDetailVO activityEventDetailVO : activityEventDetailVOS) {
      QueryWrapper<RewardRuleTempPO> rewardRulePOQueryWrapper = new QueryWrapper<>();
      rewardRulePOQueryWrapper
          .lambda()
          .eq(RewardRuleTempPO::getEventId, activityEventDetailVO.getEventId())
          .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      List<RewardRuleTempPO> rewardRulePOS = rewardRuleTempDAO.selectList(rewardRulePOQueryWrapper);
      for (RewardRuleTempPO rewardRulePO : rewardRulePOS) {
        QueryWrapper<RewardLimitPO> rewardLimitPOQueryWrapper = new QueryWrapper<>();
        rewardLimitPOQueryWrapper
            .lambda()
            .eq(RewardLimitPO::getRewardRuleId, rewardRulePO.getId())
            .eq(RewardLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<RewardLimitPO> rewardLimitPOS = rewardLimitDAO.selectList(rewardLimitPOQueryWrapper);
        List<MRewardLimitDetailVO> rewardLimitDetailVOS = Lists.newArrayList();
        for (RewardLimitPO rewardLimitPO : rewardLimitPOS) {
          rewardLimitDetailVOS.add(buildRewardLimitDetail(rewardLimitPO));
        }
        rewardRuleDetailVOS.add(buildRewardRuleDetail(rewardRulePO, rewardLimitDetailVOS));
      }
    }
    return rewardRuleDetailVOS;
  }

  /**
   * 构建奖励规则门槛详情VO
   *
   * @param rewardLimitPO
   * @return
   */
  MRewardLimitDetailVO buildRewardLimitDetail(RewardLimitPO rewardLimitPO) {
    MRewardLimitDetailVO mRewardLimitDetailVO = new MRewardLimitDetailVO();
    mRewardLimitDetailVO.setRewardRuleId(rewardLimitPO.getRewardRuleId());
    mRewardLimitDetailVO.setLimitKey(rewardLimitPO.getLimitKey());
    mRewardLimitDetailVO.setLimitJson(rewardLimitPO.getLimitJson());
    return mRewardLimitDetailVO;
  }

  /**
   * 构建奖品规则详情VO
   *
   * @param rewardRulePO
   * @param rewardLimitDetailVOS
   * @return
   */
  MRewardRuleDetailVO buildRewardRuleDetail(
      RewardRuleTempPO rewardRulePO, List<MRewardLimitDetailVO> rewardLimitDetailVOS) {
    MRewardRuleDetailVO mRewardRuleDetailVO = new MRewardRuleDetailVO();
    mRewardRuleDetailVO.setRewardRuleId(rewardRulePO.getId());
    mRewardRuleDetailVO.setActivityId(rewardRulePO.getActivityId());
    mRewardRuleDetailVO.setModuleId(rewardRulePO.getModuleId());
    mRewardRuleDetailVO.setEventId(rewardRulePO.getEventId());
    mRewardRuleDetailVO.setRewardType(rewardRulePO.getRewardType());
    mRewardRuleDetailVO.setRewardId(rewardRulePO.getRewardId());
    mRewardRuleDetailVO.setRewardLimitInfos(rewardLimitDetailVOS);
    return mRewardRuleDetailVO;
  }

  /**
   * 保存奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param rewardRuleSaveInfos
   */
  @Override
  public void saveRewardRuleTempInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityRewardRuleSaveParam> rewardRuleSaveInfos) {
    for (MActivityRewardRuleSaveParam rewardRuleSaveInfo : rewardRuleSaveInfos) {
      RewardRuleTempPO rewardRuleTempPO =
          buildRewardRuleTempPO(activityId, moduleId, eventId, rewardRuleSaveInfo);
      rewardRuleTempDAO.insert(rewardRuleTempPO);
      for (MActivityRewardLimitSaveParam rewardLimitSaveInfo :
          rewardRuleSaveInfo.getRewardLimitSaveInfos()) {
        rewardLimitTempDAO.insert(
            buildRewardLimitTempPO(rewardRuleTempPO.getId(), rewardLimitSaveInfo));
      }
    }
  }

  /**
   * 构建奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param param
   * @return
   */
  RewardRuleTempPO buildRewardRuleTempPO(
      Integer activityId, Integer moduleId, Long eventId, MActivityRewardRuleSaveParam param) {
    RewardRuleTempPO rewardRuleTempPO = new RewardRuleTempPO();
    rewardRuleTempPO.setActivityId(activityId);
    rewardRuleTempPO.setModuleId(moduleId);
    rewardRuleTempPO.setEventId(eventId);
    rewardRuleTempPO.setRewardType(param.getRewardType());
    rewardRuleTempPO.setRewardId(param.getRewardId());
    return rewardRuleTempPO;
  }

  /**
   * 构建奖品规则门槛
   *
   * @param rewardRuleId
   * @param param
   * @return
   */
  RewardLimitTempPO buildRewardLimitTempPO(Long rewardRuleId, MActivityRewardLimitSaveParam param) {
    RewardLimitTempPO rewardLimitTempPO = new RewardLimitTempPO();
    rewardLimitTempPO.setRewardRuleId(rewardRuleId);
    rewardLimitTempPO.setLimitKey(param.getLimitKey());
    rewardLimitTempPO.setLimitJson(param.getLimitJson());
    return rewardLimitTempPO;
  }

  /**
   * 更新奖品规则和奖品门槛
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param rewardRuleSaveInfos
   */
  @Override
  public void updateRewardRuleInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityRewardRuleSaveParam> rewardRuleSaveInfos) {
    QueryWrapper<RewardRuleTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(RewardRuleTempPO::getActivityId, activityId)
        .eq(RewardRuleTempPO::getModuleId, moduleId)
        .eq(RewardRuleTempPO::getEventId, eventId)
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRuleTempPO> rewardRuleTempPOS = rewardRuleTempDAO.selectList(queryWrapper);

    List<MActivityRewardRuleSaveParam> toUpdate = Lists.newArrayList();
    List<RewardRuleTempPO> toDelete = Lists.newArrayList();
    Map<Long, RewardRuleTempPO> tempMap = Maps.newHashMap();
    for (RewardRuleTempPO rewardRuleTempPO : rewardRuleTempPOS) {
      Long rewardRuleId = rewardRuleTempPO.getId();
      tempMap.put(rewardRuleId, rewardRuleTempPO);
    }
    for (MActivityRewardRuleSaveParam rewardRuleSaveInfo : rewardRuleSaveInfos) {
      Long rewardRuleId = rewardRuleSaveInfo.getRewardRuleId();
      if (tempMap.containsKey(rewardRuleId)) {
        toUpdate.add(rewardRuleSaveInfo);
        tempMap.remove(rewardRuleId);
      }
    }
    toDelete.addAll(tempMap.values());
    if (!CollectionUtils.isEmpty(toDelete)) {
      List<Long> deleteRewardRuleIds =
          toDelete.stream().map(RewardRuleTempPO::getId).collect(Collectors.toList());
      UpdateWrapper<RewardRuleTempPO> rewardRuleTempPODeleteWrapper = new UpdateWrapper<>();
      rewardRuleTempPODeleteWrapper
          .lambda()
          .set(RewardRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardRuleTempPO::getId, deleteRewardRuleIds);
      rewardRuleTempDAO.update(null, rewardRuleTempPODeleteWrapper);

      UpdateWrapper<RewardLimitTempPO> rewardLimitTempPODeleteWrapper = new UpdateWrapper<>();
      rewardLimitTempPODeleteWrapper
          .lambda()
          .set(RewardLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardLimitTempPO::getRewardRuleId, deleteRewardRuleIds)
          .eq(RewardLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      rewardLimitTempDAO.update(null, rewardLimitTempPODeleteWrapper);
    }

    for (MActivityRewardRuleSaveParam mActivityRewardRuleSaveParam : toUpdate) {
      // rewardRule是有可能变更的，变更奖品规则对应的奖品信息会涉及到奖品规则变化，所以不适用于receiveRule的变更逻辑
      UpdateWrapper<RewardRuleTempPO> rewardRuleTempPOUpdateWrapper = new UpdateWrapper<>();
      rewardRuleTempPOUpdateWrapper
          .lambda()
          .set(RewardRuleTempPO::getRewardId, mActivityRewardRuleSaveParam.getRewardId())
          .set(RewardRuleTempPO::getRewardType, mActivityRewardRuleSaveParam.getRewardType())
          .eq(RewardRuleTempPO::getId, mActivityRewardRuleSaveParam.getRewardRuleId());
      rewardRuleTempDAO.update(null, rewardRuleTempPOUpdateWrapper);

      // 此时变更rewardRule下的rewardLimit奖品规则
      UpdateWrapper<RewardLimitTempPO> rewardLimitTempPODeleteWrapper = new UpdateWrapper<>();
      rewardLimitTempPODeleteWrapper
          .lambda()
          .set(RewardLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(RewardLimitTempPO::getRewardRuleId, mActivityRewardRuleSaveParam.getRewardRuleId())
          .eq(RewardLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      rewardLimitTempDAO.update(null, rewardLimitTempPODeleteWrapper);

      List<MActivityRewardLimitSaveParam> rewardLimitSaveInfos =
          mActivityRewardRuleSaveParam.getRewardLimitSaveInfos();
      if (!CollectionUtils.isEmpty(rewardLimitSaveInfos)) {
        for (MActivityRewardLimitSaveParam rewardLimitSaveInfo : rewardLimitSaveInfos) {
          rewardLimitTempDAO.insert(
              buildRewardLimitTempPO(
                  mActivityRewardRuleSaveParam.getRewardRuleId(), rewardLimitSaveInfo));
        }
      }
    }
  }
}
