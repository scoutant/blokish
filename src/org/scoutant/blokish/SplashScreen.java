package org.scoutant.blokish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class SplashScreen extends Activity {
	private static final long DELAY = 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splashscreen);
		handler.sendEmptyMessageDelayed(0, DELAY);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			startActivityForResult(new Intent(SplashScreen.this, UI.class), 0);
			super.handleMessage(msg);
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	};
	
}