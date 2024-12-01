package com.minyan.nascommon.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @decription 领取发放参数
 * @author minyan.he
 * @date 2024/10/29 22:28
 */
@Data
public class CReceiveSendParam {
  @NotBlank(message = "用户注册id不能为空")
  private String userId;

  @NotNull(message = "活动id不能为空")
  private Integer activityId;

  private Integer moduleId;

  @NotNull(message = "渠道编码不能为空")
  private String channelCode;

  private Long eventId;
  private String eventType;
  private String jsonData;
}
