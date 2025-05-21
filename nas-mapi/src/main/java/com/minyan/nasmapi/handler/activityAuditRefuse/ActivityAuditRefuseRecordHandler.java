package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
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
 * @decription 活动操作审核拒绝handler
 * @author minyan.he
 * @date 2025/5/21 16:07
 */
@Service
@Order(0)
public class ActivityAuditRefuseRecordHandler extends ActivityAuditRefuseAbstractHandler {
  @Autowired List<ActivityAuditChangeHandler> activityAuditChangeHandlers;
  @Autowired private NasActivityAuditRecordDAO activityAuditRecordDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();

    // 预查询活动操作审核记录
    QueryWrapper<ActivityAuditRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(ActivityAuditRecordPO::getActivityId, param.getActivityId())
        .eq(ActivityAuditRecordPO::getAuditStatus, AuditStatusEnum.DEFAULT.getValue())
        .eq(ActivityAuditRecordPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityAuditRecordPO activityAuditRecordPO = activityAuditRecordDAO.selectOne(queryWrapper);

    // 活动操作记录审核拒绝
    if (!ObjectUtils.isEmpty(activityAuditRecordPO)) {
      UpdateWrapper<ActivityAuditRecordPO> activityAuditRecordPOUpdateWrapper =
          new UpdateWrapper<>();
      activityAuditRecordPOUpdateWrapper
          .lambda()
          .set(ActivityAuditRecordPO::getAuditStatus, AuditStatusEnum.REFUSE.getValue())
          .eq(ActivityAuditRecordPO::getId, activityAuditRecordPO.getId());
    }
    return null;
  }
}
