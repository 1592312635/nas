package com.minyan.nascommon.vo;

import lombok.Data;

import java.util.List;

/**
 * @decription 活动事件信息出参
 * @author minyan.he
 * @date 2024/9/19 16:42
 */
@Data
public class MActivityEventDetailVO {
    private Long eventId;
    private Long activityId;
    private Integer moduleId;
    private String eventName;
    private String eventType;
    private List<MReceiveLimitDetailVO> mReceiveLimitDetailVOS;
    private List<MRewardRuleDetailVO> mRewardRuleDetailVOS;
}
