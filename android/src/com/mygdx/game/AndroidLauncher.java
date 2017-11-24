package com.mygdx.game;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.mygdx.game.fragment.BluetoothFragment;
import com.mygdx.game.fragment.GameFragment;

import java.util.ArrayList;

public class AndroidLauncher extends FragmentActivity implements AndroidFragmentApplication.Callbacks
{
	private static final int REQUEST_COARSE_LOCATION = 1;
	private GameFragment gameFragment;
	private BluetoothFragment bluetoothFragment;
	public static AndroidLauncher instance;
	private BluetoothService bluetoothService;
	StringBuilder stringBuilder;
	ArrayList<Integer> values;

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout);

		instance = this;
		bluetoothService = new BluetoothService(this,handler);
		bluetoothService.enableBluetooth();
		checkLocationPermission();

		/*gameFragment = new GameFragment();
		bluetoothFragment = new BluetoothFragment();
		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		trans.add(R.id.frag1, gameFragment, "BluetoothFragment");
		trans.add(R.id.frag2, bluetoothFragment, "GameFragment");
		trans.commit();*/


		getSupportFragmentManager().beginTransaction().add(R.id.frag1, new GameFragment(), "GameFragment").commit();

		getSupportFragmentManager().beginTransaction().add(R.id.frag2, new BluetoothFragment(), "BluetoothFragment").commit();
		//bluetoothFragment = ((BluetoothFragment) getSupportFragmentManager().findFragmentById(R.id.frag2));

		stringBuilder = new StringBuilder();
		values = new ArrayList<Integer>();
	}

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case BluetoothConstants.MESSAGE_TOAST:
					CharSequence content = msg.getData().getString(BluetoothConstants.TOAST);
					Toast.makeText(instance, content , Toast.LENGTH_SHORT).show();
					break;
				case BluetoothConstants.MESSAGE_DEVICE_NAME:
					CharSequence connectedDevice = "Connected to " + msg.getData().getString(BluetoothConstants.DEVICE_NAME);
					Toast.makeText(instance, connectedDevice, Toast.LENGTH_SHORT).show();
					break;
				case BluetoothConstants.MESSAGE_READ:

					String readMessage = (String) msg.obj;
					stringBuilder.append(readMessage);
					int endOfLine = stringBuilder.indexOf("K");
					if(endOfLine > 0 ){
						String data = stringBuilder.substring(0,endOfLine);
						//textView.setText(data);
						if(stringBuilder.charAt(0) == 'M'){
							stringBuilder.deleteCharAt(0);
							while(!(stringBuilder.charAt(0) == 'K')){

								int pos1 = stringBuilder.indexOf(",");
								values.add(Integer.parseInt(stringBuilder.substring(0,pos1)));
								//textView1.setText(stringBuilder.substring(0,pos1));
								stringBuilder.delete(0,pos1 + 1);
							}
							gameFragment.getGameInstance().dataForGame(values);

						}
						stringBuilder.delete(0,stringBuilder.length());
						data = "";
						values.clear();
					}

					break;
				case BluetoothConstants.MESSAGE_STATE_CHANGE:
					if(bluetoothService.getState() == BluetoothConstants.STATE_CONNECTED )
					{
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,0);
						LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.MATCH_PARENT,0);

						params.weight = 100;
						params1.weight = 0;
						FrameLayout frameLayout, frameLayout1;
						frameLayout = (FrameLayout) findViewById(R.id.frag1);
						frameLayout.setLayoutParams(params);
						frameLayout1 = (FrameLayout) findViewById(R.id.frag2);
						frameLayout1.setLayoutParams(params1);
						gameFragment = ((GameFragment) getSupportFragmentManager().findFragmentById(R.id.frag1));
						gameFragment.getGameInstance().isConnected();

					}
					break;
			}
		}
	};

	public BluetoothService getBluetoothService()
	{
		return bluetoothService;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BluetoothConstants.REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_CANCELED) {
				Toast.makeText(getApplicationContext(), "Enable Bluetooth first!", Toast.LENGTH_SHORT).show();
				finish();
			} else if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "Bluetooth is now enabled", Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void checkLocationPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
					REQUEST_COARSE_LOCATION);
		}
	}

	@Override
	public void exit() {}
}
