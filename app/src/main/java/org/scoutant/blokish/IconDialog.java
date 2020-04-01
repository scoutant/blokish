package org.scoutant.blokish;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class IconDialog extends Dialog {
	private OnClick listener;

	public IconDialog(final Context context, int title_id) {
		super(context);
		setContentView( R.layout.simple_dialog);
		TextView tv = (TextView) findViewById(R.id.title);
		tv.setText( title_id);
		getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		findViewById( R.id.no).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel();
			}
		});
		findViewById( R.id.yes).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick();
				cancel();
			}
		});
	}

	public void setListener( final OnClick listener) {
		this.listener = listener;
	}

	public interface OnClick {
		void onClick();
	}
}
