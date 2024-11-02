package com.minyan.nasmapi.handler.activityAuditPass;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;

/**
 * @decription 活动审核处理handler
 * @author minyan.he
 * @date 2024/10/9 16:30
 */
public interface ActivityAuditPassHandler {
  ApiResult handle(ActivityAuditPassContext context);

  void fallBack(ActivityAuditPassContext context);
}
