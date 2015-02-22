package com.example.daylypict;

import java.io.File;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class LargeViewActivity extends Activity {

	private ImageView mImageView;
	private String mPathname;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.large_view);
		
		mImageView = (ImageView)findViewById(R.id.large_image);
		mPathname = getIntent().getExtras().getString("file");

		ViewTreeObserver vto = mImageView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
		    @Override 
		    public void onGlobalLayout() { 
		    	mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
		        int width  = mImageView.getMeasuredWidth();
		        int height = mImageView.getMeasuredHeight(); 

				File file = new File(mPathname);
				ImageAdapter.setPic(mImageView, Uri.fromFile(file), width, height);

		    } 
		});
	}
}
