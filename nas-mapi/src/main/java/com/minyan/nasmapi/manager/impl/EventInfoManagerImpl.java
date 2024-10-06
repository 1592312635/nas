package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityEventSaveParam;
import com.minyan.nascommon.param.MActivityModuleSaveParam;
import com.minyan.nascommon.po.ActivityEventTempPO;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MModuleInfoDetailVO;
import com.minyan.nasdao.NasActivityEventTempDAO;
import com.minyan.nasmapi.manager.EventInfoManager;
import com.minyan.nasmapi.manager.ReceiveRuleManager;
import com.minyan.nasmapi.manager.RewardRuleManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 事件维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:30
 */
@Service
public class EventInfoManagerImpl implements EventInfoManager {
  @Autowired private ReceiveRuleManager receiveRuleManager;
  @Autowired private RewardRuleManager rewardRuleManager;
  @Autowired private NasActivityEventTempDAO activityEventTempDAO;

  /**
   * 通过模块信息获得模块下的事件信息
   *
   * @param activityId
   * @param moduleInfoPOS
   * @return
   */
  @Override
  public List<MActivityEventDetailVO> getActivityEventByModules(
      Integer activityId, List<MModuleInfoDetailVO> moduleInfoPOS) {
    List<MActivityEventDetailVO> activityEventDetailVOS = Lists.newArrayList();
    List<Integer> moduleIds =
        moduleInfoPOS.stream().map(MModuleInfoDetailVO::getModuleId).collect(Collectors.toList());
    QueryWrapper<ActivityEventTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityEventTempPO::getActivityId, activityId)
        .in(ActivityEventTempPO::getModuleId, moduleIds)
        .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityEventTempPO> activityEventPOS = activityEventTempDAO.selectList(queryWrapper);
    if (!CollectionUtils.isEmpty(activityEventPOS)) {
      activityEventDetailVOS =
          activityEventPOS.stream()
              .map(ActivityEventTempPO::poConvertToVo)
              .collect(Collectors.toList());
    }
    return activityEventDetailVOS;
  }

  /**
   * 生成模块下事件信息、领取规则、奖品规则
   *
   * @param activityId
   * @param moduleSaveParam
   */
  @Override
  public void saveEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam) {
    List<MActivityEventSaveParam> eventSaveInfos = moduleSaveParam.getEventSaveInfos();
    for (MActivityEventSaveParam eventSaveInfo : eventSaveInfos) {
      saveEventInfo(activityId, moduleSaveParam.getModuleId(), eventSaveInfo);
    }
  }

  /**
   * 生成单个事件信息(含领取规则、奖品规则)
   *
   * @param activityId
   * @param moduleId
   * @param eventSaveInfo
   */
  @Override
  public void saveEventInfo(
      Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo) {
    // 先生成模块下事件下的领取规则和奖品规则
    receiveRuleManager.saveRuleTempInfos(activityId, moduleId, eventSaveInfo);

    // 最后生成事件信息
    ActivityEventTempPO activityEventTempPO =
        buildActivityEventTempPO(activityId, moduleId, eventSaveInfo);
    activityEventTempDAO.insert(activityEventTempPO);
  }

  /**
   * 构建活动事件信息
   *
   * @param activityId
   * @param moduleId
   * @param param
   * @return
   */
  ActivityEventTempPO buildActivityEventTempPO(
      Integer activityId, Integer moduleId, MActivityEventSaveParam param) {
    ActivityEventTempPO activityEventTempPO = new ActivityEventTempPO();
    activityEventTempPO.setActivityId(activityId);
    activityEventTempPO.setModuleId(moduleId);
    activityEventTempPO.setEventName(param.getEventName());
    activityEventTempPO.setEventType(param.getEventType());
    return activityEventTempPO;
  }

  /**
   * 变更模块下事件信息、领取规则、奖品规则
   *
   * @param activityId
   * @param moduleSaveParam
   */
  @Override
  public void updateEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam) {
    // 查询模块下现有的事件信息用于比对生成新增、变更、删除的事件信息
    QueryWrapper<ActivityEventTempPO> eventTempPOQueryWrapper = new QueryWrapper<>();
    eventTempPOQueryWrapper
        .lambda()
        .eq(ActivityEventTempPO::getActivityId, activityId)
        .eq(ActivityEventTempPO::getModuleId, moduleSaveParam.getModuleId())
        .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityEventTempPO> activityEventTempPOS =
        activityEventTempDAO.selectList(eventTempPOQueryWrapper);

    // 新增变更删除分离
    List<MActivityEventSaveParam> eventSaveInfos = moduleSaveParam.getEventSaveInfos();
    List<MActivityEventSaveParam> toAdd = Lists.newArrayList();
    List<MActivityEventSaveParam> toUpdate = Lists.newArrayList();
    List<ActivityEventTempPO> toDelete = Lists.newArrayList();
    Map<Long, ActivityEventTempPO> tempMap = Maps.newHashMap();
    for (ActivityEventTempPO activityEventTempPO : activityEventTempPOS) {
      Long eventId = activityEventTempPO.getId();
      tempMap.put(eventId, activityEventTempPO);
    }
    for (MActivityEventSaveParam eventSaveInfo : eventSaveInfos) {
      Long eventId = eventSaveInfo.getEventId();
      if (tempMap.containsKey(eventId)) {
        toUpdate.add(eventSaveInfo);
      } else {
        toAdd.add(eventSaveInfo);
      }
    }
    for (ActivityEventTempPO activityEventTempPO : activityEventTempPOS) {
      Long eventId = activityEventTempPO.getId();
      if (!eventSaveInfos.stream()
          .map(MActivityEventSaveParam::getEventId)
          .collect(Collectors.toList())
          .contains(eventId)) {
        toDelete.add(activityEventTempPO);
      }
    }
    updateEventInfos(activityId, moduleSaveParam.getModuleId(), toAdd, toUpdate, toDelete);
  }

  /**
   * 更新事件信息
   *
   * @param activityId
   * @param moduleId
   * @param toAdd
   * @param toUpdate
   * @param toDelete
   */
  void updateEventInfos(
      Integer activityId,
      Integer moduleId,
      List<MActivityEventSaveParam> toAdd,
      List<MActivityEventSaveParam> toUpdate,
      List<ActivityEventTempPO> toDelete) {
    // 新增事件信息
    for (MActivityEventSaveParam mActivityEventSaveParam : toAdd) {
      saveEventInfo(activityId, moduleId, mActivityEventSaveParam);
    }

    // 变更事件信息
    for (MActivityEventSaveParam mActivityEventSaveParam : toUpdate) {
      updateEventInfo(activityId, moduleId, mActivityEventSaveParam);
    }

    // 删除事件信息
    for (ActivityEventTempPO activityEventTempPO : toDelete) {
      // 删除当前事件下的领取规则和奖品规则
      receiveRuleManager.deleteReceiveRuleTempAndRewardRuleTemp(activityEventTempPO);

      // 删除当前事件
      UpdateWrapper<ActivityEventTempPO> eventTempPODeleteWrapper = new UpdateWrapper<>();
      eventTempPODeleteWrapper
          .lambda()
          .set(ActivityEventTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(ActivityEventTempPO::getId, activityEventTempPO.getId())
          .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    }
  }

  /**
   * 变更事件信息和事件下的领取规则、奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param param
   */
  void updateEventInfo(Integer activityId, Integer moduleId, MActivityEventSaveParam param) {
    // 变更事件
    UpdateWrapper<ActivityEventTempPO> eventTempPOUpdateWrapper = new UpdateWrapper<>();
    eventTempPOUpdateWrapper
        .lambda()
        .set(ActivityEventTempPO::getEventName, param.getEventName())
        .set(ActivityEventTempPO::getEventType, param.getEventType())
        .eq(ActivityEventTempPO::getId, param.getEventId())
        .eq(ActivityEventTempPO::getActivityId, activityId)
        .eq(ActivityEventTempPO::getModuleId, moduleId)
        .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityEventTempDAO.update(null, eventTempPOUpdateWrapper);

    // 变更领取规则和奖品规则
    receiveRuleManager.updateReceiveRuleInfos(
        activityId, moduleId, param.getEventId(), param.getReceiveRuleSaveInfos());
    rewardRuleManager.updateRewardRuleInfos(
        activityId, moduleId, param.getEventId(), param.getRewardRuleSaveInfos());
  }

  /**
   * 删除模块下的事件信息、领取规则、奖品规则
   *
   * @param moduleInfoTempPOS
   */
  @Override
  public void delEventInfos(List<ModuleInfoTempPO> moduleInfoTempPOS) {
    for (ModuleInfoTempPO moduleInfoTempPO : moduleInfoTempPOS) {
      // 先删除模块下事件下的领取规则和奖品规则
      QueryWrapper<ActivityEventTempPO> queryWrapper = new QueryWrapper<>();
      queryWrapper
          .lambda()
          .eq(ActivityEventTempPO::getActivityId, moduleInfoTempPO.getActivityId())
          .eq(ActivityEventTempPO::getModuleId, moduleInfoTempPO.getModuleId())
          .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      List<ActivityEventTempPO> activityEventTempPOS =
          activityEventTempDAO.selectList(queryWrapper);
      for (ActivityEventTempPO activityEventTempPO : activityEventTempPOS) {
        receiveRuleManager.deleteReceiveRuleTempAndRewardRuleTemp(activityEventTempPO);
      }

      // 最后删除模块下的所有事件信息
      UpdateWrapper<ActivityEventTempPO> activityEventTempPODeleteWrapper = new UpdateWrapper<>();
      activityEventTempPODeleteWrapper
          .lambda()
          .set(ActivityEventTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(ActivityEventTempPO::getActivityId, moduleInfoTempPO.getActivityId())
          .eq(ActivityEventTempPO::getModuleId, moduleInfoTempPO.getModuleId())
          .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      activityEventTempDAO.update(null, activityEventTempPODeleteWrapper);
    }
  }
}
