package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核不通过活动渠道处理
 * @author minyan.he
 * @date 2024/10/12 11:18
 */
@Order(40)
@Service
public class ActivityAuditRefuseActivityChannelHandler extends ActivityAuditRefuseAbstractHandler{
    @Override
    public ApiResult handle(ActivityAuditRefuseContext context) {
    return null;
    }
}
