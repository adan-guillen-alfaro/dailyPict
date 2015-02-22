package com.example.daylypict;

import java.io.File;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.GridView;


public class MainActivity extends Activity {
	
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final long ALARM_DELAY = 2* 60 * 1000;
	
	GridView mGridView = null;
	Uri mLastPicture = null;
	
	AlarmManager mAlarmManager = null;
	Intent mNotificationReceiverIntent = null;
	PendingIntent mNotificationReceiverPendingIntent = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setAlarm();

        ImageAdapter adapter = new ImageAdapter(getApplicationContext());
        
        mGridView = (GridView)findViewById(R.id.gridview);
        mGridView.setAdapter(adapter);
        
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				try {
					ImageAdapter adapter = (ImageAdapter)mGridView.getAdapter();
					Uri uri = (Uri)adapter.getItem(position);
					
					Intent intent = new Intent(view.getContext(), LargeViewActivity.class);
					
					intent.putExtra("file",  uri.getPath());
					startActivity(intent);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
        
        
    }
    
    @Override
    protected void onResume() {
    	super.onResume();

        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
			
				mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				ImageAdapter adapter = (ImageAdapter)mGridView.getAdapter();
				if (adapter.loadLibrary())
					adapter.notifyDataSetChanged();
				}
		});

/*		ImageAdapter adapter = (ImageAdapter)mGridView.getAdapter();
		if (adapter.loadLibrary())
			adapter.notifyDataSetChanged();
*/    }
    
    public void setAlarm()
    {
    	if (mNotificationReceiverIntent == null)
    		mNotificationReceiverIntent = new Intent(MainActivity.this, AlarmReceiver.class);
    	
    	if (mNotificationReceiverPendingIntent == null)
    		mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, mNotificationReceiverIntent, 0);
    	
    	if (mAlarmManager == null)
    		mAlarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    	else
    		mAlarmManager.cancel(mNotificationReceiverPendingIntent);
    	
    	mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime() + ALARM_DELAY,
				ALARM_DELAY,
				mNotificationReceiverPendingIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_photo) {
        	
        	Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                
                ImageRepository repository = new ImageRepository(getApplicationContext());
                photoFile = repository.createImageFile();
                
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLastPicture = Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            
        	//mGridView.invalidateViews();
            ImageAdapter adapter = (ImageAdapter)mGridView.getAdapter();
            adapter.addItem(mLastPicture);
            adapter.notifyDataSetChanged();
            galleryAddPic(mLastPicture);
            setAlarm();
        }
        else
        	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void galleryAddPic(Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
/*        File f = new File(uri.getPath());
        Uri contentUri = Uri.fromFile(f);
*/        
        mediaScanIntent.setData(uri);
        this.sendBroadcast(mediaScanIntent);
    }
}
