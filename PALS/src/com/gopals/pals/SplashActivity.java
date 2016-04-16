package com.gopals.pals;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//ActionBar actionBar = getActionBar();
	    //actionBar.hide();
		
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(5000);
				} catch (InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent main = new Intent(SplashActivity.this, MenuActivity.class);
					startActivity(main);
				}
			}
		};
		timer.start();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
