package com.omneagate.activity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.AddressForBeneficiary;
import com.omneagate.Util.CustomProgressDialog;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.SessionId;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.omneagate.activity.SplashActivity.context;

public class BillSuccessActivity extends BaseActivity implements IOCallBack {
    double totalCost = 0.0;

    MediaPlayer mediaPlayer;
    // android built in classes for bluetooth operations
    String oldCardNumber;
    String value;
    BillDto billDto;
    Button Print_Btn;

    public static ExecutorService es = Executors.newScheduledThreadPool(5);

    static Pos mPos = new Pos();
    static USBPrinting mUsb = new USBPrinting();
    public static int nPrintWidth = 384;
    public static boolean bCutter = false;
    public static boolean bDrawer = false;
    public static boolean bBeeper = true;
    public static int nPrintCount = 1;
    public static int nCompressMethod = 0;
    public static boolean bAutoPrint = false;
    public static int nPrintContent = 0;
    public static boolean bCheckReturn = false;
    String tempMsg;
    String TAG = "BillSuccessActivity";
    private AlertDialog alertDialog;
    ProgressBar printProgress;
    private static final String ACTION_USB_PERMISSION = "com.omneagate.activity.USB_PERMISSION";
    PendingIntent mPermissionIntent;
    //    Button btnCheck;
//    TextView textInfo;
    public static UsbDevice mdevice;
    UsbManager manager;
    private boolean printing = false, connecting = false;
    private static BroadcastReceiver mUsbReceiver;
    public static TaskOpen taskopen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bill_success);
        appState = (GlobalAppState) getApplication();

        try {
            tempMsg = getIntent().getStringExtra("message");
            billDto = FPSDBHelper.getInstance(this).getBillByTransactionId(tempMsg);
            Util.setTamilText((TextView) findViewById(R.id.summarySubmit), getString(R.string.sales));
            Util.setTamilText((TextView) findViewById(R.id.summaryEdit), getString(R.string.printBill));
            findViewById(R.id.printing).setVisibility(View.INVISIBLE);
            printProgress = (ProgressBar) findViewById(R.id.printerProgress);
            setUpInitialPage();
            playSound();
//        roleFeaturePrintReceipt();
            manager = (UsbManager) getSystemService(Context.USB_SERVICE);
//            check_usb_permission();
//

//        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
       /* IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        registerReceiver(mUsbReceiver, filter);*/
        } catch (Exception e) {
            BillSuccessActivity.this.finish();
        }

        /*if((tempMsg != null) && (!tempMsg.equalsIgnoreCase("null"))) {
            Util.message = tempMsg;
        }
        else {

        }*/


    }

    private boolean check_usb_permission() {
        if (mdevice == null) {
            try {
                if (es.isShutdown()||es.isTerminated()) {
                    es = Executors.newScheduledThreadPool(5);
                }
                if (taskopen != null||!taskopen.isInterrupted()) {
                    taskopen.interrupt();
                    taskopen =null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPos = new Pos();
            mUsb = new USBPrinting();

            HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            boolean is_device = false;
            while (deviceIterator.hasNext()) {
                final UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 4070) {
                    is_device = true;
                    mdevice = device;
                    if (!manager.hasPermission(device)) {
//                        set_usb_permission_receiver();
                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(BillSuccessActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        manager.requestPermission(device, mPermissionIntent);
                    } else if (!mUsb.IsOpened()) {
                        connectToprinter();
                    }
                }
            }
            if (deviceList.size() == 0)
                printing = false;
            if (!is_device) {
                connecting = false;
                Toast.makeText(BillSuccessActivity.this, R.string.connect_printer, Toast.LENGTH_SHORT).show();
            }

            return false;
        } else if (!manager.hasPermission(mdevice)) {
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(BillSuccessActivity.this, 0, new Intent(ACTION_USB_PERMISSION), 0);
            manager.requestPermission(mdevice, mPermissionIntent);
            return false;
        } else if (!mUsb.IsOpened()) {
            connectToprinter();
        }

        return true;
    }

    private void connectToprinter() {
        connecting = true;
        mPos.Set(mUsb);
        mUsb.SetCallBack(BillSuccessActivity.this);
        taskopen = new TaskOpen(mUsb, manager, mdevice, BillSuccessActivity.this);
        es.submit(taskopen);
//        taskopen.start();

    }

    private void set_usb_permission_receiver() {
        if (mUsbReceiver != null) {
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            mUsbReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("RK", "set_usb_permission_receiver");
                    mPos.Set(mUsb);
                    mUsb.SetCallBack(BillSuccessActivity.this);
                    es.submit(new TaskOpen(mUsb, manager, mdevice, BillSuccessActivity.this));
                }
            };
            registerReceiver(mUsbReceiver, filter);
        }
    }


    private void probe() {
        final UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        if (deviceList.size() > 0) {
            while (deviceIterator.hasNext()) {
                final UsbDevice device = deviceIterator.next();
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(BillSuccessActivity.this, 0, new Intent(BillSuccessActivity.this
                        .getApplicationInfo().packageName), 0);
                if (!mUsbManager.hasPermission(device)) {
                    mUsbManager.requestPermission(device, mPermissionIntent);
                    Toast.makeText(getApplicationContext(), getString(R.string.usb_printer_denied), Toast.LENGTH_LONG).show();
                } else if (device.getVendorId() == 4070) {
                    Toast.makeText(BillSuccessActivity.this, getString(R.string.usb_printer_connecting), Toast.LENGTH_SHORT).show();
                    /*progressBar = new CustomProgressDialog(this);
                    progressBar.setCanceledOnTouchOutside(false);
                    progressBar.show();*/
                    printing = true;
                    if (mUsb.IsOpened()) {
                        es.submit(new TaskPrint(mPos));
                    } else {
                        Print_Btn.setVisibility(View.INVISIBLE);
                        printProgress.setVisibility(View.VISIBLE);
                        es.submit(new TaskOpen(mUsb, mUsbManager, device, BillSuccessActivity.this));
                    }
                }
            }
        } else {
            printing = false;
        }
    }

    public class TaskOpen extends Thread {

        UsbManager usbManager = null;
        UsbDevice usbDevice = null;
        Context context = null;

        public TaskOpen(USBPrinting usbb, UsbManager usbManager, UsbDevice usbDevice, Context context) {
            this.usbManager = usbManager;
            this.usbDevice = usbDevice;
            this.context = context;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            Log.e(TAG, "interface count..." + usbDevice.getInterfaceCount());

            boolean openPrinter = mUsb.Open(usbManager, usbDevice, context);
            Log.e(TAG, "openPrinter..." + openPrinter);
        }


    }

    public void OnOpen() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                btnDisconnect.setEnabled(true);
//                btnPrint.setEnabled(true);
//                progressBar.dismiss();
                Toast.makeText(BillSuccessActivity.this, getString(R.string.usb_printer_connected), Toast.LENGTH_SHORT).show();
                printing = true;
                connecting = false;
            }
        });
    }

    public void OnOpenFailed() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
//                btnDisconnect.setEnabled(false);
//                btnPrint.setEnabled(false); Print_Btn.setVisibility(View.VISIBLE);
                connecting = false;
                try {
                    Toast.makeText(BillSuccessActivity.this, getString(R.string.usb_printer_failed), Toast.LENGTH_SHORT).show();
                    printProgress.setVisibility(View.GONE);
                    Print_Btn.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static int dwWriteIndex = 1;

    public class TaskPrint implements Runnable {
        Pos pos = null;

        public TaskPrint(Pos pos) {
            this.pos = pos;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            final boolean bPrintResult = PrintTicket(nPrintWidth, bCutter, bDrawer, bBeeper, nPrintCount, nPrintContent, nCompressMethod, bCheckReturn);
            final boolean bIsOpened = pos.GetIO().IsOpened();
            BillSuccessActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(BillSuccessActivity.this.getApplicationContext(), bPrintResult ? getResources().getString(R.string.print_success) : getResources().getString(R.string.print_failed), Toast.LENGTH_SHORT).show();
                    BillSuccessActivity.this.Print_Btn.setEnabled(bIsOpened);
                    printing = false;
                }
            });


        }

        public boolean PrintTicket(int nPrintWidth, boolean bCutter, boolean bDrawer, boolean bBeeper, int nCount, int nPrintContent, int nCompressMethod, boolean bCheckReturn) {
            Log.e(TAG, "nPrintWidth..." + nPrintWidth);
            Log.e(TAG, "bCutter..." + bCutter);
            Log.e(TAG, "bDrawer..." + bDrawer);
            Log.e(TAG, "bBeeper..." + bBeeper);
            Log.e(TAG, "nCount..." + nCount);
            Log.e(TAG, "nPrintContent..." + nPrintContent);
            Log.e(TAG, "nCompressMethod..." + nCompressMethod);
            Log.e(TAG, "bCheckReturn..." + bCheckReturn);


            boolean bPrintResult = false;
            byte[] status = new byte[1];
            if (!bCheckReturn || (bCheckReturn && pos.POS_QueryStatus(status, 3000, 2))) {
//                Bitmap bm1 = BillSuccessActivity.this.getTestImage1(nPrintWidth, nPrintWidth);
//                Bitmap bm2 = BillSuccessActivity.this.getTestImage2(nPrintWidth, nPrintWidth);
//                Bitmap bmBlackWhite = getImageFromAssetsFile("blackwhite.png");
//                Bitmap bmIu = getImageFromAssetsFile("iu.jpeg");
//                Bitmap bmYellowmen = getImageFromAssetsFile("yellowmen.png");
                for (int i = 0; i < nCount; ++i) {
                    if (!pos.GetIO().IsOpened())
                        break;

//                    if(nPrintContent >= 1)
//                    {
//                        pos.POS_FeedLine();
//                        pos.POS_S_Align(1);

//                        String content = BluetoothPrintData();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                                String content = hindiPrintData();
                                bitmapPrint(gethtmlcontent(content, 22), pos);
                            } else {
                                String content = englishPrintData();
                                bitmapPrint(gethtmlcontent(content, 22), pos);
                            }

                        }
                    });


//                        pos.POS_S_TextOut(hindiContent, 0, 0, 0, 0, 0);

//                        pos.POS_S_TextOut("REC" + String.format("%03d", i) + "\r\nCaysn Printer\r\n测试页\r\n\r\n", 0, 1, 1, 0, 0x100);
//                        pos.POS_S_TextOut("扫二维码下载苹果APP\r\n", 0, 0, 0, 0, 0x100);
                        /*pos.POS_S_SetQRcode("https://appsto.re/cn/2KF_bb.i", 8, 0, 3);
                        pos.POS_FeedLine();
                        pos.POS_S_SetBarcode("20160618", 0, 72, 3, 60, 0, 2);*/
//                        pos.POS_FeedLine();
//                    }

//                    if(nPrintContent >= 2)
//                    {
//                        if(bm1 != null)
//                        {
//                            pos.POS_PrintPicture(bm1, nPrintWidth, 1, nCompressMethod);
//                        }
//                        if(bm2 != null)
//                        {
//                            pos.POS_PrintPicture(bm2, nPrintWidth, 1, nCompressMethod);
//                        }
//                    }

                    /*if(nPrintContent >= 3)
                    {
                        if(bmBlackWhite != null)
                        {
                            pos.POS_PrintPicture(bmBlackWhite, nPrintWidth, 1, nCompressMethod);
                        }
                        if(bmIu != null)
                        {
                            pos.POS_PrintPicture(bmIu, nPrintWidth, 0, nCompressMethod);
                        }
                        if(bmYellowmen != null)
                        {
                            pos.POS_PrintPicture(bmYellowmen, nPrintWidth, 0, nCompressMethod);
                        }
                    }*/
                }

                if (bBeeper)
                    pos.POS_Beep(1, 5);
                if (bCutter)
                    pos.POS_CutPaper();
                if (bDrawer)
                    pos.POS_KickDrawer(0, 100);

                if (bCheckReturn) {
                    int dwTicketIndex = dwWriteIndex++;
                    bPrintResult = pos.POS_TicketSucceed(dwTicketIndex, 30000);
                } else {
                    bPrintResult = pos.GetIO().IsOpened();
                }
            }

            return bPrintResult;
        }
    }

    public class TaskClose implements Runnable {


        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mUsb.IsOpened())
                mUsb.Close();
        }
    }

    public void OnClose() {
        // TODO Auto-generated method stub
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.e("musb closed", "musb closed");
//                btnDisconnect.setEnabled(false);
//                btnPrint.setEnabled(false);
//                probe(); // 如果因为打印机关机导致Close。那么这里需要重新枚举一下。
            }
        });
    }

    public Bitmap getTestImage1(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(Color.BLACK);
        for (int i = 0; i < 8; ++i) {
            for (int x = i; x < width; x += 8) {
                for (int y = i; y < height; y += 8) {
                    canvas.drawPoint(x, y, paint);
                }
            }
        }
        return bitmap;
    }

    public Bitmap getTestImage2(int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, paint);
        paint.setColor(Color.BLACK);
        for (int y = 0; y < height; y += 4) {
            for (int x = y % 32; x < width; x += 32) {
                canvas.drawRect(x, y, x + 4, y + 4, paint);
            }
        }
        return bitmap;
    }


    private String gethtmlcontent(String content, int font) {
        Log.e(TAG, "content in getHtml..." + content);
        Log.e(TAG, "font in getHtml..." + font);

        String fontsize = "18px";
        if (font != 0) {
            fontsize = Integer.toString(font) + "px";
        }
        String htmlContent = "";
        htmlContent = "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">";
        htmlContent = new StringBuilder(String.valueOf(htmlContent)).append("<tr><td style=\"font-size:" + fontsize + ";\">").append(content.replaceAll("\n", "<br />")).append("</td></tr>").toString();

        htmlContent = new StringBuilder(String.valueOf(htmlContent)).append("</table>").toString();
        return htmlContent;
    }

    private void bitmapPrint(final String content, final Pos pos) {
        Log.e(TAG, "content in bitmapPrint..." + content);
        Log.e(TAG, "pos in bitmapPrint..." + pos);
        View promptsView = LayoutInflater.from(BillSuccessActivity.this).inflate(R.layout.sending_print, null);
        final WebView w = (WebView) promptsView.findViewById(R.id.mywebView);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BillSuccessActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        w.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                if (w.getWidth() <= 0 || w.getHeight() <= 0) {
                    Handler handler = new Handler();
                    final WebView webView = w;
                    final WebView webView2 = view;
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (webView.getWidth() > 0 && webView.getHeight() > 0) {
                                final WebView webView1 = webView;
                                final WebView webView22 = webView2;
                                BillSuccessActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        makeBitmap(webView1, webView22, pos, content);
                                    }
                                });
                            }
                        }
                    }, (long) 500);
                }
            }
        });
        w.setLayoutParams(new RelativeLayout.LayoutParams(Math.round((float) (48 * 8)), -2));
        w.getSettings().setAllowFileAccess(true);
        w.getSettings().setBuiltInZoomControls(true);
        w.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    public void makeBitmap(WebView w, WebView view, Pos pos, String content) {
        Log.e(TAG, "inside makeBitmap..." + content);
        float scale = BillSuccessActivity.this.getResources().getDisplayMetrics().density;
        int imgWidth = w.getWidth();
        int imgHeight = (int) (((float) view.getContentHeight()) * scale);
        Bitmap bitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawColor(-1);
        w.draw(c);
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap = PrintImage(bitmap);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap btMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (btMap != null) {
                pos.POS_PrintPicture(btMap, nPrintWidth, 1, nCompressMethod);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog.dismiss();
    }

    public Bitmap PrintImage(Bitmap bm_source) {
        int width = bm_source.getWidth();
        int height = bm_source.getHeight();
        if (width > 576) {
            bm_source = getResizedBitmap(bm_source, 576, (int) Math.floor((double) (((float) height) * (((float) 576) / ((float) width)))));
        } else if (Math.floor((double) (width / 8)) != ((double) width) / 8.0d) {
            int newWidth = (int) Math.floor((double) (width / 8));
            bm_source = getResizedBitmap(bm_source, newWidth, (int) Math.floor((double) (((float) height) * (((float) newWidth) / ((float) width)))));
        }
        return bm_source;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / ((float) width);
        float scaleHeight = ((float) newHeight) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    private void playSound() {
        try {
            Util.LoggingQueue(this, "BillSuccessActivity", "Media player running");
            mediaPlayer = MediaPlayer.create(this, R.raw.beep);
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("BillSuccessActivity", e.toString(), e);
        }
    }

    /*
*
* Initial Setup
*
* */
    private void setUpInitialPage() {
        appState = (GlobalAppState) getApplication();
        Util.LoggingQueue(this, "BillSuccessActivity", "Setting up bill summary");
        findViewById(R.id.imageViewBack).setVisibility(View.INVISIBLE);
        Util.setTamilText((TextView) findViewById(R.id.top_textView), R.string.bill_summary);
        Util.setTamilText((TextView) findViewById(R.id.commoditys), R.string.commodity);
        Util.setTamilText((TextView) findViewById(R.id.unit), R.string.unit);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailUfcLabel), R.string.billDetailTxnBill);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailAmountLabel), R.string.billDetailAmountLabel);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDateLabel), R.string.ration_card_number1);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailHeadOfTheFamilyLabel), R.string.billDetailFamilyHeadOfTheFamilyLabel);
        Util.setTamilText((TextView) findViewById(R.id.tvBillDetailAddressLabel), R.string.billDetailAddressLabel);
        Util.setTamilText((TextView) findViewById(R.id.billDetailQuantity), R.string.billDetailQuantity);
        Util.setTamilText((TextView) findViewById(R.id.billDetailProductPrice), R.string.billDetailProductPrice);
        findViewById(R.id.summarySubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new TaskClose().run();
//                if (taskopen != null)
//                    taskopen.interrupt();
//                if (!printing) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                Util.LoggingQueue(com.omneagate.activity.BillSuccessActivity.this, "BillSuccessActivity", "Continue button called");
                startActivity(new Intent(com.omneagate.activity.BillSuccessActivity.this, SaleOrderActivity.class));
                finish();
//                } else {
//                    Toast.makeText(BillSuccessActivity.this, R.string.print_wait, Toast.LENGTH_SHORT).show();
//                }
            }
        });
        Print_Btn = (Button) findViewById(R.id.summaryEdit);
        Print_Btn.setVisibility(View.VISIBLE);
     /**********************************************************************************/

        findViewById(R.id.summaryEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Print_Btn.setEnabled(false);
//                checkInfo();
//                es.submit(new TaskPrint(mPos));

//                connectPrinter();
                if (!connecting) {
                    connectPrinter_new();
                } else {
                    Toast.makeText(BillSuccessActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
                }
//                es.submit(new TaskPrint(mPos));
            }
        });
        submitBills();
        setUpPopUpPage();
    }

    private void connectPrinter_new() {
        printing = true;
        boolean permission = check_usb_permission();
        if (permission) {
            if (mUsb.IsOpened()) {
                es.submit(new TaskPrint(mPos));
            } else {
//                Print_Btn.setVisibility(View.INVISIBLE);
//                printProgress.setVisibility(View.VISIBLE);
//                es.submit(new TaskOpen(mUsb, manager, mdevice, BillSuccessActivity.this));
                Toast.makeText(BillSuccessActivity.this, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
            }
        } /*else {
            Toast.makeText(BillSuccessActivity.this, R.string.no_premission, Toast.LENGTH_SHORT).show();
        }*/
        printing = false;
    }

    private void connectPrinter() {
        mPos.Set(mUsb);
        mUsb.SetCallBack(BillSuccessActivity.this);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            probe();
        } else {
            finish();
        }
    }

    private void submitBills() {
        Util.LoggingQueue(this, "BillSuccessActivity", " bill summary:" + billDto.toString());
        try {
            TextView tvUfc = (TextView) findViewById(R.id.tvBillDetailUfc);
            TextView tvTotalAmount = (TextView) findViewById(R.id.tvBillDetailAmount);
            tvUfc.setText(billDto.getTransactionId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date convertedDate = dateFormat.parse(billDto.getBillDate());
            dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            tvTotalAmount.setText(dateFormat.format(convertedDate));
            new SearchBillTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, billDto.getBeneficiaryId());
        } catch (Exception e) {
            Log.e("BillSuccessActivity", "Date Parse Error", e);
            Util.LoggingQueue(this, "BillSuccessActivity", "Sales Error :" + e.toString());
        } finally {
            configureData(billDto);
        }
    }

    private String headOfFamily(List<BeneficiaryMemberDto> beneficiaryMember) {
        String head = "";
        for (BeneficiaryMemberDto benef : beneficiaryMember) {
            if (benef.getRelName().equalsIgnoreCase("Family Head")) {
                if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(benef.getLocalName())) {
                    return benef.getLocalName();
                } else if (GlobalAppState.language.equalsIgnoreCase("en") && StringUtils.isNotEmpty(benef.getName())) {
                    return benef.getName();
                } else {
                    return head;
                }
            }
        }
        return head;
    }

    /*Data from server has been set inside this function*/
    private void configureData(BillDto bills) {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_bill_detail);
            fpsInwardLinearLayout.removeAllViews();
            List<BillItemProductDto> billItems = new ArrayList<>(bills.getBillItemDto());
            for (BillItemProductDto items : billItems) {
                LayoutInflater lin = LayoutInflater.from(this);
                fpsInwardLinearLayout.addView(returnView(lin, items));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("BillSuccess", e.toString(), e);
        } finally {
            TextView tvBillDetailTotal = (TextView) findViewById(R.id.tvBillDetailTotal);
            /*NumberFormat formatter = new DecimalFormat("#0.00");
            formatter.setRoundingMode(RoundingMode.CEILING);*/
            String amtStr = priceRoundOffFormat(totalCost);
            totalCost = Double.parseDouble(amtStr);
            tvBillDetailTotal.setText(" ₹ " + amtStr);
            Util.setTamilText(tvBillDetailTotal, getString(R.string.total) + ":\u20B9 " + amtStr);
        }
    }

    /*User Bill Detail view*/
    private View returnView(LayoutInflater entitle, BillItemProductDto data) {
        View convertView = entitle.inflate(R.layout.adapter_bill_detail_activity, null);
        TextView entitlementName = (TextView) convertView.findViewById(R.id.entitlementName);
        TextView entitlementPurchased = (TextView) convertView.findViewById(R.id.entitlementPurchased);
        TextView entitlementUnit = (TextView) convertView.findViewById(R.id.entitlementUnit);
        TextView amountOfSelection = (TextView) convertView.findViewById(R.id.amountOfSelection);
        NumberFormat formatter = new DecimalFormat("#0.000");
        String unit = data.getProductUnit();
        entitlementUnit.setText(unit);
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductUnit())) {
            unit = data.getLocalProductUnit();
            Util.setTamilText(entitlementUnit, unit);
        }
        if (GlobalAppState.language.equalsIgnoreCase("hi") && StringUtils.isNotEmpty(data.getLocalProductName()))
//            Util.setTamilText(entitlementName, data.getLocalProductName());
            entitlementName.setText(data.getLocalProductName());
        else
            entitlementName.setText(data.getProductName());
        if (data.getQuantity() != null)
            entitlementPurchased.setText(formatter.format(data.getQuantity()));
        else
            entitlementPurchased.setText("0.000");

        /*formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
//        Math.round()
        double amountPerItem = data.getCost() * data.getQuantity();
        String amtStr = priceRoundOffFormat(amountPerItem);
        ;
//        amountPerItem = Util.priceRoundOffFormat(amountPerItem);
        totalCost = totalCost + Double.parseDouble(amtStr);
        amountOfSelection.setText(amtStr);
        return convertView;
    }

    private ProductDto getProduct(long productId) {
        List<ProductDto> products = FPSDBHelper.getInstance(this).getAllProductDetails();
        for (ProductDto productDto : products) {
            if (productDto.getId() == productId) {
                return productDto;
            }
        }
        return new ProductDto();
    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        Util.LoggingQueue(this, "BillSuccessActivity", "Back pressed");
        startActivity(new Intent(this, SaleOrderActivity.class));
        finish();
    }

    private class SearchBillTask extends AsyncTask<Long, Void, BeneficiaryDto> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar = new CustomProgressDialog(com.omneagate.activity.BillSuccessActivity.this);
                progressBar.show();
            } catch (Exception e) {
                Log.e("Error in Progress", e.toString(), e);
            }
        }

        // automatically done on worker thread (separate from UI thread)
        protected BeneficiaryDto doInBackground(final Long... args) {
            return FPSDBHelper.getInstance(com.omneagate.activity.BillSuccessActivity.this).retrieveBeneficiary(args[0]);
        }

        // can use UI thread here
        protected void onPostExecute(final BeneficiaryDto beneficiaryDto) {
            try {
                if (progressBar != null) {
                    progressBar.dismiss();
                }
            } catch (Exception e) {
            }
            Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask beneficiaryDto getEncryptedUfc = " + beneficiaryDto.getEncryptedUfc());
            TextView tvBillDate = (TextView) findViewById(R.id.tvBillDetailDate);
            TextView tvAddress = (TextView) findViewById(R.id.tvBillDetailAddress);
            TextView tvHeadOfTheFamily = (TextView) findViewById(R.id.tvBillDetailHeadOfTheFamily);
            String aReg = "";
            /*if (StringUtils.isNotEmpty(beneficiaryDto.getAregisterNum())) {
                aReg = " / " + beneficiaryDto.getAregisterNum();
            }*/
            tvBillDate.setText(beneficiaryDto.getOldRationNumber() + aReg);
            oldCardNumber = beneficiaryDto.getOldRationNumber();
            Set<BeneficiaryMemberDto> beneficiaryMembers = beneficiaryDto.getBenefMembersDto();
            List<BeneficiaryMemberDto> beneficiaryMember = new ArrayList<>(beneficiaryMembers);
            Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask beneficiaryMember size = " + beneficiaryMember.size());
            if (beneficiaryMember.size() > 0) {
                Util.setTamilText(tvAddress, AddressForBeneficiary.addressForBeneficiary(beneficiaryMember.get(0)));
                Util.setTamilText(tvHeadOfTheFamily, headOfFamily(beneficiaryMember));
            }
            BeneficiaryDto beneficiary = FPSDBHelper.getInstance(BillSuccessActivity.this).getBeneficiaryDtoandFamilyHeadAadharDetails(beneficiaryDto.getEncryptedUfc());
            BeneficiaryMemberDto beneficiaryMemberDto;
            try {
                if (beneficiary.getBenefMembersDto() != null && beneficiary.getBenefMembersDto().size() > 0) {
                    List<BeneficiaryMemberDto> bene = new ArrayList<>(beneficiary.getBenefMembersDto());
                    beneficiaryMemberDto = bene.get(0);
                    Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask() called beneficiaryMemberDto = " + beneficiaryMemberDto.toString());
                    if (GlobalAppState.language.equalsIgnoreCase("hi") && beneficiaryMemberDto.getLocalName() != null && !beneficiaryMemberDto.getLocalName().equalsIgnoreCase("null")) {
                        //Util.setTamilText(cardHolder, beneficiaryMemberDto.getLocalName());
                        Util.setTamilText(tvHeadOfTheFamily, beneficiaryMemberDto.getLocalName());
                    } else {
                        //cardHolder.setText(beneficiaryMemberDto.getName());
                        tvHeadOfTheFamily.setText(beneficiaryMemberDto.getName());
                    }
                    if (GlobalAppState.language.equalsIgnoreCase("hi")) {
                        Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask() called language = ta " + beneficiaryMemberDto.getLocalAddressLine1());
                        String addressInTamil = "";
                        if (beneficiaryMemberDto.getLocalAddressLine1() != null && !beneficiaryMemberDto.getLocalAddressLine1().equalsIgnoreCase("null"))
                            addressInTamil = addressInTamil + " " + beneficiaryMemberDto.getLocalAddressLine1();
                        if (beneficiaryMemberDto.getLocalAddressLine2() != null && !beneficiaryMemberDto.getLocalAddressLine2().equalsIgnoreCase("null"))
                            addressInTamil = addressInTamil + " " + beneficiaryMemberDto.getLocalAddressLine2();
                        if (beneficiaryMemberDto.getLocalAddressLine3() != null && !beneficiaryMemberDto.getLocalAddressLine3().equalsIgnoreCase("null"))
                            addressInTamil = addressInTamil + " " + beneficiaryMemberDto.getLocalAddressLine3();
                        if (beneficiaryMemberDto.getLocalAddressLine4() != null && !beneficiaryMemberDto.getLocalAddressLine4().equalsIgnoreCase("null"))
                            addressInTamil = addressInTamil + " " + beneficiaryMemberDto.getLocalAddressLine4();
                        if (beneficiaryMemberDto.getLocalAddressLine5() != null && !beneficiaryMemberDto.getLocalAddressLine5().equalsIgnoreCase("null"))
                            addressInTamil = addressInTamil + " " + beneficiaryMemberDto.getLocalAddressLine5();
                        Util.setTamilText(tvAddress, addressInTamil);
                    } else {
                        Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask() called language = English " + beneficiaryMemberDto.getAddressLine1());
                        String addressInEnglish = "";
                        if (beneficiaryMemberDto.getAddressLine1() != null && !beneficiaryMemberDto.getAddressLine1().equalsIgnoreCase("null"))
                            addressInEnglish = addressInEnglish + " " + beneficiaryMemberDto.getAddressLine1();
                        if (beneficiaryMemberDto.getAddressLine2() != null && !beneficiaryMemberDto.getAddressLine2().equalsIgnoreCase("null"))
                            addressInEnglish = addressInEnglish + " " + beneficiaryMemberDto.getAddressLine2();
                        if (beneficiaryMemberDto.getAddressLine3() != null && !beneficiaryMemberDto.getAddressLine3().equalsIgnoreCase("null"))
                            addressInEnglish = addressInEnglish + " " + beneficiaryMemberDto.getAddressLine3();
                        if (beneficiaryMemberDto.getAddressLine4() != null && !beneficiaryMemberDto.getAddressLine4().equalsIgnoreCase("null"))
                            addressInEnglish = addressInEnglish + " " + beneficiaryMemberDto.getAddressLine4();
                        if (beneficiaryMemberDto.getAddressLine5() != null && !beneficiaryMemberDto.getAddressLine5().equalsIgnoreCase("null"))
                            addressInEnglish = addressInEnglish + " " + beneficiaryMemberDto.getAddressLine5();
                        tvAddress.setText(addressInEnglish);
                    }
                }
            } catch (Exception e) {
                Util.LoggingQueue(BillSuccessActivity.this, "BillSuccessActivity", "SearchBillTask() called Exception =  " + e);
            }
        }

    }

    public void enableButton2() {
//        Print_Btn = (Button) findViewById(R.id.summaryEdit);
        final TextView printingTxt = (TextView) findViewById(R.id.printing);
        printingTxt.setVisibility(View.INVISIBLE);
        Print_Btn.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.summarySubmit)).setVisibility(View.VISIBLE);
    }

    public void enableButton() {
        /*findViewById(R.id.summaryEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Print_Btn = (Button) findViewById(R.id.summaryEdit);
                final TextView printingTxt = (TextView) findViewById(R.id.printing);
                printingTxt.setText(getString(R.string.printing_txt));
                Long timerWaitTime = 20 * 1000l;
                printingTxt.setVisibility(View.VISIBLE);
                Print_Btn.setVisibility(View.INVISIBLE);
                ((TextView) findViewById(R.id.summarySubmit)).setVisibility(View.INVISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Print_Btn.setVisibility(View.VISIBLE);
                        printingTxt.setVisibility(View.INVISIBLE);
                        ((TextView) findViewById(R.id.summarySubmit)).setVisibility(View.VISIBLE);
                    }
                }, timerWaitTime);
//                new PrintOperation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                if (GlobalAppState.language.equalsIgnoreCase("hi"))
                    BlueToothPrint_new.getinstance(BillSuccessActivity.this).opendialog(TamilPrintData());
                else
                    BlueToothPrint_new.getinstance(BillSuccessActivity.this).opendialog(BluetoothPrintData());


            }
        });*/
        findViewById(R.id.summarySubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                Util.LoggingQueue(com.omneagate.activity.BillSuccessActivity.this, "Sales Summary", "Continue button called");
                startActivity(new Intent(com.omneagate.activity.BillSuccessActivity.this, SaleOrderActivity.class));
                finish();
            }
        });
    }

    public void enableButtonAfterPrint() {
        findViewById(R.id.summaryEdit).setVisibility(View.VISIBLE);
        findViewById(R.id.summarySubmit).setVisibility(View.VISIBLE);
        findViewById(R.id.printing).setVisibility(View.INVISIBLE);
    }

    private String BluetoothPrintData() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(billDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String refNo = billDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(billDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
//        formatter.setRoundingMode(RoundingMode.CEILING);
//        NumberFormat formatSignle = new DecimalFormat("#0.00");
//        formatSignle.setRoundingMode(RoundingMode.CEILING);
        /*textData.append("          " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("             " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "   : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "           : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "      : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "      : " + rationCardNumber + "\n");
        textData.append("----------------------------------------------\n");
        textData.append("#    " + context.getString(R.string.print_commodity) + "\n");
        textData.append("   " + context.getString(R.string.print_unit_rate) + "  " + context.getString(R.string.print_qty) + "    " + context.getString(R.string.print_price) + "\n");
        textData.append("----------------------------------------------\n");*/
        textData.append("            " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("           " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "  : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "    : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "       : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "    : " + oldCardNumber + "\n");
        textData.append("--------------------------------\n");
        textData.append("# " + context.getString(R.string.print_commodity) + " \n" + context.getString(R.string.print_unit_rate) + " |    " + context.getString(R.string.print_qty) + "    | " + context.getString(R.string.print_price) + "\n");
//        textData.append("                    PRICE \n");
        textData.append("--------------------------------\n");
        Log.e("Print2", textData.toString());
        int i = 1;
        for (BillItemProductDto bItems : billItems) {
            String productName = "";
            String unit = "";
            /*if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                productName = bItems.getLocalProductName() + "                                 ";
                unit = bItems.getLocalProductUnit();
                if (bItems.getProductUnit().equals("LTR")) {
                    if (bItems.getLocalProductUnit() != null) unit = bItems.getLocalProductUnit();
                    else unit = "LT";
                }
            } else {*/
            productName = bItems.getProductName() + "                                 ";
            unit = bItems.getProductUnit();
            if (unit.equals("LTR")) {
                unit = "LT";
            }
//            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
//            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
            String serialNo = i + "";
            /*textData.append("" + StringUtils.substring(serialNo, 0, 2) + ")" + fixedLengthString(productName, 18) + "");*/
            textData.append("" + serialNo + ") " + fixedLengthString(productName, 23) + "\n");
            /*textData.append("" + unitPriceRoundOff + " | " + quantity + " | " + pad(price, 7, " ") + "\n");*/
            textData.append("   " + unitPriceRoundOff + "   | " + quantity + " | " + price + "\n\n");
            i++;
        }
        textData.append("--------------------------------\n");
        String amt2 = Util.priceRoundOffFormat(billDto.getAmount());
        String ledgerAmount = " " + pad(amt2, 7, " ");
//        textData.append(" " + context.getString(R.string.print_total) + "        " + ledgerAmount + "\n");
        textData.append(" " + context.getString(R.string.print_total) + "        " + amt2 + "\n");
        textData.append("--------------------------------\n");
        textData.append("\n");
        textData.append("       " + context.getString(R.string.print_wishes) + " \n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        Log.e("Print", textData.toString());
//        mPrinter.printUnicodeText(textData.toString(), Layout.Alignment.ALIGN_NORMAL, mDefaultTextPaint);
//        IntentPrint(textData.toString());
        return textData.toString();
        // Start of Printing content text size testing //
      /*  StringBuilder textData2 = new StringBuilder();
        textData2.append("                " + context.getString(R.string.print_title) + "    ");
        textData2.append("\n");
        textData2.append("" + context.getString(R.string.print_billid) + "     : " + refNo + "\n");
        textData2.append("" + context.getString(R.string.print_date) + "        : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData2.append("-----------------------------------------\n");
        textData2.append("#    " + context.getString(R.string.print_commodity) + "        " + context.getString(R.string.print_qty) + "      " + context.getString(R.string.print_price) + "\n");
        textData2.append("-----------------------------------------\n");
        IntentPrint(textData2.toString());*/
        // End of Printing content text size testing //
    }

    private String englishPrintData() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(billDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String refNo = billDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(billDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
        textData.append("               " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("                     " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "    : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "             : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "                : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "         : " + oldCardNumber + "\n");
        textData.append("----------------------------------------------------------\n");
        textData.append("# " + context.getString(R.string.print_commodity) + " \n" + context.getString(R.string.print_unit_rate) + "  |  " + context.getString(R.string.print_qty) + "  |  " + context.getString(R.string.print_price) + "\n");
        textData.append("----------------------------------------------------------\n");
        Log.e("Print2", textData.toString());
        int i = 1;
        String text = "";
        for (BillItemProductDto bItems : billItems) {
            String productName = "";
            String unit = "";
            productName = bItems.getProductName() + "                                 ";
            unit = bItems.getProductUnit();
            if (unit.equals("LTR")) {
                unit = "LT";
            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
            String serialNo = i + "";
            textData.append("" + serialNo + ") " + fixedLengthString(productName, 23) + "\n");
            text = "" + unitPriceRoundOff + "  |  " + quantity + "  |  " + price + "\n\n";
//            textData.append(text);
            textData.append(text.replaceAll("-", "&nbsp;"));
            i++;
        }
        textData.append("------------------------------------------------------------\n");
        String amt2 = Util.priceRoundOffFormat(billDto.getAmount());
//        String ledgerAmount = " " + pad(amt2, 7, " ");
//        textData.append(" " + context.getString(R.string.print_total) + "              " + ledgerAmount + "\n");
        textData.append(" " + context.getString(R.string.print_total) + "        " + amt2 + "\n");
        textData.append("------------------------------------------------------------\n");
        textData.append("\n");
        text = "------------" + context.getString(R.string.print_wishes) + " \n";
        textData.append(text.replaceAll("-", "&nbsp;"));

        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        StringBuilder temptextData = new StringBuilder();
        temptextData.append(textData.toString().replaceAll(" ", "&nbsp;"));
        Log.e("Print", temptextData.toString());
        return temptextData.toString();
    }

    private String hindiPrintData() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(billDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String refNo = billDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(billDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
        textData.append("               " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("                     " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "       : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "  : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "              : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "             : " + oldCardNumber + "\n");
        textData.append("----------------------------------------------------------\n");
        textData.append("# " + context.getString(R.string.print_commodity) + " \n" + context.getString(R.string.print_unit_rate) + "  |  " + context.getString(R.string.print_qty) + "  |  " + context.getString(R.string.print_price) + "\n");
        textData.append("----------------------------------------------------------\n");
        Log.e("Print2", textData.toString());
        int i = 1;
        String text = "";
        for (BillItemProductDto bItems : billItems) {
            String productName = "";
            String unit = "";
            productName = bItems.getLocalProductName() + "                                 ";
            unit = bItems.getProductUnit();
            if (unit.equals("LTR")) {
                unit = "LT";
            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
            String serialNo = i + "";
            textData.append("" + serialNo + ") " + fixedLengthString(productName, 23) + "\n");
            text = "" + unitPriceRoundOff + "  |  " + quantity + "  |  " + price + "\n\n";
//            textData.append(text);
            textData.append(text.replaceAll("-", "&nbsp;"));
            i++;
        }
        textData.append("------------------------------------------------------------\n");
        String amt2 = Util.priceRoundOffFormat(billDto.getAmount());
//        String ledgerAmount = " " + pad(amt2, 7, " ");
//        Log.e(TAG, "ledgerAmount..." + ledgerAmount);
//        textData.append(" " + context.getString(R.string.print_total) + "                         " + ledgerAmount + "\n");
        textData.append(" " + context.getString(R.string.print_total) + "        " + amt2 + "\n");
        textData.append("------------------------------------------------------------\n");
        textData.append("\n");
        text = "------------------------" + context.getString(R.string.print_wishes) + " \n";
        textData.append(text.replaceAll("-", "&nbsp;"));

        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        StringBuilder temptextData = new StringBuilder();
        temptextData.append(textData.toString().replaceAll(" ", "&nbsp;"));
        Log.e("Print", temptextData.toString());
        return temptextData.toString();
    }

//    private void new_hindiPrintData() {
//        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
//        StringBuilder textData = new StringBuilder();
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
//        Date convertedDate = new Date();
//        Calendar myCalendar = Calendar.getInstance();
//        int am_pm = myCalendar.get(Calendar.AM_PM);
//        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
//        try {
//            convertedDate = dateFormat.parse(billDto.getBillDate());
//        } catch (ParseException e) {
//            Log.e("Error", "Date Parse Error");
//        }
//        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
//        String refNo = billDto.getTransactionId();
//        String billDate = dateFormat.format(convertedDate);
//        BeneficiaryMemberDto dto = FPSDBHelper.getInstance(this).get_beneficiary_name(billDto.getBeneficiaryId());
//
//        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(billDto.getBillItemDto());
//        NumberFormat formatter = new DecimalFormat("00.00");
//        textData.append("दुकान सं.        "+SessionId.getInstance().getFpsCode() + "\n");
//        textData.append("दुकानदार नाम    ."+fpsStore.getAgencyName());
//        textData.append("जनपद          .."+fpsStore.getDistrictName());
//        textData.append("------------------------------------");
//        textData.append("रसीद सं.        .."+refNo + "\n");
//        textData.append("तारीख / समय   "+billDate + " " + amOrpm.toUpperCase() + "\n");
//        textData.append("धारक नाम      ."+dto.getLocalName()+ "\n");
//        textData.append("यूनिट          "+billItems.getProductUnit() + "\n");
//        textData.append("कार्ड सं.         "+oldCardNumber + "\n");
//        textData.append("कार्ड प्रकार ( अंत्योदय / पात्र गृहस्थी ) .."+);
//        textData.append("प्रमाण का प्रकार  .."+);
//        textData.append("(आधार / ओ.टी.पी / अन्य ) "+);
//        textData.append("------------------------------------");
//        int i = 1;
//        String text = "";
//        for (BillItemProductDto bItems : billItems) {
//            String productName = "";
//            String unit = "";
//            productName = bItems.getLocalProductName() + "                                 ";
//            unit = bItems.getProductUnit();
//            if (unit.equals("LTR")) {
//                unit = "LT";
//            }
//            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
//            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
//            String price = "" + amt1;
//            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
//            String serialNo = i + "";
//            textData.append("" + serialNo + ") " + fixedLengthString(productName, 23) + "\n");
//            text = "" + unitPriceRoundOff + "  |  " + quantity + "  |  " + price + "\n\n";
////            textData.append(text);
//            textData.append(text.replaceAll("-", "&nbsp;"));
//            i++;
//        }
//        textData.append("------------------------------------------------------------\n");
//        String amt2 = Util.priceRoundOffFormat(billDto.getAmount());
////        String ledgerAmount = " " + pad(amt2, 7, " ");
////        Log.e(TAG, "ledgerAmount..." + ledgerAmount);
////        textData.append(" " + context.getString(R.string.print_total) + "                         " + ledgerAmount + "\n");
//        textData.append(" " + context.getString(R.string.print_total) + "        " + amt2 + "\n");
//        textData.append("------------------------------------------------------------\n");
//        textData.append("\n");
//        text = "------------------------" + context.getString(R.string.print_wishes) + " \n";
//        textData.append(text.replaceAll("-", "&nbsp;"));
//
//        textData.append("\n");
//        textData.append("\n");
//        textData.append("\n");
//        StringBuilder temptextData = new StringBuilder();
//        temptextData.append(textData.toString().replaceAll(" ", "&nbsp;"));
//        Log.e("Print", temptextData.toString());
//        return temptextData.toString();
//    }

    private String BluetoothPrintDataSmallFont() {
        StringBuilder textData = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        Calendar myCalendar = Calendar.getInstance();
        int am_pm = myCalendar.get(Calendar.AM_PM);
        String amOrpm = ((am_pm == Calendar.AM) ? "am" : "pm");
        try {
            convertedDate = dateFormat.parse(billDto.getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.getDefault());
        String refNo = billDto.getTransactionId();
        String billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(billDto.getBillItemDto());
        NumberFormat formatter = new DecimalFormat("00.00");
//        formatter.setRoundingMode(RoundingMode.CEILING);
//        NumberFormat formatSignle = new DecimalFormat("#0.00");
//        formatSignle.setRoundingMode(RoundingMode.CEILING);
        textData.append("                " + context.getString(R.string.print_title1) + "    " + "\n");
        textData.append("                " + context.getString(R.string.print_title2) + "    ");
        textData.append("\n");
        textData.append("\n");
        textData.append("" + context.getString(R.string.print_shopcode) + "  : " + SessionId.getInstance().getFpsCode() + "\n");
        textData.append("" + context.getString(R.string.print_billid) + "    : " + refNo + "\n");
        textData.append("" + context.getString(R.string.print_date) + "       : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData.append("" + context.getString(R.string.print_cardno) + "    : " + oldCardNumber + "\n");
        textData.append("------------------------------------------\n");
        textData.append("# " + context.getString(R.string.print_commodity) + "       " + "UNIT" + "     " + context.getString(R.string.print_qty) + "     " + context.getString(R.string.print_price) + "\n");
        textData.append("                  PRICE \n");
        textData.append("------------------------------------------\n");
        Log.e("Print2", textData.toString());
        int i = 1;
        for (BillItemProductDto bItems : billItems) {
            String productName = "";
            String unit = "";
            /*if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                productName = bItems.getLocalProductName() + "                                 ";
                unit = bItems.getLocalProductUnit();
                if (bItems.getProductUnit().equals("LTR")) {
                    if (bItems.getLocalProductUnit() != null) unit = bItems.getLocalProductUnit();
                    else unit = "LT";
                }
            } else {*/
            productName = bItems.getProductName();
            unit = bItems.getProductUnit();
            if (unit.equals("LTR")) {
                unit = "LT";
            }
//            }
            String quantity = "" + formatter.format(bItems.getQuantity()) + "(" + unit + ")";
            String amt1 = priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
//            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            String price = "" + amt1;
            String unitPriceRoundOff = priceRoundOffFormat(bItems.getCost());
            String serialNo = Integer.toString(i);
            textData.append(serialNo + ") " + fixedLengthString(productName, 14) + " ");
            textData.append(unitPriceRoundOff + " " + quantity + " " + pad(price, 7, " ") + "\n");
            i++;
        }
        textData.append("------------------------------------------\n");
        String amt2 = priceRoundOffFormat(billDto.getAmount());
        String ledgerAmount = "   " + pad(amt2, 7, " ");
//        textData.append(" " + context.getString(R.string.print_total) + "              " + ledgerAmount + "\n");
        textData.append(" " + context.getString(R.string.print_total) + "        " + amt2 + "\n");
        textData.append("------------------------------------------\n");
        textData.append("\n");
        textData.append("            " + context.getString(R.string.print_wishes) + " \n");
        textData.append("\n");
        textData.append("\n");
        textData.append("\n");
        Log.e("Print", textData.toString());
//        mPrinter.printUnicodeText(textData.toString(), Layout.Alignment.ALIGN_NORMAL, mDefaultTextPaint);
        return textData.toString();
        // Start of Printing content text size testing //
      /*  StringBuilder textData2 = new StringBuilder();
        textData2.append("                " + context.getString(R.string.print_title) + "    ");
        textData2.append("\n");
        textData2.append("" + context.getString(R.string.print_billid) + "     : " + refNo + "\n");
        textData2.append("" + context.getString(R.string.print_date) + "        : " + billDate + " " + amOrpm.toUpperCase() + "\n");
        textData2.append("-----------------------------------------\n");
        textData2.append("#    " + context.getString(R.string.print_commodity) + "        " + context.getString(R.string.print_qty) + "      " + context.getString(R.string.print_price) + "\n");
        textData2.append("-----------------------------------------\n");
        IntentPrint(textData2.toString());*/
        // End of Printing content text size testing //
    }

    public void onStart(final Node node) {
        Looper.prepare();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        try {
//            if (mUsbReceiver != null) {
//                unregisterReceiver(mUsbReceiver);
//                mUsbReceiver = null;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        removeAllDialogs();
//        new TaskClose();
//        try {
//            if (taskopen != null) {
//
//                taskopen.interrupt();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            es.shutdown();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void removeAllDialogs() {
        try {
            if (progressBar != null) {
                progressBar.dismiss();
            }
        } catch (Exception e) {
        }
    }

    private String priceRoundOffFormat(Double priceValue) {
        BigDecimal currQuantity = new BigDecimal(priceValue);
        currQuantity.setScale(2, RoundingMode.HALF_EVEN);
        priceValue = (double) Math.round(priceValue * 100);
        priceValue = priceValue / 100;
        NumberFormat formatter = new DecimalFormat("#00.00");
        String pr = formatter.format(priceValue);
        Double unitPriValue = Double.parseDouble(pr);
        String unitPrice = formatter.format(unitPriValue);
        return unitPrice;
    }

    private String fixedLengthString(String text, int length) {
        return String.format("%-" + length + "." + length + "s", text);
    }

    private String pad(String value, int length, String with) {
        StringBuilder result = new StringBuilder(length);
        // Pre-fill a String value
//        result.append(fill(Math.max(0, length - value.length()), with));
        result.append(Math.max(0, length - value.length()));
        result.append(value);
        return result.toString();
    }

    private String fill(int length, String with) {
        StringBuilder sb = new StringBuilder(length);
        while (sb.length() < length) {
            sb.append(with);
        }
        return sb.toString();
    }


    /*private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null && device.getVendorId() == 4070) {
                            // call method to set up device communication
                            mPos.Set(mUsb);
                            mUsb.SetCallBack(BillSuccessActivity.this);
                            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                                probe();
                            } else {
                                finish();
                            }
                        }
                    } else {
                        Log.d("ERROR", "permission denied for device " + device);
                    }
                }
            }
        }
    };*/

    private void checkInfo() {
//        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
//        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
//        String i = "";
//        while (deviceIterator.hasNext()) {
//            device = deviceIterator.next();
//            manager.requestPermission(device, mPermissionIntent);
//            i += "\n" + "DeviceID: " + device.getDeviceId() + "\n"
//                    + "DeviceName: " + device.getDeviceName() + "\n"
//                    + "DeviceClass: " + device.getDeviceClass() + " - "
//                    + "DeviceSubClass: " + device.getDeviceSubclass() + "\n"
//                    + "VendorID: " + device.getVendorId() + "\n"
//                    + "ProductID: " + device.getProductId() + "\n";
//        }
//        Log.e(TAG, "device info..." + i);
    }

}