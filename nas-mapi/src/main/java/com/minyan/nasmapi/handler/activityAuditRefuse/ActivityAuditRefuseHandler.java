package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;

/**
 * @decription 活动审核不通过handler
 * @author minyan.he
 * @date 2024/10/12 10:29
 */
public interface ActivityAuditRefuseHandler {
    ApiResult handle(ActivityAuditRefuseContext context);

    void fallBack(ActivityAuditRefuseContext context);
}
