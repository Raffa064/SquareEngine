package com.raffa064.engine.ui;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.raffa064.engine.R;
import android.widget.ScrollView;

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
				container.removeView(notification);
			}
		});
	}
	
	public void createNotification(String title, String message) {
		final View notification = activity.getLayoutInflater().inflate(R.layout.float_notification, null);
		final TextView notificationTitle = notification.findViewById(R.id.float_notification_title);
		final TextView notificationMessage = notification.findViewById(R.id.float_notification_message);

		notificationTitle.setText(title);
		notificationMessage.setText(message);
		
		ViewUtils.gone(notificationMessage);
		
		final Thread deleteAfterDelay = new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					return; // Interrupted
				}
				
				removeNotification(notification);
			}
		};
		
		notification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (deleteAfterDelay.isAlive()) {
					deleteAfterDelay.interrupt();
				}
				
				ViewUtils.toggleVisibility(notificationMessage);
			}
		});
		
		notification.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View view) {
				removeNotification(notification);
				return true;
			}
		});
		
		container.addView(notification);
		scrollContainer.scrollTo(0, scrollContainer.getBottom());
		
		deleteAfterDelay.start();
	}
}
