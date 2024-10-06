package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityEventSaveParam;
import com.minyan.nascommon.param.MActivityReceiveRuleSaveParam;
import com.minyan.nascommon.po.ActivityEventTempPO;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MReceiveLimitDetailVO;
import java.util.List;

/**
 * @decription 领取规则维度处理manager
 * @author minyan.he
 * @date 2024/10/6 13:19
 */
public interface ReceiveRuleManager {
  List<MReceiveLimitDetailVO> getReceiveLimitDetailByEvents(
      List<MActivityEventDetailVO> activityEventDetailVOS);

  void saveRuleTempInfos(
      Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo);

  void updateReceiveRuleInfos(
      Integer activityId,
      Integer moduleId,
      Long eventId,
      List<MActivityReceiveRuleSaveParam> receiveRuleSaveInfos);

  void deleteReceiveRuleTempAndRewardRuleTemp(ActivityEventTempPO activityEventTempPO);
}
