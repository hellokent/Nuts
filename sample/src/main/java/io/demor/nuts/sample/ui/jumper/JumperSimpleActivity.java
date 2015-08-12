package io.demor.nuts.sample.ui.jumper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import io.demor.nuts.sample.config.IntentNames;

public class JumperSimpleActivity extends Activity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int count = getIntent().getIntExtra(IntentNames.COUNT, -1);
        TextView view = new TextView(this);
        view.setText(getClass().getName());
        setContentView(view);

        setResult(RESULT_OK, new Intent().putExtra(IntentNames.COUNT, count + 1));
    }
}
