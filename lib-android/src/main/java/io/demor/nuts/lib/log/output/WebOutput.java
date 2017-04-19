package io.demor.nuts.lib.log.output;

import io.demor.nuts.lib.NutsApplication;
import io.demor.nuts.lib.controller.MethodInfoUtil;
import io.demor.nuts.lib.log.LogContext;
import io.demor.nuts.lib.log.LogOutput;
import io.demor.nuts.lib.module.PushObject;
import io.demor.nuts.lib.server.impl.ApiWebSocketServer;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class WebOutput extends LogOutput {

    public WebOutput(final Element element) throws DOMException {
        super(element);
    }

    @Override
    protected void append(final LogContext context) {
        if (NutsApplication.sApiServer == null
                || NutsApplication.sApiServer.mWebSocketServer == null
                || !(NutsApplication.sApiServer.mWebSocketServer instanceof ApiWebSocketServer)) {
            return;
        }

        ApiWebSocketServer socketServer = (ApiWebSocketServer) NutsApplication.sApiServer.mWebSocketServer;
        final PushObject pushObject = new PushObject();
        pushObject.mType = PushObject.TYPE_LOG;
        pushObject.mDataClz = String.class.getName();
        pushObject.mData = MethodInfoUtil.GSON.toJson(context);
        socketServer.sendPushObj(pushObject);
    }
}
