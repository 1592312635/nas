package com.minyan.nascommon.vo;

import java.util.List;
import lombok.Data;

/**
 * @decription 事件规则信息出参
 * @author minyan.he
 * @date 2024/9/19 17:00
 */
@Data
public class MReceiveRuleDetailVO {
  private Long eventId;
  private Long receiveRuleId;
  private Integer ruleType;
  private List<MReceiveLimitDetailVO> receiveLimitInfos;
}
