package com.github.warren_bank.adb_over_wifi;

import com.github.warren_bank.adb_over_wifi.helpers.Shell;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static int default_adbd_port = 5555;

    private TextView message;
    private Button button;
    private boolean is_busy;
    private boolean is_wifi_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        message = (TextView) findViewById(R.id.message);
        button  = (Button) findViewById(R.id.button);
        is_busy = false;

        reset();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_busy) {
                    toggle();
                }
            }
        });
    }

    private void reset() {
        int current_port   = Shell.getADBdPort();
        boolean is_running = Shell.isADBdRunning();

        is_wifi_mode = (is_running && (current_port == default_adbd_port));

        String ip, msg;
        int btn;
        if (is_wifi_mode) {
            ip  = Shell.getWlanIpAddress();
            msg = (ip == null)
                ?  "ADBd is running in wireless mode.\n\nNot connected to WiFi network."
                : ("ADBd is running in wireless mode.\n\nConnect remotely:\n  adb connect " + ip + ":" + default_adbd_port)
            ;
            btn = R.string.stop;
        }
        else {
            msg = "ADBd is not running in wireless mode.";
            btn = R.string.start;
        }

        message.setText(msg);
        button.setText(btn);
    }

    private void toggle() {
        is_busy = true;
        try {
            if (is_wifi_mode) {
                Shell.stopADBd();
            }
            else {
                Shell.startADBd(default_adbd_port);
            }
            reset();
        }
        catch(Exception e) {
        }
        is_busy = false;
    }
}
