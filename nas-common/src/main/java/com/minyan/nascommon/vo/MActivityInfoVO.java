package com.minyan.nascommon.vo;

import com.minyan.nascommon.po.ActivityInfoPO;
import lombok.Data;

import java.util.Date;

/**
 * @decription m端后台活动信息查询出参
 * @author minyan.he
 * @date 2024/9/18 19:16
 */
@Data
public class MActivityInfoVO {
    private Integer activityId;
    private String activityName;
    private Date beginTime;
    private Date endTime;
    private Integer status;

    /**
     * 转化出参
     * @param po
     * @return
     */
    public static MActivityInfoVO poConvertToVo(ActivityInfoPO po) {
        MActivityInfoVO mActivityInfoVO = new MActivityInfoVO();
        mActivityInfoVO.setActivityId(po.getActivityId());
        mActivityInfoVO.setActivityName(po.getActivityName());
        mActivityInfoVO.setBeginTime(po.getBeginTime());
        mActivityInfoVO.setEndTime(po.getEndTime());
        mActivityInfoVO.setStatus(po.getStatus());
        return mActivityInfoVO;
    }
}
