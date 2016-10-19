package io.demor.server.api;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.view.Display;
import android.view.WindowManager;
import io.demor.nuts.common.server.annotation.Url;
import io.demor.server.ScreenHelper;
import io.demor.server.ServerManager;
import io.demor.server.model.PhoneInfo;
import io.demor.server.model.view.ViewModel;
import io.demor.server.model.view.ViewModelFactory;

@Url("widget")
public class WidgetApi {

    @Url("/current")
    public ViewModel getCurrentViewModel() {
        return ViewModelFactory.createViewModel(ScreenHelper.getView());
    }

    @Url("/phone")
    public PhoneInfo getPhoneInfo() {
        final WindowManager manager = (WindowManager) ServerManager.sApplication.getSystemService(Context.WINDOW_SERVICE);
        final PhoneInfo result = new PhoneInfo();
        final Display display = manager.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        result.width = point.x;
        result.height = point.y;
        result.scale = result.width / 720;
        result.name = Build.MODEL;
        result.os = "Android " + VERSION.RELEASE;
        return result;
    }
}
