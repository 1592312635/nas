package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.po.ActivityEventTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasActivityEventDAO;
import com.minyan.nasdao.NasActivityEventTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 活动审核不通过时间处理handler
 * @author minyan.he
 * @date 2024/10/20 10:43
 */
@Order(30)
@Service
public class ActivityAuditRefuseActivityEventHandler extends ActivityAuditRefuseAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseActivityEventHandler.class);
  @Autowired private NasActivityEventTempDAO activityEventTempDAO;
  @Autowired private NasActivityEventDAO activityEventDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 删除临时表数据
    UpdateWrapper<ActivityEventTempPO> activityEventTempPODeleteWrapper =
        new UpdateWrapper<ActivityEventTempPO>();
    activityEventTempPODeleteWrapper
        .lambda()
        .set(ActivityEventTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityEventTempPO::getActivityId, param.getActivityId())
        .eq(ActivityEventTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityEventTempDAO.update(null, activityEventTempPODeleteWrapper);

    // 主表数据填充临时表
    QueryWrapper<ActivityEventPO> activityEventPOQueryWrapper = new QueryWrapper<>();
    activityEventPOQueryWrapper
        .lambda()
        .eq(ActivityEventPO::getActivityId, param.getActivityId())
        .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityEventPO> activityEventPOS =
        activityEventDAO.selectList(activityEventPOQueryWrapper);
    if (!CollectionUtils.isEmpty(activityEventPOS)) {
      for (ActivityEventPO activityEventPO : activityEventPOS) {
        activityEventTempDAO.insert(activityEventPOtoActivityEventTempPO(activityEventPO));
      }
    }
    return null;
  }

  /**
   * 主表数据转化临时表数据
   *
   * @param activityEventPO
   * @return
   */
  ActivityEventTempPO activityEventPOtoActivityEventTempPO(ActivityEventPO activityEventPO) {
    ActivityEventTempPO activityEventTempPO = new ActivityEventTempPO();
    activityEventTempPO.setActivityId(activityEventPO.getActivityId());
    activityEventTempPO.setModuleId(activityEventPO.getModuleId());
    activityEventTempPO.setEventId(activityEventPO.getEventId());
    activityEventTempPO.setEventName(activityEventPO.getEventName());
    activityEventTempPO.setEventType(activityEventPO.getEventType());
    return activityEventTempPO;
  }
}
