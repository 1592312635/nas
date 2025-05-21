package com.minyan.nasmapi.handler.activityAuditPass;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityAuditPassContext;
import com.minyan.nascommon.dto.context.ActivityChangeContext;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityAuditRecordPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nasdao.NasActivityAuditRecordDAO;
import com.minyan.nasmapi.handler.activityAuditChange.ActivityAuditChangeHandler;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动操作审核通过处理
 * @author minyan.he
 * @date 2025/5/21 16:06
 */
@Service
@Order(0)
public class ActivityAuditPassRecordHandler extends ActivityAuditPassAbstractHandler {
  @Autowired List<ActivityAuditChangeHandler> activityAuditChangeHandlers;
  @Autowired private NasActivityAuditRecordDAO activityAuditRecordDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();

    // 预查询活动操作审核记录
    QueryWrapper<ActivityAuditRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityAuditRecordPO::getActivityId, param.getActivityId())
        .eq(ActivityAuditRecordPO::getAuditStatus, AuditStatusEnum.DEFAULT.getValue())
        .eq(ActivityAuditRecordPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityAuditRecordPO activityAuditRecordPO = activityAuditRecordDAO.selectOne(queryWrapper);

    // 活动操作记录审核通过
    if (!ObjectUtils.isEmpty(activityAuditRecordPO)) {
      ActivityAuditChangeHandler activityAuditChangeHandler =
          activityAuditChangeHandlers.stream()
              .filter(
                  activityAuditChangeHandler1 ->
                      activityAuditChangeHandler1.match(activityAuditRecordPO.getOperateType()))
              .findFirst()
              .orElse(null);
      if (!ObjectUtils.isEmpty(activityAuditChangeHandler)) {
        ActivityChangeContext activityChangeContext = new ActivityChangeContext();
        activityChangeContext.setActivityInfoAuditParam(param);
        activityAuditChangeHandler.handle(activityChangeContext);
      }
    }
    return null;
  }
}
