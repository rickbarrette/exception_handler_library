/**
 * ExceptionActivity.java
 * @date May 31, 2011
 * @author Twenty Codes, LLC
 * @author ricky barrette
 */
package com.TwentyCodes.android.exception;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		this.startService(new Intent(this, ReportPostingService.class).putExtra("report", this.mReport));
		this.finish();
	}
}