package org.scoutant.blokish;

import org.scoutant.blokish.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;

/** 
 * An adaptation from OsmAnd project, credits to http://wiki.openstreetmap.org/wiki/OsmAnd, license GPL.
 * And I do publish present adaptation with same license, at http://github.com/scoutant.  
 */
public class BusyIndicator {
	private View view;
	private Handler uiHandler;
	private boolean visible=false;
	private Drawable drawable;
	private RotateAnimation animation;
	
	public BusyIndicator(Context ctx, View view){
		this.view = view;
		view.setVisibility(View.INVISIBLE);
		uiHandler = new Handler();
		this.drawable =  ctx.getResources().getDrawable(R.drawable.spinner_blue_76);	
		animation = new RotateAnimation(0, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setRepeatCount(Animation.INFINITE);
		final int cycles = 12;
		animation.setInterpolator(new Interpolator(){
			public float getInterpolation(float input) {
				return ((int)(input * cycles)) / (float) cycles;
			}
		});
		animation.setDuration(1800);
		animation.setStartTime(RotateAnimation.START_ON_FIRST_FRAME);
		animation.setStartOffset(0);
	}
	
	public void show(){
		this.visible = true;
		uiHandler.post(new Runnable(){
			public void run() {
				view.setVisibility( View.VISIBLE);
				if(BusyIndicator.this.visible){
//					view.setBackgroundDrawable(drawable);
					view.setBackground(drawable);
					if(view.getAnimation() == null){
						view.startAnimation(animation);
					}
				}
			}
		});
	}
	
	public void hide(){
		this.visible = false;
		uiHandler.post(new Runnable(){
			public void run() {
				view.setVisibility(View.INVISIBLE);
				if(view.getAnimation() != null){
					view.clearAnimation();
				}
			}
		});
	}
}
