package com.minyan.nasmapi.controller;

import com.minyan.nascommon.param.*;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MActivityInfoDetailVO;
import com.minyan.nascommon.vo.MActivityInfoVO;
import com.minyan.nasmapi.service.ActivityService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @decription
 * @author minyan.he
 * @date 2024/9/18 19:10
 */
@RestController
@RequestMapping("/m/activity")
public class ActivityController {
  Logger logger = LoggerFactory.getLogger(ActivityController.class);

  @Autowired private ActivityService activityService;

  @RequestMapping("/info")
  ApiResult<List<MActivityInfoVO>> getActivityInfo(
      @RequestBody @Validated MActivityInfoQueryParam param) {
    return activityService.getActivityInfoList(param);
  }

  @RequestMapping("/detail")
  ApiResult<MActivityInfoDetailVO> getActivityInfoDetail(
      @RequestBody @Validated MActivityInfoDetailQueryParam param) {
    return activityService.getActivityInfoDetail(param);
  }

  @RequestMapping("/save")
  ApiResult<?> saveActivityInfo(@RequestBody @Validated MActivityInfoSaveParam param) {
    return activityService.saveActivityInfo(param);
  }

  @RequestMapping("/audit")
  ApiResult<?> auditActivityInfo(@RequestBody @Validated MActivityInfoAuditParam param) {
    return activityService.auditActivityInfo(param);
  }

  @RequestMapping("/change")
  ApiResult<?> changeActivityInfo(@RequestBody @Validated MActivityChangeParam param) {
    return activityService.changeActivityInfo(param);
  }
}
