package com.TwentyCodes.android.ExceptionReportViewer;

import java.util.ArrayList;

import com.TwentyCodes.android.exception.ExceptionHandler;
import com.jakewharton.android.viewpagerindicator.TitlePageIndicator;
import com.jakewharton.android.viewpagerindicator.TitledFragmentAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * This is the main activity of this application. 
 * This application will be used to download and display the exception reports from a server.
 * 
 * TODO Create settings activity and implement the following settings
 * + production url
 * + testing url
 * + version preference
 * 
 * TODO Create icons for titled view pager
 * 
 * TODO Update ReportListFragment to a dynamically load 10 reports, and retrieve more when necessary.
 * This will probably also entail server side changes
 * 
 * @author ricky barrette
 */
public class Main extends FragmentActivity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.main);
        
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new ReportListFragment("http://powers.doesntexist.com:666/?get=1"));
        fragments.add(new ReportListFragment("http://powers.doesntexist.com:666/testing/?get=1"));
        
        //the icons for the pages go here 
        int[] icons = new int[]{
        		//TODO create icons and update
        		android.R.drawable.stat_sys_warning,
        		android.R.drawable.stat_sys_warning
        };
        
        //display the pages
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new TitledFragmentAdapter(this.getSupportFragmentManager(), fragments, this.getResources().getStringArray(R.array.titles), icons));

        //display the titles
        TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }
}