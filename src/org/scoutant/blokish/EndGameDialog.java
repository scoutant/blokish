package org.scoutant.blokish;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class EndGameDialog extends Dialog {
	public EndGameDialog(final Context context, boolean redwins, String message, final int level, final int score) {
		super(context);
		setContentView( R.layout.endgame);
		// Cf layout issue http://groups.google.com/group/android-developers/browse_thread/thread/f0bb813f643604ec?pli=1
		getWindow().setLayout( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		TextView tv = (TextView) findViewById(R.id.message);
		tv.setText( message);
		Button b = (Button) findViewById(R.id.ok);
		b.setOnClickListener( new android.view.View.OnClickListener(){
			public void onClick(View v) {
				EndGameDialog.this.dismiss();
			}
		});
		findViewById(R.id.icons).setVisibility( redwins ? View.VISIBLE : View.GONE);
	}
}
