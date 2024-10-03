package com.minyan.nascommon.param;

import lombok.Data;

import java.util.List;

/**
 * @decription 领取规则保存参数
 * @author minyan.he
 * @date 2024/10/2 17:49
 */
@Data
public class MActivityReceiveRuleSaveParam {
  private Long receiveRuleId;
  private Integer ruleType;
  private List<MActivityReceiveLimitSaveParam> receiveLimitSaveInfos;
}
