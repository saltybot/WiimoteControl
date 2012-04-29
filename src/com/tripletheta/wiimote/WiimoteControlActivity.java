package com.tripletheta.wiimote;

import java.io.IOException;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WiimoteControlActivity extends Activity implements OnClickListener {

    private static final int REQUEST_ENABLE_BT = 0x424C5545;
	
    private static final String TAG = "WiimoteControlActivity";

    private static final String WIIMOTE_NINTENDO_WIIMOTE = "Nintendo RVL-CNT-01";
    private static final String WIIMOTE_NINTENDO_WIIMOTE_PLUS = "Nintendo RVL-CNT-01-TR";
    
    private UInputManager mUInputManager;
    
    private EditText mXCoordText;
    private EditText mYCoordText;
	
    private final BroadcastReceiver mBluetoothBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (WIIMOTE_NINTENDO_WIIMOTE.equals(device.getName())) {
                    Log.d(TAG, "Found Nintendo WiiMote at " + device.getAddress());
                } else if (WIIMOTE_NINTENDO_WIIMOTE_PLUS.equals(device.getName())) {
                    Log.d(TAG, "Found Nintendo Wiimote Plus at " + device.getAddress());
                }
            }
        }
    };
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Enable user read/write permission on /dev/uinput; need ROOT
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 666 /dev/uinput"});
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        //initBluetooth();
        
        mUInputManager = new UInputManager();

        mXCoordText = (EditText) findViewById(R.id.xCoordText);
        mYCoordText = (EditText) findViewById(R.id.yCoordText);
        
        Button sendEventButton = (Button) findViewById(R.id.sendEventButton);
        sendEventButton.setOnClickListener(this);
    }
    
    @Override
    public void onDestroy() {
        //unregisterReceiver(mBluetoothBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int x, y;
        try {
            x = Integer.parseInt(mXCoordText.getText().toString());
            y = Integer.parseInt(mYCoordText.getText().toString());
            mUInputManager.movePointerRelative(x, y);
        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
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
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBluetoothBroadcastReceiver, filter);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        // Prompt user to enable bluetooth, if not already enabled
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        if (!bluetoothAdapter.startDiscovery()) {
            Log.w(TAG, "Failed to start bluetooth discovery");
        }
    }
}