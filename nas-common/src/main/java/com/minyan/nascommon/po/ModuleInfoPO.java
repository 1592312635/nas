package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 
 */
@Data
@TableName("nas_module_info")
public class ModuleInfoPO implements Serializable {
    private Long id;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 模块id
     */
    private Integer moduleId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模块开始时间
     */
    private Date beginTime;

    /**
     * 模块截止时间
     */
    private Date endTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer delTag;

    private static final long serialVersionUID = 1L;
}