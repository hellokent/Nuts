package io.demor.nuts.lib.storage;

import com.google.common.reflect.Reflection;
import io.demor.nuts.lib.BaseTest;
import io.demor.nuts.lib.controller.ControllerInvokeHandler;
import io.demor.nuts.sample.lib.controller.TestController;
import io.demor.nuts.sample.lib.module.SimpleObject;
import org.junit.Assert;
import org.junit.Test;

public class StorageTest extends BaseTest {

    @Test
    public void simple() throws Exception {
        final Storage<SimpleObject> storage = new Storage.Builder<SimpleObject>()
                .setClass(SimpleObject.class)
                .setStorageEngine(new RemoteMobileStorageEngine(mAppInstance))
                .build();
        final TestController controller = Reflection.newProxy(TestController.class, new ControllerInvokeHandler(mAppInstance));
        {
            SimpleObject object = new SimpleObject();
            object.mName = "name";
            object.mAge = 18;
            object.mGender = true;
            storage.save(object);
            Assert.assertEquals(controller.getStorage(), object);
        }
    }
}
