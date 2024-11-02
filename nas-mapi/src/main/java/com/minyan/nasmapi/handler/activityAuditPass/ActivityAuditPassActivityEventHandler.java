package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityEventPO;
import com.minyan.nascommon.po.ActivityEventTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
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
 * @decription 活动审核通过时间处理handler
 * @author minyan.he
 * @date 2024/10/20 10:42
 */
@Order(30)
@Service
public class ActivityAuditPassActivityEventHandler extends ActivityAuditPassAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditPassActivityEventHandler.class);
  @Autowired private NasActivityEventTempDAO activityEventTempDAO;
  @Autowired private NasActivityEventDAO activityEventDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询临时表数据
    QueryWrapper<ActivityEventTempPO> activityEventTempPOQueryWrapper = new QueryWrapper<>();
    activityEventTempPOQueryWrapper
        .lambda()
        .eq(ActivityEventTempPO::getActivityId, param.getActivityId())
        .eq(ActivityEventTempPO::getDelTag, 0);
    List<ActivityEventTempPO> activityEventTempPOS =
        activityEventTempDAO.selectList(activityEventTempPOQueryWrapper);
    if (CollectionUtils.isEmpty(activityEventTempPOS)) {
      logger.info(
          "[ActivityAuditPassActivityEventHandler][handle]活动审核通过时，待审核事件信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_EVENT_NOT_EXIST);
    }

    // 删除主表数据
    UpdateWrapper<ActivityEventPO> activityEventPODeleteWrapper = new UpdateWrapper<>();
    activityEventPODeleteWrapper
        .lambda()
        .set(ActivityEventPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityEventPO::getActivityId, param.getActivityId())
        .eq(ActivityEventPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    activityEventDAO.update(null, activityEventPODeleteWrapper);

    // 临时表数据同步主表
    for (ActivityEventTempPO activityEventTempPO : activityEventTempPOS) {
      activityEventDAO.insert(activityEventTempPOtoActivityEventPO(activityEventTempPO));
    }

    return null;
  }

  /**
   * 活动事件临时表信息转化事件主表信息
   *
   * @param activityEventTempPO
   * @return
   */
  ActivityEventPO activityEventTempPOtoActivityEventPO(ActivityEventTempPO activityEventTempPO) {
    ActivityEventPO activityEventPO = new ActivityEventPO();
    activityEventPO.setActivityId(activityEventTempPO.getActivityId());
    activityEventPO.setModuleId(activityEventTempPO.getModuleId());
    activityEventPO.setEventId(activityEventTempPO.getEventId());
    activityEventPO.setEventName(activityEventTempPO.getEventName());
    activityEventPO.setEventType(activityEventTempPO.getEventType());
    return activityEventPO;
  }
}
