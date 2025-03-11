package com.minyan.nascapi.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.minyan.nascapi.handler.join.JoinTypeRecordHandler;
import com.minyan.nascapi.service.JoinRecordService;
import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.param.CJoinQueryParam;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.po.JoinRecordPO;
import com.minyan.nascommon.vo.ApiResult;
import com.minyan.nascommon.vo.CJoinRecordVO;
import com.minyan.nasdao.NasJoinRecordDAO;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @decription
 * @author minyan.he
 * @date 2025/3/10 16:27
 */
@Service
public class JoinRecordServiceImpl implements JoinRecordService {
  private static final Logger logger = LoggerFactory.getLogger(JoinRecordServiceImpl.class);
  @Autowired private List<JoinTypeRecordHandler> joinTypeRecordHandlerList;
  @Autowired private NasJoinRecordDAO joinRecordDAO;

  @Override
  public ApiResult record(CJoinRecordParam param) {
    joinTypeRecordHandlerList.forEach(
        joinTypeRecordHandler -> {
          if (joinTypeRecordHandler.match(param.getJoinType())) {
            joinTypeRecordHandler.handle(param);
          }
        });
    return ApiResult.build(CodeEnum.SUCCESS);
  }

  @Override
  public ApiResult query(CJoinQueryParam param) {
    List<CJoinRecordVO> cJoinRecordVOS = Lists.newArrayList();

    if (ObjectUtils.isEmpty(param.getActivityId()) && ObjectUtils.isEmpty(param.getUserId())) {
      logger.info(
          "[JoinRecordServiceImpl][query]查询参与记录请求参数异常，请求参数：{}", JSONObject.toJSONString(param));
      return ApiResult.build(CodeEnum.PARAM_ERROR);
    }

    // 开始分页查询
    QueryWrapper<JoinRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(
            !ObjectUtils.isEmpty(param.getActivityId()),
            JoinRecordPO::getActivityId,
            param.getActivityId())
        .eq(
            !ObjectUtils.isEmpty(param.getModuleId()),
            JoinRecordPO::getModuleId,
            param.getModuleId())
        .eq(!ObjectUtils.isEmpty(param.getUserId()), JoinRecordPO::getUserId, param.getUserId())
        .eq(JoinRecordPO::getJoinType, param.getJoinType())
        .orderByDesc(JoinRecordPO::getCreateTime);
    Page<JoinRecordPO> page = new Page<>(param.getPageNum(), param.getPageSize());
    Page<JoinRecordPO> joinRecordPOPage = joinRecordDAO.selectPage(page, queryWrapper);
    logger.info(
        "[JoinRecordServiceImpl][query]查询参与记录结束，请求参数：{}，返回结果：{}",
        JSONObject.toJSONString(param),
        JSONObject.toJSONString(joinRecordPOPage));
    List<JoinRecordPO> joinRecordPOS = joinRecordPOPage.getRecords();
    if (!CollectionUtils.isEmpty(joinRecordPOS)) {
      cJoinRecordVOS =
          joinRecordPOS.stream()
              .map(CJoinRecordVO::convertToVO)
              .collect(java.util.stream.Collectors.toList());
    }
    return ApiResult.buildSuccess(cJoinRecordVOS);
  }
}
