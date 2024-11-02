package com.minyan.nascommon.dto;

import lombok.Data;

/**
 * @decription 领取门槛标识格式字段
 * @author minyan.he
 * @date 2024/11/2 11:52
 */
@Data
public class ReceiveLimitJsonDto {
  private String value;
  private String min;
  private String max;
  private String express;
}
