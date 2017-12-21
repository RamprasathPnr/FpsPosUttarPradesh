package com.omneagate.printer;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;
import com.lvrenyang.io.USBPrinting;

import com.omneagate.activity.R;
import com.omneagate.activity.TgDashBoardActivity;
import com.omneagate.activity.TgFPSReportsAllotmentActivity;
import com.omneagate.activity.TgReceiveGoods;
import com.omneagate.activity.TgReceiveKeroseneGoodsActivity;
import com.omneagate.activity.TgReceivegoodsComodityList;
import com.omneagate.activity.TgSalesActivity;
import com.omneagate.activity.TgSalesConfirmationActivity;
import com.omneagate.activity.TgViewLastTransactionActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by user1 on 1/2/17.
 */
public class Usb_Printer implements IOCallBack {
    public static String content;
    String TAG = "BillSuccessActivity";

    static Pos mPos;
    static public USBPrinting mUsb;
    private boolean printing = false, connecting = false;
    private static final String ACTION_USB_PERMISSION = "com.omneagate.activity.USB_PERMISSION";
    public static ExecutorService es;
    public static UsbManager usbmanager;
    public static UsbDevice usbdevice;
    private AlertDialog alertDialog;
    public int nPrintWidth = 384;
    public boolean bCutter = false;
    public boolean bDrawer = false;
    public boolean bBeeper = true;
    public int nPrintCount = 1;
    public int nCompressMethod = 0;
    public int nPrintContent = 0;
    public boolean bCheckReturn = false;
    private static Context context;
    public static Usb_Printer usb_printer;
    static int dwWriteIndex = 1;
    private BroadcastReceiver mUsbReceiver;
    ProgressDialog progress;
    public static boolean auto_print;


    public Usb_Printer(Context context) {
        this.context = context;
        mPos = new Pos();
        mUsb = new USBPrinting();
        mUsb.SetCallBack(this);
        mPos.Set(mUsb);
        progress = new ProgressDialog(context);
        if (es == null || es.isShutdown() || es.isTerminated())
            es = Executors.newScheduledThreadPool(10);
    }

    public static Usb_Printer getinstance(Context mcontext) {
        context = mcontext;
        if (usb_printer == null) {
            usb_printer = new Usb_Printer(context);
        }

        return usb_printer;
    }


    public void connectPrinter_new() {
        printing = true;
        boolean permission = check_usb_permission();
        if (permission) {
            if (mUsb.IsOpened()) {
                es.submit(new TaskPrint(mPos));
            } else {

                Toast.makeText(context, R.string.usb_printer_connecting, Toast.LENGTH_SHORT).show();
            }
        } /*else {
            Toast.makeText(BillSuccessActivity.this, R.string.no_premission, Toast.LENGTH_SHORT).show();
        }*/
        printing = false;
    }

    public boolean check_usb_permission() {
        check_executor();
        if (usbdevice == null) {
//            mPos = new Pos();
//            mUsb = new USBPrinting();
            usbmanager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            if (Usb_Printer.mUsb != null && Usb_Printer.mUsb.IsOpened())
                Usb_Printer.mUsb.Close();

            HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            boolean is_device = false;
            while (deviceIterator.hasNext()) {
                final UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == 4070 && device.getInterfaceCount() == 1) {
                    is_device = true;
                    usbdevice = device;
                    if (!usbmanager.hasPermission(usbdevice)) {
                        set_usb_permission_receiver();
                        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        usbmanager.requestPermission(usbdevice, mPermissionIntent);
                    } else if (!mUsb.IsOpened() && !connecting) {
                        connectToprinter();
                    }
                }
            }
            if (deviceList.size() == 0)
                printing = false;
            if (!is_device) {
                connecting = false;
                Toast.makeText(context, R.string.connect_printer, Toast.LENGTH_SHORT).show();
            }

            return false;
        } else if (!usbmanager.hasPermission(usbdevice)) {
            set_usb_permission_receiver();
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            usbmanager.requestPermission(usbdevice, mPermissionIntent);
            return false;
        } else if (!mUsb.IsOpened() && !connecting) {
            connectToprinter();
        }

        return true;
    }

    private void check_executor() {
        try {
            if (mUsb == null) {
                mUsb = new USBPrinting();
                mUsb.SetCallBack(this);
                mPos = new Pos();
                mPos.Set(mUsb);
            }
            if (es.isShutdown() || es.isTerminated()) {
                es = Executors.newScheduledThreadPool(10);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectToprinter() {
        show_button(false);

        connecting = true;
        es.submit(new TaskOpen(mUsb, usbmanager, usbdevice, context));

    }

    private void start_progress() {

        progress.setMessage(context.getString(R.string.usb_printer_connecting));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();
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


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(context.getApplicationContext(), bPrintResult ? context.getApplicationContext().getString(R.string.print_success1) : context.getApplicationContext().getString(R.string.print_failed1), Toast.LENGTH_SHORT).show();
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

                for (int i = 0; i < nCount; ++i) {
                    if (!pos.GetIO().IsOpened())
                        break;


                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bitmapPrint(gethtmlcontent(content, TgFPSReportsAllotmentActivity.fontsize), pos);
                        }

//                        private String hindicontent() {
//                            String text = "परिभाषाएँ और अंग्रेजी में सामग्री का अर्थ। विशेषण। संतुष्ट या चीजों के साथ संतोष दिखाने के रूप में वे कर रहे हैं। संज्ञा। कुछ (एक व्यक्ति या वस्तु या दृश्य";
//                            return text;
//                        }
                    });

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

    private void bitmapPrint(final String content, final Pos pos) {

        View promptsView = LayoutInflater.from(context).inflate(R.layout.sending_print, null);
        final WebView w = (WebView) promptsView.findViewById(R.id.mywebView);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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
                                ((Activity) context).runOnUiThread(new Runnable() {
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null) {
//                Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                return bitmap;
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public void makeBitmap(WebView w, WebView view, Pos pos, String content) {
        Log.e(TAG, "inside makeBitmap..." + content);
        float scale = context.getResources().getDisplayMetrics().density;
        int imgWidth = w.getWidth();
        int imgHeight = (int) (((float) view.getContentHeight()) * scale);
        Bitmap billbitmap = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(billbitmap);
        c.drawColor(-1);
        w.draw(c);
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Bitmap bitmap = drawableToBitmap(context.getResources().getDrawable(R.drawable.print_header_logo));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] byteArray = stream.toByteArray();
            Bitmap btMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (btMap != null) {
                pos.POS_PrintPicture(btMap, nPrintWidth, 1, nCompressMethod);
            }
            stream = new ByteArrayOutputStream();
            billbitmap = PrintImage(billbitmap);
            billbitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            /*String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/siva");
            myDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            File file = new File(myDir, fname);
            Log.i(TAG, "" + file);
            if (file.exists())
                file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                billbitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            byteArray = stream.toByteArray();
            btMap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (btMap != null) {
                pos.POS_PrintPicture(btMap, nPrintWidth, 1, nCompressMethod);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialog != null)
            alertDialog.dismiss();

        if (context.getClass().getCanonicalName().equals(TgSalesConfirmationActivity.class.getCanonicalName())) {
            Intent intent = new Intent(context, TgSalesActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else if (context.getClass().getCanonicalName().equals(TgViewLastTransactionActivity.class.getCanonicalName())) {
            Intent intent = new Intent(context, TgSalesActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else if (context.getClass().getCanonicalName().equals(TgReceivegoodsComodityList.class.getCanonicalName())) {
            Intent intent = new Intent(context, TgReceiveGoods.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        } else if (context.getClass().getCanonicalName().equals(TgReceiveKeroseneGoodsActivity.class.getCanonicalName())) {
            Intent intent = new Intent(context, TgDashBoardActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
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

    @Override
    public void OnOpen() {
        if (auto_print == true) {
            connectPrinter_new();
        } else {
            after_connect();
        }
    }

    private void after_connect() {

        connecting = false;
        show_button(true);

    }

    private void show_button(final boolean status) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TgFPSReportsAllotmentActivity.destroyed) {
                    TgFPSReportsAllotmentActivity activity = ((TgFPSReportsAllotmentActivity) context);
                    if (activity != null)
                        activity.show_print_button(status);

                }
            }
        });

    }

    @Override
    public void OnOpenFailed() {
        faileddialog();
        after_connect();

    }

    private void faileddialog() {

        usbdevice = null;
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, context.getString(R.string.usb_notproper), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void OnClose() {
        Log.e("OnClose", "OnClose");
        faileddialog();
        after_connect();
    }

    private void set_usb_permission_receiver() {
        if (mUsbReceiver == null) {
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            mUsbReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e("RK", "set_usb_permission_receiver" + intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false));
                    if (intent.getBooleanExtra(
                            UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        connectToprinter();

                    } else {

                    }
                    unregister_receiver();
                }
            };
            context.registerReceiver(mUsbReceiver, filter);
            Log.e("RK", "mUsbReceiver is null");
        }
    }

    public void unregister_receiver() {
        try {
            if (mUsbReceiver != null) {
                context.unregisterReceiver(mUsbReceiver);
                mUsbReceiver = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("rc unreg error", e.toString());
            mUsbReceiver = null;
        } finally {
            mUsbReceiver = null;
        }

    }
}
