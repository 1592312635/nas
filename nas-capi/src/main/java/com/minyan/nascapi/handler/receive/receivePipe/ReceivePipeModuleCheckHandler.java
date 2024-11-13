package com.minyan.nascapi.handler.receive.receivePipe;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceivePipeContext;
import com.minyan.nascommon.exception.CustomException;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ModuleInfoPO;
import com.minyan.nasdao.NasModuleInfoDAO;
import java.util.Date;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 活动模块验证器
 * @author minyan.he
 * @date 2024/11/13 21:36
 */
@Order(10)
@Service
public class ReceivePipeModuleCheckHandler extends ReceivePipeAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceivePipeModuleCheckHandler.class);

  @Autowired private NasModuleInfoDAO moduleInfoDAO;

  @SneakyThrows
  @Override
  public Boolean handle(ReceivePipeContext context) {
    CReceiveSendParam param = context.getParam();

    QueryWrapper<ModuleInfoPO> moduleInfoPOQueryWrapper = new QueryWrapper<>();
    moduleInfoPOQueryWrapper
        .lambda()
        .eq(ModuleInfoPO::getModuleId, param.getModuleId())
        .eq(ModuleInfoPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    ModuleInfoPO moduleInfoPO = moduleInfoDAO.selectOne(moduleInfoPOQueryWrapper);

    if (ObjectUtils.isEmpty(moduleInfoPO)) {
      logger.info(
          "[ReceivePipeModuleCheckHandler][handle]模块信息不存在，请求参数：{}", JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.MODULE_NOT_EXIST);
    }

    Date now = new Date();
    if (moduleInfoPO.getBeginTime().after(now)) {
      logger.info(
          "[ReceivePipeModuleCheckHandler][handle]模块未开始，请求参数：{}", JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.MODULE_NOT_START);
    }
    if (moduleInfoPO.getEndTime().before(now)) {
      logger.info(
          "[ReceivePipeModuleCheckHandler][handle]模块已结束，请求参数：{}", JSONObject.toJSONString(param));
      throw new CustomException(CodeEnum.MODULE_END);
    }

    context.setModuleInfoPO(moduleInfoPO);
    return null;
  }
}
