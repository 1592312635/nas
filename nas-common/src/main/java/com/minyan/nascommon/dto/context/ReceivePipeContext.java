package com.minyan.nascommon.dto.context;

import com.google.common.collect.Maps;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.*;
import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * @decription 活动发奖管道中间处理context
 * @author minyan.he
 * @date 2024/11/13 21:15
 */
@Data
public class ReceivePipeContext {
  private CReceiveSendParam param;
  // 当前处理活动信息
  private ActivityInfoPO activityInfoPO;
  // 当前处理模块信息
  private ModuleInfoPO moduleInfoPO;
  private ActivityEventPO activityEventPO;
  private ActivityChannelPO activityChannelPO;
  // 所有领取门槛
  private List<ReceiveLimitPO> receiveLimitPOList;
  // 所有奖品规则
  private List<RewardRulePO> rewardRulePOList;
  // 所有奖品规则门槛
  private List<RewardLimitPO> rewardLimitPOList;
  // 管道筛选后的领取项
  private List<RewardRulePO> finalRewardRuleList;

  // 管道单个handler处理结果
  private Map<String, Boolean> pipeResultMap = Maps.newHashMap();
  // 临时数据存储器
  private Map<String, Object> tempMap = Maps.newHashMap();
}
