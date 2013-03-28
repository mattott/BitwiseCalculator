package com.ott.matt.bwc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements NumbersFragment.OnNumberPressedListener{
	CharSequence mCurText = "0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// inflate the view
		setContentView(R.layout.activity_main);
		if (savedInstanceState != null) {
			mCurText = savedInstanceState.getString("curText");
		}
		// use a FragmentManager to add the fragments
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		//instantiate the fragments
		DisplayFragment displayFragment = DisplayFragment.newInstance(mCurText.toString());
		NumbersFragment numbersFragment = new NumbersFragment();
		// add the all fragment instances to the ViewGroup
		ft.add(R.id.display_container, displayFragment);
		ft.add(R.id.numbers_container, numbersFragment);
		// commit the transaction
		ft.commit();
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putCharSequence("curText", mCurText);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNumberPressed(CharSequence keyText) {
		// append the input text to the current text
		if (keyText == "DELETE") {
			if (mCurText.length() > 1) {
				mCurText = mCurText.subSequence(0, mCurText.length()-1);
			} //else if (mCurText.charAt(mCurText.length()) == ' ') {
				//mCurText = mCurText.subSequence(0, mCurText.length()-3);} 
			else {
				mCurText = "0";
			}
		} else if(keyText == ">>" || keyText == "<<" || keyText == "^" || keyText == "|"
				|| keyText == "&" || keyText == "~") {
			mCurText = (CharSequence) mCurText.toString() + " " + keyText.toString() + " ";
		} else if (mCurText == "0") {
			mCurText = keyText;
		} else {
			mCurText = (CharSequence) mCurText.toString() + keyText.toString();
		}
	
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		// replace old instance of DisplayFragment with the new text
		DisplayFragment displayFragment = DisplayFragment.newInstance(mCurText.toString());
		ft.replace(R.id.display_container, displayFragment);
		// commit the change
		ft.commit();
	}
	

}
