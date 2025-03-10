package com.minyan.nascapi.service;

import com.minyan.nascommon.param.CJoinQueryParam;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.vo.ApiResult;

/**
 * @decription 活动参与处理service
 * @author minyan.he
 * @date 2025/3/10 16:27
 */
public interface JoinRecordService {
    ApiResult record(CJoinRecordParam param);

    ApiResult query(CJoinQueryParam param);
}
