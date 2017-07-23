package com.video.trimmercutter.view;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.video.trimmercutter.R;


@SuppressLint("NewApi")
public class VideoViewActivity extends Activity implements
		OnSeekBarChangeListener {

	VideoView videoview;
	String videoPath = "";
	// MediaController media_Controller;
	Button btnDeleteVideo;
	Button btnPlayVideo;
	RelativeLayout flVideoView;
	ImageView ivScreen;
//	TextView tvCreatedDate;
	TextView tvVideoName;
	boolean isPlay = false;
	int pos = 0;
	Handler handler = new Handler();
	boolean isFromMain = false;
	SeekBar seekVideo;
	TextView tvStartVideo, tvEndVideo;
	int duration = 0;
	private String outputformat;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Intent intent = getIntent();
		videoPath = intent.getStringExtra("videourl");
		pos = intent.getIntExtra("position", 0);
		isFromMain = intent.getBooleanExtra("isfrommain", false);
		int vurlLen = videoPath.length();
		char ch = videoPath.charAt(vurlLen - 5);
	
		setContentView(R.layout.lay_video_view);

		// media_Controller = new MediaController(this);
		videoview = (VideoView) findViewById(R.id.vvScreen);
		seekVideo = (SeekBar) findViewById(R.id.sbVideo);
		seekVideo.setOnSeekBarChangeListener(this);
		tvStartVideo = (TextView) findViewById(R.id.tvStartVideo);
		tvEndVideo = (TextView) findViewById(R.id.tvEndVideo);
		  outputformat=videoPath.substring(videoPath.lastIndexOf(".")+1);
	   		File f1 = new File(videoPath);
	    	   MediaScannerConnection.scanFile(VideoViewActivity.this,
	    				new String[] {f1.getAbsolutePath()},
	    				new String[] { outputformat }, null);
		// videoview.setMediaController(media_Controller);
		videoview.setVideoPath(videoPath);
		videoview.setOnErrorListener(new OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				// TODO Auto-generated method stub
				
				return true;
			}
		});
		videoview.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				duration = videoview.getDuration();
				seekVideo.setMax(duration);
				tvStartVideo.setText("00:00");
				try {
					tvEndVideo.setText("" + formatTimeUnit(duration));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		videoview.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				videoview.setVisibility(View.GONE);
				ivScreen.setVisibility(View.VISIBLE);
				btnPlayVideo.setVisibility(View.VISIBLE);
				videoview.seekTo(0);
				seekVideo.setProgress(0);
				tvStartVideo.setText("00:00");
				handler.removeCallbacks(seekrunnable);
				isPlay = false;
			}
		});
//		btnShare = (Button) findViewById(R.id.btnShare);
//		btnShare.setOnClickListener(onclicksharevideo);
		ivScreen = (ImageView) findViewById(R.id.ivScreen);
		ivScreen.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoPath,
				Thumbnails.MINI_KIND));
		TextView titel=(TextView)findViewById(R.id.textView22);
		 Typeface font = Typeface.createFromAsset(getAssets(), "helvetica_neue_bold.ttf");  
	        Typeface font2 = Typeface.createFromAsset(getAssets(), "helvetica_normal.ttf");  
		btnPlayVideo = (Button) findViewById(R.id.btnPlayVideo);
		btnDeleteVideo = (Button) findViewById(R.id.btnDeleteVideo);
		btnDeleteVideo.setTypeface(font2);
//		llShareVideo = (LinearLayout) findViewById(R.id.llShareVideo);
		flVideoView = (RelativeLayout) findViewById(R.id.flVideoView);

//		tvCreatedDate = (TextView) findViewById(R.id.tvCreateDate);
		tvVideoName = (TextView) findViewById(R.id.tvVideoName);
		File f = new File(videoPath);
		if (f.exists()) {
			String arrName[] = f.getName().split("_", 2);
			tvVideoName.setText("" + arrName[0]);
		}
		String str = "";
		if (ch == 'l' || ch == 'r' || ch == 'p') {
			str = videoPath.substring(videoPath.length() - 25,
					videoPath.length() - 15);
		} else {
			str = videoPath.substring(videoPath.length() - 23,
					videoPath.length() - 13);
		}
		Log.i("path", "" + str + " " + videoPath.charAt(videoPath.length() - 5));
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd_MM_yyyy");
			Date formattedDate = null;
			try {
				formattedDate = df.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			SimpleDateFormat df1 = new SimpleDateFormat("dd MMMM yyyy");

//			tvCreatedDate.setText("Created : " + df1.format(formattedDate));
		} catch (Exception e) {

		}
		btnPlayVideo.setOnClickListener(onclickplayvideo);
		flVideoView.setOnClickListener(onclickplayvideo);
		btnDeleteVideo.setOnClickListener(onclickdeletevideo);
		btnDeleteVideo.setTypeface(font);
//		llShareVideo.setOnClickListener(onclicksharevideo);

	}

	// View.OnClickListener onclickshareMarket = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// try {
	// Intent i = new Intent(Intent.ACTION_SEND);
	// i.setType("text/plain");
	// i.putExtra(Intent.EXTRA_SUBJECT, "Video Maker");
	// String sAux = "\nCreate Video Story From Images and Music..\n\n";
	// sAux = sAux
	// + "https://play.google.com/store/apps/details?id=com.video.maker \n\n";
	// i.putExtra(Intent.EXTRA_TEXT, sAux);
	// startActivity(Intent.createChooser(i, "Share Application"));
	// } catch (Exception e) { // e.toString();
	// }
	// }
	//
	// };
	View.OnClickListener onclickplayvideo = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!isPlay) {
				videoview.seekTo(seekVideo.getProgress());
				videoview.start();
				handler.postDelayed(seekrunnable, 500);
				videoview.setVisibility(View.VISIBLE);
				ivScreen.setVisibility(View.GONE);
				btnPlayVideo.setVisibility(View.GONE);
			} else {
				videoview.pause();
				handler.removeCallbacks(seekrunnable);
				// videoview.setVisibility(View.GONE);
				 ivScreen.setVisibility(View.VISIBLE);
				btnPlayVideo.setVisibility(View.VISIBLE);
			}
			isPlay = !isPlay;

		}
	};
	View.OnClickListener onclicksharevideo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (videoview != null)
				if (videoview.isPlaying()) {
					videoview.pause();
					handler.removeCallbacks(seekrunnable);
				}
			Intent sharingIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"Video Maker");

			sharingIntent.setType("video/*");
			File newFile = new File(videoPath);
			sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM,
					Uri.fromFile(newFile));
			sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "video");
			startActivity(Intent
					.createChooser(sharingIntent, "Where to Share?"));

		}
	};
	ProgressDialog pd = null;
	View.OnClickListener onclickdeletevideo = new OnClickListener() {

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			if (videoview != null)
				videoview.pause();
			File appmusic = new File(videoPath);
			if (appmusic.exists()) {
				appmusic.delete();
			}
			String imgpath = Environment.getExternalStorageDirectory()
					.getPath()
					+ "/"
					+"VideoCutter";
			MediaScannerConnection.scanFile(VideoViewActivity.this,
					new String[] { videoPath }, null, null);
			pd = new ProgressDialog(VideoViewActivity.this);
			pd.setMessage("Deleting Video...");
			pd.show();
			LayoutInflater	infalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View vv=	infalter.inflate(R.layout.custom_dialog, null);
			final ViewHolder2	holder = new ViewHolder2();
//			holder.frameVidelistPlay = (FrameLayout) convertView
//					.findViewById(R.id.frameVidelistPlay);
//			holder.ivVideoThumb = (ImageView) convertView
//					.findViewById(R.id.ivVideolistImage);
			
			holder.ivVideoThumb = (ImageView) vv
					.findViewById(R.id.image);
			holder.ll=(LinearLayout)vv.findViewById(R.id.layout_root);
			holder.ll.setBackgroundResource(R.drawable.deleting);
        final AnimationDrawable    mAnimation = (AnimationDrawable) holder.ivVideoThumb.getDrawable();
        pd.setContentView(vv);
       
   	 holder.ivVideoThumb.setVisibility(View.VISIBLE);
            	 mAnimation.start();

			handler.postDelayed(runnable, 2000);

		}

	};

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			pd.dismiss();
			handler.removeCallbacks(runnable);
			if (!isFromMain) {
				Intent intent = new Intent(VideoViewActivity.this,VideoListActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
//				intent.putExtra("pos", pos);
//				setResult(RESULT_OK, intent);
				finish();
			} else {
				Intent in = new Intent(VideoViewActivity.this,
						start_activity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(in);
				finish();
			}
		}
	};
	public static String formatTimeUnit(long millis) throws ParseException {
		String formatted = String.format(
		"%02d:%02d",
		TimeUnit.MILLISECONDS.toMinutes(millis),
		TimeUnit.MILLISECONDS.toSeconds(millis)
		- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
		.toMinutes(millis)));
		return formatted;
		}
	

	Runnable seekrunnable = new Runnable() {
		public void run() {
			if (videoview.isPlaying()) {
				int curPos = videoview.getCurrentPosition();
				seekVideo.setProgress(curPos);
				try {
					tvStartVideo.setText("" + formatTimeUnit(curPos));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (curPos == duration) {
					// videoview.setVideoPath(videoPath);
					// if (videoview.isPlaying())
					// videoview.pause();
					seekVideo.setProgress(0);
					// btnPlayVideo.setVisibility(View.VISIBLE);
					tvStartVideo.setText("00:00");
					handler.removeCallbacks(seekrunnable);
				} else
					handler.postDelayed(seekrunnable, 500);

			} else {
				seekVideo.setProgress(duration);
				try {
					tvStartVideo.setText("" + formatTimeUnit(duration));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				handler.removeCallbacks(seekrunnable);
			}
		}
	};

	public void onBackPressed() {
		if (videoview != null && videoview.isPlaying())
			videoview.pause();
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			Intent in = new Intent(VideoViewActivity.this, start_activity.class);
			in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(in);
			finish();
	};

	@Override
	protected void onDestroy() {
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onDestroy();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = seekBar.getProgress();
		videoview.seekTo(progress);
		try {
			tvStartVideo.setText("" + formatTimeUnit(progress));
		} catch (ParseException e) {
			e.printStackTrace();
		}
//		videoview.start();
	}
}
