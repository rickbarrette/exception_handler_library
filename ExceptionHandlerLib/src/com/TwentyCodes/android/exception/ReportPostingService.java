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
import android.app.PendingIntent;
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
	private Intent mIntent;
	private boolean hasErrored = false;

	/**
	 * Fires of a notification based upon api level
	 * @param title
	 * @param contentText
	 * @param ticker
	 * @param icon
	 * @param intent
	 * @param isOngoing
	 * @author ricky barrette
	 */
	@SuppressWarnings("deprecation")
	private void fireNotification(String title, String contentText, String ticker, int icon, Intent intent, boolean isOngoing) {
		PendingIntent pendingIntent = null;
		if(intent != null)
			pendingIntent = PendingIntent.getService(this.getApplicationContext(), 0, intent, 0);
		/*
		 * Use the appropriate notificafation methods
		 */
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) > 11){
			Builder builder = new Notification.Builder(this.getApplicationContext())
				.setContentTitle(title)
				.setContentText(contentText)
				.setTicker(ticker)
				.setSmallIcon(icon)
				.setWhen(System.currentTimeMillis());
			if(isOngoing)
				builder.setOngoing(true);
			else
				builder.setAutoCancel(true);
			if (intent != null)
				builder.setContentIntent(pendingIntent);
			mNotificationManager.notify(NOTIFICATION_ID, builder.getNotification());
		} else {
			Notification notification = new Notification(icon, title , System.currentTimeMillis());
			if(isOngoing)
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
			else
				notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.setLatestEventInfo(this.getApplicationContext(), title, contentText, pendingIntent);
			mNotificationManager.notify(NOTIFICATION_ID, notification);
		}
	}
	
	/**
	 * Extracts the report object from the intent
	 * @param intent
	 * @author ricky barrette
	 */
	private void getReport(Intent intent) {
		mReport = (Report) intent.getParcelableExtra("report");
	}

	/**
	 * notifiys the user that we are sending a report
	 * @author ricky barrette
	 */
	private void notifyError() {
		fireNotification(getString(R.string.reporting_error), 
				getString(R.string.reporting_error_msg), 
				getString(R.string.reporting_error_msg), 
				android.R.drawable.stat_notify_error, 
				new Intent(this.getApplicationContext(), ReportPostingService.class).putExtras(mIntent), 
				false);
	}
	
	/**
	 * notifiys the user that we are sending a report
	 * @author ricky barrette
	 */
	private void notifySending() {
		fireNotification(getString(R.string.sending), 
				getString(R.string.sending_report), 
				getString(R.string.sending), 
				android.R.drawable.stat_sys_upload, 
				null, 
				true);
		
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
		
		notifySending();
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
		if (hasErrored)
			notifyError();
		super.onDestroy();
	}

	/**
	 * (non-Javadoc)
	 * @see android.app.Service#onStart(android.content.Intent, int)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		mStartId = startId;
		getReport(intent);
		postReport();
		mIntent = intent;
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
		mIntent = intent;
		return super.onStartCommand(intent, Service.START_STICKY, startId);
	}

	/**
	 * Posts a copy of the report to the report handing server
	 * @author ricky barrette
	 */
	private void postReport(){
		if(!isStarted){
			isStarted = true;
			try {
				Log.d(TAG, mReport.file());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				hasErrored = true;
			} catch (IOException e) {
				e.printStackTrace();
				hasErrored = true;
			} finally {
				ReportPostingService.this.stopSelf(mStartId);					
			}
		}
	}
}
