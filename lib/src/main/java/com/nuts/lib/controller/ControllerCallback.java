package com.nuts.lib.controller;

/**
 * Created by 陈阳(chenyang@edaijia-staff.cn>)
 * Date: 6/24/14 11:18 AM.
 */
public interface ControllerCallback<T> {
    void onResult(T t);
}
