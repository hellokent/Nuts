package io.demor.server;

import android.util.SparseArray;
import org.joor.Reflect;

import java.lang.reflect.Field;
import java.util.Map;

public class Constant {
    public static final int DRAWABLE_DEFAULT_WIDTH = 100;
    public static final int DRAWABLE_DEFAULT_HEIGHT = 100;

    public static final SparseArray<String> DRAWABLE_STATE_MAP = new SparseArray<>();

    static {
        try {
            final Class<?> styleableClz = Class.forName("android.R$styleable");
            final Reflect reflect = Reflect.on("android.R$styleable");
            Field setField = styleableClz.getDeclaredField("DrawableStates");
            setField.setAccessible(true);
            final int[] content = (int[]) setField.get(styleableClz);

            for (Map.Entry<String, Reflect> entry : reflect.fields().entrySet()) {

                final String name = entry.getKey();
                if (!name.startsWith("DrawableStates_state")) {
                    continue;
                }
                Object fieldValue = reflect.field(name).get();
                if (fieldValue instanceof Integer) {
                    DRAWABLE_STATE_MAP.put(content[(int) fieldValue], name.replace("DrawableStates_state_", ""));
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
