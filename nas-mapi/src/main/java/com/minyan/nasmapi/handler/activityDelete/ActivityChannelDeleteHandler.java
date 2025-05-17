package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityChannelPO;
import com.minyan.nascommon.po.ActivityChannelTempPO;
import com.minyan.nasdao.NasActivityChannelDAO;
import com.minyan.nasdao.NasActivityChannelTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动渠道删除handler
 * @author minyan.he
 * @date 2025/5/17 18:10
 */
@Service
@Order(70)
public class ActivityChannelDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasActivityChannelDAO activityChannelDAO;
  @Autowired private NasActivityChannelTempDAO activityChannelTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ActivityChannelPO> activityChannelPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ActivityChannelTempPO> activityChannelTempPOUpdateWrapper = new UpdateWrapper<>();

    activityChannelPOUpdateWrapper
        .lambda()
        .set(ActivityChannelPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityChannelPO::getActivityId, activityId)
        .eq(ActivityChannelPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityChannelTempPOUpdateWrapper
        .lambda()
        .set(ActivityChannelTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityChannelTempPO::getActivityId, activityId)
        .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    activityChannelDAO.update(null, activityChannelPOUpdateWrapper);
    activityChannelTempDAO.update(null, activityChannelTempPOUpdateWrapper);
  }
}
