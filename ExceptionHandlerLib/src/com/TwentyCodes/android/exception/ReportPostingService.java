/**
 * ReportPostingService.java
 * @date Feb 29, 2012
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.exception;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This service will allow the exception handler to post reports in the backgound,
 * allowing the user to do what ever they want
 * @author ricky barrette
 */
public class ReportPostingService extends Service {
	
	public static final int NOTIFICATION_ID = 1973646478;
	private NotificationManager mNotificationManager;
	private static final String TAG = "ReportPostingService";
	private int mStartId;
	private Report mReport;
	private boolean isStarted;

	/**
	 * Extracts the report object from the intent
	 * @param intent
	 * @author ricky barrette
	 */
	private void getReport(Intent intent) {
		mReport = (Report) intent.getParcelableExtra("report");
	}
	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Unused
		return null;
	}

	/**
	 * Called when the service is being created
	 * Here we want to display a notifcation,
	 * to inform the user what we are doing.
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		Context context = this.getApplicationContext();
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Builder builder = new Notification.Builder(context)
			.setContentTitle(getText(R.string.sending))
			.setContentText(getText(R.string.sending_report))
			.setTicker(getText(R.string.sending))
			.setOngoing(true)
			.setSmallIcon(android.R.drawable.stat_sys_upload)
			.setWhen(System.currentTimeMillis());
		mNotificationManager.notify(NOTIFICATION_ID, builder.getNotification());
		super.onCreate();
	}

	/**
	 * Called when the service is being destroyed
	 * Here we want to dismiss the notifications
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		mNotificationManager.cancel(NOTIFICATION_ID);
		super.onDestroy();
	}

	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		mStartId = startId;
		getReport(intent);
		postReport();
		super.onStart(intent, startId);
	}

	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mStartId = startId;
		getReport(intent);
		postReport();
		return super.onStartCommand(intent, Service.START_STICKY, startId);
	}

	/**
	 * Posts a copy of the report to the report handing server
	 * @author ricky barrette
	 */
	private void postReport(){
		if(!isStarted){
			isStarted = true;
			new Thread(new Runnable() {
				@Override
				public void run(){
					try {
						Log.d(TAG, mReport.file());
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						ReportPostingService.this.stopSelf(mStartId);					
					}
				}
			}).start();
		}
	}
}
