package com.minyan.nascommon.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @decription 查询活动信息详情出参
 * @author minyan.he
 * @date 2024/9/19 16:35
 */
@Data
public class MActivityInfoDetailVO {
  private Integer activityId;
  private String activityName;
  private Date beginTime;
  private Date endTime;
  private Integer status;
  private List<MActivityRewardDetailVO> mActivityRewardDetailVOS;
  private List<MModuleInfoDetailVO> mModuleInfoDetailVOList;
  private List<MActivityChannelDetailVO> mActivityChannelDetailVOList;
}
