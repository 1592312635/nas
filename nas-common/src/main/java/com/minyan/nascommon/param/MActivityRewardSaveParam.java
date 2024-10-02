package com.minyan.nascommon.param;

import lombok.Data;

/**
 * @decription
 * @author minyan.he
 * @date 2024/9/26 17:58
 */
@Data
public class MActivityRewardSaveParam {
    private Long rewardId;
    private Integer rewardType;
    private String rewardName;
    private String batchCode;
    private String imageUrl;
}
