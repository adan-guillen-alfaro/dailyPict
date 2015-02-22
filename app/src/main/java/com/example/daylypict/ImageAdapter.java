package com.example.daylypict;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private List<Uri> mThumbIds;

	// Store the list of image IDs
	public ImageAdapter(Context c) {
		mContext = c;
		mThumbIds = new ArrayList<Uri>();
	}

	// Return the number of items in the Adapter
	@Override
	public int getCount() {
		return mThumbIds.size();
	}

	// Return the data item at position
	@Override
	public Object getItem(int position) {
		if (mThumbIds.size() > position)
			return mThumbIds.get(position);
		else
			return null;
	}

	// Will get called to provide the ID that
	// is passed to OnItemClickListener.onItemClick()
	@Override
	public long getItemId(int position) {
		//return mThumbIds.get(position);
		return position;
	}
	
	public void addItem(Uri uriFile)
	{
		mThumbIds.add(uriFile);
	}
	
	public boolean loadLibrary()
	{
		boolean result = false;
		
		mThumbIds.removeAll(mThumbIds);
		
		ImageRepository repository = new ImageRepository(mContext);
		File files[] = repository.getPhotoList();
		if (files != null)
		{
			for (int i = 0 ; i < files.length ; i++)
			{
				addItem(Uri.fromFile(files[i]));
				result = true;
			}
		}
		
		return result;
	}

	// Return an ImageView for each item referenced by the Adapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View grid = null;
		LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
		{
			grid = new View(mContext);
			grid = inflater.inflate(R.layout.grid_item, null);
		}
		else
			grid = convertView;
		
		if (grid != null)
		{
			TextView textView = (TextView)grid.findViewById(R.id.grid_text);
			ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);
			
			imageView.setImageResource(R.drawable.ic_launcher);

			int defaultWidth = 80;
			textView.setText("");
			
			Uri item = (Uri)getItem(position);
			if (item != null)
			{
				File f = new File(item.getPath());
				textView.setText(f.getName());
				setPic(imageView, item, defaultWidth, defaultWidth);
			}
		}

		return grid;
	}
	
	public static void setPic(ImageView imageView, Uri uri, int defaultWidth, int defaultHeight) {
		
		String photoPath = uri.getPath();
		
	    // Get the dimensions of the View
	    int targetW = imageView.getWidth();
	    int targetH = imageView.getHeight();
	    
	    if (targetW == 0) targetW = defaultWidth;
	    if (targetH == 0) targetH = defaultHeight;
	    
	    if (targetW == 0 || targetH == 0)
	    	return;

	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(photoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    // Determine how much to scale down the image
	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    if (bitmap != null)
	    	imageView.setImageBitmap(bitmap);
	    
	}
}
