package com.minyan.nascommon.dto.context;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.*;
import java.util.List;
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
  private List<RewardRulePO> rewardRulePOList;
  private List<RewardLimitPO> rewardLimitPOList;
}