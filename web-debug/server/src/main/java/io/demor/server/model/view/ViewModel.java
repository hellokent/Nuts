package io.demor.server.model.view;

import android.graphics.Rect;
import android.graphics.drawable.*;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import io.demor.nuts.common.server.BaseResponse;
import io.demor.server.ScreenHelper;
import org.joor.Reflect;

public class ViewModel extends BaseResponse {

    /**
     * The digits for every supported radix.
     */
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    private static final char[] UPPER_CASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    public int paddingLeft;
    public int paddingTop;
    public int paddingRight;
    public int paddingBottom;
    public String backgroundColor;
    public String bgRadius;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public int width;
    public int height;
    public Integer marginLeft;
    public Integer marginTop;
    public Integer marginRight;
    public Integer marginBottom;
    public Integer gravity;
    public String viewName;
    public String idName;

    public ViewModel(final View view) {

        viewName = view.getClass().getSimpleName();
        idName = ScreenHelper.ID_MAP.get(view.getId());

        width = view.getWidth();
        height = view.getHeight();

        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        left = rect.left - ScreenHelper.sXOffset;
        top = rect.top - ScreenHelper.sYOffset;
        right = rect.right - ScreenHelper.sXOffset;
        bottom = rect.bottom - ScreenHelper.sYOffset;

        paddingLeft = view.getPaddingLeft();
        paddingTop = view.getPaddingTop();
        paddingRight = view.getPaddingRight();
        paddingBottom = view.getPaddingBottom();

        final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        if (layoutParams != null) {

            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutParams;
                marginLeft = params.leftMargin;
                marginTop = params.topMargin;
                marginRight = params.rightMargin;
                marginBottom = params.bottomMargin;
            }

            if (layoutParams instanceof LinearLayout.LayoutParams) {
                final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layoutParams;
                gravity = params.gravity;
            } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layoutParams;
                gravity = params.gravity;
            }
        }

        setDrawableInfo(view.getBackground());
    }

    public static String colorString(int color) {
        return "#" + intToHexString(color, true, 6);
    }

    public static String intToHexString(int i, boolean upperCase, int minWidth) {
        int bufLen = 8;  // Max number of hex digits in an int
        char[] buf = new char[bufLen];
        int cursor = bufLen;

        char[] digits = upperCase ? UPPER_CASE_DIGITS : DIGITS;
        do {
            buf[--cursor] = digits[i & 0xf];
        } while ((i >>>= 4) != 0 || (bufLen - cursor < minWidth));

        return new String(buf, cursor, bufLen - cursor);
    }

    private void setDrawableInfo(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof ColorDrawable) {
            backgroundColor = colorString(((ColorDrawable) drawable).getColor());
        } else if (drawable instanceof ShapeDrawable) {
        } else if (drawable instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) drawable;
            setDrawableInfo(stateListDrawable.getCurrent());
        } else if (drawable instanceof GradientDrawable) {
            bgRadius = Reflect.on(drawable.getConstantState())
                    .field("mRadius")
                    .get().toString();
        }
    }

}
