package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import com.minyan.nasmapi.manager.ActivityInfoManager;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/6 13:22
 */
@Service
public class ActivityInfoManagerImpl implements ActivityInfoManager {
  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;

  /**
   * 通过活动id获得活动信息
   *
   * @param activityId
   * @return
   */
  @Override
  public MActivityInfoDetailVO getActivityInfoByActivityId(Integer activityId) {
    MActivityInfoDetailVO mActivityInfoDetailVO = new MActivityInfoDetailVO();
    QueryWrapper<ActivityInfoTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityInfoTempPO::getActivityId, activityId)
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoTempPO activityInfoPO = activityInfoTempDAO.selectOne(queryWrapper);
    if (!ObjectUtils.isEmpty(activityInfoPO)) {
      mActivityInfoDetailVO.setActivityId(activityInfoPO.getActivityId());
      mActivityInfoDetailVO.setActivityName(activityInfoPO.getActivityName());
      mActivityInfoDetailVO.setStatus(activityInfoPO.getStatus());
      mActivityInfoDetailVO.setBeginTime(activityInfoPO.getBeginTime());
      mActivityInfoDetailVO.setEndTime(activityInfoPO.getEndTime());
    }
    return mActivityInfoDetailVO;
  }

  /**
   * 构建活动详情VO
   *
   * @param activityInfoDetailVO
   * @param moduleInfoDetailVOS
   * @param activityEventDetailVOS
   * @param receiveRuleDetailVOS
   * @param rewardRuleDetailVOS
   * @param activityChannelDetailVOS
   * @return
   */
  @Override
  public MActivityInfoDetailVO constructActivityInfoDetailVO(
      MActivityInfoDetailVO activityInfoDetailVO,
      List<MModuleInfoDetailVO> moduleInfoDetailVOS,
      List<MActivityEventDetailVO> activityEventDetailVOS,
      List<MReceiveRuleDetailVO> receiveRuleDetailVOS,
      List<MRewardRuleDetailVO> rewardRuleDetailVOS,
      List<MActivityChannelDetailVO> activityChannelDetailVOS) {
    // 设置模块信息
    activityInfoDetailVO.setModuleInfos(moduleInfoDetailVOS);

    // 设置活动事件信息
    for (MModuleInfoDetailVO moduleInfo : moduleInfoDetailVOS) {
      List<MActivityEventDetailVO> eventsForModule =
          activityEventDetailVOS.stream()
              .filter(event -> event.getModuleId().equals(moduleInfo.getModuleId()))
              .collect(Collectors.toList());
      moduleInfo.setEventInfos(eventsForModule);

      // 设置接收限制信息
      for (MActivityEventDetailVO event : eventsForModule) {
        List<MReceiveRuleDetailVO> receiveRulesForEvent =
            receiveRuleDetailVOS.stream()
                .filter(rule -> rule.getEventId().equals(event.getEventId()))
                .collect(Collectors.toList());
        event.setReceiveRuleInfos(receiveRulesForEvent);

        // 设置奖励规则信息
        List<MRewardRuleDetailVO> rewardRulesForEvent =
            rewardRuleDetailVOS.stream()
                .filter(rule -> rule.getEventId().equals(event.getEventId()))
                .collect(Collectors.toList());
        event.setRewawrdRuleInfos(rewardRulesForEvent);
      }
    }

    // 设置活动渠道信息
    activityInfoDetailVO.setActivityChannelInfos(activityChannelDetailVOS);
    return activityInfoDetailVO;
  }

  /**
   * 保存活动基本信息
   *
   * @param param
   * @return
   */
  @Override
  public Boolean saveActivityInfoTemp(MActivityInfoSaveParam param) {
    QueryWrapper<ActivityInfoTempPO> activityInfoTempPOQueryWrapper = new QueryWrapper<>();
    activityInfoTempPOQueryWrapper
        .lambda()
        .eq(ActivityInfoTempPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoTempPO activityInfoTempPO =
        activityInfoTempDAO.selectOne(activityInfoTempPOQueryWrapper);
    if (!ObjectUtils.isEmpty(activityInfoTempPO)) {
      // 非空删除原有活动临时数据
      activityInfoTempPO.setDelTag(DelTagEnum.DEL.getValue());
      activityInfoTempDAO.updateById(activityInfoTempPO);
    }

    ActivityInfoTempPO insertActivityInfoTempPO = buildActivityInfoTempPO(param);
    int insert = activityInfoTempDAO.insert(insertActivityInfoTempPO);
    return insert > 0;
  }

  /**
   * 构建活动临时数据
   *
   * @param param
   * @return
   */
  ActivityInfoTempPO buildActivityInfoTempPO(MActivityInfoSaveParam param) {
    ActivityInfoTempPO activityInfoTempPO = new ActivityInfoTempPO();
    activityInfoTempPO.setActivityId(param.getActivityId());
    activityInfoTempPO.setActivityName(param.getActivityName());
    return activityInfoTempPO;
  }
}
