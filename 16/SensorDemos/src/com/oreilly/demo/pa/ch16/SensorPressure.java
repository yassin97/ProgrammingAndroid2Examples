package com.oreilly.demo.pa.ch16;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

public class SensorPressure extends Activity implements SensorEventListener {
	
	private boolean hassensor;
	
	private final Handler pressureEventHandler 				= new Handler() {
																	@Override
																	public void handleMessage(Message msg) {
																		String pres = (String) msg.obj;
																		((TextView) findViewById(R.id.prestext)).setText(pres);
																	}
																};

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getTempSensors() == null) {
			hassensor = false;
			Toast.makeText(this, "No Pressure Sensors Available", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		
		hassensor = true;
		setContentView(R.layout.sensorpressure);
		
		setTitle("Pressure");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(hassensor) registerListener();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterListener();
	}
	
	private List<Sensor> getTempSensors() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = mngr.getSensorList(Sensor.TYPE_TEMPERATURE);
		return list != null && !list.isEmpty() ? list : null;
	}
	
	private void registerListener() {
		SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = getTempSensors();
		if(list != null) {
			for(Sensor sensor: list) {
				mngr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
			}
		}
	}
	
	private void unregisterListener() {
		if(hassensor) {
			SensorManager mngr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			mngr.unregisterListener(this);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public void onSensorChanged(SensorEvent event) {
		float pres = event.values[0];  
		Message msg = Message.obtain();
		msg.obj = "Pressure: "+pres+" kPa";
		pressureEventHandler.sendMessage(msg);
	}
}
