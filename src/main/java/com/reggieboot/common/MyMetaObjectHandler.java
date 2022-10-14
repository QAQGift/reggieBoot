package com.reggieboot.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/*自定义元数据对象处理器，公共字段自动填充*/
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /*插入操作自动填充*/
    /*@Autowired
    HttpServletRequest request;*/

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert"+metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrent() );
        metaObject.setValue("updateUser", BaseContext.getCurrent());

    }
    /*更新操作自动填充*/
    @Override
    public void updateFill(MetaObject metaObject) {
        long id = Thread.currentThread().getId();
        log.info("线程id"+id);
        log.info("update"+metaObject.toString());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrent());
    }
}
