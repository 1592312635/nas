package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.vo.MModuleInfoDetailVO;
import java.util.List;

/**
 * @decription 模块维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:25
 */
public interface ModuleInfoManager {
  List<MModuleInfoDetailVO> getModuleInfoByActivityId(Integer activityId);

  Boolean saveActivityModuleTemp(MActivityInfoSaveParam param);
}
