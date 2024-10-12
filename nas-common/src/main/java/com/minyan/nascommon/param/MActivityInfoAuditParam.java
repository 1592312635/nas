package com.minyan.nascommon.param;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @decription 活动审核参数
 * @author minyan.he
 * @date 2024/10/9 16:18
 */
@Data
public class MActivityInfoAuditParam {
  @NotNull(message = "活动id不能为空")
  private Integer activityId;

  @NotNull(message = "活动审核操作不能为空")
  private Integer auditStatus;

  private String auditDesc;
}
