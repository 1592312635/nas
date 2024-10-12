package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核不通过活动奖励信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:16
 */
@Order(20)
@Service
public class ActivityAuditRefuseActivityRewardHandler extends ActivityAuditRefuseAbstractHandler{
    @Override
    public ApiResult handle(ActivityAuditRefuseContext context) {
        return null;
    }
}
