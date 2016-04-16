package com.gopals.pals;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Help extends Activity{
	
	LinearLayout helpDesc1, helpDesc2, helpDesc3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		
		TextView helpLbl = (TextView)findViewById(R.id.helpLbl);
		TextView help1Lbl = (TextView)findViewById(R.id.help1Lbl);
		TextView help2Lbl = (TextView)findViewById(R.id.help2Lbl);
		TextView help3Lbl = (TextView)findViewById(R.id.help3Lbl);
		TextView help4Lbl = (TextView)findViewById(R.id.help4Lbl);
		TextView help5Lbl = (TextView)findViewById(R.id.help5Lbl);
		TextView helpDesc1Lbl = (TextView)findViewById(R.id.helpDesc1Lbl);
		TextView helpDesc2Lbl = (TextView)findViewById(R.id.helpDesc2Lbl);
		TextView helpDesc3Lbl = (TextView)findViewById(R.id.helpDesc3Lbl);
		TextView helpDesc4Lbl = (TextView)findViewById(R.id.helpDesc4Lbl);
		TextView helpDesc5Lbl = (TextView)findViewById(R.id.helpDesc5Lbl);
		
		helpLbl.setTypeface(bariol, Typeface.BOLD);
		help1Lbl.setTypeface(bariol, Typeface.BOLD);
		help2Lbl.setTypeface(bariol, Typeface.BOLD);
		help3Lbl.setTypeface(bariol, Typeface.BOLD);
		help4Lbl.setTypeface(bariol, Typeface.BOLD);
		help5Lbl.setTypeface(bariol, Typeface.BOLD);
		helpDesc1Lbl.setTypeface(bariol);
		helpDesc2Lbl.setTypeface(bariol);
		helpDesc3Lbl.setTypeface(bariol);
		helpDesc4Lbl.setTypeface(bariol);
		helpDesc5Lbl.setTypeface(bariol);
		
		final LinearLayout help1 = (LinearLayout)findViewById(R.id.help1);
		final LinearLayout help2 = (LinearLayout)findViewById(R.id.help2);
		final LinearLayout help3 = (LinearLayout)findViewById(R.id.help3);
		final LinearLayout help4 = (LinearLayout)findViewById(R.id.help4);
		final LinearLayout help5 = (LinearLayout)findViewById(R.id.help5);
		final LinearLayout helpDesc1 = (LinearLayout)findViewById(R.id.helpDesc1);
		final LinearLayout helpDesc2 = (LinearLayout)findViewById(R.id.helpDesc2);
		final LinearLayout helpDesc3 = (LinearLayout)findViewById(R.id.helpDesc3);
		final LinearLayout helpDesc4 = (LinearLayout)findViewById(R.id.helpDesc4);
		final LinearLayout helpDesc5 = (LinearLayout)findViewById(R.id.helpDesc5);
		
		help1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(helpDesc1.getVisibility() == View.VISIBLE){
					helpDesc1.setVisibility(View.GONE);
					help1.setBackgroundResource(R.drawable.border);
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
				} else {
					help2.setBackgroundResource(R.drawable.border);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
					helpDesc1.setVisibility(View.VISIBLE);
					helpDesc2.setVisibility(View.GONE);
					helpDesc3.setVisibility(View.GONE);
					helpDesc4.setVisibility(View.GONE);
					helpDesc5.setVisibility(View.GONE);
				}
			}
		});
		
		help2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(helpDesc2.getVisibility() == View.VISIBLE){
					helpDesc2.setVisibility(View.GONE);
					help1.setBackgroundResource(R.drawable.border);
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
				} else {
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.border);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
					helpDesc1.setVisibility(View.GONE);
					helpDesc2.setVisibility(View.VISIBLE);
					helpDesc3.setVisibility(View.GONE);
					helpDesc4.setVisibility(View.GONE);
					helpDesc5.setVisibility(View.GONE);
				}
			}
		});
		
		help3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(helpDesc3.getVisibility() == View.VISIBLE){
					helpDesc3.setVisibility(View.GONE);
					help1.setBackgroundResource(R.drawable.border);
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
				} else {
					help2.setBackgroundResource(R.drawable.side_border_bottom);					
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.border);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
					helpDesc1.setVisibility(View.GONE);
					helpDesc2.setVisibility(View.GONE);
					helpDesc3.setVisibility(View.VISIBLE);
					helpDesc4.setVisibility(View.GONE);
					helpDesc5.setVisibility(View.GONE);
				}
			}
		});
		
		help4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(helpDesc4.getVisibility() == View.VISIBLE){
					helpDesc4.setVisibility(View.GONE);
					help1.setBackgroundResource(R.drawable.border);
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
				} else {
					help2.setBackgroundResource(R.drawable.side_border_bottom);					
					help3.setBackgroundResource(R.drawable.side_border_bottom);					
					help4.setBackgroundResource(R.drawable.side_border_bottom);					
					help5.setBackgroundResource(R.drawable.border);
					helpDesc1.setVisibility(View.GONE);
					helpDesc2.setVisibility(View.GONE);
					helpDesc3.setVisibility(View.GONE);
					helpDesc4.setVisibility(View.VISIBLE);
					helpDesc5.setVisibility(View.GONE);
				}
			}
		});
		
		help5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(helpDesc5.getVisibility() == View.VISIBLE){
					helpDesc5.setVisibility(View.GONE);
					help1.setBackgroundResource(R.drawable.border);
					help2.setBackgroundResource(R.drawable.side_border_bottom);
					help3.setBackgroundResource(R.drawable.side_border_bottom);
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
				} else {
					help2.setBackgroundResource(R.drawable.side_border_bottom);					
					help3.setBackgroundResource(R.drawable.side_border_bottom);					
					help4.setBackgroundResource(R.drawable.side_border_bottom);
					help5.setBackgroundResource(R.drawable.side_border_bottom);
					helpDesc1.setVisibility(View.GONE);
					helpDesc2.setVisibility(View.GONE);
					helpDesc3.setVisibility(View.GONE);
					helpDesc4.setVisibility(View.GONE);
					helpDesc5.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	
}
