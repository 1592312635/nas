package com.minyan.nascommon.param;

import lombok.Data;

/**
 * @decription 活动变更审核参数
 * @author minyan.he
 * @date 2025/3/29 19:39
 */
@Data
public class MActivityChangeAuditParam {
    private Integer activityId;
    private Integer auditStatus;
    private String auditDesc;
}

