package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nasdao.NasActivityEventDAO;
import com.minyan.nasdao.NasRewardLimitDAO;
import com.minyan.nasdao.NasRewardRuleDAO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2024/11/21 12:08
 */
@Order(10)
@Service
public class ReceiveEventCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveEventCheckHandler.class);

  @Autowired private NasActivityEventDAO activityEventDAO;
  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;

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

    // 补充事件下的奖品规则信息
    QueryWrapper<RewardRulePO> rewardRulePOQueryWrapper = new QueryWrapper<>();
    rewardRulePOQueryWrapper
        .lambda()
        .eq(RewardRulePO::getActivityId, param.getActivityId())
        .eq(RewardRulePO::getEventId, param.getEventId())
        .eq(RewardRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardRulePO> rewardRulePOList = rewardRuleDAO.selectList(rewardRulePOQueryWrapper);
    if (CollectionUtils.isEmpty(rewardRulePOList)) {
      logger.info(
          "[ReceiveEventCheckHandler][handle]领取接口事件验证不通过，奖品规则信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }
    List<Long> rewardRuleIds =
        rewardRulePOList.stream().map(RewardRulePO::getId).collect(Collectors.toList());
    QueryWrapper<RewardLimitPO> rewardLimitPOQueryWrapper = new QueryWrapper<>();
    rewardLimitPOQueryWrapper
        .lambda()
        .eq(RewardLimitPO::getActivityId, param.getActivityId())
        .eq(RewardLimitPO::getEventId, param.getEventId())
        .in(RewardLimitPO::getRewardRuleId, rewardRuleIds)
        .eq(RewardLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<RewardLimitPO> rewardLimitPOS = rewardLimitDAO.selectList(rewardLimitPOQueryWrapper);
    if (CollectionUtils.isEmpty(rewardLimitPOS)) {
      logger.info(
          "[ReceiveEventCheckHandler][handle]领取接口事件验证不通过，奖品规则门槛不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }

    context.setActivityEventPO(activityEventPO);
    context.setRewardRulePOList(rewardRulePOList);
    context.setRewardLimitPOList(rewardLimitPOS);
    return null;
  }
}
