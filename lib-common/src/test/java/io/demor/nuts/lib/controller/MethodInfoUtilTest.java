package io.demor.nuts.lib.controller;

import org.junit.Assert;
import org.junit.Test;

public class MethodInfoUtilTest {

    @Test
    public void invoke() throws Exception {
        final TestObject testObject = new TestObject();
        String str = MethodInfoUtil.generateMethodInfo(testObject.getClass().getMethod("add", int.class, int.class),
                new Object[]{1, 2});
        Object resp = MethodInfoUtil.parseMethodInfo(testObject, str).callImpl();
        Assert.assertEquals(3, resp);
    }

    @Test
    public void argsArray() throws Exception {
        final TestObject testObject = new TestObject();
        String str = MethodInfoUtil.generateMethodInfo(testObject.getClass().getMethod("addArray", int[].class),
                new Object[]{new int[]{1, 1, 1}});
        Object resp = MethodInfoUtil.parseMethodInfo(testObject, str).callImpl();
        Assert.assertEquals(3, resp);
    }

    @Test
    public void fromObj() throws Exception {
        final TestObject testObject = new TestObject();
        String str = MethodInfoUtil.generateMethodInfo(testObject.getClass().getMethod("fromInt", int.class),
                new Object[]{3});
        Object resp = MethodInfoUtil.parseMethodInfo(testObject, str).callImpl();
        Assert.assertEquals("3", resp);
    }

    @Test
    public void toObj() throws Exception {
        final TestObject testObject = new TestObject();
        String str = MethodInfoUtil.generateMethodInfo(testObject.getClass().getMethod("toInt", String.class),
                new Object[]{"3"});
        Object resp = MethodInfoUtil.parseMethodInfo(testObject, str).callImpl();
        Assert.assertEquals(3, resp);
    }

    public static class TestObject {

        public int add(int a, int b) {
            return a + b;
        }

        public int addArray(int... args) {
            if (args == null) {
                return 0;
            }
            int result = 0;
            for (int i : args) {
                result += i;
            }
            return result;
        }

        public String fromInt(int i) {
            return String.valueOf(i);
        }

        public int toInt(String s) {
            return Integer.parseInt(s);
        }

    }
}
