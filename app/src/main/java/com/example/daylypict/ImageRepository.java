package com.example.daylypict;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.Environment;

public class ImageRepository {
	
	private Context mContext;
	
	public ImageRepository(Context context)
	{
		mContext = context;
	}

	private File getStoragePath()
	{
	    File storageDir = null;
	    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	    	storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/dailyPics/");
	    else
	    	storageDir = new File(mContext.getFilesDir().getAbsolutePath() + "/dailyPics/");

	    return storageDir;
	}
	
	public File createImageFile()
	{
		//TODO
		// Create an image file name
	    try {
		    SimpleDateFormat timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
		    String imageFileName = timeStamp.format(new Date(0));
	
		    File storageDir = getStoragePath();
		    storageDir.mkdirs();
		    
			File image = File.createTempFile(
			    imageFileName,  /* prefix */
			    ".jpg",         /* suffix */
			    storageDir      /* directory */
			);
			
			return image;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}

	    // Save a file: path for use with ACTION_VIEW intents
	    return null;
	}
	
	public File[] getPhotoList()
	{
	    File storageDir = getStoragePath();
	    
	    try {
	    	
	    	return storageDir.listFiles();
	    				
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    return null;
	}
}
