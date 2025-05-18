package com.minyan.nasmapi.handler.activityAuditChange;

import com.minyan.nascommon.Enum.AuditOperateTypeEnum;
import com.minyan.nascommon.dto.context.ActivityChangeContext;
import com.minyan.nasmapi.handler.activityDelete.ActivityDeleteHandler;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @decription 活动删除处理handler
 * @author minyan.he
 * @date 2025/4/5 17:50
 */
@Service
public class ActivityAuditDeleteHandler implements ActivityAuditChangeHandler {
  private static final Logger logger = LoggerFactory.getLogger(ActivityAuditDeleteHandler.class);

  @Autowired private List<ActivityDeleteHandler> activityDeleteHandlers;

  @Override
  public Boolean match(Integer operateType) {
    return AuditOperateTypeEnum.ACTIVITY_CLOSE.getValue().equals(operateType);
  }

  @Override
  public void handle(ActivityChangeContext context) {
    activityDeleteHandlers.forEach(
        activityDeleteHandler ->
            activityDeleteHandler.delete(context.getActivityInfoAuditParam().getActivityId()));
  }
}
