package com.omneagate.Util;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.ChellanProductDto;
import com.omneagate.DTO.EnumDTO.RequestType;
import com.omneagate.DTO.EnumDTO.ServiceListenerType;
import com.omneagate.DTO.EnumDTO.StockTransactionType;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.StockReqBaseDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.activity.R;
import com.omneagate.activity.StockInwardConfirmActivity;
import com.omneagate.activity.dialog.StockInwardDialog;
import com.omneagate.service.HttpClientWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created FpsStockInwardSubmit.
 */
public class FpsStockInwardSubmit {

    Activity context;

    List<GodownStockOutwardDto> fpsStockInwardDetailList;

    GodownStockOutwardDto godownStockOutwardDto;

    HttpClientWrapper httpConnection;

    Handler SyncHandler;

    public FpsStockInwardSubmit(Activity context, List<GodownStockOutwardDto> inwardList) {
        this.context = context;
        this.fpsStockInwardDetailList = inwardList;

    }

    public void submitServer(HttpClientWrapper httpConnection, Handler handler) {
        this.httpConnection = httpConnection;
        SyncHandler = handler;
        SendToServerUpdate();
    }

    private void SendToServerUpdate() {
        try {
            if (fpsStockInwardDetailList.size() != 0) {
                godownStockOutwardDto = new GodownStockOutwardDto();
                godownStockOutwardDto = fpsStockInwardDetailList.get(0);


                Set<ChellanProductDto> setChellanProductDto = new HashSet<>();
                for (GodownStockOutwardDto stockList : fpsStockInwardDetailList) {
                    ChellanProductDto chellanProductDto = new ChellanProductDto();
                    chellanProductDto.setProductId(stockList.getProductId());
                    chellanProductDto.setQuantity(stockList.getQuantity());
                    chellanProductDto.setReceiProQuantity(stockList.getQuantity());
                    chellanProductDto.setMonth(stockList.getMonth());
                    chellanProductDto.setYear(stockList.getYear());
                    chellanProductDto.setRecordId(stockList.getId());
                    setChellanProductDto.add(chellanProductDto);
                }
                Log.e("InwardSize", "" + setChellanProductDto.size());
                Log.e("godownStockOutwardDto", ""+godownStockOutwardDto.toString());
                godownStockOutwardDto.setProductDto(setChellanProductDto);
            }
            getRequest();
        } catch (Exception e) {
            Log.e("fpsStockInwardDetail", e.toString(), e);
        }

    }

    //Reguest to Server
    private void getRequest() {
        try {
            if (FPSDBHelper.getInstance(context).getStockExists(godownStockOutwardDto)) {
                StockReqBaseDto stockReqBaseDto = new StockReqBaseDto();
                stockReqBaseDto.setType("com.omneagate.rest.dto.StockRequestDto");
                StockInwardConfirmActivity.stockRequestDto = new StockRequestDto();
                StockInwardConfirmActivity.stockRequestDto.setBatchNo("" + godownStockOutwardDto.getBatchno());
                StockInwardConfirmActivity.stockRequestDto.setDeliveryChallanId("" + godownStockOutwardDto.getDeliveryChallanId());
                StockInwardConfirmActivity.stockRequestDto.setType(StockTransactionType.INWARD);//Stock transaction type
                StockInwardConfirmActivity.stockRequestDto.setGodownId(godownStockOutwardDto.getGodownId());
                StockInwardConfirmActivity.stockRequestDto.setFpsId(godownStockOutwardDto.getFpsId());
                StockInwardConfirmActivity.stockRequestDto.setReferenceNo(godownStockOutwardDto.getReferenceNo());
                StockInwardConfirmActivity.stockRequestDto.setProductLists(createProducts());
                StockInwardConfirmActivity.stockRequestDto.setDate(new DateTime().getMillis());
                StockInwardConfirmActivity.stockRequestDto.setCreatedBy("" + godownStockOutwardDto.getCreatedby());
                StockInwardConfirmActivity.stockRequestDto.setUnit(godownStockOutwardDto.getUnit());
                StockInwardConfirmActivity.stockRequestDto.setGodownName(godownStockOutwardDto.getGodownName());
                StockInwardConfirmActivity.stockRequestDto.setGodownCode(godownStockOutwardDto.getGodownCode());
                stockReqBaseDto.setBaseDto(StockInwardConfirmActivity.stockRequestDto);
                NetworkConnection networkConnection = new NetworkConnection(context);
//                String inward1 = new Gson().toJson(stockReqBaseDto);
//                Log.e("send to server", inward1);

                if (networkConnection.isNetworkAvailable() && SessionId.getInstance().getSessionId() != null && StringUtils.isNotEmpty(SessionId.getInstance().getSessionId())) {
                    String inward = new Gson().toJson(stockReqBaseDto);
                    Log.e("send to server", inward);
                    StringEntity se = new StringEntity(inward, HTTP.UTF_8);
                    httpConnection = new HttpClientWrapper();
                    Util.LoggingQueue(context, "Stock Inward detail activity", "Request Inward:" + inward);
                    String url = "/fpsStock/inward";
                    httpConnection.sendRequest(url, null, ServiceListenerType.FPS_INTENT_REQUEST, SyncHandler, RequestType.POST, se, context);
                } else {
                    String stock_validation = "" + FPSDBHelper.getInstance(context).getMasterData("stock_validation");
                    if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                        if (stock_validation.equalsIgnoreCase("0")) {
                            FPSDBHelper.getInstance(context).updateReceivedQuantityTwo(StockInwardConfirmActivity.stockRequestDto, true);
                        } else if (stock_validation.equalsIgnoreCase("1")) {
                            FPSDBHelper.getInstance(context).updateReceivedQuantity(StockInwardConfirmActivity.stockRequestDto, true);
                        }
                    }
                    else {
                        FPSDBHelper.getInstance(context).updateReceivedQuantityTwo(StockInwardConfirmActivity.stockRequestDto, true);
                    }
                    ((StockInwardConfirmActivity) context).dismissDialog();
                    StockInwardDialog stockInwardDialog = new StockInwardDialog(context);
                    stockInwardDialog.show();
                }
            } else {
                ((StockInwardConfirmActivity) context).dismissDialog();
                Util.messageBar(context, context.getString(R.string.internalError));
            }
        } catch (Exception e) {
            Util.LoggingQueue(context, "Error", e.toString());
            Log.e("FPSStockInwardDetail", e.toString(), e);
        }

    }


    private List<StockRequestDto.ProductList> createProducts() {

        List<StockRequestDto.ProductList> prods = new ArrayList<>();
        List<ChellanProductDto> challan = new ArrayList(godownStockOutwardDto.getProductDto());
        Log.e("inward submit","challan list...."+challan.toString());
        for (ChellanProductDto ch : challan) {
//            DateTime dateTime = new DateTime();
//            Log.e("Current Month", dateTime.getMonthOfYear() + "::" + dateTime.getYear() + "::::::::" + ch.getMonth() + "::" + ch.getYear());
//            if (dateTime.getMonthOfYear() == ch.getMonth() && dateTime.getYear() == ch.getYear()) {
            Log.e("inward submit","challan list 2...."+FPSDBHelper.getInstance(context).getChallans(godownStockOutwardDto.getReferenceNo(), ch.getProductId(), ch.getRecordId()));
                prods.addAll(FPSDBHelper.getInstance(context).getChallans(godownStockOutwardDto.getReferenceNo(), ch.getProductId(), ch.getRecordId()));
            /*} else {
                prods.addAll(FPSDBHelper.getInstance(context).getChallans(godownStockOutwardDto.getReferenceNo(), ch.getProductId(), false));
            }*/
        }
        return prods;
    }



}
