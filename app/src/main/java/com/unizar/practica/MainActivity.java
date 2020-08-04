package com.unizar.practica;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends Activity implements SensorEventListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Vibrator vibrator;

    private TextView txt_accelerometer;
    private Switch swt_vibrate;

    private long[] vibconfig = new long[] {50, 50};


    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // views
        txt_accelerometer = findViewById(R.id.txt_acc);

        // sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // listeners
        swt_vibrate = findViewById(R.id.swt_vib);
        swt_vibrate.setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.skb_on)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.skb_off)).setOnSeekBarChangeListener(this);
        updateVibrationUI();
    }

    // ----------------------- sensor -------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        txt_accelerometer.setText(String.format(Locale.getDefault(), "X=%.5f\nY=%.5f\nZ=%.5f", event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // ------------------------ switch ------------------------

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // vibrate
            vibrator.vibrate(vibconfig, 0);
        } else {
            vibrator.cancel();
        }
    }

    // ------------------------- seekbar ---------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        vibconfig[seekBar.getId() == R.id.skb_on ? 0 : 1] = progress + 1;
        updateVibrationUI();
    }

    private void updateVibrationUI() {
        swt_vibrate.setText(String.format(Locale.getDefault(), "on=%d - off=%d", vibconfig[0], vibconfig[1]));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}