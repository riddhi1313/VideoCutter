package com.video.trimmercutter.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;

public class FileUtils {
	
	public static String getTargetFileName(String inputFileName) {
	//	final File file = new File(Environment.getExternalStorageDirectory()+"/VideoTrimmer").getAbsoluteFile();
		final File file = new File(inputFileName).getAbsoluteFile();
		final String fileName = file.getName();
		final File filetrimmer = new File(Environment.getExternalStorageDirectory()+"/VideoCutter").getAbsoluteFile();
		String[] filenames = filetrimmer.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename != null && filename.startsWith("cut-") && filename.endsWith(fileName);
			}
		});
		
		int count = 0;
		String targetFileName;
		List<String> fileList = Arrays.asList(filenames);
		
		do {
			targetFileName = "cut-" + String.format("%03d", count++)+ "-" + fileName; 
		} while(fileList.contains(targetFileName));
		
	//	return new File(file.getParent(), targetFileName).getPath();
		return new File(Environment.getExternalStorageDirectory()+"/VideoCutter", targetFileName).getPath();
	}

}
