package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @author 
 * 活动奖品规则表
 */
@Data
public class RewardRulePO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Long activity_id;

    /**
     * 模块id
     */
    private Long module_id;

    /**
     * 事件id
     */
    private Long event_id;

    /**
     * 奖品类型
     */
    private Integer reward_type;

    /**
     * 奖品id
     */
    private Long reward_id;

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