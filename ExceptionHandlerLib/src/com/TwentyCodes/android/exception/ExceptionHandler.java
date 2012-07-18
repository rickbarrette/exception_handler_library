/**
 * ExceptionHandler.java
 * @date Feb 12, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.exception;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import anroid.v4.compat.NotificationCompat;

/**
 * This is Twenty Codes, LLC Exception Handler of Awesomeness!
 * This class will be used to generate reports that will be emailed to us via the users email client after the users approval
 * @author ricky barrette
 */
public class ExceptionHandler implements UncaughtExceptionHandler, Runnable {

	private static final String MSG_SUBJECT_TAG = "Exception Report";
	private static final String MSG_BODY = "Just click send to help make this application better. "+
			"No personal information is being sent (you can check by reading the rest of the email).";
	protected static final int SIMPLE_NOTFICATION_ID = 45684645;
	private final Thread.UncaughtExceptionHandler mDefaultUEH;
	private Activity mApp= null;
	private Service mService = null;
	private BroadcastReceiver mBroadcastReceiver = null;
	private final Context mContext;
	private Report mReport;
	private static final String TAG = "ExceptionHandler";
	private String mURL = null;
	private String mEmail;
	private String mAppName;
	private String mTracker;

	/**
	 * Creates a new ExceptionHandler
	 * @param app
	 * @author ricky barrette
	 */
	public ExceptionHandler(final Activity app) {
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		mApp = app;
		mContext = app;
		parseProperties();
	}

	/**
	 * Creates a new ExceptionHandler
	 * @param broadcastReceiver
	 * @author ricky barrette
	 */
	public ExceptionHandler(final BroadcastReceiver broadcastReceiver, final Context context){
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		mBroadcastReceiver = broadcastReceiver;
		mContext = context;
		parseProperties();
	}

	/**
	 * Creates a new ExceptionHandler
	 * @param service
	 * @author ricky barrette
	 */
	public ExceptionHandler(final Service service){
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		mService = service;
		mContext = service;
		parseProperties();
	}

	/**
	 * Generates an email from the report
	 * @author ricky barrette
	 */
	private void displayEmailNotification(){
		Log.i(TAG, "displayEmailNotification");

		CharSequence title = null;
		if(mApp != null)
			title = mApp.getTitle();

		if(mService != null)
			title = mService.getClass().getName();

		if(mBroadcastReceiver != null)
			title = mBroadcastReceiver.getClass().getName();

		final Intent intent = new Intent(Intent.ACTION_SEND);
		final String theSubject = title + " " + MSG_SUBJECT_TAG;
		final String theBody = "\n\n"+MSG_BODY+mReport.toString();
		intent.putExtra(Intent.EXTRA_EMAIL,new String[] {mEmail});
		intent.putExtra(Intent.EXTRA_TEXT, theBody);
		intent.putExtra(Intent.EXTRA_SUBJECT, theSubject);
		intent.setType("message/rfc822");

		displayNotification(intent);
	}

	/**
	 * displays an notification in the status bar, letting the user know that there was an issue
	 * @param generatedReportIntent
	 */
	private void displayNotification(final Intent generatedReportIntent) {
		Log.i(TAG, "displayNotification");
		final Context context = mContext.getApplicationContext();
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		final PendingIntent intent = PendingIntent.getActivity(context, 0, generatedReportIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentTitle(context.getString(R.string.crash))
			.setContentText(context.getString(R.string.sorry))
			.setTicker(context.getString(R.string.crash))
			.setSmallIcon(android.R.drawable.stat_notify_error)
			.setWhen(System.currentTimeMillis())		
			.setAutoCancel(true)
			.setContentIntent(intent);
		notificationManager.notify(SIMPLE_NOTFICATION_ID, builder.getNotification());		
	}

	/**
	 * parses in the exception handler options from the client application's assets folder. /assets/exceptionhandler.properties
	 * @author ricky barrette
	 */
	private void parseProperties() {
		final Resources resources = mContext.getResources();
		final AssetManager assetManager = resources.getAssets();
		// Read from the /assets directory
		try {
			final InputStream inputStream = assetManager.open("exceptionhandler.properties");
			final Properties properties = new Properties();
			properties.load(inputStream);
			mURL = properties.getProperty("server");
			mEmail = properties.getProperty("email");
			mAppName = properties.getProperty("app");
			mTracker = properties.getProperty("tracker");
		} catch (final IOException e) {
			Log.e(TAG, "Failed to open exceptionhandler.properties");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		if(mEmail == null)
			displayNotification(new Intent(mContext, ExceptionReportActivity.class).putExtra("report", mReport));
		else
			displayEmailNotification();
	}

	/**
	 * Called when there is an uncaught exception.
	 * (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 * @author ricky barrette
	 */
	@Override
	public void uncaughtException(final Thread t, final Throwable e) {
		Log.d(TAG, "uncaughtException()");

		//		Log.d(TAG,"mURL = "+ this.mURL);
		//		Log.d(TAG,"mEmail = "+ this.mEmail);

		final Date theDate = new Date();
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_zzz");
		final PackageManager pm = mContext.getPackageManager();

		//app environment;
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		} catch (final NameNotFoundException eNnf) {
			//doubt this will ever run since we want info about our own package
			pi = new PackageInfo();
			pi.versionName = "unknown";
			pi.versionCode = 69;
		}

		final StringBuffer report = new StringBuffer();
		for (final StackTraceElement item : e.getStackTrace())
			report.append("at "+item.toString() + "\n");

		final StringBuffer causereport = new StringBuffer();
		final Throwable cause = e.getCause();
		if (cause != null) {
			causereport.append(cause.toString() + "\n \n");
			for (final StackTraceElement item : cause.getStackTrace())
				causereport.append("at "+item.toString() + "\n");
		}

		//generate the report
		mReport = new Report(mURL).generateReport(e.toString(), report.toString(), causereport.toString(), sdf.format(theDate), Build.FINGERPRINT, pi.versionName+"b"+pi.versionCode, mAppName != null ? mAppName : mContext.getPackageName(), mTracker, mContext.getPackageName());

		//try to send file contents via email (need to do so via the UI thread)
		if(mApp != null)
			mApp.runOnUiThread(this);


		if(mService != null)
			if(mEmail == null)
				displayNotification(new Intent(mContext, ExceptionReportActivity.class).putExtra("report", mReport));
			else
				displayEmailNotification();

		if(mBroadcastReceiver != null)
			if(mEmail == null)
				displayNotification(new Intent(mContext, ExceptionReportActivity.class).putExtra("report", mReport));
			else
				displayEmailNotification();

		//do not forget to pass this exception through up the chain
		mDefaultUEH.uncaughtException(t,e);
	}
}