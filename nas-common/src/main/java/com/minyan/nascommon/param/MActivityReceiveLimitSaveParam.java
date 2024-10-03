package com.minyan.nascommon.param;

import lombok.Data;

/**
 * @decription 领取规则门槛保存参数
 * @author minyan.he
 * @date 2024/10/3 21:11
 */
@Data
public class MActivityReceiveLimitSaveParam {
  private String limitKey;
  private String limitJson;
  private Integer limitType;
}
