package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityChannelPO;
import com.minyan.nasdao.NasActivityChannelDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 渠道验证处理器
 * @author minyan.he
 * @date 2024/11/21 10:11
 */
@Order(10)
@Service
public class ReceivePipeChannelCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceivePipeChannelCheckHandler.class);

  @Autowired private NasActivityChannelDAO activityChannelDAO;

  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    QueryWrapper<ActivityChannelPO> activityChannelPOQueryWrapper = new QueryWrapper<>();
    activityChannelPOQueryWrapper
        .lambda()
        .eq(ActivityChannelPO::getActivityId, param.getActivityId())
        .eq(ActivityChannelPO::getChannelCode, param.getChannelCode())
        .eq(ActivityChannelPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityChannelPO activityChannelPO =
        activityChannelDAO.selectOne(activityChannelPOQueryWrapper);
    if (ObjectUtils.isEmpty(activityChannelPO)) {
      logger.info(
          "[ReceivePipeChannelCheckHandler][handle]领取接口渠道验证不通过，渠道信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }

    context.setActivityChannelPO(activityChannelPO);
    return null;
  }
}
