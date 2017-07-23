/**
 * 
 */
package com.video.trimmercutter.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.video.trimmercutter.R;
import com.video.trimmercutter.util.TimeUtils;

public final class VideoCursorAdapter extends ResourceCursorAdapter {
	VideoCursorAdapter(VideoActivity videoActivity, Context context, int layout, Cursor c) {
		super(context, layout, c);

	}

	@SuppressLint("NewApi")
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView videoPreview = (ImageView) view.findViewById(R.id.image_preview);
		Bitmap thumbnail = Thumbnails.getThumbnail(context.getContentResolver(), getInt(cursor, MediaStore.Video.Media._ID), MediaStore.Video.Thumbnails.MICRO_KIND,new BitmapFactory.Options());
		videoPreview.setBackgroundDrawable(new BitmapDrawable(thumbnail));
		
		String fileName = getString(cursor, MediaStore.Video.Media.DISPLAY_NAME);
		Log.i("VideoCursorAdapter", "Binding view for : "+fileName);
		
		TextView fileNameView = (TextView) view.findViewById(R.id.file_name);
		fileNameView.setText(fileName);
		if(cursor.getPosition()%2==0){
			view.setBackgroundResource(R.drawable.light_bg);
		}else{
			view.setBackgroundResource(R.drawable.dark_bg);

		}
		
		TextView durationView = (TextView) view.findViewById(R.id.duration);
		durationView.setText("Duretion: "+getTime(cursor, MediaStore.Video.Media.DURATION));//
		durationView.bringToFront();
		
	}

	private int getInt(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndexOrThrow(columnName);
		return cursor.getInt(index);
	}

	private String getString(Cursor cursor, String columnName) {
		int index = cursor.getColumnIndexOrThrow(columnName);
		
		return cursor.getString(index);
	}
	
	private String getTime(Cursor cursor, String columnName) {
		int time = getInt(cursor, columnName);
		return TimeUtils.toFormattedTime(time);
	}
}