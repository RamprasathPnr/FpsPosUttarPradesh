package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.InspectionFindingActivity;
import com.omneagate.activity.R;

/**
 * This dialog will appear on the time of user logout
 */
public class InspectionOverallRemarkDialog extends Dialog implements
        View.OnClickListener {
    private final Activity context;  //    Context from the user
    InspectionFindingActivity inspectionFindingActivity;
    private RadioGroup radioGroup;
    EditText urlText;
    TextView urlTextlabel;

    /*Constructor class for this dialog*/
    public InspectionOverallRemarkDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_inspection_overall_remark);
        inspectionFindingActivity = new InspectionFindingActivity();
        setCancelable(false);
        urlText = (EditText) findViewById(R.id.editTextUrl);
        urlTextlabel = (TextView) findViewById(R.id.editTextUrl_label);
        TextView textViewNwTitle = (TextView) findViewById(R.id.textViewNwTitle);
        textViewNwTitle.setText(R.string.overall_inspection_finding);
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.buttonNwCancel);
        Util.setTamilText(cancelButton, R.string.cancel);
        cancelButton.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButton1) {
//                    Toast.makeText(context, "radioButton1", Toast.LENGTH_SHORT).show();
                    inspectionFindingActivity.overallStatus = 1;
                    urlText.setVisibility(View.GONE);
                    urlTextlabel.setVisibility(View.GONE);
                }
                if (checkedId == R.id.radioButton2) {
//                    Toast.makeText(context, "radioButton2", Toast.LENGTH_SHORT).show();
                    inspectionFindingActivity.overallStatus = 2;
                    urlText.setVisibility(View.VISIBLE);
                    urlTextlabel.setVisibility(View.VISIBLE);
                }
            }
        });
        inspectionFindingActivity.overallStatus = 0;
        inspectionFindingActivity.fineAmount = 0.0;
        inspectionFindingActivity.overallRemark = "";


        /*//set the switch to ON
        mySwitch.setChecked(true);
        //attach a listener to check for changes in state
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    switchStatus.setText("Switch is currently ON");
                    inspectionFindingActivity.overallStatus = true;
//                    mySwitch.setText(R.string.ok);
                } else {
//                    switchStatus.setText("Switch is currently OFF");
                    inspectionFindingActivity.overallStatus = false;
//                    mySwitch.setText(R.string.not_ok);
                }
            }
        });

        //check the current state before we display the screen
        if(mySwitch.isChecked()){
//            switchStatus.setText("Switch is currently ON");
            inspectionFindingActivity.overallStatus = true;
//            mySwitch.setText(R.string.ok);
        }
        else {
//            switchStatus.setText("Switch is currently OFF");
            inspectionFindingActivity.overallStatus = false;
//            mySwitch.setText(R.string.not_ok);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                if (storeInLocal()) {
                    if (inspectionFindingActivity.overallStatus == 0) {
                        Toast.makeText(context, R.string.enter_overall_status, Toast.LENGTH_SHORT).show();
                    } else if (urlText.getText().toString().trim().equalsIgnoreCase("") && inspectionFindingActivity.overallStatus == 2) {
                        Toast.makeText(context, R.string.enter_fine_amount, Toast.LENGTH_SHORT).show();
                    } else if (inspectionFindingActivity.fineAmount == 0.0 && inspectionFindingActivity.overallStatus == 2) {
                        Toast.makeText(context, R.string.enter_fine_amount, Toast.LENGTH_SHORT).show();
                    } else if (inspectionFindingActivity.overallRemark.trim().equalsIgnoreCase("")) {
                        Toast.makeText(context, R.string.enter_overall_comment, Toast.LENGTH_SHORT).show();
                    } else {
                        inspectionFindingActivity.submitReport(context);
                        dismiss();
                    }
                }
                break;
            case R.id.buttonNwCancel:
                inspectionFindingActivity.overallStatus = 0;
                inspectionFindingActivity.fineAmount = 0.0;
                inspectionFindingActivity.overallRemark = "";
                InputMethodManager imm = (InputMethodManager) context.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                dismiss();
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilTextInChangeURL(TextView textName, int id) {
        Log.e("ChangeURLDialog", "Util.setTamilTextInChangeURL , id passing , " + context.getString(id));
        if (GlobalAppState.language.equals("ta")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            //textName.setText(context.getString(id));
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }

    /**
     * Store changed ip in shared preference
     * returns true if value present else false
     */
    private boolean storeInLocal() {

        EditText urlText2 = (EditText) findViewById(R.id.editTextUrl2);
        if (!urlText.getText().toString().equalsIgnoreCase("")) {
            inspectionFindingActivity.fineAmount = Double.parseDouble(urlText.getText().toString());
        }
        inspectionFindingActivity.overallRemark = urlText2.getText().toString();

        /*String url = urlText.getText().toString().trim();
        if (StringUtils.isEmpty(url) || url.length() < 4) {
            return false;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(urlText.getWindowToken(), 0);
        Util.LoggingQueue(context, "Changed", url);
        FPSDBHelper.getInstance(context).updateMaserData("serverUrl", url);*/
        return true;
    }
}