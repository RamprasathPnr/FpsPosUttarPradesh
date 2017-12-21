package com.omneagate.activity;

import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.neopixl.pixlui.components.edittext.EditText;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.FromDialogMonthlySalesReport;
import com.omneagate.activity.dialog.ToDialogMonthlySalesReport;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created for Bill Search
 */
public class MonthlySalesReportActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    EditText fromDate, toDate;

    RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;

    KeyboardView keyview, keyboardViewAlpha;

    KeyBoardEnum keyBoardFocused;

//    long dateSelection = 0l;

    FromDialogMonthlySalesReport fromDialog;
    ToDialogMonthlySalesReport toDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_sales_report);
        setUpInitialPage();

        fromDialog = new FromDialogMonthlySalesReport(com.omneagate.activity.MonthlySalesReportActivity.this);
        toDialog = new ToDialogMonthlySalesReport(com.omneagate.activity.MonthlySalesReportActivity.this);

    }

    private void setUpInitialPage() {
        try {
            setUpPopUpPage();
            keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
            keyboardumber = (RelativeLayout) findViewById(R.id.keyboardNumber);
            keyboardAlpha = (RelativeLayout) findViewById(R.id.keyboardAlpha);
            Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
            Keyboard keyboardAlp = new Keyboard(this, R.layout.keyboard_alpha);
            //create KeyboardView object
            keyview = (KeyboardView) findViewById(R.id.customkeyboard);
            keyboardViewAlpha = (KeyboardView) findViewById(R.id.customkeyboardAlpha);
            keyboardViewAlpha.setKeyboard(keyboardAlp);
            //attache the keyboard object to the KeyboardView object
            keyview.setKeyboard(keyboard);
            //show the keyboard
            keyview.setVisibility(KeyboardView.VISIBLE);
            keyboardViewAlpha.setVisibility(KeyboardView.VISIBLE);
            keyview.setPreviewEnabled(false);
            keyboardViewAlpha.setPreviewEnabled(false);
            //take the keyboard to the front
            keyview.bringToFront();
            keyboardViewAlpha.bringToFront();
            //register the keyboard to receive the key pressed
            keyview.setOnKeyboardActionListener(new KeyList());
            keyboardViewAlpha.setOnKeyboardActionListener(new KeyListAlpha());
            keyBoardFocused = KeyBoardEnum.PREFIX;

            Util.LoggingQueue(this, "Monthly Sales report", "Starting up page Called");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.monthly_sales_summary);
            Util.setTamilText((Button) findViewById(R.id.cancelMonthlyReport), R.string.cancel);
            Util.setTamilText((Button) findViewById(R.id.deleteMonthlyReport), R.string.delete);
            Util.setTamilText((Button) findViewById(R.id.searchMonthlyReport), R.string.search);
            fromDate = (EditText) findViewById(R.id.startDate);
            fromDate.setHint(R.string.start_date);
            toDate = (EditText) findViewById(R.id.endDate);
            toDate.setHint(R.string.end_date);

            fromDate.setOnFocusChangeListener(this);
            fromDate.setOnClickListener(this);
            fromDate.setShowSoftInputOnFocus(false);

            toDate.setOnFocusChangeListener(this);
            toDate.setOnClickListener(this);
            toDate.setShowSoftInputOnFocus(false);

            findViewById(R.id.cancelMonthlyReport).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();

                }
            });

            findViewById(R.id.deleteMonthlyReport).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fromDate.setText("");
                    fromDate.setHint(R.string.start_date);
                    toDate.setText("");
                    toDate.setHint(R.string.end_date);

                }
            });

            findViewById(R.id.searchMonthlyReport).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                                dateBySearch();

                }
            });

            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

        } catch (Exception e) {
            Log.e("error",e.toString());
        }
    }




    @Override
    public void onClick(View v) {
        if (!(fromDialog.isShowing() || toDialog.isShowing())) {
            switch (v.getId()) {
                case R.id.startDate:
                    fromDate.requestFocus();
                    keyBoardCustom.setVisibility(View.GONE);
                    keyBoardFocused = KeyBoardEnum.NOTHING;
                    fromDialog.show();
                    break;
                case R.id.endDate:
                    toDate.requestFocus();
                    keyBoardCustom.setVisibility(View.GONE);
                    keyBoardFocused = KeyBoardEnum.NOTHING;
                    toDialog.show();
                    break;
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!(fromDialog.isShowing() || toDialog.isShowing())) {
            if (v.getId() == R.id.startDate && hasFocus) {
                keyBoardCustom.setVisibility(View.GONE);
                keyBoardFocused = KeyBoardEnum.NOTHING;
                fromDialog.show();
            } else if (v.getId() == R.id.endDate && hasFocus) {
                keyBoardCustom.setVisibility(View.GONE);
                keyBoardFocused = KeyBoardEnum.NOTHING;
                toDialog.show();
            }
        }
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }

    private void keyBoardAppear() {
        keyboardumber.setVisibility(View.VISIBLE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void changeKeyboard() {
        try {
            keyboardumber.setVisibility(View.GONE);
            keyboardAlpha.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error",e.toString(),e);
        }

    }

    private void searchQRBills(String qrCode) {
        if (StringUtils.isNotEmpty(qrCode)) {
            BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(this).beneficiaryDto(qrCode);
            if (beneficiaryDto == null) {
                Util.messageBar(this, getString(R.string.qr_exists));
            } else {
                searchBills(qrCode, "qrCode");
            }
        } else {
            Util.messageBar(this, getString(R.string.invalid_qr));
        }
    }

    public void setFromTextDate(String textDate) {
        Util.LoggingQueue(com.omneagate.activity.MonthlySalesReportActivity.this, "MonthlySalesReportActivity", "Searching by from date");
        fromDate.setText(textDate);
        fromDialog.cancel();
        toDate.requestFocus();
    }

    public void setToTextDate(String textDate) {
        Util.LoggingQueue(com.omneagate.activity.MonthlySalesReportActivity.this, "MonthlySalesReportActivity", "Searching by to date");
        toDate.setText(textDate);
        toDialog.cancel();
    }

    private void searchBills(String bills, String type) {
        Intent intent = new Intent(this, BillActivity.class);
        intent.putExtra("bills", bills);
        intent.putExtra("search", type);
        startActivity(intent);
        finish();
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.bill_layout_master);
        relativelayout.removeView(keyBoardCustom);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if (value) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp.leftMargin = 30;
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lp.rightMargin = 30;
        }
        lp.bottomMargin = 30;
        keyBoardCustom.setPadding(10, 10, 10, 10);
        relativelayout.addView(keyBoardCustom, lp);
    }

    private void dateBySearch() {
        try {
            String fromDateStr = fromDate.getText().toString();
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date fromDateFormat = format.parse(fromDateStr);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String from = simpleDateFormat.format(fromDateFormat);

            String toDateStr = toDate.getText().toString();
            DateFormat format2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date toDateFormat = format2.parse(toDateStr);
            SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String to = simpleDateFormat2.format(toDateFormat);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = sdf.parse(from);
            Date d2 = sdf.parse(to);


            if(d1.compareTo(d2) > 0) {
                Util.messageBar(this, getString(R.string.date_validation));
            }
            else {
                Util.LoggingQueue(this, "MonthlySalesReportActivity", "date search:" + from + " , " + to);
                Intent intent = new Intent(this, BillByFromToDateActivity.class);
                intent.putExtra("fromBills", from);
                intent.putExtra("toBills", to);
                intent.putExtra("search", "date");
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "MonthlySalesReportActivity", "error:" + e.toString());
        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, TransactionsSubmenuActivity.class));
        Util.LoggingQueue(this, "MonthlySalesReportActivity", "On back pressed calling");
        finish();
    }

    class KeyList implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {
            if (primaryCode == 8) {

            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    class KeyListAlpha implements KeyboardView.OnKeyboardActionListener {
        public void onKey(View v, int keyCode, KeyEvent event) {

        }

        public void onText(CharSequence text) {

        }

        public void swipeLeft() {

        }

        public void onKey(int primaryCode, int[] keyCodes) {

        }

        public void swipeUp() {

        }

        public void swipeDown() {

        }

        public void swipeRight() {

        }

        public void onPress(int primaryCode) {
            if (primaryCode == 8) {

            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;

            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeAllDialogs();
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }

        try {
            if ((fromDialog != null) && fromDialog.isShowing()) {
                fromDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            fromDialog = null;
        }

        try {
            if ((toDialog != null) && toDialog.isShowing()) {
                toDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            toDialog = null;
        }
    }

}