package com.minyan.nasmapi.handler.activityAuditPass;

import com.minyan.nascommon.param.MActivityInfoAuditParam;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.context.ActivityAuditPassContext;
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
 * @decription 审核通过领取规则处理handler
 * @author minyan.he
 * @date 2024/10/20 11:02
 */
@Order(30)
@Service
public class ActivityAuditPassReceiveRuleHandler extends ActivityAuditPassAbstractHandler{
  public static final Logger logger =
      LoggerFactory.getLogger(ActivityAuditPassReceiveRuleHandler.class);
  @Autowired private NasReceiveRuleTempDAO receiveRuleTempDAO;
  @Autowired private NasReceiveRuleDAO receiveRuleDAO;
  @Autowired private NasReceiveLimitTempDAO receiveLimitTempDAO;
  @Autowired private NasReceiveLimitDAO receiveLimitDAO;

    @Override
    public ApiResult handle(ActivityAuditPassContext context) {
        MActivityInfoAuditParam param = context.getParam();
        // 查询临时表数据

        // 删除主表数据

        // 临时表数据同步主表

        return null;
    }
}
