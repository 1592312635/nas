package com.minyan.nascapi.handler.receive;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.vo.context.ReceiveSendContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 领取门槛校验handler
 * @author minyan.he
 * @date 2024/10/29 22:51
 */
@Order(20)
@Service
public class ReceiveRuleHandler extends ReceiveAbstractHandler{
    @Override
    public Boolean handle(ReceiveSendContext context) {
        CReceiveSendParam param = context.getParam();
        return null;
    }
}
