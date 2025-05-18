package com.minyan.nascommon.dto.context;

import com.minyan.nascommon.param.MActivityInfoAuditParam;
import lombok.Data;

/**
 * @decription 活动变更中间处理context
 * @author minyan.he
 * @date 2025/4/5 17:48
 */
@Data
public class ActivityChangeContext {
  MActivityInfoAuditParam activityInfoAuditParam;
}
