package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascommon.param.MActivityInfoQueryParam;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MActivityInfoVO;
import com.minyan.nasdao.NasActivityInfoDAO;
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
}
