package com.android.sofla.drj;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;


public class DroidRunJumpActivity extends Activity {
	
	public static final String PREFS_NAME = "DRJPrefsFile";
	
	DroidRunJumpView drjView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        
        drjView = (DroidRunJumpView) findViewById(R.id.droidrunjump);                       
    }
    
    @Override
    protected void onPause() {
    	super.onPause();

    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       	SharedPreferences.Editor editor = settings.edit();  
    	
    	if (isFinishing()) {
    		// just save high score if player is exiting
    		drjView.getThread().saveGame(editor, true);
    		return;    	
    	}
    	
   		// save game       	
       	drjView.getThread().pause();
       	drjView.getThread().saveGame(editor, false);       	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	// restore game
    	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
   		drjView.getThread().restoreGame(settings);
    }    
}