package com.reggieboot.common;
/*基于ThreadLocal封装的工具类*/
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrent(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrent(){
        return threadLocal.get();
    }
}
