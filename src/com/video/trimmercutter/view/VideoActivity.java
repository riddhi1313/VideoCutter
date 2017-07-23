package com.video.trimmercutter.view;

import java.io.File;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.video.trimmercutter.R;

public class VideoActivity extends Activity {
	Cursor videocursor;
	ListView videogrid;
	ProgressDialog dilog;
	private PowerManager pm;
	private WakeLock wl;
	private File f;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.main);
		f= new File(Environment.getExternalStorageDirectory()+"/VideoCutter");
		TextView tv=(TextView)findViewById(R.id.tvVideolistname2);
        Typeface font = Typeface.createFromAsset(getAssets(), "helvetica_neue_bold.ttf");  
        Typeface font2 = Typeface.createFromAsset(getAssets(), "helvetica_normal.ttf");  
		
		if(!f.exists())
		{	f.mkdirs();}
		dilog = new ProgressDialog(VideoActivity.this);
		dilog.setMessage("Loding...");
		dilog.setCancelable(false);
	
	     pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				 wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");

		String[] parameters = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION,
				MediaStore.Video.Media.DATE_ADDED,
				};

		videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				parameters, null, null, MediaStore.Video.Media.DATE_TAKEN
						+ " DESC");

		videogrid = (ListView) findViewById(R.id.VideogridView);

		ListAdapter resourceCursorAdapter = new VideoCursorAdapter(this, this, R.layout.video_preview, videocursor) ;
			
		videogrid.setAdapter(resourceCursorAdapter);
		videogrid.setOnItemClickListener(videogridlistener);
	}
	@Override
	protected void onResume() {
		wl.acquire();
		super.onResume();
	}
@Override
protected void onPause() {
	// TODO Auto-generated method stub
	wl.release();
	super.onPause();
}
	private OnItemClickListener videogridlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			dilog.show();
			LayoutInflater	infalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v=	infalter.inflate(R.layout.custom_dialog, null);
			final ViewHolder2	holder = new ViewHolder2();
//			holder.frameVidelistPlay = (FrameLayout) convertView
//					.findViewById(R.id.frameVidelistPlay);
//			holder.ivVideoThumb = (ImageView) convertView
//					.findViewById(R.id.ivVideolistImage);
			
			holder.ivVideoThumb = (ImageView) v
					.findViewById(R.id.image);
			holder.ll=(LinearLayout)v.findViewById(R.id.layout_root);
			holder.ll.setBackgroundResource(R.drawable.loading);
        final AnimationDrawable    mAnimation = (AnimationDrawable) holder.ivVideoThumb.getDrawable();
			dilog.setContentView(v);
       
   	 holder.ivVideoThumb.setVisibility(View.VISIBLE);
            	 mAnimation.start();

			Cursor cursor = (Cursor) parent.getItemAtPosition(position);
			
			int fileNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			String filename = cursor.getString(fileNameIndex );
			Intent intent = new Intent(VideoActivity.this, ViewVideo.class);
			intent.putExtra("videofilename", filename);
			startActivity(intent);
		}
	};
	@Override
	protected void onStop() {
		if(dilog.isShowing()){
			dilog.dismiss();
		}
		super.onStop();
	};
}
class ViewHolder2 {
	ImageView ivVideoThumb;
	TextView tvVideoName;
	LinearLayout ll;
//	Button btnPlayVideo;
//	FrameLayout frameVidelistPlay;
}