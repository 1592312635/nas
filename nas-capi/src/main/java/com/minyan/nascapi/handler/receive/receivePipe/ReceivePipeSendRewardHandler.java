package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.minyan.nascapi.handler.receive.receivePipe.receivePipeSendReward.ReceivePipeSendRewardInterfaceHandler;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.CycleEnum;
import com.minyan.nascommon.Enum.SendStatusEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nascommon.po.SendFlowPO;
import com.minyan.nascommon.utils.TimeUtil;
import com.minyan.nasdao.NasSendFlowDAO;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 奖品发放handler
 * @author minyan.he
 * @date 2024/11/29 15:50
 */
@Order(70)
@Service
public class ReceivePipeSendRewardHandler extends ReceivePipeAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ReceivePipeSendRewardHandler.class);

  @Autowired
  private List<ReceivePipeSendRewardInterfaceHandler> receivePipeSendRewardInterfaceHandlers;

  @Autowired private NasSendFlowDAO nasSendFlowDAO;

  @SneakyThrows
  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();
    List<RewardRulePO> sendRewardRuleList = context.getFinalRewardRuleList();
    if (CollectionUtils.isEmpty(sendRewardRuleList)) {
      logger.info(
          "[ReceivePipeSendRewardHandler][handler]奖品发放管道领取项为空，请求参数：{}",
          JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.SEND_REWARD_RULE_IS_EMPTY);
    }

    for (RewardRulePO rewardRulePO : sendRewardRuleList) {
      // 记录nas_send_flow数据用于后续（注意待发放状态）
      SendFlowPO sendFlowPO = buildNasSendFlowPO(context, rewardRulePO);
      ReceivePipeSendRewardInterfaceHandler targetReceivePipeSendRewardInterfaceHandler =
          receivePipeSendRewardInterfaceHandlers.stream()
              .filter(
                  receivePipeSendRewardInterfaceHandler ->
                      receivePipeSendRewardInterfaceHandler.match(rewardRulePO.getRewardType()))
              .findFirst()
              .orElse(null);
      try {
        targetReceivePipeSendRewardInterfaceHandler.handle(context, rewardRulePO);
      } catch (Exception e) {
        logger.info(
            "[ReceivePipeSendRewardHandler][handler]奖品发放管道调用子系统发放奖品异常，请求参数：{}",
            JSONObject.toJSONString(context),
            e);
        sendFlowPO.setStatus(SendStatusEnum.PENDING.getValue());
        sendFlowPO.setScheduleTime(TimeUtil.adjustDate(new Date(), CycleEnum.MINUTES, 20));
        throw e;
      } finally {
        sendFlowPO.setStatus(SendStatusEnum.SUCCESS.getValue());
        nasSendFlowDAO.insert(sendFlowPO);
      }
    }
    return null;
  }

  /**
   * 构建nas_send_flow
   *
   * @param context
   * @param rewardRulePO
   * @return
   */
  SendFlowPO buildNasSendFlowPO(ReceivePipeContext context, RewardRulePO rewardRulePO) {
    SendFlowPO sendFlowPO = new SendFlowPO();
    sendFlowPO.setActivityId(context.getActivityInfoPO().getActivityId());
    sendFlowPO.setModuleId(context.getModuleInfoPO().getModuleId());
    sendFlowPO.setUserId(context.getParam().getUserId());
    sendFlowPO.setRewardId(rewardRulePO.getRewardId());
    sendFlowPO.setRewardType(rewardRulePO.getRewardType());
    sendFlowPO.setStatus(SendStatusEnum.SUCCESS.getValue());
    return sendFlowPO;
  }
}
