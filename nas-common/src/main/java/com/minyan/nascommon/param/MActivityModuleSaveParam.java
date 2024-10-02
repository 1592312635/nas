package com.minyan.nascommon.param;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @decription 活动模块信息保存参数
 * @author minyan.he
 * @date 2024/10/2 17:36
 */
@Data
public class MActivityModuleSaveParam {
    private Integer moduleId;
    private String moduleName;
    private Date beginTime;
    private Date endTime;
    private List<MActivityEventSaveParam> eventSaveInfos;
}
