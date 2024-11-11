package com.minyan.nascapi.handler.receive.receiveRule.receiveLimit;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascommon.dto.ReceiveLimitJsonDto;
import com.minyan.nascommon.po.ReceiveLimitPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2024/11/11 20:55
 */
@Service
public abstract class ReceiveLimitCheckAbstractHandler implements ReceiveLimitCheckHandler{
    public static final Logger logger = LoggerFactory.getLogger(ReceiveLimitCheckAbstractHandler.class);


    /**
     * 通过receiveLimit解析门槛json信息
     *
     * @param receiveLimitPO
     * @return
     */
    public ReceiveLimitJsonDto analyzeReceiveLimitJsonByReceiveLimitPO(
            ReceiveLimitPO receiveLimitPO) {
        ReceiveLimitJsonDto receiveLimitJsonDto = null;
        if (ObjectUtils.isEmpty(receiveLimitPO) || StringUtils.isEmpty(receiveLimitPO.getLimitJson())) {
            return receiveLimitJsonDto;
        }
        try {
            receiveLimitJsonDto =
                    JSONObject.parseObject(receiveLimitPO.getLimitJson(), ReceiveLimitJsonDto.class);
        } catch (Exception e) {
            logger.info(
                    "[ReceiveRuleManagerImpl][analyzeReceiveLimitJsonByReceiveLimitPO]解析领取门槛json时发生异常，门槛信息：{}",
                    JSONObject.toJSONString(receiveLimitPO),
                    e);
        }
        return receiveLimitJsonDto;
    }
}
