package com.minyan.nascommon.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @decription 发奖记录查询接口
 * @author minyan.he
 * @date 2025/3/11 17:52
 */
@Data
public class CReceiveQueryParam {
  @NotBlank(message = "用户注册id不能为空")
  private String userId;

  @NotNull(message = "活动id不能为空")
  private Integer activityId;

  private Integer moduleId;
  private Integer rewardType;
  private Long eventId;
  private String eventType;
  private Integer pageNum = 1;
  private Integer pageSize = 15;
}
