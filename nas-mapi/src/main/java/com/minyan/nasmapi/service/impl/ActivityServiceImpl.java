package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoDetailQueryParam;
import com.minyan.nascommon.param.MActivityInfoQueryParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ModuleInfoPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MActivityInfoDetailVO;
import com.minyan.nascommon.vo.MActivityInfoVO;
import com.minyan.nasdao.NasActivityEventDAO;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasModuleInfoDAO;
import com.minyan.nasmapi.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
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
    private NasModuleInfoDAO moduleInfoDAO;

    @Autowired
    private NasActivityEventDAO activityEventDAO;

    @Override
    public ApiResult<List<MActivityInfoVO>> getActivityInfoList(MActivityInfoQueryParam param) {
        List<MActivityInfoVO> mActivityInfoVOS = Lists.newArrayList();
        QueryWrapper<ActivityInfoPO> queryWapper = new QueryWrapper<>();
        Page<ActivityInfoPO> page = new Page<>(param.getPageNum(), param.getPageSize());
        queryWapper.eq(!ObjectUtils.isEmpty(param.getActivityId()), "activity_id", param.getActivityId())
                .like(!StringUtils.isEmpty(param.getActvityName()), "activity_name", param.getActvityName())
                .eq(!ObjectUtils.isEmpty(param.getStatus()), "status", param.getStatus());
        Page<ActivityInfoPO> activityInfoPOPage = activityInfoDAO.selectPage(page, queryWapper);
        List<ActivityInfoPO> activityInfoPOS = activityInfoPOPage.getRecords();
        if (!CollectionUtils.isEmpty(activityInfoPOS)) {
            mActivityInfoVOS = activityInfoPOS.stream().map(MActivityInfoVO::poConvertToVo).collect(Collectors.toList());
        }
        return ApiResult.buildSuccess(mActivityInfoVOS);
    }

    @Override
    public ApiResult<MActivityInfoDetailVO> getActivityInfoDetail(MActivityInfoDetailQueryParam param) {
        MActivityInfoDetailVO mActivityInfoDetailVO = new MActivityInfoDetailVO();
        ActivityInfoPO activityInfoPO = getActivityInfoByActivityId(param.getActivityId());
        if (ObjectUtils.isEmpty(activityInfoPO)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时活动不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.ACTIVITY_NOT_EXIST);
        }
        //填充活动基本数据

        List<ModuleInfoPO> moduleInfoPOS = getModuleInfoByActivityId(param.getActivityId());
        if (ObjectUtils.isEmpty(moduleInfoPOS)) {
            logger.info("[ActivityServiceImpl][getActivityInfoDetail]查询活动详情时模块不存在，活动id：{}", param.getActivityId());
            return ApiResult.build(CodeEnum.MODULE_NOT_EXIST);
        }

        List<ActivityEventPO> activityEventPOS = getActivityEventByModules(param.getActivityId(), moduleInfoPOS);
        return ApiResult.buildSuccess(mActivityInfoDetailVO);
    }

    /**
     * 通过活动id获取活动基本信息
     * @param activityId
     * @return
     */
    ActivityInfoPO getActivityInfoByActivityId(Integer activityId) {
        QueryWrapper<ActivityInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                .eq("del_tag", DelTagEnum.NOT_DEL.getValue());
        return activityInfoDAO.selectOne(queryWrapper);
    }

    /**
     * 通过活动id获取模块信息
     * @param activityId
     * @return
     */
    List<ModuleInfoPO> getModuleInfoByActivityId(Integer activityId) {
        QueryWrapper<ModuleInfoPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                .eq("del_tag", DelTagEnum.NOT_DEL.getValue());
        return moduleInfoDAO.selectList(queryWrapper);
    }

    /**
     * 通过活动模块信息获取事件信息
     * @param activityId
     * @param moduleInfoPOS
     * @return
     */
    List<ActivityEventPO> getActivityEventByModules(Integer activityId, List<ModuleInfoPO> moduleInfoPOS) {
        List<Integer> moduleIds = moduleInfoPOS.stream().map(ModuleInfoPO::getModuleId).collect(Collectors.toList());
        QueryWrapper<ActivityEventPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                .in("module_id", moduleIds)
                .eq("del_tag", DelTagEnum.NOT_DEL.getValue());
        return activityEventDAO.selectList(queryWrapper);
    }
}
