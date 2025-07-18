package com.minyan.nasmapi.handler.activityAuditChange;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.ActivityStatusEnum;
import com.minyan.nascommon.Enum.AuditOperateTypeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityChangeContext;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @decription 活动关闭处理handler
 * @author minyan.he
 * @date 2025/4/5 17:50
 */
@Service
public class ActivityAuditCloseHandler implements ActivityAuditChangeHandler {
  private static final Logger logger = LoggerFactory.getLogger(ActivityAuditCloseHandler.class);

  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public Boolean match(Integer operateType) {
    return AuditOperateTypeEnum.ACTIVITY_CLOSE.getValue().equals(operateType);
  }

  @Override
  public void handle(ActivityChangeContext context) {
    // 更新临时表状态
    UpdateWrapper<ActivityInfoTempPO> activityInfoTempPOUpdateWrapper = new UpdateWrapper<>();
    activityInfoTempPOUpdateWrapper
            .lambda()
            .set(ActivityInfoTempPO::getStatus, ActivityStatusEnum.STOP.getValue())
            .eq(ActivityInfoTempPO::getActivityId, context.getActivityInfoAuditParam().getActivityId())
            .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityInfoTempDAO.update(null, activityInfoTempPOUpdateWrapper);

    // 更新主表状态
    UpdateWrapper<ActivityInfoPO> activityInfoPOUpdateWrapper = new UpdateWrapper<>();
    activityInfoPOUpdateWrapper
        .lambda()
        .set(ActivityInfoPO::getStatus, ActivityStatusEnum.STOP.getValue())
        .eq(ActivityInfoPO::getActivityId, context.getActivityInfoAuditParam().getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityInfoDAO.update(null, activityInfoPOUpdateWrapper);
  }
}
