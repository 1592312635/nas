package com.minyan.nascapi.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascapi.handler.receive.ReceiveAbstractHandler;
import com.minyan.nascapi.service.ReceiveService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.Enum.DelTagEnum;
import com.minyan.nascommon.dto.context.ReceiveSendContext;
import com.minyan.nascommon.param.CReceiveQueryParam;
import com.minyan.nascommon.param.CReceiveSendParam;
import com.minyan.nascommon.po.SendRecordPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.CReceiveInfoVO;
import com.minyan.nasdao.NasSendRecordDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2024/10/30 21:24
 */
@Service
public class ReceiveServiceImpl implements ReceiveService {
  public static final Logger logger = LoggerFactory.getLogger(ReceiveServiceImpl.class);
  @Autowired private List<ReceiveAbstractHandler> receiveHandlers;
  @Autowired private NasSendRecordDAO sendRecordDAO;

  @Override
  public ApiResult<Boolean> send(CReceiveSendParam param) {
    ReceiveSendContext receiveSendContext = new ReceiveSendContext();
    receiveSendContext.setParam(param);
    List<ReceiveAbstractHandler> fallBackHandlers = Lists.newArrayList();
    Boolean result = true;
    try {
      for (ReceiveAbstractHandler receiveHandler : receiveHandlers) {
        fallBackHandlers.add(receiveHandler);
        result = receiveHandler.handle(receiveSendContext);
        if (!result) {
          logger.info(
              "[ReceiveServiceImpl][send]奖励发放内部异常，无需回滚，请求参数：{}", JSONObject.toJSONString(param));
          return ApiResult.build(
              CodeEnum.RECEIVE_SEND_INNER_EXCEPTION.getCode(),
              CodeEnum.RECEIVE_SEND_INNER_EXCEPTION.getMessage(),
              result);
        }
      }
    } catch (Exception e) {
      logger.info(
          "[ReceiveServiceImpl][send]奖励发放异常，开始回滚，请求参数：{}", JSONObject.toJSONString(param), e);
      for (ReceiveAbstractHandler receiveFallBackHandler : fallBackHandlers) {
        receiveFallBackHandler.fallBack(receiveSendContext);
      }
    }
    return ApiResult.buildSuccess(result);
  }

  @Override
  public ApiResult<List<CReceiveInfoVO>> query(CReceiveQueryParam param) {
    List<CReceiveInfoVO> cReceiveInfoVOS = Lists.newArrayList();

    QueryWrapper<SendRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(SendRecordPO::getActivityId, param.getActivityId())
        .eq(
            !ObjectUtils.isEmpty(param.getModuleId()),
            SendRecordPO::getModuleId,
            param.getModuleId())
        .eq(SendRecordPO::getUserId, param.getUserId())
        .eq(
            !ObjectUtils.isEmpty(param.getRewardType()),
            SendRecordPO::getRewardType,
            param.getRewardType())
        .eq(SendRecordPO::getDelTag, DelTagEnum.NOT_DEL.getValue())
        .orderByDesc(SendRecordPO::getCreateTime);
    Page<SendRecordPO> page = new Page<>(param.getPageNum(), param.getPageSize());
    Page<SendRecordPO> sendRecordPOPage = sendRecordDAO.selectPage(page, queryWrapper);
    logger.info(
        "[ReceiveServiceImpl][query]查询领取记录结束，请求参数：{}，返回结果：{}",
        JSONObject.toJSONString(param),
        JSONObject.toJSONString(sendRecordPOPage));
    List<SendRecordPO> sendRecordPOS = sendRecordPOPage.getRecords();
    if (!ObjectUtils.isEmpty(sendRecordPOS)) {
      cReceiveInfoVOS =
          sendRecordPOS.stream()
              .map(CReceiveInfoVO::convertToVO)
              .collect(java.util.stream.Collectors.toList());
    }
    return ApiResult.buildSuccess(cReceiveInfoVOS);
  }
}
