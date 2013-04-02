package com.ott.matt.bwc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NumbersAdapter extends ArrayAdapter<CharSequence> {

	public NumbersAdapter(Context context, int textViewResourceId,
			CharSequence[] objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
	}

	private int mRadix;

	public static NumbersAdapter createFromResource(Context context,
			int textArrayResourceId, int textViewResourceId) {

		Resources resources = context.getResources();
		CharSequence[] strings = resources.getTextArray(textArrayResourceId);

		return new NumbersAdapter(context, textViewResourceId, strings);
	}

	public void setRadix(int radix) {
		mRadix = radix;
	}

	@Override
	public boolean areAllItemsEnabled() {
		if (mRadix == 16) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		switch (mRadix) {
		case 2:
			if (position < 2)
				return true;
			break;
		case 8:
			if (position < 8)
				return true;
			break;
		case 10:
			if (position < 10)
				return true;
			break;
		case 16:
			if (position < 16)
				return true;
			break;
		default:
			return false;
		}
		return false;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context mContext = parent.getContext();
	    if (convertView ==  null) {
	        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = inflater.inflate(R.layout.numbers_layout, null);
	    }
	    TextView currentNumber = (TextView) convertView;

	    if (!isEnabled(position)) {
	        currentNumber.setBackgroundResource(R.drawable.numbers_disabled_background);
	    } else {
	    	currentNumber.setBackgroundResource(R.drawable.numbers_background);
	    }
	    
	    currentNumber.setTextColor(Color.WHITE);
	    currentNumber.setText(this.getItem(position));
	    return convertView;
	}

}
