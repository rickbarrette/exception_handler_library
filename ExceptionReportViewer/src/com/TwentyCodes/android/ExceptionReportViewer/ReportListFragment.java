/**
 * ReportListFragment.java
 * @date Dec 19, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.ExceptionReportViewer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.TwentyCodes.android.exception.ExceptionReportActivity;
import com.TwentyCodes.android.exception.Report;

/**
 * This fragment will be used to display a list of exception reports to the user
 * @author ricky barrette
 */
public class ReportListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<JSONObject> {

	private JSONArray mReports;
	private String mUrl;

	/**
	 * Creates a new ReportListFragment
	 * @param url of server
	 * @author ricky barrette
	 */
	public ReportListFragment(final String url) {
		super();
		mUrl = url;
	}

	/**
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.setListShown(false);
		this.getListView().setCacheColorHint(Color.TRANSPARENT);
		getLoaderManager().initLoader(0, null, this);
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

	/**
	 * Called when the loader is created
	 * (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int, android.os.Bundle)
	 */
	@Override
	public Loader<JSONObject> onCreateLoader(int id, Bundle args) {
		return new JSONLoader(mUrl, this, this.getActivity());
	}

	/**
	 * Called when the loader has finished
	 * (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android.support.v4.content.Loader, java.lang.Object)
	 */
	@Override
	public void onLoadFinished(Loader<JSONObject> loader, JSONObject jsonObject) {
		if(jsonObject != null){
			try {
				mReports = jsonObject.getJSONArray("reports");
				this.setListAdapter(new ReportAdapter(this.getActivity(), mReports));
			} catch (JSONException e) {
				this.setEmptyText(getText(R.string.there_was_an_error));
			}
			this.setEmptyText(getText(R.string.there_was_an_error));
		}
		
	}

	/**
	 * Called when the loader has been reset
	 * (non-Javadoc)
	 * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android.support.v4.content.Loader)
	 */
	@Override
	public void onLoaderReset(Loader<JSONObject> loader) {
		//not Needed
	}
}