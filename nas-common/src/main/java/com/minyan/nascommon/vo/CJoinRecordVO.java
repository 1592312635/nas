package com.minyan.nascommon.vo;

import com.minyan.nascommon.po.JoinRecordPO;
import java.util.Date;
import lombok.Data;

/**
 * @decription 参与记录出参数
 * @author minyan.he
 * @date 2025/3/11 17:22
 */
@Data
public class CJoinRecordVO {
  /** 活动id */
  private Integer activityId;

  /** 用户注册id */
  private String userId;

  /** 参与类型(JoinTypeEnum) */
  private Integer joinType;

  /** 额外参与信息 */
  private String joinInfo;

  /** 创建时间 */
  private Date createTime;

  /**
   * 构建VO
   *
   * @param joinRecordPO
   * @return
   */
  public static CJoinRecordVO convertToVO(JoinRecordPO joinRecordPO) {
    CJoinRecordVO cJoinRecordVO = new CJoinRecordVO();
    cJoinRecordVO.setActivityId(joinRecordPO.getActivityId());
    cJoinRecordVO.setUserId(joinRecordPO.getUserId());
    cJoinRecordVO.setJoinType(joinRecordPO.getJoinType());
    cJoinRecordVO.setJoinInfo(joinRecordPO.getJoinInfo());
    cJoinRecordVO.setCreateTime(joinRecordPO.getCreateTime());
    return cJoinRecordVO;
  }
}
