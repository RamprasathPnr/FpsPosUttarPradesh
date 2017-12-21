package com.omneagate.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.ErrorCodeDescription;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.InspectionCriteriaDto;
import com.omneagate.DTO.InspectionReportDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.InspectionConstants;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.CompleteObservationDialog;
import com.omneagate.activity.dialog.InspectionOverallRemarkDialog;
import com.omneagate.activity.dialog.UnsavedReportDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
//import com.omneagate.DTO.CardInspectionDto;

public class InspectionFindingActivity extends BaseActivity implements View.OnClickListener {
    private List<InspectionCriteriaDto> findingCreteriaArray;
    TextView shopCode, shopLocation, inspectionDate, shopIncharge, shopInchargeMobile, noReport;
    Button submitButton, nextButton, cancelButton;
    private Long criteriaId;
    private String findingCreteriaSelected = "";
    FpsStoreDto fpsStoreDto;
    private ImageView mIvBack;
    private List<String> inspectionCriteriaList;
    private TextView mTvTitle;
    public static String userName;
    LinearLayout fpsInwardLinearLayout;
    //    public static List<StockInspectionDto> stockInspectionDtoList;
//    public static List<CardInspectionDto> cardInspectionDtoList;
//    public static List<WeighmentInspectionDto> weightInspectionDtoList;
//    public static List<ShopAndOthersDto> shopInspectionDtoList, otherInspectionDtoList;
    CompleteObservationDialog completeObservationDialog;
    InspectionOverallRemarkDialog inspectionOverallRemarkDialog;
    UnsavedReportDialog unsavedReportDialog;
    public static double fineAmount = 0.0;
    public static String overallRemark;
    public static int overallStatus = 0;
    String TAG = "InspectionFindingActivity";
    private NoDefaultSpinner spinner;
    String r = null;
    public static String shop_code ="-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_finding);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        findView();
        getFpsStoreDetail();
        loadViewData();
    }

    private void findView() {
        noReport = (TextView) findViewById(R.id.noReport);
        shopCode = (TextView) findViewById(R.id.txt_shop_code);
        shopLocation = (TextView) findViewById(R.id.txt_location);
        inspectionDate = (TextView) findViewById(R.id.txt_date);
        shopIncharge = (TextView) findViewById(R.id.txt_shop_in_charge);
        shopInchargeMobile = (TextView) findViewById(R.id.txt_mobile_no);
        submitButton = (Button) findViewById(R.id.btn_submit);
        submitButton.setOnClickListener(null);
        submitButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));
//        submitButton.setFocusable(false);
        nextButton = (Button) findViewById(R.id.btn_next);
        nextButton.setOnClickListener(this);
        cancelButton = (Button) findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(this);
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mIvBack.setOnClickListener(this);
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        spinner = (NoDefaultSpinner) findViewById(R.id.criteriaSpinner);
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case UNSCHEDULED_REPORT:
                getReportResponse(message);
                break;
            default:
                if (progressBar != null) progressBar.dismiss();
                completeObservationDialog = new CompleteObservationDialog(this, userName, "unscheduled");
                completeObservationDialog.show();
                break;
        }
    }

    private void getReportResponse(Bundle message) {
        try {
            if (progressBar != null) progressBar.dismiss();
            String response = message.getString(InspectionConstants.RESPONSE_DATA);
            if (response.contains("Unauthorized")) {
                Toast.makeText(InspectionFindingActivity.this, "Your Session is closed", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, InspectionLoginActivity.class));
                finish();
            } else {
                Log.e("CompleteFinding", response);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                InspectionReportDto inspectionReportDto = gson.fromJson(response, InspectionReportDto.class);
                Log.e("InReportIdRes", inspectionReportDto.toString());
                int statusCode = inspectionReportDto.getStatusCode();
                Log.e("statusCode", "" + statusCode);
                if (statusCode == 0) {
                    completeObservationDialog = new CompleteObservationDialog(this, userName, "unscheduled");
                    completeObservationDialog.show();
                    if (inspectionReportDto.getId() != null) {
                        FPSDBHelper.getInstance(this).updateReportTransfered(inspectionReportDto);//Scheduled Id for Scheduled List otherwise null for Unscheduled
                    }
                    LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
                    fpsInwardLinearLayout.removeAllViews();
                } else {
                    boolean available = false;
                    for (ErrorCodeDescription ecd : ErrorCodeDescription.values()) {
                        if (ecd.getErrorCode() == inspectionReportDto.getStatusCode()) {
                            Util.messageBar(this, ecd.getErrorDescription());
                            available = true;
                        }
                    }
                    if (!available) {
                        Util.messageBar(this, "Internal error");
                    }
                    completeObservationDialog = new CompleteObservationDialog(this, userName, "unscheduled");
                    completeObservationDialog.show();
                    LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
                    fpsInwardLinearLayout.removeAllViews();
                }
            }
        } catch (Exception e) {
            if (progressBar != null) progressBar.dismiss();
            Log.e("Error", e.toString(), e);
        }
    }

    private void loadInspectionCriteriaList() {
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Integer> listpos = new ArrayList<>();
        if (checksize()) {
            submitButton.setBackgroundColor(getResources().getColor(R.color.green));
            submitButton.setOnClickListener(this);
            if (Util.findingCriteriaDto.getStockInspection().size() > 0) {
                list.add("" + getResources().getString(R.string.stock));
                listpos.add(0);
            }
            if (Util.findingCriteriaDto.getCardInspection().size() > 0) {
                list.add("" + getResources().getString(R.string.card_verification));
                listpos.add(1);
            }
            if (Util.findingCriteriaDto.getWeighmentInspection().size() > 0) {
                list.add("" + getResources().getString(R.string.weighment));
                listpos.add(2);
            }
            if (Util.findingCriteriaDto.getShopInpsection().size() > 0) {
                list.add("" + getResources().getString(R.string.title_shop_open_close));
                listpos.add(3);
            }
            if (Util.findingCriteriaDto.getOtherInsection().size() > 0) {
                list.add("" + getResources().getString(R.string.others));
                listpos.add(4);
            }
            loadTableValues(list, listpos);
        } else {
            submitButton.setBackgroundColor(getResources().getColor(R.color.lightgrey));
            submitButton.setOnClickListener(null);
        }
    }

    private void getFpsStoreDetail() {
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(InspectionFindingActivity.this).getFpsUserDetails();
            fpsStoreDto = loginResponseDto.getUserDetailDto().getFpsStore();
            shop_code =fpsStoreDto.getGeneratedCode();
        } catch (Exception e) {
        }
    }

    private void loadViewData() {
        setUpInspectionPopUpPage();
        userName = SessionId.getInstance().getUserName();
//        stockInspectionDtoList = new ArrayList<>();
//        cardInspectionDtoList = new ArrayList<>();
//        weightInspectionDtoList = new ArrayList<>();
//        shopInspectionDtoList = new ArrayList<>();
//        otherInspectionDtoList = new ArrayList<>();
        mTvTitle.setText(getResources().getString(R.string.inspection_finding));
        try {
            Log.e("FairPriceshop", "fpsStoreDtoArrayList..." + fpsStoreDto.toString());
            if (fpsStoreDto.getGeneratedCode() != null) {
                shopCode.setText(" " + fpsStoreDto.getGeneratedCode());
            }
            if (fpsStoreDto.getDistrictName() != null) {
                shopLocation.setText(" " + fpsStoreDto.getVillageName());
            }
            if (fpsStoreDto.getContactPerson() != null) {
                shopIncharge.setText(" " + fpsStoreDto.getContactPerson());
            }
            if (fpsStoreDto.getPhoneNumber() != null) {
                shopInchargeMobile.setText(" " + fpsStoreDto.getPhoneNumber());
            }
        } catch (Exception e) {
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            inspectionDate.setText(" " + dateString);
        } catch (Exception e) {
        }
        ArrayList<String> criteriaNameList = new ArrayList<String>();
        findingCreteriaArray = FPSDBHelper.getInstance(this).getAllCriteria();
        criteriaNameList.clear();
        if (criteriaNameList != null) {
            for (InspectionCriteriaDto inspectionCriteriaDto : findingCreteriaArray) {
                String criteriaName = inspectionCriteriaDto.getCriteria();
                criteriaNameList.add(criteriaName);
            }
        }
//        findingCreteriaSelected = InspectionConstants.Stock_Inspection;
//        for (int i = 0; i < Util.findingCriteriaDto.size(); i++) {
//        if (Util.findingCriteriaDto.getStockInspection().size() > 0) {
//            stockInspectionDtoList.addAll(Util.findingCriteriaDto.getStockInspection());
//        } else if (Util.findingCriteriaDto.getCardInspection().size() > 0) {
//            cardInspectionDtoList.addAll(Util.findingCriteriaDto.getCardInspection());
//        } else if (Util.findingCriteriaDto.getWeighmentInspection().size() > 0) {
//            weightInspectionDtoList.addAll(Util.findingCriteriaDto.getWeighmentInspection());
//        } else if (Util.findingCriteriaDto.getShopInpsection().size() > 0) {
//            shopInspectionDtoList.addAll(Util.findingCriteriaDto.getShopInpsection());
//        } else if (Util.findingCriteriaDto.getOtherInsection().size() > 0) {
//            otherInspectionDtoList.addAll(Util.findingCriteriaDto.getOtherInsection());
//        }
//        }
        loadInspectionCriteriaList();
        loadspinnerdata();
    }

    private void loadspinnerdata() {
        final List<InspectionCriteriaDto> list = FPSDBHelper.getInstance(this).getAllCriteria();
        ArrayList<String> arraylist = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            arraylist.add(list.get(i).getCriteria());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arraylist);
        spinner.setAdapter(adapter);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                findingCreteriaSelected = list.get(position).getCriteria();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadTableValues(ArrayList<String> type, ArrayList<Integer> listpos) {
        Log.e("LoadFindingList", type.toString());
        if (type.size() > 0) {
            noReport.setVisibility(View.GONE);
        } else {
            noReport.setVisibility(View.VISIBLE);
        }
        try {
            fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            fpsInwardLinearLayout.removeAllViews();
            int sNo = 1;
            for (int position = type.size() - 1; position >= 0; position--) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, sNo, type.get(position).toString(), listpos.get(position)));
                sNo++;
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }

    private View returnView(LayoutInflater entitle, int sno, final String inspectionType, final Integer integer) {
        View convertView = entitle.inflate(R.layout.adapter_finding_inspection, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvType = (TextView) convertView.findViewById(R.id.txt_type);
        RelativeLayout rel_explore = (RelativeLayout) convertView.findViewById(R.id.dispatchDateLay);
//        ImageView explore = (ImageView) convertView.findViewById(R.id.img_explore);
        mTvSno.setText("" + sno);
        final String criteriaType = inspectionType;
        mTvType.setText(criteriaType);
        /*remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FPSDBHelper.getInstance(InspectionFindingActivity.this).removeAllDataInStockInspectionTable();
                loadInspectionCriteriaList();
            }
        });*/
        rel_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (criteriaType.equalsIgnoreCase(InspectionConstants.Stock_Inspection)) {
                Intent intent = new Intent(InspectionFindingActivity.this, InspectionViewActivity.class);
//                intent.putExtra("Criteria", criteriaType);
                intent.putExtra("Criteria_no", integer);
                startActivity(intent);
                /*} else if (criteriaType.equalsIgnoreCase("Card Verification")) {
                    Intent intent = new Intent(InspectionFindingActivity.this, CardVerificationInspectionActivity.class);
                    intent.putExtra("Criteria", criteriaType);
                    startActivity(intent);
                }*/
            }
        });
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (findingCreteriaSelected.equalsIgnoreCase(InspectionConstants.Stock_Inspection)) {
//                    FPSDBHelper.getInstance(InspectionFindingActivity.this).insertReportList(inspectionReportDto, "R");
                    Intent intent = new Intent(InspectionFindingActivity.this, StockInspectionActivity.class);
                    startActivity(intent);
                    //finish();
                } else if (findingCreteriaSelected.equalsIgnoreCase(InspectionConstants.Card_Inspection)) {
//                    FPSDBHelper.getInstance(InspectionFindingActivity.this).insertReportList(inspectionReportDto, "R");
                    Intent intent = new Intent(InspectionFindingActivity.this, CardInspectionActivity.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                    //finish();
                } else if (findingCreteriaSelected.equalsIgnoreCase(InspectionConstants.Weighment_Inspection)) {
                    Intent intent = new Intent(InspectionFindingActivity.this, WeighmentInspectionActivity.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                    //finish();
                } else if (findingCreteriaSelected.equalsIgnoreCase(InspectionConstants.shopinspection)) {
                    Intent intent = new Intent(InspectionFindingActivity.this, ShopOpenCloseActivity.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                    //finish();
                } else if (findingCreteriaSelected.equalsIgnoreCase(InspectionConstants.otherinspection)) {
                    Intent intent = new Intent(InspectionFindingActivity.this, OtherInspectionActivity.class);
                    intent.putExtra("UserName", userName);
                    startActivity(intent);
                    //finish();
                } else {
                    Toast.makeText(InspectionFindingActivity.this, R.string.select_inspection_criteria, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_submit:
                if (checksize()) {
                    inspectionOverallRemarkDialog = new InspectionOverallRemarkDialog(this);
                    inspectionOverallRemarkDialog.show();
                } else {
                    Toast.makeText(InspectionFindingActivity.this, R.string.no_report, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.imageViewBack:
                onBackPressed();
                break;
        }
    }

    private boolean checksize() {
        return (Util.findingCriteriaDto.getStockInspection().size() > 0 || Util.findingCriteriaDto.getCardInspection().size() > 0 || Util.findingCriteriaDto.getWeighmentInspection().size() > 0 || Util.findingCriteriaDto.getShopInpsection().size() > 0 || Util.findingCriteriaDto.getOtherInsection().size() > 0);
    }

    @Override
    public void onBackPressed() {
        if (checksize()) {
            unsavedReportDialog = new UnsavedReportDialog(this);
            unsavedReportDialog.show();
        } else {
            finishInspectionFinding();
        }
    }

    public void finishInspectionFinding() {
        Util.findingCriteriaDto = new FindingCriteriaDto();
        Intent intent = new Intent(InspectionFindingActivity.this, InspectionDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void submitReport(Activity context) {
        try {
            LoginResponseDto loginResponseDto = FPSDBHelper.getInstance(context).getFpsUserDetails();
            fpsStoreDto = loginResponseDto.getUserDetailDto().getFpsStore();
        } catch (Exception e) {
        }
//        ListFindingCreteria listFindingCreteria = new ListFindingCreteria();
//        listFindingCreteria.setFindingList(Util.findingCriteriaDto);
        InspectionReportDto inspectionReportDto = new InspectionReportDto();
        inspectionReportDto.setUserName(userName);
        inspectionReportDto.setFpsId(fpsStoreDto.getId());
        inspectionReportDto.setTalukId(fpsStoreDto.getTalukId());
        inspectionReportDto.setDistrictId(fpsStoreDto.getDistrictId());
        inspectionReportDto.setVillageId(fpsStoreDto.getVillageId());
        inspectionReportDto.setTypeOfInspection(false);
        inspectionReportDto.setInspectorName(userName);
        inspectionReportDto.setInspectionPlace("FPS");
        inspectionReportDto.setCode(fpsStoreDto.getGeneratedCode());
        inspectionReportDto.setInchargeName(fpsStoreDto.getContactPerson());
        inspectionReportDto.setOverAllComments(overallRemark);
        if (overallStatus == 1) {
            inspectionReportDto.setOverAllStatus(true);
        } else if (overallStatus == 2) {
            inspectionReportDto.setOverAllStatus(false);
        }
        inspectionReportDto.setFineAmount(fineAmount);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            Date authRespDate = sdf.parse(dateString);
            inspectionReportDto.setDateOfInspection(authRespDate.getTime());
        } catch (Exception e) {
        }
        inspectionReportDto.setFindingCriteriaDto(Util.findingCriteriaDto);
        inspectionReportDto.setTransactionId(Util.getInspectionReportTransactionId(this));
        getReportRequest(inspectionReportDto, context);
//        stopService(new Intent(this, OfflineCriteriaService.class));
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        if (!isMyServiceRunning(context, OfflineCriteriaService.class))
//            context.startService(new Intent(context, OfflineCriteriaService.class));
    }

    public boolean isMyServiceRunning(Activity context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void getReportRequest(InspectionReportDto inspectionReportDto, Activity context) {
        try {
            Log.e("From UI", inspectionReportDto.toString());
            FPSDBHelper.getInstance(context).insertReportList(inspectionReportDto, "N");
            Util.findingCriteriaDto = new FindingCriteriaDto();
            /*if (networkConnection.isNetworkAvailable()) {
                InspectionReportDto inspectionReportDtoDb = FPSDBHelper.getInstance(this).getLastInsertedReportClientId();
                inspectionReportDtoDb.setId(null);
                inspectionReportDtoDb.setListFindingCreteria(null);
                Log.e("ReportrequestDB", inspectionReportDtoDb.toString());
                String login = new Gson().toJson(inspectionReportDtoDb);
                StringEntity se = new StringEntity(login, HTTP.UTF_8);
                String url = "/inspection/savereport";
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                httpConnection.sendRequest(url, null, ServiceListenerType.UNSCHEDULED_REPORT, SyncHandler, RequestType.POST, se, this);
            } else {*/
//                if(!tag.equalsIgnoreCase("FPSListActivity")){
            completeObservationDialog = new CompleteObservationDialog(context, userName, "unscheduled");
            completeObservationDialog.show();
                /*}else{
                    CompleteObservationDialog completeObservationDialog = new CompleteObservationDialog(this,userName,typeOfInspection);
                    completeObservationDialog.show();
                }*/
//            }
        } catch (Exception e) {
            Log.e("Report Request", e.toString(), e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Util.findingCriteriaDto = new FindingCriteriaDto();
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
            if ((completeObservationDialog != null) && completeObservationDialog.isShowing()) {
                completeObservationDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            completeObservationDialog = null;
        }
        try {
            if ((unsavedReportDialog != null) && unsavedReportDialog.isShowing()) {
                unsavedReportDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            unsavedReportDialog = null;
        }
    }
}
