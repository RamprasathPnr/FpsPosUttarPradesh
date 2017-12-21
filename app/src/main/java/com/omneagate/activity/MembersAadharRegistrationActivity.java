package com.omneagate.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.client.android.Intents;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.BaseDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BeneficiaryUpdateDto;
import com.omneagate.DTO.DeviceStatusRequest;
import com.omneagate.DTO.EnumDTO.KeyBoardEnum;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.Util.AadhaarVerhoeffAlgorithm;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NetworkConnection;
import com.omneagate.Util.NetworkUtil;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.GenderSelectListAdapter;
import com.omneagate.service.HttpClientWrapper;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created for Bill By DateActivity
 */
public class MembersAadharRegistrationActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    RelativeLayout keyBoardCustom;
    KeyboardView keyview;
    KeyBoardEnum keyBoardFocused;
    TextView aadharNumber1, aadharNumber2, aadharNumber3;
    String str1, str2, id;
    AadharSeedingDto beneMemberUpdate;
    String headAadhar, totalMembers;
    LinearLayout middleLayListView, middleLayTxtView;


    //Date Picker for Date of Birth of child member registration
    // DateOfBirthSelectionDialog dateDialog;
    TextView dateOfBirthText;
    RelativeLayout aadharCardNoLay;
    LinearLayout childDetailsLay;
    BeneficiaryDto beneficiaryDto;
    BeneficiaryUpdateDto beneUpdate;
    Boolean isChildSelected = false;
    Boolean isPhotocopyProofProvided = false;
    CheckBox photocopyProvidedCheckBox;
    //RadioButton genderSelectedRadioButton;
    CheckBox childMemberSelectionCheckBox;
    char selectedGenderChar;
    Long selectedDateLong;
    Spinner genderSelectSpinner;
    int maxAge = 0;

    //String selectedId;
    // Zbar variables
    /*private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    ImageScanner scanner;
    private boolean barcodeScanned = false;
    private boolean previewing = true;*/

    BeneficiaryDto beneficiaryDtoForChild;
    Dialog datePickerDialog;
    String TAG = "MembersAadharRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_aadhar_registration);
        Util.LoggingQueue(this, "MembersAadharRegistrationActivity ", "onCreate called");


        try {
            str1 = getIntent().getStringExtra("QrCode");
            str2 = getIntent().getStringExtra("UpdateString");
            id = getIntent().getStringExtra("BeneficiaryId");


            beneficiaryDto = FPSDBHelper.getInstance(this).beneficiaryFromOldCard(str1);
            beneficiaryDtoForChild = beneficiaryDto;

            headAadhar = getIntent().getStringExtra("FamilyHeadAadhar");
            totalMembers = getIntent().getStringExtra("TotalMembers");
            String updateString = getIntent().getStringExtra("UpdateString");

            Util.LoggingQueue(this, "MembersAadharRegistrationActivity ", "beneficiaryDto ->" + beneficiaryDto);

            if (updateString != null) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                beneUpdate = gson.fromJson(updateString, BeneficiaryUpdateDto.class);
                Util.LoggingQueue(this, "MembersAadharRegistrationActivity ", "if old card exists beneUpdate ->" + beneUpdate);

            } else {
                beneUpdate = new BeneficiaryUpdateDto();
                Util.LoggingQueue(this, "MembersAadharRegistrationActivity ", "No old card found ->");


            }

            configureData();
        }catch (Exception e){
            Util.LoggingQueue(this, "MembersAadharRegistrationActivity ", "onCreate called Exception ->"+e);

        }

    }


    private void setStatusCheck(Bundle message) {
        String response = message.getString(FPSDBConstants.RESPONSE_DATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        DeviceStatusRequest deviceRegistrationResponse = gson.fromJson(response,
                DeviceStatusRequest.class);
        // Log.i("Resp", response);
        if (deviceRegistrationResponse.isActive()) {
            Util.storePreferenceApproved(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
//            Util.messageBar(com.omneagate.activity.RegistrationActivity.this, getString(R.string.deviceRegistration));
            Util.storePreferenceApproved(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    /*Data from server has been set inside this function*/
    private void configureData() {
        try {

            setUpPopUpPage();
            // Util.LoggingQueue(this, "Aadhar registration activity", "Setting up main page");
            Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.members_aadhar_registration);
            Util.setTamilText((TextView) findViewById(R.id.regAadharLabel), R.string.reg_aadhar_numbers);
            Util.setTamilText((TextView) findViewById(R.id.aadharLabel), R.string.aadharNo);
            Util.setTamilText((TextView) findViewById(R.id.scanAadharLabel), R.string.scan_aadhar_card);


            Util.setTamilText((TextView) findViewById(R.id.dobLabel), R.string.dob);
            Util.setTamilText((TextView) findViewById(R.id.genderLabel), R.string.gender);


            Util.setTamilText((CheckBox) findViewById(R.id.isPhotocopyProvidedCheckBox), R.string.received_dob_photocopy);


            Util.setTamilText((CheckBox) findViewById(R.id.isChildMemberCheckBox), R.string.ischildmember);

            Util.setTamilText((Button) findViewById(R.id.btnSubmit), R.string.submit);
            Util.setTamilText((Button) findViewById(R.id.btnCancel), R.string.cancel);

            aadharNumber1 = (TextView) findViewById(R.id.firstText);
            aadharNumber2 = (TextView) findViewById(R.id.secondText);
            aadharNumber3 = (TextView) findViewById(R.id.thirdText);
            dateOfBirthText = (TextView) findViewById(R.id.DOBText);
            photocopyProvidedCheckBox = (CheckBox) findViewById(R.id.isPhotocopyProvidedCheckBox);
            childMemberSelectionCheckBox = (CheckBox) findViewById(R.id.isChildMemberCheckBox);

            genderSelectSpinner = (Spinner) findViewById(R.id.genderSelectList);
            ArrayList<String> genderList = new ArrayList<String>();
            genderList.add("Select");
            genderList.add("Male");
            genderList.add("Female");
            genderList.add("Other");

            GenderSelectListAdapter customSpinnerAdapter = new GenderSelectListAdapter(this, genderList);

            genderSelectSpinner.setAdapter(customSpinnerAdapter);
            genderSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    String item = parent.getItemAtPosition(position).toString();


                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, " ", "Gender Selected ->" + item);

                    if (position == 1)
                        selectedGenderChar = 'M';
                    else if (position == 2)
                        selectedGenderChar = 'F';
                    else if (position == 3)
                        selectedGenderChar = 'O';

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedGenderChar = ' ';

                }
            });

            ((LinearLayout) findViewById(R.id.dataOfBirthTextLay)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, " ", "show DOB Dialog  ->");

                    createDateOfBirthDialog();


                }
            });

            aadharCardNoLay = (RelativeLayout) findViewById(R.id.aadharCardNoLay);
            childDetailsLay = (LinearLayout) findViewById(R.id.childDetailsLay);


//            aadharContents = (TextView) findViewById(R.id.memberAadharContents);
//            aadharContents.setMovementMethod(new ScrollingMovementMethod());

            middleLayListView = (LinearLayout) findViewById(R.id.middleLayoutTwo);
//            middleLayTxtView = (LinearLayout) findViewById(R.id.middleLayoutTxtView);
//            spinnerLayout = (LinearLayout) findViewById(R.id.spinnerLayout);
            listenersForEditText();
            keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
            Keyboard keyboard = new Keyboard(this, R.layout.keyboard);
            //create KeyboardView object
            keyview = (KeyboardView) findViewById(R.id.customkeyboard);
            //attache the keyboard object to the KeyboardView object
            keyview.setKeyboard(keyboard);
            //show the keyboard
            keyview.setVisibility(KeyboardView.GONE);
            keyview.setPreviewEnabled(false);
            //take the keyboard to the front
            keyview.bringToFront();
            //register the keyboard to receive the key pressed
            keyview.setOnKeyboardActionListener(new KeyList());
            ((TextView) findViewById(R.id.membersCount)).setText(totalMembers);
            initializeValues();
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        } finally {
            findViewById(R.id.imageViewBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            findViewById(R.id.isChildMemberCheckBox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        dateOfBirthText.setText("");
                        genderSelectSpinner.setSelection(0);

                        aadharCardNoLay.setVisibility(View.GONE);
                        childDetailsLay.setVisibility(View.VISIBLE);
                        isChildSelected = true;
                        ((TextView) findViewById(R.id.aadharLabel)).setVisibility(View.GONE);
                        photocopyProvidedCheckBox.setChecked(false);
                        // genderSelectedRadioButton.setChecked(true);

                    } else {
                        Util.setTamilText((TextView) findViewById(R.id.aadharLabel), R.string.aadharNo);
                        isChildSelected = false;
                        aadharCardNoLay.setVisibility(View.VISIBLE);
                        childDetailsLay.setVisibility(View.GONE);
                        photocopyProvidedCheckBox.setChecked(false);
                        ((TextView) findViewById(R.id.aadharLabel)).setVisibility(View.VISIBLE);

                        //  genderSelectedRadioButton.setChecked(true);

                    }
                    //  Log.e("isChildMemberCheckBox isChildSelected", ""+isChildSelected);


                }
            });


            findViewById(R.id.isPhotocopyProvidedCheckBox).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {
                        isPhotocopyProofProvided = true;
                    } else {
                        isPhotocopyProofProvided = false;
                    }

                }
            });

            findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("btnSubmit isChildSelected", ""+isChildSelected);
                    //Log.e("btnSubmit isPhotocopyProf", ""+isPhotocopyProofProvided);

                    //  if(NetworkUtil.getConnectivityStatus(MembersAadharRegistrationActivity.this) )
                    NetworkConnection network = new NetworkConnection(MembersAadharRegistrationActivity.this);


                    if (SessionId.getInstance().getSessionId()!= null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId()) && networkConnection.isNetworkAvailable()) {
                        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "SalesSummaryWithOutOTPActivity", "isNetworkAvailable() > " + network.isNetworkAvailable());
                        if (isChildSelected) {
                            String dobString = dateOfBirthText.getText().toString();

                            if (!StringUtils.isEmpty(dobString)) {
                                if (!isPhotocopyProofProvided) {
                                    // Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.dob_photocopy_error));
                                    Toast.makeText(MembersAadharRegistrationActivity.this, getString(R.string.dob_photocopy_error), Toast.LENGTH_LONG).show();

                                } else {

                                    if (genderSelectSpinner.getSelectedItemPosition() != 0) {
                                        long l = Long.valueOf(id);
                                        BeneficiaryMemberDto sendingBeneficiaryMemberDto = new BeneficiaryMemberDto();

                                        sendingBeneficiaryMemberDto.setId(l);

                                        sendingBeneficiaryMemberDto.setGender(selectedGenderChar);
                                        /*try {
                                            DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                            Date date1 = df1.parse(dobString);
                                            sendingBeneficiaryMemberDto.setDob(date1.getTime());
                                        } catch(Exception e) {}*/

                                        Log.e(TAG,"selectedDateLong..."+selectedDateLong);
                                        sendingBeneficiaryMemberDto.setDob(selectedDateLong);
                                        sendingBeneficiaryMemberDto.setBeneficiaryDto(beneficiaryDto);
                                        sendingBeneficiaryMemberDto.setAdult(false);
                                        sendingBeneficiaryMemberDto.setUid("");

                                        Log.e("Child Submit", "sendingBeneficiaryMemberDto => " + sendingBeneficiaryMemberDto);
                                        Log.e("Child Submit", "sendingBeneficiaryDto => " + beneficiaryDto);


                                        try {
                                            if (NetworkUtil.getConnectivityStatus(MembersAadharRegistrationActivity.this) == 0) {
                                                Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.no_connectivity));

                                            } else {
                                                String membersAadharUpdate = new Gson().toJson(sendingBeneficiaryMemberDto);
                                                Log.e("Date of birth details", "gson sending....." + membersAadharUpdate);


                                                StringEntity stringEntity = new StringEntity(membersAadharUpdate, HTTP.UTF_8);
                                                httpConnection = new HttpClientWrapper();
                                                String url = "/bene/child/member/create";
                                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "Membersaadharregistrationactivity",
                                                        "Sending Child registration request to FPS server" + stringEntity);
                                                progressBar = new CustomProgressDialog(MembersAadharRegistrationActivity.this);
                                                progressBar.setCancelable(false);
                                                progressBar.show();
                                                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                                                        SyncHandler, RequestType.POST, stringEntity, MembersAadharRegistrationActivity.this);

                                            }

                                        } catch (Exception e) {

                                            //  Log.e("Child registration request to FPS server Exection", ""+e);
                                            // Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.connectionRefused));

                                        }


                                    } else {
                                        // Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.gender_selection_error));
                                        Toast.makeText(MembersAadharRegistrationActivity.this, R.string.gender_selection_error, Toast.LENGTH_LONG).show();


                                    }


                                }


                            } else {
                                photocopyProvidedCheckBox.setChecked(false);
                                //  genderSelectedRadioButton.setChecked(true);

                                isPhotocopyProofProvided = false;
                                //Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.dob_empty_error));
                                Toast.makeText(MembersAadharRegistrationActivity.this, R.string.dob_empty_error, Toast.LENGTH_LONG).show();

                            }

                        } else {

                            String cardNumber1 = aadharNumber1.getText().toString();
                            String cardNumber2 = aadharNumber2.getText().toString();
                            String cardNumber3 = aadharNumber3.getText().toString();

                            if (StringUtils.isEmpty(cardNumber1) && StringUtils.isEmpty(cardNumber2) && StringUtils.isEmpty(cardNumber3)) {
                                Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.aadhar_empty));
                            } else {
                                if (cardNumber1.length() != 4 || cardNumber2.length() != 4 || cardNumber3.length() != 4) {
                                    Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.invalidAadharNo));
//                            Util.LoggingQueue(this, "Ration Card Registration", "Card number length invalid");
                                    return;
                                }
                                if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("0")) {
                                    Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.aadharNumberZero));
                                    return;
                                }
                                if (String.valueOf(cardNumber1.charAt(0)).equalsIgnoreCase("1")) {
                                    Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.aadharNumberOne));
                                    return;
                                }
                                String cardNumber = cardNumber1 + cardNumber2 + cardNumber3;
                                AadhaarVerhoeffAlgorithm aadhaarVerhoeffAlgorithm = new AadhaarVerhoeffAlgorithm();
                                Boolean isAadharNumber = aadhaarVerhoeffAlgorithm.validateVerhoeff(cardNumber);
                                if (!isAadharNumber) {
                                    Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.checksumValidationFail));
                                    return;
                                }
                                checkValue(cardNumber);
                            }
                        }


                    }else{
                        Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.noNetworkConnection));

                    }

                }
            });

            findViewById(R.id.scanAadharLay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchQRScanner();
                }
            });

        }
    }

    public void ageCalculator(int year, int month, int day) {

        try {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "ageCalculator() calculate age for ->" + day + month + year);

            String strAgeLimit = "" + FPSDBHelper.getInstance(this).getMasterData("ageLimit");

            if (strAgeLimit != null && StringUtils.isNotEmpty(strAgeLimit) && (!strAgeLimit.equalsIgnoreCase("null"))) {
                Log.e("maas", "strAgeLimit..." + strAgeLimit + " ");
                maxAge = Integer.parseInt(strAgeLimit);

                Calendar presentCalendar = new GregorianCalendar();
//                presentCalendar.add(Calendar.MONTH, 1);

                LocalDate selectedDOB = new LocalDate(year, month + 1, day);

                LocalDate presentDate = new LocalDate(presentCalendar.get(Calendar.YEAR), presentCalendar.get(Calendar.MONTH)+1, presentCalendar.get(Calendar.DAY_OF_MONTH));

                Period period = new Period(selectedDOB, presentDate, PeriodType.yearMonthDay());

                int totalYears = period.getYears();
                int totalMonths = period.getMonths();
                int totalDays = period.getDays();

                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "Calculated AGE : -> " + period.getYears() + " years and " +
                        period.getMonths() + " months" + " -  > day" + period.getDays());

                if (totalYears == maxAge) {

                    if (totalDays == 0 && totalMonths == 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String str = sdf.format(datePickerCalender.getTime());
                        dateOfBirthText.setText(str);
                    } else {
                        Toast.makeText(MembersAadharRegistrationActivity.this, getString(R.string.dob_agelimit_error, maxAge), Toast.LENGTH_LONG).show();
                        dateOfBirthText.setText("");

                    }

                } else if (totalYears == 0) {

                    if (totalDays < 0 || totalMonths < 0) {
                        Toast.makeText(MembersAadharRegistrationActivity.this, R.string.dob_wrong, Toast.LENGTH_LONG).show();
                        dateOfBirthText.setText("");
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String str = sdf.format(datePickerCalender.getTime());
                        dateOfBirthText.setText(str);
                    }

                } else if (totalYears < maxAge && !(totalYears < 0)) {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    String str = sdf.format(datePickerCalender.getTime());
                    dateOfBirthText.setText(str);

                } else if (totalYears > maxAge) {
                    dateOfBirthText.setText("");

                    Toast.makeText(MembersAadharRegistrationActivity.this, getString(R.string.dob_agelimit_error, maxAge), Toast.LENGTH_LONG).show();

                } else if (totalYears < 0) {

                    Toast.makeText(MembersAadharRegistrationActivity.this, R.string.dob_wrong, Toast.LENGTH_LONG).show();
                    dateOfBirthText.setText("");
                }

                selectedDateLong = datePickerCalender.getTimeInMillis();

                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "Final selected date in long value  ->" + selectedDateLong);
            }
            }catch(Exception e){
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "ageCalculator() Exception" + e);

            }



    }


    // Calling QR Scanner
    private void launchQRScanner() {
        String packageString = getApplicationContext().getPackageName();
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage(packageString);
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    /**
     * QR code response received for card
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        String contents = data.getStringExtra(Intents.Scan.RESULT);
                        beneMemberUpdate.setScannedQRData(contents);
                        if (contents.contains("<PrintLetterBarcodeData")) {
                            String resultString = null;
                            StringBuilder sb = new StringBuilder(contents);
                            if ((sb.charAt(1) == '/')) {
                                sb.deleteCharAt(1);
                                resultString = sb.toString();
                            } else {
                                resultString = contents;
                            }
                            xmlParsing(resultString);
                        } else {
                            stringParsing(contents);
                        }
                        if(beneMemberUpdate.getUid() != null) {
                            String aadharNum = beneMemberUpdate.getUid();
                            aadharNumber1.setText(aadharNum.substring(0, 4));
                            aadharNumber2.setText(aadharNum.substring(4, 8));
                            aadharNumber3.setText(aadharNum.substring(8, 12));
                        }
                    } catch (Exception e) {
                        Util.messageBar(this, getString(R.string.qrCodeInvalid));
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(QRCodeSalesActivity.class.getSimpleName(), "Scan cancelled");
                }
                break;

            default:
                break;
        }
    }

    private void stringParsing(String text) {

        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> text" + text);
        String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                state = "", pc = "", dob = "";
        String[] strArr = text.split(",");
        for (int i = 0; i < strArr.length; i++) {
            try {
                Log.e("mara", "strArr contents" + strArr[i].toString());
                String element = strArr[i].toString();
                String[] strArr2 = element.split(":");

                if (strArr2[0].equalsIgnoreCase(" aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase(" DOB")) {
                    dob = strArr2[1];
                }

                if (strArr2[0].equalsIgnoreCase("aadhaar no")) {
                    uid = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Name")) {
                    name = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("Gender")) {
                    gender = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("YOB")) {
                    yob = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("co")) {
                    co = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("house")) {
                    house = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("street")) {
                    street = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("lmark")) {
                    lm = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("loc")) {
                    loc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("vtc")) {
                    vtc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("po")) {
                    po = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("dist")) {
                    dist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("subdist")) {
                    subdist = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("state")) {
                    state = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("pc")) {
                    pc = strArr2[1];
                }
                if (strArr2[0].equalsIgnoreCase("DOB")) {
                    dob = strArr2[1];
                }
            } catch (Exception e) {
                Log.e("mara", "string exception" + e);
            }
        }

        if(!uid.equalsIgnoreCase("")) {
            beneMemberUpdate.setAadhaarNum(Long.parseLong(uid));
            beneMemberUpdate.setUid(uid);
        }
        beneMemberUpdate.setName(name);
        beneMemberUpdate.setGender(gender.charAt(0));
        beneMemberUpdate.setCo(co);
        beneMemberUpdate.setHouse(house);
        beneMemberUpdate.setStreet(street);
        beneMemberUpdate.setLm(lm);
        beneMemberUpdate.setLoc(loc);
        beneMemberUpdate.setVtc(vtc);
        beneMemberUpdate.setPo(po);
        beneMemberUpdate.setDist(dist);
        beneMemberUpdate.setSubdist(subdist);
        beneMemberUpdate.setState(state);
        beneMemberUpdate.setPc(pc);
        beneMemberUpdate.setDateOfBirth(dob);
        if (!yob.equalsIgnoreCase("")) {
            beneMemberUpdate.setYob(Long.parseLong(yob));
        }
        /*try {
            if (!dob.equalsIgnoreCase("")) {
                SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = sourceFormat.parse(dob);
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "date1 ->" + date1);

                SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                String dateStr = targetFormat.format(date1);
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "dateStr ->" + dateStr);

                Date date2 = targetFormat.parse(dateStr);
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "date2 ->" + date2);

                beneMemberUpdate.setDob(date2.getTime());
            }
        } catch (Exception e) {
            try {
                if (!dob.equalsIgnoreCase("")) {
                    SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = sourceFormat.parse(dob);
                    SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    String dateStr = targetFormat.format(date1);
                    Date date2 = targetFormat.parse(dateStr);
                    beneMemberUpdate.setDob(date2.getTime());
                }
            } catch (Exception e2) {
            }
        }*/

        Log.e("UIDValue", uid);
        Log.e("name", name);
        Log.e("gender", gender);
        Log.e("yob", yob);
        Log.e("co", co);
        Log.e("house", house);
        Log.e("street", street);
        Log.e("lm", lm);
        Log.e("loc", loc);
        Log.e("vtc", vtc);
        Log.e("po", po);
        Log.e("dist", dist);
        Log.e("subdist", subdist);
        Log.e("state", state);
        Log.e("pc", pc);
        Log.e(" dob", dob);

        try {

            if (dob != null && !dob.isEmpty()) {
                if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                    // Pattern dd/MM/yyyy
                    DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd/MM/yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());
                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                    // Pattern dd-MM-yyyy
                    DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd-MM-yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());

                } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                    // Pattern yyyy/MM/dd
                    DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob dd MM yyyy ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy/MM/dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy-MM-dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());

                } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                    // Pattern yyyy-MM-dd
                    DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                    Date date1 = df1.parse(dob);
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob yyyy MM dd ->" + dob
                            + " Time in millisec -> " + date1.getTime());
                    beneMemberUpdate.setDob(date1.getTime());

                } else {

                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "stringParsing() called -> dob Unknown Pattern ->" + dob
                    );
                }
            } else {
                beneMemberUpdate.setDob(null);
            }


            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String date = sdf.format(gc.getTime());
            Date createdDate = sdf.parse(date);
            beneMemberUpdate.setCreatedDate(createdDate.getTime());


        } catch (Exception e) {

        }

    }

    private void xmlParsing(String xmlData) {
        try {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called xmlData ->" + xmlData);

            String xmlRecords = xmlData.replaceAll("&", "&amp;").replaceAll("'", "&apos;").replace("?\"", "\"?");
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document dom = db.parse(is);
            NodeList l = dom.getElementsByTagName("PrintLetterBarcodeData");
            for (int j = 0; j < l.getLength(); ++j) {
                Node prop = l.item(j);
                NamedNodeMap attr = prop.getAttributes();
                if (null != attr) {
                    //Node nodeUid = attr.getNamedItem("uid");
                    String uid = "", name = "", gender = "", yob = "", co = "", house = "", street = "", lm = "", loc = "", vtc = "", po = "", dist = "", subdist = "",
                            state = "", pc = "", dob = "";

                    try {
                        uid = attr.getNamedItem("uid").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        name = attr.getNamedItem("name").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        gender = attr.getNamedItem("gender").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        yob = attr.getNamedItem("yob").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        co = attr.getNamedItem("co").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        house = attr.getNamedItem("house").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        street = attr.getNamedItem("street").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        lm = attr.getNamedItem("lm").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        loc = attr.getNamedItem("loc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        vtc = attr.getNamedItem("vtc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        po = attr.getNamedItem("po").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        dist = attr.getNamedItem("dist").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        subdist = attr.getNamedItem("subdist").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        state = attr.getNamedItem("state").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        pc = attr.getNamedItem("pc").getNodeValue();
                    } catch (Exception e) {
                    }

                    try {
                        dob = attr.getNamedItem("dob").getNodeValue();
                        //  Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob" + dob);

                    } catch (Exception e) {
                    }

//                    aadhaarSeedingDto.setRationCardNumber(rationcardNo);
                    if(!uid.equalsIgnoreCase("")) {
                        beneMemberUpdate.setAadhaarNum(Long.parseLong(uid));
                        beneMemberUpdate.setUid(uid);
                    }
                    beneMemberUpdate.setName(name);
                    beneMemberUpdate.setGender(gender.charAt(0));
                    beneMemberUpdate.setCo(co);
                    beneMemberUpdate.setHouse(house);
                    beneMemberUpdate.setStreet(street);
                    beneMemberUpdate.setLm(lm);
                    beneMemberUpdate.setLoc(loc);
                    beneMemberUpdate.setVtc(vtc);
                    beneMemberUpdate.setPo(po);
                    beneMemberUpdate.setDist(dist);
                    beneMemberUpdate.setSubdist(subdist);
                    beneMemberUpdate.setState(state);
                    beneMemberUpdate.setPc(pc);
                    beneMemberUpdate.setDateOfBirth(dob);
                    if (!yob.equalsIgnoreCase("")) {
                        beneMemberUpdate.setYob(Long.parseLong(yob));
                    }


                   /* try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            beneMemberUpdate.setDob(date2.getTime());
                        }
                    } catch (Exception e) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy/MM/dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            beneMemberUpdate.setDob(date2.getTime());
                        }
                    } catch (Exception e) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            beneMemberUpdate.setDob(date2.getTime());
                        }
                    } catch (Exception e2) {
                    }

                    try {
                        if (!dob.equalsIgnoreCase("")) {
                            SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = sourceFormat.parse(dob);
                            SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
                            String dateStr = targetFormat.format(date1);
                            Date date2 = targetFormat.parse(dateStr);
                            beneMemberUpdate.setDob(date2.getTime());
                        }
                    } catch (Exception e2) {
                    }*/

                    Log.e("UIDValue", uid);
                    Log.e("name", name);
                    Log.e("gender", gender);
                    Log.e("yob", yob);
                    Log.e("co", co);
                    Log.e("house", house);
                    Log.e("street", street);
                    Log.e("lm", lm);
                    Log.e("loc", loc);
                    Log.e("vtc", vtc);
                    Log.e("po", po);
                    Log.e("dist", dist);
                    Log.e("subdist", subdist);
                    Log.e("state", state);
                    Log.e("pc", pc);
                    Log.e("dob", dob);


                    try {


                        // Possible Date Java Date Formats
                        /**
                         *    put("^\\d{8}$", "yyyyMMdd");
                         put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
                         put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
                         put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
                         put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
                         put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
                         put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");

                         */

                        if (dob != null && !dob.isEmpty()) {

                            if (dob.matches("(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)")) {
                                // Pattern dd/MM/yyyy
                                DateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd/MM/yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());

                                beneMemberUpdate.setDob(date1.getTime());
                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-((19|20)\\d\\d)")) {
                                // Pattern dd-MM-yyyy
                                DateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd-MM-yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                beneMemberUpdate.setDob(date1.getTime());

                            } else if (dob.matches("(0?[1-9]|[12][0-9]|3[01]) (0?[1-9]|1[012]) ((19|20)\\d\\d)")) {
                                // Pattern yyyy/MM/dd
                                DateFormat df1 = new SimpleDateFormat("dd MM yyyy");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob dd MM yyyy ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                beneMemberUpdate.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)/(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy/MM/dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                beneMemberUpdate.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d)-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy-MM-dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                beneMemberUpdate.setDob(date1.getTime());

                            } else if (dob.matches("((19|20)\\d\\d) (0?[1-9]|1[012]) (0?[1-9]|[12][0-9]|3[01])")) {
                                // Pattern yyyy-MM-dd
                                DateFormat df1 = new SimpleDateFormat("yyyy MM dd");
                                Date date1 = df1.parse(dob);
                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob yyyy MM dd ->" + dob
                                        + " Time in millisec -> " + date1.getTime());
                                beneMemberUpdate.setDob(date1.getTime());

                            } else {

                                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "xmlParsing() called -> dob Unkown Pattern ->" + dob
                                );
                            }
                        } else {
                            beneMemberUpdate.setDob(null);
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        GregorianCalendar gc = new GregorianCalendar();
                        String date = sdf.format(gc.getTime());
                        Date createdDate = sdf.parse(date);
                        beneMemberUpdate.setCreatedDate(createdDate.getTime());

                    } catch (Exception e) {

                    }
                    /*StringBuffer stringBuffer = new StringBuffer();
                    if(!name.equalsIgnoreCase("")) {
                        stringBuffer.append("<html><font color=\"red\">hello world!</font>").append("\n").append(name).append("\n\n");
                    }
                    if(!gender.equalsIgnoreCase("")) {
                        if (gender.equalsIgnoreCase("M")) {
                            stringBuffer.append("GENDER").append("\n").append("Male").append("\n\n");
                        } else {
                            stringBuffer.append("GENDER").append("\n").append("Female").append("\n\n");
                        }
                    }
                    if(!dob.equalsIgnoreCase("")) {
                        stringBuffer.append("DATE OF BIRTH").append("\n").append(dob).append("\n\n");
                    }
                    if(!yob.equalsIgnoreCase("")) {
                        stringBuffer.append("YEAR OF BIRTH").append("\n").append(yob).append("\n\n");
                    }
                    if(!uid.equalsIgnoreCase("")) {
                        stringBuffer.append("UID").append("\n").append(uid).append("\n\n");
                    }
                    if(!co.equalsIgnoreCase("")) {
                        stringBuffer.append("CARE OF").append("\n").append(co).append("\n\n");
                    }
                    if(!house.equalsIgnoreCase("")) {
                        stringBuffer.append("HOUSE").append("\n").append(house).append("\n\n");
                    }
                    if(!street.equalsIgnoreCase("")) {
                        stringBuffer.append("STREET").append("\n").append(street).append("\n\n");
                    }
                    if(!lm.equalsIgnoreCase("")) {
                        stringBuffer.append("LAND MARK").append("\n").append(lm).append("\n\n");
                    }
                    if(!loc.equalsIgnoreCase("")) {
                        stringBuffer.append("LOCALTY").append("\n").append(loc).append("\n\n");
                    }
                    if(!vtc.equalsIgnoreCase("")) {
                        stringBuffer.append("VILLAGE / TOWN / CITY").append("\n").append(vtc).append("\n\n");
                    }
                    if(!po.equalsIgnoreCase("")) {
                        stringBuffer.append("POST").append("\n").append(po).append("\n\n");
                    }
                    if(!dist.equalsIgnoreCase("")) {
                        stringBuffer.append("DISTRICT").append("\n").append(dist).append("\n\n");
                    }
                    if(!subdist.equalsIgnoreCase("")) {
                        stringBuffer.append("SUB DISTRICT").append("\n").append(subdist).append("\n\n");
                    }
                    if(!state.equalsIgnoreCase("")) {
                        stringBuffer.append("STATE").append("\n").append(state).append("\n\n");
                    }
                    if(!pc.equalsIgnoreCase("")) {
                        stringBuffer.append("PIN CODE").append("\n").append(pc).append("\n\n").append("</html>" );
                    }

                    Log.e("Xml Records", xmlRecords);
                    Log.e(">>>>>>>>>>",">>>>>>>>>");
                    Log.e("Details", stringBuffer.toString());*/
                    /*aadharContents.setText(Html.fromHtml(stringBuffer.toString()));
                    aadharContents.setTextSize(17);
                    aadharContents.setTextColor(Color.BLACK);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeValues() {

        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "initializeValues() called");


        beneMemberUpdate = new AadharSeedingDto();
        int aadharCount = FPSDBHelper.getInstance(this).getMembersAadharCount(id);
        final ArrayList<String> aadharNos = FPSDBHelper.getInstance(this).getMembersAadharNumbers(id);

        if (!headAadhar.equalsIgnoreCase("0")) {


            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "initializeValues() Aadhar head is present");

            if (!aadharNos.contains(headAadhar)) {

                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "" +
                        "Increase Aadhar Count and add head aadhar no in aadhar list");

                aadharCount = aadharCount + 1;
                aadharNos.add(0, headAadhar);
            }
        }

        ((TextView) findViewById(R.id.aadharCount)).setText(String.valueOf(aadharCount));

        ListView listView = (ListView) findViewById(R.id.list);
        String[] values = new String[aadharNos.size()];
        aadharNos.toArray(values);
        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "initializeValues() Aadhar No Array = "+values);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long benefId = FPSDBHelper.getInstance(MembersAadharRegistrationActivity.this).getBeneficiaryIdFromUid(aadharNos.get(position).toString());
                if(benefId != 0) {
                    boolean bfdRegistered = FPSDBHelper.getInstance(MembersAadharRegistrationActivity.this).checkBfd(benefId);
                    if (!bfdRegistered) {
                        String uid = aadharNos.get(position).toString();
                        Intent intent = new Intent(MembersAadharRegistrationActivity.this, TenFingerRegistrationActivity.class);
                        intent.putExtra("AadharNo", uid);
                        intent.putExtra("MemberType", "0");
                        startActivity(intent);
                    } else {
                        Toast.makeText(MembersAadharRegistrationActivity.this, R.string.bfd_already_done, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /*final NoDefaultSpinner mSpinnerRelationship = (NoDefaultSpinner) findViewById(R.id.relationSpinner);
        ArrayList<String> relationList = new ArrayList<String>();
        final List<RelationshipDto> relationshipDtos = FPSDBHelper.getInstance(this).getRelationship();
        for (RelationshipDto relationshipDto : relationshipDtos) {
            try {
                if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                    relationList.add(relationshipDto.getLname());
                } else {
                    relationList.add(relationshipDto.getName());
                }
            }
            catch(Exception e) {
                relationList.add("");
            }
        }
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relationList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerRelationship.setAdapter(adapter2);
        mSpinnerRelationship.setPrompt(getString(R.string.selection));
        mSpinnerRelationship.setFocusable(true);
        mSpinnerRelationship.setFocusableInTouchMode(true);
        mSpinnerRelationship.requestFocus();
        mSpinnerRelationship.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                beneMemberUpdate.setRelationId(relationshipDtos.get(position).getId());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });*/


        aadharNumber1.setText("");
        aadharNumber2.setText("");
        aadharNumber3.setText("");
//        aadharNumber1.requestFocus();

        /*middleLayListView.setVisibility(View.VISIBLE);
        middleLayTxtView.setVisibility(View.GONE);
        spinnerLayout.setVisibility(View.GONE);*/

        /*if (aadharCount >= Integer.parseInt(totalMembers)) {


            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "initializeValues() "
            + " Total Aadhar Count is >= to No of aadhar present in member_aadhr table ");


            ((TextView) findViewById(R.id.aadharLabel)).setVisibility(View.INVISIBLE);
            ((LinearLayout) findViewById(R.id.aadharTextLay)).setVisibility(View.INVISIBLE);
            ((LinearLayout) findViewById(R.id.childDetailsLay)).setVisibility(View.INVISIBLE);

            ((Button) findViewById(R.id.btnSubmit)).setVisibility(View.INVISIBLE);
            ((Button) findViewById(R.id.btnCancel)).setVisibility(View.INVISIBLE);
            ((ImageView) findViewById(R.id.scanAadhar)).setVisibility(View.INVISIBLE);
            ((RelativeLayout) findViewById(R.id.scanAadharLay)).setVisibility(View.INVISIBLE);
            ((CheckBox) findViewById(R.id.isChildMemberCheckBox)).setVisibility(View.INVISIBLE);

        }else{
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "initializeValues() "
                    + " Still aadhar can be added ");
        }*/
    }


    private void checkValue(String uid) {
        try {

            Util.LoggingQueue(MembersAadharRegistrationActivity.this, " MembersAadharRegistrationActivity ", "checkValue() called  uid = " + uid);
            String membersAadharUpdate2 = new Gson().toJson(beneMemberUpdate);
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, " MembersAadharRegistrationActivity ", "GSON beneMemberUpdate ->" + membersAadharUpdate2);

            if (NetworkUtil.getConnectivityStatus(this) == 0) {
                Util.messageBar(this, getString(R.string.no_connectivity));
            } else {
                long aadhar = Long.valueOf(uid);
                long benefId = Long.valueOf(id);

                beneMemberUpdate.setAadhaarNum(aadhar);
                beneMemberUpdate.setUid(uid);
                beneMemberUpdate.setBeneficiaryID(benefId);

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    GregorianCalendar gc = new GregorianCalendar();
                    String date = sdf.format(gc.getTime());
                    Date createdDate = sdf.parse(date);
                    beneMemberUpdate.setCreatedDate(createdDate.getTime());
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, " MembersAadharRegistrationActivity ", "date ->" + date + " createdDate.getTime() -> " + createdDate.getTime());

                } catch (Exception e) {


                }


                String membersAadharUpdate = new Gson().toJson(beneMemberUpdate);
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, " MembersAadharRegistrationActivity ", "GSON beneMemberUpdate ->" + membersAadharUpdate);

                StringEntity stringEntity = new StringEntity(membersAadharUpdate, HTTP.UTF_8);
                httpConnection = new HttpClientWrapper();
                String url = "/aadhaar/seeding/create";
                progressBar = new CustomProgressDialog(this);
                progressBar.setCancelable(false);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.CARD_REGISTRATION,
                        SyncHandler, RequestType.POST, stringEntity, this);
            }
        } catch (Exception e) {

            Util.LoggingQueue(MembersAadharRegistrationActivity.this, " MembersAadharRegistrationActivity ", "checkValue() called  Exception = " + e);

        }
    }

    //   EditText date_display = (EditText) findViewById(R.id.date_display);
    //   EditText year_display = (EditText) findViewById(R.id.year_display);


    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

      /*  if (v.getId() == R.id.date_display) {
            checkVisibility();
            keyBoardFocused = KeyBoardEnum.PREFIX;
            changeLayout(false);
        } else if (v.getId() == R.id.year_display) {
            checkVisibility();
            changeLayout(true);
            keyBoardFocused = KeyBoardEnum.PREFIX;
        }*/


        /*if (v.getId() == R.id.firstText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.thirdText) {
            checkVisibility();
            changeLayout(false);
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }*/
    }

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }


    public void onFocusChange(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

       /* if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } */

        /*if (v.getId() == R.id.firstText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber1.requestFocus();
            keyBoardFocused = KeyBoardEnum.PREFIX;
        } else if (v.getId() == R.id.secondText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber2.requestFocus();
            keyBoardFocused = KeyBoardEnum.SUFFIX;
        } else if (v.getId() == R.id.thirdText && hasFocus) {
            checkVisibility();
            changeLayout(false);
            aadharNumber3.requestFocus();
            keyBoardFocused = KeyBoardEnum.FINALSTRING;
        }*/
    }

    private void listenersForEditText() {
        /*aadharNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber1.getText().toString().length() == 4)     //size as per your requirement
                {
                    aadharNumber2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber2.getText().toString().length() == 4) {
                    aadharNumber3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        aadharNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (aadharNumber3.getText().toString().length() == 4) {
                    keyBoardCustom.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.membersAadharRegMaster);
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
                /*if (keyBoardFocused == KeyBoardEnum.PREFIX) {
                    String text = aadharNumber1.getText().toString();
                    if (aadharNumber1.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber1.setText(text);
                        aadharNumber1.setSelection(text.length());
                    }
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
                    String text = aadharNumber2.getText().toString();
                    if (aadharNumber2.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber2.setText(text);
                        aadharNumber2.setSelection(text.length());
                    } else {
                        aadharNumber1.requestFocus();
                    }
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
                    String text = aadharNumber3.getText().toString();
                    if (aadharNumber3.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        aadharNumber3.setText(text);
                        aadharNumber3.setSelection(text.length());
                    } else {
                        aadharNumber2.requestFocus();
                    }
                }*/
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.PREFIX) {
//                    aadharNumber1.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.SUFFIX) {
//                    aadharNumber2.append("" + ch);
                } else if (keyBoardFocused == KeyBoardEnum.FINALSTRING) {
//                    aadharNumber3.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "processMessage() called message -> " + message + " Type -> " + what);
        try {

            aadharNumber1.setText("");
            aadharNumber2.setText("");
            aadharNumber3.setText("");
            aadharNumber1.requestFocus();


            if (progressBar != null) {
                progressBar.dismiss();
            }




        } catch (Exception e) {
        }

        try {
            if (progressBar != null) {
                progressBar.hide();
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }


       /* if (message.toString().contains("\"statusCode\":500")) {
            Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.connectionRefused));
            resetViewToAadharRegistration();
        }*/

        switch (what) {

            case ERROR_MSG:
                Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.connectionRefused));

                break;



            case CARD_REGISTRATION:

                if (isChildSelected)
                    dateOfBirthSubmissionResponse(message);
                else
                    registrationSubmissionResponse(message);

                break;

            /*case BENEFICIARY_UPDATION:
                Log.e("BENEFICIARY_UPDATION","BENEFICIARY_UPDATION");

                dateOfBirthSubmissionResponse(message);

                break;*/
            default:
                break;
        }
    }

    private void resetViewToAadharRegistration() {

        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "resetViewToAadharRegistration() called ->");

        dateOfBirthText.setText("");
        photocopyProvidedCheckBox.setChecked(false);
        isPhotocopyProofProvided = false;
        childMemberSelectionCheckBox.setChecked(false);
        isChildSelected = false;
        aadharCardNoLay.setVisibility(View.VISIBLE);
        childDetailsLay.setVisibility(View.GONE);
        childMemberSelectionCheckBox.setChecked(false);
        genderSelectSpinner.setSelection(0);
        ((TextView) findViewById(R.id.aadharLabel)).setVisibility(View.VISIBLE);
    }


    private void dateOfBirthSubmissionResponse(Bundle bundle) {
        try {

            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }

            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "dateOfBirthSubmissionResponse() called  bundle ->" + bundle);

            String response = bundle.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BeneficiaryDto beneficiaryDto = gson.fromJson(response, BeneficiaryDto.class);
            BeneficiaryMemberDto beneficiaryMemberDto = gson.fromJson(response, BeneficiaryMemberDto.class);

            dateOfBirthText.setText("");
            photocopyProvidedCheckBox.setChecked(false);
            isPhotocopyProofProvided = false;
            childMemberSelectionCheckBox.setChecked(false);
            if (beneficiaryDto.getStatusCode() == 31003) {
                Toast.makeText(MembersAadharRegistrationActivity.this, R.string.childmember_exceeds, Toast.LENGTH_LONG).show();
            } else if (beneficiaryDto.getStatusCode() == 31004) {
                Toast.makeText(MembersAadharRegistrationActivity.this, getString(R.string.dob_agelimit_error, maxAge), Toast.LENGTH_LONG).show();
            } else if (beneficiaryDto.getStatusCode() == 0) {
                Toast.makeText(MembersAadharRegistrationActivity.this, R.string.childmember_updation_success, Toast.LENGTH_LONG).show();
            } else if (beneficiaryDto.getStatusCode() == 500) {
               // Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.connectionRefused));
                Log.e("500", "" + beneficiaryDto.getStatusCode());
            }else if (beneficiaryDto.getStatusCode() == 12011) {
                Log.e("12011", "" + beneficiaryDto.getStatusCode());
                Toast.makeText(MembersAadharRegistrationActivity.this, R.string.childmember_cant_add, Toast.LENGTH_LONG).show();
            }
            else {
                String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(beneficiaryDto.getStatusCode()));
                if (messageData == null) {
                    messageData = getString(R.string.connectionRefused);
                }
                Toast.makeText(MembersAadharRegistrationActivity.this, messageData, Toast.LENGTH_LONG).show();
            }

            Util.setTamilText((TextView) findViewById(R.id.aadharLabel), R.string.aadharNo);
            isChildSelected = false;
            aadharCardNoLay.setVisibility(View.VISIBLE);
            childDetailsLay.setVisibility(View.GONE);
            childMemberSelectionCheckBox.setChecked(false);
            genderSelectSpinner.setSelection(0);
            ((TextView) findViewById(R.id.aadharLabel)).setVisibility(View.VISIBLE);
            selectedGenderChar = ' ';


        } catch (Exception e) {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "dateOfBirthSubmissionResponse() Exception ->" + e);

        }
    }


    String year_display_str, date_display_str, month_display_str;
    int monthPosition = 0;
    final String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};
    Button month_plusBtn, month_minusBtn, date_plusBtn, date_minusBtn, year_plusBtn, year_minusBtn, setDateBtn, cancelBtn;
    RelativeLayout datePickerDialogLay;
    android.widget.EditText year_display_et, month_display_et, date_display_et;
    MaterialCalendarView materialCalendarView;
    Calendar datePickerCalender;
    View.OnClickListener month_plus_listener, month_minus_listener, date_plus_listener, date_minus_listener, year_plus_listener, year_minus_listener;
    public int startYear = 1950;
    public int endYear = 2400;

    Boolean isMonthChanged = false;

    private void createDateOfBirthDialog() {

        try {


            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "createDateOfBirthDialog() called  ->");

            setDatePickerDialogLay();
            setDatePickerDialogEvents();
            datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            datePickerDialog.setContentView(datePickerDialogLay);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(datePickerDialog.getWindow().getAttributes());
            lp.width = 730;
            datePickerDialog.getWindow().setAttributes(lp);
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();

        }
        catch (Exception e){
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "createDateOfBirthDialog() called  Exception ->" + e);

        }
    }


    public void setDatePickerDialogLay() {


        try {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "setDatePickerDialogLay() called  ->");

            datePickerDialog = new Dialog(this);
            datePickerDialogLay = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
            year_display_et = (android.widget.EditText) datePickerDialogLay.findViewById(R.id.year_display);
            month_display_et = (android.widget.EditText) datePickerDialogLay.findViewById(R.id.month_display);
            date_display_et = (android.widget.EditText) datePickerDialogLay.findViewById(R.id.date_display);
            materialCalendarView = (MaterialCalendarView) datePickerDialogLay.findViewById(R.id.calendarView);
            setDateBtn = (Button) datePickerDialogLay.findViewById(R.id.SetDateTime);
            cancelBtn = (Button) datePickerDialogLay.findViewById(R.id.CancelDialog);
            month_plusBtn = (Button) datePickerDialogLay.findViewById(R.id.month_plus);
            month_minusBtn = (Button) datePickerDialogLay.findViewById(R.id.month_minus);
            date_plusBtn = (Button) datePickerDialogLay.findViewById(R.id.date_plus);
            date_minusBtn = (Button) datePickerDialogLay.findViewById(R.id.date_minus);
            year_plusBtn = (Button) datePickerDialogLay.findViewById(R.id.year_plus);
            year_minusBtn = (Button) datePickerDialogLay.findViewById(R.id.year_minus);

            datePickerCalender = Calendar.getInstance();
            datePickerCalender.set(Calendar.HOUR_OF_DAY, 0);
            datePickerCalender.set(Calendar.MINUTE, 0);
            datePickerCalender.set(Calendar.SECOND, 0);
            datePickerCalender.set(Calendar.MILLISECOND, 0);

            Calendar deviceDate = Calendar.getInstance();
            date_display_et.setText("" + deviceDate.get(Calendar.DAY_OF_MONTH));
            month_display_et.setText("" + new SimpleDateFormat("MMM").format(deviceDate.getTime()));
            year_display_et.setText("" + deviceDate.get(Calendar.YEAR));
            materialCalendarView.setSelectedDate(datePickerCalender);
        } catch (Exception e) {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "setDatePickerDialogLay() Exception e  ->" + e);

        }


    }


    public void setDatePickerDialogEvents() {

        try {


            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "setDatePickerDialogEvents() called  ->");


            materialCalendarView.setOnDateChangedListener(new OnDateChangedListener() {
                @Override
                public void onDateChanged(MaterialCalendarView widget, CalendarDay date) {
                    Calendar c = Calendar.getInstance();
                    c.set(date.getYear(), date.getMonth(), date.getDay());
                    selectedDateLong = c.getTimeInMillis();
                    datePickerCalender.set(date.getYear(), date.getMonth(), date.getDay());

                    month_display_et.setText("" + new SimpleDateFormat("MMM").format(datePickerCalender.getTime()));
                    year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                    date_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.DAY_OF_MONTH)));

                    year_display_str = "" + String.valueOf(datePickerCalender.get(Calendar.YEAR));
                    date_display_str = "" + String.valueOf(datePickerCalender.get(Calendar.DAY_OF_MONTH));
                    monthPosition = datePickerCalender.get(Calendar.MONTH);

                }
            });


            materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
                @Override
                public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {


                    if (isMonthChanged) {

                        isMonthChanged = false;

                        selectedDateLong = datePickerCalender.getTimeInMillis();
                        materialCalendarView.setSelectedDate(datePickerCalender);

                        month_display_et.setText("" + new SimpleDateFormat("MMM").format(datePickerCalender.getTime()));
                        year_display_et.setText("" + datePickerCalender.get(Calendar.YEAR));
                        date_display_et.setText("" + datePickerCalender.get(Calendar.DAY_OF_MONTH));
                        year_display_str = "" + datePickerCalender.get(Calendar.YEAR);
                        date_display_str = "" + datePickerCalender.get(Calendar.DAY_OF_MONTH);
                        monthPosition = datePickerCalender.get(Calendar.MONTH);
                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } else {
                        Calendar c = Calendar.getInstance();
                        c.set(date.getYear(), date.getMonth(), date.getDay());
                        selectedDateLong = c.getTimeInMillis();
                        datePickerCalender.set(date.getYear(), date.getMonth(), date.getDay());

                        selectedDateLong = datePickerCalender.getTimeInMillis();
                        materialCalendarView.setSelectedDate(datePickerCalender);

                        month_display_et.setText("" + new SimpleDateFormat("MMM").format(datePickerCalender.getTime()));
                        year_display_et.setText("" + datePickerCalender.get(Calendar.YEAR));
                        date_display_et.setText("" + datePickerCalender.get(Calendar.DAY_OF_MONTH));
                        year_display_str = "" + datePickerCalender.get(Calendar.YEAR);
                        date_display_str = "" + datePickerCalender.get(Calendar.DAY_OF_MONTH);
                        monthPosition = datePickerCalender.get(Calendar.MONTH);
                        materialCalendarView.setSelectedDate(datePickerCalender);

                    }


                }
            });


            month_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        datePickerCalender.add(Calendar.MONTH, 1);

                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                        date_display_et.setText(String.valueOf(datePickerCalender
                                .get(Calendar.DAY_OF_MONTH)));
                        materialCalendarView.setSelectedDate(datePickerCalender);
                    } catch (Exception e) {
                        Log.e("", e.toString());
                    }
                }
            });
            month_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        datePickerCalender.add(Calendar.MONTH, -1);
                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                        date_display_et.setText(String.valueOf(datePickerCalender
                                .get(Calendar.DAY_OF_MONTH)));
                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } catch (Exception e) {
                        Log.e("", e.toString());
                    }
                }
            });
            date_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        date_display_et.requestFocus();
                        datePickerCalender.add(Calendar.DAY_OF_MONTH, 1);

                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                        date_display_et.setText(String.valueOf(datePickerCalender
                                .get(Calendar.DAY_OF_MONTH)));
                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            date_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        int initialMonth = datePickerCalender.get(Calendar.MONTH);
                        datePickerCalender.add(Calendar.DAY_OF_MONTH, -1);
                        int changedMonth = datePickerCalender.get(Calendar.MONTH);

                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));


                        if (initialMonth != changedMonth) {
                            isMonthChanged = true;
                            date_display_et.setText(String.valueOf(datePickerCalender
                                    .getActualMaximum(Calendar.DAY_OF_MONTH)));
                            Log.e(TAG, "changed month..." +Calendar.DAY_OF_MONTH+" , " + datePickerCalender.getActualMaximum(Calendar.DAY_OF_MONTH));
                            datePickerCalender.set(Calendar.DAY_OF_MONTH, datePickerCalender.getActualMaximum(Calendar.DAY_OF_MONTH));

                        } else {
                            isMonthChanged = false;
                            date_display_et.setText(String.valueOf(datePickerCalender
                                    .get(Calendar.DAY_OF_MONTH)));
                        }


                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            year_plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (datePickerCalender.get(Calendar.YEAR) >= endYear) {

                            datePickerCalender.set(Calendar.YEAR, startYear);

                        } else {
                            datePickerCalender.add(Calendar.YEAR, +1);

                        }

                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                        date_display_et.setText(String.valueOf(datePickerCalender
                                .get(Calendar.DAY_OF_MONTH)));
                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            });
            year_minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (datePickerCalender.get(Calendar.YEAR) <= startYear) {
                            datePickerCalender.set(Calendar.YEAR, endYear);

                        } else {
                            datePickerCalender.add(Calendar.YEAR, -1);

                        }

                        month_display_et.setText(months[datePickerCalender.get(Calendar.MONTH)]);
                        year_display_et.setText(String.valueOf(datePickerCalender.get(Calendar.YEAR)));
                        date_display_et.setText(String.valueOf(datePickerCalender
                                .get(Calendar.DAY_OF_MONTH)));

                        materialCalendarView.setSelectedDate(datePickerCalender);

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });


            setDateBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    year_display_str = year_display_et.getText().toString();
                    date_display_str = date_display_et.getText().toString();
                    month_display_str = month_display_et.getText().toString();
                    String selectedDateStr = "" + new SimpleDateFormat("dd-MM-yyyy").format(datePickerCalender.getTime());
                    datePickerDialog.dismiss();
                    Util.LoggingQueue(MembersAadharRegistrationActivity.this, " ", "selectedDateStr ->" + selectedDateStr + " getTimeInMillis -> " + datePickerCalender.getTimeInMillis());

                    ageCalculator(datePickerCalender.get(Calendar.YEAR), datePickerCalender.get(Calendar.MONTH), datePickerCalender.get(Calendar.DAY_OF_MONTH));

                }
            });

            cancelBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //reset();

                    dateOfBirthText.setText("");
                    datePickerDialog.cancel();
                }
            });


        }
        catch ( Exception e){
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "setDatePickerDialogEvents() called  Exception ->"+e);

        }

    }


    private void registrationSubmissionResponse(Bundle message) {
        try {
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "registrationSubmissionResponse() message ->" + message);

            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            BaseDto beneficiaryDto = gson.fromJson(response, BaseDto.class);


            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "registrationSubmissionResponse() beneficiaryDto = " + beneficiaryDto);

            if (beneficiaryDto.getStatusCode() == 0) {
                Toast.makeText(getBaseContext(), R.string.updateionSuccess, Toast.LENGTH_LONG).show();
                FPSDBHelper.getInstance(this).beneficiaryMemberAadhar(beneMemberUpdate);
                initializeValues();

            } else if (beneficiaryDto.getStatusCode() == 449) {
                Toast.makeText(getBaseContext(), R.string.all_aadhar_registered, Toast.LENGTH_LONG).show();
                aadharNumber1.setText("");
                aadharNumber2.setText("");
                aadharNumber3.setText("");
                aadharNumber1.requestFocus();


            } else if (beneficiaryDto.getStatusCode() == 7006) {
                Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity", "registrationSubmissionResponse() 7006 getStatusCode = " + beneficiaryDto.getStatusCode());

                Toast.makeText(getBaseContext(), R.string.aadhar_available, Toast.LENGTH_LONG).show();

            }  else {
                String messageData = Util.messageSelection(FPSDBHelper.getInstance(this).retrieveLanguageTable(beneficiaryDto.getStatusCode()));
                if (messageData == null) {
                    messageData = getString(R.string.connectionRefused);
                }
                if (messageData.contains("~")) {
                    messageData = messageData.replace("~", beneMemberUpdate.getUid());
                }
                errorNavigation(messageData);
            }
        } catch (Exception e) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e1) {
            }
            Util.LoggingQueue(MembersAadharRegistrationActivity.this, "MembersAadharRegistrationActivity ", "registrationSubmissionResponse() Exception ->" + e);
            Util.messageBar(this, getString(R.string.connectionRefused));

        }
    }


    private void errorNavigation(String messages) {
        if (StringUtils.isEmpty(messages)) {
            messages = getString(R.string.genericDatabaseError);
        }
        Intent intent = new Intent(this, SuccessFailureSeedingActivity.class);
        intent.putExtra("message", messages);
        startActivity(intent);
        finish();
    }

    public void onClose(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        /*if(((FrameLayout) findViewById(R.id.cameraPreview)).getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(MembersAadharRegistrationActivity.this, MembersAadharRegistrationActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("BeneficiaryId", id);
            bundle.putString("TotalMembers", totalMembers);
            bundle.putString("FamilyHeadAadhar", headAadhar);
            bundle.putString("QrCode", str1);
            bundle.putString("UpdateString", str2);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
        else {*/
        Intent intent = new Intent(MembersAadharRegistrationActivity.this, UpdateUserDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("qrCode", str1);
        bundle.putString("beneUpdate", str2);
        intent.putExtras(bundle);
        startActivity(intent);
        Util.LoggingQueue(this, "MembersAadharRegistrationActivity", "Back pressed Called");
        finish();
//        }
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
            if ((datePickerDialog != null) && datePickerDialog.isShowing()) {
                datePickerDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            datePickerDialog = null;
        }
    }


    /*public void onPause() {
        super.onPause();
        releaseCamera();
    }

    *//** A safe way to get an instance of the Camera object. *//*
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                String contents = "";
                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
//                    scanText.setText("barcode result " + sym.getData());
                    contents = sym.getData();
                    barcodeScanned = true;
                }
                ((FrameLayout) findViewById(R.id.cameraPreview)).setVisibility(View.GONE);
                ((RelativeLayout) findViewById(R.id.membersAadharLay)).setVisibility(View.VISIBLE);
                if(!contents.equalsIgnoreCase("")) {
                    try {
                        Log.e("EncryptedUFC", contents);
                        xmlParsing(contents);
                        String aadharNum = beneMemberUpdate.getUid();
                        aadharNumber1.setText(aadharNum.substring(0,4));
                        aadharNumber2.setText(aadharNum.substring(4,8));
                        aadharNumber3.setText(aadharNum.substring(8,12));
                        Log.e("aadhaarSeedingDto ",beneMemberUpdate.toString());
//                        qrResponse(contents);
                    } catch (Exception e) {
                        Util.messageBar(MembersAadharRegistrationActivity.this, getString(R.string.qrCodeInvalid));
                        Util.LoggingQueue(MembersAadharRegistrationActivity.this, "QRcode sales", "QR exception called:" + e.toString());
                        Log.e("QRCodeSalesActivity", "Empty", e);
                    }
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };*/
    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}