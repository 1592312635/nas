package com.minyan.nascommon.param;

import lombok.Data;

import java.util.List;

/**
 * @decription 活动事件保存参数
 * @author minyan.he
 * @date 2024/10/2 17:43
 */
@Data
public class MActivityEventSaveParam {
    private Long eventId;
    private String eventName;
    private String eventType;
    private List<MActivityReceiveRuleSaveParam> receiveRuleSaveInfos;
    private List<MActivityRewardRuleSaveParam> rewardRuleSaveInfos;
}
