package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动领取规则表
 */
@Data
public class ReceiveRulePO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 模块id
     */
    private Integer event_id;

    /**
     * 规则类型(1前台规则2异步发奖规则)
     */
    private Integer rule_type;

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