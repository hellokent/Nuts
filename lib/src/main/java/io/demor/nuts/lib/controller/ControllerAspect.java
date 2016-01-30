package io.demor.nuts.lib.controller;

//@Aspect
public class ControllerAspect {

//    @Around("execution(* *(..)) " +
//            "&& @within(io.demor.nuts.lib.annotation.controller.Controller)" +
//            "&& !within()")
//    public Object doController(ProceedingJoinPoint joinPoint) throws Throwable {
//
//        if (SafeTask.THREADS.contains(Thread.currentThread())) {
//
//        }
//        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
//        System.out.println(codeSignature);
//        Method[] methodArray = joinPoint.getThis().getClass().getMethods();
//        for (Method m : methodArray) {
//            if (m.getName() .equals(codeSignature.getName())) {
//                System.out.println("proxy run");
//                return ProxyInvokeHandler.invoke(joinPoint.getThis(), joinPoint.getThis(), m, joinPoint.getArgs());
//            }
//        }
//        System.out.println("direct run");
//        return joinPoint.proceed();
//    }

}
