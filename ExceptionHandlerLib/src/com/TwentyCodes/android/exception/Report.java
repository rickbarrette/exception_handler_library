/**
 * Report.java
 * @date May 29, 2011
 * @author Twenty Codes, LLC
 * @author ricky barrette
 */
package com.TwentyCodes.android.exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class will be used to generate a report, and insert it into our exception report database
 * @author ricky barrette
 */
public class Report implements Parcelable{

	private final String mUrl;
	private ArrayList<ReportItem> mReport;

	public static final Parcelable.Creator<Report> CREATOR = new Parcelable.Creator<Report>() {
		@Override
		public Report createFromParcel(final Parcel in) {
			return new Report(in);
		}

		@Override
		public Report[] newArray(final int size) {
			return new Report[size];
		}
	};

	/**
	 * Creates a new Report
	 * @param in
	 * @author ricky barrette
	 */
	public Report(final Parcel in){
		mUrl = in.readString();
		mReport = new ArrayList<ReportItem>();
		in.readTypedList(mReport, ReportItem.CREATOR);
	}

	/**
	 * Creates a new Report
	 * @author ricky barrette
	 */
	public Report(final String url) {
		//		Log.d(TAG, url);
		mUrl = url;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * Files the report with the remote database
	 * @author ricky barrette
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @return String result
	 */
	public String file() throws ClientProtocolException, IOException{
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpPost httppost = new HttpPost(mUrl);
		httppost.setEntity(new UrlEncodedFormEntity(getNameValuePairs()));
		//return the results
		final HttpResponse response = httpclient.execute(httppost);
		final HttpEntity entity = response.getEntity();
		final InputStream is = entity.getContent();

		final BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
		final StringBuilder sb = new StringBuilder();
		sb.append(reader.readLine() + "\n");
		String line="0";
		while ((line = reader.readLine()) != null)
			sb.append(line + "\n");
		is.close();
		reader.close();
		return sb.toString();
	}

	/**
	 * Generates a report to be displayed form a downloaded JSON object
	 * @param report
	 * @return
	 * @author ricky barrette
	 * @throws JSONException
	 */
	@SuppressWarnings("rawtypes")
	public Report generateReport(final JSONObject report) throws JSONException{
		mReport = new ArrayList<ReportItem>();
		final Iterator iter = report.keys();
		while(iter.hasNext()){
			final String key = (String)iter.next();
			mReport.add(new ReportItem(key , report.getString(key)));
		}
		return this;
	}

	/**
	 * Generates a report to be sent.
	 * @param msg
	 * @param stackTrace
	 * @param cause
	 * @param device
	 * @param appVersion
	 * @param app
	 * @return this
	 * @author ricky barrette
	 */
	public Report generateReport(final String msg, final String stackTrace, final String cause, final String date, final String device, final String appVersion, final String app, final String tracker, final String packageName){
		mReport = new ArrayList<ReportItem>();
		mReport.add(new ReportItem("app",app));
		mReport.add(new ReportItem("version",appVersion));
		mReport.add(new ReportItem("date",date));
		mReport.add(new ReportItem("msg",msg));
		mReport.add(new ReportItem("stackTrace",stackTrace));
		mReport.add(new ReportItem("cause",cause));
		mReport.add(new ReportItem("device",device));
		mReport.add(new ReportItem("tracker",tracker));
		mReport.add(new ReportItem("package",packageName));
		return this;
	}

	/**
	 * Extracts the name value pairs from the report bundle
	 * @return
	 * @author ricky barrette
	 */
	private ArrayList<NameValuePair> getNameValuePairs() {
		final ArrayList<NameValuePair> list = new ArrayList<NameValuePair>();
		for(final ReportItem entry : mReport)
			list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		return list;
	}

	/**
	 * @return the generated exception report
	 * @author ricky barrette
	 */
	public ArrayList<NameValuePair> getReport(){
		return getNameValuePairs();
	}

	/**
	 * Sets the optional users description of what happened
	 * @param string
	 * @author ricky barrette
	 */
	public Report setDescription(final String description) {
		mReport.add(new ReportItem("description", description));
		return this;
	}

	/**
	 * @return a human readable string of this report
	 * @author ricky barrette
	 */
	@Override
	public String toString(){
		final StringBuilder s = new StringBuilder();
		for(final ReportItem item : mReport){
			s.append("\n\n-----"+ item.getKey()+"-----");
			s.append("\n"+item.getValue());
		}
		return s.toString();
	}

	@Override
	public void writeToParcel(final Parcel out, final int flags) {
		out.writeString(mUrl);
		out.writeTypedList(mReport);
	}
}