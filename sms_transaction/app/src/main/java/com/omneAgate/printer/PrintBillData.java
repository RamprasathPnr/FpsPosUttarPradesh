package com.omneagate.printer;

import android.content.Context;
import android.util.Log;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.UpdateStockRequestDto;
import com.omneagate.Util.FPSDBHelper;
import com.omneagate.Util.Util;

import org.apache.commons.lang3.StringUtils;

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
        StringBuilder contentProduct = setBillProductDetails();
        fpsStore = FPSDBHelper.getInstance(context).retrieveDataStore();
        FpsStoreDto fpsData = getFPSStore();
        StringBuilder contentSb = new StringBuilder();

        String fpsCode = "";
        if (StringUtils.isNotEmpty(fpsData.getCode())) {
            fpsCode = fpsData.getCode();
        }
        String titleCenter = "! U1 SETLP 5 2 46" +
                "! U1 CENTER" +
                "                  FPS Sales Bill\r\n" +
                "! U1 SETLP 5 0 24\n" +
                "               CODE : " + fpsCode + "\r\n" +
                "! U1 SETLP 3 0 24\r\n" +
                "! U1 SETLP 7 0 24\r\n";

        contentSb.append(titleCenter);
        contentSb.append("! U1 SETBOLD 2\r\n" + "Transaction Ref  : " + "! U1 SETBOLD 0\r\n" + refNo + "\r\n");
        contentSb.append("! U1 SETBOLD 2\r\n" + "Transaction Date : " + "! U1 SETBOLD 0\r\n" + billDate + "\r\n");
        contentSb.append("! U1 SETBOLD 2\r\n" + "UFC              : " + "! U1 SETBOLD 0\r\n" + ufc + " " + "\r\n");
        contentSb.append("! U1 SETBOLD 2\r\n" + "Name             : " + "! U1 SETBOLD 0\r\n" + head + "\r\n");
        contentSb.append("----------------------------------------------" + "\r");
        contentSb.append("! U1 SETBOLD 2 \r" + " No" + "    " + "Commodity" + "    " + "Quantity" + "        " + "Price (Rs)" + "! U1 SETBOLD 0\r\n" + "\r\n");
        contentSb.append("----------------------------------------------" + "\r\n");
        contentSb.append(contentProduct.toString());

        String printContent = contentSb.toString();

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(updateStock.getBillDto().getBillDate());
        } catch (ParseException e) {
            Log.e("Error", "Date Parse Error");
        }
        dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
        refNo = updateStock.getBillDto().getTransactionId();
        billDate = dateFormat.format(convertedDate);
        List<BillItemDto> billItems = new ArrayList<BillItemDto>(updateStock.getBillDto().getBillItemDto());
        NumberFormat formatter = new DecimalFormat("#0.00");
        NumberFormat quantityFormat = new DecimalFormat("#0.000");
        int i = 1;
        for (BillItemDto bItems : billItems) {
            ProductDto product = FPSDBHelper.getInstance(context).getProductDetails(bItems.getProductId());
            productName = product.getName() + "                                 ";
            quantity = "                        " + quantityFormat.format(bItems.getQuantity()) + "(" + product.getProductUnit() + ")";
            price = "                        " + formatter.format(bItems.getCost() * bItems.getQuantity());
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
        NumberFormat formatter = new DecimalFormat("#0.00");
        ledgerAmount = "   " + formatter.format(updateStockRequestDto.getBillDto().getAmount());
    }

}
