package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ModuleInfoPO;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasModuleInfoDAO;
import com.minyan.nasdao.NasModuleInfoTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @decription 活动审核不通过模块信息处理
 * @author minyan.he
 * @date 2024/10/12 11:17
 */
@Order(30)
@Service
public class ActivityAuditRefuseModuleInfoHandler extends ActivityAuditRefuseAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseModuleInfoHandler.class);
  @Autowired private NasModuleInfoTempDAO moduleInfoTempDAO;
  @Autowired private NasModuleInfoDAO moduleInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 删除临时表模块信息数据
    UpdateWrapper<ModuleInfoTempPO> moduleInfoTempPODeleteWrapper = new UpdateWrapper<>();
    moduleInfoTempPODeleteWrapper
        .lambda()
        .set(ModuleInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ModuleInfoTempPO::getActivityId, param.getActivityId())
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    moduleInfoTempDAO.update(null, moduleInfoTempPODeleteWrapper);

    // 同步主表模块信息数据
    QueryWrapper<ModuleInfoPO> moduleInfoPOQueryWrapper = new QueryWrapper<>();
    moduleInfoPOQueryWrapper
        .lambda()
        .eq(ModuleInfoPO::getActivityId, param.getActivityId())
        .eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoPO> moduleInfoPOS = moduleInfoDAO.selectList(moduleInfoPOQueryWrapper);
    if (!CollectionUtils.isEmpty(moduleInfoPOS)) {
      for (ModuleInfoPO moduleInfoPO : moduleInfoPOS) {
        moduleInfoTempDAO.insert(moduleInfoPOtoModuleInfoTempPO(moduleInfoPO));
      }
    }
    return null;
  }

  /**
   * 模块信息转化模块临时表信息
   *
   * @param moduleInfoPO
   * @return
   */
  ModuleInfoTempPO moduleInfoPOtoModuleInfoTempPO(ModuleInfoPO moduleInfoPO) {
    ModuleInfoTempPO moduleInfoTempPO = new ModuleInfoTempPO();
    moduleInfoTempPO.setActivityId(moduleInfoPO.getActivityId());
    moduleInfoTempPO.setModuleId(moduleInfoPO.getModuleId());
    moduleInfoTempPO.setModuleName(moduleInfoPO.getModuleName());
    moduleInfoTempPO.setBeginTime(moduleInfoPO.getBeginTime());
    moduleInfoTempPO.setEndTime(moduleInfoPO.getEndTime());
    return moduleInfoTempPO;
  }
}
