package com.minyan.nascapi.handler.receive.receivePipe.receivePipeRewardRuleFilter;

import com.minyan.nascommon.dto.context.ReceivePipeContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 奖品规则过滤器-次数
 * @author minyan.he
 * @date 2025/1/8 21:19
 */
@Service
@Order(10)
public class ReceivePipeRewardRuleFilterUserTypeHandler implements ReceivePipeRewardRuleFilterInterfaceHandler{
    @Override
    public void handle(ReceivePipeContext context) {
        // todo 筛选用户类型对应奖品规则
    }
}
