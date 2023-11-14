package com.raffa064.engine.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class ViewUtils {
	public static void gone(View view) {
		view.setVisibility(View.GONE);
	}
	
	public static void invisible(View view) {
		view.setVisibility(View.INVISIBLE);
	} 
	
	public static void visible(View view) {
		view.setVisibility(View.VISIBLE);
	} 

	public static boolean isGone(View view) {
		return view.getVisibility() == View.GONE;
	}
	
	public static boolean isInvisible(View view) {
		return view.getVisibility() == View.INVISIBLE;
	}
	
	public static boolean isVisible(View view) {
		return view.getVisibility() == View.VISIBLE;
	}
	
    public static void toggleVisibility(View view) {
		if (isGone(view)) {
			visible(view);
			return;
		}

		gone(view);
	}
	
	public static ObjectAnimator floatPropAnim(String propName, View view, Listener listener, long duration,  float... values) {
		ObjectAnimator anim = ObjectAnimator.ofFloat(view, propName, values);
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(duration);
		
		if (listener != null) {
			anim.addListener(listener);
		}
		
		anim.start();
		
		return anim;
	}
	
	public static ObjectAnimator alpha(View view, Listener listener, long duration,  float... values) {
		return floatPropAnim("alpha", view, listener, duration, values);
	}
	
	public static class Listener implements AnimatorListener {
		@Override
		public void onAnimationStart(Animator animator) {
		}

		@Override
		public void onAnimationEnd(Animator animator) {
		}

		@Override
		public void onAnimationCancel(Animator animator) {
		}

		@Override
		public void onAnimationRepeat(Animator animator) {
		}
	}
}
