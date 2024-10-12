package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核不通过模块信息处理
 * @author minyan.he
 * @date 2024/10/12 11:17
 */
@Order(30)
@Service
public class ActivityAuditRefuseModuleInfoHandler extends ActivityAuditRefuseAbstractHandler{
    @Override
    public ApiResult handle(ActivityAuditRefuseContext context) {
        return null;
    }
}
