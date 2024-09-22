package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.Enum.IsProgressEnum;
import com.minyan.nascommon.param.MActivityInfoDetailQueryParam;
import com.minyan.nascommon.param.MActivityInfoQueryParam;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.po.*;
import com.minyan.nascommon.vo.*;
import com.minyan.nasdao.*;
import com.minyan.nasmapi.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @decription 活动m端服务实现
 * @author minyan.he
 * @date 2024/9/18 19:11
 */
@Service
public class ActivityServiceImpl implements ActivityService {
    Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private NasActivityInfoDAO activityInfoDAO;
    @Autowired
    private NasActivityInfoTempDAO activityInfoTempDAO;
    @Autowired
    private NasModuleInfoDAO moduleInfoDAO;
    @Autowired
    private NasModuleInfoTempDAO moduleInfoTempDAO;
    @Autowired
    private NasActivityEventDAO activityEventDAO;
    @Autowired
    private NasActivityEventTempDAO activityEventTempDAO;
    @Autowired
    private NasReceiveRuleDAO receiveRuleDAO;
    @Autowired
    private NasReceiveRuleTempDAO receiveRuleTempDAO;
    @Autowired
    private NasReceiveLimitDAO receiveLimitDAO;
    @Autowired
    private NasReceiveLimitTempDAO receiveLimitTempDAO;
    @Autowired
    private NasRewardRuleDAO rewardRuleDAO;
    @Autowired
    private NasRewardRuleTempDAO rewardRuleTempDAO;
    @Autowired
    private NasRewardLimitDAO rewardLimitDAO;
    @Autowired
    private NasRewardLimitTempDAO rewardLimitTempDAO;
    @Autowired
    private NasActivityChannelDAO activityChannelDAO;
    @Autowired
    private NasActivityChannelTempDAO activityChannelTempDAO;

    @Override
    public ApiResult<List<MActivityInfoVO>> getActivityInfoList(MActivityInfoQueryParam param) {
        Date now = new Date();
        List<MActivityInfoVO> mActivityInfoVOS = Lists.newArrayList();
        QueryWrapper<ActivityInfoPO> queryWrapper = new QueryWrapper<>();
        Page<ActivityInfoPO> page = new Page<>(param.getPageNum(), param.getPageSize());
        queryWrapper.lambda().eq(!ObjectUtils.isEmpty(param.getActivityId()), ActivityInfoPO::getActivityId, param.getActivityId()).like(!StringUtils.isEmpty(param.getActvityName()), ActivityInfoPO::getActivityName, param.getActvityName()).eq(!ObjectUtils.isEmpty(param.getStatus()), ActivityInfoPO::getStatus, param.getStatus()).ge(!ObjectUtils.isEmpty(param.getIsProgress()) && IsProgressEnum.IS_PROGRESS.getValue().equals(param.getIsProgress()), ActivityInfoPO::getBeginTime, now).lt(!ObjectUtils.isEmpty(param.getIsProgress()) && IsProgressEnum.IS_PROGRESS.getValue().equals(param.getIsProgress()), ActivityInfoPO::getEndTime, now).ge(!ObjectUtils.isEmpty(param.getIsProgress()) && IsProgressEnum.END.getValue().equals(param.getIsProgress()), ActivityInfoPO::getEndTime, now).lt(!ObjectUtils.isEmpty(param.getIsProgress()) && IsProgressEnum.NOT_PROGRESS.getValue().equals(param.getIsProgress()), ActivityInfoPO::getBeginTime, now).eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        Page<ActivityInfoPO> activityInfoPOPage = activityInfoDAO.selectPage(page, queryWrapper);
        List<ActivityInfoPO> activityInfoPOS = activityInfoPOPage.getRecords();
        if (!CollectionUtils.isEmpty(activityInfoPOS)) {
            mActivityInfoVOS = activityInfoPOS.stream().map(MActivityInfoVO::poConvertToVo).collect(Collectors.toList());
        }
        return ApiResult.buildSuccess(mActivityInfoVOS);
    }

    @Override
    public ApiResult<MActivityInfoDetailVO> getActivityInfoDetail(MActivityInfoDetailQueryParam param) {
        //获取活动信息
        MActivityInfoDetailVO activityInfoDetailVO = getActivityInfoByActivityId(param.getActivityId());
        if (ObjectUtils.isEmpty(activityInfoDetailVO)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
        }

        //获取模块信息
        List<MModuleInfoDetailVO> moduleInfoDetailVOS = getModuleInfoByActivityId(param.getActivityId());
        if (CollectionUtils.isEmpty(moduleInfoDetailVOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时模块不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.MODULE_NOT_EXIST);
        }

        //获取活动事件信息
        List<MActivityEventDetailVO> activityEventDetailVOS = getActivityEventByModules(param.getActivityId(), moduleInfoDetailVOS);
        if (CollectionUtils.isEmpty(activityEventDetailVOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动事件不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.EVENT_NOT_EXIST);
        }

        List<MReceiveLimitDetailVO> receiveLimitDetailVOS = getReceiveLimitDetailByEvents(activityEventDetailVOS);
        if (CollectionUtils.isEmpty(receiveLimitDetailVOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动领取规则不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.RECEIVE_LIMIT_NOT_EXIST);
        }

        List<MRewardRuleDetailVO> rewardRuleDetailVOS = getRewardRuleDetailByEvents(activityEventDetailVOS);
        if (CollectionUtils.isEmpty(rewardRuleDetailVOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动奖品规则不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.REWARD_RULE_NOT_EXIST);
        }

        List<MActivityChannelDetailVO> activityChannelDetailVOS = getActivityChannelDetailVOList(param.getActivityId());
        if (CollectionUtils.isEmpty(activityChannelDetailVOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动渠道不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_NOT_EXIST);
        }

        activityInfoDetailVO = constructActivityInfoDetailVO(activityInfoDetailVO, moduleInfoDetailVOS, activityEventDetailVOS, receiveLimitDetailVOS, rewardRuleDetailVOS, activityChannelDetailVOS);
        return ApiResult.buildSuccess(activityInfoDetailVO);
    }

    @Override
    public ApiResult<Boolean> saveActivityInfo(MActivityInfoSaveParam param) {
        Boolean activityInfoSaveResult = saveActivityInfoTemp(param);
        if (!activityInfoSaveResult){
            logger.info("[ActivityServiceImpl][saveActivityInfo]活动信息保存失败，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_TEMP_SAVE_FAIL);
        }

        Boolean activityRewardSaveResult = saveActivityRewardTemp(param);
        if(!activityRewardSaveResult){
            logger.info("[ActivityServiceImpl][saveActivityInfo]活动奖品信息保存失败，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_REWARD_TEMP_SAVE_FAIL);
        }

        Boolean activityModuleSaveResult = saveActivityModuleTemp(param);
        if(!activityModuleSaveResult){
            logger.info("[ActivityServiceImpl][saveActivityInfo]活动模块信息保存失败，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_MODULE_TEMP_SAVE_FAIL);
        }

        Boolean activityChannelSaveResult = saveActivityChannelTemp(param);
        if(!activityChannelSaveResult){
            logger.info("[ActivityServiceImpl][saveActivityInfo]活动渠道信息保存失败，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_CHANNEL_TEMP_SAVE_FAIL);
        }
        return null;
    }

    /**
     * 通过活动id获取活动基本信息
     * @param activityId
     * @return
     */
    MActivityInfoDetailVO getActivityInfoByActivityId(Integer activityId) {
        MActivityInfoDetailVO mActivityInfoDetailVO = new MActivityInfoDetailVO();
        QueryWrapper<ActivityInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityInfoPO::getActivityId, activityId).eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(queryWrapper);
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
     * @param activityId
     * @return
     */
    List<MModuleInfoDetailVO> getModuleInfoByActivityId(Integer activityId) {
        List<MModuleInfoDetailVO> mModuleInfoDetailVOList = Lists.newArrayList();
        QueryWrapper<ModuleInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModuleInfoPO::getActivityId, activityId).eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<ModuleInfoPO> moduleInfoPOS = moduleInfoDAO.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(moduleInfoPOS)) {
            mModuleInfoDetailVOList = moduleInfoPOS.stream().map(ModuleInfoPO::poConvertToVo).collect(Collectors.toList());
        }
        return mModuleInfoDetailVOList;
    }

    /**
     * 通过活动模块信息获取事件信息
     * @param activityId
     * @param moduleInfoPOS
     * @return
     */
    List<MActivityEventDetailVO> getActivityEventByModules(Integer activityId, List<MModuleInfoDetailVO> moduleInfoPOS) {
        List<MActivityEventDetailVO> activityEventDetailVOS = Lists.newArrayList();
        List<Integer> moduleIds = moduleInfoPOS.stream().map(MModuleInfoDetailVO::getModuleId).collect(Collectors.toList());
        QueryWrapper<ActivityEventPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ActivityEventPO::getActivityId, activityId).in(ActivityEventPO::getModuleId, moduleIds).eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<ActivityEventPO> activityEventPOS = activityEventDAO.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(activityEventPOS)) {
            activityEventDetailVOS = activityEventPOS.stream().map(ActivityEventPO::poConvertToVo).collect(Collectors.toList());
        }
        return activityEventDetailVOS;
    }

    /**
     * 通过事件信息获取事件规则门槛详情信息
     * @param activityEventDetailVOS
     * @return
     */
    List<MReceiveLimitDetailVO> getReceiveLimitDetailByEvents(List<MActivityEventDetailVO> activityEventDetailVOS) {
        List<MReceiveLimitDetailVO> receiveLimitDetailVOS = Lists.newArrayList();
        QueryWrapper<ReceiveRulePO> receiveRulePOQueryWrapper = new QueryWrapper<>();
        QueryWrapper<ReceiveLimitPO> receiveLimitPOQueryWrapper = new QueryWrapper<>();
        List<Long> eventIds = activityEventDetailVOS.stream().map(MActivityEventDetailVO::getEventId).distinct().collect(Collectors.toList());

        receiveRulePOQueryWrapper.lambda().in(ReceiveRulePO::getEventId, eventIds).eq(ReceiveRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<ReceiveRulePO> receiveRulePOS = receiveRuleDAO.selectList(receiveRulePOQueryWrapper);
        List<Long> receiveRuleIds = receiveRulePOS.stream().map(ReceiveRulePO::getId).collect(Collectors.toList());

        receiveLimitPOQueryWrapper.lambda().in(ReceiveLimitPO::getReceiveRuleId, receiveRuleIds).eq(ReceiveLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<ReceiveLimitPO> receiveLimitPOS = receiveLimitDAO.selectList(receiveLimitPOQueryWrapper);

//        for (ReceiveRulePO receiveRulePO : receiveRulePOS) {
//            for (ReceiveLimitPO receiveLimitPO : receiveLimitPOS) {
//                if (receiveRulePO.getId().equals(receiveLimitPO.getReceiveRuleId())) {
//                    MReceiveLimitDetailVO newMReceiveLimitDetailVO = buildReceiveLimitDetailVO(receiveRulePO, receiveLimitPO);
//                    receiveLimitDetailVOS.add(newMReceiveLimitDetailVO);
//                }
//            }
//        }
        Map<Long, ReceiveLimitPO> receiveLimitMap = Maps.newHashMap();
        for (ReceiveLimitPO receiveLimitPO : receiveLimitPOS) {
            receiveLimitMap.put(receiveLimitPO.getReceiveRuleId(), receiveLimitPO);
        }

        // 外部循环构建结果
        for (ReceiveRulePO receiveRulePO : receiveRulePOS) {
            ReceiveLimitPO receiveLimitPO = receiveLimitMap.get(receiveRulePO.getId());
            if (receiveLimitPO != null) {
                MReceiveLimitDetailVO newMReceiveLimitDetailVO = buildReceiveLimitDetailVO(receiveRulePO, receiveLimitPO);
                receiveLimitDetailVOS.add(newMReceiveLimitDetailVO);
            }
        }
        return receiveLimitDetailVOS;
    }

    /**
     * 构建事件门槛详情VO
     * @param receiveRulePO
     * @param receiveLimitPO
     * @return
     */
    MReceiveLimitDetailVO buildReceiveLimitDetailVO(ReceiveRulePO receiveRulePO, ReceiveLimitPO receiveLimitPO) {
        MReceiveLimitDetailVO mReceiveLimitDetailVO = new MReceiveLimitDetailVO();
        mReceiveLimitDetailVO.setReceiveRuleId(receiveRulePO.getId());
        mReceiveLimitDetailVO.setEventId(receiveRulePO.getEventId());
        mReceiveLimitDetailVO.setRuleType(receiveRulePO.getRuleType());
        mReceiveLimitDetailVO.setReceiveLimitId(receiveLimitPO.getId());
        mReceiveLimitDetailVO.setEventKey(receiveLimitPO.getEventKey());
        mReceiveLimitDetailVO.setLimitJson(receiveLimitPO.getLimitJson());
        mReceiveLimitDetailVO.setLimitType(receiveLimitPO.getLimitType());
        return mReceiveLimitDetailVO;
    }

    /**
     * 通过事件信息获取奖品规则信息
     * @param activityEventDetailVOS
     * @return
     */
    List<MRewardRuleDetailVO> getRewardRuleDetailByEvents(List<MActivityEventDetailVO> activityEventDetailVOS) {
        List<MRewardRuleDetailVO> rewardRuleDetailVOS = Lists.newArrayList();
        for (MActivityEventDetailVO activityEventDetailVO : activityEventDetailVOS) {
            QueryWrapper<RewardRulePO> rewardRulePOQueryWrapper = new QueryWrapper<>();
            rewardRulePOQueryWrapper.lambda().eq(RewardRulePO::getEventId, activityEventDetailVO.getEventId()).eq(RewardRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
            List<RewardRulePO> rewardRulePOS = rewardRuleDAO.selectList(rewardRulePOQueryWrapper);
            for (RewardRulePO rewardRulePO : rewardRulePOS) {
                QueryWrapper<RewardLimitPO> rewardLimitPOQueryWrapper = new QueryWrapper<>();
                rewardLimitPOQueryWrapper.lambda().eq(RewardLimitPO::getRewardRuleId, rewardRulePO.getId()).eq(RewardLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
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
     * @param rewardLimitPO
     * @return
     */
    MRewardLimitDetailVO buildRewardLimitDetail(RewardLimitPO rewardLimitPO) {
        MRewardLimitDetailVO mRewardLimitDetailVO = new MRewardLimitDetailVO();
        mRewardLimitDetailVO.setRewardRuleId(rewardLimitPO.getRewardRuleId());
        mRewardLimitDetailVO.setRewardRuleKey(rewardLimitPO.getRewardRuleKey());
        mRewardLimitDetailVO.setLimitJson(rewardLimitPO.getLimitJson());
        return mRewardLimitDetailVO;
    }

    /**
     * 构建奖品规则详情VO
     * @param rewardRulePO
     * @param rewardLimitDetailVOS
     * @return
     */
    MRewardRuleDetailVO buildRewardRuleDetail(RewardRulePO rewardRulePO, List<MRewardLimitDetailVO> rewardLimitDetailVOS) {
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
     * @param activityId
     * @return
     */
    List<MActivityChannelDetailVO> getActivityChannelDetailVOList(Integer activityId) {
        List<MActivityChannelDetailVO> activityChannelDetailVOList = Lists.newArrayList();
        QueryWrapper<ActivityChannelPO> activityChannelPOQueryWrapper = new QueryWrapper<>();
        activityChannelPOQueryWrapper.lambda().eq(ActivityChannelPO::getActivityId, activityId).eq(ActivityChannelPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        List<ActivityChannelPO> activityChannelPOS = activityChannelDAO.selectList(activityChannelPOQueryWrapper);
        activityChannelDetailVOList = activityChannelPOS.stream().map(ActivityChannelPO::poConvertToVo).collect(Collectors.toList());
        return activityChannelDetailVOList;
    }

    /**
     * 构建活动详情VO
     * @param activityInfoDetailVO
     * @param moduleInfoDetailVOS
     * @param activityEventDetailVOS
     * @param receiveLimitDetailVOS
     * @param rewardRuleDetailVOS
     * @param activityChannelDetailVOS
     * @return
     */
    MActivityInfoDetailVO constructActivityInfoDetailVO(MActivityInfoDetailVO activityInfoDetailVO, List<MModuleInfoDetailVO> moduleInfoDetailVOS, List<MActivityEventDetailVO> activityEventDetailVOS, List<MReceiveLimitDetailVO> receiveLimitDetailVOS, List<MRewardRuleDetailVO> rewardRuleDetailVOS, List<MActivityChannelDetailVO> activityChannelDetailVOS) {
        // 设置模块信息
        activityInfoDetailVO.setMModuleInfoDetailVOList(moduleInfoDetailVOS);

        // 设置活动事件信息
        for (MModuleInfoDetailVO moduleInfo : moduleInfoDetailVOS) {
            List<MActivityEventDetailVO> eventsForModule = activityEventDetailVOS.stream()
                    .filter(event -> event.getModuleId().equals(moduleInfo.getModuleId()))
                    .collect(Collectors.toList());
            moduleInfo.setMActivityEventDetailVOS(eventsForModule);

            // 设置接收限制信息
            for (MActivityEventDetailVO event : eventsForModule) {
                List<MReceiveLimitDetailVO> receiveLimitsForEvent = receiveLimitDetailVOS.stream()
                        .filter(limit -> limit.getEventId().equals(event.getEventId()))
                        .collect(Collectors.toList());
                event.setMReceiveLimitDetailVOS(receiveLimitsForEvent);

                // 设置奖励规则信息
                List<MRewardRuleDetailVO> rewardRulesForEvent = rewardRuleDetailVOS.stream()
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
     * @param param
     * @return
     */
    Boolean saveActivityInfoTemp(MActivityInfoSaveParam param){
        QueryWrapper<ActivityInfoTempPO> activityInfoTempPOQueryWrapper = new QueryWrapper<>();
        activityInfoTempPOQueryWrapper.lambda().eq(ActivityInfoTempPO::getActivityId, param.getActivityId())
                .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
        ActivityInfoTempPO activityInfoTempPO = activityInfoTempDAO.selectOne(activityInfoTempPOQueryWrapper);
        if (!ObjectUtils.isEmpty(activityInfoTempPO)){
            //非空删除原有活动临时数据
            activityInfoTempDAO.delete(activityInfoTempPOQueryWrapper);
        }

        ActivityInfoTempPO insertActivityInfoTempPO = buildActivityInfoTempPO(param);
        int insert = activityInfoTempDAO.insert(insertActivityInfoTempPO);
        return insert > 0;
    }

    /**
     * 构建活动临时数据
     * @param param
     * @return
     */
    ActivityInfoTempPO buildActivityInfoTempPO(MActivityInfoSaveParam param){
        ActivityInfoTempPO activityInfoTempPO = new ActivityInfoTempPO();
        activityInfoTempPO.setActivityId(param.getActivityId());
        activityInfoTempPO.setActivityName(param.getActivityName());
        return activityInfoTempPO;
    }
}
