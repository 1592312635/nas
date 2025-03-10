package com.minyan.nascapi.controller;

import com.minyan.nascapi.service.JoinRecordService;
import com.minyan.nascommon.param.CJoinQueryParam;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.vo.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @decription 参与记录相关controller
 * @author minyan.he
 * @date 2024/10/28 22:39
 */
@RestController
@RequestMapping("/c/join")
public class JoinController {
  @Autowired private JoinRecordService joinRecordService;

    @RequestMapping("/record")
    public ApiResult record(@RequestBody CJoinRecordParam param){
        return joinRecordService.record(param);
    }

    @RequestMapping("/query")
    public ApiResult query(@RequestBody CJoinQueryParam param){
        return joinRecordService.query(param);
    }
}
