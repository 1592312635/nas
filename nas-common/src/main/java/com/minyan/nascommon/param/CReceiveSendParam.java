package com.minyan.nascommon.param;

import lombok.Data;

/**
 * @decription 领取发放参数
 * @author minyan.he
 * @date 2024/10/29 22:28
 */
@Data
public class CReceiveSendParam {
  private Integer activityId;
  private Integer moduleId;
  private Long eventId;
  private String eventType;
  private String jsonData;
}
