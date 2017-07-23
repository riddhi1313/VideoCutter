package com.video.trimmercutter.view;


import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.video.trimmercutter.R;


@TargetApi(Build.VERSION_CODES.ECLAIR)
public class VideoListActivity extends Activity {

	ListView lvVideoList;
	VideoGridAdapter adapter;
	ArrayList<VideoData> videoList = new ArrayList<VideoData>();
	private static final int BACK_FROM_VIDEOSHARE = 99;

//	@Override
//	public void onStart() {
//		super.onStart();
//		EasyTracker.getInstance(this).activityStart(this); // Add this method.
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		EasyTracker.getInstance(this).activityStop(this); // Add this method.
//	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_video_list);
		TextView titel=(TextView)findViewById(R.id.tvt);
		 Typeface font = Typeface.createFromAsset(getAssets(), "helvetica_neue_bold.ttf");  
	        Typeface font2 = Typeface.createFromAsset(getAssets(), "helvetica_normal.ttf");  
		FindbyId();
		if (videoList.size() > 0)
			videoList.clear();
		new loadVideo().execute();
	}

	private void FindbyId() {
		lvVideoList = (ListView) findViewById(R.id.lvVideoList);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private boolean getVideoList() {
		final Uri MEDIA_EXTERNAL_CONTENT_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		final String _ID = MediaStore.Video.Media._ID;
		final String MEDIA_DATA = MediaStore.Video.Media.DATA;
		final String _NAME = MediaStore.Video.Media.DISPLAY_NAME;
		Cursor cursor;
		String[] proj = { MEDIA_DATA, _ID, _NAME };
		cursor = managedQuery(
				MEDIA_EXTERNAL_CONTENT_URI,
				proj,
				MEDIA_DATA + " like ? ",
				new String[] { "%"
						+ "VideoCutter"
						+ "%" }, " " + _ID + " DESC");
		int count = cursor.getCount();

		if (count > 0) {
			int columnIndex = cursor.getColumnIndex(_ID);
			cursor.moveToFirst();
			for (int i = 0; i < count; i++) {
				VideoData vdata = new VideoData();
				long id = cursor.getLong(cursor.getColumnIndex(_ID));
				vdata.videoId = id;
				int nameIndex = cursor.getColumnIndex(_NAME);
				vdata.videoName = cursor.getString(nameIndex);
				columnIndex = cursor.getColumnIndex(MEDIA_DATA);
				vdata.videoPath = cursor.getString(columnIndex);
				File f = new File(vdata.videoPath);
				MediaScannerConnection.scanFile(VideoListActivity.this,
						new String[] { f.getAbsolutePath() },
						new String[] { "mp4" }, null);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(
						getContentResolver(), id,
						MediaStore.Video.Thumbnails.MINI_KIND, options);
				vdata.bmp = curThumb;
				curThumb = null;
				videoList.add(vdata);
				cursor.moveToNext();
			}
			return true;
		}
		return false;
	}

	public void callVideo(int position) {
		Intent intent = new Intent(VideoListActivity.this,
				VideoViewActivity.class);
		intent.putExtra("videourl", videoList.get(position).videoPath);
		intent.putExtra("position", position);
		intent.putExtra("isfrommain", false);
		startActivityForResult(intent, BACK_FROM_VIDEOSHARE);
	}

	@SuppressLint("NewApi")
	private class loadVideo extends AsyncTask<Void, Void, Boolean> {

		ProgressDialog pd = null;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(VideoListActivity.this);
			pd.setMessage("Loading...");
			pd.setCancelable(false);
			pd.show();
			LayoutInflater	infalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v=	infalter.inflate(R.layout.custom_dialog, null);
			final ViewHolder2	holder = new ViewHolder2();
			holder.ivVideoThumb = (ImageView) v
					.findViewById(R.id.image);
			holder.ll=(LinearLayout)v.findViewById(R.id.layout_root);
			holder.ll.setBackgroundResource(R.drawable.loading);
        final AnimationDrawable    mAnimation = (AnimationDrawable) holder.ivVideoThumb.getDrawable();
        pd.setContentView(v);
       
   	 holder.ivVideoThumb.setVisibility(View.VISIBLE);
            	 mAnimation.start();		}

		@Override
		protected Boolean doInBackground(Void... params) {

			return getVideoList();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			pd.dismiss();
			if (result) {
				adapter = new VideoGridAdapter(VideoListActivity.this,videoList);
				lvVideoList.setAdapter(adapter);
			}
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case BACK_FROM_VIDEOSHARE:
				videoList.remove(data.getIntExtra("pos", 0));
				adapter.notifyDataSetChanged();
				break;

			}
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent intent = new Intent(VideoListActivity.this, start_activity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
