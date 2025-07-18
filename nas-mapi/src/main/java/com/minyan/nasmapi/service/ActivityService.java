package com.minyan.nasmapi.service;

import com.minyan.nascommon.param.*;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MActivityInfoDetailVO;
import com.minyan.nascommon.vo.MActivityInfoVO;
import java.util.List;

/**
 * @decription
 * @author minyan.he
 * @date 2024/9/18 19:11
 */
public interface ActivityService {
  ApiResult<List<MActivityInfoVO>> getActivityInfoList(MActivityInfoQueryParam param);

  ApiResult<MActivityInfoDetailVO> getActivityInfoDetail(MActivityInfoDetailQueryParam param);

  ApiResult<?> saveActivityInfo(MActivityInfoSaveParam param);

  ApiResult<?> auditActivityInfo(MActivityInfoAuditParam param);

  ApiResult<?> changeActivityInfo(MActivityChangeParam param);
}
