package com.minyan.nascapi.controller;

import com.minyan.nascommon.vo.ApiResult;
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
    @RequestMapping("/record")
    public ApiResult record(){
        return ApiResult.buildSuccess("参与记录");
    }

    @RequestMapping("/query")
    public ApiResult query(){
        return ApiResult.buildSuccess("查询参与记录");
    }
}
