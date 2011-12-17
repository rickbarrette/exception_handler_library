/**
 * ReportItem.java
 * @date July 13, 2010
 * @author Twenty Codes, LLC
 * @author ricky barrette
 */
package com.TwentyCodes.android.exception;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class will represent an individual report item. The ReportItems will be used in an array list that will be passed via intent.
 * This will allow for our report items to stay in the proper order.
 * @author ricky
 */
public class ReportItem implements Parcelable {

	public static final Parcelable.Creator<ReportItem> CREATOR = new Parcelable.Creator<ReportItem>() {
		public ReportItem createFromParcel(Parcel in) {
			return new ReportItem(in);
		}
		
		public ReportItem[] newArray(int size) {
			return new ReportItem[size];
		}
	};
	
	private String mKey;
	private String mValue;
	
	/**
	 * Creates a new ReportItem
	 * @author ricky barrette
	 */
	public ReportItem(String key, String value) {
		this.mKey = key;
		this.mValue = value;
	}
	
	/**
	 * Creates a new ReportItem from a parcel
	 * @param in
	 */
	public ReportItem(Parcel in){
		this.mKey = in.readString();
		this.mValue = in.readString();
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mKey);
		dest.writeString(this.mValue);
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return this.mKey;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return this.mValue;
	}

}