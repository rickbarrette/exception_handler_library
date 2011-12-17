/**
 * ReportAdapter.java
 * @date May 31, 2011
 * @author Twenty Codes, LLC
 * @author ricky barrette
 */
package com.TwentyCodes.android.exception;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.TwentyCodes.android.exception.R;

/**
 * This class will be used to populate a custom Listview used to display the Generated exception report
 * @author ricky barrette
 */
public class ReportAdapter extends BaseAdapter {

	private ArrayList<NameValuePair> mReport;
	private LayoutInflater mInflater;

	/**
	 * Creates a new ReportAdapter
	 * @author ricky barrette
	 */
	public ReportAdapter(Context context, ArrayList<NameValuePair> report) {
		super();
		// Cache the LayoutInflate to avoid asking for a new one each time.
        this.mInflater = LayoutInflater.from(context);
		this.mReport = report;
	}

	/**
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 * @author ricky barrette
	 */
	@Override
	public int getCount() {
		return this.mReport.size();
	}

	/**
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 * @author ricky barrette
	 */
	@Override
	public NameValuePair getItem(int index) {
		return this.mReport.get(index);
	}

	/**
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 * @author ricky barrette
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * 
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		 // A ViewHolder keeps references to children views to avoid unnecessary calls to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
        	convertView = mInflater.inflate(R.layout.exception_list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.exception_title);
            holder.body = (TextView) convertView.findViewById(R.id.exception_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        /*
         * Bind the data efficiently with the holder.
         */
        holder.title.setText(getItem(position).getName());
        holder.body.setText(getItem(position).getValue());
        
        return convertView;
	}
	
	class ViewHolder {
        TextView title;
        TextView body;
    }

}
