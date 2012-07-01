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
public final class ReportItem implements Parcelable {

	public static final Parcelable.Creator<ReportItem> CREATOR = new Parcelable.Creator<ReportItem>() {
		@Override
		public ReportItem createFromParcel(final Parcel in) {
			return new ReportItem(in);
		}

		@Override
		public ReportItem[] newArray(final int size) {
			return new ReportItem[size];
		}
	};

	private final String mKey;
	private final String mValue;

	/**
	 * Creates a new ReportItem from a parcel
	 * @param in
	 */
	public ReportItem(final Parcel in){
		mKey = in.readString();
		mValue = in.readString();
	}

	/**
	 * Creates a new ReportItem
	 * @author ricky barrette
	 */
	public ReportItem(final String key, final String value) {
		mKey = key;
		mValue = value;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return mKey;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return mValue;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(mKey);
		dest.writeString(mValue);
	}

}