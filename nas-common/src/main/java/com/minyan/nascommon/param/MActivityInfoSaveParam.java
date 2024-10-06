package com.minyan.nascommon.param;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @decription 活动保存参数
 * @author minyan.he
 * @date 2024/9/22 10:46
 */
@Data
public class MActivityInfoSaveParam {
  private Integer activityId;

  @NotNull(message = "活动名称不能为空")
  private String activityName;

  private Date beginTime;

  private Date endTime;

  private List<MActivityRewardSaveParam> activityRewardSaveInfos;
  private List<MActivityModuleSaveParam> moduleSaveInfos;
  private List<MActivityChannelSaveParam> channelSaveInfos;
}
