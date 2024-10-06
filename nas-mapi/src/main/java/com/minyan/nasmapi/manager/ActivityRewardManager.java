package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityInfoSaveParam;

/**
 * @decription 奖品维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:47
 */
public interface ActivityRewardManager {
  Boolean saveActivityRewardTemp(MActivityInfoSaveParam param);
}
