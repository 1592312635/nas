package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityRewardPO;
import com.minyan.nascommon.po.ActivityRewardTempPO;
import com.minyan.nasdao.NasActivityRewardDAO;
import com.minyan.nasdao.NasActivityRewardTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动奖品信息删除handler
 * @author minyan.he
 * @date 2025/5/17 18:01
 */
@Service
@Order(40)
public class ActivityRewardDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasActivityRewardDAO activityRewardDAO;
  @Autowired private NasActivityRewardTempDAO activityRewardTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ActivityRewardPO> activityRewardPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ActivityRewardTempPO> activityRewardTempPOUpdateWrapper = new UpdateWrapper<>();

    activityRewardPOUpdateWrapper
        .lambda()
        .set(ActivityRewardPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityRewardPO::getActivityId, activityId)
        .eq(ActivityRewardPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityRewardTempPOUpdateWrapper
        .lambda()
        .set(ActivityRewardTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityRewardTempPO::getActivityId, activityId)
        .eq(ActivityRewardTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    activityRewardDAO.update(null, activityRewardPOUpdateWrapper);
    activityRewardTempDAO.update(null, activityRewardTempPOUpdateWrapper);
  }
}
