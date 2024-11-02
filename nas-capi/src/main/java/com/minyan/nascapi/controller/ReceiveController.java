package com.minyan.nascapi.controller;

import com.minyan.nascapi.service.ReceiveService;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.vo.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @decription 领取接口
 * @author minyan.he
 * @date 2024/10/28 22:37
 */
@RestController
@RequestMapping("/c/receive")
public class ReceiveController {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveController.class);
  @Autowired private ReceiveService receiveService;

  @RequestMapping("/send")
  public ApiResult<?> send(@RequestBody CReceiveSendParam param) {
    return receiveService.send(param);
  }
}
