package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动信息删除handler
 * @author minyan.he
 * @date 2025/5/17 17:45
 */
@Service
@Order(10)
public class ActivityInfoDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasActivityInfoDAO activityInfoDAO;
  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ActivityInfoPO> activityInfoPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ActivityInfoTempPO> activityInfoTempPOUpdateWrapper = new UpdateWrapper<>();

    activityInfoPOUpdateWrapper
        .lambda()
        .set(ActivityInfoPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityInfoPO::getActivityId, activityId)
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityInfoTempPOUpdateWrapper
        .lambda()
        .set(ActivityInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityInfoTempPO::getActivityId, activityId)
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    activityInfoDAO.update(null, activityInfoPOUpdateWrapper);
    activityInfoTempDAO.update(null, activityInfoTempPOUpdateWrapper);
  }
}
