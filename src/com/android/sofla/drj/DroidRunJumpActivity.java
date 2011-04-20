package com.android.sofla.drj;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;


public class DroidRunJumpActivity extends Activity {
	
	public static final String PREFS_NAME = "DRJPrefsFile";
	
	DroidRunJumpView drjView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        drjView = (DroidRunJumpView) findViewById(R.id.droidrunjump);        
        Log.w("DRJ", "onCreate called...");        
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

    	Log.w("DRJ", "onPause called");

    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       	SharedPreferences.Editor editor = settings.edit();  
    	
    	if (isFinishing()) {
    		Log.w("DRJ", "is Finishing...");    		
    		// don't save if user wants to exit
    		return;    	
    	}
    	
   		// save game       	
       	drjView.getThread().pause();
       	drjView.getThread().saveGame(editor);       	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	Log.w("DRJ", "onResume called");
    	
    	// restore game
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
   		drjView.getThread().restoreGame(settings);
    }    
}