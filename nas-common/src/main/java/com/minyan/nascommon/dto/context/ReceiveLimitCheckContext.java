package com.minyan.nascommon.dto.context;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import java.util.Map;
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
  // 临时存放待验证门槛信息的map，key是limitKey，value是需要与活动门槛进行验证的数据
  Map<String, String> limitMap;
}
