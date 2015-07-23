package com.example.amrizalzainuddin.weatherstation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Timer;
import java.util.TimerTask;


public class WeatherStation extends ActionBarActivity {

    private SensorManager sensorManager;
    private TextView temperatureTextView;
    private TextView pressureTextView;
    private TextView lightTextView;

    private float currentTemperature = Float.NaN;
    private float currentPressure = Float.NaN;
    private float currentLight = Float.NaN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_station);

        temperatureTextView = (TextView)findViewById(R.id.temperature);
        pressureTextView = (TextView)findViewById(R.id.pressure);
        lightTextView = (TextView)findViewById(R.id.light);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);

        Timer updateTimer = new Timer("weatherUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGUI();
            }
        },0,1000);
    }

    private final SensorEventListener tempSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentTemperature = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final SensorEventListener pressureSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentPressure = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private final SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            currentLight = event.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null)
            sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            lightTextView.setText("Light Sensor Unavailable");

        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor != null)
            sensorManager.registerListener(pressureSensorListener, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            pressureTextView.setText("Barometer Unavailable");

        Sensor temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if (temperatureSensor != null)
            sensorManager.registerListener(tempSensorEventListener, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        else
            temperatureTextView.setText("Thermometer Unavailable");
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(pressureSensorListener);
        sensorManager.unregisterListener(tempSensorEventListener);
        sensorManager.unregisterListener(lightSensorEventListener);

        super.onPause();
    }

    private void updateGUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!Float.isNaN(currentPressure)){
                    pressureTextView.setText(currentPressure + "hPa");
                    pressureTextView.invalidate();
                }

                if(!Float.isNaN(currentLight)){
                    String lightStr = "Sunny";
                    if(currentLight <= SensorManager.LIGHT_CLOUDY)
                        lightStr = "Night";
                    else if(currentLight <= SensorManager.LIGHT_OVERCAST)
                        lightStr = "Cloudy";
                    else if(currentLight <= SensorManager.LIGHT_SUNLIGHT)
                        lightStr = "Overcast";

                    lightTextView.setText(lightStr);
                    lightTextView.invalidate();
                }

                if(!Float.isNaN(currentTemperature)){
                    temperatureTextView.setText(currentTemperature + "C");
                    temperatureTextView.invalidate();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_station, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
