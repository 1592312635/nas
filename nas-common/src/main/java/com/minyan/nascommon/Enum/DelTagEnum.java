package com.minyan.nascommon.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @decription 删除标识枚举
 * @author minyan.he
 * @date 2024/9/19 18:01
 */
@AllArgsConstructor
@Getter
public enum DelTagEnum {
    DEL(1, "删除"),
    NOT_DEL(0, "未删除");

    private final Integer value;
    private final String desc;

}
