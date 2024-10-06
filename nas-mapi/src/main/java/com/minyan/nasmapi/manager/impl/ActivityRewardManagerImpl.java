package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.param.MActivityRewardSaveParam;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import com.minyan.nasdao.NasActivityRewardTempDAO;
import com.minyan.nasmapi.manager.ActivityRewardManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 奖品维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:47
 */
@Service
public class ActivityRewardManagerImpl implements ActivityRewardManager {
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;

  /**
   * 保存活动奖品信息(保存、更新、删除)
   *
   * @param param
   * @return
   */
  @Override
  public Boolean saveActivityRewardTemp(MActivityInfoSaveParam param) {
    QueryWrapper<ActivityRewardTempPO> acitvityRewardTempPOQueryWrapper = new QueryWrapper<>();
    acitvityRewardTempPOQueryWrapper
        .lambda()
        .eq(ActivityRewardTempPO::getActivityId, param.getActivityId())
        .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityRewardTempPO> activityRewardTempPOS =
        activityRewardTempDAO.selectList(acitvityRewardTempPOQueryWrapper);

    // 新增变更删除分离——如果存在已有reward_id为不变更奖品规则，历史reward_id需要保留
    List<MActivityRewardSaveParam> activityRewardList = param.getActivityRewardSaveInfos();
    List<MActivityRewardSaveParam> toUpdate = Lists.newArrayList();
    List<MActivityRewardSaveParam> toAdd = Lists.newArrayList();
    List<ActivityRewardTempPO> toDelete = Lists.newArrayList();
    Map<Long, ActivityRewardTempPO> tempMap = Maps.newHashMap();
    for (ActivityRewardTempPO temp : activityRewardTempPOS) {
      tempMap.put(temp.getRewardId(), temp);
    }
    for (MActivityRewardSaveParam reward : activityRewardList) {
      Long rewardId = reward.getRewardId();
      if (rewardId == null || !tempMap.containsKey(rewardId)) {
        toAdd.add(reward);
      } else {
        toUpdate.add(reward);
        tempMap.remove(rewardId);
      }
      toDelete.addAll(tempMap.values());
    }

    // 数据库操作
    for (MActivityRewardSaveParam mActivityRewardSaveParam : toAdd) {
      activityRewardTempDAO.insert(
          buildActivityRewardTempPO(param.getActivityId(), mActivityRewardSaveParam));
    }
    for (MActivityRewardSaveParam mActivityRewardSaveParam : toUpdate) {
      UpdateWrapper<ActivityRewardTempPO> updateWrapper = new UpdateWrapper<>();
      updateWrapper
          .lambda()
          .set(ActivityRewardTempPO::getRewardName, mActivityRewardSaveParam.getRewardName())
          .set(ActivityRewardTempPO::getRewardType, mActivityRewardSaveParam.getRewardType())
          .set(ActivityRewardTempPO::getBatchCode, mActivityRewardSaveParam.getBatchCode())
          .set(ActivityRewardTempPO::getImageUrl, mActivityRewardSaveParam.getImageUrl())
          .eq(ActivityRewardTempPO::getRewardId, mActivityRewardSaveParam.getRewardId())
          .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      activityRewardTempDAO.update(null, updateWrapper);
    }

    if (!CollectionUtils.isEmpty(toDelete)) {
      List<Long> delRewardIds =
          toDelete.stream().map(ActivityRewardTempPO::getRewardId).collect(Collectors.toList());
      UpdateWrapper<ActivityRewardTempPO> deleteWrapper = new UpdateWrapper<>();
      deleteWrapper
          .lambda()
          .set(ActivityRewardTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ActivityRewardTempPO::getRewardId, delRewardIds);
      activityRewardTempDAO.update(null, deleteWrapper);
    }
    return true;
  }

  /**
   * 构建活动奖品信息
   *
   * @param activityId
   * @param param
   * @return
   */
  ActivityRewardTempPO buildActivityRewardTempPO(
      Integer activityId, MActivityRewardSaveParam param) {
    ActivityRewardTempPO activityRewardTempPO = new ActivityRewardTempPO();
    activityRewardTempPO.setActivityId(activityId);
    activityRewardTempPO.setRewardId(param.getRewardId());
    activityRewardTempPO.setRewardName(param.getRewardName());
    activityRewardTempPO.setBatchCode(param.getBatchCode());
    activityRewardTempPO.setImageUrl(param.getImageUrl());
    return activityRewardTempPO;
  }
}
