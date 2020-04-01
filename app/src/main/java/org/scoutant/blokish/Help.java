package org.scoutant.blokish;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class Help extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

//		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		View v = findViewById(R.id.video);
		v.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube://3Q7ow07uaMw"))); // if device has Youtube app
				} catch (Exception e) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=3Q7ow07uaMw"))); // if not
				}
			}
		});
	}
	
}
