package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ActivityChannelPO;
import com.minyan.nascommon.po.ActivityChannelTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasActivityChannelDAO;
import com.minyan.nasdao.NasActivityChannelTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动审核不通过活动渠道处理
 * @author minyan.he
 * @date 2024/10/12 11:18
 */
@Order(40)
@Service
public class ActivityAuditRefuseActivityChannelHandler extends ActivityAuditRefuseAbstractHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseActivityChannelHandler.class);
  @Autowired private NasActivityChannelTempDAO activityChannelTempDAO;
  @Autowired private NasActivityChannelDAO activityChannelDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    // 删除临时表渠道数据
    UpdateWrapper<ActivityChannelTempPO> activityChannelTempPODeleteWrapper = new UpdateWrapper<>();
    activityChannelTempPODeleteWrapper
        .lambda()
        .set(ActivityChannelTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityChannelTempPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityChannelTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityChannelTempDAO.update(null, activityChannelTempPODeleteWrapper);

    // 主表渠道同步临时表
    QueryWrapper<ActivityChannelPO> activityChannelPOQueryWrapper = new QueryWrapper<>();
    activityChannelPOQueryWrapper
        .lambda()
        .eq(ActivityChannelPO::getActivityId, context.getParam().getActivityId())
        .eq(ActivityChannelPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ActivityChannelPO> activityChannelPOS =
        activityChannelDAO.selectList(activityChannelPOQueryWrapper);
    if (!CollectionUtils.isEmpty(activityChannelPOS)) {
      for (ActivityChannelPO activityChannelPO : activityChannelPOS) {
        activityChannelTempDAO.insert(activityChannelTempPOToActivityChannelPO(activityChannelPO));
      }
    }

    return null;
  }

  /**
   * 活动渠道PO转活动渠道临时PO
   *
   * @param activityChannelPO
   * @return
   */
  ActivityChannelTempPO activityChannelTempPOToActivityChannelPO(
      ActivityChannelPO activityChannelPO) {
    ActivityChannelTempPO activityChannelTempPO = new ActivityChannelTempPO();
    activityChannelTempPO.setActivityId(activityChannelPO.getActivityId());
    activityChannelTempPO.setChannelCode(activityChannelPO.getChannelCode());
    activityChannelTempPO.setChannelName(activityChannelPO.getChannelName());
    return activityChannelTempPO;
  }
}
