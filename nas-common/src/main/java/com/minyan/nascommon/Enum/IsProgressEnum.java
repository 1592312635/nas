package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 查询活动是否进行中枚举
 * @author minyan.he
 * @date 2024/9/21 9:16
 */
@AllArgsConstructor
@Getter
public enum IsProgressEnum {
    IS_PROGRESS(1, "进行中"),
    NOT_PROGRESS(2, "未开始"),
    END(3, "已结束");
    private final Integer value;
    private final String desc;
}
