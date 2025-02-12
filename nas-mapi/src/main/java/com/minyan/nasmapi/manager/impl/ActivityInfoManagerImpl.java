package com.minyan.nasmapi.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.ActivityStatusEnum;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.RedisKeyEnum;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nascommon.service.RedisService;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import com.minyan.nasmapi.manager.ActivityInfoManager;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  Logger logger = LoggerFactory.getLogger(ActivityInfoManagerImpl.class);
  @Autowired private RedisService redisService;
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

    // 如果是新生成活动，需要生成新的活动ID
    int insert = 0;
    try {
      if (ObjectUtils.isEmpty(param.getActivityId())) {
        param.setActivityId(produceActivityId());
      }
      ActivityInfoTempPO insertActivityInfoTempPO = buildActivityInfoTempPO(param);
      insert = activityInfoTempDAO.insert(insertActivityInfoTempPO);
    } catch (Exception e) {
      logger.info(
          "[ActivityInfoManagerImpl][saveActivityInfoTemp]保存/更新活动基本信息时失败，活动id：{}",
          param.getActivityId(),
          e);
      return false;
    } finally {
      redisService.releaseLock(
          String.format(
              "%s:%s", RedisKeyEnum.ACTIVITY_SAVE_ACTIVITY_ID.getKey(), param.getActivityId()));
    }
    return insert > 0;
  }

  /**
   * 生成新的activityId
   *
   * @return
   */
  Integer produceActivityId() {
    QueryWrapper<ActivityInfoTempPO> activityInfoTempPOQueryWrapper = new QueryWrapper<>();
    activityInfoTempPOQueryWrapper.select("max(activity_id) as max_activity_id");
    List<Object> result = activityInfoTempDAO.selectObjs(activityInfoTempPOQueryWrapper);
    Integer maxActivityId = result.isEmpty() ? null : (Integer) result.get(0);
    Integer targetActivityId = ObjectUtils.isEmpty(maxActivityId) ? 60000 : maxActivityId + 1;
    redisService.lock(
            String.format("%s:%s", RedisKeyEnum.ACTIVITY_SAVE_ACTIVITY_ID.getKey(), targetActivityId));
    return targetActivityId;
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
    activityInfoTempPO.setStatus(ActivityStatusEnum.STOP.getValue());
    activityInfoTempPO.setAuditStatus(AuditStatusEnum.DEFAULT.getValue());
    activityInfoTempPO.setBeginTime(param.getBeginTime());
    activityInfoTempPO.setEndTime(param.getEndTime());
    return activityInfoTempPO;
  }
}
