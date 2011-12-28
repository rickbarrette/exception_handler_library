/**
 * JSONLoader.java
 * @date Dec 28, 2011
 * @author ricky barrette
 * @author Twenty Codes, LLC
 */
package com.TwentyCodes.android.ExceptionReportViewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

/**
 * This Loader will be used to download and parse a JSON object from the internet
 * @author ricky barrette
 */
public class JSONLoader extends Loader<JSONObject> {

	private LoaderCallbacks<JSONObject> mListener;
	private String mUrl;
	private InputStream mContent;

	/**
	 * Creates an new JSONLoader
	 * @param url 
	 * @param listener
	 * @param context
	 * @author ricky barrette
	 */
	public JSONLoader(String url, LoaderManager.LoaderCallbacks<JSONObject> listener, Context context) {
		super(context);
		mListener = listener;
		mUrl = url;
	}
	
	/**
	 * (non-Javadoc)
	 * @see android.support.v4.content.Loader#onAbandon()
	 */
	@Override
	protected void onAbandon() {
		super.onAbandon();
		if(mContent != null)
			try {
				mContent.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	/**
	 * (non-Javadoc)
	 * @see android.support.v4.content.Loader#onForceLoad()
	 */
	@Override
	protected void onForceLoad() {
		// TODO Auto-generated method stub
		super.onForceLoad();
	}

	/**
	 * (non-Javadoc)
	 * @see android.support.v4.content.Loader#onReset()
	 */
	@Override
	protected void onReset() {
		super.onReset();
		if(mContent != null)
			try {
				mContent.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(mListener != null)
			mListener.onLoaderReset(this);
	}

	/**
	 * (non-Javadoc)
	 * @see android.support.v4.content.Loader#onStartLoading()
	 */
	@Override
	protected void onStartLoading() {
		super.onStartLoading();
		if(mListener != null)
			try {
				mListener.onLoadFinished(this, new JSONObject(downloadJSON(mUrl)));
			} catch (IllegalStateException e) {
				mListener.onLoadFinished(this, null);
			} catch (ClientProtocolException e) {
				mListener.onLoadFinished(this, null);
			} catch (JSONException e) {
				mListener.onLoadFinished(this, null);
			} catch (IOException e) {
				mListener.onLoadFinished(this, null);
			} catch (NullPointerException e) {
				mListener.onLoadFinished(this, null);
			}

	}

	/**
	 * (non-Javadoc)
	 * @see android.support.v4.content.Loader#onStopLoading()
	 */
	@Override
	protected void onStopLoading() {
		super.onStopLoading();
		if(mContent != null)
			try {
				mContent.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		mContent = new DefaultHttpClient().execute(new HttpGet(url)).getEntity().getContent();
		BufferedReader br = new BufferedReader(new InputStreamReader(mContent));
		String buff = null;
		while ((buff = br.readLine()) != null){
			System.out.print(buff);
			response.append(buff);
		}
		return response.toString();
	}

}
