package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nasdao.NasActivityEventDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动事件验证handler
 * @author minyan.he
 * @date 2024/11/21 12:08
 */
@Order(10)
@Service
public class ReceivePipeEventCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceivePipeEventCheckHandler.class);

  @Autowired private NasActivityEventDAO activityEventDAO;

  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    QueryWrapper<ActivityEventPO> activityEventPOQueryWrapper = new QueryWrapper<>();
    activityEventPOQueryWrapper
        .lambda()
        .eq(ActivityEventPO::getEventId, param.getEventId())
        .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityEventPO activityEventPO = activityEventDAO.selectOne(activityEventPOQueryWrapper);
    if (ObjectUtils.isEmpty(activityEventPO)) {
      logger.info(
          "[ReceiveEventCheckHandler][handle]领取接口事件验证不通过，事件信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }

    context.setActivityEventPO(activityEventPO);
    return null;
  }
}
