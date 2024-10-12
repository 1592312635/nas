package com.minyan.nasmapi.handler.activityAuditPass;

import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/9 16:31
 */
@Service
public abstract class ActivityAuditPassAbstractHandler implements ActivityAuditPassHandler {
  @Override
  public void fallBack(ActivityAuditPassContext context) {}
}
