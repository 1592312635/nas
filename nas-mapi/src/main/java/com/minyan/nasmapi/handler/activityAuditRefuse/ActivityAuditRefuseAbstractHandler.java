package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/12 10:30
 */
@Service
public abstract class ActivityAuditRefuseAbstractHandler implements ActivityAuditRefuseHandler {
  @Override
  public void fallBack(ActivityAuditRefuseContext context) {}
}
