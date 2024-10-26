package com.minyan.nasmapi.handler.activityAuditPass;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveLimitTempPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import com.minyan.nascommon.po.ReceiveRuleTempPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
import com.minyan.nasdao.NasReceiveLimitDAO;
import com.minyan.nasdao.NasReceiveLimitTempDAO;
import com.minyan.nasdao.NasReceiveRuleDAO;
import com.minyan.nasdao.NasReceiveRuleTempDAO;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @decription 审核通过领取规则处理handler
 * @author minyan.he
 * @date 2024/10/20 11:02
 */
@Order(30)
@Service
public class ActivityAuditPassReceiveRuleHandler extends ActivityAuditPassAbstractHandler {
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditPassRewardRuleHandler.class);
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;

  @Override
  public ApiResult handle(ActivityAuditPassContext context) {
    MActivityInfoAuditParam param = context.getParam();
    // 查询相关表数据
    QueryWrapper<ReceiveRuleTempPO> receiveRuleTempPOQueryWrapper = new QueryWrapper<>();
    receiveRuleTempPOQueryWrapper
        .lambda()
        .eq(ReceiveRuleTempPO::getActivityId, param.getActivityId())
        .eq(ReceiveRuleTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRuleTempPO> receiveRuleTempPOS =
        receiveRuleTempDAO.selectList(receiveRuleTempPOQueryWrapper);
    QueryWrapper<ReceiveRulePO> receiveRulePOQueryWrapper = new QueryWrapper<>();
    receiveRulePOQueryWrapper
        .lambda()
        .eq(ReceiveRulePO::getActivityId, param.getActivityId())
        .eq(ReceiveRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveRulePO> receiveRulePOS = receiveRuleDAO.selectList(receiveRulePOQueryWrapper);
    QueryWrapper<ReceiveLimitTempPO> receiveLimitTempPOQueryWrapper = new QueryWrapper<>();
    receiveLimitTempPOQueryWrapper
        .lambda()
        .eq(ReceiveLimitTempPO::getActivityId, param.getActivityId())
        .eq(ReceiveLimitTempPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    List<ReceiveLimitTempPO> receiveLimitTempPOS =
        receiveLimitTempDAO.selectList(receiveLimitTempPOQueryWrapper);

    // 删除多余的receiveRule
    List<Long> deleteReceiveRuleIds =
        receiveRuleTempPOS.stream()
            .filter(receiveRuleTempPO -> ObjectUtils.isEmpty(receiveRuleTempPO.getReceiveRuleId()))
            .map(ReceiveRuleTempPO::getReceiveRuleId)
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(deleteReceiveRuleIds)) {
      UpdateWrapper<ReceiveRulePO> receiveRulePODeleteWrapper = new UpdateWrapper<>();
      receiveRulePODeleteWrapper
          .lambda()
          .set(ReceiveRulePO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveRulePO::getId, deleteReceiveRuleIds);
      receiveRuleDAO.update(null, receiveRulePODeleteWrapper);
      UpdateWrapper<ReceiveLimitPO> receiveLimitPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitPODeleteWrapper
          .lambda()
          .set(ReceiveLimitPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveLimitPO::getReceiveRuleId, deleteReceiveRuleIds);
      receiveLimitDAO.update(null, receiveLimitPODeleteWrapper);
    }

    // 临时表同步主表
    List<ReceiveRuleTempPO> updateReceiveRuleTemps =
        receiveRuleTempPOS.stream()
            .filter(receiveRuleTempPO -> !ObjectUtils.isEmpty(receiveRuleTempPO.getReceiveRuleId()))
            .collect(Collectors.toList());
    List<ReceiveRuleTempPO> addReceiveRuleTemps =
        receiveRuleTempPOS.stream()
            .filter(receiveRuleTempPO -> ObjectUtils.isEmpty(receiveRuleTempPO.getReceiveRuleId()))
            .collect(Collectors.toList());

    // 临时表同步主表-更新主表已有领取规则
    if (!CollectionUtils.isEmpty(updateReceiveRuleTemps)) {
      List<Long> receiveRuleIds =
          updateReceiveRuleTemps.stream()
              .map(ReceiveRuleTempPO::getReceiveRuleId)
              .collect(Collectors.toList());
      // 删除主表receiveLimit数据
      UpdateWrapper<ReceiveLimitPO> receiveLimitPODeleteWrapper = new UpdateWrapper<>();
      receiveLimitPODeleteWrapper
          .lambda()
          .set(ReceiveLimitPO::getDelTag, DelTagEnum.DEL.getValue())
          .in(ReceiveLimitPO::getReceiveRuleId, receiveRuleIds);

      // 同步临时表receiveLimit到主表
      for (ReceiveRuleTempPO updateReceiveRuleTemp : updateReceiveRuleTemps) {
        List<ReceiveLimitTempPO> tempReceiveLimits =
            receiveLimitTempPOS.stream()
                .filter(
                    receiveLimitTempPO ->
                        updateReceiveRuleTemp.getId().equals(receiveLimitTempPO.getReceiveRuleId()))
                .collect(Collectors.toList());
        for (ReceiveLimitTempPO tempReceiveLimit : tempReceiveLimits) {
          receiveLimitDAO.insert(
              buildReceiveLimitByRuleAndLimit(
                  updateReceiveRuleTemp.getReceiveRuleId(), tempReceiveLimit));
        }
      }
    }

    // 临时表同步主表-新增主表没有的新领取规则
    if (!CollectionUtils.isEmpty(addReceiveRuleTemps)) {
      for (ReceiveRuleTempPO addReceiveRuleTemp : addReceiveRuleTemps) {
        ReceiveRulePO addReceiveRulePO =
            ReceiveRulePO.tempConvertToReceiveRulePO(addReceiveRuleTemp);
        receiveRuleDAO.insert(addReceiveRulePO);
        List<ReceiveLimitTempPO> tempReceiveLimits =
            receiveLimitTempPOS.stream()
                .filter(
                    receiveLimitTempPO ->
                        addReceiveRuleTemp.getId().equals(receiveLimitTempPO.getReceiveRuleId()))
                .collect(Collectors.toList());
        for (ReceiveLimitTempPO tempReceiveLimit : tempReceiveLimits) {
          receiveLimitDAO.insert(
              buildReceiveLimitByRuleAndLimit(addReceiveRulePO.getId(), tempReceiveLimit));
        }
      }
    }
    return null;
  }

  /**
   * 构建主表领取规则门槛
   *
   * @param receiveRuleId
   * @param receiveLimitTempPO
   * @return
   */
  ReceiveLimitPO buildReceiveLimitByRuleAndLimit(
      Long receiveRuleId, ReceiveLimitTempPO receiveLimitTempPO) {
    ReceiveLimitPO receiveLimitPO = new ReceiveLimitPO();
    receiveLimitPO.setActivityId(receiveLimitTempPO.getActivityId());
    receiveLimitPO.setModuleId(receiveLimitTempPO.getModuleId());
    receiveLimitPO.setEventId(receiveLimitTempPO.getEventId());
    receiveLimitPO.setReceiveRuleId(receiveRuleId);
    receiveLimitPO.setLimitKey(receiveLimitTempPO.getLimitKey());
    receiveLimitPO.setLimitJson(receiveLimitTempPO.getLimitJson());
    receiveLimitPO.setLimitType(receiveLimitTempPO.getLimitType());
    return receiveLimitPO;
  }
}
