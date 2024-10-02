package com.minyan.nascommon.param;

import lombok.Data;

/**
 * @decription 领取规则保存参数
 * @author minyan.he
 * @date 2024/10/2 17:49
 */
@Data
public class MActivityReceiveRuleSaveParam {
  private Long receiveRuleId;
  private Integer runType;
  private String limitKey;
  private String limitJson;
  private Integer limitType;
}
