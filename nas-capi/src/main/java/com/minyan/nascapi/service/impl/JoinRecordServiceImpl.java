package com.minyan.nascapi.service.impl;

import com.minyan.nascapi.handler.join.JoinTypeRecordHandler;
import com.minyan.nascapi.service.JoinRecordService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.param.CJoinQueryParam;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @decription
 * @author minyan.he
 * @date 2025/3/10 16:27
 */
@Service
public class JoinRecordServiceImpl implements JoinRecordService {
    @Autowired private List<JoinTypeRecordHandler> joinTypeRecordHandlerList;

    @Override
    public ApiResult record(CJoinRecordParam param) {
        joinTypeRecordHandlerList.forEach(joinTypeRecordHandler -> {
            if (joinTypeRecordHandler.match(param.getJoinType())) {
                joinTypeRecordHandler.handle(param);
            }
        });
        return ApiResult.build(CodeEnum.SUCCESS);
    }

    @Override
    public ApiResult query(CJoinQueryParam param) {
        return null;
    }
}
