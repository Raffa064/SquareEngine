package com.raffa064.engine.ui;

import android.app.Activity;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import com.raffa064.engine.R;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

public class FloatBubble {
	private Activity activity;
	private View bubble;
	private ImageView bubbleToggler;
	private ScrollView bubbleScrollContainer;
	private LinearLayout bubbleContainer;
	private OnBubbleActionListener onBubbleActionListener;
	
	public FloatBubble(Activity activity) {
		this.activity = activity;
		
		inflate();
	}

	public void setOnBubbleActionListener(OnBubbleActionListener onBubbleActionListener) {
		this.onBubbleActionListener = onBubbleActionListener;
	}

	public OnBubbleActionListener getOnBubbleActionListener() {
		return onBubbleActionListener;
	}
	
	public void inflate() {
		bubble = activity.getLayoutInflater().inflate(R.layout.float_bubble, null);
		bubbleToggler = bubble.findViewById(R.id.float_bubble_toggler);
		bubbleScrollContainer = bubble.findViewById(R.id.float_bubble_scroll_container);
		bubbleContainer = bubble.findViewById(R.id.float_bubble_container);
		
		ViewUtils.gone(bubbleScrollContainer);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bubble.setLayoutParams(params);
		bubbleToggler.setOnTouchListener(new HoverListener(bubble));
		bubbleToggler.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ViewUtils.toggleVisibility(bubbleScrollContainer);
			}
		});
	}
	
	public void addIntoView(ViewGroup view) {
		view.addView(bubble);	
	}
	
	public void addAction(final int action, int resIcon, final String label) {
		final ImageButton button = new ImageButton(activity);
		button.setAdjustViewBounds(true);
		button.setScaleType(ScaleType.FIT_CENTER);
		button.setImageResource(resIcon);
		button.setBackgroundColor(0x00000000);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (onBubbleActionListener != null) {
					onBubbleActionListener.onAction(action);
				}
				
				ObjectAnimator anim = ObjectAnimator.ofFloat(button, "alpha", 1, 0, 1);
				anim.setInterpolator(new LinearInterpolator());
				anim.setDuration(280);
				anim.start();
			}
		});
		button.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				Toast.makeText(activity, label, Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		
		bubbleContainer.addView(button);
	}
	
	public static interface OnBubbleActionListener {
		public void onAction(int action);
	}
}
