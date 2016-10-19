package io.demor.server.model.view;

import android.text.TextUtils;
import android.widget.TextView;

public class TextViewModel extends ViewModel {

    public String text;
    public String textColor;
    public float textSize;

    public String hintText;
    public String hintTextColor;

    public TextViewModel(TextView textView) {
        super(textView);
        text = textView.getText().toString();
        textColor = colorString(textView.getTextColors().getDefaultColor());
        textSize = textView.getTextSize();

        if (!TextUtils.isEmpty(textView.getHint())) {
            hintText = textView.getHint().toString();
            hintTextColor = colorString(textView.getHintTextColors().getDefaultColor());
        }
    }
}
