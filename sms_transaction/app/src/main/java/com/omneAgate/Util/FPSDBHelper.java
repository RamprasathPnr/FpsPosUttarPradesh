package com.omneagate.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BeneficiaryRegistrationData;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.CardTypeDto;
import com.omneagate.DTO.ChellanProductDto;
import com.omneagate.DTO.EntitlementMasterRule;
import com.omneagate.DTO.EnumDTO.StockTransactionType;
import com.omneagate.DTO.FPSIndentRequestDto;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FpsDataDto;
import com.omneagate.DTO.FpsIntentReqProdDto;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.GodownDto;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.MessageDto;
import com.omneagate.DTO.OpeningClosingBalanceDto;
import com.omneagate.DTO.PersonBasedRule;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.ServiceProviderDto;
import com.omneagate.DTO.SplEntitlementRule;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.DTO.UserDetailDto;
import com.omneagate.Util.Constants.FPSDBConstants;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * FPS database Helper
 */
public class FPSDBHelper extends SQLiteOpenHelper {

    // All Static variables
    private static FPSDBHelper dbHelper = null;
    private static SQLiteDatabase database = null;
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "FPS.db";

    // fpsUsers table name
    private final String TABLE_USERS = "fpsUsers";


    // benefRegReq table name
    private final String TABLE_REG_REQ = "benefRegReq";

    // Products table name
    private final String TABLE_PRODUCTS = "fpsProducts";

    // Entitlement Rules table name
    private final String TABLE_ENTITLEMENT_RULES = "entitlementRules";

    // Card Images Address table name
    private final String TABLE_CARD_IMAGE = "rationImages";

    // Entitlement Rules table name
    private final String TABLE_SPECIAL_RULES = "specialRules";

    // Person Rules table name
    private final String TABLE_PERSON_RULES = "personRules";

    // Products table name
    private final String TABLE_BENEFICIARY = "fpsBeneficiary";

    // Beneficiary Member table name
    private final String TABLE_BENEFICIARY_MEMBER = "fpsBeneficiaryMember";


    // STOCK table name
    private final String TABLE_STOCK = "fpsStock";

    // STOCK table name
    private final String TABLE_REGISTRATION = "fpsRegistration";


    // STORE table name
    private final String TABLE_GO_DOWN = "fpsGodown";


    // CardType table name
    private final String TABLE_CARD_TYPE = "cardType";


    // CardType table name
    private final String TABLE_BILL_ITEM = "billItem";


    // bill table name
    private final String TABLE_BILL = "bill";


    //Lanuage Database Table for Error Message
    private final String TABLE_LANGUAGE = "errorMessages";

    private final String TABLE_STOCK_HISTORY = "stockHistory";


    //Godown Stock inward table
    private final String TABLE_FPS_STOCK_INWARD = "fpsStockInward";

    //Fps Intent Request Product Table
    private final String TABLE_FPS_INTENT_REQUEST_PRODUCT = "fpsIndentRequestProduct";


    //Fps Manual Stock inward table
    private final String TABLE_FPS_MANUAL_STOCK_INWARD = "fpsManualStockInward";

    //Fps Manual Stock Inward Product Table
    private final String TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE = "fpsManualStockInwardProduct";


    //LPG PROVIDER
    private final String TABLE_LPG_PROVIDER = "lpgProvider";

    //Key for id in tables
    public final static String KEY_ID = "_id";


    private static Context contextValue;
    // Users table with username and passwordHash
    private final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY ," + FPSDBConstants.KEY_USERS_NAME + " VARCHAR(150) NOT NULL UNIQUE,"
            + FPSDBConstants.KEY_USERS_PASS_HASH + " VARCHAR(150)," + FPSDBConstants.KEY_USERS_FPS_ID + " VARCHAR(30), contactPerson VARCHAR(30), phoneNumber VARCHAR(15), "
            + "addressLine1 VARCHAR(30), addressLine2 VARCHAR(30),addressLine3 VARCHAR(30), " + FPSDBConstants.KEY_USERS_PROFILE + " VARCHAR(150)," + FPSDBConstants.KEY_USERS_CREATE_DATE + " INTEGER," + FPSDBConstants.KEY_USERS_MODIFIED_DATE + " INTEGER, isActive INTEGER," + FPSDBConstants.KEY_USERS_CREATED_BY + " VARCHAR(150), code VARCHAR(15), " + FPSDBConstants.KEY_USERS_MODIFIED_BY + " VARCHAR(150)" + " )";

    // Products  table with unique product name and unique product code
    private final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_PRODUCT_NAME + " VARCHAR(150) NOT NULL UNIQUE,"
            + FPSDBConstants.KEY_PRODUCT_PRICE + " DOUBLE NOT NULL," + FPSDBConstants.KEY_PRODUCT_UNIT + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_PRODUCT_CODE + " VARCHAR(150) NOT NULL UNIQUE," + FPSDBConstants.KEY_LPRODUCT_UNIT + " VARCHAR(150) NOT NULL," + FPSDBConstants.KEY_LPRODUCT_NAME + " VARCHAR(250) NOT NULL UNIQUE," + FPSDBConstants.KEY_NEGATIVE_INDICATOR + " INTEGER NOT NULL," + FPSDBConstants.KEY_PRODUCT_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_MODIFIED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_CREATED_BY + " VARCHAR(150)" + ")";


    // Entitlement Master  table
    private final String CREATE_ENTITLEMENT_RULES_TABLE = "CREATE TABLE " + TABLE_ENTITLEMENT_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_RULES_CARD_TYPE + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_IS_PERSON + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_IS_CALC + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_RULES_IS_REGION + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_HAS_SPECIAL + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_QUANTITY + " INTEGER NOT NULL)";

    // Entitlement Master  table
    private final String CREATE_SPECIAL_RULES_TABLE = "CREATE TABLE " + TABLE_SPECIAL_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_SPECIAL_DISTRICT + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_TALUK + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_VILLAGE + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_SPECIAL_ISMUNICIPALITY + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_ISADD + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_ISCITY_HEAD + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_SPECIAL_QUANTITY + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_SPECIAL_CYLINDER + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_ISTALUK + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_ISCITY + " INTEGER NOT NULL)";


    // Entitlement Master  table
    private final String CREATE_PERSON_RULES_TABLE = "CREATE TABLE " + TABLE_PERSON_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NULL,"
            + FPSDBConstants.KEY_PERSON_MIN + " DOUBLE NOT NULL," + FPSDBConstants.KEY_PERSON_MAX + " DOUBLE NOT NULL," + FPSDBConstants.KEY_PERSON_CHILD + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_ADULT + " DOUBLE NOT NULL)";


    /*// Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    private final String CREATE_BENEFICIARY_TABLE = "CREATE TABLE " + TABLE_BENEFICIARY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(50) NOT NULL UNIQUE,"

            + FPSDBConstants.KEY_BENEFICIARY_TIN + " VARCHAR(150) NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_FPS_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " VARCHAR(20) UNIQUE ," + FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_TALUK_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID + " VARCHAR(10)," + FPSDBConstants.KEY_BENEFICIARY_STATE_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " INTEGER NOT NULL" + ")";*/
// Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    private final String CREATE_BENEFICIARY_TABLE = "CREATE TABLE " + TABLE_BENEFICIARY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(50),"
            + FPSDBConstants.KEY_BENEFICIARY_TIN + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_FPS_ID + " INTEGER NOT NULL,"
            + "oldRationNumber VARCHAR(150),numOfCylinder INTEGER NOT NULL,numOfAdults INTEGER NOT NULL,numOfChild INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " VARCHAR(20) UNIQUE ," + FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_TALUK_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID + " VARCHAR(10)," + FPSDBConstants.KEY_BENEFICIARY_STATE_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " INTEGER NOT NULL" + ")";

    // Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    private final String CREATE_BENEFICIARY_REQ_TABLE = "CREATE TABLE " + TABLE_REG_REQ + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + "oldRationCardNum VARCHAR(150),numOfCylinder INTEGER NOT NULL,numOfAdults INTEGER NOT NULL,numOfChild INTEGER NOT NULL,"
            + "mobNum VARCHAR(20) UNIQUE ,fpsId INTEGER,"
            + "channel VARCHAR(10),cardType VARCHAR(5)" + ")";

    // Beneficiary MEMBER
    private final String CREATE_BENEFICIARY_MEMBER_TABLE = "CREATE TABLE " + TABLE_BENEFICIARY_MEMBER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN + " VARCHAR(150) NOT NULL, " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME + " VARCHAR(100),"
            + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(100) ,"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FIRST_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MIDDLE_NAME + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LAST_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NAME + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER + " VARCHAR(1)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PERMANENT_ADDRESS + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TEMP_ADDRESS + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY + " VARCHAR(150), " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_BY + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_RESIDENT_ID + " VARCHAR(1),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID + " VARCHAR(1)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB + " VARCHAR(30) ,"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB_TYPE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID + " VARCHAR(1)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5 + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE + " VARCHAR(150),"

            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + " INTEGER ," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT + " INTEGER " + ")";


    // card type table with card types
    private final String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD_TYPE + "(" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + FPSDBConstants.KEY_CARD_TYPE + " VARCHAR(1) NOT NULL UNIQUE," + FPSDBConstants.KEY_CARD_DESCRIPTION + " VARCHAR(150)  UNIQUE" + " )";


    // Stock  table with unique bill item id, Quantity
    private final String CREATE_STOCK_TABLE = "CREATE TABLE " + TABLE_STOCK + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_STOCK_FPS_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_STOCK_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + FPSDBConstants.KEY_STOCK_QUANTITY + " DOUBLE NOT NULL, "
            + FPSDBConstants.KEY_STOCK_REORDER_LEVEL + " DOUBLE, " + FPSDBConstants.KEY_STOCK_EMAIL_ACTION + " INTEGER," + FPSDBConstants.KEY_STOCK_SMSACTION + " INTEGER " + ")";


    // REGISTRATION  table with unique bill item id, Quantity
    private final String CREATE_REGISTRATION_TABLE = "CREATE TABLE " + TABLE_REGISTRATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_REGISTRATION_DESC + " VARCHAR(150),"
            + FPSDBConstants.KEY_REGISTRATION_STATUS + " VARCHAR(1)," + FPSDBConstants.KEY_REGISTRATION_CARD_NO + " VARCHAR(30) NOT NULL, "
            + FPSDBConstants.KEY_REGISTRATION_RMN + " VARCHAR(30))";


    private final String CREATE_FPS_STOCK_INVARD_TABLE = "CREATE TABLE " + TABLE_FPS_STOCK_INWARD + "("
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY + " DOUBLE NOT NULL, " + FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT + " VARCHAR(150)," + FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS + " INTEGER(1)," +
            FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY + " DOUBLE ," + FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID + " INTEGER" + ")";


    private final String CREATE_FPS_MANUAL_STOCK_INVARD_TABLE = "CREATE TABLE " + TABLE_FPS_MANUAL_STOCK_INWARD + "(" + KEY_ID + "  INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS + " INTEGER,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE + " INTEGER, status VARCHAR(2)," + FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_COUNT + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_TIME + " INTEGER" + ")";

    private final String CREATE_FPS_MANUAL_STOCK_PRODUCT_TABLE = "CREATE TABLE " + TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE + "("
            + KEY_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL,"
            + "refId INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY + " DOUBLE NOT NULL, " + FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT + " VARCHAR(150),"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY + " DOUBLE ," + FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY + " INTEGER" + ")";

    // GoDown table with unique goDown_code
    private final String CREATE_GO_DOWN_TABLE = "CREATE TABLE " + TABLE_GO_DOWN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_GO_DOWN_GO_DOWN_CODE + " VARCHAR(150) UNIQUE," + FPSDBConstants.KEY_GO_DOWN_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_DISTRICT + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_TALUK + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_CATEGORY_ID + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_CONTACT_PERSON_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_CONTACT_NUMBER + " VARCHAR(150),"
            + FPSDBConstants.KEY_GO_DOWN_PIN_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_ADDRESS + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_IS_STATUS + " INTEGER," + FPSDBConstants.KEY_GO_DOWN_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_GO_DOWN_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_GO_DOWN_CREATED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_GO_DOWN_MODIFIED_BY + " VARCHAR(150)" + ")";


    // Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    private final String CREATE_BILL_TABLE = "CREATE TABLE " + TABLE_BILL + "("
            + FPSDBConstants.KEY_BILL_REF_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_BILL_SERVER_ID + " INTEGER UNIQUE," + FPSDBConstants.KEY_BILL_FPS_ID + " INTEGER ,"
            + FPSDBConstants.KEY_BILL_DATE + " INTEGER NOT NULL,transactionId VARCHAR(150) NOT NULL UNIQUE," + FPSDBConstants.KEY_BILL_SERVER_REF_ID + " INTEGER UNIQUE," + FPSDBConstants.KEY_BILL_MODE + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(100),"
            + FPSDBConstants.KEY_BILL_CHANNEL + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BILL_STATUS + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BILL_BENEFICIARY + " INTEGER,"
            + FPSDBConstants.KEY_BILL_AMOUNT + " DOUBLE NOT NULL," + FPSDBConstants.KEY_BILL_TIME_MONTH + " INTEGER," + FPSDBConstants.KEY_BILL_CREATED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_BILL_CREATED_DATE + " INTEGER " + ")";

    // Bill item  table with unique bill item id, Quantity ,bill item cost  and transmitted ir not
    private final String CREATE_BILL_ITEM_TABLE = "CREATE TABLE " + TABLE_BILL_ITEM + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + FPSDBConstants.KEY_BILL_ITEM_BILL_REF + " INTEGER NOT NULL," + FPSDBConstants.KEY_BILL_ITEM_DATE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + " INTEGER NOT NULL,transactionId VARCHAR(150) NOT NULL," + FPSDBConstants.KEY_BILL_TIME_MONTH + " INTEGER ," + FPSDBConstants.KEY_BILL_BENEFICIARY + " INTEGER NOT NULL," + FPSDBConstants.KEY_BILL_ITEM_QUANTITY + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_BILL_ITEM_COST + "  DOUBLE NOT NULL" + ")";

    private final String CREATE_TABLE_LANGUAGE = "CREATE TABLE " + TABLE_LANGUAGE + "("
            + KEY_ID + " INTEGER  PRIMARY KEY," + FPSDBConstants.KEY_LANGUAGE_CODE + " INTEGER,"
            + FPSDBConstants.KEY_LANGUAGE_ID + "  VARCHAR(30)," + FPSDBConstants.KEY_LANGUAGE_MESSAGE + " VARCHAR(1000)"
            + ")";

    private final String CREATE_TABLE_CARD_IMAGES = "CREATE TABLE " + TABLE_CARD_IMAGE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,cardNumber VARCHAR(50) UNIQUE,imageId VARCHAR(100),status VARCHAR(2))";


    private final String CREATE_TABLE_FPS_INTENT_REQUEST_PRODUCT = "CREATE TABLE " + TABLE_FPS_INTENT_REQUEST_PRODUCT + "("
            + KEY_ID + " INTEGER  PRIMARY KEY  AUTOINCREMENT NOT NULL," + FPSDBConstants.KEY_FPS_INTENT_REQUEST_GODOWN_ID + " INTEGER,"
            + FPSDBConstants.KEY_FPS_INTENT_REQUEST_FPS_ID + "  INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_INTENT_REQUEST_PRODUCT_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_FPS_INTENT_REQUEST_QUANTITY + " DOUBLE," + FPSDBConstants.KEY_FPS_INTENT_REQUEST_TALUK_OFFICER_APPROVAL + " INTEGER,"
            + FPSDBConstants.KEY_FPS_INTENT_REQUEST_DATE_OF_APPROVAL + " INTEGER," + FPSDBConstants.KEY_FPS_INTENT_REQUEST_MODIFIED_QUANTITY + " DOUBLE,"
            + FPSDBConstants.KEY_FPS_INTENT_REQUEST_STATUS + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_INTENT_REQUEST_REASON + " VARCHAR(150),"
            + FPSDBConstants.KEY_FPS_INTENT_REQUEST_DESCRIPTION + " VARCHAR(500),requestId INTEGER )";


    private final String CREATE_TABLE_FPS_STOCK_HISTORY = "CREATE TABLE " + TABLE_STOCK_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + FPSDBConstants.KEY_STOCK_HISTORY_PRODUCT_ID + " INTEGER," + FPSDBConstants.KEY_STOCK_HISTORY_DATE + " INTEGER,"
            + FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE + " DOUBLE," + FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE + " DOUBLE," + FPSDBConstants.KEY_STOCK_HISTORY_CHANGE_BALANCE + " DOUBLE,"
            + FPSDBConstants.KEY_STOCK_HISTORY_ACTION + " VARCHAR(100)" + ")";


    private final String CREATE_TABLE_LPG_PROVIDER = "CREATE TABLE " + TABLE_LPG_PROVIDER + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + FPSDBConstants.KEY_LPG_PROVIDER_NAME + " VARCHAR(150) UNIQUE," + FPSDBConstants.KEY_LPG_PROVIDER_CREATEDBY + " INTEGER,"
            + FPSDBConstants.KEY_LPG_PROVIDER_CREATEDDATE + " INTEGER," + FPSDBConstants.KEY_LPG_PROVIDER_MODIFIEDDATE + " INTEGER,"
            + FPSDBConstants.KEY_LPG_PROVIDER_STATUS + " INTEGER" + ")";

    private FPSDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this.getWritableDatabase();
        dbHelper = this;
    }


    //Singleton to Instantiate the SQLiteOpenHelper
    public static FPSDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new FPSDBHelper(context);
            openConnection();
        }
        contextValue = context;
        return dbHelper;
    }

    // It is used to open database
    private static void openConnection() {
        if (database == null) {
            database = dbHelper.getWritableDatabase();

        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        Log.e("Inside DB", "DB Creation");
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_PRODUCTS_TABLE);
        db.execSQL(CREATE_BENEFICIARY_TABLE);
        db.execSQL(CREATE_BENEFICIARY_MEMBER_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
        db.execSQL(CREATE_BILL_ITEM_TABLE);
        db.execSQL(CREATE_TABLE_CARD_IMAGES);
        db.execSQL(CREATE_STOCK_TABLE);
        db.execSQL(CREATE_REGISTRATION_TABLE);
        db.execSQL(CREATE_ENTITLEMENT_RULES_TABLE);
        db.execSQL(CREATE_PERSON_RULES_TABLE);
        db.execSQL(CREATE_GO_DOWN_TABLE);
        db.execSQL(CREATE_BILL_TABLE);
        db.execSQL(CREATE_SPECIAL_RULES_TABLE);
        db.execSQL(CREATE_TABLE_LANGUAGE);
        db.execSQL(CREATE_BENEFICIARY_REQ_TABLE);
        db.execSQL(CREATE_FPS_STOCK_INVARD_TABLE);
        db.execSQL(CREATE_TABLE_FPS_INTENT_REQUEST_PRODUCT);
        db.execSQL(CREATE_TABLE_FPS_STOCK_HISTORY);
        db.execSQL(CREATE_FPS_MANUAL_STOCK_INVARD_TABLE);
        db.execSQL(CREATE_FPS_MANUAL_STOCK_PRODUCT_TABLE);
        db.execSQL(CREATE_TABLE_LPG_PROVIDER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BENEFICIARY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BENEFICIARY_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GO_DOWN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIAL_RULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARD_IMAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REG_REQ);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTITLEMENT_RULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON_RULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LANGUAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FPS_STOCK_INWARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FPS_INTENT_REQUEST_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCK_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FPS_MANUAL_STOCK_INWARD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LPG_PROVIDER);
        onCreate(db);
    }

    //insert login data inside database
    public void insertData(LoginResponseDto loginResponse) {
        Log.e("FPS", loginResponse.getUserDetailDto().getFpsStore().toString());
        ContentValues values = new ContentValues();
        values.put(KEY_ID, loginResponse.getUserDetailDto().getId());
        values.put(FPSDBConstants.KEY_USERS_NAME, loginResponse.getUserDetailDto().getUserId().toLowerCase());
        values.put(FPSDBConstants.KEY_USERS_PASS_HASH, loginResponse.getUserDetailDto().getPassword());
        values.put(FPSDBConstants.KEY_USERS_PROFILE, loginResponse.getUserDetailDto().getProfile());
        values.put(FPSDBConstants.KEY_USERS_FPS_ID, loginResponse.getUserDetailDto().getFpsStore().getId());
        values.put("code", loginResponse.getUserDetailDto().getFpsStore().getCode());
        values.put("contactPerson", loginResponse.getUserDetailDto().getFpsStore().getContactPerson());
        values.put("phoneNumber", loginResponse.getUserDetailDto().getFpsStore().getPhoneNumber());
        values.put("addressLine1", loginResponse.getUserDetailDto().getFpsStore().getAddressLine1());
        values.put("addressLine2", loginResponse.getUserDetailDto().getFpsStore().getAddressLine2());
        values.put("addressLine3", loginResponse.getUserDetailDto().getFpsStore().getAddressLine3());
        values.put("code", loginResponse.getUserDetailDto().getFpsStore().getCode());
        int status = 0;
        if (loginResponse.getUserDetailDto().getFpsStore().isActive()) {
            status = 1;
        }
        values.put("isActive", status);
        database.insertWithOnConflict(TABLE_USERS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

    }


    // Used to retrieve data when no network available in device
    public LoginResponseDto retrieveData(String userName) {
        String selectQuery = "SELECT  * FROM " + TABLE_USERS + " where " + FPSDBConstants.KEY_USERS_NAME + " = '" + userName.toLowerCase() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        LoginResponseDto loginResponse;
        if (cursor.moveToFirst()) {
            loginResponse = new LoginResponseDto(cursor);
            loginResponse.setUserDetailDto(new UserDetailDto(cursor));
            Log.e("Error", loginResponse.toString());
            return loginResponse;
        }
        cursor.close();
        return null;
    }


    // Used to retrieve data when no network available in device
    public List<FpsStoreDto> retrieveDataStore() {
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;
        List<FpsStoreDto> data = new ArrayList<FpsStoreDto>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            FpsStoreDto loginResponse = new FpsStoreDto(cursor);
            data.add(loginResponse);
            Log.e("Error", loginResponse.toString());
        }
        cursor.close();
        return data;
    }


    //Get Product data
    public List<EntitlementMasterRule> getAllEntitlementMasterRule(long cardType) {
        String selectQuery = "SELECT  * FROM " + TABLE_ENTITLEMENT_RULES + " where " + FPSDBConstants.KEY_RULES_CARD_TYPE + " = " + cardType;
        List<EntitlementMasterRule> products = new ArrayList<EntitlementMasterRule>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            products.add(new EntitlementMasterRule(cursor));
            cursor.moveToNext();
        }
        return products;
    }


    public long insertStockInward(StockRequestDto stock) {
        try {

            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, stock.getGodownId());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, stock.getFpsId());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, stock.getDate());
            values.put("status", "R");
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY, stock.getCreatedBy());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_COUNT, 0);
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_TIME, System.currentTimeMillis());

            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.insert(TABLE_FPS_MANUAL_STOCK_INWARD, null, values);
            String selectQuery = "SELECT " + KEY_ID + " FROM " + TABLE_FPS_MANUAL_STOCK_INWARD + " order by " + KEY_ID + " DESC limit 1";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(KEY_ID));
            insertManualInwardProduct(id, stock);
            return id;
        } catch (Exception e) {
            Log.e("Excep", e.toString(), e);
            return 0;
        }

    }


    private long insertManualInwardProduct(long id, StockRequestDto stock) {
        ContentValues values = new ContentValues();
        for (StockRequestDto.ProductList products : stock.getProductLists()) {
            values.put("refId", id);
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, products.getId());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, products.getQuantity());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, products.getRecvQuantity());
            database.insert(TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE, null, values);
            FPSStockDto stockList = getAllProductStockDetails(products.getId());
            double closing = stockList.getQuantity() + products.getRecvQuantity();
            insertStockHistory(stockList.getQuantity(), closing, "MANUAL INWARD", products.getRecvQuantity(), products.getId());
            stockList.setQuantity(closing);
            stockUpdate(stockList);


        }
        return id;
    }

    //Update Stock Inward
    public void updateStockInward(long inwardId, int retryCount) {

        ContentValues values = new ContentValues();
        values.put("status", "T");
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_COUNT, retryCount);
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_TIME, System.currentTimeMillis());

        database.update(TABLE_FPS_MANUAL_STOCK_INWARD, values, KEY_ID + "=" + inwardId, null);
        Log.e("iupdate", "" + inwardId);

    }


    // Used to retrieve beneficiary details
    public String retrieveDataFromBeneficiary(long userName) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + KEY_ID + "=" + userName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        BeneficiaryDto beneficiaryDto;
        if (cursor.moveToFirst()) {
            beneficiaryDto = new BeneficiaryDto(cursor);
            return beneficiaryDto.getEncryptedUfc();
        }
        cursor.close();
        return "";
    }

    //Insert into Stock history
    public void insertStockHistory(double openingBalance, double closingBalance, String action, double changeInBalance, long productId) {
        ContentValues values = new ContentValues();
        try {
            NumberFormat formatter = new DecimalFormat("#0.000");
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE, Double.parseDouble(formatter.format(openingBalance)));
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE, Double.parseDouble(formatter.format(closingBalance)));
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_ACTION, action);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_CHANGE_BALANCE, changeInBalance);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_PRODUCT_ID, productId);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_DATE, new Date().getTime());
            database.insert(TABLE_STOCK_HISTORY, null, values);
        } catch (Exception e) {
            Log.e("Error in SH", e.toString(), e);
        }
    }

    //Insert into Stock history
    public boolean insertRegistration(String phoneNumber, String cardNumber) {
        boolean status = true;
        ContentValues values = new ContentValues();
        try {
            values.put(FPSDBConstants.KEY_REGISTRATION_CARD_NO, cardNumber.toUpperCase());
            values.put(FPSDBConstants.KEY_REGISTRATION_STATUS, "R");
            values.put(FPSDBConstants.KEY_REGISTRATION_RMN, phoneNumber);
            database.insert(TABLE_REGISTRATION, null, values);
        } catch (Exception e) {
            status = false;
            Log.e("Error in SH", e.toString(), e);
        }
        return status;
    }

    //Insert into Stock history
    public void updateRegistration(String cardNumber, String status, String description) {
        ContentValues values = new ContentValues();
        try {
            values.put(FPSDBConstants.KEY_REGISTRATION_STATUS, status);
            values.put(FPSDBConstants.KEY_REGISTRATION_DESC, description);
            database.update(TABLE_REGISTRATION, values, FPSDBConstants.KEY_REGISTRATION_CARD_NO + "='" + cardNumber.toUpperCase() + "'", null);
        } catch (Exception e) {
            Log.e("Error in SH", e.toString(), e);
        }
    }

    public BenefActivNewDto getCardDetails(String cardNo) {
        String selectQuery = "SELECT  * FROM " + TABLE_REGISTRATION + " where " + FPSDBConstants.KEY_REGISTRATION_CARD_NO + " = '" + cardNo + "'";
        BenefActivNewDto benficiary = new BenefActivNewDto();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            benficiary = new BenefActivNewDto(cursor);
        }
        return benficiary;
    }

    public boolean deleteCardDetails(String cardNo) {
        database.delete(TABLE_REGISTRATION, FPSDBConstants.KEY_REGISTRATION_CARD_NO + " ='" + cardNo + "'", null);
        return true;
    }

    //Get Product data
    public PersonBasedRule getAllPersonBasedRule(long cardType) {
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON_RULES + " where " + FPSDBConstants.KEY_RULES_PRODUCT_ID + " = " + cardType;
        PersonBasedRule products = new PersonBasedRule();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            products = new PersonBasedRule(cursor);
        }
        return products;
    }


    //Insert into Person Rules
    public void insertPersonRules(Set<PersonBasedRule> masterRules) {

        try {
            List<PersonBasedRule> rules = new ArrayList<PersonBasedRule>(masterRules);
            for (PersonBasedRule personBasedRule : rules) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, personBasedRule.getId());
                values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, personBasedRule.getProductDto().getId());
                NumberFormat formatter = new DecimalFormat("#0.000");
                values.put(FPSDBConstants.KEY_PERSON_MIN, formatter.format(personBasedRule.getMin()));
                values.put(FPSDBConstants.KEY_PERSON_MAX, formatter.format(personBasedRule.getMax()));
                values.put(FPSDBConstants.KEY_PERSON_CHILD, formatter.format(personBasedRule.getPerChild()));
                values.put(FPSDBConstants.KEY_PERSON_ADULT, formatter.format(personBasedRule.getPerAdult()));
                database.insertWithOnConflict(TABLE_PERSON_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

            }
        } catch (Exception e) {
            Log.e("Error in SH", e.toString(), e);
        }
    }

    //Insert into Master Rules
    public void insertMasterRules(Set<EntitlementMasterRule> masterRules) {
        try {
            List<EntitlementMasterRule> rules = new ArrayList<EntitlementMasterRule>(masterRules);
            for (EntitlementMasterRule entitlementMasterRule : rules) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, entitlementMasterRule.getId());
                values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, entitlementMasterRule.getProductDto().getId());
                values.put(FPSDBConstants.KEY_RULES_CARD_TYPE, entitlementMasterRule.getCardTypeDto().getId());
                NumberFormat formatter = new DecimalFormat("#0.000");
                values.put(FPSDBConstants.KEY_RULES_IS_CALC, returnInteger(entitlementMasterRule.isCalcRequired()));
                values.put(FPSDBConstants.KEY_RULES_IS_PERSON, returnInteger(entitlementMasterRule.isPersonBased()));
                values.put(FPSDBConstants.KEY_RULES_IS_REGION, returnInteger(entitlementMasterRule.isRegionBased()));
                values.put(FPSDBConstants.KEY_RULES_HAS_SPECIAL, returnInteger(entitlementMasterRule.isHasSpecialRule()));
                values.put(FPSDBConstants.KEY_RULES_QUANTITY, formatter.format(entitlementMasterRule.getQuantity()));
                database.insertWithOnConflict(TABLE_ENTITLEMENT_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            Log.e("Error in SH", e.toString(), e);
        }
    }

    //Insert into Master Rules
    public void insertSpecialRules(Set<SplEntitlementRule> masterRules) {
        try {
            List<SplEntitlementRule> rules = new ArrayList<SplEntitlementRule>(masterRules);
            for (SplEntitlementRule entitlementMasterRule : rules) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, entitlementMasterRule.getId());
                values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, entitlementMasterRule.getProductDto().getId());
                values.put(FPSDBConstants.KEY_SPECIAL_DISTRICT, entitlementMasterRule.getDistrictId());
                values.put(FPSDBConstants.KEY_SPECIAL_TALUK, entitlementMasterRule.getTalukId());
                values.put(FPSDBConstants.KEY_SPECIAL_VILLAGE, entitlementMasterRule.getVillageId());
                NumberFormat formatter = new DecimalFormat("#0.000");
                values.put(FPSDBConstants.KEY_SPECIAL_ISMUNICIPALITY, returnInteger(entitlementMasterRule.isMunicipality()));
                values.put(FPSDBConstants.KEY_SPECIAL_ISADD, returnInteger(entitlementMasterRule.isAdd()));
                values.put(FPSDBConstants.KEY_SPECIAL_ISCITY_HEAD, returnInteger(entitlementMasterRule.isCityHeadQuarter()));
                values.put(FPSDBConstants.KEY_SPECIAL_ISCITY, returnInteger(entitlementMasterRule.isCity()));
                values.put(FPSDBConstants.KEY_SPECIAL_ISTALUK, returnInteger(entitlementMasterRule.isTaluk()));
                values.put(FPSDBConstants.KEY_SPECIAL_CYLINDER, entitlementMasterRule.getCylinderCount());
                values.put(FPSDBConstants.KEY_SPECIAL_QUANTITY, formatter.format(entitlementMasterRule.getQuantity()));
                database.insertWithOnConflict(TABLE_SPECIAL_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            Log.e("Error in SH", e.toString(), e);
        }
    }

    private int returnInteger(boolean value) {
        if (value)
            return 1;
        return 0;
    }


    //Get Beneficiary data by QR Code
    public List<PersonBasedRule> personRulesDetails() {
        List<PersonBasedRule> masterRulesDto = new ArrayList<PersonBasedRule>();
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON_RULES;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            PersonBasedRule masterRules = new PersonBasedRule(cursor);
            masterRulesDto.add(masterRules);
            cursor.moveToNext();
        }
        return masterRulesDto;
    }


    // Used to BeneficiaryDto beneficiary details
    public BeneficiaryDto retrieveBeneficiary(long id) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + KEY_ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        BeneficiaryDto beneficiaryDto;
        if (cursor.moveToFirst()) {
            beneficiaryDto = new BeneficiaryDto(cursor);
            return beneficiaryDto;
        }
        cursor.close();
        return null;
    }

    //Get Beneficiary data by QR Code
    public BeneficiaryDto beneficiaryDetails(String qrCode) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where oldRationNumber ='" + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            return beneficiary;
        }
    }

    //Get Beneficiary data by QR Code
    public BeneficiaryDto beneficiaryDetailsByCode(String qrCode) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return new BeneficiaryDto();
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            return beneficiary;
        }
    }

    //Get Beneficiary data by QR Code
    public List<BenefActivNewDto> allBeneficiaryDetails() {
        String selectQuery = "SELECT  * FROM " + TABLE_REGISTRATION + " where " + FPSDBConstants.KEY_REGISTRATION_STATUS + " = 'S'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BenefActivNewDto> beneficiary = new ArrayList<BenefActivNewDto>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BenefActivNewDto beneficiaryDto = new BenefActivNewDto(cursor);
            beneficiaryDto.setActivationType("PENDING_REGISTRATION");
            beneficiary.add(beneficiaryDto);
            cursor.moveToNext();
        }
        return beneficiary;


    } //Get Beneficiary data by QR Code

    public List<BenefActivNewDto> allBeneficiaryDetailsPending() {
        String selectQuery = "SELECT  * FROM " + TABLE_REG_REQ;
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BenefActivNewDto> beneficiary = new ArrayList<BenefActivNewDto>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BenefActivNewDto beneficiaryDto = new BenefActivNewDto();
            beneficiaryDto.setFpsId(cursor.getLong(cursor.getColumnIndex("fpsId")) + "");
            beneficiaryDto.setRationCardNumber(cursor.getString(cursor.getColumnIndex("oldRationCardNum")));
            beneficiaryDto.setNumOfCylinder(cursor.getInt(cursor.getColumnIndex("numOfCylinder")));
            beneficiaryDto.setNumOfAdults(cursor.getInt(cursor.getColumnIndex("numOfAdults")));
            beneficiaryDto.setNumOfChild(cursor.getInt(cursor.getColumnIndex("numOfChild")));
            beneficiaryDto.setMobileNum(cursor.getString(cursor.getColumnIndex("mobNum")));
            beneficiaryDto.setCardType(cursor.getString(cursor.getColumnIndex("cardType")).charAt(0));

            beneficiaryDto.setActivationType("PENDING_ACTIVATION");
            beneficiary.add(beneficiaryDto);
            cursor.moveToNext();
        }
        return beneficiary;
    }
/*
//Get Beneficiary data by QR Code
    public BeneficiaryDto beneficiaryDetails(String qrCode) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            return beneficiary;
        }
    }
*/


    //Get Beneficiary data by QR Code
    public List<BillItemDto> billItemsDetails(long qrCode, int month, long productId) {
        List<BillItemDto> billItems = new ArrayList<BillItemDto>();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL_ITEM + " where " + FPSDBConstants.KEY_BILL_TIME_MONTH + " = " + month + " AND " + FPSDBConstants.KEY_BILL_BENEFICIARY + "=" + qrCode + " AND productId=" + productId + " order by transactionId";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemDto billItemDto = new BillItemDto(cursor);
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        return billItems;
    }

    //This function inserts details to TABLE_BILL,;

    private void insertBillData(Set<BillDto> billDto) {
        try {
            List<BillDto> billList = new ArrayList<BillDto>(billDto);
            for (BillDto bill : billList) {
                ContentValues values = new ContentValues();
                values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
                values.put(FPSDBConstants.KEY_BILL_SERVER_REF_ID, bill.getBillRefId());
                values.put(FPSDBConstants.KEY_BILL_FPS_ID, bill.getFpsId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, bill.getUfc());
                values.put(FPSDBConstants.KEY_BILL_DATE, bill.getBillDate());
                values.put(FPSDBConstants.KEY_BILL_CREATED_BY, bill.getCreatedby());
                values.put("transactionId", bill.getTransactionId());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                Date convertedDate = dateFormat.parse(bill.getBillDate());
                Date currentDate = new Date();
                if (currentDate.getYear() == convertedDate.getYear()) {
                    values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, convertedDate.getMonth() + 1);
                }
                values.put(FPSDBConstants.KEY_BILL_AMOUNT, bill.getAmount());
                values.put(FPSDBConstants.KEY_BILL_MODE, String.valueOf(bill.getMode()));
                values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
                values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, bill.getBeneficiaryId());
                values.put(FPSDBConstants.KEY_BILL_CREATED_DATE, bill.getCreatedDate());
                values.put(FPSDBConstants.KEY_BILL_STATUS, "T");
                database.insertWithOnConflict(TABLE_BILL, "transactionId", values, SQLiteDatabase.CONFLICT_REPLACE);
                String selectQuery = "SELECT  * FROM " + TABLE_BILL + " order by " + FPSDBConstants.KEY_BILL_REF_ID + " DESC limit 1";
                Cursor cursor = database.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                BillDto billNew = new BillDto(cursor);
                insertBillItems(bill.getBillItemDto(), billNew.getBillLocalRefId(), billNew.getBillMonth(), billNew.getBeneficiaryId(), bill.getBillDate(), bill.getTransactionId());
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    public void setSyncData(FpsDataDto fpsDataDto) {

        Log.e("ResLPG", fpsDataDto.toString());
        if (fpsDataDto.getBillDto() != null) {
            insertBillData(fpsDataDto.getBillDto());
            Log.i("Resp", "Bill" + fpsDataDto.getBillDto().toString());
        }
        if (fpsDataDto.getBeneficiaryDto() != null) {
            insertBeneficiaryData(fpsDataDto.getBeneficiaryDto());
            Log.i("Resp", "Beneficiary " + fpsDataDto.getBeneficiaryDto().toString());
        }
        if (fpsDataDto.getProductDto() != null) {
            Log.i("Resp", "Product" + fpsDataDto.getProductDto().toString());
            insertProductData(fpsDataDto.getProductDto());

        }
        if (fpsDataDto.getCardtypeDto() != null) {
            insertCardTypeData(fpsDataDto.getCardtypeDto());
            Log.i("CT", "Card Type" + fpsDataDto.getCardtypeDto().toString());
        }

        if (fpsDataDto.getBenefRegReqDto() != null) {
            insertRegistrationRequestData(fpsDataDto.getBenefRegReqDto());
            Log.i("BR RT", "Registration" + fpsDataDto.getBenefRegReqDto().toString());
        }

        if (fpsDataDto.getFpsStockDto() != null) {
            insertFpsStockData(fpsDataDto.getFpsStockDto());
            Log.i("RES", "FPS STOCK" + fpsDataDto.getFpsStockDto().toString());
        }

        if (fpsDataDto.getFpsStoInwardDto() != null) {
            insertFpsStockInwardDetails(fpsDataDto.getFpsStoInwardDto());
            Log.i("RES", "FPSIN" + fpsDataDto.getFpsStoInwardDto().toString());

        }
        if (fpsDataDto.getUserdetailDto() != null) {
            insertUserDetailData(fpsDataDto.getUserdetailDto());
            Log.i("User Resp", fpsDataDto.getUserdetailDto().toString());
        }

        if (fpsDataDto.getEntitlementMasterRulesDto() != null) {
            insertMasterRules(fpsDataDto.getEntitlementMasterRulesDto());
            Log.i("User Resp", fpsDataDto.getEntitlementMasterRulesDto().toString());
        }
        if (fpsDataDto.getSplEntitlementRulesDto() != null) {
            insertSpecialRules(fpsDataDto.getSplEntitlementRulesDto());
            Log.i("User Resp", fpsDataDto.getSplEntitlementRulesDto().toString());
        }

        if (fpsDataDto.getPersonBasedRulesDto() != null) {
            insertPersonRules(fpsDataDto.getPersonBasedRulesDto());
            Log.i("User Resp", fpsDataDto.getPersonBasedRulesDto().toString());

        }
        if (fpsDataDto.getServiceProviderDto() != null) {
            insertLpgProviderDetails(fpsDataDto.getServiceProviderDto());
            Log.e("LPG", fpsDataDto.getServiceProviderDto().toString());
        }
    }
    //This function inserts details to TABLE_BILL;

    public boolean insertBill(BillDto bill) {
        try {
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
            values.put(FPSDBConstants.KEY_BILL_FPS_ID, bill.getFpsId());
            values.put(FPSDBConstants.KEY_BILL_CREATED_BY, bill.getCreatedby());
            values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, bill.getUfc());
            values.put(FPSDBConstants.KEY_BILL_AMOUNT, bill.getAmount());
            values.put(FPSDBConstants.KEY_BILL_MODE, String.valueOf(bill.getMode()));
            values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
            SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            values.put(FPSDBConstants.KEY_BILL_DATE, billDate.format(new Date()));
            values.put(FPSDBConstants.KEY_BILL_CREATED_DATE, new Date().getTime());
            values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, new DateTime().getMonthOfYear());
            values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, bill.getBeneficiaryId());
            values.put("transactionId", bill.getTransactionId());
            values.put(FPSDBConstants.KEY_BILL_STATUS, "R");
            values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, bill.getUfc());
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.insertWithOnConflict(TABLE_BILL, "transactionId", values, SQLiteDatabase.CONFLICT_REPLACE);
            String selectQuery = "SELECT  * FROM " + TABLE_BILL + " order by " + FPSDBConstants.KEY_BILL_REF_ID + " DESC limit 1";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            BillDto billNew = new BillDto(cursor);
            insertBillItems(bill.getBillItemDto(), billNew.getBillLocalRefId(), billNew.getBillMonth(), billNew.getBeneficiaryId(), bill.getBillDate(), bill.getTransactionId());
            cursor.close();
            return true;
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "Error", e.toString());
            Log.e("Error", e.toString(), e);
            return false;
        }

    }

    //Get Beneficiary data by QR Code
    public BillDto lastGenBill() {
        String selectQuery = "SELECT  * FROM " + TABLE_BILL + " order by refId DESC limit 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else {
            BillDto beneficiary = new BillDto(cursor);
            return beneficiary;
        }
    }

    //Update the bill
    public void billUpdate(BillDto bill) {
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
        values.put(FPSDBConstants.KEY_BILL_SERVER_REF_ID, bill.getBillRefId());
        values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
        values.put(FPSDBConstants.KEY_BILL_STATUS, "T");
        database.update(TABLE_BILL, values, "transactionId='" + bill.getTransactionId() + "'", null);

    }

    //Update the stock
    public void stockUpdate(List<FPSStockDto> stock) {
        for (FPSStockDto fpsStockDto : stock) {
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_STOCK_QUANTITY, fpsStockDto.getQuantity());
            database.update(TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + fpsStockDto.getProductId(), null);
        }

    }

    //This function inserts details to TABLE_BILL_ITEM,;
    private void insertBillItems(Set<BillItemDto> billItem, long refId, int month, long beneficiaryId, String billDate, String transactionId) {
        ContentValues values = new ContentValues();
        List<BillItemDto> billList = new ArrayList<BillItemDto>(billItem);
        for (BillItemDto billItems : billList) {
            values.put(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID, billItems.getProductId());
            values.put(FPSDBConstants.KEY_BILL_ITEM_QUANTITY, billItems.getQuantity());
            values.put(FPSDBConstants.KEY_BILL_ITEM_COST, billItems.getCost());
            values.put(FPSDBConstants.KEY_BILL_ITEM_BILL_REF, refId);
            values.put("transactionId", transactionId);
            values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, month);
            values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, beneficiaryId);
            values.put(FPSDBConstants.KEY_BILL_ITEM_DATE, billDate);
            database.insert(TABLE_BILL_ITEM, null, values);

        }
    }

    //Bill for background sync
    public List<BillDto> getAllBillsForSync() {
        List<BillDto> bills = new ArrayList<BillDto>();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_STATUS + "<>'T'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto bill = new BillDto(cursor);
            bill.setBillItemDto(getBillItems(bill.getBillLocalRefId()));
            bills.add(bill);
            cursor.moveToNext();
        }
        return bills;
    }

    //Bill for background sync
    public List<StockRequestDto> getAllStockSync() {
        List<StockRequestDto> stockRequestDtos = new ArrayList<StockRequestDto>();
        String selectQuery = "SELECT  * FROM " + TABLE_FPS_MANUAL_STOCK_INWARD + " where status ='T'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            StockRequestDto manualStocks = new StockRequestDto();
            manualStocks.setType(StockTransactionType.INWARD);
            manualStocks.setFpsId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID)));
            manualStocks.setGodownId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID)));
            manualStocks.setDate(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE)));
            manualStocks.setCreatedBy(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY)) + "");
            manualStocks.setUnit("5");
            manualStocks.setProductLists(getProduct(cursor.getLong(cursor.getColumnIndex(KEY_ID))));
            stockRequestDtos.add(manualStocks);
            cursor.moveToNext();
        }
        return stockRequestDtos;
    }

    private List<StockRequestDto.ProductList> getProduct(long id) {
        List<StockRequestDto.ProductList> productList = new ArrayList<StockRequestDto.ProductList>();
        String selectQuery = "SELECT  * FROM " + TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE + " where refId =" + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            StockRequestDto.ProductList manualProduct = new StockRequestDto.ProductList();
            manualProduct.setId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID)));
            manualProduct.setQuantity(cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY)));
            manualProduct.setRecvQuantity(cursor.getDouble(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY)));
            productList.add(manualProduct);
        }
        return productList;
    }

    //Bill from local db
    public List<BillItemDto> getAllBillItems(long id, int month) {
        List<BillItemDto> billItems = new ArrayList<BillItemDto>();
        String selectQuery = "SELECT productId,SUM(quantity) as total FROM " + TABLE_BILL_ITEM + " where " + FPSDBConstants.KEY_BILL_BENEFICIARY + " = " + id + " AND " + FPSDBConstants.KEY_BILL_TIME_MONTH + " = " + month + " group by " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemDto billItemDto = new BillItemDto();
            billItemDto.setProductId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID)));
            billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        return billItems;
    }

    //Bill from local db
    public List<BillDto> getAllBillsUser(long id, int month) {
        List<BillDto> bills = new ArrayList<BillDto>();
        String selectQuery = "SELECT * FROM " + TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_BENEFICIARY + " = " + id + " AND " + FPSDBConstants.KEY_BILL_TIME_MONTH + " = " + month + " order by transactionId";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto(cursor);
            bills.add(billDto);
            cursor.moveToNext();
        }
        return bills;
    }

    public Set<BillItemDto> getBillItems(long referenceId) {
        List<BillItemDto> billItems = new ArrayList<BillItemDto>();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL_ITEM + " where " + FPSDBConstants.KEY_BILL_ITEM_BILL_REF + "=" + referenceId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            billItems.add(new BillItemDto(cursor));
            cursor.moveToNext();
        }
        Set<BillItemDto> billItemSet = new HashSet<>(billItems);
        return billItemSet;
    }

    //Bill from local db
    public BillDto getBill(long billId) {
        BillDto billDto = new BillDto();
        String selectQuery = "SELECT  * FROM " + TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_REF_ID + "=" + billId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            billDto = new BillDto(cursor);
            billDto.setBillItemDto(getBillItems(billDto.getBillLocalRefId()));
            cursor.moveToNext();
        }
        return billDto;
    }


    //This function inserts details to TABLE_PRODUCTS;
    private void insertProductData(Set<ProductDto> productDto) {
        List<ProductDto> productList = new ArrayList<ProductDto>(productDto);
        for (ProductDto products : productList) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, products.getId());
            values.put(FPSDBConstants.KEY_PRODUCT_NAME, products.getName());
            values.put(FPSDBConstants.KEY_LPRODUCT_NAME, products.getLocalProdName());
            values.put(FPSDBConstants.KEY_PRODUCT_CODE, products.getCode());
            values.put(FPSDBConstants.KEY_LPRODUCT_UNIT, products.getLocalProdUnit());
            values.put(FPSDBConstants.KEY_PRODUCT_UNIT, products.getProductUnit());
            values.put(FPSDBConstants.KEY_PRODUCT_PRICE, products.getProductPrice());
            if (products.isNegativeIndicator())
                values.put(FPSDBConstants.KEY_NEGATIVE_INDICATOR, 0);
            else {
                values.put(FPSDBConstants.KEY_NEGATIVE_INDICATOR, 1);
            }
            values.put(FPSDBConstants.KEY_PRODUCT_MODIFIED_DATE, products.getModifiedDate());
            if (products.getModifiedby() != null)
                values.put(FPSDBConstants.KEY_MODIFIED_BY, products.getModifiedby());
            values.put(FPSDBConstants.KEY_CREATED_DATE, products.getCreatedDate());
            if (products.getCreatedby() != null)
                values.put(FPSDBConstants.KEY_CREATED_BY, products.getCreatedby());
            Log.e("ertetr", products.toString());
            Log.e("Insert", "" + database.insertWithOnConflict(TABLE_PRODUCTS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE));
        }
    }


    //This function inserts details to TABLE_BENEFICIARY;
    private void insertBeneficiaryData(Set<BeneficiaryDto> beneficiaryDtos) {
        try {
            ContentValues values = new ContentValues();
            List<BeneficiaryDto> beneficiaryDto = new ArrayList<BeneficiaryDto>(beneficiaryDtos);//get Beneficiary Details
            for (BeneficiaryDto beneficiary : beneficiaryDto) {
                Log.e("Beneficiary", beneficiary.toString());
                values.put(KEY_ID, beneficiary.getId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, beneficiary.getEncryptedUfc());
                //  values.put(FPSDBConstants.KEY_BENEFICIARY_QR_CODE, beneficiary.getUfc());
                values.put(FPSDBConstants.KEY_BENEFICIARY_FPS_ID, beneficiary.getFpsId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_TIN, beneficiary.getTin());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MOBILE, beneficiary.getMobileNumber());
                values.put(FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE, beneficiary.getCreatedDate());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY, beneficiary.getModifiedBy());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE, beneficiary.getModifiedDate());
                values.put(FPSDBConstants.KEY_BENEFICIARY_STATE_ID, beneficiary.getStateId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID, beneficiary.getCardTypeId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID, beneficiary.getDistrictId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_TALUK_ID, beneficiary.getTalukId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID, beneficiary.getVillageId());
                String cardNumber = beneficiary.getOldRationNumber().replaceAll("[^a-zA-Z0-9]", "");
                values.put("oldRationNumber", cardNumber.toUpperCase());
                values.put("numOfCylinder", beneficiary.getNumOfCylinder());
                values.put("numOfAdults", beneficiary.getNumOfAdults());
                values.put("numOfChild", beneficiary.getNumOfChild());

                int active = 0;
                if (beneficiary.isActive()) {
                    active = 1;
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, active);

                setBeneficiaryMemberData(beneficiary);
                database.insertWithOnConflict(TABLE_BENEFICIARY, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }


    //This function inserts details to TABLE_BENEFICIARY_MEMBER;
    private void setBeneficiaryMemberData(BeneficiaryDto fpsDataDto) {

        ContentValues values = new ContentValues();
        List<BeneficiaryMemberDto> beneficiaryMemberList = new ArrayList<BeneficiaryMemberDto>(fpsDataDto.getBenefMembersDto());
        for (BeneficiaryMemberDto beneficiaryMember : beneficiaryMemberList) {
            values.put(KEY_ID, beneficiaryMember.getId());
            values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, fpsDataDto.getEncryptedUfc());
            if (beneficiaryMember.getTin() != null) {
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN, beneficiaryMember.getTin());
            }
            if (beneficiaryMember.getUid() != null) {
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, beneficiaryMember.getUid());
            }
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID, beneficiaryMember.getEid());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME, beneficiaryMember.getLname());

            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FIRST_NAME, beneficiaryMember.getFirstName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MIDDLE_NAME, beneficiaryMember.getMiddleName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LAST_NAME, beneficiaryMember.getLastName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NAME, beneficiaryMember.getFatherName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NAME, beneficiaryMember.getMotherName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER, String.valueOf(beneficiaryMember.getGender()));//gender char
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY, beneficiaryMember.getCreatedBy());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY, beneficiaryMember.getCreatedBy());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_DATE, beneficiaryMember.getCreatedDate());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_BY, beneficiaryMember.getModifiedBy());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_DATE, beneficiaryMember.getModifiedDate());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAME, beneficiaryMember.getName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME, beneficiaryMember.getRelName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID, String.valueOf(beneficiaryMember.getGenderId())); //gender id
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB, beneficiaryMember.getDob());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB_TYPE, String.valueOf(beneficiaryMember.getDobType())); //dob type
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID, String.valueOf(beneficiaryMember.getMstatusId()));
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME, beneficiaryMember.getEduName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME, beneficiaryMember.getOccuName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE, beneficiaryMember.getFatherCode());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM, beneficiaryMember.getLfatherName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE, beneficiaryMember.getMotherCode());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM, beneficiaryMember.getLmotherName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE, beneficiaryMember.getSpouseCode());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM, beneficiaryMember.getSpouseName());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME, beneficiaryMember.getNatname());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1, beneficiaryMember.getAddressLine1());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2, beneficiaryMember.getAddressLine2());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3, beneficiaryMember.getAddressLine3());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4, beneficiaryMember.getAddressLine4());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5, beneficiaryMember.getAddressLine5());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1, beneficiaryMember.getLaddressLine1());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2, beneficiaryMember.getLaddressLine2());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3, beneficiaryMember.getLaddressLine3());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4, beneficiaryMember.getLaddressLine4());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5, beneficiaryMember.getLaddressLine5());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE, beneficiaryMember.getPincode());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR, beneficiaryMember.getDurationInYear());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1, beneficiaryMember.getPaddressLine1());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2, beneficiaryMember.getPaddressLine2());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3, beneficiaryMember.getPaddressLine3());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4, beneficiaryMember.getPaddressLine4());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5, beneficiaryMember.getPaddressLine5());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1, beneficiaryMember.getLpaddressLine1());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2, beneficiaryMember.getLpaddressLine2());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3, beneficiaryMember.getLpaddressLine3());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4, beneficiaryMember.getLpaddressLine4());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5, beneficiaryMember.getLpaddressLine5());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE, beneficiaryMember.getPpincode());
            values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED, beneficiaryMember.getDateDataEntered());

            if (beneficiaryMember.isAliveStatus())
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS, 0);
            else {
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS, 1);
            }

            if (beneficiaryMember.isAdult())
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT, 0);
            else {
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT, 1);
            }
            database.insertWithOnConflict(TABLE_BENEFICIARY_MEMBER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

        }


    }

    private List<BeneficiaryMemberDto> getAllBeneficiaryMembers(String qrCode) {
        List<BeneficiaryMemberDto> beneficiaryMembers = new ArrayList<BeneficiaryMemberDto>();
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY_MEMBER + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + "= 0 order by bmTIN";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            beneficiaryMembers.add(new BeneficiaryMemberDto(cursor));
            cursor.moveToNext();
        }
        return beneficiaryMembers;
        //new BeneficiaryDto(cursor);
    }

    //Get Beneficiary data by QR Code
    public BeneficiaryDto beneficiaryDto(String qrCode) {
        String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            return beneficiary;
        }
    }

    //Get Product data by Product Id
    public ProductDto getProductDetails(long _id) {
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;//+" AND " +FPSDBConstants.KEY_BENEFICIARY_ACTIVE +"=0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else
            return new ProductDto(cursor);
    }

    //Get Product data
    public List<ProductDto> getAllProductDetails() {
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS;
        List<ProductDto> products = new ArrayList<ProductDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            products.add(new ProductDto(cursor));
            cursor.moveToNext();
        }
        return products;
    }

    //Get Product stock data
    public FPSStockDto getAllProductStockDetails(long productId) {
        String selectQuery = "SELECT  * FROM " + TABLE_STOCK + " where " + FPSDBConstants.KEY_STOCK_PRODUCT_ID + " = " + productId;
        FPSStockDto productStock = new FPSStockDto();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            productStock = new FPSStockDto(cursor);
        return productStock;
    }


    //This function inserts details to TABLE_CARD_TYPE.
    private void insertCardTypeData(Set<CardTypeDto> cardTypeDto) {
        ContentValues values = new ContentValues();
        List<CardTypeDto> cardTypeList = new ArrayList<CardTypeDto>(cardTypeDto);
        if (!cardTypeList.isEmpty()) {
            for (CardTypeDto cardType : cardTypeList) {
                values.put(KEY_ID, cardType.getId());
                if (String.valueOf(cardType.getType()) != null) {
                    values.put(FPSDBConstants.KEY_CARD_TYPE, String.valueOf(cardType.getType()));
                }
                if (String.valueOf(cardType.getDescription()) != null) {
                    values.put(FPSDBConstants.KEY_CARD_DESCRIPTION, cardType.getDescription());
                }
                database.insertWithOnConflict(TABLE_CARD_TYPE, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    //This function inserts details to TABLE_CARD_TYPE.
    private void insertRegistrationRequestData(Set<BeneficiaryRegistrationData> beneficiaryRegistrationDataSet) {
        ContentValues values = new ContentValues();
        List<BeneficiaryRegistrationData> beneficiaryRegistrationDataList = new ArrayList<BeneficiaryRegistrationData>(beneficiaryRegistrationDataSet);
        if (!beneficiaryRegistrationDataList.isEmpty()) {
            for (BeneficiaryRegistrationData beneficiaryRegistrationData : beneficiaryRegistrationDataList) {
                values.put(KEY_ID, beneficiaryRegistrationData.getId());
                values.put("oldRationCardNum", beneficiaryRegistrationData.getOldRationCardNum());
                values.put("numOfCylinder", beneficiaryRegistrationData.getNumOfCylinder());
                values.put("numOfAdults", beneficiaryRegistrationData.getNumOfAdults());
                values.put("numOfChild", beneficiaryRegistrationData.getNumOfChild());
                values.put("mobNum", beneficiaryRegistrationData.getMobNum());
                values.put("fpsId", beneficiaryRegistrationData.getFpsId());
                values.put("channel", beneficiaryRegistrationData.getChannel());
                values.put("cardType", beneficiaryRegistrationData.getCardType());
                database.insertWithOnConflict(TABLE_REG_REQ, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    //This function inserts details to TABLE_USERS,
    public void insertUserDetailData(Set<UserDetailDto> userdetailDto) {

        ContentValues values = new ContentValues();
        List<UserDetailDto> userDetailList = new ArrayList<UserDetailDto>(userdetailDto);
        if (!userDetailList.isEmpty()) {
            for (UserDetailDto userDetail : userDetailList) {
                values.put(KEY_ID, userDetail.getId());
                values.put(FPSDBConstants.KEY_USERS_NAME, userDetail.getUserId().toLowerCase());
                values.put(FPSDBConstants.KEY_USERS_PASS_HASH, userDetail.getPassword());
                values.put(FPSDBConstants.KEY_USERS_PROFILE, userDetail.getProfile());
                values.put(FPSDBConstants.KEY_USERS_CREATE_DATE, userDetail.getCreatedDate());
                values.put(FPSDBConstants.KEY_USERS_MODIFIED_DATE, userDetail.getModifiedDate());
                values.put(FPSDBConstants.KEY_USERS_CREATED_BY, userDetail.getCreatedBy());
                values.put(FPSDBConstants.KEY_USERS_MODIFIED_BY, userDetail.getModifiedBy());
                values.put(FPSDBConstants.KEY_USERS_FPS_ID, userDetail.getFpsStore().getId());
                values.put("contactPerson", userDetail.getFpsStore().getContactPerson());
                values.put("phoneNumber", userDetail.getFpsStore().getPhoneNumber());
                values.put("addressLine1", userDetail.getFpsStore().getAddressLine1());
                values.put("addressLine2", userDetail.getFpsStore().getAddressLine2());
                values.put("addressLine3", userDetail.getFpsStore().getAddressLine3());
                values.put("code", userDetail.getFpsStore().getCode());
                int status = 0;
                if (userDetail.getFpsStore().isActive()) {
                    status = 1;
                }
                values.put("isActive", status);
                database.insertWithOnConflict(TABLE_USERS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

            }
        } else {
            Log.e("UD", TABLE_USERS + "is empty");
        }
    }


    // This function inserts details to TABLE_STOCK
    private void insertFpsStockData(Set<FPSStockDto> fpsStockDto) {

        List<FPSStockDto> fpsStockList = new ArrayList<FPSStockDto>(fpsStockDto);
        if (!fpsStockList.isEmpty()) {
            for (FPSStockDto fpsStock : fpsStockList) {
                ContentValues values = new ContentValues();
                values.put(FPSDBConstants.KEY_STOCK_FPS_ID, fpsStock.getFpsId());
                values.put(FPSDBConstants.KEY_STOCK_PRODUCT_ID, fpsStock.getProductId());
                values.put(FPSDBConstants.KEY_STOCK_QUANTITY, fpsStock.getQuantity());
                values.put(FPSDBConstants.KEY_STOCK_REORDER_LEVEL, fpsStock.getReorderLevel());

                if (fpsStock.isEmailAction()) {
                    values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 0);
                } else {
                    values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 1);
                }

                if (fpsStock.isSmsMSAction()) {
                    values.put(FPSDBConstants.KEY_STOCK_SMSACTION, 0);
                } else {
                    values.put(FPSDBConstants.KEY_STOCK_SMSACTION, 1);
                }
                database.insertWithOnConflict(TABLE_STOCK, FPSDBConstants.KEY_STOCK_PRODUCT_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                insertStockHistory(0.0, fpsStock.getQuantity(), "INITIAL STOCK", fpsStock.getQuantity(), fpsStock.getProductId());
            }
        }

    }

/*
    // This function inserts details to TABLE_GO_DOWN
    private void setGoDownDetails(GodownDto goDown) {

        if (goDown != null) {

            ContentValues values = new ContentValues();
            if (goDown.getId() != 0) {
                values.put(KEY_ID, goDown.getId());
            }
            Log.i("GoId", "setGodownId" + goDown.getId());

            values.put(FPSDBConstants.KEY_GO_DOWN_GO_DOWN_CODE, goDown.getGodownCode());
            values.put(FPSDBConstants.KEY_GO_DOWN_NAME, goDown.getName());
            values.put(FPSDBConstants.KEY_GO_DOWN_DISTRICT, goDown.getDistrict());
            values.put(FPSDBConstants.KEY_GO_DOWN_CATEGORY_ID, goDown.getCategoryId());
            values.put(FPSDBConstants.KEY_GO_DOWN_CONTACT_PERSON_NAME, goDown.getContactPersonName());
            values.put(FPSDBConstants.KEY_GO_DOWN_CONTACT_NUMBER, goDown.getContactNumber());
            values.put(FPSDBConstants.KEY_GO_DOWN_PIN_CODE, goDown.getPincode());
            values.put(FPSDBConstants.KEY_GO_DOWN_ADDRESS, goDown.getAddress());

            if (goDown.isStatus()) {
                values.put(FPSDBConstants.KEY_GO_DOWN_IS_STATUS, 0);
            } else {
                values.put(FPSDBConstants.KEY_GO_DOWN_IS_STATUS, 1);
            }


            values.put(FPSDBConstants.KEY_GO_DOWN_CREATED_DATE, goDown.getCreatedDate());
            values.put(FPSDBConstants.KEY_GO_DOWN_MODIFIED_DATE, goDown.getModifiedDate());
            values.put(FPSDBConstants.KEY_GO_DOWN_CREATED_BY, goDown.getCreatedby());
            values.put(FPSDBConstants.KEY_GO_DOWN_MODIFIED_BY, goDown.getModifiedby());
            database.insertWithOnConflict(TABLE_GO_DOWN, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }*/


    //This function inserts details to TABLE_USERS,
    public void insertLpgProviderDetails(Set<ServiceProviderDto> lpgProviderDto) {

        ContentValues values = new ContentValues();
        List<ServiceProviderDto> lpgDetailList = new ArrayList<ServiceProviderDto>(lpgProviderDto);
        if (!lpgDetailList.isEmpty()) {
            for (ServiceProviderDto lpgDetail : lpgDetailList) {
                values.put(KEY_ID, lpgDetail.getId());
                values.put(FPSDBConstants.KEY_LPG_PROVIDER_NAME, lpgDetail.getProviderName());
                values.put(FPSDBConstants.KEY_LPG_PROVIDER_CREATEDBY, lpgDetail.getCreatedBy());
                values.put(FPSDBConstants.KEY_LPG_PROVIDER_CREATEDDATE, lpgDetail.getCreatedDate());
                values.put(FPSDBConstants.KEY_LPG_PROVIDER_MODIFIEDDATE, lpgDetail.getModifiedDate());
                if (lpgDetail.isStatus()) {
                    values.put(FPSDBConstants.KEY_LPG_PROVIDER_STATUS, 0);
                } else {
                    values.put(FPSDBConstants.KEY_LPG_PROVIDER_STATUS, 1);
                }
                database.insertWithOnConflict(TABLE_LPG_PROVIDER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);

            }
        } else {
            Log.e("LPG", TABLE_LPG_PROVIDER + "is empty");
        }
    }

    // This function returns Card types
    public List<String> getCardType() {
        List<String> cardType = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM " + TABLE_CARD_TYPE + " order by " + FPSDBConstants.KEY_CARD_DESCRIPTION;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cardType.add(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION)));
            cursor.moveToNext();
        }
        return cardType;
    }

    // This function returns Card types
    public String getCardType(String card) {
        String cardType = "";
        String selectQuery = "SELECT  * FROM " + TABLE_CARD_TYPE + " where " + FPSDBConstants.KEY_CARD_DESCRIPTION + " = '" + card + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        cardType = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE));
        cursor.moveToNext();
        return cardType;
    }


    /*// This function return beneficiary details for beneficiary activation
    public List<String> retrieveBeneficiaryActivation(String ufc) {

        SQLiteDatabase db = this.getReadableDatabase();
        int fpsId = 0;
        int createdBy = 0;
        int modifiedBy = 0;
        int active = 0;
        List<String> valueList = new ArrayList<String>();

        if (!valueList.isEmpty()) {
            String selectQuery = "SELECT  * FROM " + TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "=?";
            Cursor c = db.rawQuery(selectQuery, new String[]{ufc});
            if (c.moveToFirst()) {
                fpsId = c.getInt(c.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_FPS_ID));
                createdBy = c.getInt(c.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY));
                modifiedBy = c.getInt(c.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY));
                active = c.getInt(c.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_ACTIVE));
            }
            valueList.add(String.valueOf(fpsId));
            valueList.add(String.valueOf(createdBy));
            valueList.add(String.valueOf(modifiedBy));
            valueList.add(String.valueOf(active));
            c.close();
            return valueList;
        }
        return valueList;
    }
*/

    //This function loads data to language table
    public void insertLanguageTable(MessageDto message) {
        ContentValues values = new ContentValues();
        values.put(KEY_ID, message.getId());
        values.put(FPSDBConstants.KEY_LANGUAGE_CODE, message.getLanguageCode());
        values.put(FPSDBConstants.KEY_LANGUAGE_ID, message.getLanguageId());
        values.put(FPSDBConstants.KEY_LANGUAGE_MESSAGE, message.getDescription());

        database.insertWithOnConflict(TABLE_LANGUAGE, FPSDBConstants.KEY_LANGUAGE_ID, values,
                SQLiteDatabase.CONFLICT_REPLACE);

    }

    //This function loads data to language table
    public void insertImageData(String cardNumber, String imageId) {
        ContentValues values = new ContentValues();
        values.put("cardNumber", cardNumber);
        values.put("imageId", imageId);
        values.put("status", "R");
        database.insertWithOnConflict(TABLE_CARD_IMAGE, "cardNumber", values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    //This function retrieve error description from language table
    public MessageDto retrieveLanguageTable(int errorCode, String language) {
        String selectQuery = "SELECT  * FROM " + TABLE_LANGUAGE + " where  " + FPSDBConstants.KEY_LANGUAGE_CODE + " = " + errorCode + " AND " + FPSDBConstants.KEY_LANGUAGE_ID + " ='" + language + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        MessageDto messageDto = new MessageDto();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            messageDto = new MessageDto(cursor);
            break;
        }
        cursor.close();
        return messageDto;
    }

    // This function inserts details to TABLE_STOCK
    private void insertFpsStockInwardDetails(Set<GodownStockOutwardDto> fpsStoInwardDto) {


        List<GodownStockOutwardDto> fpsStockInwardList = new ArrayList<GodownStockOutwardDto>(fpsStoInwardDto);

        if (!fpsStockInwardList.isEmpty()) {
            for (GodownStockOutwardDto fpsStockInvard : fpsStockInwardList) {
                ContentValues values = new ContentValues();

                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, fpsStockInvard.getGodownId());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, fpsStockInvard.getFpsId());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE, fpsStockInvard.getOutwardDate());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, fpsStockInvard.getProductId());

                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, fpsStockInvard.getQuantity());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT, fpsStockInvard.getUnit());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO, fpsStockInvard.getBatchno());

                if (fpsStockInvard.isFpsAckStatus() == false) {
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 0);
                } else {
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
                }
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, fpsStockInvard.getFpsAckDate());
                if (fpsStockInvard.getFpsReceiQuantity() != null)
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, fpsStockInvard.getFpsReceiQuantity());
                else
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, 0);
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY, fpsStockInvard.getCreatedby());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, fpsStockInvard.getDeliveryChallanId());

                database.insert(TABLE_FPS_STOCK_INWARD, null, values);
                // database.insertWithOnConflict(TABLE_FPS_STOCK_INVARD, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }

    }

    public List<GodownStockOutwardDto> showFpsStockInvard(long fpsId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_FPS_STOCK_INWARD + " where " + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY + " = 0.0 group by " + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID;
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<GodownStockOutwardDto> fpsInwardList = new ArrayList<GodownStockOutwardDto>();
        GodownStockOutwardDto fpsStockOutwardDto = new GodownStockOutwardDto();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsStockOutwardDto = new GodownStockOutwardDto(cursor);
            fpsInwardList.add(fpsStockOutwardDto);
            cursor.moveToNext();
        }
        cursor.close();
        return fpsInwardList;
    }


    public List<GodownStockOutwardDto> showFpsStockInvardDetail(long chellanId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<GodownStockOutwardDto> fpsInwardList = new ArrayList<GodownStockOutwardDto>();
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_FPS_STOCK_INWARD + " where " + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID + "=" + chellanId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Set<ChellanProductDto> chellanProductDtoSet = new HashSet<ChellanProductDto>();
            cursor.moveToFirst();
            String productName = null;
            for (int i = 0; i < cursor.getCount(); i++) {
                GodownStockOutwardDto fpsStockOutwardDto = new GodownStockOutwardDto(cursor);
                productName = getProductName(fpsStockOutwardDto.getProductId());
                ChellanProductDto chellanProductDto = new ChellanProductDto();
                chellanProductDto.setName(productName);
                chellanProductDto.setProductId(fpsStockOutwardDto.getProductId());
                chellanProductDto.setQuantity(fpsStockOutwardDto.getQuantity());

                chellanProductDtoSet.add(chellanProductDto);

                fpsStockOutwardDto.setProductDto(chellanProductDtoSet);

                fpsInwardList.add(fpsStockOutwardDto);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (Exception e) {
            Log.e("Exception", e.toString());

        }

        return fpsInwardList;
    }


    public void updateReceivedQuantity(GodownStockOutwardDto godownStockOutwardDto) {
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, godownStockOutwardDto.getFpsAckDate());
        double receivedQuantity = 0.0;
        for (ChellanProductDto chellanProductDto : godownStockOutwardDto.getProductDto()) {
            receivedQuantity = chellanProductDto.getReceiProQuantity();
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, receivedQuantity);
            database.update(TABLE_FPS_STOCK_INWARD, values, FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID + " = ? AND " + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID + " = ?", new String[]{String.valueOf(chellanProductDto.getProductId()), String.valueOf(godownStockOutwardDto.getDeliveryChallanId())});
            FPSStockDto stockList = getAllProductStockDetails(chellanProductDto.getProductId());
            double closing = stockList.getQuantity() + chellanProductDto.getReceiProQuantity();
            insertStockHistory(stockList.getQuantity(), closing, "INWARD", chellanProductDto.getReceiProQuantity(), chellanProductDto.getProductId());
            stockList.setQuantity(closing);
            stockUpdate(stockList);
        }
    }

    //Update the stock
    public void stockUpdate(FPSStockDto stock) {
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_STOCK_QUANTITY, stock.getQuantity());
        database.update(TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + stock.getProductId(), null);
    }

    //Update Stock Inward
    public void updateStockInwardDeclined(long inwardId) {
        ContentValues values = new ContentValues();
        values.put("status", "D");
        database.update(TABLE_FPS_MANUAL_STOCK_INWARD, values, KEY_ID + "=" + inwardId, null);
        Log.e("Declinedupdate", "" + inwardId);

    }


    //Get Product Name by Product Id
    public String getProductName(long _id) {
        String selectQuery = "SELECT  * FROM " + TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else
            return new ProductDto(cursor).getName();
    }

    // database connection  close
    public synchronized void closeConnection() {
        if (dbHelper != null) {
            dbHelper.close();
            database.close();
            dbHelper = null;
            database = null;
        }
    }

    //Bill for background sync
    public List<BillItemDto> getAllBillItemsListToday(String toDate) {
        List<BillItemDto> bills = new ArrayList<BillItemDto>();
        String selectQuery = "SELECT  productId,SUM(quantity) as total FROM " + TABLE_BILL_ITEM + " where " + FPSDBConstants.KEY_BILL_ITEM_DATE + " LIKE '" + toDate + " %' group by productId";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemDto billItemDto = new BillItemDto();
            billItemDto.setProductId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID)));
            billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
            bills.add(billItemDto);
            cursor.moveToNext();
        }

        return bills;
    }


    public List<OpeningClosingBalanceDto> showOpeningCloseBalanceProductDetails(String date) {
        List<OpeningClosingBalanceDto> openingBalanceClosingBalanceDtoList = new ArrayList<OpeningClosingBalanceDto>();

        List<BillItemDto> billItemDtoList = getAllBillItemsListToday(date);
        for (BillItemDto billItemDto : billItemDtoList) {
            OpeningClosingBalanceDto openingClosingBalanceDto = new OpeningClosingBalanceDto();
            String productName = getProductName(billItemDto.getProductId());
            openingClosingBalanceDto.setQuantity(billItemDto.getQuantity());
            openingClosingBalanceDto.setName(productName);
            openingBalanceClosingBalanceDtoList.add(openingClosingBalanceDto);

        }
        return openingBalanceClosingBalanceDtoList;
    }


    /*public long getGodownId(long fpsId) {
        String selectQuery = "SELECT  * FROM  " + TABLE_STORE + " where " + KEY_ID + "=" + fpsId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.i("query", selectQuery + "cursor size" + cursor.getCount());
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            FpsStoreDto fpsStoreDto = new FpsStoreDto(cursor);
            Log.i("FSTORE", "" + "GodownDto" + fpsStoreDto.getId() + "godownid" + fpsStoreDto.getGodownId());
            return fpsStoreDto.getGodownId();
        } else {
            Log.i("FPSTORE", "fbsstore is null");
            return 0;
        }
    }
*/

    public List<FPSIndentRequestDto> showFpsIntentRequestProduct(long fpsId) {

        List<ProductDto> productDtoList = getAllProductDetails();

        List<FPSIndentRequestDto> fpsIndentRequestDtoList = new ArrayList<FPSIndentRequestDto>();
        Set<FpsIntentReqProdDto> fpsIntentReqProdDtoSet = new HashSet<FpsIntentReqProdDto>();
        FPSIndentRequestDto fpsIndentRequestDto = new FPSIndentRequestDto();

        // fpsIndentRequestDto.setGodownId(godownId);
        fpsIndentRequestDto.setFpsId(fpsId);

        for (ProductDto productDto : productDtoList) {
            FpsIntentReqProdDto fpsIntentReqProdDto = new FpsIntentReqProdDto();

            fpsIntentReqProdDto.setProductId(productDto.getId());
            fpsIntentReqProdDto.setName(productDto.getName());
            fpsIntentReqProdDtoSet.add(fpsIntentReqProdDto);
            fpsIndentRequestDto.setProdDtos(fpsIntentReqProdDtoSet);
            fpsIndentRequestDtoList.add(fpsIndentRequestDto);
        }

        return fpsIndentRequestDtoList;
    }

    public void insertFpsIntentReqProQuantity(FPSIndentRequestDto fpsIndentRequestDto) {

        Log.i("RDto", fpsIndentRequestDto.toString());

        if (fpsIndentRequestDto != null) {
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_GODOWN_ID, fpsIndentRequestDto.getGodownId());
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_FPS_ID, fpsIndentRequestDto.getFpsId());

            if (fpsIndentRequestDto.isTalukOffiApproval() == false) {
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_TALUK_OFFICER_APPROVAL, 0);
            } else {
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_TALUK_OFFICER_APPROVAL, 1);
            }
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_DATE_OF_APPROVAL, fpsIndentRequestDto.getDateOfApproval());
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_MODIFIED_QUANTITY, fpsIndentRequestDto.getModifiedQuantity());
            if (fpsIndentRequestDto.isStatus() == false) {
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_STATUS, 0);
            } else {
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_STATUS, 1);
            }
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_REASON, fpsIndentRequestDto.getReason());
            values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_DESCRIPTION, fpsIndentRequestDto.getDescription());
            for (FpsIntentReqProdDto fpsIntentReqProdDto : fpsIndentRequestDto.getProdDtos()) {
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_PRODUCT_ID, fpsIntentReqProdDto.getProductId());
                values.put(FPSDBConstants.KEY_FPS_INTENT_REQUEST_QUANTITY, fpsIntentReqProdDto.getQuantity());
                database.insert(TABLE_FPS_INTENT_REQUEST_PRODUCT, null, values);
            }


        }

    }

    //This function retrieve error description from language table
    public List<FpsIntentReqProdDto> retrieveIntentRequest() {
        String selectQuery = "SELECT  * FROM " + TABLE_FPS_INTENT_REQUEST_PRODUCT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<FpsIntentReqProdDto> list = new ArrayList<>();
        FPSIndentRequestDto fpsIndentRequestDto = new FPSIndentRequestDto();
        FpsIntentReqProdDto fpsIntentReqProdDto = new FpsIntentReqProdDto();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsIntentReqProdDto = new FpsIntentReqProdDto(cursor);
            cursor.moveToNext();
            list.add(fpsIntentReqProdDto);
        }
        cursor.close();
        return list;
    }

    public List<BillDto> getAllBillDetailsMonth(int limit, int count) {
        List<BillDto> bill = new ArrayList<BillDto>();
        String selectQuery = "SELECT * FROM " + TABLE_BILL + " ORDER BY " + FPSDBConstants.KEY_BILL_REF_ID + " DESC limit " + (count * limit) + "," + limit;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto(cursor);
            bill.add(billDto);
            cursor.moveToNext();
        }
        return bill;
    }


    public void purgeBillBItemDetails(int noOfDays, String transmitted) {
        String sql = "DELETE FROM " + TABLE_BILL + " WHERE " + FPSDBConstants.KEY_BILL_DATE + " <= date('now','-" + noOfDays + " day')" + " AND " + FPSDBConstants.KEY_BILL_STATUS + " ='" + transmitted + "'";
        //String sql = "DELETE FROM bill WHERE date <= date('now','-0 day')AND billStatus ='T' ";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            Log.e("working", "workingBBill     " + sql + "   noDays" + noOfDays);
            purgeBillItemDetails(noOfDays);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        } finally {
            database.endTransaction();
        }

    }

    public void purgeBillItemDetails(int noOfDays) {
        String sql = "DELETE FROM " + TABLE_BILL_ITEM + " WHERE " + FPSDBConstants.KEY_BILL_ITEM_DATE + " <= date('now','-" + noOfDays + " day')";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
            Log.e("working", "workingBBillItem" + sql);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);

        } finally {
            database.endTransaction();
        }

    }


 /*   //Get Fps store
    public List<FpsStoreDto> getFpsStoreDetails(long fpsId) {
        String selectQuery = "SELECT  * FROM  " + TABLE_STORE + " where " + KEY_ID + "=" + fpsId;
        List<FpsStoreDto> fpsStoreList = new ArrayList<FpsStoreDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsStoreList .add(new FpsStoreDto(cursor));
            cursor.moveToNext();

            Log.e("fpsstorelist",fpsStoreList.toString());
        }
        return fpsStoreList ;
    }*/


    public List<GodownDto> getGodownDetails() {
        String selectQuery = "SELECT  * FROM  " + TABLE_GO_DOWN;
        List<GodownDto> godownList = new ArrayList<GodownDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);

        Log.e("cursorSize", "" + cursor.getCount());

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            godownList.add(new GodownDto(cursor));
            cursor.moveToNext();

            Log.e("godownListDb", godownList.toString());
        }
        return godownList;
    }


    //Get Godown data by GodownId
    public GodownDto getGodown(String name) {
        String selectQuery = "SELECT  * FROM " + TABLE_GO_DOWN + " where " + FPSDBConstants.KEY_GO_DOWN_NAME + "='" + name + "'";
        Log.e("idquery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        } else
            return new GodownDto(cursor);
    }

    public int readManualStockInward(long inwardKey) {
        int retryCount = 0;
        String selectQuery = "SELECT  * FROM " + TABLE_FPS_MANUAL_STOCK_INWARD + " where " + KEY_ID + "=" + inwardKey;
        Log.e("Manualinward", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            retryCount = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_COUNT));
        }
        return retryCount;
    }


    public void purgeStockHistoryDetails() {
        String sql = "DELETE FROM " + TABLE_STOCK_HISTORY + " WHERE " + FPSDBConstants.KEY_STOCK_HISTORY_DATE + " <= date('now','-30 day')";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);

        } finally {
            database.endTransaction();
        }

    }

    public void stockAdjustmentUpdate(List<StockRequestDto.ProductList> prodsList) {

        try {
            for (StockRequestDto.ProductList product : prodsList) {
                ContentValues values = new ContentValues();
                double closing = 0.0;
                if (product.getAdjustment().equals("Decrease") || product.getAdjustment().equals("குறை")) {

                    closing = product.getQuantity() - product.getRecvQuantity();
                    values.put(FPSDBConstants.KEY_STOCK_QUANTITY, closing);
                }
                if (product.getAdjustment().equals("Increase") || product.getAdjustment().equals("அதிகரி")) {
                    closing = product.getQuantity() + product.getRecvQuantity();
                    values.put(FPSDBConstants.KEY_STOCK_QUANTITY, closing);
                }
                database.beginTransaction();

                FPSStockDto stockList = getAllProductStockDetails(product.getId());
                insertStockHistory(stockList.getQuantity(), closing, "ADJUSTMENT", product.getRecvQuantity(), product.getId());

                database.update(TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + product.getId(), null);
                database.setTransactionSuccessful();

            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        } finally {
            database.endTransaction();
        }

    }
}