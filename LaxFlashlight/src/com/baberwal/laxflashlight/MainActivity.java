package com.baberwal.laxflashlight;
 
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {
 
	 	
	 	private Camera camera;
	    private boolean isFlashOn;
	    private boolean hasFlash;
	    private NotificationManager notificationManager;
	    private static final int NOTIFICATION_EX=1;
	    Parameters params;
	    MediaPlayer mp;
	    WakeLock wl;
	    Notification notification;
	    ImageButton btnSwitch;
	    
	 
	    @SuppressWarnings("deprecation")
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
	        wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNjfdhotDimScreen");
	        
	        //Status bar notification
	        notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
	        int icon= R.drawable.notification_icon;
	        CharSequence tickerText="Lax Flashlight";
	        long when=System.currentTimeMillis();
	        notification= new Notification(icon,tickerText,when);
	        Context context= getApplicationContext();
	        CharSequence contentTitle="Lax Flashlight";
	        CharSequence contentText= "Launch Flashlight";
	        Intent notificationIntent=new Intent(this,MainActivity.class);
	        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
	        PendingIntent contentIntent=PendingIntent.getActivity(this, 0, notificationIntent, 0);
	        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
	        notificationManager.notify(NOTIFICATION_EX,notification);
	        //end
	    
	        
	        btnSwitch = (ImageButton)findViewById(R.id.btnSwitch);
	 
	     
	        // First check if device is supporting flashlight or not        
	        hasFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	 
	        if (!hasFlash) {
	            // device doesn't support flash
	            // Show alert message and close the application
	            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
	            alert.setTitle("Error");
	            alert.setMessage("Oops,Seems like your device doesn't support flash light!");
	            alert.setButton("OK", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    // closing the application
	                    finish();
	                }
	            });
	            alert.show();
	            return;
	        }
	 
	        // get the camera
	        getCamera();
	         
	        // displaying button image
	        toggleButtonImage();
	         
	         
	        // Switch button click event to toggle flash on/off
	        btnSwitch.setOnClickListener(new View.OnClickListener() {
	 
	            @Override
	            public void onClick(View v) {
	                if (isFlashOn) {
	                    // turn off flash
	                    turnOffFlash();
	                } else {
	                    // turn on flash
	                    turnOnFlash();
	                }
	            }
	        });
	    }
	 
	     
	    // Get the camera
	    private void getCamera() {
	        if (camera == null) {
	            try {
	                camera = Camera.open();
	                params = camera.getParameters();
	                
	            } catch (RuntimeException e) {
	                Log.e("Camera Error. Failed to Open. Error: ", e.getMessage());
	            }
	        }
	    }
	 
	     
	     // Turning On flash
	    private void turnOnFlash() {
	        if (!isFlashOn) {
	            if (camera == null || params == null) {
	                return;
	            }
	            // play sound
	            playSound();
	             
	            params = camera.getParameters();
	            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
	            camera.setParameters(params);
	            camera.startPreview();
	            isFlashOn = true;
	            
				
	             
	            // changing button/switch image
	            toggleButtonImage();
	        }
	 
	    }
	 
	 
	    // Turning Off flash
	    private void turnOffFlash() {
	        if (isFlashOn) {
	            if (camera == null || params == null) {
	                return;
	            }
	            // play sound
	            playSound();
	             
	            params = camera.getParameters();
	            params.setFlashMode(Parameters.FLASH_MODE_OFF);
	            camera.setParameters(params);
	            camera.stopPreview();
	            isFlashOn = false;
	             
	            // changing button/switch image
	            toggleButtonImage();
	            
	        }
	    }
	     
	 
	     // Playing sound
	     // will play button toggle sound on flash on / off
	    private void playSound(){
	        if(isFlashOn){
	            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_off);
	        }else{
	            mp = MediaPlayer.create(MainActivity.this, R.raw.light_switch_on);
	        }
	        mp.setOnCompletionListener(new OnCompletionListener() {
	 
	            public void onCompletion(MediaPlayer mp) {
	                // TODO Auto-generated method stub
	                mp.release();
	            }
	        }); 
	        mp.start();
	    }
	     
	    /*
	     * Toggle switch button images
	     * changing image states to on / off
	     * */
	    private void toggleButtonImage(){
	        if(isFlashOn){
	            btnSwitch.setImageResource(R.drawable.btn_switch_on);
	        }else{
	            btnSwitch.setImageResource(R.drawable.btn_switch_off);
	        }
	    }
	 
	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	    }
	 
	    @Override
	    protected void onPause() {
	        super.onPause();
	        wl.release();
	        
	        // on pause turn off the flash
	        if(isFlashOn)
	        turnOnFlash();
	        else
	        {
	        	turnOffFlash();
	        	notificationManager.cancel(NOTIFICATION_EX);
	        	if (camera != null) {
		            camera.release();
		            camera = null;
		    }
	        	
	      }
	        
	    }
	 
	    @Override
	    protected void onRestart() {
	        super.onRestart();
	        notificationManager.cancel(NOTIFICATION_EX);
	    }
	    
	    @Override
	    public void onBackPressed() {
	        // TODO Auto-generated method stub
	        super.onBackPressed();
	        notificationManager.cancel(NOTIFICATION_EX);

	        turnOffFlash();

	        if (camera != null) {
	            camera.release();
	            camera = null;
	        }
	        Log.d("Camera","Back Pressed");
	    }
	 
	    @Override
	    protected void onResume() {
	        super.onResume();
	        wl.acquire();
	        notificationManager.notify(NOTIFICATION_EX,notification);
	         
	        // on resume turn on the flash
	        
	        turnOnFlash();
	    }
	 
	    @Override
	    protected void onStart() {
	        super.onStart();
	        
	        getCamera();
	    }
	    
	 
	    @Override
	    protected void onStop() {
	        super.onStop();
	        
	    }
	 
}
