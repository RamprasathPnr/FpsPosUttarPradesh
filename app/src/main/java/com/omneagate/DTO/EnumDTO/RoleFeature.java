package com.omneagate.DTO.EnumDTO;

import com.omneagate.activity.R;

/**
 * Created by user1 on 31/7/15.
 */
public enum RoleFeature {

    SALES_ORDER_MENU(R.string.sales_order, R.drawable.pink_bg, R.drawable.icon_sale_order, "SaleOrderActivity"),

    CARD_ACTIVATION_MENU(R.string.card_activation, R.drawable.purple_bg, R.drawable.icon_card, "CardActivationActivity"),

    STOCK_MANAGEMENT_MENU(R.string.stock_management, R.drawable.green_bg, R.drawable.icon_stocks, "StockManagementActivity"),

    TRANSACTIONS_MENU(R.string.transactions, R.drawable.orange_bg, R.drawable.icon_transaction, "TransactionsSubmenuActivity"),

    CLOSE_SALES_MENU(R.string.close_sale, R.drawable.brown_bg, R.drawable.icon_closesale, "TransactionCommodityActivity"),

    OTHER_MENUS(R.string.other_menus, R.drawable.teal_bg, R.drawable.icon_othermenu, "BeneficiaryMenuActivity"),

    SALES_ORDER(R.string.ration_based, R.drawable.red_bg, R.drawable.icon_ration_card, "RationCardSalesActivity"),

    QR_BASED(R.string.qr_based, R.drawable.green_bg, R.drawable.img_qr_code, "QRCodeSalesActivity"),

    RMN_BASED(R.string.otp_based, R.drawable.purple_bg, R.drawable.icon_otp_base, "MobileOTPOptionsActivity"),

    RATION_CARD_BASED(R.string.ration_based, R.drawable.red_bg, R.drawable.icon_ration_card, "RationCardSalesActivity"),

    AADHAAR_BASED(R.string.aadhar_based, R.drawable.red_bg, R.drawable.icon_aadhaar_biometric, "AadharCardSalesActivity"),

    BIOMETRIC_BASED(R.string.biometric_based, R.drawable.brown_bg, R.drawable.biometric_sale, "RcScanEntryActivity"),

//    AADHAAR_BASED(R.string.aadhar_based, R.drawable.red_bg, R.drawable.aadhar_scan, "AadharCardSalesActivity"),

    CARD_ACTIVATION(R.string.qrcard_registration, R.drawable.red_bg, R.drawable.icon_scan_qr, "QRActivationActivity"),

    //    FPS_CARD_ACTIVATION(R.string.card_activation,R.drawable.pink_bg, R.drawable.icon_scan_qr,"BeneficiaryCardActivationActivity"),
    FPS_CARD_REGISTRATION(R.string.card_registration, R.drawable.purple_bg, R.drawable.icon_card_reg, "CardRegistrationActivity"),

    RATION_CARD_ACTIVATION(R.string.ration_card_activation, R.drawable.red_bg, R.drawable.icon_ration_card, "BeneficiaryRationCardActivationNewActivity"),

    PROXY_REGISTRATION(R.string.proxy_activation, R.drawable.red_bg, R.drawable.proxy_registration, "RcScanEntryProxyActivity"),

    OAP_ANP_CARD_ACTIVATION(R.string.oap_anp_card_activation, R.drawable.red_bg, R.drawable.icon_ration_card, "BeneficiaryOapAnpActivationActivity"),

    CARD_REGISTRATION_REQUEST(R.string.card_activation, R.drawable.green_bg, R.drawable.icon_req_list, "CardRegistrationActivity"),

    STOCK_INWARD(R.string.stock_inward_tv, R.drawable.green_bg, R.drawable.icon_inward, "FpsStockInwardActivity"),

    STOCK_STATUS(R.string.stockCheck, R.drawable.pink_bg, R.drawable.icon_stock_check, "StockCheckActivity"),

    TRANSACTIONS(R.string.retrieve, R.drawable.purple_bg, R.drawable.icon_retrive_db, "RationCardSalesActivity"),

    RESTORE_DB(R.string.restoration, R.drawable.pink_bg, R.drawable.icon_restore_db, "restoreDB"),

    RETRIEVE_DB(R.string.retrieve, R.drawable.purple_bg, R.drawable.icon_retrive_db, "retrieveDB"),

    STATISTICS(R.string.statistics, R.drawable.brown_bg, R.drawable.icon_pos_stats, "getStatistics"),

    BENIFICIARY_VIEW(R.string.benedetails, R.drawable.red_bg, R.drawable.icon_ration_card, "BeneficiaryMenuActivity"),

    GEO_LOCATION(R.string.geolocation, R.drawable.green_bg, R.drawable.icon_lang_lat, "findLocation"),

    OPEN_STOCK(R.string.opening_stock, R.drawable.orange_bg, R.drawable.icon_opening_balance, "openStock"),

    VERSION_UPGRADE(R.string.version_upgrade, R.drawable.orange_bg, R.drawable.version_upgrade,"versionUpgrade");

    //AADHAAR_QR_BASED



    final private int rollName;

    final private int colorCode;

    final private int background;
    final private String description;

    RoleFeature(int rollName, int colorCode, int background, String description) {

        this.rollName = rollName;
        this.colorCode = colorCode;
        this.background = background;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getRollName() {
        return rollName;
    }

    public int getColorCode() {
        return colorCode;
    }

    public int getBackground() {
        return background;
    }
}
