package org.scoutant.blokish;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

public class RateDialog extends Dialog {
	private SharedPreferences.Editor editor; 
    private final static String APP_PNAME = "org.scoutant.blokish";
	public RateDialog(final Context context) {
		super(context);
        editor = context.getSharedPreferences("apprater", 0).edit();		
		setContentView( R.layout.rate);
		// Cf layout issue http://groups.google.com/group/android-developers/browse_thread/thread/f0bb813f643604ec?pli=1
		getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		Button now = (Button) findViewById(R.id.now);
		now.setOnClickListener( new android.view.View.OnClickListener(){
			public void onClick(View v) {
				context.startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
				resetCounter();
				RateDialog.this.dismiss();
			}
		});
		Button later = (Button) findViewById(R.id.later);
		later.setOnClickListener( new android.view.View.OnClickListener(){
			public void onClick(View v) {
				resetCounter();
				RateDialog.this.dismiss();
			}
		});
		Button never = (Button) findViewById(R.id.never);
		never.setOnClickListener( new android.view.View.OnClickListener(){
			public void onClick(View v) {
                if (editor != null) editor.putBoolean("dontshowagain", true).commit();
				RateDialog.this.dismiss();
			}
		});
	}
	
	private void resetCounter() {
		if (editor== null) return;
		editor.putLong("launch_count", 0);
		editor.putLong("date_firstlaunch", 0);
		editor.commit();
	}
	
}
