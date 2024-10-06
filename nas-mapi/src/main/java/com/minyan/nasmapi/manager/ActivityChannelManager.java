package com.minyan.nasmapi.manager;

import com.minyan.nascommon.param.MActivityInfoSaveParam;
import com.minyan.nascommon.vo.MActivityChannelDetailVO;
import java.util.List;

/**
 * @decription 渠道维度manager处理
 * @author minyan.he
 * @date 2024/10/6 13:41
 */
public interface ActivityChannelManager {
  List<MActivityChannelDetailVO> getActivityChannelDetailVOList(Integer activityId);

  Boolean saveActivityChannelTemp(MActivityInfoSaveParam param);
}
