package com.minyan.nascapi.handler.join;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.minyan.nascommon.Enum.JoinTypeEnum;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.po.JoinRecordPO;
import com.minyan.nascommon.utils.TimeUtil;
import com.minyan.nasdao.NasJoinRecordDAO;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @decription 日维度记录参与记录
 * @author minyan.he
 * @date 2025/3/10 17:49
 */
@Service
public class JoinRecordDayHandler implements JoinTypeRecordHandler {
  private static final Logger logger = LoggerFactory.getLogger(JoinRecordDayHandler.class);
  @Autowired private NasJoinRecordDAO joinRecordDAO;

  @Override
  public Boolean match(Integer joinType) {
    return JoinTypeEnum.DAY.getValue().equals(joinType);
  }

  @Override
  public void handle(CJoinRecordParam param) {
    QueryWrapper<JoinRecordPO> queryWrapper = new QueryWrapper<>();
    queryWrapper
        .lambda()
        .eq(JoinRecordPO::getActivityId, param.getActivityId())
        .eq(JoinRecordPO::getUserId, param.getUserId())
        .eq(JoinRecordPO::getJoinType, param.getJoinType())
        .ge(JoinRecordPO::getCreateTime, TimeUtil.getDayStartTime(new Date()));
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
