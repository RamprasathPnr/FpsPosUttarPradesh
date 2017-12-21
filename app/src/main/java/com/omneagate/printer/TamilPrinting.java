package com.omneagate.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.omneagate.activity.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by user1 on 9/11/16.
 */
public class TamilPrinting {
    private Context context;
    private Activity activity;
    private OutputStream outStream;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private BluetoothSocket btSocket;
    private AlertDialog alertDialog;
    static TamilPrinting tamilPrinting;

    public void print(OutputStream outputstream, Context mcontext, Activity mactivity, final String text, String address, final int fontsize) {
        context = mcontext;
        activity = mactivity;
        if (outputstream == null) {
            if (btSocket == null || !btSocket.isConnected()) {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                device = mBluetoothAdapter.getRemoteDevice(address);
                try {
                    btSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    btSocket.connect();
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    outStream = btSocket.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            outStream = outputstream;
        }
        if (outStream != null) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    bitmapPrint(gethtmlcontent(text, fontsize));
                }
            });
        }
    }

    public static TamilPrinting getinstance() {
        if (tamilPrinting == null) {
            tamilPrinting = new TamilPrinting();
        }
        return tamilPrinting;
    }

    private String gethtmlcontent(String content, int font) {
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

    private void bitmapPrint(String content) {
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
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        makeBitmap(webView1, webView22);
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

    public void makeBitmap(WebView w, WebView view) {
        float scale = context.getResources().getDisplayMetrics().density;
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
            byte[] data = POS_PrintBMP(btMap, 384, 0);
            if (data != null) {
                outStream.write(data);
            }
            outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        alertDialog.dismiss();
    }

    public byte[] POS_PrintBMP(Bitmap mBitmap, int nWidth, int nMode) {
        int width = ((nWidth + 7) / 8) * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = ((height + 7) / 8) * 8;
        Bitmap rszBitmap = mBitmap;
        if (mBitmap.getWidth() != width) {
            rszBitmap = Other.resizeImage(mBitmap, width, height);
        }
        Bitmap grayBitmap = Other.toGrayscale(rszBitmap);
        byte[] dithered = Other.thresholdToBWPic(grayBitmap);
        byte[] data = Other.eachLinePixToCmd(dithered, width, nMode);
        return data;
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
}
