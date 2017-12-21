package com.omneagate.Util;

import com.omneagate.Util.Constants.FPSDBConstants;

/**
 * Created for tables creation
 */
public class FPSDBTables {
    //Key for id in tables
    public final static String KEY_ID = "_id";
    // fpsUsers table name
    public static final String TABLE_USERS = "users";
    // benefRegReq table name
    public static final String TABLE_REG_REQ = "beneficiary_registration";
    // Products table name
    public static final String TABLE_PRODUCTS = "products";
    // Entitlement Rules table name
    public static final String TABLE_ENTITLEMENT_RULES = "entitlement_rules";

    /*// Card Images Address table name
    public static final String TABLE_CARD_IMAGE = "ration_card_images";*/
    // Close Sale table name
    // Card Images Address table name
    public static final String TABLE_SMS_PROVIDER = "sms_provider";
    // Entitlement Rules table name
    public static final String TABLE_SPECIAL_RULES = "special_rules";
    public static final String TABLE_CLOSE_SALE = "close_sale";
    // Person Rules table name
    public static final String TABLE_PERSON_RULES = "person_rules";
    // Person Rules table name
    public static final String TABLE_REGION_RULES = "region_rules";
    // beneficiary table name
    public static final String TABLE_BENEFICIARY = "beneficiary";
    public static final String TABLE_BENEFICIARY_IN = "beneficiary_in";
    public static final String TABLE_OFFLINE_ACTIVATION = "offline_activation";
    public static final String TABLE_PRODUCT_PRICE_OVERRIDE = "product_price_override";
    public static final String TABLE_PRODUCT_GROUP = "product_group";
    public static final String TABLE_MEMBERS_AADHAR = "members_aadhar";
    // Beneficiary Member table name
    public static final String TABLE_BENEFICIARY_MEMBER = "beneficiary_member";
    public static final String TABLE_BENEFICIARY_MEMBER_IN = "beneficiary_member_in";
    // STOCK table name
    public static final String TABLE_STOCK = "stock";
    public static final String TABLE_UPGRADE = "table_upgrade";
    // STOCK table name
    public static final String TABLE_REGISTRATION = "registration";
    // CardType table name
    public static final String TABLE_CARD_TYPE = "card_type";
    public static final String TABLE_CONFIG_TABLE = "configuration";
    // CardType table name
    public static final String TABLE_BILL_ITEM = "bill_item";
    public static final String TABLE_LOGIN_HISTORY = "login_history";
    // bill table name
    public static final String TABLE_BILL = "bill";
    //Lanuage Database Table for Error Message
    public static final String TABLE_LANGUAGE = "error_messages";
    public static final String TABLE_STOCK_HISTORY = "stock_history";
//    public static final String TABLE_STOCK_ALLOTMENT_DETAILS = "stock_allotment_details";
    //Godown Stock inward table
    public static final String TABLE_FPS_STOCK_INWARD = "stock_inward";
    //Godown Stock inward table
    public static final String TABLE_FPS_ADVANCE_STOCK_INWARD = "advance_stock_inward";
    public static final String TABLE_FPS_MIGRATION_IN = "migration_in";
    public static final String TABLE_FPS_MIGRATION_OUT = "migration_out";
    /* 15-07-2016
   * Added new table CREATE_TABLE_POSOPERATINGHOURS
   */
    public static final String TABLE_POSOPERATINGHOURS = "pos_operating_hours";
    //Fps Manual Stock inward table
//    public static final String TABLE_FPS_MANUAL_STOCK_INWARD = "manual_stock_inward";
    //Fps Manual Stock Inward Product Table
//    public static final String TABLE_FPS_MANUAL_STOCK_INWARD_PRODUCT_TABLE = "manual_stock_inward_product";
    //Roll future table
    public static final String TABLE_ROLE_FEATURE = "role_feature";
    public static final String TABLE_CARD_VERIFICATION = "table_card_verification";
    public static final String TABLE_OTHERS = "table_others";
    public static final String TABLE_WEIGHMENT = "table_weighment";
    public static final String TABLE_FPS_TIME = "table_fpstime";
    public static final String TABLE_STOCK_ALLOCATION = "table_stock_allocation";
    public static final String TABLE_NFSAPOSDATA = "table_nfsa_pos_data";
    public static final String TABLE_PERSON_RULES_TEMP = "person_rules_temp";
    public static final String TABLE_NFSAPOSDATA_TEMP = "table_nfsa_pos_data_temp";

    public static final String TABLE_TG_CLOSING_BALANCE ="table_closing_balance";
    /*
    // Users table with username and passwordHash
    public static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY ,gen_code VARCHAR(20)," + FPSDBConstants.KEY_USERS_NAME + " VARCHAR(150) NOT NULL UNIQUE," + FPSDBConstants.KEY_USERS_ID + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_USERS_PASS_HASH + " VARCHAR(150)," + FPSDBConstants.KEY_USERS_FPS_ID + " VARCHAR(30),"
            + FPSDBConstants.KEY_USERS_CONTACT_PERSON + "  VARCHAR(30)," + FPSDBConstants.KEY_USERS_PHONE_NUMBER + " VARCHAR(15), "
            + FPSDBConstants.KEY_USERS_ADDRESS_LINE1 + " VARCHAR(60)," + FPSDBConstants.KEY_USERS_ADDRESS_LINE2 + " VARCHAR(60),"
            + "village_name VARCHAR(30),taluk_name VARCHAR(30),district_name VARCHAR(30),device_sim_no VARCHAR(30),agency_name VARCHAR(30),agency_code VARCHAR(30),"
            + "operation_closing_time VARCHAR(30),operation_opening_time VARCHAR(30),village_code VARCHAR(30),taluk_code VARCHAR(30),district_code VARCHAR(30),last_login_time VARCHAR(30),"
            + "fps_category VARCHAR(30),fps_type VARCHAR(30),encrypted_password VARCHAR(300),"
            + FPSDBConstants.KEY_USERS_ADDRESS_LINE3 + " VARCHAR(30), " + FPSDBConstants.KEY_USERS_ENTITLE_CLASSIFICATION + " VARCHAR(30), " + FPSDBConstants.KEY_USERS_PROFILE + " VARCHAR(150),"
            + FPSDBConstants.KEY_USERS_DISTRICT_ID + " INTEGER," + FPSDBConstants.KEY_USERS_TALUK_ID + " INTEGER, "
            + FPSDBConstants.KEY_USERS_VILLAGE_ID + " INTEGER," + FPSDBConstants.KEY_USERS_IS_ACTIVE + " INTEGER," + FPSDBConstants.KEY_USERS_CODE +
             " VARCHAR(15), is_user_active INTEGER)";
  */
    // Users table with username and passwordHash
    public static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + KEY_ID + " INTEGER PRIMARY KEY ,gen_code VARCHAR(20)," + FPSDBConstants.KEY_USERS_NAME + " VARCHAR(150) NOT NULL UNIQUE," + FPSDBConstants.KEY_USERS_ID + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_USERS_PASS_HASH + " VARCHAR(150)," + FPSDBConstants.KEY_USERS_FPS_ID + " VARCHAR(30),"
            + FPSDBConstants.KEY_USERS_CONTACT_PERSON + "  VARCHAR(30)," + FPSDBConstants.KEY_USERS_PHONE_NUMBER + " VARCHAR(15), "
            + FPSDBConstants.KEY_USERS_ADDRESS_LINE1 + " VARCHAR(60)," + FPSDBConstants.KEY_USERS_ADDRESS_LINE2 + " VARCHAR(60),"
            + "village_name VARCHAR(30),taluk_name VARCHAR(30),district_name VARCHAR(30),device_sim_no VARCHAR(30),agency_name VARCHAR(30),agency_code VARCHAR(30),"
            + "operation_closing_time VARCHAR(30),operation_opening_time VARCHAR(30),village_code VARCHAR(30),taluk_code VARCHAR(30),district_code VARCHAR(30),last_login_time VARCHAR(30),"
            + "fps_category VARCHAR(30),fps_type VARCHAR(30),encrypted_password VARCHAR(300),"
            + FPSDBConstants.KEY_USERS_ADDRESS_LINE3 + " VARCHAR(30), " + FPSDBConstants.KEY_USERS_ENTITLE_CLASSIFICATION + " VARCHAR(30), " + FPSDBConstants.KEY_USERS_PROFILE + " VARCHAR(150),"
            + FPSDBConstants.KEY_USERS_DISTRICT_ID + " INTEGER," + FPSDBConstants.KEY_USERS_TALUK_ID + " INTEGER, "
            + FPSDBConstants.KEY_USERS_VILLAGE_ID + " INTEGER," + FPSDBConstants.KEY_USERS_IS_ACTIVE + " INTEGER," + FPSDBConstants.KEY_USERS_CODE +
            " VARCHAR(15), is_user_active INTEGER, " +
            "createdDate VARCHAR(150) " +
            ")";
    // Products  table with unique product name and unique product code
    public static final String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_PRODUCT_NAME + " VARCHAR(150) NOT NULL UNIQUE,"
            + FPSDBConstants.KEY_PRODUCT_PRICE + " DOUBLE NOT NULL," + FPSDBConstants.KEY_PRODUCT_UNIT + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_PRODUCT_CODE + " VARCHAR(150) NOT NULL UNIQUE,isDeleted INTEGER,groupId INTEGER," + FPSDBConstants.KEY_LPRODUCT_UNIT + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_LPRODUCT_NAME + " VARCHAR(250) NOT NULL UNIQUE," + FPSDBConstants.KEY_NEGATIVE_INDICATOR + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_PRODUCT_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_MODIFIED_BY + " VARCHAR(150)," + FPSDBConstants.KEY_CREATED_DATE + " INTEGER,"
            + FPSDBConstants.KEY_CREATED_BY + " VARCHAR(150)" + ")";
    // Entitlement Master  table
    public static final String CREATE_OFFLINE_CARD_ACTIVATION = "CREATE TABLE " + TABLE_OFFLINE_ACTIVATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,aRegister INTEGER NOT NULL," +
            FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_ADULT_NO + " INTEGER NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_CHILD_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " VARCHAR(20) ," + FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID + " VARCHAR(10),"
            + FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " INTEGER NOT NULL)";
    public static final String CREATE_TABLE_LOGIN_HISTORY = "CREATE TABLE " + TABLE_LOGIN_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,login_time VARCHAR(60),login_type VARCHAR(50),user_id INTEGER,logout_time VARCHAR(60),logout_type VARCHAR(50)," +
            "fps_id INTEGER,transaction_id VARCHAR(50),created_time INTEGER,is_sync INTEGER,is_logout_sync INTEGER)";
    // Entitlement Master  table
    public static final String CREATE_ENTITLEMENT_RULES_TABLE = "CREATE TABLE " + TABLE_ENTITLEMENT_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER ,"
            + FPSDBConstants.KEY_RULES_CARD_TYPE + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_IS_PERSON + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_SPECIAL_OVERRIDE + " INTEGER NOT NULL," + FPSDBConstants.KEY_SPECIAL_MINIMUM + " INTEGER NOT NULL," + FPSDBConstants.KEY_RULES_IS_CALC + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_RULES_IS_REGION + " INTEGER NOT NULL,isDeleted INTEGER,groupId INTEGER," + FPSDBConstants.KEY_RULES_HAS_SPECIAL + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_RULES_QUANTITY + " INTEGER NOT NULL)";
    // Entitlement Master  table
    public static final String CREATE_SPECIAL_RULES_TABLE = "CREATE TABLE " + TABLE_SPECIAL_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER ," + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_DISTRICT + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_TALUK + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_VILLAGE + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_MUNICIPALITY + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_IS_ADD + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_CITY_HEAD + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_QUANTITY + " DOUBLE ,isDeleted INTEGER,groupId INTEGER,"
            + FPSDBConstants.KEY_SPECIAL_CYLINDER + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_IS_TALUK + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_CITY + " INTEGER , " +
            " hill_area INTEGER , " +
            " spl_area INTEGER , " +
            " town_panchayat INTEGER , " +
            " village_panchayat INTEGER )";

/* db.execSQL("alter table special_rules add column hill_area INTEGER");
                        db.execSQL("alter table special_rules add column spl_area INTEGER");
                        db.execSQL("alter table special_rules add column town_panchayat INTEGER");
                        db.execSQL("alter table special_rules add column village_panchayat INTEGER");*/
    // Entitlement Master  table
    public static final String CREATE_SPECIAL_RULES_TABLE_TEMP = "CREATE TABLE special_rules_temp" + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER ," + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_DISTRICT + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_TALUK + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_VILLAGE + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_MUNICIPALITY + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_IS_ADD + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_CITY_HEAD + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_QUANTITY + " DOUBLE ,isDeleted INTEGER,groupId INTEGER,"
            + FPSDBConstants.KEY_SPECIAL_CYLINDER + " INTEGER ," + FPSDBConstants.KEY_SPECIAL_IS_TALUK + " INTEGER ,"
            + FPSDBConstants.KEY_SPECIAL_IS_CITY + " INTEGER  , " +
            " hill_area INTEGER , " +
            " spl_area INTEGER , " +
            " town_panchayat INTEGER , " +
            " village_panchayat INTEGER )";
    // Entitlement Master  table
    public static final String CREATE_PERSON_RULES_TABLE = "CREATE TABLE " + TABLE_PERSON_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_PERSON_MIN + " DOUBLE NOT NULL,isDeleted INTEGER,groupId INTEGER," + FPSDBConstants.KEY_PERSON_MAX + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_CHILD + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_ADULT + " DOUBLE NOT NULL, card_type_id INTEGER)";
    // Close Sale Master  table
    public static final String CREATE_TABLE_PRODUCT_GROUP = "CREATE TABLE " + TABLE_PRODUCT_GROUP + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,group_id INTEGER,name VARCHAR(100),product_id INTEGER,is_deleted INTEGER,UNIQUE (product_id,group_id)  ON CONFLICT REPLACE) ";
    // Close Sale Master  table
    public static final String CREATE_TABLE_PRODUCT_OVERRIDE = "CREATE TABLE " + TABLE_PRODUCT_PRICE_OVERRIDE + "("
            + KEY_ID + " INTEGER PRIMARY KEY ,card_type VARCHAR(3),card_type_id INTEGER,percentage DOUBLE,product_id INTEGER,is_deleted INTEGER,UNIQUE (product_id,card_type_id)  ON CONFLICT REPLACE)";
    // Entitlement Master  table
    public static final String CREATE_REGION_RULES_TABLE = "CREATE TABLE " + TABLE_REGION_RULES + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NULL,"
            + FPSDBConstants.KEY_PERSON_CYLINDER + " INTEGER NOT NULL," + FPSDBConstants.KEY_PERSON_QUANTITY + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_TALUK + " INTEGER NOT NULL,isDeleted INTEGER,groupId INTEGER,"
            + FPSDBConstants.KEY_PERSON_CITY + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_PERSON_MUNICIPALITY + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_PERSON_HEAD + " INTEGER NOT NULL)";
    public static final String CREATE_BENEFICIARY_TABLE = "CREATE TABLE " + TABLE_BENEFICIARY + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(50),aRegister INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_TIN + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_FPS_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + " VARCHAR(150) UNIQUE," + FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_ADULT_NO + " INTEGER NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_CHILD_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " VARCHAR(20) UNIQUE ," + FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_TALUK_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID + " VARCHAR(10)," + FPSDBConstants.KEY_BENEFICIARY_STATE_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " INTEGER NOT NULL,aadharNumber VARCHAR(30)" + ")";
    public static final String CREATE_BENEFICIARY_TABLE_IN = "CREATE TABLE " + TABLE_BENEFICIARY_IN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(50),aRegister INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_TIN + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_FPS_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + " VARCHAR(150) UNIQUE," + FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_ADULT_NO + " INTEGER NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_CHILD_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " VARCHAR(20) UNIQUE ," + FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_TALUK_ID + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID + " VARCHAR(10)," + FPSDBConstants.KEY_BENEFICIARY_STATE_ID + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " INTEGER NOT NULL,aadharNumber VARCHAR(30)" + ")";
    // Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    public static final String CREATE_BENEFICIARY_REQ_TABLE = "CREATE TABLE " + TABLE_REG_REQ + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + " VARCHAR(150),aRegister VARCHAR(10),"
            + FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO + " INTEGER NOT NULL,reqTime INTEGER," +
            FPSDBConstants.KEY_BENEFICIARY_ADULT_NO + " INTEGER NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_CHILD_NO + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_REGISTRATION_MOB + " VARCHAR(20) ," + FPSDBConstants.KEY_REGISTRATION_FPS_ID + " INTEGER," + FPSDBConstants.KEY_REGISTRATION_RTIME + " VARCHAR(50),"
            + "channel VARCHAR(10)," + FPSDBConstants.KEY_ALLOTMENT_CARD_TYPE + " VARCHAR(5)," + FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED + " INTEGER)";
    // REGISTRATION  table with unique bill item id, Quantity
    public static final String CREATE_REGISTRATION_TABLE = "CREATE TABLE " + TABLE_REGISTRATION + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_REGISTRATION_DESC + " VARCHAR(150),"
            + FPSDBConstants.KEY_REGISTRATION_STATUS + " VARCHAR(1)," + FPSDBConstants.KEY_REGISTRATION_CARD_NO + " VARCHAR(30) NOT NULL,channel VARCHAR(10), "
            + FPSDBConstants.KEY_REGISTRATION_RMN + " VARCHAR(30)," + FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO + " VARCHAR(20) UNIQUE ,"
            + FPSDBConstants.KEY_REGISTRATION_TIME + " VARCHAR(50),reqTime INTEGER,aRegister INTEGER,"
            + FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED + " INTEGER)";
    // Beneficiary MEMBER
    public static final String CREATE_BENEFICIARY_MEMBER_TABLE = "CREATE TABLE " + TABLE_BENEFICIARY_MEMBER + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN + " VARCHAR(150), "
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME + " VARCHAR(100),"
            + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(100) ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FIRST_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MIDDLE_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LAST_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER + " VARCHAR(1)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PERMANENT_ADDRESS + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TEMP_ADDRESS + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY + " VARCHAR(150), " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_DATE + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_BY + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_RESIDENT_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB + " VARCHAR(30) ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB_TYPE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + " INTEGER ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT + " INTEGER," + "is_removed INTEGER," + "removed_date INTEGER" + ")";
    // Beneficiary MEMBER
    public static final String CREATE_BENEFICIARY_MEMBER_TABLE_IN = "CREATE TABLE " + TABLE_BENEFICIARY_MEMBER_IN + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN + " VARCHAR(150), "
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME + " VARCHAR(100),"
            + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(100) ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FIRST_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MIDDLE_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LAST_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER + " VARCHAR(1)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PERMANENT_ADDRESS + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_TEMP_ADDRESS + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_BY + " VARCHAR(150), " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_CREATED_DATE + " INTEGER,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_BY + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MODIFIED_DATE + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_RESIDENT_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB + " VARCHAR(30) ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB_TYPE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID + " VARCHAR(1),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4 + " VARCHAR(150)," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5 + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE + " VARCHAR(150),"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED + " INTEGER," + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + " INTEGER ,"
            + FPSDBConstants.KEY_BENEFICIARY_MEMBER_IS_ADULT + " INTEGER " + ")";
    // card type table with card types
    public static final String CREATE_CARD_TABLE = "CREATE TABLE " + TABLE_CARD_TYPE + "(" + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,"
            + FPSDBConstants.KEY_CARD_TYPE + " VARCHAR(1) NOT NULL UNIQUE," + FPSDBConstants.KEY_CARD_DESCRIPTION + " VARCHAR(150)  UNIQUE, isDeleted INTEGER, localDescription VARCHAR(150), display_sequence INTEGER)";
    // card type table with card types
    public static final String CREATE_MASTER_TABLE = "CREATE TABLE " + TABLE_CONFIG_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,value VARCHAR(150)  UNIQUE" + " )";
    // card type table with card types
    public static final String CREATE_SYNC_MASTER_TABLE = "CREATE TABLE  master_first_sync (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + "name VARCHAR(50) NOT NULL UNIQUE,value VARCHAR(20)" + " )";
    // Stock  table with unique bill item id, Quantity
    public static final String CREATE_STOCK_TABLE = "CREATE TABLE " + TABLE_STOCK + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_STOCK_FPS_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_STOCK_PRODUCT_ID + " INTEGER NOT NULL UNIQUE," + FPSDBConstants.KEY_STOCK_QUANTITY + " DOUBLE NOT NULL, "
            + FPSDBConstants.KEY_STOCK_REORDER_LEVEL + " DOUBLE, " + FPSDBConstants.KEY_STOCK_EMAIL_ACTION + " INTEGER,"
            + FPSDBConstants.KEY_STOCK_SMS_ACTION + " INTEGER )";
    public static final String CREATE_FPS_STOCK_INWARD_TABLE = "CREATE TABLE " + TABLE_FPS_STOCK_INWARD + "("
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY + " DOUBLE NOT NULL, " + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE_OLD + " VARCHAR(100), " + FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT + " VARCHAR(150),godown_name VARCHAR(100),godown_code VARCHAR(100),"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO + " INTEGER NOT NULL,referenceNo VARCHAR(150)," + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS + " INTEGER(1),is_server_add INTEGER(1)," +
            FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY + " DOUBLE ,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID + " INTEGER, _id INTEGER UNIQUE,challanId VARCHAR(30),vehicleN0 VARCHAR(20),driverName VARCHAR(20)," +
            "month INTEGER,year INTEGER,transportName VARCHAR(20),driverMobileNumber VARCHAR(20), inwardType VARCHAR(20))";
    public static final String CREATE_FPS_ADVANCE_STOCK_INWARD_TABLE = "CREATE TABLE " + TABLE_FPS_ADVANCE_STOCK_INWARD + "("
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE + " INTEGER NOT NULL," + FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY + " DOUBLE NOT NULL, " + FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT + " VARCHAR(150),godown_name VARCHAR(100),godown_code VARCHAR(100),"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO + " INTEGER NOT NULL,referenceNo VARCHAR(150)," + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS + " INTEGER(1)," +
            FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE + " INTEGER," + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY + " DOUBLE ," + "month INTEGER,year INTEGER,isAdded INTEGER,"
            + FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY + " INTEGER,_id INTEGER UNIQUE," + FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID + " INTEGER" + ",advanceStockId INTEGER UNIQUE,syncStatus INTEGER )";
    // Beneficiary  table with unique UFC code, FPS id ,QRCode  and unique mobile number
    public static final String CREATE_BILL_TABLE = "CREATE TABLE " + TABLE_BILL + "("
            + FPSDBConstants.KEY_BILL_REF_ID + " INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_BILL_SERVER_ID + " INTEGER," + FPSDBConstants.KEY_BILL_FPS_ID + " INTEGER ,"
            + FPSDBConstants.KEY_BILL_DATE + " INTEGER NOT NULL," + FPSDBConstants.KEY_BILL_TRANSACTION_ID + " VARCHAR(150) NOT NULL UNIQUE,"
            + FPSDBConstants.KEY_BILL_SERVER_REF_ID + " INTEGER UNIQUE," + FPSDBConstants.KEY_BILL_MODE + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BENEFICIARY_UFC + " VARCHAR(100),"
            + FPSDBConstants.KEY_BILL_CHANNEL + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BILL_STATUS + " VARCHAR(1) NOT NULL," + FPSDBConstants.KEY_BILL_BENEFICIARY + " INTEGER,"
            + FPSDBConstants.KEY_BILL_AMOUNT + " DOUBLE NOT NULL," + FPSDBConstants.KEY_BILL_TIME_MONTH
            + " INTEGER," + FPSDBConstants.KEY_BILL_CREATED_BY + " VARCHAR(150),otpId INTEGER, otpTime VARCHAR(150)," + FPSDBConstants.KEY_BILL_CREATED_DATE + " INTEGER " + ")";
    // Bill item  table with unique bill item id, Quantity ,bill item cost  and transmitted ir not
    public static final String CREATE_BILL_ITEM_TABLE = "CREATE TABLE " + TABLE_BILL_ITEM + "(" +
            FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + " INTEGER NOT NULL," + FPSDBConstants.KEY_BILL_TRANSACTION_ID + " VARCHAR(150) NOT NULL,"
            + FPSDBConstants.KEY_BILL_ITEM_DATE + " VARCHAR(150)," + FPSDBConstants.KEY_BILL_TIME_MONTH + " INTEGER ," + FPSDBConstants.KEY_BILL_BENEFICIARY + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_BILL_ITEM_QUANTITY + " DOUBLE NOT NULL," + FPSDBConstants.KEY_BILL_ITEM_COST + "  DOUBLE NOT NULL,totalCost  DOUBLE NOT NULL," +
            " PRIMARY KEY (" + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + "," + FPSDBConstants.KEY_BILL_TRANSACTION_ID + ")" + ")";
    public static final String CREATE_TABLE_LANGUAGE = "CREATE TABLE " + TABLE_LANGUAGE + "("
            + KEY_ID + " INTEGER  PRIMARY KEY AUTOINCREMENT  NOT NULL," + FPSDBConstants.KEY_LANGUAGE_CODE + " INTEGER UNIQUE,"
            + FPSDBConstants.KEY_LANGUAGE_L_MESSAGE + "  VARCHAR(1000)," + FPSDBConstants.KEY_LANGUAGE_MESSAGE + " VARCHAR(1000) )";
/*
    public static final String CREATE_TABLE_CARD_IMAGES = "CREATE TABLE " + TABLE_CARD_IMAGE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,card_number VARCHAR(50) UNIQUE,image_id VARCHAR(100),status VARCHAR(2))";*/
    public static final String CREATE_TABLE_SMS_PROVIDER = "CREATE TABLE " + TABLE_SMS_PROVIDER + "("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL," + FPSDBConstants.KEY_SMS_PROVIDER_NAME + " VARCHAR(50),"
            + FPSDBConstants.KEY_SMS_PROVIDER_NUMBER + " VARCHAR(100) UNIQUE,status VARCHAR(2)," + FPSDBConstants.KEY_SMS_PROVIDER_PREFIX + " VARCHAR(20),"
            + FPSDBConstants.KEY_SMS_PROVIDER_PREFERENCE + " VARCHAR(20))";
    public static final String CREATE_TABLE_UPGRADE = "CREATE TABLE if not exists " + TABLE_UPGRADE + "(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,ref_id VARCHAR(30),android_old_version INTEGER,android_new_version INTEGER,"
            + "state VARCHAR(30),description VARCHAR(250),status VARCHAR(20),refer_id VARCHAR(30),created_date VARCHAR(30),server_status INTEGER,execution_time VARCHAR(30))";
    public static final String CREATE_TABLE_FPS_STOCK_HISTORY = "CREATE TABLE " + TABLE_STOCK_HISTORY + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + FPSDBConstants.KEY_STOCK_HISTORY_PRODUCT_ID + " INTEGER,"
            + FPSDBConstants.KEY_STOCK_HISTORY_DATE + " INTEGER,"
            + FPSDBConstants.KEY_STOCK_DATE + " VARCHAR(100),"
            + FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE + " DOUBLE," + FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE + " DOUBLE,"
            + FPSDBConstants.KEY_STOCK_HISTORY_CHANGE_BALANCE + " DOUBLE,"
            + FPSDBConstants.KEY_STOCK_HISTORY_ACTION + " VARCHAR(100)" + ")";
    public static final String CREATE_TABLE_FPS_CLOSE_SALE = "CREATE TABLE IF NOT EXISTS close_sale_product("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,productId INTEGER,dateOfTxn INTEGER,transactionId INTEGER,totalCost VARCHAR(10),totalQuantity VARCHAR(10),"
            + FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE + " DOUBLE," + FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE + " DOUBLE,inward DOUBLE" + ")";
    public static final String CREATE_TABLE_FPS_CLOSE_SALE_PRODUCT = "CREATE TABLE IF NOT EXISTS close_sale("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,numofTrans INTEGER,transactionId INTEGER,totalSaleCost DOUBLE,dateOfTxn INTEGER,"
            + "isServerAdded INTEGER" + ")";
    public static final String CREATE_TABLE_ROLE_FEATURE = "CREATE TABLE " + TABLE_ROLE_FEATURE + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,isDeleted INTEGER," + FPSDBConstants.KEY_ROLE_USERID + " INTEGER,role_id INTEGER," +
            FPSDBConstants.KEY_ROLE_FEATUREID + " INTEGER NOT NULL," + FPSDBConstants.KEY_ROLE_NAME + " VARCHAR(150)," + FPSDBConstants.KEY_ROLE_PARENTID + " VARCHAR(150)," +
            FPSDBConstants.KEY_ROLE_TYPE + " VARCHAR(150),UNIQUE (" + FPSDBConstants.KEY_ROLE_USERID + ",role_id) ON CONFLICT REPLACE) ";
    public static final String CREATE_FPS_ADVANCE_MIGRATION_IN_TABLE = "CREATE TABLE " + TABLE_FPS_MIGRATION_IN + "("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,ration_card_number VARCHAR(150),beneficiary_id INTEGER,a_register_no  VARCHAR(150),ufc_code  VARCHAR(150) UNIQUE,month_in INTEGER,year_in INTEGER,isAdded INTEGER)";
    public static final String CREATE_FPS_ADVANCE_MIGRATION_OUT_TABLE = "CREATE TABLE " + TABLE_FPS_MIGRATION_OUT + "("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,beneficiary_id INTEGER UNIQUE,month_out INTEGER,year_out INTEGER,isAdded INTEGER)";
    public static final String CREATE_FPS_ADVANCE_ADJUST_TABLE = "CREATE TABLE fps_stock_adjustment ("
            + KEY_ID + " INTEGER PRIMARY KEY NOT NULL,product_id INTEGER ,quantity DOUBLE,dateOfAck VARCHAR(150),requestType VARCHAR(20),isServerAdded INTEGER,isAdjusted INTEGER, referenceNo VARCHAR(100), createdDate VARCHAR(150), godownReferenceNo VARCHAR(100))";
    // members aadhar numbers table
    public static final String CREATE_TABLE_MEMBERS_AADHAR = "CREATE TABLE IF NOT EXISTS " + TABLE_MEMBERS_AADHAR + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,beneficiary_id INTEGER, aadhar_number INTEGER, beneficiary_member_id INTEGER) ";
    // regularSyncException table
    public static final String CREATE_TABLE_SYNC_EXCEPTION = "CREATE TABLE IF NOT EXISTS syncException ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, fpsId VARCHAR(150), syncMode VARCHAR(150), tableName VARCHAR(150), action VARCHAR(150), recordId VARCHAR(150), lastSyncTime VARCHAR(150), rawData VARCHAR(150), errorDescription VARCHAR(150), isSynced VARCHAR(150)) ";
    public static final String CREATE_TABLE_BIFURCATION = "CREATE TABLE IF NOT EXISTS Bifurcation ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, id INTEGER, oldFpsId INTEGER, newFpsId INTEGER, benefId INTEGER, createdById INTEGER, createdDate INTEGER, status INTEGER)";
    public static final String CREATE_TABLE_BIOMETRIC_AUTHENTICATION = "CREATE TABLE IF NOT EXISTS biometric_authentication ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, uid VARCHAR(150), fpsId INTEGER, benefId INTEGER, authResponse VARCHAR(150), authStatus VARCHAR(150), posReqDate INTEGER, posRespDate INTEGER, authReqDate INTEGER, authRespDate INTEGER, fingerPrintData BLOB, syncStatus INTEGER)";


    public static final String CREATE_TABLE_BFD_DETAILS = "CREATE TABLE IF NOT EXISTS bfd_details ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, server_id INTEGER UNIQUE, benef_id INTEGER, proxy_id INTEGER, finger_01 VARCHAR(150), finger_02 VARCHAR(150), finger_03 VARCHAR(150), finger_04 VARCHAR(150), finger_05 VARCHAR(150), finger_06 VARCHAR(150), finger_07 VARCHAR(150), finger_08 VARCHAR(150), finger_09 VARCHAR(150), finger_10 VARCHAR(150), created_date INTEGER, created_by INTEGER, modified_date INTEGER, modified_by INTEGER, status INTEGER, sync_status VARCHAR(150))";

    public static final String CREATE_TABLE_PROXY_DETAILS = "CREATE TABLE IF NOT EXISTS proxy_details ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, server_id INTEGER UNIQUE, benef_id INTEGER, benef_member_id INTEGER, name VARCHAR(150), uid VARCHAR(150), dob VARCHAR(150), mobile VARCHAR(150), created_date INTEGER, created_by INTEGER, modified_date INTEGER, modified_by INTEGER, approval_status VARCHAR(150), sync_status VARCHAR(150))";

    public static final String CREATE_TABLE_KYC_REQUEST_DETAILS = "CREATE TABLE IF NOT EXISTS kyc_request_details ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, fingerPrintData BLOB, aadharNumber VARCHAR(150), benefId VARCHAR(150), ackStatus INTEGER, created_date INTEGER, created_by INTEGER, modified_date INTEGER, modified_by INTEGER)";




   /* public static final String CREATE_TABLE_RELATIONSHIP = "CREATE TABLE IF NOT EXISTS relationship ("
            + KEY_ID + " INTEGER, name VARCHAR(150), local_name VARCHAR(150), modified_date INTEGER, modified_by INTEGER, created_date INTEGER, created_by INTEGER, status INTEGER)";
*/
    /**
     * 15-07-2016
     * MSWork
     * Created table for shop operating timing
     */
    public static final String CREATE_TABLE_POSOPERATINGHOURS = "CREATE TABLE IF NOT EXISTS pos_operating_hours ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "serialVersionUID VARCHAR(150), " +
            "createdDate INTEGER, " +
            "createdBy INTEGER, " +
            "modifiedDate INTEGER, " +
            "modifiedBy INTEGER, " +
            "applicationType VARCHAR(50), " +
            "entityId INTEGER, " +
            "day VARCHAR(50), " +
            "firstSessionOpeningTime VARCHAR(50), " +
            "firstSessionClosingTime VARCHAR(50), " +
            "secondSessionOpeningTime VARCHAR(50), " +
            "secondSessionClosingTime VARCHAR(50), " +
            "talukId INTEGER, " +
            "entityGeneratedCode VARCHAR(50) " +
            ")";
    public static final String CREATE_TABLE_INSPECTION_CRITERIA = "CREATE TABLE if not exists inspection_criteria ( "
            + " criteria_id  INTEGER PRIMARY KEY ," + " criteria VARCHAR(150)" + ")";
    public static final String CREATE_TABLE_INSPECTION_STOCK = "CREATE TABLE inspection_stock ( _id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL, client_reportId  INTEGER, product_id  INTEGER, stock_pos_system INTEGER, actual_fps_system  INTEGER, variance  INTEGER, remarks VARCHAR(150), status VARCHAR(150), transaction_id VARCHAR(50))";
    public static final String CREATE_INSPECTION_REPORT = "CREATE TABLE if not exists inspection_report ( "
            + InspectionConstants.KEY_INSPECTION_CLIENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL ,"
            + InspectionConstants.KEY_INSPECTION_ID + " INTEGER ,"
            + InspectionConstants.KEY_INSPECTION_USERNAME + " VARCHAR(150) ,"
            + " userId " + " INTEGER ,"
            + InspectionConstants.KEY_DATE_OF_INSPECTION + " INTEGER ,"
            + InspectionConstants.KEY_TYPE_OF_INSPECTION + " VARCHAR(15) ,"
            + InspectionConstants.KEY_INSPECTOR_NAME + " VARCHAR(150) ,"
            + InspectionConstants.KEY_INSPECTOR_DESIGNATION + " VARCHAR(50) ,"
            + InspectionConstants.KEY_INSPECTOR_DEPARTMENT + " VARCHAR(50) ,"
            + InspectionConstants.KEY_INSPECTOR_EMPLOYEE_ID + " VARCHAR(50) ,"
            + InspectionConstants.KEY_INSPECTION_STATE + " VARCHAR(100) ,"
            + InspectionConstants.KEY_INSPECTION_DISTRICT + " VARCHAR(100) ,"
            + InspectionConstants.KEY_INSPECTION_TALUK + " VARCHAR(100) ,"
            + InspectionConstants.KEY_INSPECTION_VILLAGE + " VARCHAR(100) ,"
            + "districtId " + " INTEGER ,"
            + "talukId " + " INTEGER ,"
            + "villageId " + " INTEGER ,"
            + "stateId " + " INTEGER ,"
            + InspectionConstants.KEY_INSPECTION_PLACE + " VARCHAR(20) ,"
            + InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_CODE + " VARCHAR(150) ,"
            + InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_NAME + " VARCHAR(150) ,"
            + InspectionConstants.KEY_INSPECTION_FPS_ID + " INTEGER ,"
            + InspectionConstants.KEY_INSPECTION_GODOWN_ID + " INTEGER ,"
            + InspectionConstants.KEY_FPS_OR_GODOWN_INCHARGE_NAME + " VARCHAR(150) ,"
            + InspectionConstants.KEY_FPS_OR_GODOWN_ADDRESS1 + " VARCHAR(150) ,"
//            + InspectionConstants.KEY_FPS_OR_GODOWN_ADDRESS2 + " VARCHAR(150) ,"
//            + InspectionConstants.KEY_FPS_OR_GODOWN_ADDRESS3 + " VARCHAR(150) ,"
            + InspectionConstants.KEY_IS_TRANSEFERED + " VARCHAR(10) ,"
            + InspectionConstants.KEY_AREA_OFFICER + " VARCHAR(150) ,"
            + InspectionConstants.KEY_INSPECTION_REPORT_RETRY_COUNT + " INTEGER DEFAULT 0 ,"
            + InspectionConstants.KEY_INSPECTION_REPORT_RETRY_TIME + " INTEGER ,"
            + InspectionConstants.KEY_OVERALL_COMMANDS + " VARCHAR(150) ,"
            + InspectionConstants.KEY_IS_INSPECTION_COMPLETED + " INTEGER ,"
            + InspectionConstants.KEY_SCHEDULED_LIST_ID + " INTEGER,"
            + InspectionConstants.KEY_OVERALL_STATUS + " VARCHAR(150)" + ", fineAmount DOUBLE, transaction_id VARCHAR(50), fps_ack_status VARCHAR(50), fps_ack_sync_status INTEGER)";
    protected static final String CREATE_TABLE_CARD_VERIFICATION = "CREATE TABLE if not exists " + TABLE_CARD_VERIFICATION + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " client_reportId  INTEGER," + " product_id  INTEGER," + " card_number  VARCHAR(150)," + " fpsId  INTEGER," + " commodity_issued_pos INTEGER," + " commodity_entered_card  INTEGER, " + "variance  INTEGER," + " remarks VARCHAR(150)," + " status VARCHAR(150)" + ")";
    public static final String CREATE_TABLE_BACKGROUND_PROCESS_HISTORY = "CREATE TABLE if not exists backgroundProcessHistory ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " requestData  VARCHAR(50), responseData  VARCHAR(50), requestDateTime INTEGER, responseDateTime INTEGER, errorDescription VARCHAR(50), serviceType VARCHAR(50), status VARCHAR(50)" + ")";
    protected static final String CREATE_TABLE_WEIGHMENT = "CREATE TABLE if not exists " + TABLE_WEIGHMENT + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " client_reportId  INTEGER," + " bill_number  VARCHAR(300)," + " fpsId  INTEGER," + " card_number  VARCHAR(150)," + " product_id  INTEGER," + " sold_quantity INTEGER," + " observed_quantity  INTEGER, " + "variance  INTEGER," + " remarks VARCHAR(150)," + " status VARCHAR(150)" + ")";
    public static final String CREATE_TABLE_RECONCILIATION = "CREATE TABLE if not exists reconciliation ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " requestData  VARCHAR(50), responseData  VARCHAR(50), requestDateTime INTEGER, responseDateTime INTEGER, serverId INTEGER, reconciliationId INTEGER, errorDescription VARCHAR(50), status VARCHAR(50)" + ")";
    protected static final String CREATE_TABLE_OTHERS = "CREATE TABLE if not exists " + TABLE_OTHERS + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " client_reportId  INTEGER," + " remarks VARCHAR(150)," + " status VARCHAR(150)" + ")";
    protected static final String CREATE_TABLE_FPS_TIME = "CREATE TABLE if not exists " + TABLE_FPS_TIME + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + " client_reportId  INTEGER," + " remarks VARCHAR(150)," + " status VARCHAR(150)" + ")";
    public static final String CREATE_STOCK_ALLOCATION_TABLE = "CREATE TABLE if not exists " + FPSDBTables.TABLE_STOCK_ALLOCATION + "(id INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL,"
            + "group_id INTEGER NOT NULL, group_name VARCHAR(256), allocated_qty DOUBLE, advance_qty DOUBLE, current_qty DOUBLE, total_qty DOUBLE, month VARCHAR(256), year INTEGER, unit VARCHAR(256), localUnit VARCHAR(256))";

    public static final String CREATE_TABLE_NFSAPOSDATA = "CREATE TABLE if not exists " + FPSDBTables.TABLE_NFSAPOSDATA + "(beneficiary_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, card_type VARCHAR(256), head_of_family_name VARCHAR(256), l_head_of_family_name VARCHAR(256), is_deleted INTEGER, nfsaStatus INTEGER)";

    public static final String CREATE_PERSON_RULES_TABLE_TEMP = "CREATE TABLE if not exists " + TABLE_PERSON_RULES_TEMP + "("
            + KEY_ID + " INTEGER PRIMARY KEY," + FPSDBConstants.KEY_RULES_PRODUCT_ID + " INTEGER NOT NULL,"
            + FPSDBConstants.KEY_PERSON_MIN + " DOUBLE NOT NULL,isDeleted INTEGER,groupId INTEGER," + FPSDBConstants.KEY_PERSON_MAX + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_CHILD + " DOUBLE NOT NULL,"
            + FPSDBConstants.KEY_PERSON_ADULT + " DOUBLE NOT NULL, card_type_id INTEGER)";

    public static final String CREATE_TABLE_NFSAPOSDATA_TEMP = "CREATE TABLE if not exists " + FPSDBTables.TABLE_NFSAPOSDATA_TEMP + "(beneficiary_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, card_type VARCHAR(256), head_of_family_name VARCHAR(256), l_head_of_family_name VARCHAR(256), is_deleted INTEGER, nfsaStatus INTEGER)";

    public static final String CREATE_TABLE_CLOSING_BALANCE ="CREATE TABLE if not exists " +FPSDBTables.TABLE_TG_CLOSING_BALANCE +"(code INTEGER PRIMARY KEY NOT NULL, name VARCHAR(256), closing_balance DOUBLE NOT NULL,product_price DOUBLE NOT NULL)";
}
