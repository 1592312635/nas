package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.vo.*;
import java.util.List;

/**
 * @decription 活动维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:22
 */
public interface ActivityInfoManager {
  MActivityInfoDetailVO getActivityInfoByActivityId(Integer activityId);

  MActivityInfoDetailVO constructActivityInfoDetailVO(
      MActivityInfoDetailVO activityInfoDetailVO,
      List<MModuleInfoDetailVO> moduleInfoDetailVOS,
      List<MActivityEventDetailVO> activityEventDetailVOS,
      List<MReceiveRuleDetailVO> receiveRuleDetailVOS,
      List<MRewardRuleDetailVO> rewardRuleDetailVOS,
      List<MActivityChannelDetailVO> activityChannelDetailVOS);

  Boolean saveActivityInfoTemp(MActivityInfoSaveParam param);
}
