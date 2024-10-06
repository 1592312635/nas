package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityEventSaveParam;
import com.minyan.nascommon.param.MActivityReceiveLimitSaveParam;
import com.minyan.nascommon.param.MActivityReceiveRuleSaveParam;
import com.minyan.nascommon.po.*;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MReceiveLimitDetailVO;
import com.minyan.nascommon.vo.MReceiveRuleDetailVO;
import com.minyan.nasdao.NasReceiveLimitTempDAO;
import com.minyan.nasdao.NasReceiveRuleTempDAO;
import com.minyan.nasdao.NasRewardRuleTempDAO;
import com.minyan.nasmapi.manager.ReceiveRuleManager;
import com.minyan.nasmapi.manager.RewardRuleManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 领取规则维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:19
 */
@Service
public class ReceiveRuleManagerImpl implements ReceiveRuleManager {
  @Autowired private RewardRuleManager rewardRuleManager;
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;

  /**
   * 通过事件信息获取事件规则门槛详情信息
   *
   * @param activityEventDetailVOS
   * @return
   */
  @Override
  public List<MReceiveRuleDetailVO> getReceiveRuleDetailByEvents(
      List<MActivityEventDetailVO> activityEventDetailVOS) {
    List<MReceiveRuleDetailVO> receiveRuleDetailVOS = Lists.newArrayList();
    QueryWrapper<ReceiveRuleTempPO> receiveRulePOQueryWrapper = new QueryWrapper<>();
    QueryWrapper<ReceiveLimitTempPO> receiveLimitPOQueryWrapper = new QueryWrapper<>();
    List<Long> eventIds =
        activityEventDetailVOS.stream()
            .map(MActivityEventDetailVO::getEventId)
            .distinct()
            .collect(Collectors.toList());

    receiveRulePOQueryWrapper
        .lambda()
        .in(ReceiveRuleTempPO::getEventId, eventIds)
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRuleTempPO> receiveRulePOS =
        receiveRuleTempDAO.selectList(receiveRulePOQueryWrapper);
    List<Long> receiveRuleIds =
        receiveRulePOS.stream().map(ReceiveRuleTempPO::getId).collect(Collectors.toList());

    receiveLimitPOQueryWrapper
        .lambda()
        .in(ReceiveLimitTempPO::getReceiveRuleId, receiveRuleIds)
        .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveLimitTempPO> receiveLimitPOS =
        receiveLimitTempDAO.selectList(receiveLimitPOQueryWrapper);

    for (ReceiveRuleTempPO receiveRulePO : receiveRulePOS) {
      List<ReceiveLimitTempPO> receiveLimitPOSForRule =
          receiveLimitPOS.stream()
              .filter(limit -> limit.getReceiveRuleId().equals(receiveRulePO.getId()))
              .collect(Collectors.toList());
      receiveRuleDetailVOS.add(buildReceiveRuleDetailVO(receiveRulePO, receiveLimitPOSForRule));
    }
    return receiveRuleDetailVOS;
  }

  /**
   * 构建事件门槛详情VO
   *
   * @param receiveRulePO
   * @param receiveLimitPOS
   * @return
   */
  MReceiveRuleDetailVO buildReceiveRuleDetailVO(
      ReceiveRuleTempPO receiveRulePO, List<ReceiveLimitTempPO> receiveLimitPOS) {
    MReceiveRuleDetailVO mReceiveRuleDetailVO = new MReceiveRuleDetailVO();
    mReceiveRuleDetailVO.setReceiveRuleId(receiveRulePO.getId());
    mReceiveRuleDetailVO.setEventId(receiveRulePO.getEventId());
    mReceiveRuleDetailVO.setRuleType(receiveRulePO.getRuleType());
    mReceiveRuleDetailVO.setReceiveLimitInfos(
        receiveLimitPOS.stream()
            .map(MReceiveLimitDetailVO::convertToVO)
            .collect(Collectors.toList()));
    return mReceiveRuleDetailVO;
  }

  /**
   * 保存领取规则和奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param eventSaveInfo
   */
  @Override
  public void saveRuleTempInfos(
      Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo) {
    saveReceiveRuleTempInfos(
        activityId, moduleId, eventSaveInfo.getEventId(), eventSaveInfo.getReceiveRuleSaveInfos());
    rewardRuleManager.saveRewardRuleTempInfos(
        activityId, moduleId, eventSaveInfo.getEventId(), eventSaveInfo.getRewardRuleSaveInfos());
  }

  /**
   * 保存领取规则临时表数据
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param receiveRuleSaveInfos
   */
  void saveReceiveRuleTempInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityReceiveRuleSaveParam> receiveRuleSaveInfos) {
    for (MActivityReceiveRuleSaveParam receiveRuleSaveInfo : receiveRuleSaveInfos) {
      ReceiveRuleTempPO receiveRuleTempPO =
          buildReceiveRuleTempPO(activityId, moduleId, eventId, receiveRuleSaveInfo);
      receiveRuleTempDAO.insert(receiveRuleTempPO);
      Long receiveRuleId = receiveRuleTempPO.getId();
      for (MActivityReceiveLimitSaveParam receiveLimitSaveInfo :
          receiveRuleSaveInfo.getReceiveLimitSaveInfos()) {
        receiveLimitTempDAO.insert(buildReceiveLimitTempPO(receiveRuleId, receiveLimitSaveInfo));
      }
    }
  }

  /**
   * 构建领取规则实体
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param param
   * @return
   */
  ReceiveRuleTempPO buildReceiveRuleTempPO(
      Integer activityId, Integer moduleId, Long eventId, MActivityReceiveRuleSaveParam param) {
    ReceiveRuleTempPO receiveRuleTempPO = new ReceiveRuleTempPO();
    receiveRuleTempPO.setActivityId(activityId);
    receiveRuleTempPO.setModuleId(moduleId);
    receiveRuleTempPO.setEventId(eventId);
    receiveRuleTempPO.setRuleType(param.getRuleType());
    return receiveRuleTempPO;
  }

  /**
   * 构建领取门槛实体
   *
   * @param receiveRuleId
   * @param param
   * @return
   */
  ReceiveLimitTempPO buildReceiveLimitTempPO(
      Long receiveRuleId, MActivityReceiveLimitSaveParam param) {
    ReceiveLimitTempPO receiveLimitTempPO = new ReceiveLimitTempPO();
    receiveLimitTempPO.setReceiveRuleId(receiveRuleId);
    receiveLimitTempPO.setLimitKey(param.getLimitKey());
    receiveLimitTempPO.setLimitJson(param.getLimitJson());
    receiveLimitTempPO.setLimitType(param.getLimitType());
    return receiveLimitTempPO;
  }

  /**
   * 更新领取规则和规则门槛
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param receiveRuleSaveInfos
   */
  @Override
  public void updateReceiveRuleInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityReceiveRuleSaveParam> receiveRuleSaveInfos) {
    QueryWrapper<ReceiveRuleTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ReceiveRuleTempPO::getActivityId, activityId)
        .eq(ReceiveRuleTempPO::getModuleId, moduleId)
        .eq(ReceiveRuleTempPO::getEventId, eventId)
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRuleTempPO> receiveRuleTempPOS = receiveRuleTempDAO.selectList(queryWrapper);

    List<MActivityReceiveRuleSaveParam> toUpdate = Lists.newArrayList();
    List<ReceiveRuleTempPO> toDelete = Lists.newArrayList();
    Map<Long, ReceiveRuleTempPO> tempMap = Maps.newHashMap();
    for (ReceiveRuleTempPO receiveRuleTempPO : receiveRuleTempPOS) {
      Long receiveRuleId = receiveRuleTempPO.getId();
      tempMap.put(receiveRuleId, receiveRuleTempPO);
    }
    for (MActivityReceiveRuleSaveParam receiveRuleSaveInfo : receiveRuleSaveInfos) {
      Long receiveRuleId = receiveRuleSaveInfo.getReceiveRuleId();
      if (tempMap.containsKey(receiveRuleId)) {
        toUpdate.add(receiveRuleSaveInfo);
        tempMap.remove(receiveRuleId);
      }
    }
    toDelete.addAll(tempMap.values());

    // 数据库操作
    if (!CollectionUtils.isEmpty(toDelete)) {
      List<Long> deleteReceiveRuleIds =
          toDelete.stream().map(ReceiveRuleTempPO::getId).collect(Collectors.toList());
      UpdateWrapper<ReceiveRuleTempPO> receiveRuleTempPODeleteWrapper = new UpdateWrapper<>();
      receiveRuleTempPODeleteWrapper
          .lambda()
          .set(ReceiveRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveRuleTempPO::getId, deleteReceiveRuleIds);
      receiveRuleTempDAO.update(null, receiveRuleTempPODeleteWrapper);

      UpdateWrapper<ReceiveLimitTempPO> receiveLimitTempPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitTempPODeleteWrapper
          .lambda()
          .set(ReceiveLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveLimitTempPO::getReceiveRuleId, deleteReceiveRuleIds)
          .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      receiveLimitTempDAO.update(null, receiveLimitTempPODeleteWrapper);
    }

    for (MActivityReceiveRuleSaveParam mActivityReceiveRuleSaveParam : toUpdate) {
      // receiveRule只起到连接作用，本身不存在变更，直接操作receiveLimit
      UpdateWrapper<ReceiveLimitTempPO> receiveLimitTempPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitTempPODeleteWrapper
          .lambda()
          .set(ReceiveLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(
              ReceiveLimitTempPO::getReceiveRuleId,
              mActivityReceiveRuleSaveParam.getReceiveRuleId())
          .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      receiveLimitTempDAO.update(null, receiveLimitTempPODeleteWrapper);

      List<MActivityReceiveLimitSaveParam> receiveLimitSaveInfos =
          mActivityReceiveRuleSaveParam.getReceiveLimitSaveInfos();
      if (!CollectionUtils.isEmpty(receiveLimitSaveInfos)) {
        for (MActivityReceiveLimitSaveParam receiveLimitSaveInfo : receiveLimitSaveInfos) {
          receiveLimitTempDAO.insert(
              buildReceiveLimitTempPO(
                  mActivityReceiveRuleSaveParam.getReceiveRuleId(), receiveLimitSaveInfo));
        }
      }
    }
  }

  /**
   * 删除单个事件下的领取规则和奖品规则
   *
   * @param activityEventTempPO
   */
  @Override
  public void deleteReceiveRuleTempAndRewardRuleTemp(ActivityEventTempPO activityEventTempPO) {
    Long eventId = activityEventTempPO.getId();

    // 删除领取规则
    QueryWrapper<ReceiveRuleTempPO> receiveRuleTempPOQueryWrapper = new QueryWrapper<>();
    receiveRuleTempPOQueryWrapper
        .lambda()
        .eq(ReceiveRuleTempPO::getEventId, eventId)
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRuleTempPO> receiveRuleTempPOS =
        receiveRuleTempDAO.selectList(receiveRuleTempPOQueryWrapper);
    if (!CollectionUtils.isEmpty(receiveRuleTempPOS)) {
      // 删除领取规则下的领取规则门槛
      List<Long> delReceiveRuleIds =
          receiveRuleTempPOS.stream().map(ReceiveRuleTempPO::getId).collect(Collectors.toList());
      UpdateWrapper<ReceiveLimitTempPO> receiveLimitTempPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitTempPODeleteWrapper
          .lambda()
          .set(ReceiveLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveLimitTempPO::getReceiveRuleId, delReceiveRuleIds);
    }
    UpdateWrapper<ReceiveRuleTempPO> receiveRuleTempPODeleteWrapper = new UpdateWrapper<>();
    receiveRuleTempPODeleteWrapper
        .lambda()
        .set(ReceiveRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveRuleTempPO::getEventId, eventId)
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    // 删除奖品规则
    QueryWrapper<RewardRuleTempPO> rewardRuleTempPOQueryWrapper = new QueryWrapper<>();
    rewardRuleTempPOQueryWrapper
        .lambda()
        .eq(RewardRuleTempPO::getEventId, eventId)
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRuleTempPO> rewardRuleTempPOS =
        rewardRuleTempDAO.selectList(rewardRuleTempPOQueryWrapper);
    if (!CollectionUtils.isEmpty(rewardRuleTempPOS)) {
      // 删除奖品规则下的奖品规则门槛
      List<Long> delRewardRuleIds =
          rewardRuleTempPOS.stream().map(RewardRuleTempPO::getId).collect(Collectors.toList());
      UpdateWrapper<RewardLimitTempPO> rewardLimitTempPODeleteWrapper = new UpdateWrapper<>();
      rewardLimitTempPODeleteWrapper
          .lambda()
          .set(RewardLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(RewardLimitTempPO::getRewardRuleId, delRewardRuleIds);
    }
    UpdateWrapper<RewardRuleTempPO> rewardRuleTempPODeleteWrapper = new UpdateWrapper<>();
    rewardRuleTempPODeleteWrapper
        .lambda()
        .set(RewardRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(RewardRuleTempPO::getEventId, eventId)
        .eq(RewardRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
  }
}
