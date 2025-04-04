package com.minyan.nasmapi.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MRewardDeleteParam;
import com.minyan.nascommon.param.MRewardSaveParam;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.MRewardSaveVO;
import com.minyan.nasdao.NasActivityRewardTempDAO;
import com.minyan.nasmapi.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @decription
 * @author minyan.he
 * @date 2025/3/29 18:01
 */
@Service
public class RewardServiceImpl implements RewardService {
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;

  @Override
  public ApiResult<MRewardSaveVO> saveReward(MRewardSaveParam param) {
    ActivityRewardTempPO activityRewardTempPO = buildActivityRewardTempPO(param);
    activityRewardTempDAO.insert(activityRewardTempPO);
    return ApiResult.buildSuccess(
        buildMRewardSaveVO(activityRewardTempPO.getId(), activityRewardTempPO));
  }

  @Override
  public ApiResult<?> deleteReward(MRewardDeleteParam param) {
    UpdateWrapper<ActivityRewardTempPO> updateWrapper = new UpdateWrapper<>();
    updateWrapper
        .lambda()
        .set(ActivityRewardTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityRewardTempPO::getId, param.getId())
        .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityRewardTempDAO.update(null, updateWrapper);
    return ApiResult.buildSuccess(null);
  }

  /**
   * 构建奖品信息
   *
   * @param param
   * @return
   */
  private ActivityRewardTempPO buildActivityRewardTempPO(MRewardSaveParam param) {
    ActivityRewardTempPO activityRewardTempPO = new ActivityRewardTempPO();
    activityRewardTempPO.setRewardType(param.getRewardType());
    activityRewardTempPO.setRewardName(param.getRewardName());
    activityRewardTempPO.setBatchCode(param.getBatchCode());
    activityRewardTempPO.setImageUrl(param.getImageUrl());
    return activityRewardTempPO;
  }

  /**
   * 构建输出奖品信息
   *
   * @param id
   * @param activityRewardTempPO
   * @return
   */
  private MRewardSaveVO buildMRewardSaveVO(Long id, ActivityRewardTempPO activityRewardTempPO) {
    MRewardSaveVO mRewardSaveVO = new MRewardSaveVO();
    mRewardSaveVO.setId(id);
    mRewardSaveVO.setRewardType(activityRewardTempPO.getRewardType());
    mRewardSaveVO.setRewardName(activityRewardTempPO.getRewardName());
    mRewardSaveVO.setBatchCode(activityRewardTempPO.getBatchCode());
    mRewardSaveVO.setImageUrl(activityRewardTempPO.getImageUrl());
    return mRewardSaveVO;
  }
}
