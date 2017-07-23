package com.video.trimmercutter.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.video.trimmercutter.R;

public class VideoGridAdapter extends BaseAdapter {

	private final Context mContext;
	private LayoutInflater infalter;
	ArrayList<VideoData> videoList = new ArrayList<VideoData>();

	public VideoGridAdapter(Context c, ArrayList<VideoData> vdata) {
		this.mContext = c;
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		videoList.addAll(vdata);

	}

	@Override
	public int getCount() {
		return videoList.size();
	}

	@Override
	public Object getItem(int position) {
		return videoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = infalter.inflate(R.layout.row_video_list, null);
			holder = new ViewHolder();
			holder.frameVidelistPlay = (FrameLayout) convertView
					.findViewById(R.id.frameVidelistPlay);
			holder.ivVideoThumb = (ImageView) convertView
					.findViewById(R.id.ivVideolistImage);
			holder.tvVideoName = (TextView) convertView
					.findViewById(R.id.tvVideolistname);
			holder.btnPlayVideo = (Button) convertView
					.findViewById(R.id.btnVidelistPlay);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.ivVideoThumb.setImageBitmap(null);
		holder.ivVideoThumb.setImageBitmap(videoList.get(position).bmp);
		// new AsyncThumbnailLoader(holder.ivVideoThumb, position).execute();
		holder.btnPlayVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((VideoListActivity) mContext).callVideo(position);
			}
		});
		holder.frameVidelistPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((VideoListActivity) mContext).callVideo(position);
			}
		});
		holder.tvVideoName.setText("" + videoList.get(position).videoName);
		return convertView;
	}

	private class ViewHolder {
		ImageView ivVideoThumb;
		TextView tvVideoName;
		Button btnPlayVideo;
		FrameLayout frameVidelistPlay;
	}

	// private class AsyncThumbnailLoader extends AsyncTask<Object, Void,
	// Bitmap> {
	//
	// private ImageView imageView;
	// int position;
	//
	// public AsyncThumbnailLoader(ImageView imageView, int position) {
	// this.imageView = imageView;
	// this.position = position;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	//
	// }
	//
	// @Override
	// protected Bitmap doInBackground(Object... params) {
	//
	// return ThumbnailUtils.createVideoThumbnail(
	// Utils.videoList.get(position).videoPath,
	// Thumbnails.MINI_KIND);
	// }
	//
	// @Override
	// protected void onPostExecute(Bitmap result) {
	// imageView.setImageBitmap(result);
	// }
	// }
}
