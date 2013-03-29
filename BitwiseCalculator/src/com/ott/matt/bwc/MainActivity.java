package com.ott.matt.bwc;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {
	CharSequence mCurText = "0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// inflate the view
		setContentView(R.layout.activity_main);
		GridView gV = (GridView) findViewById(R.id.keypad_view);
		TextView tV = (TextView) findViewById(R.id.display_view);
		OnClickListener mClickListener = new OnClickListener() {
			public void onClick(View v) {
				Button clickedButton = (Button) v;
				Resources res = getResources();
				String text = String.format(res.getString(clickedButton.getId()));
				CharSequence styledText = Html.fromHtml(text);
				onButtonPressed(styledText);
			}
		};
		final KeypadAdapter keypadAdapter = new KeypadAdapter(this);
		keypadAdapter.setOnButtonClickListener(mClickListener);
		gV.setAdapter(keypadAdapter);
		//gV.setOnItemClickListener(mClickListener);
		tV.setText(mCurText.toString());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); // takes care of all of the default
												// saves
		outState.putCharSequence("curText", mCurText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onButtonPressed(CharSequence keyText) {
		// append the input text to the current textinput
		if (keyText == "DELETE") {
			if (mCurText.length() > 1) {
				mCurText = mCurText.subSequence(0, mCurText.length() - 1);
			} // else if (mCurText.charAt(mCurText.length()) == ' ') {
				// mCurText = mCurText.subSequence(0, mCurText.length()-3);}
			else {
				mCurText = "0";
			}
		} else if (keyText == ">>" || keyText == "<<" || keyText == "^"
				|| keyText == "|" || keyText == "&" || keyText == "~") {
			mCurText = (CharSequence) mCurText.toString() + " "
					+ keyText.toString() + " ";
		} else if (mCurText == "0") {
			mCurText = keyText;
		} else {
			mCurText = (CharSequence) mCurText.toString() + keyText.toString();
		}
		TextView tV = (TextView) findViewById(R.id.display_view);
		tV.setText(mCurText);
	}

}
