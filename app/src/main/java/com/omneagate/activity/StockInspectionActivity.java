package com.omneagate.activity;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.NoDefaultSpinner;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.InspectionRemoveDialog;
import com.omneagate.activity.dialog.StockInspectionDialog;
import com.omneagate.activity.dialog.StockInspectionViewDialog;
import com.omneagate.activity.dialog.UnsavedStockInspectionDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StockInspectionActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    List<ProductDto> getProductDetails;
    List<String> productSpinnerList;
    public static final int IMAGE_CAPTURE_REQUEST_CODE = 1;
    private static final int FILE_SELECT_CODE = 2;
    List<byte[]> imagesList;
    List<String> photoPathList;
    private List<String> stackList;
    private ImageView mIvBack;
    //    private Button mBtCancel;
    private TextView mTvTitle, total, noCommodity;
    private Button mBtNext, addBtn, mBtCancel;
    EditText remarks;
    InspectionRemoveDialog inspectionRemoveDialog;
    StockInspectionDialog stockInspectionDialog;
    UnsavedStockInspectionDialog unsavedStockInspectionDialog;
    StockInspectionViewDialog stockInspectionViewDialog;
    /*RelativeLayout keyBoardCustom, keyboardAlpha, keyboardumber;
    KeyboardView keyview, keyboardViewAlpha;
    KeyBoardEnum keyBoardFocused;*/
    public static String productId = "";
    public static double systemStock, existingStock, stockVariance;
    //    List<StockInspectionDto> stockInspectionDtoList;
    public static StockInspectionDto editedStockInspectionDto;
    public static boolean stockInspectionDialogValidation = false;
    InspectionFindingActivity inspectionFindingActivity;
    int listSize;
    public NoDefaultSpinner commoditySpinner;
    String TAG = "StockInspectionActivity";
    public ArrayAdapter<String> adapter;

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_inspection);
        findView();
        getProductList();
        loadViewData();
    }

    private void findView() {
        mTvTitle = (TextView) findViewById(R.id.top_textView);
        noCommodity = (TextView) findViewById(R.id.noCommodity);
        remarks = (EditText) findViewById(R.id.edt_remark);
        mIvBack = (ImageView) findViewById(R.id.imageViewBack);
        mBtCancel = (Button) findViewById(R.id.btn_cancel);
        mBtNext = (Button) findViewById(R.id.btn_submit);
        addBtn = (Button) findViewById(R.id.btn_add_new);
        total = (TextView) findViewById(R.id.txt_total);
        mBtNext.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mBtCancel.setOnClickListener(this);
//        mBtCancel.setOnClickListener(this);
        addBtn.setOnClickListener(this);

        /*stackList = new ArrayList<>();
        stackList.add("Stock");
        stackList.add("Card verification");
        stackList.add("Weight inspection");
        stackList.add("Shop open/close");
        stackList.add("Shop open/close");
        loadTableValues(stackList);*/
    }

    private void getProductList() {
        getProductDetails = new ArrayList<>();
        getProductDetails = FPSDBHelper.getInstance(this).getProduct();
        productSpinnerList = new ArrayList<String>();
        for (ProductDto productDto : getProductDetails) {
            String productName = productDto.getName();
            String productLocalName = productDto.getLocalProductName();
            String productId = String.valueOf(productDto.getId());
            if (productName != null) {
                if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                    productSpinnerList.add(productLocalName + "~" + productId);
                } else {
                    productSpinnerList.add(productName + "~" + productId);
                }
            }
        }
    }

    private void loadViewData() {
        setUpInspectionPopUpPage();
        systemStock = 0.0;
        existingStock = 0.0;
        stockVariance = 0.0;
        loadStockInspectionList();
        inspectionFindingActivity = new InspectionFindingActivity();
        mTvTitle.setText(getResources().getString(R.string.stock_inspection));
        total.setText(": " + String.valueOf(Util.findingCriteriaDto.getStockInspection().size()) + " " + getResources().getString(R.string.inspection));
        listSize = Util.findingCriteriaDto.getStockInspection().size();
        loadProductSpinner();
//        stockInspectionDtoList = new ArrayList<>();
//        loadStockInspectionList();
        /*keyBoardCustom = (RelativeLayout) findViewById(R.id.key_board_custom);
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
        keyBoardCustom.setVisibility(View.GONE);*/
    }

    private void loadProductSpinner() {
        commoditySpinner = (NoDefaultSpinner) findViewById(R.id.commoditySpinner);
        Log.e("StockInspectionActivity", "productSpinnerList..." + productSpinnerList);
        List<String> productName = new ArrayList<>();
        for (int i = 0; i < productSpinnerList.size(); i++) {
            String[] productDetail = productSpinnerList.get(i).split("~");
            productName.add(productDetail[0]);
        }
adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productName);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        commoditySpinner.setAdapter(adapter);
        commoditySpinner.setPrompt(getString(R.string.selection));
        commoditySpinner.setFocusable(true);
        commoditySpinner.setFocusableInTouchMode(true);
        commoditySpinner.requestFocus();
        commoditySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        commoditySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String[] productDetail = productSpinnerList.get(position).split("~");
                String productNameStr = productDetail[0];
                productId = productDetail[1];
                double qty = FPSDBHelper.getInstance(StockInspectionActivity.this).getStockOfSpecificProduct(productId);
                stockInspectionDialog = new StockInspectionDialog(com.omneagate.activity.StockInspectionActivity.this, qty, productNameStr);
                stockInspectionDialog.show();
                stockInspectionDialog.setCanceledOnTouchOutside(false);
                remarks.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void loadStockInspectionList() {
//        stockInspectionDtoList = FPSDBHelper.getInstance(this).getAllStockInspectionData();
        if (Util.findingCriteriaDto.getStockInspection().size() > 0) {
            noCommodity.setVisibility(View.GONE);
        } else {
            noCommodity.setVisibility(View.VISIBLE);
        }
        total.setText(": " + String.valueOf(Util.findingCriteriaDto.getStockInspection().size()) + " " + getResources().getString(R.string.inspection));
        loadTableValues(Util.findingCriteriaDto.getStockInspection());
    }

    // This method gets the photo from camera
    private void onCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
    }

    /**
     * After photo taken by camera
     * From activity result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGE_CAPTURE_REQUEST_CODE:
                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (bitmap != null) {
                        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                        String photoPath = getRealPathFromURI(tempUri);
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        byte[] ba = bao.toByteArray();
                        if (ba != null) {
                            if (imagesList.size() < 3) {
                                imagesList.add(ba);
                                photoPathList.add(photoPath);
                                setImageLayout(imagesList, photoPathList);
                            } else {
                                Toast.makeText(this, "You are allowed capture only 3 images", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Log.e("ImageListSize Camera", "" + imagesList.size());
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
                    Log.e("Camera", e.toString());
                }
                break;
            case FILE_SELECT_CODE:
                try {
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String photoPath = cursor.getString(column_index);
                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(photoPath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(photoPath, options);
                    if (bm != null) {
                        String[] strArr = null;
                        strArr = photoPath.split("/");
                        int size = strArr.length;
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        if (strArr[size - 1].toLowerCase().contains(".jpg")) {
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        } else if (strArr[size - 1].toLowerCase().contains(".jpeg")) {
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, bao);
                        } else if (strArr[size - 1].toLowerCase().contains(".png")) {
                            bm.compress(Bitmap.CompressFormat.PNG, 50, bao);
                        } else {
                            Toast.makeText(this, "Invalid File format", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        byte[] ba = bao.toByteArray();
                        if (imagesList.size() < 3) {
                            imagesList.add(ba);
                            photoPathList.add(photoPath);
                            setImageLayout(imagesList, photoPathList);
                        } else {
                            Toast.makeText(this, "You are allowed capture only 3 images", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } catch (Exception e) {
                    Log.e("Attachement", e.toString());
                }
                break;
            default:
                Log.e("Error", "Failed to load photo");
                break;
        }
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private Bitmap ByteArrayToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void setImageLayout(List<byte[]> imageListByte, final List<String> listPhotoPath) {
        /*final LinearLayout linearLayoutRow1 = (LinearLayout)findViewById(R.id.attachmentLayoutRow1);
        linearLayoutRow1.setVisibility(View.VISIBLE);
        linearLayoutRow1.removeAllViews();
        imagesList = imageListByte;
        photoPathList = listPhotoPath;

        Log.e("PhotopathList_before_cancel",photoPathList.toString());

        int i=0;
        for(byte[] bytes:imageListByte){
            final  LinearLayout linearBg = new LinearLayout(this);
            linearBg.setLayoutParams(new LinearLayout.LayoutParams(90,90));
            final  LinearLayout linearSpace = new LinearLayout(this);
            linearSpace.setLayoutParams(new LinearLayout.LayoutParams(10,10));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,90);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER;
//            linearBg.setBackgroundResource(R.drawable.spinner_background);
            final ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(80,80));
            //imageView.setPadding(5,5,5,5);
            imageView.setLayoutParams(params);
            imageView.setImageBitmap(ByteArrayToBitmap(bytes));
            linearBg.addView(imageView);
            linearBg.setPadding(10, 0, 10, 0);
            linearBg.setId(10000 + i);
            Log.e("ImageLaoutId", "" + (100000 + i));
            i++;
            linearLayoutRow1.addView(linearBg);
            linearLayoutRow1.addView(linearSpace);
            linearBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageCancelDialog imageCancelDialog = new ImageCancelDialog(InspectionActivity.this,v.getId(),imagesList,photoPathList);
                    imageCancelDialog.show();
                }
            });
        }*/
    }

    private void loadTableValues(List<StockInspectionDto> value) {
        try {
            LinearLayout transactionLayout = (LinearLayout) findViewById(R.id.listView_linearLayout_stock_status);
            transactionLayout.removeAllViews();
            LayoutInflater lin = LayoutInflater.from(StockInspectionActivity.this);
            int sno = 1;
            /*for (FindingCriteriaDto findingCriteriaDto : value) {
                transactionLayout.addView(returnView(lin, sno, findingCriteriaDto));
                sno++;
            }*/
            for (int j = value.size() - 1; j >= 0; j--) {
                transactionLayout.addView(returnView(lin, sno, value.get(j), j));
                sno++;
            }
        } catch (Exception e) {
            Log.e("StockInspectionActivity", "loadTableValues exc..." + e);
        }
    }

    private View returnView(LayoutInflater entitle, final int sno, final StockInspectionDto dto, final int position) {
        View convertView = entitle.inflate(R.layout.adapter_stock_inspection, new LinearLayout(this), false);
        TextView mTvSno = (TextView) convertView.findViewById(R.id.sno);
        TextView mTvType = (TextView) convertView.findViewById(R.id.txt_type);
        ImageView remove = (ImageView) convertView.findViewById(R.id.img_remove);
//        ImageView viewRecord = (ImageView) convertView.findViewById(R.id.img_explore);
        mTvSno.setText("" + sno);
        final String productName = FPSDBHelper.getInstance(this).getProductName(dto.getCommodity());
        mTvType.setText(productName);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inspectionRemoveDialog = new InspectionRemoveDialog(com.omneagate.activity.StockInspectionActivity.this, position, productName,"StockInspectionActivity");
                inspectionRemoveDialog.show();
            }
        });
        /*viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stockInspectionViewDialog = new StockInspectionViewDialog(com.omneagate.activity.StockInspectionActivity.this, stockInspectionDto, productId);
                stockInspectionViewDialog.show();
                stockInspectionDtoList.remove(sno - 1);
                InspectionReportDto inspectionReportDto = FPSDBHelper.getInstance(StockInspectionActivity.this).getLastInsertedReportClientId();
                editedStockInspectionDto.setClientReportId(inspectionReportDto.getClientId());
                stockInspectionDtoList.add(editedStockInspectionDto);
                editedStockInspectionDto = null;
                loadStockInspectionList();
            }
        });*/
        return convertView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewBack:
                onBackPressed();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
            case R.id.btn_submit:
                if (commoditySpinner.getSelectedItem() == null) {
                    if (remarks.getText().toString().trim().equalsIgnoreCase("")) {
                        if (existingStock == 0.0) {
                            finishStockInspection();
                            break;
                        }
                    }
                }
                boolean added = addStockInspection();
                if (added) {
                    finishStockInspection();
                }
                break;
//            case R.id.capture_image:
//                onCamera();
//                break;
            case R.id.edt_remark:
                /*checkVisibility();
                remarks.requestFocus();
                keyBoardAppear();
                changeLayout(false);
                keyBoardFocused = KeyBoardEnum.REMARKS;*/
                break;
            case R.id.btn_add_new:
                addStockInspection();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "existing stock..." + existingStock);
        if (!(commoditySpinner.getSelectedItem() == null)) {
            if (!stockInspectionDialogValidation) {
                finishStockInspection();
            } else {
                unsavedStockInspectionDialog = new UnsavedStockInspectionDialog(com.omneagate.activity.StockInspectionActivity.this, getResources().getString(R.string.unsavedStockInspection));
                unsavedStockInspectionDialog.show();
                unsavedStockInspectionDialog.setCanceledOnTouchOutside(false);
            }
        } else if (!remarks.getText().toString().equalsIgnoreCase("")) {
            unsavedStockInspectionDialog = new UnsavedStockInspectionDialog(com.omneagate.activity.StockInspectionActivity.this, getResources().getString(R.string.unsavedStockInspection));
            unsavedStockInspectionDialog.show();
            unsavedStockInspectionDialog.setCanceledOnTouchOutside(false);
        } else {
            finishStockInspection();
        }
    }

    public void finishStockInspection() {
        Intent intent = new Intent(StockInspectionActivity.this, InspectionFindingActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean addStockInspection() {
        Log.e(TAG, "existingStock..." + existingStock);
        if (commoditySpinner.getSelectedItem() == null) {
            Toast.makeText(StockInspectionActivity.this, R.string.sel_commodity, Toast.LENGTH_SHORT).show();
            return false;
        } else if (remarks.getText().toString().trim().equalsIgnoreCase("")) {
            Toast.makeText(StockInspectionActivity.this, R.string.enter_remarks, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!stockInspectionDialogValidation) {
            Toast.makeText(StockInspectionActivity.this, R.string.enter_physical_stock, Toast.LENGTH_SHORT).show();
            return false;
        } else if (containsProductId(Util.findingCriteriaDto.getStockInspection(), Long.valueOf(productId))) {
            Toast.makeText(StockInspectionActivity.this, R.string.stock_already_inspected, Toast.LENGTH_SHORT).show();
            return false;
        } else {
//            InspectionReportDto inspectionReportDto = FPSDBHelper.getInstance(StockInspectionActivity.this).getLastInsertedReportClientId();
            StockInspectionDto stockInspectionDto = new StockInspectionDto();
            stockInspectionDto.setCommodity(Long.valueOf(productId));
            stockInspectionDto.setPosStock(systemStock);
            stockInspectionDto.setActualStock(existingStock);
            stockInspectionDto.setVariance(stockVariance);
            stockInspectionDto.setRemarks(remarks.getText().toString());
            stockInspectionDto.setTransactionId(Util.getInspectionStockTransactionId(this));
//                    stockInspectionDto.setClientReportId(inspectionReportDto.getClientId());
//                    stockInspectionDtoList.add(stockInspectionDto);
            systemStock = 0.0;
            existingStock = 0.0;
            stockVariance = 0.0;
            stockInspectionDialogValidation = false;
//            FindingCriteriaDto findingCriteria = new FindingCriteriaDto();
//            findingCriteria.setCriteria(InspectionConstants.Stock_Inspection);
//            findingCriteria.setStockInspection(stockInspectionDto);
            Util.findingCriteriaDto.getStockInspection().add(stockInspectionDto);
            listSize = Util.findingCriteriaDto.getStockInspection().size();
            loadStockInspectionList();
            remarks.setText("");
            loadProductSpinner();
            return true;
        }
    }

    public static boolean containsProductId(List<StockInspectionDto> list, long id) {
        for (StockInspectionDto dto: list) {
            if (dto.getCommodity() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.edt_remark && hasFocus) {
            /*remarks.requestFocus();
            checkVisibility();
            keyBoardAppear();
            keyBoardFocused = KeyBoardEnum.REMARKS;
            changeLayout(false);*/
        }
    }

    /*private void keyBoardAppear() {
        keyboardumber.setVisibility(View.VISIBLE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void keyBoardDisappear() {
        keyboardumber.setVisibility(View.GONE);
        keyboardAlpha.setVisibility(View.GONE);
    }

    private void changeKeyboard() {
        try {
            keyboardumber.setVisibility(View.GONE);
            keyboardAlpha.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e("Error","keyboard");
        }
    }

    private void changeLayout(boolean value) {
        RelativeLayout relativelayout = (RelativeLayout) findViewById(R.id.masterStockInspLay);
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

    private void checkVisibility() {
        if (keyBoardCustom.getVisibility() == View.GONE) {
            keyBoardCustom.setVisibility(View.VISIBLE);
        }
    }*/

    private void listenersForEditText() {
        remarks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /*class KeyList implements KeyboardView.OnKeyboardActionListener {
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
                if (keyBoardFocused == KeyBoardEnum.REMARKS) {
                    String text = remarks.getText().toString();
                    if (remarks.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        remarks.setText(text);
                        remarks.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
                if (keyBoardFocused == KeyBoardEnum.REMARKS) {
                    remarks.requestFocus();
                }
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.REMARKS) {
                    remarks.append("" + ch);
                }
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
                if (keyBoardFocused == KeyBoardEnum.REMARKS) {
                    String text = remarks.getText().toString();
                    if (remarks.length() > 0) {
                        text = text.substring(0, text.length() - 1);
                        remarks.setText(text);
                        remarks.setSelection(text.length());
                    }
                }
            } else if (primaryCode == 46) {
                keyBoardCustom.setVisibility(View.GONE);
            } else {
                char ch = (char) primaryCode;
                if (keyBoardFocused == KeyBoardEnum.REMARKS) {
                    remarks.append("" + ch);
                }
            }
        }

        public void onRelease(int primaryCode) {

        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("StockInspectionActivity", "on destroy called");
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
            if ((stockInspectionDialog != null) && stockInspectionDialog.isShowing()) {
                stockInspectionDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            stockInspectionDialog = null;
        }
        try {
            if ((unsavedStockInspectionDialog != null) && unsavedStockInspectionDialog.isShowing()) {
                unsavedStockInspectionDialog.dismiss();
            }
        } catch (final Exception e) {
        } finally {
            unsavedStockInspectionDialog = null;
        }
    }
}
