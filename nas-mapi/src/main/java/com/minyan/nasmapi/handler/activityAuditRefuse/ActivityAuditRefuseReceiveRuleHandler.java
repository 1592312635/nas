package com.minyan.nasmapi.handler.activityAuditRefuse;

import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditRefuseContext;
import com.minyan.nasdao.NasReceiveLimitDAO;
import com.minyan.nasdao.NasReceiveLimitTempDAO;
import com.minyan.nasdao.NasReceiveRuleDAO;
import com.minyan.nasdao.NasReceiveRuleTempDAO;
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

    return null;
  }
}
