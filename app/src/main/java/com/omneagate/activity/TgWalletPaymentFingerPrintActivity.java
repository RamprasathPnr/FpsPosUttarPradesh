package com.omneagate.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantra.mfs100.DeviceInfo;
import com.mantra.mfs100.FingerData;
import com.mantra.mfs100.MFS100;
import com.mantra.mfs100.MFS100Event;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.Product;
import com.omneagate.Util.EntitlementResponse;
import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.Util.XMLUtil;
import com.omneagate.activity.dialog.SaleTransactionCompleted;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TgWalletPaymentFingerPrintActivity extends BaseActivity implements View.OnClickListener,MFS100Event {


    private TextView top_textView;
    private ImageView imageViewBack;
    private ImageView imageViewUserProfile;
    private TextView txt_ration_card;
    private LinearLayout tWalletMode;
    private LinearLayout irisLayout;
    private TextView txtUid;
    private TextView txtName;
    private TextView txtdate;
    private TextView txtAmount;
    private Button btnFingerScan;
    private Button btnFingerPrintSubmit,btnPrint,btnExit;
    private List<Product> commodityList;
    private List<Product> tempProductList;
    private double amount;
    private TextView txt_sucessStatus;

    byte[] Enroll_Template;
    ImageView imgFinger,previewImgFinger;
    byte[] isoFeatureSet;
    int timeout = 10000;
    Bitmap bitmapImg;
    DeviceInfo deviceIfo = null;
    int mfsVer = 41;
    private Timer timer;
    private LogOutTimerTask logoutTimeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg_wallet_payment_finger_print);
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(TgWalletPaymentFingerPrintActivity.this, TgSalesConfirmationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {

    }

    private void initView() {
        ((TextView) findViewById(R.id.page_header)).setText(getString(R.string.fps_id) + " : " + LoginData.getInstance().getShopNo() + " " + getResources().getString(R.string.welcome) + "  " + LoginData.getInstance().getFpsUsername());
        setPopUpPage();
        updateDateTime();
        ((TextView) findViewById(R.id.top_textView)).setText(getString(R.string.sales_top_heading)+ " > " +getString(R.string.payment_authentication));
        commodityList = EntitlementResponse.getInstance().getRcAuthResponse().getItemsAllotedList();
        tempProductList = new ArrayList<Product>();
        top_textView = (TextView) findViewById(R.id.top_textView);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        imageViewUserProfile = (ImageView) findViewById(R.id.imageViewUserProfile);
        txt_ration_card = (TextView) findViewById(R.id.aadhaar_card_number_mem);
        tWalletMode = (LinearLayout) findViewById(R.id.tWalletMode);
        txtUid = (TextView) findViewById(R.id.txtUid);
        txtName = (TextView) findViewById(R.id.txtName);
        txtdate = (TextView) findViewById(R.id.txtdate);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        btnFingerScan = (Button) findViewById(R.id.btnFingerScan);
        btnFingerPrintSubmit = (Button) findViewById(R.id.btnFingerPrintSubmit);
        txt_sucessStatus=(TextView)findViewById(R.id.txt_sucessStatus);
        btnPrint=(Button)findViewById(R.id.btnPrint);
        btnExit=(Button)findViewById(R.id.btnExit);

        irisLayout=(LinearLayout)findViewById(R.id.finger_layout);

        btnFingerScan.setOnClickListener(this);
        btnFingerPrintSubmit.setOnClickListener(this);


        imgFinger =(ImageView)findViewById(R.id.img_finger);
        previewImgFinger=(ImageView)findViewById(R.id.img_finger_preview);
        for (Product product : commodityList) {
            if ((product.getQuantityEntered() != null) && (product.getQuantityEntered() > 0.0)) {
                amount = amount + product.getAmount();
                tempProductList.add(product);
            }

        }

        DateFormat df = new SimpleDateFormat("dd-MM-yyyy' & 'HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        txtUid.setText(""+LoginData.getInstance().getUid());
        txtName.setText(""+LoginData.getInstance().getMemberName());
        txtdate.setText(""+date);
        txtAmount.setText(""+amount);
        txt_ration_card.setText(" "+LoginData.getInstance().getRationCardNo());


        if(Util.mfs100==null)
        {
            Util.mfs100 = new MFS100(this, mfsVer);
        }

        if(Util.mfs100!=null)
        {
            Util.mfs100.SetApplicationContext(this);
        }

        CommonMethod.DeleteDirectory();
        CommonMethod.CreateDirectory();

        Bitmap bitmapImg = null;
        try {
            bitmapImg = (Bitmap) getIntent().getParcelableExtra("Image");
            imgFinger.setImageBitmap(bitmapImg);
            isoFeatureSet = Enroll_Template;
            if (isoFeatureSet.length > 0) {
                findViewById(R.id.btnFingerPrintSubmit).setEnabled(true);
                findViewById(R.id.btnFingerPrintSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }
    private void scan() {
        Bitmap bitmapImg = StartSyncCapture();
        // imgFinger.setImageResource(0);
        imgFinger.setImageBitmap(null);
        imgFinger.setImageBitmap(bitmapImg);
        isoFeatureSet = Enroll_Template;
        try {
            if (isoFeatureSet.length > 0) {
                previewImgFinger.setVisibility(View.GONE);
                imgFinger.setVisibility(View.VISIBLE);
                Log.e("BenefBfdScan", "isoFeatureSet..." + Arrays.toString(isoFeatureSet));
                findViewById(R.id.btnFingerPrintSubmit).setVisibility(View.VISIBLE);
                findViewById(R.id.btnFingerPrintSubmit).setEnabled(true);
                findViewById(R.id.btnFingerPrintSubmit).setBackground(getResources().getDrawable(R.drawable.green_background));


            }else{
                imgFinger.setVisibility(View.GONE);
                previewImgFinger.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
        }
    }

    private Bitmap StartSyncCapture() {
        try {
            Thread trd = new Thread(new Runnable() {
                @Override
                public void run() {
                    FingerData fingerData = new FingerData();
                    int ret = Util.mfs100.AutoCapture(fingerData, timeout, false, true);
                    Log.e("sample app", "ret value..." + ret);
                    Log.e("sample app", "ret value..." + Util.mfs100.GetErrorMsg(ret));
                    if (ret != 0) {
                        imgFinger.setVisibility(View.GONE);
                        previewImgFinger.setVisibility(View.VISIBLE);
                        Toast.makeText(TgWalletPaymentFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                    } else {
                        Enroll_Template = new byte[fingerData.ISOTemplate().length];
                        System.arraycopy(fingerData.ISOTemplate(), 0, Enroll_Template, 0, fingerData.ISOTemplate().length);
                        bitmapImg = BitmapFactory.decodeByteArray(fingerData.FingerImage(), 0, fingerData.FingerImage().length);
                    }
                }
            });
            trd.run();
            trd.join();
        } catch (Exception ex) {
            CommonMethod.writeLog("Exception in ContinuesScan(). Message:- " + ex.getMessage());
            imgFinger.setVisibility(View.GONE);
            previewImgFinger.setVisibility(View.VISIBLE);
        } finally {
        }
        return bitmapImg;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFingerScan:
                scan();
                break;
            case R.id.btnFingerPrintSubmit:
                btnFingerScan.setVisibility(View.GONE);
                btnFingerPrintSubmit.setVisibility(View.GONE);
                irisLayout.setVisibility(View.GONE);
                btnPrint.setVisibility(View.VISIBLE);
                btnExit.setVisibility(View.VISIBLE);
                txt_sucessStatus.setVisibility(View.VISIBLE);
                SaleTransactionCompleted saleTransactionCompleted = new SaleTransactionCompleted(TgWalletPaymentFingerPrintActivity.this);
                saleTransactionCompleted.setCanceledOnTouchOutside(false);
                saleTransactionCompleted.show();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnInitScanner();

    }

    @Override
    public void OnDeviceAttached(int vid, int pid, boolean hasPermission) {
        int ret = 0;
        if (!hasPermission) {
            Toast.makeText(TgWalletPaymentFingerPrintActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vid == 1204 || vid == 11279) {
            if (pid == 34323) {
                ret = Util.mfs100.LoadFirmware();
                if (ret != 0) {
                    Toast.makeText(TgWalletPaymentFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TgWalletPaymentFingerPrintActivity.this, "Loadfirmware success", Toast.LENGTH_SHORT).show();
                }
            } else if (pid == 4101) {
                ret = Util.mfs100.Init();
                if (ret != 0) {
                    Toast.makeText(TgWalletPaymentFingerPrintActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
                    deviceIfo = Util.mfs100.GetDeviceInfo();
                }
            }
        }

    }

    @Override
    public void OnPreview(FingerData fingerData) {

    }

    @Override
    public void OnCaptureCompleted(boolean b, int i, String s, FingerData fingerData) {

    }

    @Override
    public void OnDeviceDetached() {

    }

    @Override
    public void OnHostCheckFailed(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        UnInitScanner();
    }

    public void UnInitScanner() {
        try {
            deviceIfo = null;
            if (Util.mfs100 != null) {
                Util.mfs100.Dispose();
                int ret = Util.mfs100.UnInit();
                if (ret != 0) {
//                    Toast.makeText(BenefBfdScanActivity.this, Util.mfs100.GetErrorMsg(ret), Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(BenefBfdScanActivity.this, "Uninit Success", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("UnInitScanner.EX", e.toString());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (logoutTimeTask != null) {
            logoutTimeTask.cancel();
            logoutTimeTask = null;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Timer();
        Log.i("Main", "Invoking logout timer");
        logoutTimeTask = new LogOutTimerTask();
        timer.schedule(logoutTimeTask, XMLUtil.autologOutTime); //auto logout in 5 minutes

    }
    private class LogOutTimerTask extends TimerTask {

        @Override
        public void run() {

            //redirect user to login screen
            Intent i = new Intent(TgWalletPaymentFingerPrintActivity.this, TgLoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}
