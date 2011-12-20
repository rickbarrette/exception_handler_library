/**
 * ReportAdapter.java
 * @date Dec 19, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.ExceptionReportViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * This adaptor will be used to populate a listview with report titles provided
 * a JSONArray
 * 
 * @author ricky barrette
 */
public class ReportAdapter extends BaseAdapter {

	private static final String TAG = "ReportAdapter";
	private JSONArray mReports;
	private LayoutInflater mInflater;

	/**
	 * Creates a new ReportAdator
	 * 
	 * @author ricky barrette
	 */
	public ReportAdapter(Context context, JSONArray reports) {
		mReports = reports;
		mInflater = LayoutInflater.from(context);
	}

	/**
	 * returns the amount of reports in the reports JSONArray (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return mReports.length();
	}

	/**
	 * returns the report at index
	 * 
	 * @param index
	 *            (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public JSONObject getItem(int position) {
		try {
			return mReports.getJSONObject(position);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * this method will be used to populate the views (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		/*
		 * here we will only create new views when needed, and recycle old ones
		 * when provided
		 */
		Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new Holder();
			holder.id = (TextView) convertView.findViewById(R.id.id);
			holder.msg = (TextView) convertView.findViewById(R.id.msg);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.app = (TextView) convertView.findViewById(R.id.app);
			
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		/*
		 * here we will populate the views
		 */
		JSONObject report = null;
		try {
			report = getItem(position).getJSONObject("report");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		Log.d(TAG, report.toString());

		try {
			holder.id.setText(report.getString("id"));
		} catch (JSONException e) {
			holder.id.setText(e.getMessage());
		}
		try {
			holder.msg.setText(report.getString("msg"));
		} catch (JSONException e) {
			holder.msg.setText(e.getMessage());
		}
		try {
			holder.status.setText(report.getString("status"));
		} catch (JSONException e) {
			holder.status.setText(e.getMessage());
		}
		try {
			holder.app.setText(report.getString("app"));
		} catch (JSONException e) {
			holder.app.setText(e.getMessage());
		}

		return convertView;
	}

	/**
	 * This simple class will be used to hold views, so they can be recycled
	 * 
	 * @author ricky barrette
	 */
	class Holder {
		TextView id;
		TextView status;
		TextView msg;
		TextView app;
	}

}
