package com.gopals.pals;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class About extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		String versionName = getResources().getString(R.string.versionName);
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		TextView aboutLbl = (TextView) findViewById(R.id.aboutLbl);
		TextView devLbl = (TextView) findViewById(R.id.developersLbl);
		TextView copyrightLbl = (TextView) findViewById(R.id.copyrightLbl);
		TextView versionLbl = (TextView) findViewById(R.id.versionLbl);
		TextView version = (TextView) findViewById(R.id.versionName);
		TextView rateLbl = (TextView) findViewById(R.id.rateLbl);
		version.setText(versionName);
		
		aboutLbl.setTypeface(bariol, Typeface.BOLD);
		devLbl.setTypeface(bariol);
		copyrightLbl.setTypeface(bariol);
		versionLbl.setTypeface(bariol);
		version.setTypeface(bariol);
		rateLbl.setTypeface(bariol);

		LinearLayout rate = (LinearLayout) findViewById(R.id.rateApps);
		rate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*
				 * Intent googlePlay = new Intent(Intent.ACTION_VIEW,
				 * Uri.parse("market://details?id=com.gopals.pals"));
				 * startActivity(googlePlay);
				 */
			}
		});

	}
}
