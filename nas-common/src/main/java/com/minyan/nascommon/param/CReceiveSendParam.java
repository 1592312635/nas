package com.minyan.nascommon.param;

import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @decription 领取发放参数
 * @author minyan.he
 * @date 2024/10/29 22:28
 */
@Data
public class CReceiveSendParam {
  @NotNull(message = "活动id不能为空")
  private Integer activityId;

  private Integer moduleId;
  private Long eventId;
  private String eventType;
  private String jsonData;
}