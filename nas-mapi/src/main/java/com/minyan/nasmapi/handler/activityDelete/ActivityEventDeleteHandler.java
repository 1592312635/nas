package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.po.ActivityEventTempPO;
import com.minyan.nasdao.NasActivityEventDAO;
import com.minyan.nasdao.NasActivityEventTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动事件删除handler
 * @author minyan.he
 * @date 2025/5/17 17:58
 */
@Service
@Order(30)
public class ActivityEventDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasActivityEventDAO activityEventDAO;
  @Autowired private NasActivityEventTempDAO activityEventTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ActivityEventPO> activityEventPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ActivityEventTempPO> activityEventTempPOUpdateWrapper = new UpdateWrapper<>();

    activityEventTempPOUpdateWrapper
        .lambda()
        .set(ActivityEventTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityEventTempPO::getActivityId, activityId)
        .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityEventPOUpdateWrapper
        .lambda()
        .set(ActivityEventPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityEventPO::getActivityId, activityId)
        .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    activityEventDAO.update(null, activityEventPOUpdateWrapper);
    activityEventTempDAO.update(null, activityEventTempPOUpdateWrapper);
  }
}
