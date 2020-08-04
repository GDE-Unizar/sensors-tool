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

    private long[] vibconfig = new long[2];


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
        ((Switch) findViewById(R.id.swt_vib)).setOnCheckedChangeListener(this);
        ((SeekBar) findViewById(R.id.skb_on)).setOnSeekBarChangeListener(this);
    }

    // ----------------------- sensor -------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        txt_accelerometer.setText(String.format(Locale.getDefault(), "X=%.2f\nY=%.2f\nZ=%.2f", event.values[0], event.values[1], event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // ------------------------ switch ------------------------

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            // vibrate
            vibrator.vibrate(vibconfig, vibconfig.length);
        }
    }

    // ------------------------- seekbar ---------------------

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        vibconfig[seekBar.getId() == R.id.skb_on ? 0 : 1] = progress;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}