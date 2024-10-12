package com.minyan.nasmapi.handler.activityAuditPass;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核通过活动奖励信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:13
 */
@Order(20)
@Service
public class ActivityAuditPassActivityRewardHandler extends ActivityAuditPassAbstractHandler{
    @Override
    public ApiResult handle(ActivityAuditPassContext context) {
        return null;
    }
}
