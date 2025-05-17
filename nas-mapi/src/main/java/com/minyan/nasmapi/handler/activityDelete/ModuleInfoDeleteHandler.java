package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ModuleInfoPO;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nasdao.NasModuleInfoDAO;
import com.minyan.nasdao.NasModuleInfoTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 活动模块删除handler
 * @author minyan.he
 * @date 2025/5/17 17:50
 */
@Service
@Order(20)
public class ModuleInfoDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasModuleInfoDAO moduleInfoDAO;
  @Autowired private NasModuleInfoTempDAO moduleInfoTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ModuleInfoPO> moduleInfoPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ModuleInfoTempPO> moduleInfoTempPOUpdateWrapper = new UpdateWrapper<>();

    moduleInfoPOUpdateWrapper
        .lambda()
        .set(ModuleInfoPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ModuleInfoPO::getActivityId, activityId)
        .eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    moduleInfoTempPOUpdateWrapper
        .lambda()
        .set(ModuleInfoTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ModuleInfoTempPO::getActivityId, activityId)
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    moduleInfoDAO.update(null, moduleInfoPOUpdateWrapper);
    moduleInfoTempDAO.update(null, moduleInfoTempPOUpdateWrapper);
  }
}
