package com.video.trimmercutter.view;


import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.video.trimmercutter.R;
import com.video.trimmercutter.model.VideoPlayerState;
import com.video.trimmercutter.service.VideoTrimmingService;
import com.video.trimmercutter.util.FileUtils;
import com.video.trimmercutter.util.TimeUtils;

public class ViewVideo extends Activity {
	protected final int LOADING_DIALOG = 1;

	boolean ok=false;

	private VideoView videoView;
	private TextView detailView;
    View videoControlBtn;
	private VideoPlayerState videoPlayerState = new VideoPlayerState();

	protected String outputformat;
	
	protected Handler completionHander = new Handler() {
		@SuppressLint("NewApi")
		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {
			Log.i("VideoView", "Recieved message");
			String s[]=msg.getData().getString("text").split("VideoCutter");
			videoPlayerState.setMessageText(s[0]+"VideoCutter");
			removeDialog(LOADING_DIALOG);

			  outputformat=path.substring(path.lastIndexOf(".")+1);
		   		File f1 = new File(path);
		    	   MediaScannerConnection.scanFile(ViewVideo.this,
		    				new String[] {f1.getAbsolutePath()},
		    				new String[] { outputformat }, null);

			Intent intent=new Intent(ViewVideo.this,VideoViewActivity.class);
			intent.putExtra("videourl", path);
			intent.putExtra("isfrommain", true);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		
//			showDialog(MESSAGE_DIALOG);
			ok=true;
			stopService(videoTrimmingServiceIntent());
			System.exit(0);
		}
	};
	private TextView textViewLeft;
	private TextView textViewRight;
	private WakeLock wl;
	private PowerManager pm;
    VideoSliceSeekBar videoSliceSeekBar;
	private TextView textName;
	String path=null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("VideoView", "In on create");
		setContentView(R.layout.cut_view);
	     textViewLeft = (TextView) findViewById(R.id.left_pointer);
	        textViewRight = (TextView) findViewById(R.id.right_pointer);
		     textName = (TextView) findViewById(R.id.textfilename);
		     TextView titel = (TextView) findViewById(R.id.textView1);
		     Typeface font = Typeface.createFromAsset(getAssets(), "helvetica_neue_bold.ttf");  
		        Typeface font2 = Typeface.createFromAsset(getAssets(), "helvetica_normal.ttf");  
		     

	        videoControlBtn = findViewById(R.id.buttonply);
	        
	        videoSliceSeekBar = (VideoSliceSeekBar) findViewById(R.id.seek_bar);
	        
	        
	        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	   			 wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
		Object lastState = getLastNonConfigurationInstance();
		if (lastState != null) {
			videoPlayerState = (VideoPlayerState) lastState;
		} else {
			Bundle extras = getIntent().getExtras();
			videoPlayerState.setFilename(extras.getString("videofilename"));
			path=extras.getString("videofilename");
			String s[]=extras.getString("videofilename").split("/");
			textName.setText(s[s.length-1]);

		}
		videoView = (VideoView) findViewById(R.id.videoView1);
		videoView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
		
				if(plypush){
					videoView.pause();
					plypush=false;
					videoControlBtn.setVisibility(View.VISIBLE);
				}
				return true;
			}
		});
        initVideoView();


		Button trimButton = (Button) findViewById(R.id.trimbut);
		trimButton.setOnClickListener(trimClickListener());
		trimButton.setTypeface(font);
	}

	
	@Override
	public Object onRetainNonConfigurationInstance() {
		Log.i("VideoView", "In on retain");
		return videoPlayerState;
	}
	 String startTime="00";
	   String endTime;
	    private void initVideoView() {
	        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	            @Override
	            public void onPrepared(MediaPlayer mp) {
	                videoSliceSeekBar.setSeekBarChangeListener(new VideoSliceSeekBar.SeekBarChangeListener() {
	                    @Override
	                    public void SeekBarValueChanged(int leftThumb, int rightThumb) {
	                    	int selected=videoSliceSeekBar.getSelectedThumb();
	                		if(selected==1){       
	                			videoView.seekTo(videoSliceSeekBar.getLeftProgress());
	                }
	                        textViewLeft.setText(getTimeForTrackFormat(leftThumb, true));
	                        textViewRight.setText(getTimeForTrackFormat(rightThumb, true));
	                        startTime=getTimeForTrackFormat(leftThumb, true);
	                	videoPlayerState.setStart(leftThumb);

//	                				refreshDetailView();
	                        endTime=getTimeForTrackFormat(rightThumb, true);
		                	videoPlayerState.setStop(rightThumb);

	                    }
	                });
	                endTime=getTimeForTrackFormat(mp.getDuration(), true);
	                videoSliceSeekBar.setMaxValue(mp.getDuration());
	                videoSliceSeekBar.setLeftProgress(0);
	                videoSliceSeekBar.setRightProgress(mp.getDuration());
	                videoSliceSeekBar.setProgressMinDiff(0);

	                videoControlBtn.setOnClickListener(new View.OnClickListener() {
	                    @Override
	                    public void onClick(View v) {
	                    	if(!plypush)
	                    	{
	                    		videoControlBtn.setVisibility(View.GONE);
	                    		plypush=true;
	                    	}else {
	                    		videoControlBtn.setVisibility(View.VISIBLE);
	                    		plypush=false;
							}
	                        performVideoViewClick();
	                    }
	                });
	            }
	        });
	        videoView.setVideoPath(videoPlayerState.getFilename());
	        
            endTime=getTimeForTrackFormat(videoView.getDuration(), true);

	    }
	   Boolean plypush=false;

	    private void performVideoViewClick() {
	        if (videoView.isPlaying()) {
	            videoView.pause();
	            videoSliceSeekBar.setSliceBlocked(false);
	            videoSliceSeekBar.removeVideoStatusThumb();
	        } else {
	            videoView.seekTo(videoSliceSeekBar.getLeftProgress());
	            videoView.start();
//	            videoSliceSeekBar.setSliceBlocked(true);
	            videoSliceSeekBar.videoPlayingProgress(videoSliceSeekBar.getLeftProgress());
	            videoStateObserver.startVideoProgressObserving();
	        }
	    }


	    private StateObserver videoStateObserver = new StateObserver();

	    private class StateObserver extends Handler {

	        private boolean alreadyStarted = false;

	        private void startVideoProgressObserving() {
	            if (!alreadyStarted) {
	                alreadyStarted = true;
	                sendEmptyMessage(0);
	            }
	        }
	        private Runnable observerWork = new Runnable() {
	            @Override
	            public void run() {
	                startVideoProgressObserving();
	            }
	        };

	        @Override
	        public void handleMessage(Message msg) {
	            alreadyStarted = false;
	            videoSliceSeekBar.videoPlayingProgress(videoView.getCurrentPosition());
	            if (videoView.isPlaying() && videoView.getCurrentPosition() < videoSliceSeekBar.getRightProgress()) {
	                postDelayed(observerWork, 50);
	            } else {

	                if (videoView.isPlaying()){ 
	                	videoView.pause();
	                	videoControlBtn.setVisibility(View.VISIBLE);
	                	plypush=false;}

	                videoSliceSeekBar.setSliceBlocked(false);
	                videoSliceSeekBar.removeVideoStatusThumb();
	            }
	        }
	    }
	    public static String getTimeForTrackFormat(int timeInMills, boolean display2DigitsInMinsSection) {
	    	int hour=(timeInMills / (60 *60* 1000));
	        int minutes = (timeInMills / (60 * 1000));
	        int seconds = (timeInMills - minutes * 60 * 1000) / 1000;
	        String result = display2DigitsInMinsSection && hour < 10 ? "0" : "";
	        result += hour + ":";

	         result += display2DigitsInMinsSection && minutes < 10 ? "0" : "";

	        result += minutes%60 + ":";
	        if (seconds < 10) {
	            result += "0" + seconds;
	        } else {
	            result += seconds;
	        }
	        return result;
	    }								

	@Override
	protected void onResume() {
		super.onResume();
		wl.acquire();
		
		Log.i("VideoView", "In on resume");
		videoView.seekTo(videoPlayerState.getCurrentTime());	
	}


	@Override
	protected void onPause() {
		wl.release();
		super.onPause();
		Log.i("VideoView", "In on pause");

		videoPlayerState.setCurrentTime(videoView.getCurrentPosition());
	}

	private OnClickListener trimClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoView.pause();
				
				if(!videoPlayerState.isValid()) {
					return;
				}
				Intent intent = videoTrimmingServiceIntent();

				String inputFileName = videoPlayerState.getFilename();
				intent.putExtra("inputFileName", inputFileName);
				path=FileUtils.getTargetFileName(inputFileName);
				intent.putExtra("outputFileName", path);
				intent.putExtra("start", videoPlayerState.getStart()/1000);
				intent.putExtra("duration", videoPlayerState.getDuration()/1000);

				intent.putExtra("messenger", new Messenger(completionHander));
				
				startService(intent);
				showDialog(LOADING_DIALOG);
			}
		};
	}

	private Intent videoTrimmingServiceIntent() {
		return new Intent(ViewVideo.this, VideoTrimmingService.class);
	}
	
	private OnClickListener resetClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoPlayerState.reset();
				refreshDetailView();
			}
		};
	}

	private OnClickListener stopClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int stop = videoView.getCurrentPosition();
				videoPlayerState.setStop(stop);
				refreshDetailView();
			}
		};
	}

	private OnClickListener startClickListener() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				videoPlayerState
						.setStart(videoView.getCurrentPosition());
				refreshDetailView();
			}
		};
	}



	@Override
	protected Dialog onCreateDialog(int id) {
		Log.i("VideoView", "In on create dialog");

		Dialog dialog;

		switch (id) {
	
		case LOADING_DIALOG:
			dialog = ProgressDialog.show(ViewVideo.this, "", "Trimming...",	true, true);
			dialog.setCancelable(false);
			final ImageView im=(ImageView)findViewById(R.id.image);
		LayoutInflater	infalter = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View v=	infalter.inflate(R.layout.custom_dialog, null);
					final ViewHolder	holder = new ViewHolder();

					holder.ivVideoThumb = (ImageView) v
							.findViewById(R.id.image);
	            dialog.setCancelable(false);
	            final AnimationDrawable    mAnimation = (AnimationDrawable) holder.ivVideoThumb.getDrawable();
     			dialog.setContentView(v);
	           
	                	 holder.ivVideoThumb.setVisibility(View.VISIBLE);
	                	 mAnimation.start();
			break;
	
		default:
			dialog = null;
		}

		return dialog;
	}


	private Dialog simpleAlertDialog(String message) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setCancelable(true)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@SuppressLint("NewApi")
							public void onClick(DialogInterface dialog, int id) {
								ViewVideo.this.removeDialog(LOADING_DIALOG);
								if(ok){}else{
									finish();
								}
								
							}
						});
		dialog = builder.create();
		return dialog;
	}

	private void refreshDetailView() {
		String start = TimeUtils.toFormattedTime(videoPlayerState.getStart());
		String stop = TimeUtils.toFormattedTime(videoPlayerState.getStop());
		detailView.setText("Start at " + start + "\nEnd at " + stop);
	}
}
class ViewHolder {
	ImageView ivVideoThumb;
	TextView tvVideoName;

}