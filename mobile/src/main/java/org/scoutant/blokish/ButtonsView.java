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
import android.graphics.Point;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;

import org.scoutant.blokish.model.Move;

public class ButtonsView extends FrameLayout {

	protected static final String tag = "ui";
	private final Vibrator vibrator;

	private Context context;
	private ImageButton cancel;
	public ImageButton ok;

	private GameView game;

	private int width;

	public ButtonsView(Context context) {
		super(context);
		this.context = context;
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		setVisibility(INVISIBLE);
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point pointSize = new Point();
		display.getSize(pointSize);
		width = pointSize.x;
		int height = pointSize.y;
		int h = height - width;
		setLayoutParams( new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, h, Gravity.BOTTOM));
		cancel = button(R.drawable.cancel, doCancel, 0);
		addView(cancel );
		ok = button(R.drawable.checkmark, doOk, 1);
		addView(ok);
		setOkState( false);
	}
	
	
	private ImageButton button(int src, OnClickListener l, int position) {
		ImageButton btn = new ImageButton(context);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
		int margin = Math.min( (width - 3*128)/3, 80);
		params.leftMargin = margin;
		params.rightMargin = margin;
		if (position==0) params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		if (position==1) params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		btn.setLayoutParams(params);
		btn.setImageDrawable(context.getResources().getDrawable(src));
		btn.setScaleType(ScaleType.CENTER_INSIDE);
		btn.setBackgroundColor(Color.TRANSPARENT);
		btn.setOnClickListener(l);
		return btn;
		
	}

	protected void setState( ImageButton btn, boolean state) {
		btn.setEnabled( state);
//		btn.setAlpha( state ? 200 : 50 );
		btn.setAlpha( state ? 0.78f : 0.196f );
	}
	
	public void setOkState(boolean state) {
		setState(ok, state);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		game = (GameView) getParent();
	}
	
	private OnClickListener doOk = new OnClickListener() {
		public void onClick(View v) {
			Log.d(tag, "ok...");
			PieceUI piece = game.selected;
			if (piece==null) {
				Log.e(tag, "cannot retrieve piece!");
				return;
			}
			Move move = new Move(piece.piece, piece.i, piece.j);
			boolean possible = game.game.valid( move);
			if (possible) {
				if (vibrator!=null) vibrator.vibrate(20);
				// TODO refactor with place()
				piece.movable=false;
				piece.setLongClickable(false);
				piece.setClickable(false);
				game.lasts[piece.piece.color] = piece;
				piece.invalidate();
				ButtonsView.this.setVisibility(INVISIBLE);
				ButtonsView.this.game.game.play( move);
				((GameView)getParent()).tabs[move.piece.color].setText( ""+game.game.boards.get(move.piece.color).score);
				game.selected = null;
				game.ui.turn = (piece.piece.color+1)%4;
				if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ai", true)) {
					game.ui.think(game.ui.turn);
				} else {
						game.showPieces(game.ui.turn);
						game.invalidate();
				}
			}
		}
	};
	private OnClickListener doCancel = new OnClickListener() {
		public void onClick(View v) {
			if (vibrator!=null) vibrator.vibrate(20);
			Log.d(tag, "cancel...");
			game.selected.replace();
			game.selected = null;
			ButtonsView.this.setVisibility(INVISIBLE);
		}
	};
	
}