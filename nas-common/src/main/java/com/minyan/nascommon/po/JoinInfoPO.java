package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author 
 * 活动参与信息表
 */
@Data
@TableName("nas_join_info")
public class JoinInfoPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户注册id
     */
    private String userId;

    /**
     * 活动id
     */
    private Integer activityId;

    /**
     * 模块id
     */
    private Integer moduleId;

    /**
     * 事件id
     */
    private Integer eventId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 删除标识(1删除0未删除)
     */
    private Integer delTag;

    private static final long serialVersionUID = 1L;
}