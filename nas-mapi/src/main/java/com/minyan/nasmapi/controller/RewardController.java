package com.minyan.nasmapi.controller;

import com.minyan.nascommon.param.MRewardSaveParam;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MRewardSaveVO;
import com.minyan.nasmapi.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @decription 奖品操作入口
 * @author minyan.he
 * @date 2025/3/29 17:49
 */
@RestController
@RequestMapping("/m/reward")
public class RewardController {

  @Autowired private RewardService rewardService;

  /**
   * 注意：该接口只负责生成新奖品，用于活动保存时携带奖品id，更新走活动保存接口
   *
   * @param param
   * @return
   */
  public ApiResult<MRewardSaveVO> saveReward(@RequestBody @Validated MRewardSaveParam param) {
    return rewardService.saveReward(param);
  }
}
