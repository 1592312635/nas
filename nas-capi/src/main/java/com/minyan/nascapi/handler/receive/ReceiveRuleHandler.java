package com.minyan.nascapi.handler.receive;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.minyan.nascapi.handler.receive.receiveRule.ReceiveRuleCheckHandler;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceiveSendContext;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.ReceiveLimitPO;
import com.minyan.nascommon.po.ReceiveRulePO;
import com.minyan.nasdao.NasReceiveLimitDAO;
import com.minyan.nasdao.NasReceiveRuleDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @decription 领取门槛校验handler
 * @author minyan.he
 * @date 2024/10/29 22:51
 */
@Order(20)
@Service
public class ReceiveRuleHandler extends ReceiveAbstractHandler {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveRuleHandler.class);
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;
  @Autowired private List<ReceiveRuleCheckHandler> receiveRuleCheckHandlers;

  @Override
  public Boolean handle(ReceiveSendContext context) {
    CReceiveSendParam param = context.getParam();
    // 获取领取规则信息
    List<ReceiveRulePO> receiveRuleLists = getReceiveRuleLists(param);
    context.setReceiveRulePOList(receiveRuleLists);
    // 获取待验证领取门槛信息
    List<ReceiveLimitPO> receiveLimitList = getReceiveLimitList(param);
    context.setReceiveLimitList(receiveLimitList);

    // 开始处理领取门槛验证
    for (ReceiveRuleCheckHandler handler : receiveRuleCheckHandlers) {
      Boolean result = handler.handle(context);
      if (!result) {
        logger.info(
            "[ReceiveRuleHandler][handle]领取校验门槛不通过，请求参数：{}，门槛：{}，校验结果：{}",
            JSONObject.toJSONString(param),
            handler.getClass().getName(),
            result);
        return result;
      }
    }
    return true;
  }

  /**
   * 通过请求参数筛选当前需要的receiveRule
   *
   * @param param
   * @return
   */
  List<ReceiveRulePO> getReceiveRuleLists(CReceiveSendParam param) {
    List<ReceiveRulePO> receiveRulePOS = Lists.newArrayList();
    QueryWrapper<ReceiveRulePO> receiveRulePOQueryWrapper = new QueryWrapper<>();
    receiveRulePOQueryWrapper
        .lambda()
        .eq(ReceiveRulePO::getActivityId, param.getActivityId())
        .eq(ReceiveRulePO::getModuleId, param.getModuleId())
        .eq(ReceiveRulePO::getEventId, param.getEventId())
        .eq(ReceiveRulePO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveRulePOS = receiveRuleDAO.selectList(receiveRulePOQueryWrapper);
    return receiveRulePOS;
  }

  /**
   * 通过请求参数筛选当前需要校验的receiveLimit
   *
   * @param param
   * @return
   */
  List<ReceiveLimitPO> getReceiveLimitList(CReceiveSendParam param) {
    List<ReceiveLimitPO> receiveLimitPOS = Lists.newArrayList();
    QueryWrapper<ReceiveLimitPO> receiveLimitPOQueryWrapper = new QueryWrapper<>();
    receiveLimitPOQueryWrapper
        .lambda()
        .eq(ReceiveLimitPO::getActivityId, param.getActivityId())
        .eq(ReceiveLimitPO::getEventId, param.getEventId())
        .eq(ReceiveLimitPO::getDelTag, DelTagEnum.NOT_DEL.getValue());
    receiveLimitPOS = receiveLimitDAO.selectList(receiveLimitPOQueryWrapper);
    return receiveLimitPOS;
  }
}
