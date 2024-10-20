package com.minyan.nasmapi.handler.activityAuditPass;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ModuleInfoPO;
import com.minyan.nascommon.po.ModuleInfoTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
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
 * @decription 活动审核通过模块信息处理handler
 * @author minyan.he
 * @date 2024/10/12 11:14
 */
@Order(30)
@Service
public class ActivityAuditPassModuleInfoHandler extends ActivityAuditPassAbstractHandler {
  Logger logger = LoggerFactory.getLogger(ActivityAuditPassModuleInfoHandler.class);
  @Autowired private NasModuleInfoTempDAO moduleInfoTempDAO;
  @Autowired private NasModuleInfoDAO moduleInfoDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询临时表模块信息
    QueryWrapper<ModuleInfoTempPO> moduleInfoTempPOQueryWrapper = new QueryWrapper<>();
    moduleInfoTempPOQueryWrapper
        .lambda()
        .eq(ModuleInfoTempPO::getActivityId, param.getActivityId())
        .eq(ModuleInfoTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoTempPO> moduleInfoTempPOS =
        moduleInfoTempDAO.selectList(moduleInfoTempPOQueryWrapper);
    if (CollectionUtils.isEmpty(moduleInfoTempPOS)) {
      logger.info(
          "[ActivityAuditPassModuleInfoHandler][handle]活动审核通过处理模块信息时，待审核模块信息不存在，请求参数：{}",
          JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.AUDIT_MODULE_NOT_EXIST);
    }

    // 删除主表模块信息
    QueryWrapper<ModuleInfoPO> moduleInfoPOQueryWrapper = new QueryWrapper<>();
    moduleInfoPOQueryWrapper
        .lambda()
        .eq(ModuleInfoPO::getActivityId, param.getActivityId())
        .eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ModuleInfoPO> moduleInfoPOS = moduleInfoDAO.selectList(moduleInfoPOQueryWrapper);
    if (!CollectionUtils.isEmpty(moduleInfoPOS)) {
      UpdateWrapper<ModuleInfoPO> moduleInfoPODeleteWrapper = new UpdateWrapper<>();
      moduleInfoPODeleteWrapper
          .lambda()
          .set(ModuleInfoPO::getDelTag, DelTagEnum.DEL.getValue())
          .eq(ModuleInfoPO::getActivityId, param.getActivityId())
          .eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
      moduleInfoDAO.update(null, moduleInfoPODeleteWrapper);
    }
    return null;
  }
}
