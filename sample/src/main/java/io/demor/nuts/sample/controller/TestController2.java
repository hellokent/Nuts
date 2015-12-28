package io.demor.nuts.sample.controller;

import io.demor.nuts.lib.annotation.controller.Controller;
import io.demor.nuts.lib.controller.BaseController;
import io.demor.nuts.lib.controller.VoidReturn;

import java.util.concurrent.TimeUnit;

@Controller
public class TestController2 extends BaseController {

    public void test1() {
        System.out.println("test1");
    }

    public VoidReturn test2() {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("test2!!!!!!!!!");
        return ofVoid();
    }
}
