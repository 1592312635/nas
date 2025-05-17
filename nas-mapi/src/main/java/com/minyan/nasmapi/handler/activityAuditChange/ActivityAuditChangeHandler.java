package com.minyan.nasmapi.handler.activityAuditChange;

import com.minyan.nascommon.dto.context.ActivityChangeContext;

/**
 * @decription 活动变更处理
 * @author minyan.he
 * @date 2025/4/5 17:45
 */
public interface ActivityAuditChangeHandler {
  Boolean match(Integer operateType);

  void handle(ActivityChangeContext context);
}
