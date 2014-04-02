package com.ott.matt.bwc;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends RoboFragmentActivity {
	@InjectView(R.id.display_view)
	TextView displayView;
	@InjectView(R.id.operations_pager)
	ViewPager mOperationsPager;

	private Symbols mSymbols = new Symbols();
	private String mCurText = "";
	private String nums[] = new String[20];
	private String[] mSpinnerRadix;
	private char ops[] = new char[20];
	private int opIndex = 0;
	private int numIndex = 0;
	private int mRadix = 10;
	private boolean mBitshift = false;
	private DropDownAdapter mDropDownAdapter;
	private GestureDetectorCompat mViewerDetector;
	
	private final OnClickListener mListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			char selectedButtonText = ((Button) v).getText().charAt(0);
			mCurText += selectedButtonText;
			displayView.setText(mCurText);
			enqueue(selectedButtonText);
		}
	};
	
	interface OnTitleChangedListener {
        void onTitleChange(String title);
	}

	private void initializeDropDownMenu() {
		/* Prepare the Drop-down navigation spinner */
        mDropDownAdapter = new DropDownAdapter(this, R.array.radix_array);
        mSpinnerRadix = getResources().getStringArray(R.array.radix_array);
        
        if (getActionBar() != null) {
            getActionBar().setListNavigationCallbacks(mDropDownAdapter, new SpinnerItemSelectedListener());
        }
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	}
	
	private class SpinnerItemSelectedListener implements OnNavigationListener {
        @Override public boolean onNavigationItemSelected(int itemPosition, long itemId) {  
        	
            if (isSwapEligible(itemPosition)) {
                replaceFragment(itemPosition);
                return true;
            } else {
            	return false;
            }
        }

        boolean isSwapEligible(int position) {
            return !getTitle().toString().equals(mSpinnerRadix[position]);
        }
    }
	
	void replaceFragment(int itemPosition) {
		RadixFragment fragment = mDropDownAdapter.getRadix(itemPosition);
		int result = 0;
		try {
			result = Integer.parseInt(mCurText, mRadix);
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.d("MainActivity", "Couldn't parse " + mCurText);
			}
		}
		mRadix = fragment.getRadix();
		if (result != 0) {
			mCurText = Integer.toString(result, mRadix);
			displayView.setText(mCurText);
		}
		getSupportFragmentManager().beginTransaction().replace(R.id.button_fragment, (Fragment)fragment, fragment.getTitle()).commit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.button_fragment, new HexFragment(), "Hexadecimal").commit();
        mRadix = 16;
		initializeDropDownMenu();
		initializeViewerTouch();
		mOperationsPager.setAdapter(new OperatorPagerAdapter(mOperationsPager, getResources()));
		if (getActionBar() != null) {
            getActionBar().setDisplayShowTitleEnabled(false);
        }
	}
	
	private void initializeViewerTouch() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int displayWidth = metrics.widthPixels;
		
		ViewConfiguration vc = ViewConfiguration.get(this);
        int touchSlop = vc.getScaledTouchSlop();
        
		ViewerGestureListener listener = new ViewerGestureListener(this, displayWidth, touchSlop,250);
		mViewerDetector = new GestureDetectorCompat(this, listener);
        findViewById(R.id.display_view).setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
                return mViewerDetector.onTouchEvent(event);
			}
		});
	}
	
	public void setButtonListeners(View parentView, int buttonArrayResource) {
		final Resources res = getResources();

		final TypedArray typedArray = res
				.obtainTypedArray(buttonArrayResource);
		for (int i = 0; i < typedArray.length(); i++) {
			setOnClickListener(parentView, typedArray.getResourceId(i, 0));
		}
		typedArray.recycle();
	}

	public void setOnClickListener(View root, int id) {
		final View target = root != null ? root.findViewById(id)
				: findViewById(id);
		target.setOnClickListener(mListener);
	}

	public void clickedOperator(View v) {
		char selectedButtonText = ((Button) v).getText().charAt(0);
		if (selectedButtonText == '<' || selectedButtonText == '>')
			mCurText += Character.toString(selectedButtonText)
					+ Character.toString(selectedButtonText);
		else
			mCurText += selectedButtonText;

		displayView.setText(mCurText);
		enqueue(selectedButtonText);
	}

	public void onDelete(View v) {
		numIndex = opIndex = 0;
		nums = new String[20];
		ops = new char[20];
		mCurText.substring(0, mCurText.length() - 2);
		displayView.setText(mCurText);
	}
	
	public void onClear(View v) {
		numIndex = opIndex = 0;
		nums = new String[20];
		ops = new char[20];
		mCurText = "";
		displayView.setText(mCurText);
	}
	
	public void showDeleteView() {
		findViewById(R.id.delete_view_layer).performClick();
	}

	public void onEquate(View v) {
		if (mCurText.length() < 1)
			return;
		String result = "Error";
		try {
			result = eval();
		} catch (SyntaxException e) {
			e.printStackTrace();
		}
		displayView.setText(result);
		mCurText = "";
	}
	
	public void showEquateView() {
		findViewById(R.id.equate_view_layer).performClick();
	}

	public void enqueue(char input) {
		if (opIndex >= ops.length) {
			return;
		} else if (isOperator(input)) {
			ops[opIndex] = input;
			opIndex += 1;
			if (nums[0] != null)
				numIndex += 1;
		} else if (nums[numIndex] == null) {
			nums[numIndex] = Character.toString(input);
		} else {
			nums[numIndex] += input;
		}
	}

	public String eval() throws SyntaxException {
		String answer = null;
		toDecimal();
		if (numIndex == opIndex) {
			answer = evaluateExpression(nums[0] + ops[0] + nums[1], ops[0]);
			for (int i = 1; i < numIndex && answer != "Error"; i++)
				answer = evaluateExpression(answer + ops[i] + nums[i + 1],
						ops[i]);
		} else {
			answer = evaluateExpression(ops[0] + nums[0], ops[0]);
			for (int i = 1; i <= numIndex && answer != "Error"; i++) {
				answer = evaluateExpression(answer + ops[i] + nums[i], ops[i]);}
		}
		if (answer != "Error" && !mBitshift)
			return convertToRadix(Double.parseDouble(answer));
		else {
			mBitshift = false;
			return answer;
		}
	}

	public void toDecimal() {
		if (mRadix != 10)
			for (int i = 0; i <= numIndex; i++)
				if (nums[i] != null)
					nums[i] = Integer.toString(Integer
							.parseInt(nums[i], mRadix));
	}

	public boolean isOperator(char input) {
		if (Character.isDigit(input) || Character.isLetter(input)
				|| input == '.')
			return false;
		else
			return true;
	}

	public boolean containsBitwise(String input) {
		for (int i = 0; i < bitwise_operators.length; i++)
			if (input.contains(Character.toString(bitwise_operators[i])))
				return true;
		return false;
	}

	public String evaluateExpression(String input, char operator)
			throws SyntaxException {
		if (containsBitwise(input))
			if (input.contains("."))
				return "Error";
			else
				return evaluateBitwise(input, operator);
		else
			return Double.toString(mSymbols.eval(input));
	}

	public String evaluateBitwise(String input, char operator) {
		String parts[] = { "", "" };
		String result = "";
		switch (operator) {
		case '<':
			parts = input.split("<");
			result = Integer.toString(
					Integer.parseInt(parts[0]) << Integer.parseInt(parts[1]),
					mRadix);
			mBitshift = true;
			break;
		case '>':
			parts = input.split(">");
			result = Integer.toString(
					Integer.parseInt(parts[0]) >> Integer.parseInt(parts[1]),
					mRadix);
			mBitshift = true;
			break;
		case '|':
			parts = input.split("\\|");
			result = Integer.toString(
					Integer.parseInt(parts[0]) | Integer.parseInt(parts[1]),
					mRadix);
			break;
		case '&':
			parts = input.split("\\&");
			result = Integer.toString(
					Integer.parseInt(parts[0]) & Integer.parseInt(parts[1]),
					mRadix);
			break;
		case '^':
			parts = input.split("\\^");
			result = Integer.toString(
					Integer.parseInt(parts[0]) ^ Integer.parseInt(parts[1]),
					mRadix);
			break;
		case '~':
			parts = input.split("\\~");
			result = Integer.toString(~Integer.parseInt(parts[1]), mRadix);
			break;
		default:
			break;
		}
		return result;
	}

	public String convertToRadix(double input) {
		if (mRadix == 10)
			return Double.toString(input);
		int fixed = (int) input;
		String result = Integer.toString(fixed, mRadix);
		return result;
	}
	
	private char bitwise_operators[] = { '>', '<', '~', '^', '&', '|' };
}
