package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.IsProgressEnum;
import com.minyan.nascommon.param.*;
import com.minyan.nascommon.po.*;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.*;
import com.minyan.nasmapi.service.ActivityService;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    MActivityInfoDetailVO activityInfoDetailVO = getActivityInfoByActivityId(param.getActivityId());
    if (ObjectUtils.isEmpty(activityInfoDetailVO)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
    }

    // 获取模块信息
    List<MModuleInfoDetailVO> moduleInfoDetailVOS =
        getModuleInfoByActivityId(param.getActivityId());
    if (CollectionUtils.isEmpty(moduleInfoDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时模块不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.MODULE_NOT_EXIST);
    }

    // 获取活动事件信息
    List<MActivityEventDetailVO> activityEventDetailVOS =
        getActivityEventByModules(param.getActivityId(), moduleInfoDetailVOS);
    if (CollectionUtils.isEmpty(activityEventDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动事件不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.EVENT_NOT_EXIST);
    }

    List<MReceiveLimitDetailVO> receiveLimitDetailVOS =
        getReceiveLimitDetailByEvents(activityEventDetailVOS);
    if (CollectionUtils.isEmpty(receiveLimitDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动领取规则不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.RECEIVE_LIMIT_NOT_EXIST);
    }

    List<MRewardRuleDetailVO> rewardRuleDetailVOS =
        getRewardRuleDetailByEvents(activityEventDetailVOS);
    if (CollectionUtils.isEmpty(rewardRuleDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动奖品规则不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.REWARD_RULE_NOT_EXIST);
    }

    List<MActivityChannelDetailVO> activityChannelDetailVOS =
        getActivityChannelDetailVOList(param.getActivityId());
    if (CollectionUtils.isEmpty(activityChannelDetailVOS)) {
      logger.info(
          "[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动渠道不存在，活动id：{}",
          param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_NOT_EXIST);
    }

    activityInfoDetailVO =
        constructActivityInfoDetailVO(
            activityInfoDetailVO,
            moduleInfoDetailVOS,
            activityEventDetailVOS,
            receiveLimitDetailVOS,
            rewardRuleDetailVOS,
            activityChannelDetailVOS);
    return ApiResult.buildSuccess(activityInfoDetailVO);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ApiResult<Boolean> saveActivityInfo(MActivityInfoSaveParam param) {
    Boolean activityInfoSaveResult = saveActivityInfoTemp(param);
    if (!activityInfoSaveResult) {
      logger.info("[ActivityServiceImpl][saveActivityInfo]活动信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_TEMP_SAVE_FAIL);
    }

    Boolean activityRewardSaveResult = saveActivityRewardTemp(param);
    if (!activityRewardSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动奖品信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_REWARD_TEMP_SAVE_FAIL);
    }

    Boolean activityModuleSaveResult = saveActivityModuleTemp(param);
    if (!activityModuleSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动模块信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_MODULE_TEMP_SAVE_FAIL);
    }

    Boolean activityChannelSaveResult = saveActivityChannelTemp(param);
    if (!activityChannelSaveResult) {
      logger.info(
          "[ActivityServiceImpl][saveActivityInfo]活动渠道信息保存失败，活动id：{}", param.getActivityId());
      return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_TEMP_SAVE_FAIL);
    }
    return null;
  }

  /**
   * 通过活动id获取活动基本信息
   *
   * @param activityId
   * @return
   */
  MActivityInfoDetailVO getActivityInfoByActivityId(Integer activityId) {
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
   * 通过活动id获取模块信息
   *
   * @param activityId
   * @return
   */
  List<MModuleInfoDetailVO> getModuleInfoByActivityId(Integer activityId) {
    List<MModuleInfoDetailVO> mModuleInfoDetailVOList = Lists.newArrayList();
    QueryWrapper<ModuleInfoTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, activityId)
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoTempPO> moduleInfoPOS = moduleInfoTempDAO.selectList(queryWrapper);
    if (!CollectionUtils.isEmpty(moduleInfoPOS)) {
      mModuleInfoDetailVOList =
          moduleInfoPOS.stream().map(ModuleInfoTempPO::poConvertToVo).collect(Collectors.toList());
    }
    return mModuleInfoDetailVOList;
  }

  /**
   * 通过活动模块信息获取事件信息
   *
   * @param activityId
   * @param moduleInfoPOS
   * @return
   */
  List<MActivityEventDetailVO> getActivityEventByModules(
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
   * 通过事件信息获取事件规则门槛详情信息
   *
   * @param activityEventDetailVOS
   * @return
   */
  List<MReceiveLimitDetailVO> getReceiveLimitDetailByEvents(
      List<MActivityEventDetailVO> activityEventDetailVOS) {
    List<MReceiveLimitDetailVO> receiveLimitDetailVOS = Lists.newArrayList();
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

    Map<Long, ReceiveLimitTempPO> receiveLimitMap = Maps.newHashMap();
    for (ReceiveLimitTempPO receiveLimitPO : receiveLimitPOS) {
      receiveLimitMap.put(receiveLimitPO.getReceiveRuleId(), receiveLimitPO);
    }

    // 外部循环构建结果
    for (ReceiveRuleTempPO receiveRulePO : receiveRulePOS) {
      ReceiveLimitTempPO receiveLimitPO = receiveLimitMap.get(receiveRulePO.getId());
      if (receiveLimitPO != null) {
        MReceiveLimitDetailVO newMReceiveLimitDetailVO =
            buildReceiveLimitDetailVO(receiveRulePO, receiveLimitPO);
        receiveLimitDetailVOS.add(newMReceiveLimitDetailVO);
      }
    }
    return receiveLimitDetailVOS;
  }

  /**
   * 构建事件门槛详情VO
   *
   * @param receiveRulePO
   * @param receiveLimitPO
   * @return
   */
  MReceiveLimitDetailVO buildReceiveLimitDetailVO(
      ReceiveRuleTempPO receiveRulePO, ReceiveLimitTempPO receiveLimitPO) {
    MReceiveLimitDetailVO mReceiveLimitDetailVO = new MReceiveLimitDetailVO();
    mReceiveLimitDetailVO.setReceiveRuleId(receiveRulePO.getId());
    mReceiveLimitDetailVO.setEventId(receiveRulePO.getEventId());
    mReceiveLimitDetailVO.setRuleType(receiveRulePO.getRuleType());
    mReceiveLimitDetailVO.setReceiveLimitId(receiveLimitPO.getId());
    mReceiveLimitDetailVO.setLimitKey(receiveLimitPO.getLimitKey());
    mReceiveLimitDetailVO.setLimitJson(receiveLimitPO.getLimitJson());
    mReceiveLimitDetailVO.setLimitType(receiveLimitPO.getLimitType());
    return mReceiveLimitDetailVO;
  }

  /**
   * 通过事件信息获取奖品规则信息
   *
   * @param activityEventDetailVOS
   * @return
   */
  List<MRewardRuleDetailVO> getRewardRuleDetailByEvents(
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
    mRewardRuleDetailVO.setRewardLimitDetailVOList(rewardLimitDetailVOS);
    return mRewardRuleDetailVO;
  }

  /**
   * 获取活动渠道信息
   *
   * @param activityId
   * @return
   */
  List<MActivityChannelDetailVO> getActivityChannelDetailVOList(Integer activityId) {
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
   * 构建活动详情VO
   *
   * @param activityInfoDetailVO
   * @param moduleInfoDetailVOS
   * @param activityEventDetailVOS
   * @param receiveLimitDetailVOS
   * @param rewardRuleDetailVOS
   * @param activityChannelDetailVOS
   * @return
   */
  MActivityInfoDetailVO constructActivityInfoDetailVO(
      MActivityInfoDetailVO activityInfoDetailVO,
      List<MModuleInfoDetailVO> moduleInfoDetailVOS,
      List<MActivityEventDetailVO> activityEventDetailVOS,
      List<MReceiveLimitDetailVO> receiveLimitDetailVOS,
      List<MRewardRuleDetailVO> rewardRuleDetailVOS,
      List<MActivityChannelDetailVO> activityChannelDetailVOS) {
    // 设置模块信息
    activityInfoDetailVO.setMModuleInfoDetailVOList(moduleInfoDetailVOS);

    // 设置活动事件信息
    for (MModuleInfoDetailVO moduleInfo : moduleInfoDetailVOS) {
      List<MActivityEventDetailVO> eventsForModule =
          activityEventDetailVOS.stream()
              .filter(event -> event.getModuleId().equals(moduleInfo.getModuleId()))
              .collect(Collectors.toList());
      moduleInfo.setMActivityEventDetailVOS(eventsForModule);

      // 设置接收限制信息
      for (MActivityEventDetailVO event : eventsForModule) {
        List<MReceiveLimitDetailVO> receiveLimitsForEvent =
            receiveLimitDetailVOS.stream()
                .filter(limit -> limit.getEventId().equals(event.getEventId()))
                .collect(Collectors.toList());
        event.setMReceiveLimitDetailVOS(receiveLimitsForEvent);

        // 设置奖励规则信息
        List<MRewardRuleDetailVO> rewardRulesForEvent =
            rewardRuleDetailVOS.stream()
                .filter(rule -> rule.getEventId().equals(event.getEventId()))
                .collect(Collectors.toList());
        event.setMRewardRuleDetailVOS(rewardRulesForEvent);
      }
    }

    // 设置活动渠道信息
    activityInfoDetailVO.setMActivityChannelDetailVOList(activityChannelDetailVOS);
    return activityInfoDetailVO;
  }

  /**
   * 保存活动基本信息
   *
   * @param param
   * @return
   */
  Boolean saveActivityInfoTemp(MActivityInfoSaveParam param) {
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

  /**
   * 保存活动奖品信息(保存、更新、删除)
   *
   * @param param
   * @return
   */
  Boolean saveActivityRewardTemp(MActivityInfoSaveParam param) {
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

  Boolean saveActivityModuleTemp(MActivityInfoSaveParam param) {
    Integer activityId = param.getActivityId();
    List<MActivityModuleSaveParam> moduleSaveInfos = param.getModuleSaveInfos();
    QueryWrapper<ModuleInfoTempPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, activityId)
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoTempPO> moduleInfoTempPOS = moduleInfoTempDAO.selectList(queryWrapper);

    // 新增变更删除分离
    List<MActivityModuleSaveParam> toAdd = Lists.newArrayList();
    List<MActivityModuleSaveParam> toUpdate = Lists.newArrayList();
    List<ModuleInfoTempPO> toDelete = Lists.newArrayList();
    Map<Integer, ModuleInfoTempPO> tempMap = Maps.newHashMap();
    for (ModuleInfoTempPO temp : moduleInfoTempPOS) {
      tempMap.put(temp.getModuleId(), temp);
    }
    for (MActivityModuleSaveParam module : moduleSaveInfos) {
      Integer moduleId = module.getModuleId();
      if (!tempMap.containsKey(moduleId)) {
        toAdd.add(module);
      } else {
        toUpdate.add(module);
        tempMap.remove(moduleId);
      }
      toDelete.addAll(tempMap.values());
    }

    // 数据库操作
    for (MActivityModuleSaveParam mActivityModuleSaveParam : toAdd) {
      // 新增事件信息
      saveEventInfos(activityId, mActivityModuleSaveParam);

      moduleInfoTempDAO.insert(buildModuleInfoTempPO(activityId, mActivityModuleSaveParam));
    }
    for (MActivityModuleSaveParam mActivityModuleSaveParam : toUpdate) {
      // 变更事件信息
      updateEventInfos(activityId, mActivityModuleSaveParam);

      UpdateWrapper<ModuleInfoTempPO> updateWrapper = new UpdateWrapper<>();
      updateWrapper
          .lambda()
          .set(ModuleInfoTempPO::getModuleName, mActivityModuleSaveParam.getModuleName())
          .set(ModuleInfoTempPO::getBeginTime, mActivityModuleSaveParam.getBeginTime())
          .set(ModuleInfoTempPO::getEndTime, mActivityModuleSaveParam.getEndTime())
          .eq(ModuleInfoTempPO::getModuleId, mActivityModuleSaveParam.getModuleId());
      moduleInfoTempDAO.update(null, updateWrapper);
    }
    if (!CollectionUtils.isEmpty(toDelete)) {
      // 删除模块下的事件信息
      delEventInfos(toDelete);

      List<Integer> delModuleIds =
          toDelete.stream().map(ModuleInfoTempPO::getModuleId).collect(Collectors.toList());
      UpdateWrapper<ModuleInfoTempPO> deleteWrapper = new UpdateWrapper<>();
      deleteWrapper
          .lambda()
          .set(ModuleInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ModuleInfoTempPO::getModuleId, delModuleIds);
      moduleInfoTempDAO.update(null, deleteWrapper);
    }
    return true;
  }

  /**
   * 构建模块信息数据
   *
   * @param activityId
   * @param param
   * @return
   */
  ModuleInfoTempPO buildModuleInfoTempPO(Integer activityId, MActivityModuleSaveParam param) {
    ModuleInfoTempPO moduleInfoTempPO = new ModuleInfoTempPO();
    moduleInfoTempPO.setActivityId(activityId);
    moduleInfoTempPO.setModuleId(param.getModuleId());
    moduleInfoTempPO.setModuleName(param.getModuleName());
    moduleInfoTempPO.setBeginTime(param.getBeginTime());
    moduleInfoTempPO.setEndTime(param.getEndTime());
    return moduleInfoTempPO;
  }

  /**
   * 生成模块下事件信息、领取规则、奖品规则
   *
   * @param activityId
   * @param moduleSaveParam
   */
  void saveEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam) {
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
  void saveEventInfo(Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo) {
    // 先生成模块下事件下的领取规则和奖品规则
    saveRuleTempInfos(activityId, moduleId, eventSaveInfo);

    // 最后生成事件信息
    ActivityEventTempPO activityEventTempPO =
        buildActivityEventTempPO(activityId, moduleId, eventSaveInfo);
    activityEventTempDAO.insert(activityEventTempPO);
  }

  /**
   * 保存领取规则和奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param eventSaveInfo
   */
  void saveRuleTempInfos(
      Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo) {
    saveReceiveRuleTempInfos(
        activityId, moduleId, eventSaveInfo.getEventId(), eventSaveInfo.getReceiveRuleSaveInfos());
    saveRewardRuleTempInfos(
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
   * 保存奖品规则
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param rewardRuleSaveInfos
   */
  void saveRewardRuleTempInfos(
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
  void updateEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam) {
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
      deleteReceiveRuleTempAndRewardRuleTemp(activityEventTempPO);

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
    updateReceiveRuleInfos(
        activityId, moduleId, param.getEventId(), param.getReceiveRuleSaveInfos());
    updateRewardRuleInfos(activityId, moduleId, param.getEventId(), param.getRewardRuleSaveInfos());
  }

  /**
   * 更新领取规则和规则门槛
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param receiveRuleSaveInfos
   */
  void updateReceiveRuleInfos(
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
   * 更新奖品规则和奖品门槛
   *
   * @param activityId
   * @param moduleId
   * @param eventId
   * @param rewardRuleSaveInfos
   */
  void updateRewardRuleInfos(
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

  /**
   * 删除模块下的事件信息、领取规则、奖品规则
   *
   * @param moduleInfoTempPOS
   */
  void delEventInfos(List<ModuleInfoTempPO> moduleInfoTempPOS) {
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
        deleteReceiveRuleTempAndRewardRuleTemp(activityEventTempPO);
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

  /**
   * 删除单个事件下的领取规则和奖品规则
   *
   * @param activityEventTempPO
   */
  void deleteReceiveRuleTempAndRewardRuleTemp(ActivityEventTempPO activityEventTempPO) {
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

  /**
   * 保存活动渠道信息
   *
   * @param param
   * @return
   */
  Boolean saveActivityChannelTemp(MActivityInfoSaveParam param) {
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
