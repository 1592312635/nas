package com.minyan.nasmapi.service;

import com.minyan.nascommon.param.MRewardDeleteParam;
import com.minyan.nascommon.param.MRewardSaveParam;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MRewardSaveVO;

/**
 * @decription
 * @author minyan.he
 * @date 2025/3/29 18:01
 */
public interface RewardService {
  ApiResult<MRewardSaveVO> saveReward(MRewardSaveParam param);

  ApiResult<?> deleteReward(MRewardDeleteParam param);
}
