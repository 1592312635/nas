package com.minyan.nascapi.service;

import com.minyan.nascommon.param.CJoinQueryParam;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.CJoinRecordVO;
import java.util.List;

/**
 * @decription 活动参与处理service
 * @author minyan.he
 * @date 2025/3/10 16:27
 */
public interface JoinRecordService {
  ApiResult<Boolean> record(CJoinRecordParam param);

  ApiResult<List<CJoinRecordVO>> query(CJoinQueryParam param);
}
