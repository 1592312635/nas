package com.minyan.nasmapi.handler.activityDelete;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveLimitTempPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import com.minyan.nascommon.po.ReceiveRuleTempPO;
import com.minyan.nasdao.NasReceiveLimitDAO;
import com.minyan.nasdao.NasReceiveLimitTempDAO;
import com.minyan.nasdao.NasReceiveRuleDAO;
import com.minyan.nasdao.NasReceiveRuleTempDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 领取规则删除handler
 * @author minyan.he
 * @date 2025/5/17 18:10
 */
@Service
@Order(50)
public class ReceiveRuleDeleteHandler implements ActivityDeleteHandler {
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;

  @Override
  public void delete(Integer activityId) {
    UpdateWrapper<ReceiveRulePO> receiveRulePOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ReceiveRuleTempPO> receiveRuleTempPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ReceiveLimitPO> receiveLimitPOUpdateWrapper = new UpdateWrapper<>();
    UpdateWrapper<ReceiveLimitTempPO> receiveLimitTempPOUpdateWrapper = new UpdateWrapper<>();

    receiveRuleTempPOUpdateWrapper
        .lambda()
        .set(ReceiveRuleTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveRuleTempPO::getActivityId, activityId)
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveRulePOUpdateWrapper
        .lambda()
        .set(ReceiveRulePO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveRulePO::getActivityId, activityId)
        .eq(ReceiveRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveLimitTempPOUpdateWrapper
        .lambda()
        .set(ReceiveLimitTempPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveLimitTempPO::getActivityId, activityId)
        .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveLimitPOUpdateWrapper
        .lambda()
        .set(ReceiveLimitPO::getDelTag, DelTagEnum.DEL.getValue())
        .eq(ReceiveLimitPO::getActivityId, activityId)
        .eq(ReceiveLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());

    receiveRuleDAO.update(null, receiveRulePOUpdateWrapper);
    receiveRuleTempDAO.update(null, receiveRuleTempPOUpdateWrapper);
    receiveLimitDAO.update(null, receiveLimitPOUpdateWrapper);
    receiveLimitTempDAO.update(null, receiveLimitTempPOUpdateWrapper);
  }
}
