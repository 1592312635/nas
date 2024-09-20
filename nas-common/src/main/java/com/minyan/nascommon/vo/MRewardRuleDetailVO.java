package com.minyan.nascommon.vo;

import lombok.Data;

import java.util.List;

/**
 * @decription 活动奖品规则出参
 * @author minyan.he
 * @date 2024/9/19 17:04
 */
@Data
public class MRewardRuleDetailVO {
    private Long rewardRuleId;
    private Long activityId;
    private Long moduleId;
    private Long eventId;
    private Integer rewardType;
    private Long rewardId;
    private List<MRewardLimitDetailVO> rewardLimitDetailVOList;
}
