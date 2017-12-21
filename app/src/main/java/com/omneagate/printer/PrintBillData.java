package com.omneagate.printer;

import android.content.Context;
import android.util.Log;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by user1 on 21/4/15.
 */
public class PrintBillData {
    Context context;
    UpdateStockRequestDto updateStock;


    String billDate = "";
    String productName = "";
    String quantity = "";
    String price = "";
    String serialNo = "";
    String head = "";

    String ufc = "";
    String refNo = "";
    String ledgerAmount = "";

    List<FpsStoreDto> fpsStore;

    public PrintBillData(Context context, UpdateStockRequestDto updateStock) {
        this.context = context;
        this.updateStock = updateStock;
    }

    public void printBill() {
        BlueToothCheck blueToothCheck = new BlueToothCheck(context, printBills());
        blueToothCheck.checkBluetooth();
    }

    private String printBills() {
       // StringBuilder contentProduct = setBillProductDetails();
        fpsStore = FPSDBHelper.getInstance(context).retrieveDataStore();
        FpsStoreDto fpsData = getFPSStore();
        StringBuilder contentSb = new StringBuilder();

        String fpsCode = "";
        if (StringUtils.isNotEmpty(fpsData.getCode())) {
            fpsCode = fpsData.getCode();
        }
        String titleCenter = "testchar";
        contentSb.append(titleCenter);
        String printContent = contentSb.toString();
        Log.e("printcontent",printContent);
        return printContent;
    }

    private FpsStoreDto getFPSStore() {
        for (FpsStoreDto fps : fpsStore) {
            if (StringUtils.isNotEmpty(fps.getCode())) {
                return fps;
            }
        }
        return new FpsStoreDto();
    }

    private StringBuilder setBillProductDetails() {
        StringBuilder contentProduct = new StringBuilder();
        setBeneficiaryDetails(updateStock);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(updateStock.getBillDto().getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
        refNo = updateStock.getBillDto().getTransactionId();
        billDate = dateFormat.format(convertedDate);
        List<BillItemProductDto> billItems = new ArrayList<BillItemProductDto>(updateStock.getBillDto().getBillItemDto());
        /*NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        /*NumberFormat quantityFormat = new DecimalFormat("#0.000");
        quantityFormat.setRoundingMode(RoundingMode.CEILING);*/
        int i = 1;
        for (BillItemProductDto bItems : billItems) {
            productName = bItems.getProductName() + "                                 ";
            String qty1 = Util.quantityRoundOffFormat(bItems.getQuantity());
            quantity = "                        " + qty1 + "(" + bItems.getProductUnit() + ")";
            String amt1 = Util.priceRoundOffFormat(bItems.getCost() * bItems.getQuantity());
            price = "                        " + amt1;
            int priceLength = price.length();
            price = StringUtils.substring(price, priceLength - 6);
            priceLength = quantity.length();
            quantity = StringUtils.substring(quantity, priceLength - 12);
            serialNo = i + "                        ";
            contentProduct.append("  " + StringUtils.substring(serialNo, 0, 2) + "  " + StringUtils.substring(productName, 0, 15) + " " + quantity + "       " + price + "\r\n");
            i++;
        }
        contentProduct.append("                                  ----------\r\n");
        ledgerAmount = StringUtils.substring(ledgerAmount, ledgerAmount.length() - 7);
        contentProduct.append("                     " + "! U1 SETBOLD 2\r\n" + "Total" + "! U1 SETBOLD 0\r\n" + "      Rs." + ledgerAmount + "\r\n");
        contentProduct.append("                                  ----------\r\n");
        return contentProduct;
    }

    private void setBeneficiaryDetails(UpdateStockRequestDto updateStockRequestDto) {

        BeneficiaryDto beneficiaryDto = FPSDBHelper.getInstance(context).retrieveBeneficiary(updateStockRequestDto.getBillDto().getBeneficiaryId());
        ufc = Util.DecryptedBeneficiary(context, beneficiaryDto.getEncryptedUfc());
        head = "";//beneficiaryDto.getFamilyMembers().get(0).getName();
        /*NumberFormat formatter = new DecimalFormat("#0.00");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        String amt1 = Util.priceRoundOffFormat(updateStockRequestDto.getBillDto().getAmount());
        ledgerAmount = "   " + amt1;
    }

}
