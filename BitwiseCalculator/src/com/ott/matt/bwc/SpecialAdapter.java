package com.ott.matt.bwc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class SpecialAdapter extends ArrayAdapter<CharSequence> {

	public SpecialAdapter(Context context, int textViewResourceId,
			CharSequence[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}


	public static SpecialAdapter createFromResource(Context context,
			int textArrayResourceId, int textViewResourceId) {

		Resources resources = context.getResources();
		CharSequence[] strings = resources.getTextArray(textArrayResourceId);

		return new SpecialAdapter(context, textViewResourceId, strings);
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context mContext = parent.getContext();
	    if (convertView ==  null) {
	        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.special_layout, null);
	    }
	    TextView currentSp = (TextView) convertView;
	    DisplayMetrics metrics = new DisplayMetrics();
	    WindowManager wm = (WindowManager) convertView.getContext().getSystemService(Context.WINDOW_SERVICE);
	    wm.getDefaultDisplay().getRealMetrics(metrics);
	    float screenWidth = metrics.widthPixels;
	    float screenHeight = metrics.heightPixels;
	    int orientation = convertView.getContext().getResources().getConfiguration().orientation;
	    if (orientation == 1)
	    	currentSp.setLayoutParams(new GridView.LayoutParams((int) (screenWidth/2), (int) (screenHeight/8)));
	    else
		    currentSp.setLayoutParams(new GridView.LayoutParams((int) (screenWidth/2), (int) (screenHeight/6)));

	    currentSp.setBackgroundResource(R.drawable.special_background);
	    
	    currentSp.setTextColor(Color.WHITE);
	    currentSp.setText(this.getItem(position));
	    return convertView;
	}

}
