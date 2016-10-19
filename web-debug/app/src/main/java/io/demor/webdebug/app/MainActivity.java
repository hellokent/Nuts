package io.demor.webdebug.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import io.demor.server.ServerManager;
import io.demor.server.sniff.SimpleSniffer;

public class MainActivity extends Activity {

    @InjectView(R.id.address)
    TextView mAddress;

    @InjectView(R.id.http_port)
    TextView mHttpPort;

    SimpleSniffer mSniff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mSniff = ServerManager.getSniffer("btn");
        ServerManager.start(8080);

        mAddress.setText(ServerManager.getIpAddress());
        mHttpPort.setText(String.valueOf(ServerManager.getHttpPort()));
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sniff:
                ServerManager.showAddressDialog(this);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_net:
                startActivity(new Intent(this, NetWatcherActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
