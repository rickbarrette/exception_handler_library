/**
 * ExceptionActivity.java
 * @date May 31, 2011
 * @author Twenty Codes, LLC
 * @author ricky barrette
 */
package com.TwentyCodes.android.exception;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;

/**
 * This activity will be used to present the user with the exception report, and allows them to send it, or not
 * @author ricky barrette
 */
public class ExceptionReportActivity extends Activity implements OnClickListener {

	private static final String TAG = "ExceptionActivity";
	private Report mReport;

	/**
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * @author ricky barrette
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		this.mReport = (Report) this.getIntent().getParcelableExtra("report");
		
		if(this.getIntent().hasExtra("display"))
			this.setContentView(R.layout.list);
		else {
			this.setContentView(R.layout.exception_activity);
			this.findViewById(R.id.send).setOnClickListener(this);
		}
		ListView list = (ListView) this.findViewById(android.R.id.list);
		list.setAdapter(new ReportAdapter(this, this.mReport.getReport()));
		list.setClickable(false);
	}

	/**
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * @author ricky barrette
	 */
	@Override
	public void onClick(View v) {
		EditText description = (EditText) findViewById(R.id.description);
		this.mReport.setDescription(description.getText().toString());
		v.setEnabled(false);
		final ProgressDialog progress = ProgressDialog.show(this, "", getString(R.string.sending), true, true);
		new Thread( new Runnable(){
			@Override
			public void run(){
				Looper.prepare();
				try {
					Log.d(TAG, ExceptionReportActivity.this.mReport.file());
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				progress.dismiss();
				ExceptionReportActivity.this.finish();
			}
		}).start();
	}
}