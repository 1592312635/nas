package com.minyan.nascommon.dto.context;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import lombok.Data;

/**
 * @decription 领取门槛校验handler
 * @author minyan.he
 * @date 2024/11/2 10:46
 */
@Data
public class ReceiveLimitCheckContext {
  CReceiveSendParam param;
  ReceiveLimitPO receiveLimitPO;
}
