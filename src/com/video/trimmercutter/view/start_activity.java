package com.video.trimmercutter.view;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.video.trimmercutter.R;

public class start_activity extends Activity 
{
	Button start,marge;
	private PowerManager pm;
	private WakeLock wl;
	 @Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.home_activity);
	        TextView titel=(TextView)findViewById(R.id.tvVideolistname);
	        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			 wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
	        start = (Button) findViewById(R.id.button1);
	        marge = (Button) findViewById(R.id.button2);
	        Typeface font = Typeface.createFromAsset(getAssets(), "helvetica_neue_bold.ttf");  
	        Typeface font2 = Typeface.createFromAsset(getAssets(), "helvetica_normal.ttf");  
	        marge.setTypeface(font);

	        start.setTypeface(font);
	        marge.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent vdeolist = new Intent(start_activity.this,VideoListActivity.class);
					startActivity(vdeolist);
									}
			});
	      
	        start.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent vdeolist = new Intent(start_activity.this,VideoActivity.class);
					startActivity(vdeolist);
									}
			});
	        
	    }
	 
	 @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		 wl.acquire();
		super.onResume();
	}
	 @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		 wl.release();

		super.onPause();
	}
	
}
