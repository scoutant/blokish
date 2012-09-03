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

import org.scoutant.blokish.model.Piece;
import org.scoutant.blokish.model.Square;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class PieceUI extends FrameLayout implements OnTouchListener, OnLongClickListener, Comparable<PieceUI> {
	
	public static final int PADDING = 4;
	private static final String tag = "activity";
	
	private Resources resources;
	private Drawable square;
	private Drawable square_bold;
	private Canvas canvas;
	/** square size in pixel */
	private int size;
	/** piece size in # of square */
	private int footprint;
	/** display footprint */
	private int df;

	public Piece piece;
	public int i0;
	public int j0;

	public int i;
	public int j;
	
	private int localX=0;
	private int localY=0;
	
	public static int[] icons = { R.drawable.red, R.drawable.green, R.drawable.blue, R.drawable.orange };
	public static int[] icons_bold = { R.drawable.red_bold, R.drawable.green_bold, R.drawable.blue_bold, R.drawable.orange_bold };
	
	public boolean movable=true;
	public boolean moving=false;
	private boolean rotating = false;
	
	private Paint paint = new Paint();
	
	public int swipeX=0;
	private int downX;
	private int downY;
	private int angle=0;
	private int radius=0;
	private double rDown=0;

	private float rawX;
	private float rawY;
	// origin offset
	private int oo;
	private Context context;
	private Vibrator vibrator; 

	private Animation animation;
	private int statusBarHeight=-1;
	private Matrix m = new Matrix();

	protected PieceUI(Context context) {
		super(context);
		this.context = context;
		setWillNotDraw(false);
		setOnLongClickListener(this);
		setOnTouchListener(this);
		resources = context.getApplicationContext().getResources();
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		size = display.getWidth()/20;
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		animation = AnimationUtils.loadAnimation(context, R.anim.wave_scale);
		paint.setColor(0x99999999);
	}
	
	public PieceUI(Context context, Piece piece) {
		this(context);
		this.piece = piece;
		footprint = piece.size;
		df = Math.max(footprint, 3);
		oo = ( footprint>2? 1 : 0);
		if (footprint==5) oo = 2;
		radius = PADDING*size + footprint*size/2;
		square = resources.getDrawable( icons[piece.color]);
		square_bold = resources.getDrawable( icons_bold[piece.color]);
		resetLocalXY();
	}

	public PieceUI( Context context, Piece piece, int i, int j){
		this(context, piece);
		i0=i;
		j0=j;
		replace();
		setVisibility(INVISIBLE);
	}

	private void place(int i, int j){
		move(i,j);
		place();
	}

	public void place(int i, int j, boolean animate){
		place(i, j);
		if (animate) {
			this.startAnimation(animation);
		}
	}

	public void place(){
		movable=false;
		setVisibility(VISIBLE);
	}
	
	public void replace(){
		rotating=false;
		move(i0,j0);
	}
	

	public void move(int i, int j) {
		this.i=i;
		this.j=j;
		// Caution : Must invoque doLayout every time i and j is modified! invalidate() and onDraw() will operate only if "piece is in viewport". Which will be reevaluated with doLayout()! 
		doLayout();
		invalidate();
	}
	
	private void resetLocalXY(){
		localX=PADDING*size + footprint*size/2;
		localY=PADDING*size + footprint*size/2;
		if (footprint==4) localX += size;
		localY += 2*size;
	}
	
	public void swipe(int x) {
		swipeX = (x+size/2)/size;
		bringToFront();
		doLayout();
		invalidate();
	}

	private void doLayout() {
		FrameLayout.LayoutParams layout;
		if (j>20) {
			layout = new FrameLayout.LayoutParams(df*size, df*size, Gravity.TOP);
			layout.leftMargin = (i-1)*size;
			if (!moving) layout.leftMargin -= swipeX*size;
			layout.topMargin  = (j-1)*size;
		} else {
			layout = new FrameLayout.LayoutParams( 2*radius, 2*radius, Gravity.TOP);
			layout.leftMargin = (i-PADDING-1)*size;
			layout.topMargin  = (j-PADDING-1)*size;
			if (footprint<=2) {
				layout.leftMargin = (i-PADDING)*size;
				layout.topMargin  = (j-PADDING)*size;				
			}
			if (footprint==5) {
				layout.leftMargin = (i-PADDING-2)*size;
				layout.topMargin  = (j-PADDING-2)*size;				
			}
		}
		setLayoutParams( layout);		
	}
	

	/** 
	 * Caution : must invoke doLayout() before any invalidate() if i or j happened to be updated! As onDraw wont be called if piece is (was) out of  viewport. 
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (rotating) {
			m.setRotate(angle, radius, radius);
			canvas.concat(m);
		} else {
			doLayout();
		}
		gotCanvas(canvas);
		if (movable && j<20) {
			canvas.drawCircle(radius, radius, radius, paint);
			canvas.drawCircle(radius, size, size, paint);
			canvas.drawCircle(radius, 2*radius-size, size, paint);
			canvas.drawCircle(size, radius, size, paint);
			canvas.drawCircle(2*radius-size, radius, size, paint);
		}
		if (j>20 && footprint==1) {
			for (Square s : piece.squares()) add( s.i+1, s.j+1);
		} else {
			for (Square s : piece.squares()) add( s.i, s.j);			
		}
	}

	private void gotCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	private PieceUI add(int i, int j){
		GameView game = (GameView) this.getParent();
		if (game.lasts[piece.color] == this && this.j<=20) {
			square_bold.setBounds( new Rect((i+PADDING+oo)*size, (j+PADDING+oo)*size, (i+PADDING+oo+1)*size+1, (j+PADDING+oo+1)*size+1));
			square_bold.draw(canvas);
			return this;
		}
		if (this.j<=20) {
			square.setBounds( new Rect((i+PADDING+oo)*size+1, (j+PADDING+oo)*size+1, (i+PADDING+oo+1)*size, (j+PADDING+oo+1)*size));				
		} else {
			square.setBounds( new Rect((i+oo)*size+1, (j+oo)*size+1, (i+oo+1)*size, (j+oo+1)*size));
		}
		square.draw(canvas);
		return this;
	}

	public boolean onLongClick(View v) {
		if (!movable) return false;
		GameView game = (GameView) v.getParent();
		if (game.selected == null) {
			game.selected = this;
			return true;
		} else {
			if (!moving && !rotating) flip();
		}
		return false;
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO possible to hook it in lifecycle? onAttachedToWindow() is to early...
		if (statusBarHeight<0) {
			Rect decor = new Rect();
			((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(decor);
			statusBarHeight = decor.top;
			Log.i(tag, "status bar height is : " +  statusBarHeight);
		}
		
		GameView game = (GameView) getParent();
		int action = event.getAction();
		
		if (game.selected==null && !PreferenceManager.getDefaultSharedPreferences(context).getBoolean("ai", true) && piece.color!=game.ui.turn) {
			game.doTouch(event);
			return false;
		}
		
		if (game.selected==null) {
	    	if (action==MotionEvent.ACTION_DOWN) {
	    		rawX=event.getRawX();
	    		rawY=event.getRawY();
    			rotating=false;
				game.doTouch(event);
				return false;
	    	}
    		int dX = Float.valueOf( event.getRawX()-rawX).intValue();
    		int dY = Float.valueOf( event.getRawY()-rawY).intValue();
    		if ( movable==false || -dY < Math.abs(dX) ) { 
    			game.doTouch(event);
    			return false;
    		}
    		game.selected = this;
			moving = true;
			return false;
		}
    	if (action==MotionEvent.ACTION_DOWN) {
    		localX = (int)event.getX();
    		localY = (int)event.getY();
    		downX = localX/size;
    		downY = localY/size;
       		if ( willRotate()) {
    			rotating = true;
            	rDown = Math.toDegrees( Math.atan2(event.getX()-radius, radius-event.getY()));
    		} else {
    			rotating=false;
    		}
    		bringToFront();
    	}
    	if (action==MotionEvent.ACTION_MOVE && game.selected==this) {
    		bringToFront();
    		if (rotating) {
            	double r = Math.toDegrees( Math.atan2(event.getX()-radius, radius-event.getY()));
    			int a = Double.valueOf( r-rDown).intValue();
    			if (a>180) a-= 360;
    			if (a<-180) a+= 360;
    			if (angle==a) return false;
    			angle = a;
    		} else {
    			int r = (footprint%2==0 ? radius-size/2 : radius);
	    		int newi = ((int) event.getRawX() - localX  + r)/size;
	    		int newj = ((int) event.getRawY() - statusBarHeight - localY + r)/size;
	    		if (i==newi && j==newj) return false;
	    		i=newi;
	    		j=newj;
	    		moving = true;
    		}
    	}
    	if (action==MotionEvent.ACTION_UP) {
    		moving=false;
    		rotating=false;
    		rotateAgainstGrid();
    		angle=0;
    		resetLocalXY();
    		if (j>20) {
    			game.buttons.setVisibility( INVISIBLE);
    			this.replace();
    			game.selected=null;
    		} else {
    			game.buttons.setVisibility( VISIBLE);
    			game.buttons.bringToFront();
    			boolean okState = game.game.valid(piece, i, j) && !game.thinking;
    			game.buttons.setOkState( okState);
    			if (okState && vibrator!=null) vibrator.vibrate(20);
    		}
    	}
    	invalidate();
		return false;
	}

	private void rotateAgainstGrid(){
		if (angle>45) piece.rotate(1);
		if (angle>135) piece.rotate(1);
		if (angle<-45) piece.rotate(-1);
		if (angle<-135) piece.rotate(-1);
	}
	
	public void rotate(int dir) {
		piece.rotate(dir);
		invalidate();
	}

	public void flip() {
		piece.flip();
		invalidate();
	}
	
	private boolean willRotate(){
		int r = radius/size;
		if (Math.abs( downX-r)<= 1 &&  Math.abs(downY-1) <= 1 ) return true;
		if (Math.abs( downX-1)<= 1 &&  Math.abs(downY-r) <= 1 ) return true;
		if (Math.abs( downX-2*r+1)<= 1 &&  Math.abs(downY-r) <= 1 ) return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "<PieceUI> : (" + this.i + ", " + this.j + ") ; " + piece;
	}

	public int compareTo(PieceUI that) {
		return (2*this.piece.count + this.piece.size) - (2*that.piece.count + that.piece.size) ;
	}
}
