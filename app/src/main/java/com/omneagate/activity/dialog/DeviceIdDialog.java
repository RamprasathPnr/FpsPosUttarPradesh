package com.omneagate.activity.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/*import com.google.zxing.BarcodeFormat;

import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;*/
/*import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;*/
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.omneagate.Util.AndroidDeviceProperties;
import com.omneagate.Util.TamilUtil;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;
/*import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.EAN13;
import com.onbarcode.barcode.android.IBarcode;*/
/*import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.AndroidFont;
import com.onbarcode.barcode.android.EAN13;
import com.onbarcode.barcode.android.IBarcode;*/

import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * This dialog will appear on the time of user logout
 */
public class DeviceIdDialog extends Dialog implements
        View.OnClickListener {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private final Activity context;  //    Context from the user

    /*Constructor class for this dialog*/
    public DeviceIdDialog(Activity _context) {
        super(_context);
        context = _context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_deviceid);
        setCancelable(false);
        TextView message = (TextView) findViewById(R.id.textViewNwText);
        TextView textViewDeviceId = (TextView) findViewById(R.id.textViewDeviceId);
        TextView apkVersion = (TextView) findViewById(R.id.textViewDevice);
        TextView textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        ImageView iv = (ImageView) findViewById(R.id.barcodeimage);

        Util.setTamilText((TextView) findViewById(R.id.textViewNwTitle), R.string.device_details);
        AndroidDeviceProperties props = new AndroidDeviceProperties(context);
        Util.setTamilText(message, R.string.deviceId);
        textViewDeviceId.setText(props.getDeviceProperties().getSerialNumber());
        Util.LoggingQueue(context, "Device id dialog", props.getDeviceProperties().getSerialNumber());
        apkVersion.setText("Apk Version");
        textViewVersion.setText(props.getDeviceProperties().getVersionName());
        Button okButton = (Button) findViewById(R.id.buttonNwOk);
        Util.setTamilText(okButton, R.string.ok);
        okButton.setOnClickListener(this);
        Bitmap bitmap = null;
        try {
//            generateQrCode(props.getDeviceProperties().getSerialNumber());
            bitmap = encodeAsBitmap(props.getDeviceProperties().getSerialNumber(), BarcodeFormat.CODE_128, 600, 100);
//            createBitmap();
            iv.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonNwOk:
                dismiss();
                break;
        }
    }

    /**
     * Tamil text textView typeface
     * input  textView name and id for string.xml
     */
    public void setTamilText(TextView textName, int id) {
        if (GlobalAppState.language.equals("hi")) {
            Typeface tfBamini = Typeface.createFromAsset(context.getAssets(), "fonts/Bamini.ttf");
            textName.setTypeface(tfBamini);
            textName.setText(TamilUtil.convertToTamil(TamilUtil.BAMINI, context.getString(id)));
        } else {
            textName.setText(context.getString(id));
        }
    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, (Hashtable) hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /*public static Bitmap generateQrCode(String myCodeText) throws WriterException {
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); // H = 30% damage

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        int size = 256;

        ByteMatrix bitMatrix = qrCodeWriter.encode(myCodeText,BarcodeFormat.QR_CODE, 600, 100, hintMap);
        int width = bitMatrix.width();
        Bitmap bmp = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < width; y++) {
                bmp.setPixel(y, x, bitMatrix.get(x, y)==0 ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }*/

    /*private void createBitmap() {
        try {
            EAN13 barcode = new EAN13();


	   *//*EAN 13 Valid data char set:
	        0, 1, 2, 3, 4, 5, 6, 7, 8, 9 (Digits)

	   EAN 13 Valid data length: 12 digits only, excluding the last checksum digit*//*

            AndroidDeviceProperties props = new AndroidDeviceProperties(context);
            String devId = props.getDeviceProperties().getSerialNumber();
            barcode.setData(devId);

            // for EAN13 with supplement data (2 or 5 digits)

	barcode.setSupData("12");
	// supplement bar height vs bar height ratio
	barcode.setSupHeight(0.8f);
	// space between barcode and supplement barcode (in pixel)
	barcode.setSupSpace(15);


            // Unit of Measure, pixel, cm, or inch
            barcode.setUom(IBarcode.UOM_PIXEL);
            // barcode bar module width (X) in pixel
            barcode.setX(1f);
            // barcode bar module height (Y) in pixel
            barcode.setY(45f);

            // barcode image margins
            barcode.setLeftMargin(10f);
            barcode.setRightMargin(10f);
            barcode.setTopMargin(10f);
            barcode.setBottomMargin(10f);

            // barcode image resolution in dpi
            barcode.setResolution(72);

            // disply barcode encoding data below the barcode
            barcode.setShowText(true);
            // barcode encoding data font style
            barcode.setTextFont(new AndroidFont("Arial", Typeface.NORMAL, 10));
            // space between barcode and barcode encoding data
            barcode.setTextMargin(6);
            barcode.setTextColor(AndroidColor.black);

            // barcode bar color and background color in Android device
            barcode.setForeColor(AndroidColor.black);
            barcode.setBackColor(AndroidColor.white);


	*//*specify your barcode drawing area*//*

            Canvas canvas = null;
            RectF bounds = new RectF(30, 30, 0, 0);
            barcode.drawBarcode(canvas, bounds);
        }
        catch(Exception e) {
            Log.e("gen bar exc",""+e);
        }
    }*/

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}