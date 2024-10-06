package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityChannelSaveParam;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.po.ActivityChannelTempPO;
import com.minyan.nascommon.vo.MActivityChannelDetailVO;
import com.minyan.nasdao.NasActivityChannelTempDAO;
import com.minyan.nasmapi.manager.ActivityChannelManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 渠道维度maanger处理
 * @author minyan.he
 * @date 2024/10/6 13:41
 */
@Service
public class ActivityChannelManagerImpl implements ActivityChannelManager {
  @Autowired private NasActivityChannelTempDAO activityChannelTempDAO;

  /**
   * 获取活动渠道信息
   *
   * @param activityId
   * @return
   */
  @Override
  public List<MActivityChannelDetailVO> getActivityChannelDetailVOList(Integer activityId) {
    List<MActivityChannelDetailVO> activityChannelDetailVOList = Lists.newArrayList();
    QueryWrapper<ActivityChannelTempPO> activityChannelPOQueryWrapper = new QueryWrapper<>();
    activityChannelPOQueryWrapper
        .lambda()
        .eq(ActivityChannelTempPO::getActivityId, activityId)
        .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityChannelTempPO> activityChannelPOS =
        activityChannelTempDAO.selectList(activityChannelPOQueryWrapper);
    activityChannelDetailVOList =
        activityChannelPOS.stream()
            .map(ActivityChannelTempPO::poConvertToVo)
            .collect(Collectors.toList());
    return activityChannelDetailVOList;
  }

  /**
   * 保存活动渠道信息
   *
   * @param param
   * @return
   */
  @Override
  public Boolean saveActivityChannelTemp(MActivityInfoSaveParam param) {
    QueryWrapper<ActivityChannelTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityChannelTempPO::getActivityId, param.getActivityId())
        .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityChannelTempPO> activityChannelTempPOS =
        activityChannelTempDAO.selectList(queryWrapper);

    // 新增变更删除分离
    List<MActivityChannelSaveParam> activityChannelList = param.getChannelSaveInfos();
    List<MActivityChannelSaveParam> toUpdate = Lists.newArrayList();
    List<MActivityChannelSaveParam> toAdd = Lists.newArrayList();
    List<ActivityChannelTempPO> toDelete = Lists.newArrayList();
    Map<String, ActivityChannelTempPO> tempMap = Maps.newHashMap();
    for (ActivityChannelTempPO temp : activityChannelTempPOS) {
      tempMap.put(temp.getChannelCode(), temp);
    }
    for (MActivityChannelSaveParam channel : activityChannelList) {
      String channelCode = channel.getChannelCode();
      if (channelCode == null || !tempMap.containsKey(channelCode)) {
        toAdd.add(channel);
      } else {
        toUpdate.add(channel);
        tempMap.remove(channelCode);
      }
      toDelete.addAll(tempMap.values());
    }

    // 数据库操作
    for (MActivityChannelSaveParam mActivityChannelSaveParam : toAdd) {
      activityChannelTempDAO.insert(
          buildActivityChannelTempPO(param.getActivityId(), mActivityChannelSaveParam));
    }
    for (MActivityChannelSaveParam mActivityChannelSaveParam : toUpdate) {
      UpdateWrapper<ActivityChannelTempPO> updateWrapper = new UpdateWrapper<>();
      updateWrapper
          .lambda()
          .set(ActivityChannelTempPO::getChannelName, mActivityChannelSaveParam.getChannelName())
          .eq(ActivityChannelTempPO::getActivityId, param.getActivityId())
          .eq(ActivityChannelTempPO::getChannelCode, mActivityChannelSaveParam.getChannelCode())
          .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      activityChannelTempDAO.update(null, updateWrapper);
    }
    if (!CollectionUtils.isEmpty(toDelete)) {
      List<String> delChannelCodes =
          toDelete.stream().map(ActivityChannelTempPO::getChannelCode).collect(Collectors.toList());
      UpdateWrapper<ActivityChannelTempPO> deleteWrapper = new UpdateWrapper<>();
      deleteWrapper
          .lambda()
          .set(ActivityChannelTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ActivityChannelTempPO::getChannelCode, delChannelCodes)
          .eq(ActivityChannelTempPO::getActivityId, param.getActivityId())
          .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      activityChannelTempDAO.update(null, deleteWrapper);
    }
    return true;
  }

  /**
   * 构建活动渠道保存参数
   *
   * @param activityId
   * @param param
   * @return
   */
  ActivityChannelTempPO buildActivityChannelTempPO(
      Integer activityId, MActivityChannelSaveParam param) {
    ActivityChannelTempPO activityChannelTempPO = new ActivityChannelTempPO();
    activityChannelTempPO.setActivityId(activityId);
    activityChannelTempPO.setChannelCode(param.getChannelCode());
    activityChannelTempPO.setChannelName(param.getChannelName());
    return activityChannelTempPO;
  }
}
