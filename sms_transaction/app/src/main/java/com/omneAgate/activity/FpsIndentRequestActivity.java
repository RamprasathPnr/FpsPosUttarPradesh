package com.omneagate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.FPSIndentRequestDto;
import com.omneagate.DTO.FpsIntentReqProdDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;
import com.omneagate.activity.dialog.FpsIntentRequestDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FpsIndentRequestActivity extends BaseActivity {

    // Godown Dto list
    private List<FPSIndentRequestDto> fpsIndentRequestDtoList;

    // Fps Intent Request Product Dto Set
    private Set<FpsIntentReqProdDto> fpsIntentReqProdDtoSet;

    // Godown Dto
    private FPSIndentRequestDto fpsIndentRequestDtos;


    // Fps Intent Request Product List
    private List<FpsIntentReqProdDto> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fps_intent_request);
        appState = (GlobalAppState) getApplication();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        actionBarCreation();
        long fpsId = appState.getFpsId();
        //long godownId = FPSDBHelper.getInstance(this).getGodownId(fpsId);
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailProductLabel), getString(R.string.product));
        Util.setTamilText((TextView) findViewById(R.id.fpsInvardDetailQuantityLabel), getString(R.string.quantity));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDSubmit), getString(R.string.submit));
        Util.setTamilText((TextView) findViewById(R.id.btnfpsIDCancel), getString(R.string.cancel));
        fpsIndentRequestDtoList = FPSDBHelper.getInstance(this).showFpsIntentRequestProduct(fpsId);

        for (FPSIndentRequestDto fpsIndentRequestDto : fpsIndentRequestDtoList) {
            fpsIndentRequestDtos = new FPSIndentRequestDto();
            fpsIndentRequestDtos = fpsIndentRequestDto;
            fpsIntentReqProdDtoSet = fpsIndentRequestDto.getProdDtos();

        }
        configureData();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    /*Data from server has been set inside this function*/
    private void configureData() {
        try {
            LinearLayout fpsInwardLinearLayout = (LinearLayout) findViewById(R.id.listView_fps_intent_request_product);
            fpsInwardLinearLayout.removeAllViews();
            list = new ArrayList<>(fpsIntentReqProdDtoSet);

            for (int position = 0; position < list.size(); position++) {
                LayoutInflater lin = LayoutInflater.from(this);

                fpsInwardLinearLayout.addView(returnView(lin, list.get(position), position));
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
        }
    }

    /*User entitlement view*/
    private View returnView(LayoutInflater entitle, FpsIntentReqProdDto data, int position) {
        View convertView = entitle.inflate(R.layout.adapter_fps_intent_request, null);

        TextView serialNo = (TextView) convertView.findViewById(R.id.tvFIRSerialNo);
        TextView productName = (TextView) convertView.findViewById(R.id.tvFIRProductName);
        EditText quantity = (EditText) convertView.findViewById(R.id.edtFIRQuantity);
        ProductDto product = FPSDBHelper.getInstance(this).getProductDetails(data.getProductId());
        if (GlobalAppState.language.equals("ta") && StringUtils.isNotEmpty(product.getLocalProductName()))
            Util.setTamilText(productName, product.getLocalProductName() + "(" + product.getLocalProductUnit() + ")");
        else
            productName.setText(product.getName() + "(" + product.getProductUnit() + ")");
        serialNo.setText("" + (position + 1));
        quantity.setText("" + data.getQuantity());
        quantity.setId(position);

        return convertView;

    }


    // This method submit  product id and quantity

    public void onSubmit(View v) {
        try {
            List<FpsIntentReqProdDto> list = new ArrayList<>(fpsIntentReqProdDtoSet);
            Set<FpsIntentReqProdDto> fpsIntentReqProdDtoSet = new HashSet<>();
            boolean valueEntered = false;
            for (int i = 0; i < list.size(); i++) {
                String quantity = ((EditText) findViewById(i)).getText().toString();
                FpsIntentReqProdDto fpsIntentReqProdDto = new FpsIntentReqProdDto();
                fpsIntentReqProdDto.setProductId(list.get(i).getProductId());
                Double reqQuantity = 0.0;
                if (quantity.length() > 0) {
                    reqQuantity = Double.parseDouble(quantity);
                }
                if (reqQuantity > 0.0) {
                    valueEntered = true;
                }
                fpsIntentReqProdDto.setQuantity(reqQuantity);
                fpsIntentReqProdDtoSet.add(fpsIntentReqProdDto);
            }
            if (valueEntered) {
                fpsIndentRequestDtos.setProdDtos(fpsIntentReqProdDtoSet);
                getRequest(fpsIndentRequestDtos.getFpsId(), fpsIndentRequestDtos.getProdDtos());
            } else {
                Util.messageBar(this, getString(R.string.itemZero));
            }
        } catch (Exception e) {

        }
    }

    // Cancel Button
    public void onCancel(View v) {
        startActivity(new Intent(this, SaleActivity.class));
        finish();
    }


    //Request to Server

    private void getRequest(long fpsId, Set<FpsIntentReqProdDto> productSet) {
        try {
            FPSIndentRequestDto fpsRequest = new FPSIndentRequestDto();
            fpsRequest.setFpsId(fpsId);
            fpsRequest.setProdDtos(productSet);
            fpsRequest.setModifiedQuantity(0);
            fpsRequest.setDescription("Description");
            fpsRequest.setReason("reason");
            fpsRequest.setStatus(false);
            fpsRequest.setDateOfApproval(System.currentTimeMillis());//Date of approval by officer
            fpsRequest.setTalukOffiApproval(false);
            Log.e("requestFps", fpsRequest.toString());
            String login = new Gson().toJson(fpsRequest);
            StringEntity se = new StringEntity(login, HTTP.UTF_8);
            httpConnection = new HttpClientWrapper();
            String url = "/fpsIndent/create";
            httpConnection.sendRequest(url, null, ServiceListenerType.FPS_INTENT_REQUEST, SyncHandler, RequestType.POST, se, this);

        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }

    @Override
    protected void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FPS_INTENT_REQUEST:
                getStockResponse(message);
                break;

            default:
                break;
        }
    }


    // After response received from server successfully in android
    private void getStockResponse(Bundle message) {
        try {
            String response = message.getString(FPSDBConstants.RESPONSE_DATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            FPSIndentRequestDto fpsIndentRequestDto = gson.fromJson(response, FPSIndentRequestDto.class);
            int statusCode = fpsIndentRequestDto.getStatusCode();
            if (statusCode == 0) {
               /* Util.messageBar(this, getString(R.string.serverUpdated));*/
                FPSDBHelper.getInstance(this).insertFpsIntentReqProQuantity(fpsIndentRequestDtos);
                FpsIntentRequestDialog fpsIntentRequestDialog = new FpsIntentRequestDialog(this);
                fpsIntentRequestDialog.show();
            } else {
                Util.messageBar(this, FPSDBHelper.getInstance(this).retrieveLanguageTable(fpsIndentRequestDto.getStatusCode(), GlobalAppState.language).getDescription());
            }
        } catch (Exception e) {
            Util.LoggingQueue(this, "Error", e.toString());
            Log.e("Error", e.toString(), e);
        }

    }


}
