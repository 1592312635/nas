package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ActivityRewardPO;
import com.minyan.nascommon.po.RewardLimitPO;
import com.minyan.nascommon.po.RewardRulePO;
import com.minyan.nasdao.NasActivityRewardDAO;
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

/**
 * @decription 奖品规则验证handler
 * @author minyan.he
 * @date 2024/11/21 20:09
 */
@Order(10)
@Service
public class ReceivePipeRuleCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceivePipeRuleCheckHandler.class);

  @Autowired private NasRewardRuleDAO rewardRuleDAO;
  @Autowired private NasRewardLimitDAO rewardLimitDAO;
  @Autowired private NasActivityRewardDAO activityRewardDAO;

  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();

    // 获得奖品信息
    QueryWrapper<ActivityRewardPO> activityRewardPOQueryWrapper = new QueryWrapper<>();
    activityRewardPOQueryWrapper
        .lambda()
        .eq(ActivityRewardPO::getActivityId, param.getActivityId())
        .eq(ActivityRewardPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityRewardPO> activityRewardPOS =
        activityRewardDAO.selectList(activityRewardPOQueryWrapper);
    if (CollectionUtils.isEmpty(activityRewardPOS)){
      logger.info(
          "[ReceiveEventCheckHandler][handle]领取接口事件验证不通过，奖品信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return false;
    }

    // 获取奖品规则
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

    // 通过奖品规则获取奖品门槛
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

    context.setActivityRewardPOS(activityRewardPOS);
    context.setRewardRulePOList(rewardRulePOList);
    context.setRewardLimitPOList(rewardLimitPOS);
    return null;
  }
}
