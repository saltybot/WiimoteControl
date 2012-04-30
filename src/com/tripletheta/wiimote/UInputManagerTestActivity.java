package com.tripletheta.wiimote;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class UInputManagerTestActivity extends Activity {
	
	private UInputManager mUInputManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uinput_test);

        // Enable user read/write permission on /dev/uinput; need ROOT
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "chmod 666 /dev/uinput"});
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        mUInputManager = new UInputManager();

        final EditText relXCoordText = (EditText) findViewById(R.id.xRelCoordText);
        final EditText relYCoordText = (EditText) findViewById(R.id.yRelCoordText);
        final EditText absXCoordText = (EditText) findViewById(R.id.xAbsCoordText);
        final EditText absYCoordText = (EditText) findViewById(R.id.yAbsCoordText);
        
        Button sendRelEventButton = (Button) findViewById(R.id.sendRelEventButton);
        sendRelEventButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int x, y;
				try {
					x = Integer.parseInt(relXCoordText.getText().toString());
					y = Integer.parseInt(relYCoordText.getText().toString());
					mUInputManager.movePointerRelative(x, y);
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
        Button sendAbsEventButton = (Button) findViewById(R.id.sendAbsEventButton);
        sendAbsEventButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				int x, y;
				try {
					x = Integer.parseInt(absXCoordText.getText().toString());
					y = Integer.parseInt(absYCoordText.getText().toString());
					mUInputManager.movePointerAbsolute(x, y);
				}
				catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
        
        findViewById(R.id.clickButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mUInputManager.click();
            }
        });
    }
    
    @Override
    public void onDestroy() {
        mUInputManager.destroy();
        super.onDestroy();
    }
}
