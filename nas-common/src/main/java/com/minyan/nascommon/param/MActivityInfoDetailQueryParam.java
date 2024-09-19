package com.minyan.nascommon.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @decription
 * @author minyan.he
 * @date 2024/9/19 17:16
 */
@Data
public class MActivityInfoDetailQueryParam {
    @NotNull(message = "活动id不能为空")
    private Integer activityId;
}
