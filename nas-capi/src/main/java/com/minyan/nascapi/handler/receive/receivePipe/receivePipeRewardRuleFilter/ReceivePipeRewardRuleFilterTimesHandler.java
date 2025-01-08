package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2025/1/8 21:19
 */
@Service
@Order(20)
public class ReceivePipeRewardRuleFilterTimesHandler implements ReceivePipeRewardRuleFilterInterfaceHandler{
    @Override
    public void handle(ReceivePipeContext context) {

    }
}
