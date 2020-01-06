package com.example.fd;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

public class AlertService extends Service {
    public AlertService() {
    }

    private MediaPlayer player;
    int count = 0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Ringing Started", Toast.LENGTH_LONG).show();
        player = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
        player.setLooping(true);

        player.start();
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        player.stop();
        Toast.makeText(this,"Alert Stopped", Toast.LENGTH_LONG).show();
    }


}
