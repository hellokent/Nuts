package com.example;

import java.lang.reflect.Method;

public class MyClass {

    public static void main(String[] args) {
        for (Method m : MyClass.class.getDeclaredMethods()) {
            System.out.println(m);
        }
    }

    public void a(int b, short c, double d) {
    }
}
