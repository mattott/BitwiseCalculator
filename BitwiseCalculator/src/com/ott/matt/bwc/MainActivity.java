package com.ott.matt.bwc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity implements NumbersFragment.OnNumberPressedListener{
	String mCurText = "0";
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
		DisplayFragment displayFragment = DisplayFragment.newInstance(mCurText);
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
		outState.putString("curText", mCurText);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNumberPressed(String keyText) {
		// append the input text to the current text
		mCurText += keyText;
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		// replace old instance of DisplayFragment with the new text
		DisplayFragment displayFragment = DisplayFragment.newInstance(mCurText);
		ft.replace(R.id.display_container, displayFragment);
		// commit the change
		ft.commit();
	}

}
