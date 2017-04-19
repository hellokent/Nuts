package io.demor.nuts.lib;

import com.google.common.base.Joiner;
import io.demor.nuts.lib.module.BaseResponse;
import io.demor.nuts.lib.module.ControllerInvocationResponse;
import io.demor.nuts.lib.server.ApiServer;
import io.demor.nuts.lib.server.BaseWebSocketServer;
import io.demor.nuts.lib.server.IApi;
import io.demor.nuts.lib.server.IClient;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static io.demor.nuts.lib.controller.MethodInfoUtil.parseMethodInfo;

public class MockApiServer extends ApiServer {

    public MockApiServer(IClient client, BaseWebSocketServer webSocketServer) {
        super(client, webSocketServer);
    }


    public <T> ApiServer registerController(final Class<T> api, final T impl) {
        mHttpServer.registerApi(new IApi() {
            @Override
            public String name() {
                return "controller/" + api.getName();
            }

            @Override
            public Object call(Map<String, String> param, String body) {
                try {
                    return new ControllerInvocationResponse(parseMethodInfo(impl, body).directCall());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("no such method");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("invoke target exception:" + e.getMessage() + "\n" +
                            Joiner.on('\n').join(e.getTargetException().getStackTrace()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("illegal access exception:" + e.getMessage());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return new BaseResponse().ofFailed("class not found:" + e.getMessage());
                }
            }
        });
        return this;
    }
}
