package com.minyan.nascapi.service;

import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.vo.ApiResult;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/30 21:24
 */
public interface ReceiveService {
    ApiResult<Boolean> send(CReceiveSendParam param);
}
