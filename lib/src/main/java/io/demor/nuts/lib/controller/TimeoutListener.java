package io.demor.nuts.lib.controller;

import java.util.Date;

public interface TimeoutListener {

    /**
     * Controller执行时候超时后执行的回调函数
     *
     * @param startTime 任务执行开始时间
     * @return 是否执行后续UI回调
     */
    boolean onTimeout(Date startTime, Date stopTime);
}
