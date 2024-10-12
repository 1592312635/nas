package com.minyan.nascommon.vo.context;

import com.minyan.nascommon.param.MActivityInfoAuditParam;
import lombok.Data;

/**
 * @decription 活动审核不通过中间处理context
 * @author minyan.he
 * @date 2024/10/12 10:31
 */
@Data
public class ActivityAuditRefuseContext {
  private MActivityInfoAuditParam param;
}
