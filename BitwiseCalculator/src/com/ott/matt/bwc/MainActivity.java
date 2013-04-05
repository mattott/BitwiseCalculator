package com.ott.matt.bwc;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends RoboFragmentActivity {

	@InjectView(R.id.delete_button)
	Button deleteBtn;
	@InjectView(R.id.equate_button)
	Button equateBtn;
	@InjectView(R.id.display_view)
	TextView displayView;
	@InjectView(R.id.operation_pager)
	ViewPager oPager;
	@InjectView(R.id.number_pager)
	ViewPager nPager;

	private String mCurText = "";
	private int radix = 10;
	private boolean hasOperator = false;
	private OnClickListener mListener;

	private static final String STATE_CURRENT_VIEW = "state-current-view";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedButtonText = ((Button) v).getText().toString();
				mCurText += selectedButtonText;
				displayView.setText(mCurText);
			}
		};

		if (oPager != null) {
			oPager.setAdapter(new OperatorPagerAdapter(oPager));
		} else {
			final TypedArray op_buttons = getResources().obtainTypedArray(
					R.array.bitwise_array);
			for (int i = 0; i < op_buttons.length(); i++) {
				setOnClickListener(null, op_buttons.getResourceId(i, 0));
			}
			op_buttons.recycle();
		}

		if (nPager != null) {
			nPager.setAdapter(new NumberPagerAdapter(nPager));
		} else {
			final TypedArray num_buttons = getResources().obtainTypedArray(
					R.array.hex_buttons);
			for (int i = 0; i < num_buttons.length(); i++) {
				setOnClickListener(null, num_buttons.getResourceId(i, 0));
			}
			num_buttons.recycle();
		}

		if (oPager != null) {
			oPager.setCurrentItem(savedInstanceState == null ? 0
					: savedInstanceState.getInt(STATE_CURRENT_VIEW, 0));
		}
		if (nPager != null) {
			nPager.setCurrentItem(savedInstanceState == null ? 0
					: savedInstanceState.getInt(STATE_CURRENT_VIEW, 0));
		}
		
		nPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				String error = "PageSelected: " + Integer.toString(position);
				Log.d("Kook", error);
				switch(position) {
				case(0): radix = 10; break;
				case(1): radix = 16; break;
				case(2): radix = 2; break;
				case(3): radix = 8; break;
				}
				String error1 = "Radix: " + Integer.toString(radix);
				Log.d("Kook", error1);
			}
		});

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int windowWidth = metrics.widthPixels;
		int windowHeight = metrics.heightPixels;
		int orientation = getResources().getConfiguration().orientation;

		oPager.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, windowHeight / 4));
		nPager.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, windowHeight / 2));
		displayView.setLayoutParams(new LinearLayout.LayoutParams(
				windowWidth * 4 / 5, windowHeight / 6));
		((View) equateBtn.getParent())
				.setLayoutParams(new LinearLayout.LayoutParams(windowWidth / 5,
						LayoutParams.WRAP_CONTENT));

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	public void setOnClickListener(View root, int id) {
		final View target = root != null ? root.findViewById(id)
				: findViewById(id);
		target.setOnClickListener(mListener);
	}

	public void clickedOperator(String operator) {
		if (!hasOperator
				&& (mCurText.length() > 0 || operator.equalsIgnoreCase("~"))) {
			hasOperator = true;
			mCurText += operator;
		}
		displayView.setText(mCurText);
	}

	public void clickedNumber(String number) {
		displayView.setText(mCurText + number);
	}

	public void onDelete(View v) {

		if (mCurText.length() > 1) {
			char lastChar = mCurText.charAt(mCurText.length() - 1);
			if (lastChar == '<' || lastChar == '>') {
				hasOperator = false;
				mCurText = (String) mCurText.subSequence(0,
						mCurText.length() - 2);
			} else {
				if (!Character.isDigit(lastChar)
						|| !Character.isLetter(lastChar))
					hasOperator = false;
				mCurText = (String) mCurText.subSequence(0,
						mCurText.length() - 1);
			}
		} else {
			hasOperator = false;
			mCurText = "";
		}
		displayView.setText(mCurText);
	}

	public void onEquate(View v) {
		if (mCurText.length() < 1) {
			return;
		}
		String[] parts = {};
		char operator = ' ';
		boolean beginsWithNumber = String.valueOf(mCurText.charAt(0)).matches(
				"[0-9a-zA-Z]");
		boolean endsWithNumber = String.valueOf(
				mCurText.charAt(mCurText.length() - 1)).matches("[0-9a-zA-Z]");
		if (beginsWithNumber || endsWithNumber) {
			for (int i = 0; i < mCurText.length(); i++) {
				if (!(Character.isDigit(mCurText.charAt(i)))
						&& !(Character.isLetter(mCurText.charAt(i)))) {
					char current = mCurText.charAt(i);
					String error2 = "Kook2: "
							+ mCurText.substring(i + 1, i + 2);
					String error3 = "Kook3: " + Integer.toString(i);
					String error4 = "Kook4: " + current;
					Log.d("Kook2", error2);
					Log.d("Kook3", error3);
					Log.d("Kook4", error4);
					if (current == '<' || current == '>')
						parts = mCurText.split(Character.toString(current)
								+ current);
					else if (current == '+' || current == '^' || current == '%')
						parts = mCurText.split("\\" + current);
					else
						parts = mCurText.split(Character.toString(current));

					operator = current;
					i = mCurText.length();
				}
			}
			if (operator == ' ')
				return;
		} else
			return;
		int[] operands = { Integer.parseInt(parts[0], radix),
				Integer.parseInt(parts[1], radix) };

		switch (operator) {
		case '<':
			mCurText = Integer.toString(operands[0] << operands[1], radix);
			break;
		case '>':
			mCurText = Integer.toString(operands[0] >> operands[1], radix);
			break;
		case '|':
			mCurText = Integer.toString(operands[0] | operands[1], radix);
			break;
		case '&':
			mCurText = Integer.toString(operands[0] & operands[1], radix);
			break;
		case '^':
			mCurText = Integer.toString(operands[0] ^ operands[1], radix);
			break;
		case '+':
			mCurText = Integer.toString(operands[0] + operands[1], radix);
			break;
		case '-':
			mCurText = Integer.toString(operands[0] - operands[1], radix);
			break;
		case '%':
			if (operands[1] != 0)
				mCurText = Integer.toString(operands[0] % operands[1], radix);
			break;
		case '~':
			mCurText = Integer.toString(~operands[0], radix);
			break;
		default:
			break;
		}
		displayView.setText(mCurText);
	}

	class OperatorPagerAdapter extends PagerAdapter {
		private View mOperator_bitwise;
		private View mOperator_basic;

		public OperatorPagerAdapter(ViewPager parent) {
			final LayoutInflater inflater = LayoutInflater.from(parent
					.getContext());
			final View operator_bitwise = inflater.inflate(
					R.layout.operator_bitwise, parent, false);
			final View operator_basic = inflater.inflate(
					R.layout.operator_basic, parent, false);

			mOperator_bitwise = operator_bitwise;
			mOperator_basic = operator_basic;

			final Resources res = getResources();
			final TypedArray bitwise_buttons = res
					.obtainTypedArray(R.array.bitwise_buttons);
			for (int i = 0; i < bitwise_buttons.length(); i++) {
				setOnClickListener(operator_bitwise,
						bitwise_buttons.getResourceId(i, 0));
			}
			bitwise_buttons.recycle();

			final TypedArray basic_buttons = res
					.obtainTypedArray(R.array.basic_buttons);
			for (int i = 0; i < basic_buttons.length(); i++) {
				setOnClickListener(operator_basic,
						basic_buttons.getResourceId(i, 0));
			}
			basic_buttons.recycle();
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public Object instantiateItem(View container, int position) {
			final View page = position == 0 ? mOperator_basic
					: mOperator_bitwise;
			((ViewGroup) container).addView(page);
			return page;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

	class NumberPagerAdapter extends PagerAdapter {
		private View mNumber_bin;
		private View mNumber_oct;
		private View mNumber_dec;
		private View mNumber_hex;

		public NumberPagerAdapter(ViewPager parent) {
			final LayoutInflater inflater = LayoutInflater.from(parent
					.getContext());
			final View number_bin = inflater.inflate(R.layout.bin_layout,
					parent, false);
			final View number_oct = inflater.inflate(R.layout.oct_layout,
					parent, false);
			final View number_dec = inflater.inflate(R.layout.dec_layout,
					parent, false);
			final View number_hex = inflater.inflate(R.layout.hex_layout,
					parent, false);

			mNumber_bin = number_bin;
			mNumber_oct = number_oct;
			mNumber_dec = number_dec;
			mNumber_hex = number_hex;

			final Resources res = getResources();

			final TypedArray hex_buttons = res
					.obtainTypedArray(R.array.hex_buttons);
			for (int i = 0; i < hex_buttons.length(); i++) {
				setOnClickListener(number_hex, hex_buttons.getResourceId(i, 0));
			}
			hex_buttons.recycle();

			final TypedArray dec_buttons = res
					.obtainTypedArray(R.array.dec_buttons);
			for (int i = 0; i < dec_buttons.length(); i++) {
				setOnClickListener(number_dec, dec_buttons.getResourceId(i, 0));
			}
			dec_buttons.recycle();

			final TypedArray oct_buttons = res
					.obtainTypedArray(R.array.oct_buttons);
			for (int i = 0; i < oct_buttons.length(); i++) {
				setOnClickListener(number_oct, oct_buttons.getResourceId(i, 0));
			}
			oct_buttons.recycle();

			final TypedArray bin_buttons = res
					.obtainTypedArray(R.array.bin_buttons);
			for (int i = 0; i < bin_buttons.length(); i++) {
				setOnClickListener(number_bin, bin_buttons.getResourceId(i, 0));
			}
			bin_buttons.recycle();
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public Object instantiateItem(View container, int position) {
			final View page;
			switch (position) {
			case (0):
				page = mNumber_dec;
				break;
			case (1):
				page = mNumber_hex;
				break;
			case (2):
				page = mNumber_bin;
				break;
			default:
				page = mNumber_oct;
				break;
			}
			((ViewGroup) container).addView(page);

			return page;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

}
