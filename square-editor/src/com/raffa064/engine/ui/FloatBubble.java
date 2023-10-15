package com.raffa064.engine.ui;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import com.raffa064.engine.R;
import android.view.View.OnClickListener;

public class FloatBubble {
	private Activity activity;
	private View bubble;
	private ImageView bubbleToggler;
	private ScrollView bubbleScrollContainer;

	public FloatBubble(Activity activity) {
		this.activity = activity;
		
		inflate();
	}
	
	public void inflate() {
		bubble = activity.getLayoutInflater().inflate(R.layout.float_bubble, null);
		bubbleToggler = bubble.findViewById(R.id.float_bubble_toggler);
		bubbleScrollContainer = bubble.findViewById(R.id.float_bubble_scroll_container);
		
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		bubble.setLayoutParams(params);
		bubbleToggler.setOnTouchListener(new HoverListener(bubble));
		bubbleToggler.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View p1) {
				ViewUtils.toggleVisibility(bubbleScrollContainer);
			}
		});
	}
	
	public void addIntoView(ViewGroup view) {
		view.addView(bubble);	
	}
}
