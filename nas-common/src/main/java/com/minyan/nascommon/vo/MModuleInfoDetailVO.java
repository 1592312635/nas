package com.minyan.nascommon.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @decription 活动详情模块信息出参
 * @author minyan.he
 * @date 2024/9/19 16:37
 */
@Data
public class MModuleInfoDetailVO {
    private Integer activityId;
    private Integer moduleId;
    private String moduleName;
    private Date beginTime;
    private Date endTime;
    private List<MActivityEventDetailVO> mActivityEventDetailVOS;
}
