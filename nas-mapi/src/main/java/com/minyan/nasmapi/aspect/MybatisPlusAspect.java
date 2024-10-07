package com.minyan.nasmapi.aspect;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.minyan.nascommon.Enum.DelTagEnum;
import java.util.Date;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @decription 数据库插入默认填充
 * @author minyan.he
 * @date 2024/10/6 18:28
 */
@Component
public class MybatisPlusAspect implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
    this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
    this.strictInsertFill(metaObject, "delTag", Integer.class, DelTagEnum.NOT_DEL.getValue());
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
  }
}
