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
import android.app.Notification;
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
	private Thread.UncaughtExceptionHandler mDefaultUEH;
	private Activity mApp= null;
	private Service mService = null;
	private BroadcastReceiver mBroadcastReceiver = null;
	private Context mContext;
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
	public ExceptionHandler(Activity app) {
		this.mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	    this.mApp = app;
	    this.mContext = app;
	    parseProperties();
	 }

	/**
	 * Creates a new ExceptionHandler
	 * @param broadcastReceiver
	 * @author ricky barrette
	 */
	public ExceptionHandler(BroadcastReceiver broadcastReceiver, Context context){
		this.mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		this.mBroadcastReceiver = broadcastReceiver;
		this.mContext = context;
		parseProperties();
	}
	
	/**
	 * Creates a new ExceptionHandler
	 * @param service
	 * @author ricky barrette
	 */
	public ExceptionHandler(Service service){
		this.mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		this.mService = service;
		this.mContext = service;
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
		
		Intent intent = new Intent(Intent.ACTION_SEND);
		String theSubject = title + " " + MSG_SUBJECT_TAG;
		String theBody = "\n\n"+MSG_BODY+this.mReport.toString();
		intent.putExtra(Intent.EXTRA_EMAIL,new String[] {this.mEmail});
		intent.putExtra(Intent.EXTRA_TEXT, theBody);
		intent.putExtra(Intent.EXTRA_SUBJECT, theSubject);
		intent.setType("message/rfc822");
		
		displayNotification(intent);
	}

	/**
	 * displays an notification in the status bar, letting the user know that there was an issue
	 * @param generatedReportIntent
	 */
	private void displayNotification(Intent generatedReportIntent) {
		Log.i(TAG, "displayNotification");
		Context context = mContext.getApplicationContext();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notifyDetails = new Notification(android.R.drawable.stat_notify_error, context.getString(R.string.sorry), System.currentTimeMillis());
		PendingIntent intent = PendingIntent.getActivity(context, 0, generatedReportIntent, android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
		notifyDetails.setLatestEventInfo(context, context.getString(R.string.crash), context.getString(R.string.sorry), intent);
		notifyDetails.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(SIMPLE_NOTFICATION_ID, notifyDetails);
	}

	/**
	 * parses in the exception handler options from the client application's assets folder. /assets/exceptionhandler.properties
	 * @author ricky barrette
	 */
	private void parseProperties() {
		Resources resources = this.mContext.getResources();
		AssetManager assetManager = resources.getAssets();
		// Read from the /assets directory
		try {
			InputStream inputStream = assetManager.open("exceptionhandler.properties");
		    Properties properties = new Properties();
		    properties.load(inputStream);
		    this.mURL = properties.getProperty("server") + properties.getProperty("file");
		    this.mEmail = properties.getProperty("email");
		    this.mAppName = properties.getProperty("app");
		    this.mTracker = properties.getProperty("tracker");
		} catch (IOException e) {
		    Log.e(TAG, "Failed to open exceptionhandler.properties");
		    e.printStackTrace();
		}
	}
	
	public void run() {
		if(this.mEmail == null)
			displayNotification(new Intent(this.mContext, ExceptionReportActivity.class).putExtra("report", this.mReport));
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
	public void uncaughtException(Thread t, Throwable e) {
		Log.d(TAG, "uncaughtException()");
		
//		Log.d(TAG,"mURL = "+ this.mURL);
//		Log.d(TAG,"mEmail = "+ this.mEmail);

		Date theDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss_zzz");
		PackageManager pm = mContext.getPackageManager();
		
		//app environment;
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
		} catch (NameNotFoundException eNnf) {
			//doubt this will ever run since we want info about our own package
			pi = new PackageInfo();
			pi.versionName = "unknown";
			pi.versionCode = 69;
		}
		
		StringBuffer report = new StringBuffer();
		for (StackTraceElement item : e.getStackTrace()) 
			report.append("at "+item.toString() + "\n");
		
		StringBuffer causereport = new StringBuffer();
		Throwable cause = e.getCause();
		if (cause != null) {
			causereport.append(cause.toString() + "\n \n");
			for (StackTraceElement item : cause.getStackTrace())
				causereport.append("at "+item.toString() + "\n");
		}
		
		//generate the report
		this.mReport = new Report(mURL).generateReport(e.toString(), report.toString(), causereport.toString(), sdf.format(theDate), Build.FINGERPRINT, pi.versionName+"b"+pi.versionCode, mAppName != null ? mAppName : mContext.getPackageName(), this.mTracker, mContext.getPackageName());
		
		//try to send file contents via email (need to do so via the UI thread)
		if(this.mApp != null){
			this.mApp.runOnUiThread(this);
		}
				
		
		if(this.mService != null){
			if(this.mEmail == null)
					displayNotification(new Intent(this.mContext, ExceptionReportActivity.class).putExtra("report", this.mReport));
			else
				displayEmailNotification();
		}
		
		if(this.mBroadcastReceiver != null){
			if(this.mEmail == null)
				displayNotification(new Intent(this.mContext, ExceptionReportActivity.class).putExtra("report", this.mReport));
			else
				displayEmailNotification();
		}
		
		//do not forget to pass this exception through up the chain
		mDefaultUEH.uncaughtException(t,e);
	}
}