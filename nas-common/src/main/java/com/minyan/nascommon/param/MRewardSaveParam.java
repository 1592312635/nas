package com.minyan.nascommon.param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @decription 奖品生成请求参数
 * @author minyan.he
 * @date 2025/3/29 17:51
 */
@Data
public class MRewardSaveParam {
  @NotNull(message = "奖品类型不能为空")
  private Integer rewardType;

  @NotBlank(message = "奖品名称不能为空")
  private String rewardName;

  private String batchCode;
  private String imageUrl;
}
