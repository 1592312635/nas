package com.minyan.nascommon.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @decription m端查询活动信息入参
 * @author minyan.he
 * @date 2024/9/18 19:20
 */
@Data
public class MActivityInfoQueryParam {
    private Integer activityId;
    private String actvityName;
    private Integer status;
    @NotNull(message = "页码不能为空")
    private Integer pageNum;
    @NotNull(message = "页面带大小不能为空")
    private Integer pageSize;
}
