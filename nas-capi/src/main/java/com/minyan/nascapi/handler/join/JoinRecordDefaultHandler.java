package com.minyan.nascapi.handler.join;

import com.minyan.nascommon.Enum.JoinTypeEnum;
import com.minyan.nascommon.param.CJoinRecordParam;
import com.minyan.nascommon.po.JoinRecordPO;
import com.minyan.nasdao.NasJoinRecordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * @decription 参与记录默认处理handler
 * @author minyan.he
 * @date 2025/3/10 17:06
 */
@Service
public class JoinRecordDefaultHandler implements JoinTypeRecordHandler {
  @Autowired private NasJoinRecordDAO joinRecordDAO;

  @Override
  public Boolean match(Integer joinType) {
    return ObjectUtils.isEmpty(joinType) || JoinTypeEnum.DEFAULT.getValue().equals(joinType);
  }

  @Override
  public void handle(CJoinRecordParam param) {
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
