package com.stucom.flx.VCTR_DRRN;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.stucom.flx.VCTR_DRRN.views.WormyView;

public class NewPlayActivity extends AppCompatActivity implements WormyView.WormyListener, SensorEventListener {

    private WormyView wormyView;
    private TextView tvScore;
    private MediaPlayer mp;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newplay);
        wormyView = findViewById(R.id.wormyView);
        Button btnNewGame = findViewById(R.id.btnNewGame);
        tvScore = findViewById(R.id.tvScore);
      mp= MediaPlayer.create(NewPlayActivity.this, R.raw.music2);
        mp.start();


        // We "capture" the Sensor Manager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);



        btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvScore.setText("0");
                wormyView.newGame();
            }
        });
        wormyView.setWormyListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch(event.getKeyCode()) {
            case KeyEvent.KEYCODE_A: wormyView.update(0, +10); break;
            case KeyEvent.KEYCODE_Q: wormyView.update(0, -10); break;
            case KeyEvent.KEYCODE_O: wormyView.update(-10, 0); break;
            case KeyEvent.KEYCODE_P: wormyView.update(+10, 0); break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void scoreUpdated(View view, int score) {
        tvScore.setText(String.valueOf(score));
    }

    @Override
    public void gameLost(View view) {
        Toast.makeText(this, getString(R.string.you_lost), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPause() {
        // Nicely disconnect the sensor's listener from the view
        sensorManager.unregisterListener(this);
        mp.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        // Connect the sensor's listener to the view
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
        mp.start();
        super.onResume();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Read the sensor's information
        float ax = sensorEvent.values[0];
        float ay = sensorEvent.values[1];
        float az = sensorEvent.values[2];

        wormyView.onSensorChanged(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        wormyView.onAccuracyChanged(sensor, accuracy);

    }
}
