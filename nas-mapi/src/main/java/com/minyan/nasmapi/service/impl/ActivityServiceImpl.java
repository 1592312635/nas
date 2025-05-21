package com.minyan.nasmapi.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.IsProgressEnum;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nascommon.param.*;
import com.minyan.nascommon.po.*;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.*;
import com.minyan.nasmapi.handler.activityAuditPass.ActivityAuditPassAbstractHandler;
import com.minyan.nasmapi.handler.activityAuditRefuse.ActivityAuditRefuseAbstractHandler;
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

  @Autowired List<ActivityAuditPassAbstractHandler> activityAuditPassActivityInfoHandlers;
  @Autowired List<ActivityAuditRefuseAbstractHandler> activityAuditRefuseAbstractHandlers;
  @Autowired private ActivityInfoManager activityInfoManager;
  @Autowired private ModuleInfoManager moduleInfoManager;
  @Autowired private EventInfoManager eventInfoManager;
  @Autowired private ActivityRewardManager activityRewardManager;
  @Autowired private ReceiveRuleManager receiveRuleManager;
  @Autowired private RewardRuleManager rewardRuleManager;
  @Autowired private ActivityChannelManager activityChannelManager;
  @Autowired private NasActivityInfoDAO activityInfoDAO;
  @Autowired private NasActivityAuditRecordDAO activityAuditRecordDAO;

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
  public ApiResult<?> saveActivityInfo(MActivityInfoSaveParam param) {
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
    return ApiResult.build(CodeEnum.SUCCESS);
  }

  @Transactional(rollbackFor = Exception.class)
  @Override
  public ApiResult<?> auditActivityInfo(MActivityInfoAuditParam param) {
    ActivityAuditPassContext activityAuditPassContext = new ActivityAuditPassContext();
    ActivityAuditRefuseContext activityAuditRefuseContext = new ActivityAuditRefuseContext();
    activityAuditPassContext.setParam(param);
    activityAuditRefuseContext.setParam(param);

    // 活动审核操作
    if (AuditStatusEnum.PASS.getValue().equals(param.getAuditStatus())) {
      List<ActivityAuditPassAbstractHandler> activityAuditPassFallBackHandlers =
          Lists.newArrayList();
      try {
        // 活动内容审核通过
        for (ActivityAuditPassAbstractHandler activityAuditPassHandler :
            activityAuditPassActivityInfoHandlers) {
          activityAuditPassFallBackHandlers.add(activityAuditPassHandler);
          ApiResult handleResult = null;
          handleResult = activityAuditPassHandler.handle(activityAuditPassContext);
          if (!ObjectUtils.isEmpty(handleResult)) {
            logger.info(
                "[ActivityServiceImpl][auditActivityInfo]活动审核通过处理结束，活动id：{}，结束处理handler：{}",
                param.getActivityId(),
                activityAuditPassHandler.getClass().getName());
            return handleResult;
          }
        }
      } catch (Exception e) {
        logger.info(
            "[ActivityServiceImpl][auditActivityInfo]活动审核通过处理异常，开始回滚，请求参数：{}",
            JSONObject.toJSONString(param),
            e);
        for (ActivityAuditPassAbstractHandler activityAuditPassFallBackHandler :
            activityAuditPassFallBackHandlers) {
          activityAuditPassFallBackHandler.fallBack(activityAuditPassContext);
        }
        return ApiResult.build(CodeEnum.ACTIVITY_AUDIT_PASS_FAIL);
      }
    } else if (AuditStatusEnum.REFUSE.getValue().equals(param.getAuditStatus())) {
      // 活动内容审核拒绝
      List<ActivityAuditRefuseAbstractHandler> activityAuditRefuseFallBackHandlers =
          Lists.newArrayList();
      try {
        for (ActivityAuditRefuseAbstractHandler activityAuditRefuseHandler :
            activityAuditRefuseAbstractHandlers) {
          activityAuditRefuseFallBackHandlers.add(activityAuditRefuseHandler);
          ApiResult handleResult = null;
          handleResult = activityAuditRefuseHandler.handle(activityAuditRefuseContext);
          if (!ObjectUtils.isEmpty(handleResult)) {
            logger.info(
                "[ActivityServiceImpl][auditActivityInfo]活动审核不通过处理结束，活动id：{}，结束处理handler：{}",
                param.getActivityId(),
                activityAuditRefuseHandler.getClass().getName());
            return handleResult;
          }
        }
      } catch (Exception e) {
        logger.info(
            "[ActivityServiceImpl][auditActivityInfo]活动审核不通过处理异常，开始回滚，请求参数：{}",
            JSONObject.toJSONString(param),
            e);
        for (ActivityAuditRefuseAbstractHandler activityAuditRefuseFallBackHandler :
            activityAuditRefuseFallBackHandlers) {
          activityAuditRefuseFallBackHandler.fallBack(activityAuditRefuseContext);
        }
        return ApiResult.build(CodeEnum.ACTIVITY_AUDIT_REFUSE_FAIL);
      }
    }
    return ApiResult.build(CodeEnum.SUCCESS);
  }

  @Override
  public ApiResult<?> changeActivityInfo(MActivityChangeParam param) {
    // 前置校验
    QueryWrapper<ActivityAuditRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityAuditRecordPO::getActivityId, param.getActivityId())
        .eq(ActivityAuditRecordPO::getAuditStatus, AuditStatusEnum.DEFAULT.getValue());
    ActivityAuditRecordPO activityAuditRecordPO = activityAuditRecordDAO.selectOne(queryWrapper);
    if (!ObjectUtils.isEmpty(activityAuditRecordPO)) {
      return ApiResult.build(CodeEnum.AUDIT_RECORD_EXIST);
    }

    // 生成活动审核操作记录
    ActivityAuditRecordPO newActivityAuditRecordPO = buildActivityAuditRecordPO(param);
    activityAuditRecordDAO.insert(newActivityAuditRecordPO);
    return null;
  }

  /**
   * 构建活动审核记录参数
   *
   * @param param
   * @return
   */
  ActivityAuditRecordPO buildActivityAuditRecordPO(MActivityChangeParam param) {
    ActivityAuditRecordPO activityAuditRecordPO = new ActivityAuditRecordPO();
    activityAuditRecordPO.setActivityId(param.getActivityId());
    activityAuditRecordPO.setOperateType(param.getOperateType());
    activityAuditRecordPO.setAuditStatus(AuditStatusEnum.DEFAULT.getValue());
    return activityAuditRecordPO;
  }
}
