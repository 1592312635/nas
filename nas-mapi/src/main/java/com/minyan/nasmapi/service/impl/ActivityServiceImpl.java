package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.IsProgressEnum;
import com.minyan.nascommon.param.*;
import com.minyan.nascommon.po.*;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.*;
import com.minyan.nasmapi.manager.*;
import com.minyan.nasmapi.service.ActivityService;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription 活动m端服务实现
 * @author minyan.he
 * @date 2024/9/18 19:11
 */
@Service
public class ActivityServiceImpl implements ActivityService {
  Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

  @Autowired private ActivityInfoManager activityInfoManager;
  @Autowired private ModuleInfoManager moduleInfoManager;
  @Autowired private EventInfoManager eventInfoManager;
  @Autowired private ActivityRewardManager activityRewardManager;
  @Autowired private ReceiveRuleManager receiveRuleManager;
  @Autowired private RewardRuleManager rewardRuleManager;
  @Autowired private ActivityChannelManager activityChannelManager;

  @Autowired private NasActivityInfoDAO activityInfoDAO;
  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;
  @Autowired private NasModuleInfoDAO moduleInfoDAO;
  @Autowired private NasModuleInfoTempDAO moduleInfoTempDAO;
  @Autowired private NasActivityRewardDAO activityRewardDAO;
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;
  @Autowired private NasActivityEventDAO activityEventDAO;
  @Autowired private NasActivityEventTempDAO activityEventTempDAO;
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;
  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardRuleTempDAO rewardRuleTempDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;
  @Autowired private NasRewardLimitTempDAO rewardLimitTempDAO;
  @Autowired private NasActivityChannelDAO activityChannelDAO;
  @Autowired private NasActivityChannelTempDAO activityChannelTempDAO;

  @Override
  public ApiResult<List<MActivityInfoVO>> getActivityInfoList(MActivityInfoQueryParam param) {
    Date now = new Date();
    List<MActivityInfoVO> mActivityInfoVOS = Lists.newArrayList();
    QueryWrapper<ActivityInfoPO> queryWrapper = new QueryWrapper<>();
    Page<ActivityInfoPO> page = new Page<>(param.getPageNum(), param.getPageSize());
    queryWrapper
        .lambda()
        .eq(
            !ObjectUtils.isEmpty(param.getActivityId()),
            ActivityInfoPO::getActivityId,
            param.getActivityId())
        .like(
            !StringUtils.isEmpty(param.getActvityName()),
            ActivityInfoPO::getActivityName,
            param.getActvityName())
        .eq(!ObjectUtils.isEmpty(param.getStatus()), ActivityInfoPO::getStatus, param.getStatus())
        .ge(
            !ObjectUtils.isEmpty(param.getIsProgress())
                && IsProgressEnum.IS_PROGRESS.getValue().equals(param.getIsProgress()),
            ActivityInfoPO::getBeginTime,
            now)
        .lt(
            !ObjectUtils.isEmpty(param.getIsProgress())
                && IsProgressEnum.IS_PROGRESS.getValue().equals(param.getIsProgress()),
            ActivityInfoPO::getEndTime,
            now)
        .ge(
            !ObjectUtils.isEmpty(param.getIsProgress())
                && IsProgressEnum.END.getValue().equals(param.getIsProgress()),
            ActivityInfoPO::getEndTime,
            now)
        .lt(
            !ObjectUtils.isEmpty(param.getIsProgress())
                && IsProgressEnum.NOT_PROGRESS.getValue().equals(param.getIsProgress()),
            ActivityInfoPO::getBeginTime,
            now)
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    Page<ActivityInfoPO> activityInfoPOPage = activityInfoDAO.selectPage(page, queryWrapper);
    List<ActivityInfoPO> activityInfoPOS = activityInfoPOPage.getRecords();
    if (!CollectionUtils.isEmpty(activityInfoPOS)) {
      mActivityInfoVOS =
          activityInfoPOS.stream().map(MActivityInfoVO::poConvertToVo).collect(Collectors.toList());
    }
    return ApiResult.buildSuccess(mActivityInfoVOS);
  }

  @Override
  public ApiResult<MActivityInfoDetailVO> getActivityInfoDetail(
      MActivityInfoDetailQueryParam param) {
    // 获取活动信息
    MActivityInfoDetailVO activityInfoDetailVO =
        activityInfoManager.getActivityInfoByActivityId(param.getActivityId());
    if (ObjectUtils.isEmpty(activityInfoDetailVO)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
    }

    // 获取模块信息
    List<MModuleInfoDetailVO> moduleInfoDetailVOS =
        moduleInfoManager.getModuleInfoByActivityId(param.getActivityId());
    if (CollectionUtils.isEmpty(moduleInfoDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时模块不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.MODULE_NOT_EXIST);
    }

    // 获取活动事件信息
    List<MActivityEventDetailVO> activityEventDetailVOS =
        eventInfoManager.getActivityEventByModules(param.getActivityId(), moduleInfoDetailVOS);
    if (CollectionUtils.isEmpty(activityEventDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动事件不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.EVENT_NOT_EXIST);
    }

    List<MReceiveRuleDetailVO> receiveRuleDetailVOS =
        receiveRuleManager.getReceiveRuleDetailByEvents(activityEventDetailVOS);
    if (CollectionUtils.isEmpty(receiveRuleDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动领取规则不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.RECEIVE_LIMIT_NOT_EXIST);
    }

    List<MRewardRuleDetailVO> rewardRuleDetailVOS =
        rewardRuleManager.getRewardRuleDetailByEvents(activityEventDetailVOS);
    if (CollectionUtils.isEmpty(rewardRuleDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动奖品规则不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.REWARD_RULE_NOT_EXIST);
    }

    List<MActivityChannelDetailVO> activityChannelDetailVOS =
        activityChannelManager.getActivityChannelDetailVOList(param.getActivityId());
    if (CollectionUtils.isEmpty(activityChannelDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动渠道不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_NOT_EXIST);
    }

    activityInfoDetailVO =
        activityInfoManager.constructActivityInfoDetailVO(
            activityInfoDetailVO,
            moduleInfoDetailVOS,
            activityEventDetailVOS,
            receiveRuleDetailVOS,
            rewardRuleDetailVOS,
            activityChannelDetailVOS);
    return ApiResult.buildSuccess(activityInfoDetailVO);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ApiResult<Boolean> saveActivityInfo(MActivityInfoSaveParam param) {
    Boolean activityInfoSaveResult = activityInfoManager.saveActivityInfoTemp(param);
    if (!activityInfoSaveResult) {
      logger.info("[ActivityServiceImpl][saveActivityInfo]活动信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_TEMP_SAVE_FAIL);
    }

    Boolean activityRewardSaveResult = activityRewardManager.saveActivityRewardTemp(param);
    if (!activityRewardSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动奖品信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_REWARD_TEMP_SAVE_FAIL);
    }

    Boolean activityModuleSaveResult = moduleInfoManager.saveActivityModuleTemp(param);
    if (!activityModuleSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动模块信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_MODULE_TEMP_SAVE_FAIL);
    }

    Boolean activityChannelSaveResult = activityChannelManager.saveActivityChannelTemp(param);
    if (!activityChannelSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动渠道信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_TEMP_SAVE_FAIL);
    }
    return null;
  }
}
