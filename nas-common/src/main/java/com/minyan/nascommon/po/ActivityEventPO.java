package com.minyan.nascommon.po;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.minyan.nascommon.vo.MActivityEventDetailVO;
import lombok.Data;

/**
 * @author 
 * 活动事件表
 */
@Data
@TableName("nas_activity_event")
public class ActivityEventPO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 模块id
     */
    private Long moduleId;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件类型
     */
    private String eventType;

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

    /**
     * 转化po为事件详情出参
     * @param po
     * @return
     */
    public static MActivityEventDetailVO poConvertToVo(ActivityEventPO po) {
        MActivityEventDetailVO mActivityEventDetailVO = new MActivityEventDetailVO();
        mActivityEventDetailVO.setEventId(po.getId());
        mActivityEventDetailVO.setActivityId(po.getActivityId());
        mActivityEventDetailVO.setModuleId(po.getModuleId());
        mActivityEventDetailVO.setEventName(po.getEventName());
        mActivityEventDetailVO.setEventType(po.getEventType());
        return mActivityEventDetailVO;
    }
}