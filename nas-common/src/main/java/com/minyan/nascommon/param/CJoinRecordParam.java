package com.minyan.nascommon.param;

import com.minyan.nascommon.Enum.JoinTypeEnum;
import lombok.Data;

/**
 * @decription 参与记录记录接口
 * @author minyan.he
 * @date 2025/3/10 16:34
 */
@Data
public class CJoinRecordParam {
  private String userId;
  private Integer activityId;
  private Integer moduleId;
  private Integer joinType = JoinTypeEnum.DEFAULT.getValue();
  private String joinInfo;
}
