package com.omneagate.activity.dialog;

import java.util.Calendar; 


import android.content.Context;  
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View; 
import android.widget.Button; 
import android.widget.EditText; 
import android.widget.RelativeLayout;

import com.omneagate.activity.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

public class DOBSelectionDialog extends RelativeLayout {
	// DatePicker reference 
		public int startYear = 1950;
		public int endYear = 2400;

		public View myPickerView;

		public Button month_plus;
		public EditText month_display;
		public Button month_minus;

		public Button date_plus;
		public EditText date_display;
		public Button date_minus;

		public Button year_plus;
		public EditText year_display;
		public Button year_minus;

		public Button hour_plus;
		public EditText hour_display;
		public Button hour_minus;

		public Button min_plus;
		public EditText min_display;
		public Button min_minus;

		public Calendar cal;
//	MaterialCalendarView widget;//

		// Constructor start
		public DOBSelectionDialog(Context context) {
			this(context, null);

			init(context);
		}
		public DOBSelectionDialog(Context context, AttributeSet attrs) {
			this(context, attrs, 0);
		}

		public DOBSelectionDialog(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);

//			// Get LayoutInflater instance
//			final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			// Inflate myself
//			inflater.inflate(R.layout.datetimepicker, this, true);
			LayoutInflater inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myPickerView = inflator.inflate(R.layout.datetimepicker, null);
			this.addView(myPickerView);

			initializeReference();

		}
		public void init(Context mContext) {
			LayoutInflater inflator = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myPickerView = inflator.inflate(R.layout.datetimepicker, null);
			this.addView(myPickerView);

			initializeReference();
		}
		public void initializeReference() {



			month_plus = (Button) myPickerView.findViewById(R.id.month_plus);
			month_plus.setOnClickListener(month_plus_listener);
			month_display = (EditText) myPickerView.findViewById(R.id.month_display);
			month_minus = (Button) myPickerView.findViewById(R.id.month_minus);
			month_minus.setOnClickListener(month_minus_listener);

			date_plus = (Button) myPickerView.findViewById(R.id.date_plus);
			date_plus.setOnClickListener(date_plus_listener);
			date_display = (EditText) myPickerView.findViewById(R.id.date_display);
			date_display.addTextChangedListener(date_watcher);
			date_minus = (Button) myPickerView.findViewById(R.id.date_minus);
			date_minus.setOnClickListener(date_minus_listener);

			year_plus = (Button) myPickerView.findViewById(R.id.year_plus);
			year_plus.setOnClickListener(year_plus_listener);
			year_display = (EditText) myPickerView.findViewById(R.id.year_display);
			year_display.setOnFocusChangeListener(mLostFocusYear);
			year_display.addTextChangedListener(year_watcher);
			year_minus = (Button) myPickerView.findViewById(R.id.year_minus);
			year_minus.setOnClickListener(year_minus_listener);


			hour_plus = (Button) myPickerView.findViewById(R.id.hour_plus);
			hour_plus.setOnClickListener(hour_plus_listener);
			hour_display = (EditText) myPickerView.findViewById(R.id.hour_display);
			hour_display.addTextChangedListener(hour_watcher);
			hour_minus = (Button) myPickerView.findViewById(R.id.hour_minus);
			hour_minus.setOnClickListener(hour_minus_listener);

			min_plus = (Button) myPickerView.findViewById(R.id.min_plus);
			min_plus.setOnClickListener(min_plus_listener);
			min_display = (EditText) myPickerView.findViewById(R.id.min_display);
			min_display.addTextChangedListener(min_watcher);
			min_minus = (Button) myPickerView.findViewById(R.id.min_minus);
			min_minus.setOnClickListener(min_minus_listener);

			initData();
			initFilterNumericDigit();

		}
		public void initData() {
			cal = Calendar.getInstance();

			month_display.setText(months[cal.get(Calendar.MONTH)]);
			date_display.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
			year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
			min_display.setText(String.valueOf(cal.get(Calendar.MINUTE)));
		}

		public void initFilterNumericDigit() {

			try {
				date_display.setFilters(new InputFilter[] { new InputFilterMinMax(
						1, cal.getActualMaximum(Calendar.DAY_OF_MONTH)) });

				InputFilter[] filterArray_year = new InputFilter[1];
				filterArray_year[0] = new InputFilter.LengthFilter(4);
				year_display.setFilters(filterArray_year);
				hour_display.setFilters(new InputFilter[] { new InputFilterMinMax(
						0, 23) });
				min_display.setFilters(new InputFilter[] { new InputFilterMinMax(0, 59) });
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void changeFilter() {
			try {

				date_display.setFilters(new InputFilter[] { new InputFilterMinMax(
						1, cal.getActualMaximum(Calendar.DAY_OF_MONTH)) });
			} catch (Exception e) {
				date_display.setText("" + cal.get(Calendar.DAY_OF_MONTH));
				e.printStackTrace();
			}
		}
		public void setTimeChangedListener(TimeWatcher listener) {
			this.mTimeWatcher = listener;
		}

		public void removeTimeChangedListener() {
			this.mTimeWatcher = null;
		}

		OnClickListener hour_plus_listener = new OnClickListener() {

			public void onClick(View v) {
				hour_display.requestFocus();

				try {
						cal.add(Calendar.HOUR_OF_DAY, 1);
					sendToDisplay();
				} catch (Exception e) {
					Log.e("", e.toString());

				}
			}
		};
		OnClickListener hour_minus_listener = new OnClickListener() {


			public void onClick(View v) {
				hour_display.requestFocus();

				try {
						cal.add(Calendar.HOUR_OF_DAY, -1);
					sendToDisplay();
				} catch (Exception e) {
					Log.e("", e.toString());
				}
			}
		};

		OnClickListener min_plus_listener = new OnClickListener() {


			public void onClick(View v) {
				min_display.requestFocus();

				try {
					cal.add(Calendar.MINUTE, 1);
					sendToDisplay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		OnClickListener min_minus_listener = new OnClickListener() {


			public void onClick(View v) {
				min_display.requestFocus();

				try {
					cal.add(Calendar.MINUTE, -1);
					sendToDisplay();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
				"Sep", "Oct", "Nov", "Dec" };

		OnClickListener month_plus_listener = new OnClickListener() {


			public void onClick(View v) {

				try {
					cal.add(Calendar.MONTH, 1);

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					changeFilter();
					sendToListener();
				} catch (Exception e) {
					Log.e("", e.toString());
				}
			}
		};
		OnClickListener month_minus_listener = new OnClickListener() {


			public void onClick(View v) {
				try {
					cal.add(Calendar.MONTH, -1);

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					changeFilter();
					sendToListener();
				} catch (Exception e) {
					Log.e("", e.toString());
				}
			}
		};
		OnClickListener date_plus_listener = new OnClickListener() {


			public void onClick(View v) {

				try {
					date_display.requestFocus();
					cal.add(Calendar.DAY_OF_MONTH, 1);

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					sendToListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		OnClickListener date_minus_listener = new OnClickListener() {


			public void onClick(View v) {

				try {
					date_display.requestFocus();
					cal.add(Calendar.DAY_OF_MONTH, -1);

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					sendToListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		OnClickListener year_plus_listener = new OnClickListener() {


			public void onClick(View v) {

				try {
					year_display.requestFocus();

					if (cal.get(Calendar.YEAR) >= endYear) {

						cal.set(Calendar.YEAR, startYear);

					} else {
						cal.add(Calendar.YEAR, +1);

					}

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					changeFilter();
					sendToListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		OnClickListener year_minus_listener = new OnClickListener() {


			public void onClick(View v) {

				try {
					year_display.requestFocus();

					if (cal.get(Calendar.YEAR) <= startYear) {
						cal.set(Calendar.YEAR, endYear);

					} else {
						cal.add(Calendar.YEAR, -1);

					}

					month_display.setText(months[cal.get(Calendar.MONTH)]);
					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
					date_display.setText(String.valueOf(cal
							.get(Calendar.DAY_OF_MONTH)));

					changeFilter();
					sendToListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};


		class InputFilterMinMax implements InputFilter {

			public int min, max;

			public InputFilterMinMax(int min, int max) {
				this.min = min;
				this.max = max;
			}

			public InputFilterMinMax(String min, String max) {
				this.min = Integer.parseInt(min);
				this.max = Integer.parseInt(max);
			}


			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				try {
					int input = Integer.parseInt(dest.toString()
							+ source.toString());
					if (isInRange(min, max, input)) {
						return null;
					}
				} catch (NumberFormatException nfe) {
				}
				return "";
			}

			public boolean isInRange(int a, int b, int c) {
				return b > a ? c >= a && c <= b : c >= b && c <= a;
			}
		}

		public void reset() {
			cal = Calendar.getInstance();
			initFilterNumericDigit();
			initData();
			sendToDisplay();
		}

		synchronized public void sendToListener() {

			if (mTimeWatcher != null) {
					mTimeWatcher.onTimeChanged(cal.get(Calendar.HOUR_OF_DAY),
							cal.get(Calendar.MINUTE), -1);
			}
			if (mDateWatcher != null) {
				mDateWatcher.onDateChanged(cal);
			}
		}

		public void sendToDisplay() {

			hour_display.setText(String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
			min_display.setText(String.valueOf(cal.get(Calendar.MINUTE)));
		}

		TimeWatcher mTimeWatcher = null;

		public interface TimeWatcher {
			void onTimeChanged(int h, int m, int am_pm);
		}

		TextWatcher hour_watcher = new TextWatcher() {


			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}


			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}


			public void afterTextChanged(Editable s) {
				try {
					if (s.toString().length() > 0) {
							cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(s.toString()));
						sendToListener();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		TextWatcher min_watcher = new TextWatcher() {


			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}


			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}


			public void afterTextChanged(Editable s) {
				try {
					if (s.toString().length() > 0) {
						cal.set(Calendar.MINUTE, Integer.parseInt(s.toString()));
						sendToListener();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		public int getYear() {
			return Integer.parseInt(year_display.getText().toString());
		}

		public int getDay() {
			return Integer.parseInt(date_display.getText().toString());
		}

		public String getMonth() {
			return month_display.getText().toString();
		}

		public int getHour() {
			return Integer.parseInt(hour_display.getText().toString());
		}

		public int getMinute() {
			return Integer.parseInt(min_display.getText().toString());
		}

		public void setDateChangedListener(DateWatcher listener) {
			this.mDateWatcher = listener;
		}

		public void removeDateChangedListener() {
			this.mDateWatcher = null;
		}


		OnFocusChangeListener mLostFocusYear = new OnFocusChangeListener() {


			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {

					year_display.setText(String.valueOf(cal.get(Calendar.YEAR)));
				}
			}
		};



		TextWatcher date_watcher = new TextWatcher() {


			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}


			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}


			public void afterTextChanged(Editable s) {

				try {
					if (s.toString().length() > 0) {
						// Log.e("", "afterTextChanged : " + s.toString());
						cal.set(Calendar.DAY_OF_MONTH,
								Integer.parseInt(s.toString()));

						month_display.setText(months[cal.get(Calendar.MONTH)]);

						sendToListener();
					}
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		};

		TextWatcher year_watcher = new TextWatcher() {

			
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			
			public void afterTextChanged(Editable s) {
				try {
					if (s.toString().length() == 4) {
						int year = Integer.parseInt(s.toString());

						if (year > endYear) {
							cal.set(Calendar.YEAR, endYear);
						} else if (year < startYear) {
							cal.set(Calendar.YEAR, startYear);
						} else {
							cal.set(Calendar.YEAR, year);
						}
					}

					sendToListener();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
 
		DateWatcher mDateWatcher = null;
	
		public interface DateWatcher {
			void onDateChanged(Calendar c);
		}

	
 
		
}
