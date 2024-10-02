package com.minyan.nascommon.vo;

import lombok.Data;

/**
 * @decription 渠道信息出参
 * @author minyan.he
 * @date 2024/9/19 17:14
 */
@Data
public class MActivityChannelDetailVO {
  private Integer activityId;
  private String channelName;
  private String channelCode;
  private String channelUrl;
}
