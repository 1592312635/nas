package com.minyan.nascapi.handler.join;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.JoinTypeEnum;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.po.JoinRecordPO;
import com.minyan.nasdao.NasJoinRecordDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @decription 活动+用户维度去重参与记录
 * @author minyan.he
 * @date 2025/3/10 17:26
 */
@Service
public class JoinRecordUserActivityHandler implements JoinTypeRecordHandler {
  private static final Logger logger = LoggerFactory.getLogger(JoinRecordUserActivityHandler.class);
  @Autowired private NasJoinRecordDAO joinRecordDAO;

  @Override
  public Boolean match(Integer joinType) {
    return JoinTypeEnum.USER_ACTIVITY.getValue().equals(joinType);
  }

  @Override
  public void handle(CJoinRecordParam param) {
    // 去重校验
    QueryWrapper<JoinRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(JoinRecordPO::getActivityId, param.getActivityId())
        .eq(JoinRecordPO::getUserId, param.getUserId())
        .eq(JoinRecordPO::getJoinType, param.getJoinType());
    if (joinRecordDAO.selectCount(queryWrapper) > 0) {
      logger.info(
          "[JoinRecordUserActivityHandler][handle]参与记录已存在，请求参数：{}", JSONObject.toJSONString(param));
      return;
    }

    // 生成参与记录
    JoinRecordPO joinRecordPO = buildJoinRecordPO(param);
    joinRecordDAO.insert(joinRecordPO);
  }

  /**
   * 构建参与记录
   *
   * @param param
   * @return
   */
  JoinRecordPO buildJoinRecordPO(CJoinRecordParam param) {
    JoinRecordPO joinRecordPO = new JoinRecordPO();
    joinRecordPO.setActivityId(param.getActivityId());
    joinRecordPO.setModuleId(param.getModuleId());
    joinRecordPO.setJoinType(param.getJoinType());
    joinRecordPO.setJoinInfo(param.getJoinInfo());
    joinRecordPO.setUserId(param.getUserId());
    return joinRecordPO;
  }
}
