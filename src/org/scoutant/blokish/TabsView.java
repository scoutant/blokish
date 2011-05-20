/*
* Copyright (C) 2011- stephane coutant
*
* This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
*/

package org.scoutant.blokish;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class TabsView extends FrameLayout {

	protected static final String tag = "ui";
	private GameView game;
	private TextView[] labels = new TextView[4]; 

	public TabsView(Context context) {
		super(context);
		setLayoutParams( new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, 40, Gravity.BOTTOM));
		setBackgroundColor(Color.TRANSPARENT);
		for(int i=0; i<4; i++) { 
			addView( new TabButton(context, i));
			labels[i] = new TabLabel(context, i);
			addView(labels[i]);
		}
	}	
	
	public void putLabel( int position, String label) {
		labels[position].setText( label);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		game = (GameView) getParent();
	}

	private class TabButton extends ImageButton implements OnClickListener {
		public int color;
		public TabButton(Context context, int color) {
			super(context);
			this.color = color;
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int w = display.getWidth();
			Drawable drawable = context.getResources().getDrawable( PieceUI.icons[color]);
			LayoutParams params = new FrameLayout.LayoutParams( w/6, 35, Gravity.BOTTOM);
			setImageDrawable( drawable);
			params.leftMargin = color * w/4 + 20;
			setLayoutParams( params);
			setOnClickListener( this);
		}
		public void onClick(View v) {
			game.showPieces(this.color);
			game.invalidate();
		}
	}
	
	private class TabLabel extends TextView  {
		public TabLabel(Context context, int position) {
			super(context);
			setText("0");
			setTextColor(Color.BLACK);
			setTextSize( 16);
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			int w = display.getWidth();
			LayoutParams params = new FrameLayout.LayoutParams( w/6, 35, Gravity.BOTTOM);
			params.leftMargin = position * w/4 + 20;
			setLayoutParams( params);
			setPadding(15, 0, 15, 0);
		}
		
	}
}