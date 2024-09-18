package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动参与记录表
 */
@Data
public class JoinFlowPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户注册id
     */
    private String user_id;

    /**
     * 行为
     */
    private Integer flow_type;

    /**
     * 操作对象
     */
    private Integer flow_sub_type;

    /**
     * 预留字段1
     */
    private String column1;

    /**
     * 预留字段2
     */
    private String column2;

    /**
     * 预留字段3
     */
    private String column3;

    /**
     * 预留字段4
     */
    private String column4;

    /**
     * 预留字段5
     */
    private String column5;

    /**
     * 预留字段6
     */
    private String column6;

    /**
     * 预留字段7
     */
    private String column7;

    /**
     * 预留字段8
     */
    private String column8;

    /**
     * 预留字段9
     */
    private String column9;

    /**
     * 预留字段10
     */
    private String column10;

    /**
     * 状态(0待处理1完成2失败)
     */
    private Integer flow_status;

    /**
     * 确认状态(0待确认1已确认)
     */
    private Integer ack_status;

    /**
     * 调度时间
     */
    private Date schedule_time;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 删除标识(1删除0未删除)
     */
    private Integer del_tag;

    private static final long serialVersionUID = 1L;
}