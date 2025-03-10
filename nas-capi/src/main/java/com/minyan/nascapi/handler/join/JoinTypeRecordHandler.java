package com.minyan.nascapi.handler.join;

import com.minyan.nascommon.Enum.CodeEnum;
import com.minyan.nascommon.param.CJoinRecordParam;

/**
 * @decription 参与记录参与类型匹配处理handler
 * @author minyan.he
 * @date 2025/3/10 17:05
 */
public interface JoinTypeRecordHandler {
  Boolean match(Integer joinType);

  void handle(CJoinRecordParam param);
}
