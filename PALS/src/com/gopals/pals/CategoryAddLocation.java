package com.gopals.pals;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("InflateParams")
public class CategoryAddLocation extends ListActivity{
	
	public static final String TAG_CATEGORY = "category";
	ArrayList<HashMap<String, String>> categoryList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.category_add_location);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1a1a1a")));
		bar.setTitle("");
		
		ListView lv = getListView();
		
		final Typeface bariol = Typeface.createFromAsset(getAssets(), "fonts/bariol.ttf");
		TextView categoryLbl = (TextView)findViewById(R.id.categoryLbl);
		categoryLbl.setTypeface(bariol, Typeface.BOLD);
		
		categoryList = new ArrayList<HashMap<String, String>>();
		final String[] from = {TAG_CATEGORY};
		String[] categoryItem = {"ATM", "Gas Station", "Repair Shop"};
		final int[] to = {R.id.categoryName};
		
		for(int i=0; i<categoryItem.length; i++){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(TAG_CATEGORY, categoryItem[i]);
			categoryList.add(map);
		}
		
		runOnUiThread(new Runnable() {
            public void run() {
            	ListAdapter adapter = new SimpleAdapter(getApplicationContext(), categoryList,
        				R.layout.list_item_category, from, to){
            		
					@Override
                    public View getView(int position, View convertView, ViewGroup parent){
						if(convertView== null){
			                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			                convertView=vi.inflate(R.layout.list_item_category, null);
			            }
                        TextView categoryLbl = (TextView)convertView.findViewById(R.id.categoryName);
                        categoryLbl.setText(categoryList.get(position).get(TAG_CATEGORY));
                        categoryLbl.setTypeface(bariol);
		        		
                        return convertView;
                    }
            	};
            	setListAdapter(adapter);
            }
		});
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, 
					int position, long id) {
				Network ic = new Network();
				if (ic.isNetworkConnected(getApplicationContext())) {
					String selectedCategory = ((TextView) view.findViewById(R.id.categoryName)).
							getText().toString();
					
					Intent mapsActivity = new Intent(getApplicationContext(), AddLocationMapsActivity.class);
					mapsActivity.putExtra("category", selectedCategory);
					startActivity(mapsActivity);
					
				} else 	Toast.makeText(getApplicationContext(), "No Internet Connection", 
							Toast.LENGTH_SHORT).show();
			}
		});
	}
	
}
