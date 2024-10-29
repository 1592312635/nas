package com.minyan.nascapi.handler.receive;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.vo.context.ReceiveSendContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 发奖管道处理handler
 * @author minyan.he
 * @date 2024/10/29 22:50
 */
@Order(30)
@Service
public class ReceivePipeHandler extends ReceiveAbstractHandler{
    @Override
    public Boolean handle(ReceiveSendContext context) {
        CReceiveSendParam param = context.getParam();
        return null;
    }
}
