/**
 * ReportListFragment.java
 * @date Dec 19, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.ExceptionReportViewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.TwentyCodes.android.exception.ExceptionReportActivity;
import com.TwentyCodes.android.exception.Report;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This fragment will be used to display a list of exception reports to the user
 * @author ricky barrette
 */
public class ReportListFragment extends ListFragment {

		protected static final int DOWNLOADED_REPORTS = 0;
	protected static final int ERROR = 1;
	private JSONArray mReports;

	/**
	 * Creates a new ReportListFragment
	 * @param url of server
	 * @author ricky barrette
	 */
	public ReportListFragment(final String url) {
		
		final Handler handler = new Handler(){

			/**
			 * (non-Javadoc)
			 * @see android.os.Handler#handleMessage(android.os.Message)
			 */
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
					case DOWNLOADED_REPORTS:
					try {
						parseJSON((String) msg.obj);
					} catch (JSONException e) {
						ReportListFragment.this.setEmptyText(e.getMessage());
						e.printStackTrace();
					}
						break;
					case ERROR:
						ReportListFragment.this.setEmptyText((String) msg.obj);
						break;
						
				}
				super.handleMessage(msg);
			}
			
		};
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				/*
				 * Here we will try to download and parse the reports from the server
				 * if there is any errors, the user is notified via the list's empty text view
				 */
				try {
					handler.sendMessage(handler.obtainMessage(DOWNLOADED_REPORTS, downloadJSON(url)));
				} catch (IllegalStateException e) {
					handler.sendMessage(handler.obtainMessage(ERROR, e.getMessage()));
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					handler.sendMessage(handler.obtainMessage(ERROR, e.getMessage()));
					e.printStackTrace();
				} catch (IOException e) {
					handler.sendMessage(handler.obtainMessage(ERROR, e.getMessage()));
					e.printStackTrace();
				}
			
			}
		}).start();
	}

	/**
	 * parses the JSON reports and displays them in a list
	 * @param json
	 * @throws JSONException
	 * @author ricky barrette
	 */
	private void parseJSON(String json) throws JSONException {
		mReports = new JSONObject(json).getJSONArray("reports");
		this.setListAdapter(new ReportAdapter(this.getActivity(), mReports));
	}

	/**
	 * Downloads exception report JSON from the Internet 
	 * @param url
	 * @return
	 * @throws IllegalStateException
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @author ricky barrette
	 */
	private String downloadJSON(String url) throws IllegalStateException, ClientProtocolException, IOException {
		if(url == null)
			throw new NullPointerException();
		StringBuffer response = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent()));
		String buff = null;
		while ((buff = br.readLine()) != null){
			System.out.print(buff);
			response.append(buff);
		}
		return response.toString();
	}
	
	/**
	 * Called when the user selects a report to display
	 * (non-Javadoc)
	 * @see android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView, android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		try {
			this.startActivity(new Intent(this.getActivity(), ExceptionReportActivity.class)
				.putExtra("display", true)
				.putExtra("report", new Report("").generateReport(mReports.getJSONObject(position).getJSONObject("report"))));
		} catch (JSONException e) {
			Toast.makeText(this.getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}