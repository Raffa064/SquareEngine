package com.raffa064.engine.ui;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.raffa064.engine.R;
import android.animation.Animator;

public class FloatNotifications {
	private Activity activity;
	private ScrollView scrollContainer;
	private LinearLayout container;

	public FloatNotifications(Activity activity, ScrollView scrollContainer, LinearLayout container) {
		this.activity = activity;
		this.scrollContainer = scrollContainer;
		this.container = container;
	}

	private void removeNotification(final View notification) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ViewUtils.alpha(notification, new ViewUtils.Listener() {
					@Override
					public void onAnimationEnd(Animator animator) {
						container.removeView(notification);
					}
				}, 280, 1, 0);
			}
		});
	}
	
	public Thread deleteAfterDelay(final View notification) {
		Thread deleteThread = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					return; // Interrupted
				}

				removeNotification(notification);
			}
		};
		
		deleteThread.start();
		
		return deleteThread;
	}
	
	public void createNotification(String title, String message) {
		final View notification = activity.getLayoutInflater().inflate(R.layout.float_notification, null);
		final TextView notificationTitle = notification.findViewById(R.id.float_notification_title);
		final TextView notificationMessage = notification.findViewById(R.id.float_notification_message);

		notificationTitle.setText(title);
		notificationMessage.setText(message);
		
		ViewUtils.gone(notificationMessage);
		
		final Thread[] thread = new Thread[1]; // I use this array to access and modify the thread inside it
		thread[0] = deleteAfterDelay(notification);
		
		notification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (thread[0].isAlive()) {
					thread[0].interrupt();
				}
				
				ViewUtils.toggleVisibility(notificationMessage);
				
				if (ViewUtils.isGone(notificationMessage)) {
					thread[0] = deleteAfterDelay(notification);
				}
			}
		});
		
		notification.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View view) {
				if (thread[0].isAlive()) {
					thread[0].interrupt();
				}
				
				removeNotification(notification);
				return true;
			}
		});
		
		container.addView(notification);
		scrollContainer.scrollTo(0, scrollContainer.getBottom());
		
		ViewUtils.alpha(notification, null, 280, 0, 1);
	}
}
