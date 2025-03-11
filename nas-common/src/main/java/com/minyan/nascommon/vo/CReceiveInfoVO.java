package com.minyan.nascommon.vo;

import com.minyan.nascommon.po.SendRecordPO;
import java.util.Date;
import lombok.Data;

/**
 * @decription 领取记录出参
 * @author minyan.he
 * @date 2025/3/11 18:20
 */
@Data
public class CReceiveInfoVO {
  /** 奖品id */
  private Long rewardId;

  /** 奖品类型 */
  private Integer rewardType;

  /** 奖品名称 */
  private String rewardName;

  /** 图片链接 */
  private String imageUrl;

  /** 创建时间 */
  private Date createTime;

  /**
   * 构建VO
   *
   * @param sendRecordPO
   * @return
   */
  public static CReceiveInfoVO convertToVO(SendRecordPO sendRecordPO) {
    CReceiveInfoVO vo = new CReceiveInfoVO();
    vo.setRewardId(sendRecordPO.getRewardId());
    vo.setRewardType(sendRecordPO.getRewardType());
    vo.setRewardName(sendRecordPO.getRewardName());
    vo.setImageUrl(sendRecordPO.getImageUrl());
    vo.setCreateTime(sendRecordPO.getCreateTime());
    return vo;
  }
}
