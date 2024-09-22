package com.minyan.nascommon.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @decription 活动保存参数
 * @author minyan.he
 * @date 2024/9/22 10:46
 */
@Data
public class MActivityInfoSaveParam {
    @NotNull(message = "活动id不能为空")
    private Integer activityId;
    @NotNull(message = "活动名称不能为空")
    private String activityName;
}
