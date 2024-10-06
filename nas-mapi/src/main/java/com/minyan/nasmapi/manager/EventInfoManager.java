package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityEventSaveParam;
import com.minyan.nascommon.param.MActivityModuleSaveParam;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import com.minyan.nascommon.vo.MModuleInfoDetailVO;
import java.util.List;

/**
 * @decription 事件维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:29
 */
public interface EventInfoManager {
  List<MActivityEventDetailVO> getActivityEventByModules(
      Integer activityId, List<MModuleInfoDetailVO> moduleInfoPOS);

  void saveEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam);

  void saveEventInfo(Integer activityId, Integer moduleId, MActivityEventSaveParam eventSaveInfo);

  void updateEventInfos(Integer activityId, MActivityModuleSaveParam moduleSaveParam);

  void delEventInfos(List<ModuleInfoTempPO> moduleInfoTempPOS);
}
