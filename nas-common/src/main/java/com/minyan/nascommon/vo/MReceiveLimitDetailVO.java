package com.minyan.nascommon.vo;

import com.minyan.nascommon.po.ReceiveLimitTempPO;
import lombok.Data;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/6 14:13
 */
@Data
public class MReceiveLimitDetailVO {
  private Long receiveRuleId;
  private Long receiveLimitId;
  private String limitKey;
  private String limitJson;
  private Integer limitType;

  public static MReceiveLimitDetailVO convertToVO(ReceiveLimitTempPO receiveLimitTempPO) {
    MReceiveLimitDetailVO vo = new MReceiveLimitDetailVO();
    vo.setReceiveLimitId(receiveLimitTempPO.getId());
    vo.setReceiveRuleId(receiveLimitTempPO.getReceiveRuleId());
    vo.setLimitKey(receiveLimitTempPO.getLimitKey());
    vo.setLimitJson(receiveLimitTempPO.getLimitJson());
    vo.setLimitType(receiveLimitTempPO.getLimitType());
    return vo;
  }
}
