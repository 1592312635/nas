package com.minyan.nascapi.handler.receive;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.minyan.nascapi.handler.receive.receivePipe.ReceivePipeAbstractHandler;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.dto.context.ReceiveSendContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 发奖管道处理handler
 * @author minyan.he
 * @date 2024/10/29 22:50
 */
@Order(30)
@Service
public class ReceivePipeHandler extends ReceiveAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceivePipeHandler.class);

  @Autowired List<ReceivePipeAbstractHandler> receivePipeAbstractHandlers;

  @Override
  public Boolean handle(ReceiveSendContext context) {
    CReceiveSendParam param = context.getParam();
    ReceivePipeContext receivePipeContext = new ReceivePipeContext();
    receivePipeContext.setParam(param);
    receivePipeContext.setReceiveLimitPOList(context.getReceiveLimitList());
    List<ReceivePipeAbstractHandler> fallBackList = Lists.newArrayList();

    try {
      for (ReceivePipeAbstractHandler receivePipeAbstractHandler : receivePipeAbstractHandlers) {
        fallBackList.add(receivePipeAbstractHandler);
        Boolean handle = receivePipeAbstractHandler.handle(receivePipeContext);
        if (!ObjectUtils.isEmpty(handle)) {
          return handle;
        }
      }
    } catch (Exception e) {
      logger.info(
          "[ReceivePipeHandler][handle]活动发奖管道内部异常，请求参数：{}", JSONObject.toJSONString(param), e);
      for (ReceivePipeAbstractHandler receivePipeAbstractHandler : fallBackList) {
        receivePipeAbstractHandler.fallBack(receivePipeContext);
      }
    }

    return false;
  }
}
