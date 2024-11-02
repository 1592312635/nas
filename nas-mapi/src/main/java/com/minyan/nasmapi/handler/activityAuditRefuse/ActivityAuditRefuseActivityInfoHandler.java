package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.AuditStatusEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ActivityInfoPO;
import com.minyan.nascommon.po.ActivityInfoTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasActivityInfoDAO;
import com.minyan.nasdao.NasActivityInfoTempDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动审核不通过活动基本信息处理
 * @author minyan.he
 * @date 2024/10/12 10:40
 */
@Order(10)
@Service
public class ActivityAuditRefuseActivityInfoHandler extends ActivityAuditRefuseAbstractHandler {
  private static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseActivityInfoHandler.class);

  @Autowired private NasActivityInfoTempDAO activityInfoTempDAO;
  @Autowired private NasActivityInfoDAO activityInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 删除临时表数据
    UpdateWrapper<ActivityInfoTempPO> activityInfoTempPOUpdateWrapper = new UpdateWrapper<>();
    activityInfoTempPOUpdateWrapper
        .lambda()
        .set(ActivityInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ActivityInfoTempPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    // 主表数据填充
    QueryWrapper<ActivityInfoPO> activityInfoPOQueryWrapper = new QueryWrapper<>();
    activityInfoPOQueryWrapper
        .lambda()
        .eq(ActivityInfoPO::getActivityId, param.getActivityId())
        .eq(ActivityInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ActivityInfoPO activityInfoPO = activityInfoDAO.selectOne(activityInfoPOQueryWrapper);
    if (!ObjectUtils.isEmpty(activityInfoPO)) {
      ActivityInfoTempPO activityInfoTempPO = activityInfoPOToActivityInfoTempPO(activityInfoPO);
      activityInfoTempDAO.insert(activityInfoTempPO);
    }
    return null;
  }

  /**
   * 审核失败活动主表信息转化临时表信息
   *
   * @param activityInfoPO
   * @return
   */
  ActivityInfoTempPO activityInfoPOToActivityInfoTempPO(ActivityInfoPO activityInfoPO) {
    ActivityInfoTempPO activityInfoTempPO = new ActivityInfoTempPO();
    activityInfoTempPO.setActivityId(activityInfoPO.getActivityId());
    activityInfoTempPO.setActivityName(activityInfoPO.getActivityName());
    activityInfoTempPO.setStatus(activityInfoPO.getStatus());
    activityInfoTempPO.setAuditStatus(AuditStatusEnum.REFUSE.getValue());
    activityInfoTempPO.setBeginTime(activityInfoPO.getBeginTime());
    activityInfoTempPO.setEndTime(activityInfoPO.getEndTime());
    return activityInfoTempPO;
  }
}
