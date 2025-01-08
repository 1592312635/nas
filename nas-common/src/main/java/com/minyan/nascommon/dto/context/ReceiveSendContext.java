package com.minyan.nascommon.dto.context;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import java.util.List;
import lombok.Data;

/**
 * @decription 领取发放中间处理context
 * @author minyan.he
 * @date 2024/10/29 22:51
 */
@Data
public class ReceiveSendContext {
  private CReceiveSendParam param;

  // 本次请求时间涉及的所有领取规则
  List<ReceiveRulePO> receiveRulePOList;
  // 本次请求事件涉及的所有领取门槛
  List<ReceiveLimitPO> receiveLimitList;
}
