package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveLimitTempPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import com.minyan.nascommon.po.ReceiveRuleTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.dto.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasReceiveLimitDAO;
import com.minyan.nasdao.NasReceiveLimitTempDAO;
import com.minyan.nasdao.NasReceiveRuleDAO;
import com.minyan.nasdao.NasReceiveRuleTempDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 审核拒绝领取规则处理
 * @author minyan.he
 * @date 2024/10/26 9:17
 */
@Order(30)
@Service
public class ActivityAuditRefuseReceiveRuleHandler extends ActivityAuditRefuseAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditRefuseReceiveRuleHandler.class);
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;

  @Override
  public ApiResult handle(ActivityAuditRefuseContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 删除临时表领取规则数据
    UpdateWrapper<ReceiveRuleTempPO> receiveRuleTempPODeleteWrapper = new UpdateWrapper<>();
    receiveRuleTempPODeleteWrapper
        .lambda()
        .set(ReceiveRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveRuleTempPO::getActivityId, param.getActivityId())
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveRuleTempDAO.update(null, receiveRuleTempPODeleteWrapper);
    UpdateWrapper<ReceiveLimitTempPO> receiveLimitTempPODeleteWrapper = new UpdateWrapper<>();
    receiveLimitTempPODeleteWrapper
        .lambda()
        .set(ReceiveLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveLimitTempPO::getActivityId, param.getActivityId())
        .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveLimitTempDAO.update(null, receiveLimitTempPODeleteWrapper);

    // 同步主表数据到临时库
    QueryWrapper<ReceiveRulePO> receiveRulePOQueryWrapper = new QueryWrapper<>();
    receiveRulePOQueryWrapper
        .lambda()
        .eq(ReceiveRulePO::getActivityId, param.getActivityId())
        .eq(ReceiveRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRulePO> receiveRulePOS = receiveRuleDAO.selectList(receiveRulePOQueryWrapper);
    QueryWrapper<ReceiveLimitPO> receiveLimitPOQueryWrapper = new QueryWrapper<>();
    receiveLimitPOQueryWrapper
        .lambda()
        .eq(ReceiveLimitPO::getActivityId, param.getActivityId())
        .eq(ReceiveLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveLimitPO> receiveLimitPOS = receiveLimitDAO.selectList(receiveLimitPOQueryWrapper);
    for (ReceiveRulePO receiveRulePO : receiveRulePOS) {
      ReceiveRuleTempPO receiveRuleTempPO = poToReceiveRuleTempPO(receiveRulePO);
      receiveRuleTempDAO.insert(receiveRuleTempPO);
      // 同步当前领取规则下的领取门槛信息到临时表
      receiveLimitPOS.stream()
          .filter(receiveLimitPO -> receiveRulePO.getId().equals(receiveLimitPO.getReceiveRuleId()))
          .forEach(
              receiveLimitPO -> {
                receiveLimitTempDAO.insert(
                    poToReceiveLimitTempPO(receiveRuleTempPO.getId(), receiveLimitPO));
              });
    }
    return null;
  }

  /**
   * 领取规则主表转化临时表对象
   *
   * @param receiveRulePO
   * @return
   */
  ReceiveRuleTempPO poToReceiveRuleTempPO(ReceiveRulePO receiveRulePO) {
    ReceiveRuleTempPO receiveRuleTempPO = new ReceiveRuleTempPO();
    receiveRuleTempPO.setActivityId(receiveRulePO.getActivityId());
    receiveRuleTempPO.setModuleId(receiveRulePO.getModuleId());
    receiveRuleTempPO.setEventId(receiveRulePO.getEventId());
    receiveRuleTempPO.setReceiveRuleId(receiveRulePO.getId());
    receiveRuleTempPO.setRuleType(receiveRulePO.getRuleType());
    return receiveRuleTempPO;
  }

  /**
   * 领取门槛主表转化临时表对象
   *
   * @param receiveRuleId
   * @param receiveLimitPO
   * @return
   */
  ReceiveLimitTempPO poToReceiveLimitTempPO(Long receiveRuleId, ReceiveLimitPO receiveLimitPO) {
    ReceiveLimitTempPO receiveLimitTempPO = new ReceiveLimitTempPO();
    receiveLimitTempPO.setActivityId(receiveLimitPO.getActivityId());
    receiveLimitTempPO.setModuleId(receiveLimitPO.getModuleId());
    receiveLimitTempPO.setEventId(receiveLimitPO.getEventId());
    receiveLimitTempPO.setReceiveRuleId(receiveRuleId);
    receiveLimitTempPO.setLimitType(receiveLimitPO.getLimitType());
    receiveLimitTempPO.setLimitKey(receiveLimitPO.getLimitKey());
    receiveLimitTempPO.setLimitJson(receiveLimitPO.getLimitJson());
    return receiveLimitTempPO;
  }
}
