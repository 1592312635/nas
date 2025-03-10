package com.minyan.nasmapi.service;

import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.param.MActivityInfoDetailQueryParam;
import com.minyan.nascommon.param.MActivityInfoQueryParam;
import com.minyan.nascommon.param.MActivityInfoSaveParam;
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

  ApiResult<Boolean> saveActivityInfo(MActivityInfoSaveParam param);

  ApiResult<Boolean> auditActivityInfo(MActivityInfoAuditParam param);
}
