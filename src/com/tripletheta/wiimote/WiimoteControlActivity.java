package com.tripletheta.wiimote;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class WiimoteControlActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 0x424C5545;
	
    private static final String TAG = "WiimoteControlActivity";

    private static final String WIIMOTE_NINTENDO_WIIMOTE = "Nintendo RVL-CNT-01";
    private static final String WIIMOTE_NINTENDO_WIIMOTE_PLUS = "Nintendo RVL-CNT-01-TR";
    
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothServerSocket mBluetoothServerSocket;
	
    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (WIIMOTE_NINTENDO_WIIMOTE.equals(device.getName())) {
                    Log.d(TAG, "Found Nintendo WiiMote at " + device.getAddress());
                }
                else if (WIIMOTE_NINTENDO_WIIMOTE_PLUS.equals(device.getName())) {
                    Log.d(TAG, "Found Nintendo Wiimote Plus at " + device.getAddress());
                }
                else {
                	Log.d(TAG, "Discovered bluetooth device: " + device.getName());
                }
            }
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initBluetooth();
        
        Button scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mBluetoothAdapter.startDiscovery();
			}
		});
        
        
    }
    
    @Override
    public void onDestroy() {
        unregisterReceiver(mBluetoothBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "User agreed to enable bluetooth");
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "User declined to enable bluetooth");
            }
        }
    }
    
    private void initBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
        	Toast.makeText(this, R.string.bluetooth_adapter_not_found, Toast.LENGTH_SHORT);
            finish();
        }
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothBroadcastReceiver, filter);
        
        // Prompt user to enable bluetooth, if not already enabled
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}