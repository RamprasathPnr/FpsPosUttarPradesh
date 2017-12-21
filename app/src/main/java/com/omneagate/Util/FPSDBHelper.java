package com.omneagate.Util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.omneagate.DTO.AadharSeedingDto;
import com.omneagate.DTO.AppfeatureDto;
import com.omneagate.DTO.BFDDetailDto;
import com.omneagate.DTO.BackgroundServiceDto;
import com.omneagate.DTO.BenefActivNewDto;
import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.DTO.BeneficiaryRegistrationData;
import com.omneagate.DTO.BeneficiarySearchDto;
import com.omneagate.DTO.BeneficiaryUpdateDto;
import com.omneagate.DTO.BifurcationHistoryDto;
import com.omneagate.DTO.BillDto;
import com.omneagate.DTO.BillInbetweenDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.CardInspectionDto;
import com.omneagate.DTO.CardTypeDto;
import com.omneagate.DTO.CloseOfProductDto;
import com.omneagate.DTO.CloseSaleTransactionDto;
import com.omneagate.DTO.EntitlementMasterRule;
import com.omneagate.DTO.EntitlementMasterRuleDtod;
import com.omneagate.DTO.EnumDTO.InspectionReportStatus;
import com.omneagate.DTO.EnumDTO.StockTransactionType;
import com.omneagate.DTO.FPSIndentRequestDto;
import com.omneagate.DTO.FPSMigrationDto;
import com.omneagate.DTO.FPSStockDto;
import com.omneagate.DTO.FPSStockHistoryCollectionDto;
import com.omneagate.DTO.FPSStockHistoryDto;
import com.omneagate.DTO.FailedKycDto;
import com.omneagate.DTO.FindingCriteriaDto;
import com.omneagate.DTO.FpsAdvanceStockDto;
import com.omneagate.DTO.FpsAllocationCommodityDetailDto;
import com.omneagate.DTO.FpsIntentReqProdDto;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.GodownStockOutwardDto;
import com.omneagate.DTO.GroupDto;
import com.omneagate.DTO.InspectionCriteriaDto;
import com.omneagate.DTO.InspectionReportDto;
import com.omneagate.DTO.LoginHistoryDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.MessageDto;
import com.omneagate.DTO.NfsaPosDataDto;
import com.omneagate.DTO.POSAadharAuthRequestDto;
import com.omneagate.DTO.POSOperatingHoursDto;
import com.omneagate.DTO.POSStockAdjustmentDto;
import com.omneagate.DTO.POSSyncExceptionDto;
import com.omneagate.DTO.PersonBasedRule;
import com.omneagate.DTO.Product;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.ProductPriceOverrideDto;
import com.omneagate.DTO.ProxyDetailDto;
import com.omneagate.DTO.RationcardSummaryDto;
import com.omneagate.DTO.ReconciliationRequestDto;
import com.omneagate.DTO.ReconciliationStockDto;
import com.omneagate.DTO.RegionBasedRule;
import com.omneagate.DTO.RelationshipDto;
import com.omneagate.DTO.RoleFeatureDto;
import com.omneagate.DTO.ShopAndOthersDto;
import com.omneagate.DTO.SmsProviderDto;
import com.omneagate.DTO.SplEntitlementRule;
import com.omneagate.DTO.StockInspectionDto;
import com.omneagate.DTO.StockRequestDto;
import com.omneagate.DTO.UnitwiseSummaryDto;
import com.omneagate.DTO.UpgradeDetailsDto;
import com.omneagate.DTO.UserDetailDto;
import com.omneagate.DTO.UserDto.BillItemProductDto;
import com.omneagate.DTO.UserDto.BillUserDto;
import com.omneagate.DTO.UserDto.MigrationOutDTO;
import com.omneagate.DTO.UserDto.StockCheckDto;
import com.omneagate.DTO.UserDto.UserFistSyncDto;
import com.omneagate.DTO.VersionDto;
import com.omneagate.DTO.WeighmentInspectionDto;
import com.omneagate.Util.Constants.FPSDBConstants;
import com.omneagate.activity.BaseActivity;
import com.omneagate.activity.GlobalAppState;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static android.content.Context.MODE_PRIVATE;
import static com.omneagate.Util.FPSDBTables.TABLE_PERSON_RULES;
import static com.omneagate.activity.SplashActivity.context;

/**
 * FPS database Helper
 */
public class FPSDBHelper extends SQLiteOpenHelper {
    // Database Name
    public static final String DATABASE_NAME = "FPS.db";
    //Key for id in tables
    public final static String KEY_ID = "_id";
    // Database Version
//    private static final int DATABASE_VERSION = 27 ;
    private static final int DATABASE_VERSION = 2;

    // All Static variables
    private static com.omneagate.Util.FPSDBHelper dbHelper = null;
    private static SQLiteDatabase database = null;
    private static Context contextValue;
    String TAG = "FPSDBHelper";

    private static final String AFSC_CARD_TYPE = "4";
    private static final String FSC_CARD_TYPE = "5";
    private static final String AAP_CARD_TYPE = "9";

    private static final String RICEAAP_CODE = "106";
    private static final String RICEAFSC_CODE = "107";
    private static final String RICEFSC_CODE = "108";
    private static final String RICE_CODE = "114";

    public FPSDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = this.getWritableDatabase();
        dbHelper = this;
        contextValue = context;
    }

    // Singleton to Instantiate the SQLiteOpenHelper
    public static com.omneagate.Util.FPSDBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new com.omneagate.Util.FPSDBHelper(context);
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

    String tableType = "";
    Long childRecordId = null;
    BillItemProductDto billItemProductDto1 = null;
    BeneficiaryMemberDto beneficiaryMemberDto1 = null;
    CloseSaleTransactionDto closeSaleTransactionDto1 = null;

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.e("Inside DB", "DB Creation");
            db.execSQL(FPSDBTables.CREATE_USERS_TABLE);
            db.execSQL(FPSDBTables.CREATE_PRODUCTS_TABLE);
            db.execSQL(FPSDBTables.CREATE_BENEFICIARY_TABLE);
            db.execSQL(FPSDBTables.CREATE_BENEFICIARY_MEMBER_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_SMS_PROVIDER);
            db.execSQL(FPSDBTables.CREATE_CARD_TABLE);
            db.execSQL(FPSDBTables.CREATE_BILL_ITEM_TABLE);
            db.execSQL(FPSDBTables.CREATE_STOCK_TABLE);
            db.execSQL(FPSDBTables.CREATE_MASTER_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_UPGRADE);
            db.execSQL(FPSDBTables.CREATE_REGISTRATION_TABLE);
            db.execSQL(FPSDBTables.CREATE_OFFLINE_CARD_ACTIVATION);
            db.execSQL(FPSDBTables.CREATE_ENTITLEMENT_RULES_TABLE);
            db.execSQL(FPSDBTables.CREATE_PERSON_RULES_TABLE);
            db.execSQL(FPSDBTables.CREATE_REGION_RULES_TABLE);
            db.execSQL(FPSDBTables.CREATE_BILL_TABLE);
            db.execSQL(FPSDBTables.CREATE_SPECIAL_RULES_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_LANGUAGE);
            db.execSQL(FPSDBTables.CREATE_BENEFICIARY_REQ_TABLE);
            db.execSQL(FPSDBTables.CREATE_FPS_STOCK_INWARD_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_FPS_STOCK_HISTORY);
            db.execSQL(FPSDBTables.CREATE_TABLE_LOGIN_HISTORY);
            db.execSQL(FPSDBTables.CREATE_TABLE_ROLE_FEATURE);
            db.execSQL(FPSDBTables.CREATE_TABLE_PRODUCT_OVERRIDE);
            db.execSQL(FPSDBTables.CREATE_TABLE_PRODUCT_GROUP);
            db.execSQL(FPSDBTables.CREATE_FPS_ADVANCE_STOCK_INWARD_TABLE);
            db.execSQL(FPSDBTables.CREATE_BENEFICIARY_MEMBER_TABLE_IN);
            db.execSQL(FPSDBTables.CREATE_BENEFICIARY_TABLE_IN);
            db.execSQL(FPSDBTables.CREATE_FPS_ADVANCE_MIGRATION_IN_TABLE);
            db.execSQL(FPSDBTables.CREATE_SYNC_MASTER_TABLE);
            db.execSQL(FPSDBTables.CREATE_FPS_ADVANCE_MIGRATION_OUT_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_FPS_CLOSE_SALE_PRODUCT);
            db.execSQL(FPSDBTables.CREATE_TABLE_FPS_CLOSE_SALE);
            db.execSQL(FPSDBTables.CREATE_FPS_ADVANCE_ADJUST_TABLE);
            db.execSQL("Create index beneficiary_ufc on beneficiary (ufc_code)");
            db.execSQL("Create index beneficiary_register on beneficiary (aRegister)");
            db.execSQL("Create index beneficiary_ration_card on beneficiary (old_ration_card_num)");
            db.execSQL(FPSDBTables.CREATE_TABLE_MEMBERS_AADHAR);
            db.execSQL(FPSDBTables.CREATE_TABLE_SYNC_EXCEPTION);
            db.execSQL(FPSDBTables.CREATE_TABLE_BIFURCATION);
            db.execSQL(FPSDBTables.CREATE_TABLE_BIOMETRIC_AUTHENTICATION);
            db.execSQL(FPSDBTables.CREATE_TABLE_BFD_DETAILS);
            db.execSQL(FPSDBTables.CREATE_TABLE_PROXY_DETAILS);
            db.execSQL(FPSDBTables.CREATE_TABLE_KYC_REQUEST_DETAILS);




//            db.execSQL("alter table person_rules add column card_type_id INTEGER");
            db.execSQL("alter table region_rules add column hill_area INTEGER");
            db.execSQL("alter table region_rules add column spl_area INTEGER");
            db.execSQL("alter table region_rules add column town_panchayat INTEGER");
            db.execSQL("alter table region_rules add column village_panchayat INTEGER");
            db.execSQL("alter table beneficiary_member add column beneficiary_id INTEGER");
            db.execSQL(FPSDBTables.CREATE_TABLE_POSOPERATINGHOURS);
            db.execSQL(FPSDBTables.CREATE_TABLE_INSPECTION_CRITERIA);
            db.execSQL(FPSDBTables.CREATE_INSPECTION_REPORT);
            db.execSQL(FPSDBTables.CREATE_TABLE_INSPECTION_STOCK);
            db.execSQL(FPSDBTables.CREATE_TABLE_CARD_VERIFICATION);
            db.execSQL(FPSDBTables.CREATE_TABLE_WEIGHMENT);
            db.execSQL(FPSDBTables.CREATE_TABLE_OTHERS);
            db.execSQL(FPSDBTables.CREATE_TABLE_FPS_TIME);
            db.execSQL("alter table products add column sequenceNo INTEGER");
            db.execSQL(FPSDBTables.CREATE_TABLE_BACKGROUND_PROCESS_HISTORY);
            db.execSQL(FPSDBTables.CREATE_TABLE_RECONCILIATION);
            db.execSQL(FPSDBTables.CREATE_STOCK_ALLOCATION_TABLE);
            db.execSQL(FPSDBTables.CREATE_TABLE_NFSAPOSDATA);
            db.execSQL("alter table migration_in add column blocked_date INTEGER");
            db.execSQL("alter table migration_in add column migrated_date INTEGER");
            db.execSQL("alter table migration_out add column blocked_date INTEGER");
            db.execSQL("alter table migration_out add column migrated_date INTEGER");
            db.execSQL("alter table card_type add column isStatus INTEGER");


            db.execSQL(FPSDBTables.CREATE_TABLE_CLOSING_BALANCE);
        }
        catch(Exception e) {
            Log.e("db helper oncreate", "exception...." + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            Log.e(TAG, "onUpgrade..." + "oldVersion..." + oldVersion + " , " + "newVersion..." + newVersion);
            switch (oldVersion) {
                case 1:
                    newVersion = 2;
                    try {db.execSQL("alter table beneficiary_member add column is_removed INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table beneficiary_member add column removed_date INTEGER");} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_POSOPERATINGHOURS);} catch (Exception e) {}
                    try {db.execSQL("alter table person_rules add column card_type_id INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table region_rules add column hill_area INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table region_rules add column spl_area INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table region_rules add column town_panchayat INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table region_rules add column village_panchayat INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table special_rules add column hill_area INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table special_rules add column spl_area INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table special_rules add column town_panchayat INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table special_rules add column village_panchayat INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table beneficiary_member add column beneficiary_id INTEGER");} catch (Exception e) {}
                    try {db.execSQL("Update users set entitlement_classification = 'District Head Quarter' where entitlement_classification = 'Head Quarter'");} catch (Exception e) {}
                    try {db.execSQL("Update users set entitlement_classification = 'Village Panchayats' where entitlement_classification = 'Village Panchayat'");} catch (Exception e) {}
                    try {db.execSQL("Update users set entitlement_classification = 'Municipality' where entitlement_classification = 'Other Districts' OR entitlement_classification = 'Belt Area'");} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_SPECIAL_RULES_TABLE_TEMP);} catch (Exception e) {}
                    try {db.execSQL("INSERT INTO special_rules_temp SELECT * FROM special_rules");} catch (Exception e) {}
                    try {db.execSQL("drop table if exists special_rules");} catch (Exception e) {}
                    try {db.execSQL("INSERT" + " INTO special_rules SELECT * FROM special_rules_temp");} catch (Exception e) {}
                    try {db.execSQL("drop table if exists special_rules_temp");} catch (Exception e) {}
                    try {db.execSQL("alter table users add column createdDate VARCHAR(150)");} catch (Exception e) {}
                    try {db.execSQL("alter table advance_stock_inward add column advanceStockId INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table advance_stock_inward add column syncStatus INTEGER");} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_INSPECTION_CRITERIA);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_INSPECTION_REPORT);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_INSPECTION_STOCK);} catch (Exception e) {}
                    try {db.execSQL("alter table products add column sequenceNo INTEGER");} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_BACKGROUND_PROCESS_HISTORY);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_FPS_TIME);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_CARD_VERIFICATION);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_WEIGHMENT);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_OTHERS);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_RECONCILIATION);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_STOCK_ALLOCATION_TABLE);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_NFSAPOSDATA);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_PERSON_RULES_TABLE_TEMP);} catch (Exception e) {}
                    try {db.execSQL("Insert into " + FPSDBTables.TABLE_PERSON_RULES_TEMP + " select * from " + FPSDBTables.TABLE_PERSON_RULES);} catch (Exception e) {}
                    try {db.execSQL("drop table if exists " + TABLE_PERSON_RULES);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_PERSON_RULES_TABLE);} catch (Exception e) {}
                    try {db.execSQL("Insert into " + FPSDBTables.TABLE_PERSON_RULES + " select * from " + FPSDBTables.TABLE_PERSON_RULES_TEMP);} catch (Exception e) {}
                    try {db.execSQL("drop table if exists " + FPSDBTables.TABLE_PERSON_RULES_TEMP);} catch (Exception e) {}
                    try {FPSDBHelper.getInstance(context).updateMaserData("syncTime", "2016-11-01 00:00:00.000");} catch (Exception e) {}
                    try {db.execSQL("drop table if exists " + FPSDBTables.TABLE_NFSAPOSDATA);} catch (Exception e) {}
                    try {db.execSQL(FPSDBTables.CREATE_TABLE_NFSAPOSDATA);} catch (Exception e) {}
                    try {FPSDBHelper.getInstance(context).updateMaserData("syncTime", "2016-11-01 00:00:00.000");} catch (Exception e) {}
                    try {db.execSQL("alter table migration_in add column blocked_date INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table migration_in add column migrated_date INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table migration_out add column blocked_date INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table migration_out add column migrated_date INTEGER");} catch (Exception e) {}
                    try {db.execSQL("alter table card_type add column isStatus INTEGER");} catch (Exception e) {}

                default:
                    break;
            }

            SharedPreferences sharedpreferences;
            if (BaseActivity.globalContext == null) {
                Log.e("Null", "Context null");
            }
            sharedpreferences = BaseActivity.globalContext.getSharedPreferences("DBData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt("version", newVersion);
            editor.apply();
        } catch (Exception e) {
            Log.e("Upgrade Error", e.toString(), e);
        }
    }

    public void specialTableTempCreation() {
        try {
            Util.LoggingQueue(contextValue, "FPSDBHelper ", "specialTableTempCreation called ");
            database.execSQL(FPSDBTables.CREATE_SPECIAL_RULES_TABLE_TEMP);
            try {
                database.execSQL("INSERT INTO special_rules_temp SELECT * FROM special_rules;");
            } catch (Exception e) {
                Util.LoggingQueue(contextValue, "FPSDBHelper ", "special_rules_temp insertion err " + e.toString());
            }
            database.execSQL("drop table if exists special_rules");
            database.execSQL(FPSDBTables.CREATE_SPECIAL_RULES_TABLE);
            database.execSQL("INSERT INTO special_rules SELECT * FROM special_rules_temp;");
            // database.execSQL("drop table if exists special_rules_temp");
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "FPSDBHelper ", "special_rules_temp Excweption  " + e.toString());
        }
    }

    private void userTableValues(SQLiteDatabase db) {
        if (!isTableExists(db, "temp_InwardValue")) {
            db.execSQL("CREATE TABLE temp_InwardValue AS SELECT * FROM stock_inward");
            db.execSQL("drop table if exists roll");
            db.execSQL("drop table if exists stock_inward");
            db.execSQL(FPSDBTables.CREATE_FPS_STOCK_INWARD_TABLE);
            db.execSQL(FPSDBTables.CREATE_SYNC_MASTER_TABLE);
        }
    }

    private void checkTable(SQLiteDatabase db) {
        if (!isTableExists(db, "fps_stock_adjustment")) {
            db.execSQL(FPSDBTables.CREATE_FPS_ADVANCE_ADJUST_TABLE);
        }
        db.execSQL("alter table beneficiary_in add column aadharNumber VARCHAR(30)");
        db.execSQL("drop table if exists indent_request_product");
        db.execSQL("drop table if exists bill_item_offline");
        db.execSQL("drop table if exists lpg_provider");
        db.execSQL("drop table if exists offline_bill");
        db.execSQL("drop table if exists stock_allotment_details");
        db.execSQL("drop table if exists manual_stock_inward");
        db.execSQL("drop table if exists manual_stock_inward_product");
        db.execSQL("drop table if exists ration_card_images");
    }

    //This function loads data to language table
    public void insertErrorMessages(MessageDto message) {
        try {
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_LANGUAGE_CODE, message.getLanguageCode());
            values.put(FPSDBConstants.KEY_LANGUAGE_MESSAGE, message.getDescription());
            values.put(FPSDBConstants.KEY_LANGUAGE_L_MESSAGE, message.getLocalDescription());
            database.insertWithOnConflict(FPSDBTables.TABLE_LANGUAGE, "code", values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e("Error Message", e.toString(), e);
        }
    }

    public long insertBackgroundProcessHistory(String requestData, String serviceType) {
        long id = -1;
        try {
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put("requestData", requestData);
            values.put("requestDateTime", date.getTime());
            values.put("serviceType", serviceType);
            id = database.insert("backgroundProcessHistory", null, values);
        } catch (Exception e) {
            Log.e("Error Message", e.toString(), e);
        }
        return id;
    }

    public void updateBackgroundProcessHistory(String responseData, String errorDesc, String status, long primaryId) {
        try {
            Date date = new Date();
            ContentValues values = new ContentValues();
            values.put("responseData", responseData);
            values.put("responseDateTime", date.getTime());
            values.put("errorDescription", errorDesc);
            values.put("status", status);
            database.update("backgroundProcessHistory", values, "_id = " + primaryId, null);
        } catch (Exception e) {
            Log.e("Error Message", e.toString(), e);
        }
    }

    // This function inserts details to FPSDBTables.TABLE_FPS_STOCK_INWARD
    public boolean insertFpsStockInwardDetails(Set<GodownStockOutwardDto> fpsStoInwardDto, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        GodownStockOutwardDto fpsStoInwardDto1 = null;
        if (!fpsStoInwardDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (GodownStockOutwardDto fpsStockInward : fpsStoInwardDto) {
                    recordId = fpsStockInward.getId();
                    fpsStoInwardDto1 = fpsStockInward;
                    ContentValues values = new ContentValues();
                    if (fpsStockInward.getId() != null)
                        values.put("_id", fpsStockInward.getId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, fpsStockInward.getGodownId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, fpsStockInward.getFpsId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE, fpsStockInward.getOutwardDate());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, fpsStockInward.getProductId());
                    Double qty4 = Double.parseDouble(Util.quantityRoundOffFormat(fpsStockInward.getQuantity()));
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, qty4);
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT, fpsStockInward.getUnit());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO, fpsStockInward.getBatchno());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, fpsStockInward.getDeliveryChallanId());
                    values.put("godown_name", fpsStockInward.getGodownName());
                    values.put("godown_code", fpsStockInward.getGodownCode());
                    values.put("referenceNo", fpsStockInward.getReferenceNo());
                    if (fpsStockInward.getVehicleN0() != null)
                        values.put("vehicleN0", fpsStockInward.getVehicleN0());
                    if (fpsStockInward.getDeliveryChallanId() != null)
                        values.put("challanId", fpsStockInward.getDeliveryChallanId());
                    if (fpsStockInward.getDriverName() != null)
                        values.put("driverName", fpsStockInward.getDriverName());
                    if (fpsStockInward.getTransportName() != null)
                        values.put("transportName", fpsStockInward.getTransportName());
                    if (fpsStockInward.getDriverMobileNumber() != null)
                        values.put("driverMobileNumber", fpsStockInward.getDriverMobileNumber());
                    if (fpsStockInward.getInwardType() != null)
                        values.put("inwardType", fpsStockInward.getInwardType());
                    if (!fpsStockInward.isFpsAckStatus()) {
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 0);
                    } else {
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
                    }
                    if (fpsStockInward.getFpsAckDate() != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date(fpsStockInward.getFpsAckDate());
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
                    }
                    if (fpsStockInward.getFpsReceiQuantity() != null) {
                        Double qty5 = Double.parseDouble(Util.quantityRoundOffFormat(fpsStockInward.getFpsReceiQuantity()));
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, qty5);
                    } else
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, 0);
                    values.put("month", fpsStockInward.getMonth());
                    values.put("year", fpsStockInward.getYear());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY, fpsStockInward.getCreatedby());
                    database.insertWithOnConflict(FPSDBTables.TABLE_FPS_STOCK_INWARD, "_id", values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_FPS_STOCK_INWARD Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(fpsStoInwardDto1);
                    insertSyncException("TABLE_FPS_STOCK_INWARD", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public void setStockInspectionTable(StockInspectionDto stockInspection, long clientReportId) {
        try {
            ContentValues values = new ContentValues();
            values.put("client_reportId", clientReportId);
            values.put("product_id", stockInspection.getCommodity());
            values.put("stock_pos_system", stockInspection.getPosStock());
            values.put("actual_fps_system", stockInspection.getActualStock());
            values.put("variance", stockInspection.getVariance());
            values.put("remarks", stockInspection.getRemarks());
            values.put("status", "R");
            values.put("transaction_id", stockInspection.getTransactionId());
            database.insert("inspection_stock", null, values);
                /*if(stockInspection.getPhotoPathList()!=null){
                    setImageTable("Stock",stockInspection.getPhotoPathList(),getStockTableId());
                    Log.e("Working","WorkingStock");
                }*/
        } catch (Exception e) {
            Log.e("setStockInspection exc", e.toString());
        }
    }

    public void updateReportTransfered(InspectionReportDto inspectionReportDto) {
        ContentValues values = new ContentValues();
        values.put(InspectionConstants.KEY_INSPECTION_ID, inspectionReportDto.getId());
        values.put(InspectionConstants.KEY_IS_TRANSEFERED, "T");
        values.put(InspectionConstants.KEY_IS_INSPECTION_COMPLETED, 1);
        //values.put(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_COUNT, retryCount);
        values.put(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_TIME, System.currentTimeMillis());
        database.update("inspection_report", values, InspectionConstants.KEY_INSPECTION_CLIENT_ID + "=" + inspectionReportDto.getClientId(), null);
        /*if(inspectionReportDto.getScheduledInspectionId()!=null){
            updateScheduledList(inspectionReportDto.getScheduledInspectionId());
        }*/
        /*if(inspectionReportDto.getClientId()!=null){
            updateCriteriaFindingInspectionReportId(inspectionReportDto);

        }*/
    }

    public void updateAckString(long clientIdVal, String ackStr) {
        ContentValues values = new ContentValues();
        values.put("fps_ack_status", ackStr);
        database.update("inspection_report", values, "clientId = " + clientIdVal, null);
    }

    public void updateFpsAckSyncStatus(long clientIdVal) {
        ContentValues values = new ContentValues();
        values.put("fps_ack_sync_status", 1);
        database.update("inspection_report", values, "clientId = " + clientIdVal, null);
    }

    public long getReportId(long clientReportId) {
        long lastId = 0l;
        try {
            String query = "SELECT inspectionId from inspection_report where clientId=" + clientReportId;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery(query, null);
            if (c != null && c.moveToFirst()) {
                lastId = c.getLong(0); //The 0 is the column index, we only have 1 column, so the index is 0
                Log.e("DBOffline_ReportId", "" + c.getLong(0));
            }
        } catch (Exception e) {
            Log.e("Exception", "Inspection Report Id  for Card Verification Table " + e.toString());
        }
        return lastId;
    }

    public void updateStockTable(long stockTableId) {
        try {
            ContentValues values = new ContentValues();
            values.put("status", "T");
            database.update("inspection_stock", values, "_id" + "=" + stockTableId, null);
        } catch (Exception e) {
            Log.e("Error", "Image Stock Table Updating" + e.toString());
        }
    }

    public void updateCardVerificationTable(long cardTableId) {
        try {
            ContentValues values = new ContentValues();
            values.put("status", "T");
            database.update(FPSDBTables.TABLE_CARD_VERIFICATION, values, "_id" + "=" + cardTableId, null);
        } catch (Exception e) {
            Log.e("Error", "Image Card Table Updating" + e.toString());
        }
    }

    public void insertReportList(InspectionReportDto inspectionReportDto, String transmittedOrNot) {
        Log.e("inspectionL", inspectionReportDto.toString());
        ContentValues values = new ContentValues();
        values.put(InspectionConstants.KEY_INSPECTION_USERNAME, inspectionReportDto.getUserName());
        values.put(InspectionConstants.KEY_INSPECTION_ID, inspectionReportDto.getId());
        values.put("userId", inspectionReportDto.getUserId());
        values.put(InspectionConstants.KEY_INSPECTION_FPS_ID, inspectionReportDto.getFpsId());
        values.put(InspectionConstants.KEY_INSPECTION_GODOWN_ID, inspectionReportDto.getGodownId());
        values.put(InspectionConstants.KEY_INSPECTOR_NAME, inspectionReportDto.getInspectorName());
        values.put(InspectionConstants.KEY_INSPECTOR_DESIGNATION, inspectionReportDto.getDesignation());
        values.put(InspectionConstants.KEY_INSPECTOR_DEPARTMENT, inspectionReportDto.getDepartment());
        values.put(InspectionConstants.KEY_INSPECTOR_EMPLOYEE_ID, inspectionReportDto.getEmployeeId());
        values.put(InspectionConstants.KEY_INSPECTION_STATE, inspectionReportDto.getState());
        values.put("stateId", inspectionReportDto.getStateId());
        values.put("districtId", inspectionReportDto.getDistrictId());
        values.put("talukId", inspectionReportDto.getTalukId());
        values.put("villageId", inspectionReportDto.getVillageId());
        values.put(InspectionConstants.KEY_INSPECTION_DISTRICT, inspectionReportDto.getDistrict());
        values.put(InspectionConstants.KEY_INSPECTION_TALUK, inspectionReportDto.getTaluk());
        values.put(InspectionConstants.KEY_INSPECTION_PLACE, inspectionReportDto.getInspectionPlace());
        values.put(InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_CODE, inspectionReportDto.getCode());
        values.put(InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_NAME, inspectionReportDto.getFpsOrGodownName());
        values.put(InspectionConstants.KEY_FPS_OR_GODOWN_INCHARGE_NAME, inspectionReportDto.getInchargeName());
        values.put(InspectionConstants.KEY_FPS_OR_GODOWN_ADDRESS1, inspectionReportDto.getMailingAddress());
        values.put(InspectionConstants.KEY_AREA_OFFICER, inspectionReportDto.getAreaOfficer());
        values.put(InspectionConstants.KEY_OVERALL_COMMANDS, inspectionReportDto.getOverAllComments());
        values.put(InspectionConstants.KEY_OVERALL_STATUS, Boolean.toString(inspectionReportDto.getOverAllStatus()));
        values.put(InspectionConstants.KEY_TYPE_OF_INSPECTION, Boolean.toString(inspectionReportDto.getTypeOfInspection()));

         /* Log.e("overallstaus",""+inspectionReportDto.getOverAllStatus());
          if(inspectionReportDto.getOverAllStatus()){
              values.put(InspectionConstants.KEY_OVERALL_STATUS , 1);
          }else{
              values.put(InspectionConstants.KEY_OVERALL_STATUS , 0);
          }*/
        values.put(InspectionConstants.KEY_DATE_OF_INSPECTION, inspectionReportDto.getDateOfInspection());
        // values.put(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_COUNT , inspectionReportDto.getRetryCount());//By Default it is set to zero
        values.put(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_TIME, inspectionReportDto.getRetryTime());
        values.put(InspectionConstants.KEY_IS_TRANSEFERED, transmittedOrNot);//Not Transfered to Server
        values.put(InspectionConstants.KEY_IS_INSPECTION_COMPLETED, 1);//Completed for both Schedule or unscheduled but not transfered to server
        /*if(inspectionReportDto.getScheduledInspectionId()!=null){
            values.put(InspectionConstants.KEY_SCHEDULED_LIST_ID,inspectionReportDto.getScheduledInspectionId());
            updateScheduledList(inspectionReportDto.getScheduledInspectionId());
        }*/
        values.put("fineAmount", inspectionReportDto.getFineAmount());
        values.put("transaction_id", inspectionReportDto.getTransactionId());
        values.put("fps_ack_status", InspectionReportStatus.PENDING.toString());
        values.put("fps_ack_sync_status", 0);
        database.insert("inspection_report", null, values);
        setCriteriaType(inspectionReportDto.getFindingCriteriaDto(), getLastInsertedReportClientId().getClientId());
    }

    public List<StockInspectionDto> getAllStockImagesView(long clientId) {
        List<StockInspectionDto> listStockInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM inspection_stock where client_reportId=" + clientId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                StockInspectionDto stockInspectionDto = new StockInspectionDto(cursor);
//                if (getReportId(stockInspectionDto.getClientReportId()) != 0) {
                stockInspectionDto.setReportId(getReportId(stockInspectionDto.getClientReportId()));
//                }
                /*if(getBase64CardImages(stockInspectionDto.getId(),"stock_id")!=null){
                    List<String> listImages = getBase64CardImages(stockInspectionDto.getId(), "stock_id");
                    stockInspectionDto.setImages(listImages);//List of Images from Image Table
                }*/
                listStockInspectionDto.add(stockInspectionDto);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Image Table" + e.toString());
        }
        return listStockInspectionDto;
    }

    public void updateWeighmentTable(long weighmentId) {
        try {
            ContentValues values = new ContentValues();
            values.put("status", "T");
            database.update(FPSDBTables.TABLE_WEIGHMENT, values, "_id" + "=" + weighmentId, null);
        } catch (Exception e) {
            Log.e("Error", "Image Weighment Table Updating" + e.toString());
        }
    }

    public void updateShopAndOtherTable(String cType, long shopId) {
        String tableName = null;
        try {
            if (cType.equalsIgnoreCase("FpsTime")) {
                tableName = FPSDBTables.TABLE_FPS_TIME;
            } else {
                tableName = FPSDBTables.TABLE_OTHERS;
            }
            ContentValues values = new ContentValues();
            values.put("status", "T");
            database.update(tableName, values, "_id" + "=" + shopId, null);
        } catch (Exception e) {
            Log.e("Error", "Image " + tableName + " Table Updating" + e.toString());
        }
    }

    public void setCriteriaType(FindingCriteriaDto findingCriteriaDto, long clientReportId) {
//        for(FindingCriteriaDto findingCriteriaDto:listFindingCriteriaDto.getFindingList()) {
        if (findingCriteriaDto.getCardInspection().size() > 0) {
            for (CardInspectionDto cardInspectionDto : findingCriteriaDto.getCardInspection()) {
                setCardInspectionTable(cardInspectionDto, clientReportId);
            }
        }
        if (findingCriteriaDto.getStockInspection().size() > 0) {
            for (StockInspectionDto stockInspectionDto : findingCriteriaDto.getStockInspection()) {
                setStockInspectionTable(stockInspectionDto, clientReportId);
            }
        }
        if (findingCriteriaDto.getWeighmentInspection().size() > 0) {
            for (WeighmentInspectionDto weighmentInspectionDto : findingCriteriaDto.getWeighmentInspection()) {
                setWeighmentInspectionTable(weighmentInspectionDto, clientReportId);
            }
        }
        if (findingCriteriaDto.getOtherInsection().size() > 0) {
            for (ShopAndOthersDto shopAndOthersDto : findingCriteriaDto.getOtherInsection()) {
                setOthersInpsection(shopAndOthersDto, clientReportId);
            }
        }
        if (findingCriteriaDto.getShopInpsection().size() > 0) {
            for (ShopAndOthersDto shopAndOthersDto : findingCriteriaDto.getShopInpsection()) {
                setShopandInpsection(shopAndOthersDto, clientReportId);
            }
        }
//        }
    }

    private void setShopandInpsection(ShopAndOthersDto shopdto, long clientReportId) {
        try {
            ContentValues values = new ContentValues();
            values.put("client_reportId", clientReportId);
            values.put("remarks", shopdto.getRemarks());
            values.put("status", "R");
            database.insert(FPSDBTables.TABLE_FPS_TIME, null, values);
        } catch (Exception e) {
            Log.e("Weighment_Error", e.toString());
        }
    }

    private void setOthersInpsection(ShopAndOthersDto othersdto, long clientReportId) {
        try {
            ContentValues values = new ContentValues();
            values.put("client_reportId", clientReportId);
            values.put("remarks", othersdto.getRemarks());
            values.put("status", "R");
            database.insert(FPSDBTables.TABLE_OTHERS, null, values);
        } catch (Exception e) {
            Log.e("Other_Error", e.toString());
        }
    }

    private void setWeighmentInspectionTable(WeighmentInspectionDto weightmentdto, long clientReportId) {
        try {
            ContentValues values = new ContentValues();
            values.put("client_reportId", clientReportId);
            values.put("bill_number", weightmentdto.getBillNumber());
            values.put("card_number", weightmentdto.getCardNo());
            values.put("fpsId", weightmentdto.getFpsId());
            values.put("product_id", weightmentdto.getCommodity());
            values.put("sold_quantity", weightmentdto.getSoldQuantity());
            values.put("observed_quantity", weightmentdto.getObservedQuantity());
            values.put("variance", weightmentdto.getVariance());
            values.put("remarks", weightmentdto.getRemarks());
            values.put("status", "R");
            database.insert(FPSDBTables.TABLE_WEIGHMENT, null, values);
        } catch (Exception e) {
            Log.e("Weighment_Error", e.toString());
        }
    }

    public void setCardInspectionTable(CardInspectionDto cardInspectionDto, long clientReportId) {
        try {
            ContentValues values = new ContentValues();
            values.put("client_reportId", clientReportId);
            values.put("card_number", cardInspectionDto.getCardNumber());
            values.put("fpsId", cardInspectionDto.getFpsId());
            values.put("product_id", cardInspectionDto.getCommodity());
            values.put("commodity_issued_pos", cardInspectionDto.getCommodityIssuedasperPos());
            values.put("commodity_entered_card", cardInspectionDto.getCommodityIssuedasperCard());
            values.put("variance", cardInspectionDto.getVariance());
            values.put("remarks", cardInspectionDto.getRemarks());
            values.put("status", "R");
            database.insert(FPSDBTables.TABLE_CARD_VERIFICATION, null, values);
        } catch (Exception e) {
            Log.e("CardinsertError", e.toString());
        }
    }

    public List<InspectionReportDto> getReportsUnsend() {
        List<InspectionReportDto> listInspectionReportDto = new ArrayList<InspectionReportDto>();
        String selectQuery = "SELECT  * FROM inspection_report where status ='N'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            InspectionReportDto inspectionReportDto = new InspectionReportDto(cursor);
            listInspectionReportDto.add(inspectionReportDto);
            cursor.moveToNext();
        }
        cursor.close();
        Log.e("OfflineListReportDto", listInspectionReportDto.toString());
        return listInspectionReportDto;
    }

    public List<InspectionReportDto> getReportsAckUnsend() {
        List<InspectionReportDto> listInspectionReportDto = new ArrayList<InspectionReportDto>();
        String selectQuery = "SELECT  * FROM inspection_report where fps_ack_sync_status = 0 and fps_ack_status != '" + InspectionReportStatus.PENDING.toString() + "' and inspectionId IS NOT NULL ";
        Log.e(TAG, "selectQuery..." + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            InspectionReportDto inspectionReportDto = new InspectionReportDto(cursor);
            listInspectionReportDto.add(inspectionReportDto);
            cursor.moveToNext();
        }
        cursor.close();
        Log.e("OfflineListReportDto", listInspectionReportDto.toString());
        return listInspectionReportDto;
    }

    public List<InspectionReportDto> getAllReports() {
        List<InspectionReportDto> listInspectionReportDto = new ArrayList<InspectionReportDto>();
        String selectQuery = "SELECT  * FROM inspection_report";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            InspectionReportDto inspectionReportDto = new InspectionReportDto(cursor);
            listInspectionReportDto.add(inspectionReportDto);
            cursor.moveToNext();
        }
        cursor.close();
        Log.e("OfflineListReportDto", listInspectionReportDto.toString());
        return listInspectionReportDto;
    }

    public InspectionReportDto retryCount(long id) {
        String selectQuery = "SELECT  * FROM  inspection_report where " + InspectionConstants.KEY_INSPECTION_CLIENT_ID + " = " + id;
        InspectionReportDto inspectionReportDto = null;
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("InspectionRSize", "" + cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            inspectionReportDto = new InspectionReportDto(cursor);
            cursor.moveToNext();
            Log.e("inspectionReportDto", inspectionReportDto.toString());
        }
        return inspectionReportDto;
    }

    public InspectionReportDto getLastInsertedReportClientId() {
        InspectionReportDto inspectionReportDto = null;
        try {
            String selectQuery = "SELECT * from inspection_report order by " + InspectionConstants.KEY_INSPECTION_CLIENT_ID + " DESC limit 1";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("LastInsertedReCSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                inspectionReportDto = new InspectionReportDto(cursor);
                cursor.moveToNext();
                Log.e("DB inspectionReportDto", inspectionReportDto.toString());
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Exception", "Db lastInsertedClientId" + " " + e.toString());
        }
        return inspectionReportDto;
    }

    public void removeFromStockInspectionTable(StockInspectionDto stockInspection) {
        try {
            database.delete("inspection_stock", "_id='" + stockInspection.getId() + "'", null);
        } catch (Exception e) {
            Log.e(TAG, "removeFromStockInspectionTable exc..." + e.toString());
        }
    }

    public void removeAllDataInStockInspectionTable() {
        try {
            database.execSQL("delete from inspection_stock");
        } catch (Exception e) {
            Log.e(TAG, "removeAllDataInStockInspectionTable exc..." + e.toString());
        }
    }

    public List<StockInspectionDto> getAllStockInspectionData() {
        List<StockInspectionDto> listStockInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM inspection_stock";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                StockInspectionDto stockInspectionDto = new StockInspectionDto(cursor);
                listStockInspectionDto.add(stockInspectionDto);
                /*if(getReportId(stockInspectionDto.getClientReportId())!=0) {
                    stockInspectionDto.setReportId(getReportId(stockInspectionDto.getClientReportId()));
                    if (getBase64CardImages(stockInspectionDto.getId(), "stock_id") != null) {
                        List<String> listImages = getBase64CardImages(stockInspectionDto.getId(), "stock_id");
                        stockInspectionDto.setImages(listImages);//List of Images from Image Table
                    }
                    listStockInspectionDto.add(stockInspectionDto);
                }*/
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("dbhelper", "getAllStockInspectionData exc..." + e.toString());
        }
        return listStockInspectionDto;
    }
    // This function inserts details to members aadhar
    /*public void insertMembersAadhar(Set<GodownStockOutwardDto> membersAadharDto) {
        if (!membersAadharDto.isEmpty()) {
            Log.e("1st sync", "members aadhar..." + membersAadharDto.toString());
            for (GodownStockOutwardDto membersAadhar : membersAadharDto) {
                ContentValues values = new ContentValues();
                try {
                    if (membersAadhar.getVehicleN0() != null)
                        values.put("_id", membersAadhar.getVehicleN0());
                    if (membersAadhar.getDeliveryChallanId() != null)
                        values.put("beneficiary_id", membersAadhar.getDeliveryChallanId());
                    if (membersAadhar.getDriverName() != null)
                        values.put("aadhar_number", membersAadhar.getDriverName());
                    database.insert(FPSDBTables.TABLE_MEMBERS_AADHAR, null, values);
                }
                catch(Exception e) {
                    Log.e("Table members aadhar", "Exception", e);
                }
            }
        }
    }*/

    public boolean insertAdvanceFpsStockSync(Set<FpsAdvanceStockDto> fpsAdvanceStockDtos, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        FpsAdvanceStockDto fpsAdvanceStockDtos1 = null;
        if (!fpsAdvanceStockDtos.isEmpty()) {
            database.beginTransaction();
            try {
                for (FpsAdvanceStockDto stocklist : fpsAdvanceStockDtos) {
                    recordId = stocklist.getId();
                    fpsAdvanceStockDtos1 = stocklist;
                    ContentValues values = new ContentValues();
                    Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "insertAdvanceFpsStockSync() FpsAdvanceStockDto = " + stocklist);
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, stocklist.getGodownStockOutwardDto().getGodownId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, stocklist.getFpsStoreDto().getId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE, stocklist.getGodownStockOutwardDto().getOutwardDate());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, stocklist.getProductDto().getId());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, stocklist.getQuantity());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT, stocklist.getGodownStockOutwardDto().getUnit());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO, stocklist.getGodownStockOutwardDto().getBatchno());
                    if (stocklist.getGodownStockOutwardDto().isFpsAckStatus()) {
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, "0");
                    } else {
                        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, "1");
                    }
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, stocklist.getGodownStockOutwardDto().getFpsAckDate());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, stocklist.getGodownStockOutwardDto().getFpsReceiQuantity());
                    values.put("month", stocklist.getMonth());
                    values.put("year", stocklist.getYear());
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, stocklist.getGodownStockOutwardDto().getDeliveryChallanId());
                    if (stocklist.isAddedToStock()) {
                        values.put("isAdded", 0);
                    } else {
                        values.put("isAdded", 1);
                    }
                    Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "insertAdvanceFpsStockSync() stocklist.getGodownStockOutwardDto() = " + stocklist.getGodownStockOutwardDto());
                    values.put("referenceNo", stocklist.getGodownStockOutwardDto().getReferenceNo());
                    values.put("godown_name", stocklist.getGodownStockOutwardDto().getGodownName());
                    values.put("godown_code", stocklist.getGodownStockOutwardDto().getGodownCode());
                    values.put("created_by", stocklist.getGodownStockOutwardDto().getCreatedby());
                    values.put("_id", stocklist.getGodownStockOutwardDto().getId());
                    values.put("advanceStockId", stocklist.getId());
                    values.put("syncStatus", 0);
                    database.insert(FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD, null, values);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_FPS_ADVANCE_STOCK_INWARD Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(fpsAdvanceStockDtos1);
                    insertSyncException("TABLE_FPS_ADVANCE_STOCK_INWARD", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }
    //This function inserts details to TABLE_CARD_TYPE.
    /*public boolean insertCardTypeData(Set<CardTypeDto> cardTypeDto) {
        boolean isSuccessFullyInserted = false;

        if (!cardTypeDto.isEmpty()) {
            for (CardTypeDto cardType : cardTypeDto) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, cardType.getId());
                    values.put(FPSDBConstants.KEY_CARD_TYPE, String.valueOf(cardType.getType()));
                    if (String.valueOf(cardType.getDescription()) != null) {
                        values.put(FPSDBConstants.KEY_CARD_DESCRIPTION, cardType.getDescription());
                    }

                    if (cardType.isDeleted()) {
                        values.put("isDeleted", 1);
                    } else {
                        values.put("isDeleted", 0);
                    }

                    try {
                        if (String.valueOf(cardType.getLdescription()) != null) {
                            Log.e("dbhelper", "local desc.." + cardType.getLdescription());
                            values.put("localDescription", cardType.getLdescription());
                        }
                    } catch (Exception e) {
                        values.put("localDescription", "");
                        Log.e("dbhelper", "local desc 2.." + cardType.getLdescription());
                    }
                    try {
                        if (String.valueOf(cardType.getDisplaySequence()) != null) {
                            values.put("display_sequence", cardType.getDisplaySequence());
                        }
                    } catch (Exception e) {
                        values.put("display_sequence", "");
                    }
                    Log.e("dbhelper", "values.." + values.toString());
                    database.insertWithOnConflict(FPSDBTables.TABLE_CARD_TYPE, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    isSuccessFullyInserted = true;
                }
                catch(Exception e) {
                    isSuccessFullyInserted = false;
                    com.omneagate.Util.Util.LoggingQueue(contextValue, "card_type Exception...", e.toString());
                    try {
                        POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                        posSyncExceptionDto.setSyncMode("REGULAR_SYNC");
                        posSyncExceptionDto.setTableName("TABLE_CARD_TYPE");
                        posSyncExceptionDto.setAction("INSERT");
                        posSyncExceptionDto.setRecordId(cardType.getId());
                        String json = new Gson().toJson(cardType);
                        posSyncExceptionDto.setRawData(json);
                        posSyncExceptionDto.setErrorDescription("Exception while inserting TABLE_CARD_TYPE");
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                        String dateStr = df.format(new Date());
                        Date currentDate = df.parse(dateStr);
                        posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                        FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
                        break;
                    }
                    catch(Exception e1) {
                        com.omneagate.Util.Util.LoggingQueue(contextValue, "card_type Exception 2...", e.toString());
                    }
                }
            }
        }
        return isSuccessFullyInserted;
    }*/

    // This function inserts details to TABLE_CARD_TYPE.
    public boolean insertCardTypeData(Set<CardTypeDto> cardTypeDto, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        CardTypeDto cardTypeDto1 = null;
        if (!cardTypeDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (CardTypeDto cardType : cardTypeDto) {
                    recordId = cardType.getId();
                    cardTypeDto1 = cardType;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, cardType.getId());
                    values.put(FPSDBConstants.KEY_CARD_TYPE, String.valueOf(cardType.getType()));
                    if (String.valueOf(cardType.getDescription()) != null) {
                        values.put(FPSDBConstants.KEY_CARD_DESCRIPTION, cardType.getDescription());
                    }
                    if (cardType.getIsDeleted()) {
                        values.put("isDeleted", 1);
                    } else {
                        values.put("isDeleted", 0);
                    }
                    if (cardType.isStatus()) {
                        values.put("isStatus", 1);
                    } else {
                        values.put("isStatus", 0);
                    }
                    try {
                        if (String.valueOf(cardType.getLdescription()) != null) {
                            values.put("localDescription", cardType.getLdescription());
                        }
                    } catch (Exception e) {
                        values.put("localDescription", "");
                    }
                    try {
                        if (String.valueOf(cardType.getDisplaySequence()) != null) {
                            values.put("display_sequence", cardType.getDisplaySequence());
                        }
                    } catch (Exception e) {
                        values.put("display_sequence", "");
                    }
                    database.insertWithOnConflict(FPSDBTables.TABLE_CARD_TYPE, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "card_type Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(cardTypeDto1);
                    insertSyncException("TABLE_CARD_TYPE", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public boolean insertSyncCriteria(Set<InspectionCriteriaDto> inspectionCriteriaDtoList, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        InspectionCriteriaDto inspectionCriteriaDto2 = null;
        if (!inspectionCriteriaDtoList.isEmpty()) {
            database.beginTransaction();
            try {
                for (InspectionCriteriaDto inspectionCriteriaDto : inspectionCriteriaDtoList) {
                    recordId = inspectionCriteriaDto.getId();
                    inspectionCriteriaDto2 = inspectionCriteriaDto;
                    Log.e("inspectionC", inspectionCriteriaDto.toString());
                    ContentValues values = new ContentValues();
                    values.put("criteria_id", inspectionCriteriaDto.getId());
                    values.put("criteria", inspectionCriteriaDto.getCriteria());
                    database.insertWithOnConflict("inspection_criteria", KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "inspection_criteria Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(inspectionCriteriaDto2);
                    insertSyncException("TABLE_CRITERIA", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public List<ProductDto> getProduct() {
        String selectQuery = "SELECT  * FROM  products where isDeleted = 0 ORDER BY name";
        List<ProductDto> getProductList = new ArrayList<ProductDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("cursorSize", "" + cursor.getCount());
        getProductList.add(new ProductDto());
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            getProductList.add(new ProductDto(cursor));
            cursor.moveToNext();
            Log.e("Product", getProductList.toString());
        }
        return getProductList;
    }

    public long retriveCriteriaId(String name) {
        long id = 0;
        String selectQuery = "SELECT  * FROM table_criteria where criteria" + "='" + name + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        Log.e("criteria count", count + "");
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getLong(cursor.getColumnIndex("criteria_id"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    // This function inserts details to TABLE_POSOPERATINGHOURS.
    public boolean insertPosOperatingHoursData(Set<POSOperatingHoursDto> posOperatingHoursDtoSet, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        POSOperatingHoursDto posOperatingHoursDtoSet1 = null;
        if (!posOperatingHoursDtoSet.isEmpty()) {
            database.beginTransaction();
            try {
                for (POSOperatingHoursDto posOperatingHoursDto : posOperatingHoursDtoSet) {
                    recordId = posOperatingHoursDto.getId();
                    posOperatingHoursDtoSet1 = posOperatingHoursDto;
                    ContentValues values = new ContentValues();
                    values.put("createdBy", posOperatingHoursDto.getCreatedBy());
                    values.put("createdDate", posOperatingHoursDto.getCreatedDate());
                    values.put("modifiedBy", posOperatingHoursDto.getModifiedBy());
                    values.put("modifiedDate", posOperatingHoursDto.getModifiedDate());
                    values.put("applicationType", posOperatingHoursDto.getApplicationType());
                    values.put("entityId", posOperatingHoursDto.getEntityId());
                    values.put("day", posOperatingHoursDto.getDay());
                    values.put("firstSessionOpeningTime", posOperatingHoursDto.getFirstSessionOpeningTime());
                    values.put("firstSessionClosingTime", posOperatingHoursDto.getFirstSessionClosingTime());
                    values.put("secondSessionOpeningTime", posOperatingHoursDto.getSecondSessionOpeningTime());
                    values.put("secondSessionClosingTime", posOperatingHoursDto.getSecondSessionClosingTime());
                    values.put("talukId", posOperatingHoursDto.getTalukId());
                    values.put("entityGeneratedCode", posOperatingHoursDto.getEntityGeneratedCode());
                    database.insertWithOnConflict(FPSDBTables.TABLE_POSOPERATINGHOURS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_POSOPERATINGHOURS Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(posOperatingHoursDtoSet1);
                    insertSyncException("TABLE_POSOPERATINGHOURS", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }
    //This function inserts details to TABLE_RELATIONSHIP.
    /*public void insertRelationshipData(Set<RelationshipDto> relationshipDtoSet) {
        ContentValues values = new ContentValues();
        if (!relationshipDtoSet.isEmpty()) {
            for (RelationshipDto relationshipDto : relationshipDtoSet) {
                try {
                    values.put(KEY_ID, relationshipDto.getId());
                    values.put("name", relationshipDto.getName());
                    values.put("local_name", relationshipDto.getLname());
                    values.put("modified_date", relationshipDto.getModifiedDate());
                    values.put("modified_by", relationshipDto.getModifiedBy());
                    values.put("created_date", relationshipDto.getCreatedDate());
                    values.put("created_by", relationshipDto.getCreatedBy());
                    if(relationshipDto.getStatus()) {
                        values.put("status", 1);
                    }
                    else {
                        values.put("status", 0);
                    }
                    database.insertWithOnConflict("relationship", KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                catch(Exception e) {
                    com.omneagate.Util.Util.LoggingQueue(contextValue, "relationship Exception...", e.toString());
                    try {
                        POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                        posSyncExceptionDto.setSyncMode("REGULAR_SYNC");
                        posSyncExceptionDto.setTableName("TABLE_RELATIONSHIP");
                        posSyncExceptionDto.setAction("INSERT");
                        posSyncExceptionDto.setRecordId(relationshipDto.getId());
                        String json = new Gson().toJson(relationshipDto);
                        posSyncExceptionDto.setRawData(json);
                        posSyncExceptionDto.setErrorDescription("Exception while inserting TABLE_RELATIONSHIP");
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                        String dateStr = df.format(new Date());
                        Date currentDate = df.parse(dateStr);
                        posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                        FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
                    }
                    catch(Exception e1) {
                        com.omneagate.Util.Util.LoggingQueue(contextValue, "relationship Exception 2...", e.toString());
                    }
                }
            }
        }
    }*/

    public List<BeneficiaryMemberDto> getBenefMemberDetail(String ufc_code) {
        Cursor cursor = null;
        try {
            Log.e("^^^^FPSDBHelper^^^^", "getBenefMemberDetail()  ufc_code ->" + ufc_code);
            // Log.e("getBenefMemberDetail", "ufc_code = " + ufc_code);
            List<BeneficiaryMemberDto> beneficiaryMembers = new ArrayList<>();
            String selectQuery = "SELECT * FROM beneficiary_member where ufc_code='" + ufc_code + "' and is_removed = 0";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                //Log.e("getBenefMemberDetail", "new BeneficiaryMemberDto() = " + new BeneficiaryMemberDto(cursor));
                beneficiaryMembers.add(new BeneficiaryMemberDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return beneficiaryMembers;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // This function inserts details to TABLE_REG_REQ.
    public boolean insertRegistrationRequestData(Set<BeneficiaryRegistrationData> beneficiaryRegistrationDataSet, String syncType) {
        tableType = "";
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        BeneficiaryRegistrationData beneficiaryRegistrationDataSet1 = null;
        if (!beneficiaryRegistrationDataSet.isEmpty()) {
            database.beginTransaction();
            try {
                for (BeneficiaryRegistrationData beneficiaryRegistrationData : beneficiaryRegistrationDataSet) {
                    tableType = "Parent";
                    recordId = beneficiaryRegistrationData.getId();
                    beneficiaryRegistrationDataSet1 = beneficiaryRegistrationData;
                    ContentValues values = new ContentValues();
                    if (beneficiaryRegistrationData.getChannel().equals("POS") || beneficiaryRegistrationData.getChannel().equals("SMS")) {
                        insertUserRegistration(beneficiaryRegistrationData.getId(), beneficiaryRegistrationData.getMobNum(), beneficiaryRegistrationData.getOldRationCardNum(),
                                beneficiaryRegistrationData.getTransactionId(), beneficiaryRegistrationData.getRequestedTime(), beneficiaryRegistrationData.getAregisterNum());
                    } else {
                        values.put(KEY_ID, beneficiaryRegistrationData.getId());
                        values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, beneficiaryRegistrationData.getOldRationCardNum());
                        values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiaryRegistrationData.getNumOfCylinder());
                        values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiaryRegistrationData.getNumOfAdults());
                        values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiaryRegistrationData.getNumOfChild());
                        String mobile = beneficiaryRegistrationData.getMobNum();
                        if (StringUtils.isEmpty(mobile)) {
                            mobile = "";
                        }
                        values.put(FPSDBConstants.KEY_REGISTRATION_MOB, mobile);
                        values.put(FPSDBConstants.KEY_REGISTRATION_FPS_ID, beneficiaryRegistrationData.getFpsId());
                        values.put("channel", beneficiaryRegistrationData.getChannel());
                        values.put(FPSDBConstants.KEY_ALLOTMENT_CARD_TYPE, beneficiaryRegistrationData.getCardType());
                        Date todayDate = new Date(beneficiaryRegistrationData.getRequestedTime());
                        SimpleDateFormat regDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
                        values.put(FPSDBConstants.KEY_REGISTRATION_RTIME, regDate.format(todayDate));
                        values.put("reqTime", beneficiaryRegistrationData.getRequestedTime());
                        database.insertWithOnConflict(FPSDBTables.TABLE_REG_REQ, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "beneficiary_registration Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    if (tableType.equalsIgnoreCase("Parent")) {
                        String json = new Gson().toJson(beneficiaryRegistrationDataSet1);
                        insertSyncException("TABLE_REG_REQ", syncType, recordId, json);
                    } else if (tableType.equalsIgnoreCase("Child")) {
                        String json = new Gson().toJson(beneficiaryRegistrationDataSet1);
                        insertSyncException("TABLE_REGISTRATION", syncType, recordId, json);
                    }
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // This function inserts details to FPSDBTables.TABLE_USERS,
    public boolean insertUserDetailData(Set<UserDetailDto> userdetailDto, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        UserDetailDto userdetailDto1 = null;
        if (!userdetailDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (UserDetailDto userDetail : userdetailDto) {
                    recordId = userDetail.getId();
                    userdetailDto1 = userDetail;
                    ContentValues values = new ContentValues();
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------insertUserDetailData-------- userDetail->" + userDetail);
                    values.put(KEY_ID, userDetail.getId());
                    values.put(FPSDBConstants.KEY_USERS_NAME, userDetail.getUserId().toLowerCase());
                    values.put(FPSDBConstants.KEY_USERS_ID, userDetail.getUsername().toLowerCase());
                    values.put(FPSDBConstants.KEY_USERS_PASS_HASH, userDetail.getPassword());
                    values.put(FPSDBConstants.KEY_USERS_PROFILE, userDetail.getProfile());
                    values.put(FPSDBConstants.KEY_USERS_FPS_ID, userDetail.getFpsStore().getId());
                    values.put(FPSDBConstants.KEY_USERS_CONTACT_PERSON, userDetail.getFpsStore().getContactPerson());
                    values.put(FPSDBConstants.KEY_USERS_PHONE_NUMBER, userDetail.getFpsStore().getPhoneNumber());
                    values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE1, userDetail.getFpsStore().getAddressLine1());
                    values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE2, userDetail.getFpsStore().getAddressLine2());
                    values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE3, userDetail.getFpsStore().getAddressLine3());
                    values.put(FPSDBConstants.KEY_USERS_CODE, userDetail.getFpsStore().getCode());
                    values.put("entitlement_classification", userDetail.getFpsStore().getEntitlementClassification());
                    values.put("village_name", userDetail.getFpsStore().getVillageName());
                    values.put("village_code", userDetail.getFpsStore().getVillageCode());
                    values.put("gen_code", userDetail.getFpsStore().getGeneratedCode());
                    values.put("taluk_name", userDetail.getFpsStore().getTalukName());
                    values.put("taluk_code", userDetail.getFpsStore().getTalukCode());
                    values.put("district_name", userDetail.getFpsStore().getDistrictName());
                    values.put("district_id", userDetail.getFpsStore().getDistrictId());
                    values.put("village_id", userDetail.getFpsStore().getVillageId());
                    values.put("taluk_id", userDetail.getFpsStore().getTalukId());
                    try {
                        values.put("createdDate", userDetail.getFpsStore().getCreatedDate());
                        Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------getCreatedDate-------- userDetail->" + userDetail.getFpsStore().getCreatedDate());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------getCreatedDate-------- Exception->" + e);
                    }
                    if (userDetail.getFpsStore().getFpsCategory() != null)
                        values.put("fps_category", userDetail.getFpsStore().getFpsCategory().toString());
                    if (userDetail.getFpsStore().getFpsType() != null)
                        values.put("fps_type", userDetail.getFpsStore().getFpsType().toString());
                    values.put("district_code", userDetail.getFpsStore().getDistrictCode());
                    Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------district_code-------- userDetail->" + userDetail.getFpsStore().getDistrictCode());
                    Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------district_id-------- userDetail->" + userDetail.getFpsStore().getDistrictId());
                    Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------village_id-------- userDetail->" + userDetail.getFpsStore().getVillageId());
                    Util.LoggingQueue(contextValue, "^^^^insertUserDetailData^^^^", "------------------taluk_id-------- userDetail->" + userDetail.getFpsStore().getTalukId());
                    values.put("device_sim_no", userDetail.getFpsStore().getDeviceSimNo());
                    values.put("agency_name", userDetail.getFpsStore().getAgencyName());
                    values.put("agency_code", userDetail.getFpsStore().getAgencyCode());
                    values.put("operation_closing_time", userDetail.getFpsStore().getOperationClosingTime());
                    values.put("operation_opening_time", userDetail.getFpsStore().getOperationOpeningTime());
                    int storeStatus = 0;
                    if (userDetail.getFpsStore().isActive()) {
                        storeStatus = 1;
                    }
                    values.put(FPSDBConstants.KEY_USERS_IS_ACTIVE, storeStatus);
                    int userStatus = 0;
                    if (userDetail.getActive()) {
                        userStatus = 1;
                    }
                    values.put("is_user_active", userStatus);
                    database.insertWithOnConflict(FPSDBTables.TABLE_USERS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_USERS Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(userdetailDto1);
                    insertSyncException("TABLE_USERS", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // This function inserts details to TABLE_STOCK
    public boolean insertFpsStockData(Set<FPSStockDto> fpsStockDto, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        FPSStockDto fpsStockDto1 = null;
        if (!fpsStockDto.isEmpty()) {
            database.beginTransaction();
            try {
//                int count = 0;
                for (FPSStockDto fpsStock : fpsStockDto) {
                    recordId = fpsStock.getProductId();
                    Log.e("dbhelper", "recordId.." + recordId);
                    fpsStockDto1 = fpsStock;
                    ContentValues values = new ContentValues();
                    values.put(FPSDBConstants.KEY_STOCK_FPS_ID, fpsStock.getFpsId());
                    values.put(FPSDBConstants.KEY_STOCK_PRODUCT_ID, fpsStock.getProductId());
                    Double qty1 = Double.parseDouble(Util.quantityRoundOffFormat(fpsStock.getQuantity()));
                    values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty1);
                    values.put(FPSDBConstants.KEY_STOCK_REORDER_LEVEL, fpsStock.getReorderLevel());
                    if (fpsStock.isEmailAction()) {
                        values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 0);
                    } else {
                        values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 1);
                    }
                    if (fpsStock.isSmsMSAction()) {
                        values.put(FPSDBConstants.KEY_STOCK_SMS_ACTION, 0);
                    } else {
                        values.put(FPSDBConstants.KEY_STOCK_SMS_ACTION, 1);
                    }
//                    if (count == 3) {
//                        throw new NullPointerException();
//                    }
                    database.insertWithOnConflict(FPSDBTables.TABLE_STOCK, FPSDBConstants.KEY_STOCK_PRODUCT_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
//                    Log.e("dbhelper", "count.." + count);
//                    count++;
                }
                Log.e("dbhelper", "a..");
                isSuccessFullyInserted = true;
                Log.e("dbhelper", "b..");
                database.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("dbhelper", "c..");
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "stock Exception...", e.toString());
            } finally {
                Log.e("dbhelper", "d..");
                database.endTransaction();
                String json = new Gson().toJson(fpsStockDto1);
                insertSyncException("TABLE_STOCK", syncType, recordId, json);
            }
        }
        return isSuccessFullyInserted;
    }

    public void insertRoles(long userId, Set<AppfeatureDto> roleFeatures) {
        Log.e("^^^^FPSDBHelper^^^^", "insertRoles"+roleFeatures.toString());
        ContentValues values = new ContentValues();
        for (AppfeatureDto features : roleFeatures) {
            try {
                values.put(FPSDBConstants.KEY_ROLE_USERID, userId);
                values.put(FPSDBConstants.KEY_ROLE_FEATUREID, features.getFeatureId());
                values.put("role_id", features.getFeatureId());
                values.put(FPSDBConstants.KEY_ROLE_NAME, features.getFeatureName());
                values.put(FPSDBConstants.KEY_ROLE_PARENTID, features.getParentId());
                values.put(FPSDBConstants.KEY_ROLE_TYPE, features.getName());
                values.put("isDeleted", 1);
                database.insert(FPSDBTables.TABLE_ROLE_FEATURE, null, values);
            } catch (Exception e) {
                Log.e("roleFeature", e.toString(), e);
            }
        }
    }

    public void insertFpsStockDataAdmin(List<FPSStockDto> fpsStockList) {
        if (!fpsStockList.isEmpty()) {
            for (FPSStockDto fpsStock : fpsStockList) {
                ContentValues values = new ContentValues();
                Log.e("^^^^FPSDBHelper^^^^", "STOCK_PRODUCT_ID" + fpsStock.getProductId());
                values.put(FPSDBConstants.KEY_STOCK_FPS_ID, fpsStock.getFpsId());
                values.put(FPSDBConstants.KEY_STOCK_PRODUCT_ID, fpsStock.getProductId());
                /*NumberFormat formatter = new DecimalFormat("#0.000");
                formatter.setRoundingMode(RoundingMode.CEILING);*/
                Double qty2 = Double.parseDouble(Util.quantityRoundOffFormat(fpsStock.getQuantity()));
                values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty2);
                values.put(FPSDBConstants.KEY_STOCK_REORDER_LEVEL, fpsStock.getReorderLevel());
                if (fpsStock.isEmailAction()) {
                    values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 0);
                } else {
                    values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 1);
                }
                if (fpsStock.isSmsMSAction()) {
                    values.put(FPSDBConstants.KEY_STOCK_SMS_ACTION, 0);
                } else {
                    values.put(FPSDBConstants.KEY_STOCK_SMS_ACTION, 1);
                }
                insertStockHistory(fpsStock.getQuantity(), fpsStock.getQuantity(), "INITIAL STOCK", 0.0, fpsStock.getProductId());
                database.insertWithOnConflict(FPSDBTables.TABLE_STOCK, FPSDBConstants.KEY_STOCK_PRODUCT_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void insertSyncValue(List<UserFistSyncDto> userSync) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertSyncValue() called ");
        if (!userSync.isEmpty()) {
            for (UserFistSyncDto fpsStock : userSync) {
                ContentValues values = new ContentValues();
                values.put("name", fpsStock.getValue());
                values.put("value", fpsStock.getMasterValue());
                database.insert("master_first_sync", null, values);
            }
        }
    }

    public void insertValues() {
        Log.e("^^^^FPSDBHelper^^^^", "insertValues() called ->");
        insertMaserData("serverUrl", "http://52.66.76.172:9201");
//        Util.serverUrl = null;
        insertMaserData("purgeBill", "0");
        insertMaserData("syncTime", null);
        try {
            Calendar calendar = Calendar.getInstance();
            String syncTime = new SimpleDateFormat("yyyy-MM-").format(calendar.getTime()) + "01 00:00:00.000";
            insertMaserData("allocationSyncTime", syncTime);
        } catch (Exception e) {
            Log.e("^^^^FPSDBHelper^^^^", "exc all time"+e);
        }
        insertMaserData("status", null);
        insertMaserData("printer", null);
        insertMaserData("language", "te");
    }

    public void insertMaserData(String name, String value) {
        Log.e("^^^^FPSDBHelper^^^^", "insertMaserData() called name ->" + name + " value -> " + value);
        // Log.e("^^^^FPSDBHelper^^^^", "name = " + name + " value = " + value);
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("value", value);
        if (database == null) {
            database = dbHelper.getWritableDatabase();
        }
        database.insertWithOnConflict(FPSDBTables.TABLE_CONFIG_TABLE, "name", values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    //insert login data inside database
    public void insertLoginUserData(LoginResponseDto loginResponse, String password) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "insertLoginUserData() called password -> " + password);
        ContentValues values = new ContentValues();
        try {
            values.put(KEY_ID, loginResponse.getUserDetailDto().getId());
            values.put(FPSDBConstants.KEY_USERS_NAME, loginResponse.getUserDetailDto().getUserId().toLowerCase());
            values.put(FPSDBConstants.KEY_USERS_ID, loginResponse.getUserDetailDto().getUsername().toLowerCase());
            values.put(FPSDBConstants.KEY_USERS_PASS_HASH, loginResponse.getUserDetailDto().getPassword());
            values.put(FPSDBConstants.KEY_USERS_PROFILE, loginResponse.getUserDetailDto().getProfile());
            values.put("encrypted_password", com.omneagate.Util.Util.EncryptPassword(password));
            if (loginResponse.getUserDetailDto().getFpsStore() != null) {
                values.put(FPSDBConstants.KEY_USERS_FPS_ID, loginResponse.getUserDetailDto().getFpsStore().getId());
                values.put(FPSDBConstants.KEY_USERS_CODE, loginResponse.getUserDetailDto().getFpsStore().getCode());
                values.put(FPSDBConstants.KEY_USERS_CONTACT_PERSON, loginResponse.getUserDetailDto().getFpsStore().getContactPerson());
                values.put(FPSDBConstants.KEY_USERS_PHONE_NUMBER, loginResponse.getUserDetailDto().getFpsStore().getPhoneNumber());
                values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE1, loginResponse.getUserDetailDto().getFpsStore().getAddressLine1());
                values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE2, loginResponse.getUserDetailDto().getFpsStore().getAddressLine2());
                values.put(FPSDBConstants.KEY_USERS_ADDRESS_LINE3, loginResponse.getUserDetailDto().getFpsStore().getAddressLine3());
                values.put("entitlement_classification", loginResponse.getUserDetailDto().getFpsStore().getEntitlementClassification());
                values.put("village_name", loginResponse.getUserDetailDto().getFpsStore().getVillageName());
                values.put("village_code", loginResponse.getUserDetailDto().getFpsStore().getVillageCode());
                values.put("gen_code", loginResponse.getUserDetailDto().getFpsStore().getGeneratedCode());
                values.put("taluk_name", loginResponse.getUserDetailDto().getFpsStore().getTalukName());
                values.put("taluk_code", loginResponse.getUserDetailDto().getFpsStore().getTalukCode());
                values.put("district_name", loginResponse.getUserDetailDto().getFpsStore().getDistrictName());
                values.put("device_sim_no", loginResponse.getUserDetailDto().getFpsStore().getDeviceSimNo());
                values.put("agency_name", loginResponse.getUserDetailDto().getFpsStore().getAgencyName());
                values.put("agency_code", loginResponse.getUserDetailDto().getFpsStore().getAgencyCode());
                values.put("operation_closing_time", loginResponse.getUserDetailDto().getFpsStore().getOperationClosingTime());
                values.put("operation_opening_time", loginResponse.getUserDetailDto().getFpsStore().getOperationOpeningTime());
                try {
                    values.put("district_id", loginResponse.getUserDetailDto().getFpsStore().getDistrictId());
                    values.put("village_id", loginResponse.getUserDetailDto().getFpsStore().getVillageId());
                    values.put("taluk_id", loginResponse.getUserDetailDto().getFpsStore().getTalukId());
                    values.put("district_code", loginResponse.getUserDetailDto().getFpsStore().getDistrictCode());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------district_code-------- userDetail->" +
                            loginResponse.getUserDetailDto().getFpsStore().getDistrictCode());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------district_id-------- userDetail->" +
                            loginResponse.getUserDetailDto().getFpsStore().getDistrictId());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------village_id-------- userDetail->" +
                            loginResponse.getUserDetailDto().getFpsStore().getVillageId());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------taluk_id-------- userDetail->" +
                            loginResponse.getUserDetailDto().getFpsStore().getTalukId());
                    values.put("createdDate", loginResponse.getUserDetailDto().getFpsStore().getCreatedDate());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "------------------getCreatedDate-------- userDetail->" +
                            loginResponse.getUserDetailDto().getFpsStore().getCreatedDate());
                } catch (Exception e) {
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "Exception " + e);
                }


               /* try {
                    values.put("createdDate", loginResponse.getUserDetailDto().getFpsStore().getDistrictId());

                }catch (Exception e){

                }*/
                int storeStatus = 0;
                if (loginResponse.getUserDetailDto().getFpsStore().isActive()) {
                    storeStatus = 1;
                }
                values.put(FPSDBConstants.KEY_USERS_IS_ACTIVE, storeStatus);
                int userStatus = 0;
                if (loginResponse.getUserDetailDto().getActive()) {
                    userStatus = 1;
                }
                values.put("is_user_active", userStatus);
                if (loginResponse.getUserDetailDto().getFpsStore().getFpsCategory() != null)
                    values.put("fps_category", loginResponse.getUserDetailDto().getFpsStore().getFpsCategory().toString());
                if (loginResponse.getUserDetailDto().getFpsStore().getFpsType() != null)
                    values.put("fps_type", loginResponse.getUserDetailDto().getFpsStore().getFpsType().toString());
            }
        } catch (Exception e) {
            //  Log.e("User data", e.toString(), e);
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "insertLoginUserData() called Exception ->" + e);
        } finally {
            database.insertWithOnConflict(FPSDBTables.TABLE_USERS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public void insertLoginHistory(LoginHistoryDto loginHistory) {
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "insertLoginHistory() called LoginHistoryDto ->" + loginHistory);
            ContentValues values = new ContentValues();
            values.put("login_time", loginHistory.getLoginTime());
            values.put("login_type", loginHistory.getLoginType());
            values.put("user_id", loginHistory.getUserId());
            values.put("fps_id", loginHistory.getFpsId());
            values.put("transaction_id", loginHistory.getTransactionId());
            values.put("created_time", new Date().getTime());
            values.put("is_sync", 0);
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.insert(FPSDBTables.TABLE_LOGIN_HISTORY, null, values);
        } catch (Exception e) {
            Log.e("Login History", e.toString(), e);
        }
    }

    public void insertBackGroundLoginHistory(LoginHistoryDto loginHistory) {
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertBackGroundLoginHistory() called LoginHistoryDto = " + loginHistory);
            ContentValues values = new ContentValues();
            values.put("login_time", loginHistory.getLoginTime());
            values.put("login_type", loginHistory.getLoginType());
            values.put("user_id", loginHistory.getUserId());
            values.put("fps_id", loginHistory.getFpsId());
            values.put("created_time", new Date().getTime());
            values.put("is_sync", 1);
            values.put("is_logout_sync", 1);
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.insert(FPSDBTables.TABLE_LOGIN_HISTORY, null, values);
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertBackGroundLoginHistory() called Exception = " + e);
        }
    }

    //Insert into Stock history
    public void insertStockHistory(double openingBalance, double closingBalance, String action, double changeInBalance, long productId) {
        ContentValues values = new ContentValues();
        Log.e("^^^^FPSDBHelper^^^^", "insertStockHistory" + openingBalance + " , " + closingBalance + " , " + action + " , " + changeInBalance + " , " + productId);
        try {
            /*NumberFormat formatter = new DecimalFormat("#0.000");
            formatter.setRoundingMode(RoundingMode.CEILING);*/
            openingBalance = Double.parseDouble(Util.quantityRoundOffFormat(openingBalance));
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_OPEN_BALANCE, openingBalance);
            closingBalance = Double.parseDouble(Util.quantityRoundOffFormat(closingBalance));
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_CLOSE_BALANCE, closingBalance);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_ACTION, action);
            changeInBalance = Double.parseDouble(Util.quantityRoundOffFormat(changeInBalance));
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_CHANGE_BALANCE, changeInBalance);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_PRODUCT_ID, productId);
            values.put(FPSDBConstants.KEY_STOCK_HISTORY_DATE, new Date().getTime());
            SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            values.put(FPSDBConstants.KEY_STOCK_DATE, billDate.format(new Date()));
            database.insert(FPSDBTables.TABLE_STOCK_HISTORY, null, values);
        } catch (Exception e) {
            Log.e("insertStockHistory Exce", e.toString(), e);
        }
    }

    public void stockAdjustmentData(List<POSStockAdjustmentDto> fpsStockAdjustmentList) {
        /*String selectQuery = "SELECT *  FROM fps_stock_adjustment where isAdjusted = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for(int i=0;i<cursor.getCount();i++) {
            adjustStocks(new POSStockAdjustmentDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();*/
        Log.e("^^^^FPSDBHelper^^^^", "fpsStockAdjustmentList.size()..." + fpsStockAdjustmentList.size());
        for (int i = 0; i < fpsStockAdjustmentList.size(); i++) {
            Log.e("dbhelper", "fpsStockAdjustmentList.get(i)..." + fpsStockAdjustmentList.get(i));
            adjustStocks(fpsStockAdjustmentList.get(i));
        }
    }

    public List<POSStockAdjustmentDto> stockAdjustmentDataToServer() {
        List<POSStockAdjustmentDto> posAcknowledge = new ArrayList<>();
        String selectQuery = "SELECT *  FROM fps_stock_adjustment where isAdjusted = 1 and isServerAdded = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            posAcknowledge.add(new POSStockAdjustmentDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return posAcknowledge;
    }

    private void adjustStocks(POSStockAdjustmentDto posStockAdjustmentDto) {
        try {
            String stock_validation = "" + getMasterData("stock_validation");
            if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                if (stock_validation.equalsIgnoreCase("1")) {
                    updateAdjustmentInStock(posStockAdjustmentDto);
                }
            }
            stockAdjustmentAcknowledge(posStockAdjustmentDto);
        } catch (Exception e) {
        }
    }

    private void updateAdjustmentInStock(POSStockAdjustmentDto posStockAdjustmentDto) {
        FPSStockDto stocks = getAllProductStockDetails(posStockAdjustmentDto.getProductId());
        if(stocks != null) {
            Double quantity = stocks.getQuantity();
            String typeAdjust;
            if (posStockAdjustmentDto.getRequestType().equalsIgnoreCase("STOCK_INCREMENT")) {
                quantity = quantity + posStockAdjustmentDto.getQuantity();
                typeAdjust = "INCREMENT";
            } else {
                quantity = quantity - posStockAdjustmentDto.getQuantity();
                typeAdjust = "DECREMENT";
            }
            Log.e(TAG, "OPENING BALANCE..." + stocks.getQuantity());
            Log.e(TAG, "CLOSING BALANCE..." + quantity);
            insertStockHistory(stocks.getQuantity(), quantity, "STOCK ADJUSTMENT" + typeAdjust, posStockAdjustmentDto.getQuantity(), posStockAdjustmentDto.getProductId());
            stocks.setQuantity(quantity);
            String stock_validation = "" + FPSDBHelper.getInstance(contextValue).getMasterData("stock_validation");
            if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                if (stock_validation.equalsIgnoreCase("1")) {
                    stockUpdate(stocks);
                }
            }

        }

    }

    public boolean stockAdjustmentFirstSync(Set<POSStockAdjustmentDto> stockAdjustment, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        POSStockAdjustmentDto stockAdjustment1 = null;
        if (!stockAdjustment.isEmpty()) {
            database.beginTransaction();
            try {
                Log.e("Stock Adj", stockAdjustment.toString());
                for (POSStockAdjustmentDto stockAdjustmentDto : stockAdjustment) {
                    recordId = stockAdjustmentDto.getId();
                    stockAdjustment1 = stockAdjustmentDto;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, stockAdjustmentDto.getId());
                    values.put("product_id", stockAdjustmentDto.getProductId());
                    values.put("quantity", stockAdjustmentDto.getQuantity());
                    SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    if (stockAdjustmentDto.getPosAckDate() != null)
                        values.put("dateOfAck", billDate.format(new Date(stockAdjustmentDto.getPosAckDate())));
                    if (stockAdjustmentDto.getPosAckStatus() == null || !stockAdjustmentDto.getPosAckStatus()) {
                        values.put("isServerAdded", 0);
                        values.put("isAdjusted", 0);
                    } else {
                        values.put("isServerAdded", 1);
                        values.put("isAdjusted", 1);
                    }
                    values.put("requestType", stockAdjustmentDto.getRequestType());
                    values.put("referenceNo", stockAdjustmentDto.getReferenceNumber());
                    values.put("godownReferenceNo", stockAdjustmentDto.getGodownStockOutwardReferenceNumber());
                    values.put("createdDate", stockAdjustmentDto.getCreatedDate());
                    database.insertWithOnConflict("fps_stock_adjustment", null, values, SQLiteDatabase.CONFLICT_IGNORE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "fps_stock_adjustment Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(stockAdjustment1);
                    insertSyncException("fps_stock_adjustment", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    private void stockAdjustmentAcknowledge(POSStockAdjustmentDto stockAdjustmentDto) {
        ContentValues values = new ContentValues();
        try {
//            SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            values.put("dateOfAck", billDate.format(new Date()));
            values.put("isServerAdded", 0);
            values.put("isAdjusted", 1);
            database.update("fps_stock_adjustment", values, KEY_ID + "=" + stockAdjustmentDto.getId(), null);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    //Insert into Registration
    public boolean insertRegistration(String phoneNumber, String cardNumber, String refId) {
        boolean status = true;
        ContentValues values = new ContentValues();
        try {
            values.put(FPSDBConstants.KEY_REGISTRATION_CARD_NO, cardNumber.toUpperCase());
            values.put(FPSDBConstants.KEY_REGISTRATION_STATUS, "R");
            values.put(FPSDBConstants.KEY_REGISTRATION_RMN, phoneNumber);
            values.put(FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO, refId);
            values.put(FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED, 0);
            values.put("channel", "POS");
            Date todayDate = new Date();
            SimpleDateFormat regDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            values.put(FPSDBConstants.KEY_REGISTRATION_TIME, regDate.format(todayDate));
            values.put("reqTime", todayDate.getTime());
            database.insert(FPSDBTables.TABLE_REGISTRATION, null, values);
        } catch (Exception e) {
            status = false;
            Log.e("Registration", e.toString(), e);
        }
        return status;
    }

    // Insert into Registration
    private void insertUserRegistration(long recId, String phoneNumber, String cardNumber, String refId, long reqDate, String aRegister) {
        tableType = "Child";
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_REGISTRATION_CARD_NO, cardNumber.toUpperCase());
        values.put(FPSDBConstants.KEY_REGISTRATION_STATUS, "S");
        values.put(FPSDBConstants.KEY_REGISTRATION_RMN, phoneNumber);
        values.put(FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO, refId);
        values.put(FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED, 0);
        values.put("channel", "POS");
        if (StringUtils.isNotEmpty(aRegister))
            values.put("aRegister", Integer.parseInt(aRegister));
        Date todayDate = new Date(reqDate);
        SimpleDateFormat regDate = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        values.put(FPSDBConstants.KEY_REGISTRATION_TIME, regDate.format(todayDate));
        values.put("reqTime", reqDate);
        database.insertWithOnConflict(FPSDBTables.TABLE_REGISTRATION, FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    private void deleteCard(String cardNo) {
        database.delete(FPSDBTables.TABLE_REG_REQ, "old_ration_card_num='" + cardNo + "'", null);
    }

    public void bifurcationDeactivateBenef(String benefId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "bifurcationDeactivateBenef() called benefId = " + benefId);
        ContentValues values = new ContentValues();
        values.put("active", 0);
        database.update("beneficiary", values, "_id = " + benefId, null);
    }

    public void updateBifurcationStatus(String benefId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "updateBifurcationStatus() called benefId = " + benefId);
        try {
            ContentValues values = new ContentValues();
            values.put("status", 1);
            database.update("Bifurcation", values, "benefId = " + benefId, null);
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "updateBifurcationStatus() called Exception = " + e);
        }
    }

    public void updateBiometricSyncStatus(long benefId) {
        try {
            ContentValues values = new ContentValues();
            values.put("syncStatus", 1);
            database.update("biometric_authentication", values, "benefId = " + benefId, null);
        } catch (Exception e) {
        }
    }

    public void updateBfdDetailsSyncStatus(long benefId) {
        try {
            ContentValues values = new ContentValues();
            values.put("sync_status", 1);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("modified_date", created.getTime());
            }
            catch(Exception e) {}
            values.put("modified_by", SessionId.getInstance().getFpsId());
            database.update("bfd_details", values, "benef_id = " + benefId, null);
        }
        catch(Exception e) {}
    }

    public void updateKycRequestDetails(long id) {
        try {
            ContentValues values = new ContentValues();
            values.put("ackStatus", 1);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("modified_date", created.getTime());
            }
            catch(Exception e) {}
            values.put("modified_by", SessionId.getInstance().getFpsId());
            database.update("kyc_request_details", values, "_id = " + id, null);
        }
        catch(Exception e) {}
    }

    public void updateProxyDetailsSyncStatus(long benefId) {
        try {
            ContentValues values = new ContentValues();
            values.put("sync_status", 1);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("modified_date", created.getTime());
            } catch (Exception e) {
            }
            values.put("modified_by", SessionId.getInstance().getFpsId());
            database.update("proxy_details", values, "benef_id = " + benefId, null);
        } catch (Exception e) {
        }
    }

    public boolean insertMigrations(Set<FPSMigrationDto> migration, String syncType) {
        tableType = "";
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        FPSMigrationDto migration1 = null;
        if (!migration.isEmpty()) {
            database.beginTransaction();
            try {
                for (FPSMigrationDto migrate : migration) {
                    tableType = "";
                    recordId = migrate.getId();
                    migration1 = migrate;
                    if (migrate.getType().equalsIgnoreCase("IN")) {
                        insertMigrateIn(migrate);
                        insertBeneficiaryNewIn(migrate.getBeneficiaryDto());
                    } else {
                        insertMigrateOut(migrate);
                    }
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "insertMigrations Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(migration1);
                    insertSyncException(tableType, syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Person Rules
    public boolean insertPersonRules(Set<PersonBasedRule> masterRules, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        PersonBasedRule masterRules1 = null;
        if (!masterRules.isEmpty()) {
            database.beginTransaction();
            try {
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertPersonRules() called Set<PersonBasedRule> Size ->" + masterRules.size());
                for (PersonBasedRule personBasedRule : masterRules) {
                    recordId = personBasedRule.getId();
                    masterRules1 = personBasedRule;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, personBasedRule.getId());
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertPersonRules() called KEY_ID ->" + personBasedRule.getId());
                    if (personBasedRule.getIsDeleted() != null && personBasedRule.getIsDeleted())
                        values.put("isDeleted", 1);
                    else {
                        values.put("isDeleted", 0);
                    }
                    values.put("groupId", personBasedRule.getGroupDto().getId());
                    values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, personBasedRule.getProductDto().getId());
                    if (personBasedRule.getCardTypeDto() != null)
                        values.put(FPSDBConstants.KEY_RULES_CARD_TYPE_ID, personBasedRule.getCardTypeDto().getId());
                    Double qty9 = Double.parseDouble(Util.quantityRoundOffFormat(personBasedRule.getMin()));
                    values.put(FPSDBConstants.KEY_PERSON_MIN, qty9);
                    Double qty1 = Double.parseDouble(Util.quantityRoundOffFormat(personBasedRule.getMax()));
                    values.put(FPSDBConstants.KEY_PERSON_MAX, qty1);
                    Double qty2 = Double.parseDouble(Util.quantityRoundOffFormat(personBasedRule.getPerChild()));
                    values.put(FPSDBConstants.KEY_PERSON_CHILD, qty2);
                    Double qty3 = Double.parseDouble(Util.quantityRoundOffFormat(personBasedRule.getPerAdult()));
                    values.put(FPSDBConstants.KEY_PERSON_ADULT, qty3);
                    database.insertWithOnConflict(TABLE_PERSON_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertPersonRules() called Successfully inserted ->" + personBasedRule);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_PERSON_RULES Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(masterRules1);
                    insertSyncException("TABLE_PERSON_RULES", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Person Rules
    private void insertMigrateIn(FPSMigrationDto fpsMigrationDto) {
        tableType = "TABLE_FPS_MIGRATION_IN";
        Log.e("^^^^FPSDBHelper^^^^", "migration_in..." + fpsMigrationDto.toString());
        ContentValues values = new ContentValues();
        values.put(KEY_ID, fpsMigrationDto.getId());
        if (fpsMigrationDto.getIsMigrated() != null && fpsMigrationDto.getIsMigrated())
            values.put("isAdded", 1);
        else {
            values.put("isAdded", 0);
        }
        values.put("ration_card_number", fpsMigrationDto.getBeneficiaryDto().getOldRationNumber());
        values.put("a_register_no", fpsMigrationDto.getBeneficiaryDto().getAregisterNum());
        values.put("ufc_code", fpsMigrationDto.getBeneficiaryDto().getEncryptedUfc());
        values.put("beneficiary_id", fpsMigrationDto.getBeneficiaryDto().getId());
        values.put("month_in", fpsMigrationDto.getMonth());
        values.put("year_in", fpsMigrationDto.getYear());
        values.put("blocked_date", fpsMigrationDto.getBenefBlockedDate());
        values.put("migrated_date", fpsMigrationDto.getBenefMigratedDate());
        database.insertWithOnConflict(FPSDBTables.TABLE_FPS_MIGRATION_IN, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Insert into Person Rules
    private void insertMigrateOut(FPSMigrationDto fpsMigrationDto) {
        tableType = "TABLE_FPS_MIGRATION_OUT";
        ContentValues values = new ContentValues();
        values.put(KEY_ID, fpsMigrationDto.getId());
        if (fpsMigrationDto.getIsMigrated() != null && fpsMigrationDto.getIsMigrated())
            values.put("isAdded", 1);
        else {
            values.put("isAdded", 0);
        }
        values.put("beneficiary_id", fpsMigrationDto.getBeneficiaryId());
        values.put("month_out", fpsMigrationDto.getMonth());
        values.put("year_out", fpsMigrationDto.getYear());
        values.put("blocked_date", fpsMigrationDto.getBenefBlockedDate());
        values.put("migrated_date", fpsMigrationDto.getBenefMigratedDate());
        database.insertWithOnConflict(FPSDBTables.TABLE_FPS_MIGRATION_OUT, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Insert into Person Rules
    public boolean insertProductGroup(Set<GroupDto> groupProduct, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        GroupDto groupProduct1 = null;
        if (!groupProduct.isEmpty()) {
            database.beginTransaction();
            try {
                for (GroupDto productGroup : groupProduct) {
                    recordId = productGroup.getId();
                    groupProduct1 = productGroup;
                    ContentValues values = new ContentValues();
                    values.put("group_id", productGroup.getId());
                    if (productGroup.getDeleted() != null && productGroup.getDeleted())
                        values.put("is_deleted", 1);
                    else {
                        values.put("is_deleted", 0);
                    }
                    values.put("name", productGroup.getGroupName());
                    for (ProductDto products : productGroup.getProductDto()) {
                        values.put("product_id", products.getId());
                        database.insert(FPSDBTables.TABLE_PRODUCT_GROUP, null, values);
                    }
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_PRODUCT_GROUP Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(groupProduct1);
                    insertSyncException("TABLE_PRODUCT_GROUP", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    //Insert into Person Rules
    public void insertProductHistory(Set<FPSStockHistoryCollectionDto> groupProduct) {
        try {
            for (FPSStockHistoryCollectionDto productGroup : groupProduct) {
                insertStockHistory(productGroup.getCurrQuantity(), getClosingStock(productGroup.getProductDto().getId()), "INITIAL STOCK", 0.0, productGroup.getProductDto().getId());
            }
        } catch (Exception e) {
            Log.e("insertProductHistory e", e.toString(), e);
        }
    }

    public double getClosingStock(long productId) {
        double closingStock = 0l;
        String selectQuery = "SELECT * FROM stock where product_id = " + productId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            closingStock = cursor.getDouble(cursor.getColumnIndex("quantity"));
        }
        cursor.close();
        return closingStock;
    }

    // Insert into Person Rules
    public boolean insertProductPriceOverride(Set<ProductPriceOverrideDto> groupProduct, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        ProductPriceOverrideDto groupProduct1 = null;
        if (!groupProduct.isEmpty()) {
            database.beginTransaction();
            try {
                for (ProductPriceOverrideDto productGroup : groupProduct) {
                    recordId = productGroup.getId();
                    groupProduct1 = productGroup;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, productGroup.getId());
                    if (productGroup.getIsDeleted() != null && productGroup.getIsDeleted())
                        values.put("is_deleted", 1);
                    else {
                        values.put("is_deleted", 0);
                    }
                    values.put("percentage", productGroup.getPercentage());
                    values.put("card_type_id", productGroup.getCardTypeId());
                    values.put("card_type", productGroup.getCardType());
                    values.put("product_id", productGroup.getProductId());
                    database.insertWithOnConflict(FPSDBTables.TABLE_PRODUCT_PRICE_OVERRIDE, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "product_price_override Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(groupProduct1);
                    insertSyncException("TABLE_PRODUCT_PRICE_OVERRIDE", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Person Rules
    public boolean insertRegionRules(Set<RegionBasedRule> masterRules, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        RegionBasedRule masterRules1 = null;
        if (!masterRules.isEmpty()) {
            database.beginTransaction();
            try {
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertRegionRules() called Set<RegionBasedRule> Size ->" + masterRules.size());
                for (RegionBasedRule regionBasedRule : masterRules) {
                    recordId = regionBasedRule.getId();
                    masterRules1 = regionBasedRule;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, regionBasedRule.getId());
                    if (regionBasedRule.getIsDeleted() != null && regionBasedRule.getIsDeleted())
                        values.put("isDeleted", 1);
                    else {
                        values.put("isDeleted", 0);
                    }
                    values.put("groupId", regionBasedRule.getGroupDto().getId());
                    if (regionBasedRule.getProductDto().getId() != null)
                        values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, regionBasedRule.getProductDto().getId());
                    Double qty3 = Double.parseDouble(Util.quantityRoundOffFormat(regionBasedRule.getQuantity()));
                    values.put(FPSDBConstants.KEY_PERSON_QUANTITY, qty3);
                    values.put(FPSDBConstants.KEY_PERSON_CYLINDER, regionBasedRule.getCylinderCount());
                    values.put(FPSDBConstants.KEY_PERSON_TALUK, returnInteger(regionBasedRule.isTaluk()));
                    values.put(FPSDBConstants.KEY_PERSON_HILLAREA, returnInteger(regionBasedRule.isHillyArea()));
                    values.put(FPSDBConstants.KEY_PERSON_SPLAREA, returnInteger(regionBasedRule.isSplArea()));
                    values.put(FPSDBConstants.KEY_PERSON_TOWNPANCHAYAT, returnInteger(regionBasedRule.isTownPanchayat()));
                    values.put(FPSDBConstants.KEY_PERSON_VILLAGEPANCHAYAT, returnInteger(regionBasedRule.isVillagePanchayat()));
                    values.put(FPSDBConstants.KEY_PERSON_CITY, returnInteger(regionBasedRule.isCity()));
                    values.put(FPSDBConstants.KEY_PERSON_MUNICIPALITY, returnInteger(regionBasedRule.isMunicipality()));
                    values.put(FPSDBConstants.KEY_PERSON_HEAD, returnInteger(regionBasedRule.isCityHeadQuarter()));
                    database.insertWithOnConflict(FPSDBTables.TABLE_REGION_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertRegionRules() called Exception ->" + e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(masterRules1);
                    insertSyncException("TABLE_PRODUCTS", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Person Rules
    public boolean insertSmsProvider(Set<SmsProviderDto> smsProviderDtoSet, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        SmsProviderDto smsProviderDtoSet1 = null;
        if (!smsProviderDtoSet.isEmpty()) {
            database.beginTransaction();
            try {
                for (SmsProviderDto smsProviderDto : smsProviderDtoSet) {
                    recordId = smsProviderDto.getId();
                    smsProviderDtoSet1 = smsProviderDto;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, smsProviderDto.getId());
                    values.put(FPSDBConstants.KEY_SMS_PROVIDER_NAME, smsProviderDto.getProviderName());
                    values.put(FPSDBConstants.KEY_SMS_PROVIDER_NUMBER, smsProviderDto.getIncomingNumber());
                    int status = 0;
                    if (smsProviderDto.isEnabledStatus()) {
                        status = 1;
                    }
                    values.put("status", status);
                    values.put(FPSDBConstants.KEY_SMS_PROVIDER_PREFIX, smsProviderDto.getPrefixKey());
                    values.put(FPSDBConstants.KEY_SMS_PROVIDER_PREFERENCE, smsProviderDto.getPreference());
                    database.insertWithOnConflict(FPSDBTables.TABLE_SMS_PROVIDER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_SMS_PROVIDER Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(smsProviderDtoSet1);
                    insertSyncException("TABLE_SMS_PROVIDER", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Master Rules
    public boolean insertMasterRules(Set<EntitlementMasterRule> masterRules, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        EntitlementMasterRule masterRules1 = null;
        if (!masterRules.isEmpty()) {
            database.beginTransaction();
            try {
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertMasterRules() called Set<EntitlementMasterRule> Size ->" + masterRules.size());
                for (EntitlementMasterRule entitlementMasterRule : masterRules) {
                    recordId = entitlementMasterRule.getId();
                    masterRules1 = entitlementMasterRule;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, entitlementMasterRule.getId());
                    if (entitlementMasterRule.getProductDto() != null)
                        values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, entitlementMasterRule.getProductDto().getId());
                    if (entitlementMasterRule.getCardTypeDto() != null)
                        values.put(FPSDBConstants.KEY_RULES_CARD_TYPE, entitlementMasterRule.getCardTypeDto().getId());
                    if (entitlementMasterRule.getIsDeleted() != null && entitlementMasterRule.getIsDeleted())
                        values.put("isDeleted", 1);
                    else {
                        values.put("isDeleted", 0);
                    }
                    values.put("groupId", entitlementMasterRule.getGroupDto().getId());
                    values.put(FPSDBConstants.KEY_RULES_IS_CALC, returnInteger(entitlementMasterRule.isCalcRequired()));
                    values.put(FPSDBConstants.KEY_SPECIAL_MINIMUM, returnInteger(entitlementMasterRule.isMinimumQty()));
                    values.put(FPSDBConstants.KEY_SPECIAL_OVERRIDE, returnInteger(entitlementMasterRule.isOverridePrice()));
                    values.put(FPSDBConstants.KEY_RULES_IS_PERSON, returnInteger(entitlementMasterRule.isPersonBased()));
                    values.put(FPSDBConstants.KEY_RULES_IS_REGION, returnInteger(entitlementMasterRule.isRegionBased()));
                    values.put(FPSDBConstants.KEY_RULES_HAS_SPECIAL, returnInteger(entitlementMasterRule.isHasSpecialRule()));
                    Double qty7 = Double.parseDouble(Util.quantityRoundOffFormat(entitlementMasterRule.getQuantity()));
                    int myInt = qty7.intValue();
                    values.put(FPSDBConstants.KEY_RULES_QUANTITY, myInt);
                    database.insertWithOnConflict(FPSDBTables.TABLE_ENTITLEMENT_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_ENTITLEMENT_RULES Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(masterRules1);
                    insertSyncException("TABLE_ENTITLEMENT_RULES", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Insert into Master Rules
    public boolean insertSpecialRules(Set<SplEntitlementRule> masterRules, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        SplEntitlementRule masterRules1 = null;
        if (!masterRules.isEmpty()) {
            database.beginTransaction();
            try {
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertSpecialRules() called Set<SplEntitlementRule> Size ->" + masterRules.size());
                for (SplEntitlementRule splEntitlementRule : masterRules) {
                    recordId = splEntitlementRule.getId();
                    masterRules1 = splEntitlementRule;
                    Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "insertSpecialRules() splEntitlementRule ->" + splEntitlementRule);
                    ContentValues values = new ContentValues();
                    try {
                        values.put(KEY_ID, splEntitlementRule.getId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getId Exception ->" + splEntitlementRule.getId());
                    }
                    try {
                        if (splEntitlementRule.getIsDeleted() != null && splEntitlementRule.getIsDeleted())
                            values.put("isDeleted", 1);
                        else {
                            values.put("isDeleted", 0);
                        }
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getIsDeleted Exception ->" + splEntitlementRule.getIsDeleted());
                    }
                    try {
                        values.put("groupId", splEntitlementRule.getGroupDto().getId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "groupId Exception ->" + splEntitlementRule.getGroupDto().getId());
                    }
                    try {
                        if (splEntitlementRule.getProductDto().getId() != null)
                            values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, splEntitlementRule.getProductDto().getId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getProductDto().getId() Exception ->" + splEntitlementRule.getProductDto().getId());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_DISTRICT, splEntitlementRule.getDistrictId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getDistrictId() Exception ->" + splEntitlementRule.getDistrictId());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_TALUK, splEntitlementRule.getTalukId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getTalukId() Exception ->" + splEntitlementRule.getTalukId());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_VILLAGE, splEntitlementRule.getVillageId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getVillageId() Exception ->" + splEntitlementRule.getVillageId());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_MUNICIPALITY, returnInteger(splEntitlementRule.isMunicipality()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isMunicipality() Exception ->" + splEntitlementRule.isMunicipality());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_ADD, returnInteger(splEntitlementRule.isAdd()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isAdd() Exception ->" + splEntitlementRule.isAdd());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_CITY_HEAD, returnInteger(splEntitlementRule.isCityHeadQuarter()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isCityHeadQuarter() Exception ->" + splEntitlementRule.isCityHeadQuarter());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_CITY, returnInteger(splEntitlementRule.isCity()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isCity() Exception ->" + splEntitlementRule.isCity());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_TALUK, returnInteger(splEntitlementRule.isTaluk()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isTaluk() Exception ->" + splEntitlementRule.isTaluk());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_HILLAREA, returnInteger(splEntitlementRule.isHillyArea()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isHillyArea() Exception ->" + splEntitlementRule.isHillyArea());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_SPLAREA, returnInteger(splEntitlementRule.isSplArea()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isSplArea() Exception ->" + splEntitlementRule.isSplArea());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_TOWNPANCHAYAT, returnInteger(splEntitlementRule.isTownPanchayat()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isTownPanchayat() Exception ->" + splEntitlementRule.isTownPanchayat());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_IS_VILLAGE_PANCHAYAT, returnInteger(splEntitlementRule.isVillagePanchayat()));
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "isVillagePanchayat() Exception ->" + splEntitlementRule.isVillagePanchayat());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_SPECIAL_CYLINDER, splEntitlementRule.getCylinderCount());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getCylinderCount() Exception ->" + splEntitlementRule.getCylinderCount());
                    }
                    try {
                        values.put(FPSDBConstants.KEY_RULES_CARD_TYPE_ID, splEntitlementRule.getCardTypeId());
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getCardTypeId() Exception ->" + splEntitlementRule.getCardTypeId());
                    }
                    try {
                        Double qty4 = Double.parseDouble(Util.quantityRoundOffFormat(splEntitlementRule.getQuantity()));
                        values.put(FPSDBConstants.KEY_SPECIAL_QUANTITY, qty4);
                    } catch (Exception e) {
                        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getQuantity() Exception ->" + splEntitlementRule.getQuantity());
                    }
                    database.insertWithOnConflict(FPSDBTables.TABLE_SPECIAL_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_SPECIAL_RULES Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(masterRules1);
                    insertSyncException("TABLE_SPECIAL_RULES", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    private int returnInteger(boolean value) {
        if (value)
            return 1;
        return 0;
    }

    // This function inserts details to TABLE_BILL,;
    public boolean insertBillData(Set<BillDto> billDto, String syncType) {
        tableType = "";
        childRecordId = null;
        billItemProductDto1 = null;
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        BillDto billDto1 = null;
        if (!billDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (BillDto bill : billDto) {
                    tableType = "Parent";
                    recordId = bill.getId();
                    billDto1 = bill;
                    ContentValues values = new ContentValues();
                    values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
                    values.put(FPSDBConstants.KEY_BILL_SERVER_REF_ID, bill.getBillRefId());
                    values.put(FPSDBConstants.KEY_BILL_FPS_ID, bill.getFpsId());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, bill.getUfc());
                    values.put(FPSDBConstants.KEY_BILL_DATE, bill.getBillDate());
                    values.put(FPSDBConstants.KEY_BILL_CREATED_BY, bill.getCreatedby());
                    values.put(FPSDBConstants.KEY_BILL_TRANSACTION_ID, bill.getTransactionId());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    Date convertedDate = dateFormat.parse(bill.getBillDate());
                    int month = 0;
                    DateTime date = new DateTime();
                    DateTime newDate = new DateTime(convertedDate);
                    if (date.getYear() == newDate.getYear()) {
                        month = newDate.getMonthOfYear();
                        values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, month);
                    }
                    values.put(FPSDBConstants.KEY_BILL_AMOUNT, bill.getAmount());
                    values.put(FPSDBConstants.KEY_BILL_MODE, String.valueOf(bill.getMode()));
                    values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
                    values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, bill.getBeneficiaryId());
                    values.put(FPSDBConstants.KEY_BILL_CREATED_DATE, bill.getCreatedDate());
                    values.put(FPSDBConstants.KEY_BILL_STATUS, "T");
                    database.insertWithOnConflict(FPSDBTables.TABLE_BILL, FPSDBConstants.KEY_BILL_TRANSACTION_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    insertBillItems(bill.getBillItemDto(), month, bill.getBeneficiaryId(), bill.getBillDate(), bill.getTransactionId());
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "bill Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    if (tableType.equalsIgnoreCase("Parent")) {
                        String json = new Gson().toJson(billDto1);
                        insertSyncException("TABLE_BILL", syncType, recordId, json);
                    } else if (tableType.equalsIgnoreCase("Child")) {
                        String json = new Gson().toJson(billItemProductDto1);
                        insertSyncException("TABLE_BILL_ITEM", syncType, childRecordId, json);
                    }
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public boolean insertBill(BillDto bill) {
        try {
            Log.e("^^^^FPSDBHelper^^^^ ", "insertBill() called" + bill);
            Log.e("^^^^FPSDBHelper^^^^ ", "insertBill() called Mode " + bill.getMode());
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
            values.put(FPSDBConstants.KEY_BILL_TRANSACTION_ID, bill.getTransactionId());
            values.put(FPSDBConstants.KEY_BILL_STATUS, "R");
            values.put("otpTime", bill.getOtpTime());
            values.put("otpId", bill.getOtpId());
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.insertWithOnConflict(FPSDBTables.TABLE_BILL, FPSDBConstants.KEY_BILL_TRANSACTION_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            insertBillItems(bill.getBillItemDto(), new DateTime().getMonthOfYear(), bill.getBeneficiaryId(), bill.getBillDate(), bill.getTransactionId());
            return true;
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ insertBill() Exception", e.toString());
            return false;
        }
    }

    // This function inserts details to FPSDBTables.TABLE_BILL_ITEM,;
    private void insertBillItems(Set<BillItemProductDto> billItem, int month, long beneficiaryId, String billDate, String transactionId) {
        tableType = "Child";
        Log.e("^^^^FPSDBHelper^^^^ ", "insertBillItems() called BillItemProductDto" + billItem);
        Log.e("^^^^FPSDBHelper^^^^ ", "insertBillItems() called month" + month);
        Log.e("^^^^FPSDBHelper^^^^ ", "insertBillItems() called beneficiaryId" + beneficiaryId);
        Log.e("^^^^FPSDBHelper^^^^ ", "insertBillItems() called billDate" + billDate);
        Log.e("^^^^FPSDBHelper^^^^ ", "insertBillItems() called transactionId" + transactionId);
        for (BillItemProductDto billItems : billItem) {
            childRecordId = billItems.getProductId();
            billItemProductDto1 = billItems;
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID, billItems.getProductId());
            Double qty1 = Double.parseDouble(Util.quantityRoundOffFormat(billItems.getQuantity()));
            values.put(FPSDBConstants.KEY_BILL_ITEM_QUANTITY, qty1);
            Double amt1 = Double.parseDouble(Util.priceRoundOffFormat(billItems.getCost() * billItems.getQuantity()));
            values.put("totalCost", amt1);
            Double amt2 = Double.parseDouble(Util.priceRoundOffFormat(billItems.getCost()));
            values.put(FPSDBConstants.KEY_BILL_ITEM_COST, amt2);
            values.put(FPSDBConstants.KEY_BILL_TRANSACTION_ID, transactionId);
            values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, month);
            values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, beneficiaryId);
            values.put(FPSDBConstants.KEY_BILL_ITEM_DATE, billDate);
            database.insert(FPSDBTables.TABLE_BILL_ITEM, null, values);
        }
    }

    //This function inserts details to FPSDBTables.TABLE_BILL_ITEM,;
    public void insertOffLineRegistration(BenefActivNewDto beneficiaryNew) {
        ContentValues values = new ContentValues();
        values.put("aRegister", Integer.parseInt(beneficiaryNew.getAregisterNum()));
        values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, beneficiaryNew.getRationCardNumber());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiaryNew.getNumOfCylinder());
        values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiaryNew.getNumOfAdults());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiaryNew.getNumOfChild());
        if (StringUtils.isNotEmpty(beneficiaryNew.getMobileNum()))
            values.put(FPSDBConstants.KEY_BENEFICIARY_MOBILE, beneficiaryNew.getMobileNum());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID, String.valueOf(beneficiaryNew.getCardType()));
        values.put(FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE, new Date().getTime());
        values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, 0);
        database.insert(FPSDBTables.TABLE_OFFLINE_ACTIVATION, null, values);
    }

    public void insertReconciliationData(ReconciliationRequestDto reconciliationRequestDto, String requestData) {
        ContentValues values = new ContentValues();
        values.put("requestData", requestData);
        Date requestDate = new Date();
        values.put("requestDateTime", requestDate.getTime());
        values.put("reconciliationId", reconciliationRequestDto.getTransactionId());
        values.put("status", "No Response");
        database.insert("reconciliation", null, values);
    }

    public void updateReconciliationData(ReconciliationRequestDto reconciliationRequestDto, String responseData) {
        ContentValues values = new ContentValues();
        values.put("responseData", responseData);
        Date responseDate = new Date();
        values.put("responseDateTime", responseDate.getTime());
        values.put("serverId", reconciliationRequestDto.getId());
        values.put("status", reconciliationRequestDto.getStatus());
        database.update("reconciliation", values, "reconciliationId=" + reconciliationRequestDto.getTransactionId(), null);
    }

    public void updateReconciliationStatus(ReconciliationRequestDto reconciliationRequestDto, String responseData) {
        ContentValues values = new ContentValues();
        values.put("status", reconciliationRequestDto.getStatus());
        values.put("responseData", responseData);
        Date responseDate = new Date();
        values.put("responseDateTime", responseDate.getTime());
        database.update("reconciliation", values, "reconciliationId=" + reconciliationRequestDto.getTransactionId(), null);
    }

    public void updateReconciliationErrorData(ReconciliationRequestDto reconciliationRequestDto, String responseData, String errorMsg) {
        ContentValues values = new ContentValues();
        values.put("responseData", responseData);
        Date responseDate = new Date();
        values.put("responseDateTime", responseDate.getTime());
        values.put("errorDescription", errorMsg);
        values.put("status", "Response Error");
        database.update("reconciliation", values, "reconciliationId=" + reconciliationRequestDto.getTransactionId(), null);
    }

    public void insertTableUpgrade(int android_version, String userLog, String status, String state, int androidNewVersion, String refId, String serverRefId) {
        ContentValues values = new ContentValues();
        try {
            values.put("android_old_version", android_version);
            values.put("ref_id", refId);
            values.put("android_new_version", androidNewVersion);
            values.put("description", userLog);
            values.put("status", status.toUpperCase());
            values.put("state", state);
            values.put("refer_id", serverRefId);
            SimpleDateFormat regDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
            values.put("created_date", regDate.format(new Date()));
            Log.e("updateinfo", "" + values);
            database.insert(FPSDBTables.TABLE_UPGRADE, null, values);
        } catch (Exception e) {
            Log.e("Table Upgrade", "Exception", e);
        }
    }

    public void insertTableUpgradeExec(int android_version, String userLog, String status, String state, int androidNewVersion, String refId, String serverRefId) {
        ContentValues values = new ContentValues();
        try {
            values.put("android_old_version", android_version);
            values.put("ref_id", refId);
            values.put("android_new_version", androidNewVersion);
            values.put("description", userLog);
            values.put("status", status.toUpperCase());
            values.put("state", state);
            SimpleDateFormat regDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
            values.put("created_date", "" + regDate.format(new Date()));
            values.put("server_status", 0);
            values.put("refer_id", serverRefId);
            database.insert(FPSDBTables.TABLE_UPGRADE, null, values);
        } catch (Exception e) {
            Log.e("Table Upgrade", e.toString(), e);
        }
    }

    // This function inserts details to TABLE_PRODUCTS;
    public boolean insertProductData(Set<ProductDto> productDto, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        ProductDto productDto1 = null;
        if (!productDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (ProductDto products : productDto) {
                    recordId = products.getId();
                    productDto1 = products;
                    ContentValues values = new ContentValues();
                    values.put(KEY_ID, products.getId());
                    values.put(FPSDBConstants.KEY_PRODUCT_NAME, products.getName().toUpperCase());
                    values.put(FPSDBConstants.KEY_LPRODUCT_NAME, products.getLocalProdName());
                    if (products.isDeleted()) {
                        values.put("isDeleted", 1);
                    } else {
                        values.put("isDeleted", 0);
                    }
                    values.put(FPSDBConstants.KEY_PRODUCT_CODE, products.getCode());
                    values.put("groupId", products.getGroupId());
                    values.put(FPSDBConstants.KEY_LPRODUCT_UNIT, products.getLocalProdUnit());
                    values.put(FPSDBConstants.KEY_PRODUCT_UNIT, products.getProductUnit().toUpperCase());
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
                    values.put("sequenceNo", products.getSequence_No());
                    database.insertWithOnConflict(FPSDBTables.TABLE_PRODUCTS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "products Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(productDto1);
                    insertSyncException("TABLE_PRODUCTS", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // This function inserts details to FPSDBTables.TABLE_BENEFICIARY;
    public boolean insertBeneficiaryData(Set<BeneficiaryDto> beneficiaryDtos, String syncType) {
        tableType = "";
        childRecordId = null;
        beneficiaryMemberDto1 = null;
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        BeneficiaryDto beneficiaryDtos1 = null;
        if (!beneficiaryDtos.isEmpty()) {
            database.beginTransaction();
            try {
                for (BeneficiaryDto beneficiary : beneficiaryDtos) {
                    tableType = "Parent";
                    recordId = beneficiary.getId();
                    beneficiaryDtos1 = beneficiary;
                    ContentValues values = new ContentValues();
                    Log.e("^^^^FPSDBHelper^^^^", "insertBeneficiaryData BeneficiaryDto = " + beneficiary);
                    values.put(KEY_ID, beneficiary.getId());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, beneficiary.getEncryptedUfc());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_FPS_ID, beneficiary.getFpsId());
                    if (StringUtils.isNotEmpty(beneficiary.getAregisterNum()))
                        values.put("aRegister", Integer.parseInt(beneficiary.getAregisterNum()));
                    else {
                        values.put("aRegister", -1);
                    }
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
                    values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, cardNumber.toUpperCase());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiary.getNumOfCylinder());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiary.getNumOfAdults());
                    values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiary.getNumOfChild());
                    values.put("aadharNumber", beneficiary.getFamilyHeadAadharNumber());
                    int active = 0;
                    if (beneficiary.isActive()) {
                        active = 1;
                    }
                    values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, active);
                    database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                    setBeneficiaryMemberData(beneficiary);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "insertBeneficiaryData() called Exception ->" + e);
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    if (tableType.equalsIgnoreCase("Parent")) {
                        String json = new Gson().toJson(beneficiaryDtos1);
                        insertSyncException("TABLE_BENEFICIARY", syncType, recordId, json);
                    } else if (tableType.equalsIgnoreCase("Child")) {
                        String json = new Gson().toJson(beneficiaryMemberDto1);
                        insertSyncException("TABLE_BENEFICIARY_MEMBER", syncType, childRecordId, json);
                    }
                }
            }
        }
        return isSuccessFullyInserted;
    }

    //This function inserts details to FPSDBTables.TABLE_BENEFICIARY;
    public void insertBeneficiaryNew(BeneficiaryDto beneficiary) {
        try {
            Log.e("FPSDBHelper", ">>>insertBeneficiaryNew mobile = " + beneficiary.getMobileNumber());
            ContentValues values = new ContentValues();
            Log.i("Beneficiary", beneficiary.toString());
            values.put(KEY_ID, beneficiary.getId());
            values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, beneficiary.getEncryptedUfc());
            values.put("aadharNumber", beneficiary.getFamilyHeadAadharNumber());
            values.put(FPSDBConstants.KEY_BENEFICIARY_FPS_ID, beneficiary.getFpsId());
            if (StringUtils.isNotEmpty(beneficiary.getAregisterNum()))
                values.put("aRegister", Integer.parseInt(beneficiary.getAregisterNum()));
            else {
                values.put("aRegister", -1);
            }
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
            values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, cardNumber.toUpperCase());
            values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiary.getNumOfCylinder());
            values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiary.getNumOfAdults());
            values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiary.getNumOfChild());
//            try{
//
//                if(beneficiary.getEncryptedUfc() != null && !beneficiary.getEncryptedUfc().isEmpty()){
//
//
//
//                    values.put("beneficiary_id", beneficiary.getNumOfChild());
//
//                }
//
//            }catch (Exception e){
//
//            }
            int active = 0;
            if (beneficiary.isActive()) {
                active = 1;
            }
            values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, active);
            setBeneficiaryMemberData(beneficiary);
            database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            Log.e("BeneficiaryData", e.toString(), e);
        }
    } //This function inserts details to FPSDBTables.TABLE_BENEFICIARY;

    public void insertBeneficiaryNewIn(BeneficiaryDto beneficiary) {
        tableType = "TABLE_BENEFICIARY_IN";
        Log.e("FPSDBHelper", ">>>insertBeneficiaryNew IN mobile = " + beneficiary.getMobileNumber());
        ContentValues values = new ContentValues();
        Log.e("db helper", "beneficiary_in..." + beneficiary.toString());
        values.put(KEY_ID, beneficiary.getId());
        values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, beneficiary.getEncryptedUfc());
        values.put(FPSDBConstants.KEY_BENEFICIARY_FPS_ID, beneficiary.getFpsId());
        if (StringUtils.isNotEmpty(beneficiary.getAregisterNum()))
            values.put("aRegister", Integer.parseInt(beneficiary.getAregisterNum()));
        else {
            values.put("aRegister", -1);
        }
        values.put(FPSDBConstants.KEY_BENEFICIARY_TIN, beneficiary.getTin());
        values.put(FPSDBConstants.KEY_BENEFICIARY_MOBILE, beneficiary.getMobileNumber());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE, beneficiary.getCreatedDate());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CREATED_DATE, beneficiary.getCreatedDate());
        values.put(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_BY, beneficiary.getModifiedBy());
        values.put(FPSDBConstants.KEY_BENEFICIARY_MODIFIED_DATE, beneficiary.getModifiedDate());
        values.put(FPSDBConstants.KEY_BENEFICIARY_STATE_ID, beneficiary.getStateId());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CARD_TYPE_ID, beneficiary.getCardTypeId());
        values.put(FPSDBConstants.KEY_BENEFICIARY_DISTRICT_ID, beneficiary.getDistrictId());
        values.put(FPSDBConstants.KEY_BENEFICIARY_TALUK_ID, beneficiary.getTalukId());
        values.put(FPSDBConstants.KEY_BENEFICIARY_VILLAGE_ID, beneficiary.getVillageId());
        String cardNumber = beneficiary.getOldRationNumber().replaceAll("[^a-zA-Z0-9]", "");
        values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, cardNumber.toUpperCase());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiary.getNumOfCylinder());
        values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiary.getNumOfAdults());
        values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiary.getNumOfChild());
        values.put("aadharNumber", beneficiary.getFamilyHeadAadharNumber());
        int active = 0;
        if (beneficiary.isActive()) {
            active = 1;
        }
        values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, active);
        setBeneficiaryMemberDataIn(beneficiary);
        database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY_IN, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean updateBeneficiary(MigrationOutDTO beneficiary) {
        try {
            ContentValues values = new ContentValues();
            Log.i("Beneficiary", beneficiary.toString());
            values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, 0);
            database.update(FPSDBTables.TABLE_BENEFICIARY, values, KEY_ID + "=" + beneficiary.getBeneficiaryId(), null);
            return true;
        } catch (Exception e) {
            Log.e("BeneficiaryData", e.toString(), e);
            return false;
        }
    }

    public boolean updateCloseDale(CloseSaleTransactionDto closeSaleTransactionDto) {
        try {
            ContentValues values = new ContentValues();
            Log.i("Beneficiary", closeSaleTransactionDto.toString());
            values.put("isServerAdded", 0);
            database.update("close_sale", values, "transactionId=" + closeSaleTransactionDto.getTransactionId(), null);
            return true;
        } catch (Exception e) {
            Log.e("BeneficiaryData", e.toString(), e);
            return false;
        }
    }

    public boolean updateMigrationOut(MigrationOutDTO beneficiary) {
        try {
            ContentValues values = new ContentValues();
            Log.i("Beneficiary", beneficiary.toString());
            values.put("isAdded", 1);
            database.update(FPSDBTables.TABLE_FPS_MIGRATION_OUT, values, KEY_ID + " =" + beneficiary.getId(), null);
            database.execSQL("Update beneficiary set active = '0' where _id = " + beneficiary.getBeneficiaryId());
            return true;
        } catch (Exception e) {
            Log.e("db helper", "update migration out exception..." + e);
            return false;
        }
    }

    public void updateMigrationIn(MigrationOutDTO beneficiary) {
        try {
            ContentValues values = new ContentValues();
            Log.e("db helper", "update migration in beneficiary details..." + beneficiary);
            values.put("isAdded", 1);
            database.update(FPSDBTables.TABLE_FPS_MIGRATION_IN, values, KEY_ID + "=" + beneficiary.getId(), null);
            try {
                database.execSQL("INSERT INTO beneficiary SELECT * FROM beneficiary_in where _id = " + beneficiary.getBeneficiaryId());
            } catch (Exception e) {
                Log.e("db helper", "update migration in benef exception..." + e);
            }
            try {
                database.execSQL("INSERT INTO beneficiary_member SELECT * FROM beneficiary_member_in where ufc_code IN (Select ufc_code from beneficiary_in where _id = " + beneficiary.getBeneficiaryId() + ")");
            } catch (Exception e) {
                Log.e("db helper", "update migration in benef member exception..." + e);
            }

             /*insertBeneficiaryInToBeneficiary(beneficiary);
             insertBeneficiaryMemberInToBeneficiaryMember(beneficiary);*/
        } catch (Exception e) {
            Log.e("db helper", "update migration in exception..." + e);
        }
    }

    //This function inserts details to FPSDBTables.TABLE_BENEFICIARY;
    public void insertBeneficiaryInToBeneficiary(MigrationOutDTO migrationOut) {
        try {
            String selectQuery = "INSERT INTO beneficiary SELECT * FROM beneficiary_in where _id = " + migrationOut.getBeneficiaryId();
            Cursor cursor = database.rawQuery(selectQuery, null);
            List<BeneficiaryDto> beneficiaryList = new ArrayList<>();
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    beneficiaryList.add(new BeneficiaryDto(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            ContentValues values = new ContentValues();
            for (BeneficiaryDto beneficiary : beneficiaryList) {
                Log.i("Beneficiary", beneficiary.toString());
                values.put(KEY_ID, beneficiary.getId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, beneficiary.getEncryptedUfc());
                values.put(FPSDBConstants.KEY_BENEFICIARY_FPS_ID, beneficiary.getFpsId());
                if (StringUtils.isNotEmpty(beneficiary.getAregisterNum()))
                    values.put("aRegister", Integer.parseInt(beneficiary.getAregisterNum()));
                else {
                    values.put("aRegister", -1);
                }
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
                values.put(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION, cardNumber.toUpperCase());
                values.put(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO, beneficiary.getNumOfCylinder());
                values.put(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO, beneficiary.getNumOfAdults());
                values.put(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO, beneficiary.getNumOfChild());
                values.put("aadharNumber", beneficiary.getFamilyHeadAadharNumber());
                int active = 0;
                if (beneficiary.isActive()) {
                    active = 1;
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_ACTIVE, active);
                database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            Log.e("db helper", "update migration in benef exception..." + e);
        }
    }

    //This function inserts details to FPSDBTables.TABLE_BENEFICIARY_MEMBER;
    public void insertBeneficiaryMemberInToBeneficiaryMember(MigrationOutDTO beneficiary) {
        try {
            String selectQuery = "SELECT * FROM beneficiary_member_in where ufc_code IN (Select ufc_code from beneficiary_in where _id = " + beneficiary.getBeneficiaryId() + ")";
            Cursor cursor = database.rawQuery(selectQuery, null);
            List<BeneficiaryMemberDto> beneficiaryMemberList = new ArrayList<>();
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    beneficiaryMemberList.add(new BeneficiaryMemberDto(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            ContentValues values = new ContentValues();
            for (BeneficiaryMemberDto beneficiaryMember : beneficiaryMemberList) {
                values.put(KEY_ID, beneficiaryMember.getId());
//                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, fpsDataDto.getEncryptedUfc());
                if (beneficiaryMember.getTin() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN, beneficiaryMember.getTin());
                }
                if (beneficiaryMember.getUid() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, beneficiaryMember.getUid());
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID, beneficiaryMember.getEid());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME, beneficiaryMember.getLocalName());
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
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID, String.valueOf(beneficiaryMember.getGender())); //gender id
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB, beneficiaryMember.getDob());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID, String.valueOf(beneficiaryMember.getMstatusId()));
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME, beneficiaryMember.getEduName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME, beneficiaryMember.getOccuName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE, beneficiaryMember.getFatherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM, beneficiaryMember.getLocalFatherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE, beneficiaryMember.getMotherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM, beneficiaryMember.getLocalMotherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE, beneficiaryMember.getSpouseCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM, beneficiaryMember.getSpouseName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME, beneficiaryMember.getNatname());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1, beneficiaryMember.getAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2, beneficiaryMember.getAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3, beneficiaryMember.getAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4, beneficiaryMember.getAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5, beneficiaryMember.getAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1, beneficiaryMember.getLocalAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2, beneficiaryMember.getLocalAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3, beneficiaryMember.getLocalAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4, beneficiaryMember.getLocalAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5, beneficiaryMember.getLocalAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE, beneficiaryMember.getPincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR, beneficiaryMember.getDurationInYear());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1, beneficiaryMember.getPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2, beneficiaryMember.getPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3, beneficiaryMember.getPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4, beneficiaryMember.getPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5, beneficiaryMember.getPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1, beneficiaryMember.getLocalPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2, beneficiaryMember.getLocalPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3, beneficiaryMember.getLocalPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4, beneficiaryMember.getLocalPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5, beneficiaryMember.getLocalPaddressLine5());
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
                database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY_MEMBER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            Log.e("db helper", "update migration in benef member exception..." + e);
        }
    }

    /*//This function inserts details to FPSDBTables.TABLE_BENEFICIARY;
    public void insertTempBeneficiaryData(List<EntitlementMasterRule> rules) {
        for (EntitlementMasterRule entitlementMasterRule : rules) {
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, entitlementMasterRule.getId());
                values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, entitlementMasterRule.getProductId());
                values.put(FPSDBConstants.KEY_RULES_CARD_TYPE, entitlementMasterRule.getCardTypeId());
                if (entitlementMasterRule.getIsDeleted() != null && entitlementMasterRule.getIsDeleted())
                    values.put("isDeleted", 1);
                else {
                    values.put("isDeleted", 0);
                }
                values.put("groupId", entitlementMasterRule.getGroupId());
                NumberFormat formatter = new DecimalFormat("#0.000");
                values.put(FPSDBConstants.KEY_RULES_IS_CALC, returnInteger(entitlementMasterRule.isCalcRequired()));
                values.put(FPSDBConstants.KEY_SPECIAL_MINIMUM, returnInteger(entitlementMasterRule.isMinimumQty()));
                values.put(FPSDBConstants.KEY_SPECIAL_OVERRIDE, returnInteger(entitlementMasterRule.isOverridePrice()));
                values.put(FPSDBConstants.KEY_RULES_IS_PERSON, returnInteger(entitlementMasterRule.isPersonBased()));
                values.put(FPSDBConstants.KEY_RULES_IS_REGION, returnInteger(entitlementMasterRule.isRegionBased()));
                values.put(FPSDBConstants.KEY_RULES_HAS_SPECIAL, returnInteger(entitlementMasterRule.isHasSpecialRule()));
                values.put(FPSDBConstants.KEY_RULES_QUANTITY, formatter.format(entitlementMasterRule.getQuantity()));
                database.insertWithOnConflict(FPSDBTables.TABLE_ENTITLEMENT_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (Exception e) {
                Log.e("Master Rules", e.toString(), e);
            }
        }

    }*/

   /* public void insertTempPersonData(List<PersonBasedRule> rules) {
        for (PersonBasedRule personBasedRule : rules) {
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, personBasedRule.getId());
                if (personBasedRule.getIsDeleted() != null && personBasedRule.getIsDeleted())
                    values.put("isDeleted", 1);
                else {
                    values.put("isDeleted", 0);
                }
                values.put("groupId", personBasedRule.getGroupId());
                values.put(FPSDBConstants.KEY_RULES_PRODUCT_ID, personBasedRule.getProductId());
                NumberFormat formatter = new DecimalFormat("#0.000");
                values.put(FPSDBConstants.KEY_PERSON_MIN, formatter.format(personBasedRule.getMin()));
                values.put(FPSDBConstants.KEY_PERSON_MAX, formatter.format(personBasedRule.getMax()));
                values.put(FPSDBConstants.KEY_PERSON_CHILD, formatter.format(personBasedRule.getPerChild()));
                values.put(FPSDBConstants.KEY_PERSON_ADULT, formatter.format(personBasedRule.getPerAdult()));
                database.insertWithOnConflict(FPSDBTables.TABLE_PERSON_RULES, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (Exception e) {
                Log.e("Master Rules", e.toString(), e);
            }
        }

    }
*/

    // This function inserts details to FPSDBTables.TABLE_BENEFICIARY_MEMBER;
    private void setBeneficiaryMemberData(BeneficiaryDto fpsDataDto) {
        tableType = "Child";
        if (fpsDataDto.getBenefMembersDto() != null) {
            List<BeneficiaryMemberDto> beneficiaryMemberList = new ArrayList<>(fpsDataDto.getBenefMembersDto());
            for (BeneficiaryMemberDto beneficiaryMember : beneficiaryMemberList) {
                childRecordId = beneficiaryMember.getId();
                beneficiaryMemberDto1 = beneficiaryMember;
                ContentValues values = new ContentValues();
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "setBeneficiaryMemberData BeneficiaryMemberDto : " + beneficiaryMember);
                values.put(KEY_ID, beneficiaryMember.getId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, fpsDataDto.getEncryptedUfc());
                if (beneficiaryMember.getTin() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN, beneficiaryMember.getTin());
                }
                if (beneficiaryMember.getUid() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, beneficiaryMember.getUid());
                }
                if (fpsDataDto.getId() != null) {
                    values.put("beneficiary_id", fpsDataDto.getId());
                }
                if (beneficiaryMember.getFirstName() == null && beneficiaryMember.getName() == null && beneficiaryMember.getLocalName() == null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, "");
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID, beneficiaryMember.getEid());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME, beneficiaryMember.getLocalName());
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
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID, String.valueOf(beneficiaryMember.getGender())); //gender id
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB, beneficiaryMember.getDob());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID, String.valueOf(beneficiaryMember.getMstatusId()));
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME, beneficiaryMember.getEduName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME, beneficiaryMember.getOccuName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE, beneficiaryMember.getFatherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM, beneficiaryMember.getLocalFatherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE, beneficiaryMember.getMotherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM, beneficiaryMember.getLocalMotherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE, beneficiaryMember.getSpouseCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM, beneficiaryMember.getSpouseName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME, beneficiaryMember.getNatname());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1, beneficiaryMember.getAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2, beneficiaryMember.getAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3, beneficiaryMember.getAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4, beneficiaryMember.getAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5, beneficiaryMember.getAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1, beneficiaryMember.getLocalAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2, beneficiaryMember.getLocalAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3, beneficiaryMember.getLocalAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4, beneficiaryMember.getLocalAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5, beneficiaryMember.getLocalAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE, beneficiaryMember.getPincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR, beneficiaryMember.getDurationInYear());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1, beneficiaryMember.getPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2, beneficiaryMember.getPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3, beneficiaryMember.getPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4, beneficiaryMember.getPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5, beneficiaryMember.getPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1, beneficiaryMember.getLocalPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2, beneficiaryMember.getLocalPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3, beneficiaryMember.getLocalPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4, beneficiaryMember.getLocalPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5, beneficiaryMember.getLocalPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE, beneficiaryMember.getPpincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED, beneficiaryMember.getDateDataEntered());
                if (beneficiaryMember.isIsremoved()) {
                    values.put("is_removed", 1);
                } else {
                    values.put("is_removed", 0);
                }
                values.put("removed_date", beneficiaryMember.getRemovedDate());
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
                database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY_MEMBER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void setBeneficiaryChildMemberData(BeneficiaryMemberDto beneficiaryMember, String ufc) {
        ContentValues values = new ContentValues();
        try {
            Log.e("^^^^FPSDBHelper^^^^", "setBeneficiaryChildMemberData()");
            // Log.e("^^^^FPSDBHelper^^^^", "fpsDataDto = " + beneficiaryMember);
            if (beneficiaryMember != null) {
                Log.e("setBenefChiMembDa", "" + beneficiaryMember.toString());
                values.put(KEY_ID, beneficiaryMember.getId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, ufc);
                if (beneficiaryMember.getTin() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN, beneficiaryMember.getTin());
                }
                if (beneficiaryMember.getUid() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, beneficiaryMember.getUid());
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID, beneficiaryMember.getEid());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME, beneficiaryMember.getLocalName());
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
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID, String.valueOf(beneficiaryMember.getGender())); //gender id
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB, beneficiaryMember.getDob());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID, String.valueOf(beneficiaryMember.getMstatusId()));
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME, beneficiaryMember.getEduName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME, beneficiaryMember.getOccuName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE, beneficiaryMember.getFatherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM, beneficiaryMember.getLocalFatherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE, beneficiaryMember.getMotherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM, beneficiaryMember.getLocalMotherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE, beneficiaryMember.getSpouseCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM, beneficiaryMember.getSpouseName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME, beneficiaryMember.getNatname());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1, beneficiaryMember.getAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2, beneficiaryMember.getAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3, beneficiaryMember.getAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4, beneficiaryMember.getAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5, beneficiaryMember.getAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1, beneficiaryMember.getLocalAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2, beneficiaryMember.getLocalAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3, beneficiaryMember.getLocalAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4, beneficiaryMember.getLocalAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5, beneficiaryMember.getLocalAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE, beneficiaryMember.getPincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR, beneficiaryMember.getDurationInYear());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1, beneficiaryMember.getPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2, beneficiaryMember.getPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3, beneficiaryMember.getPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4, beneficiaryMember.getPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5, beneficiaryMember.getPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1, beneficiaryMember.getLocalPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2, beneficiaryMember.getLocalPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3, beneficiaryMember.getLocalPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4, beneficiaryMember.getLocalPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5, beneficiaryMember.getLocalPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_PIN_CODE, beneficiaryMember.getPpincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DATE_DATA_ENTERED, beneficiaryMember.getDateDataEntered());
                if (beneficiaryMember.isIsremoved()) {
                    values.put("is_removed", 1);
                } else {
                    values.put("is_removed", 0);
                }
                values.put("removed_date", beneficiaryMember.getRemovedDate());
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
                database.insert(FPSDBTables.TABLE_BENEFICIARY_MEMBER, null, values);
                //  database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY_MEMBER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            Log.e("Bene_member Exc...", e.toString(), e);
        }
    }

    // This function inserts details to FPSDBTables.TABLE_BENEFICIARY_MEMBER;
    private void setBeneficiaryMemberDataIn(BeneficiaryDto fpsDataDto) {
        tableType = "TABLE_BENEFICIARY_MEMBER_IN";
        if (fpsDataDto.getBenefMembersDto() != null) {
            List<BeneficiaryMemberDto> beneficiaryMemberList = new ArrayList<>(fpsDataDto.getBenefMembersDto());
            for (BeneficiaryMemberDto beneficiaryMember : beneficiaryMemberList) {
                ContentValues values = new ContentValues();
                values.put(KEY_ID, beneficiaryMember.getId());
                values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, fpsDataDto.getEncryptedUfc());
                if (beneficiaryMember.getTin() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_TIN, beneficiaryMember.getTin());
                }
                if (beneficiaryMember.getUid() != null) {
                    values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_UID, beneficiaryMember.getUid());
                }
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EID, beneficiaryMember.getEid());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LNAME, beneficiaryMember.getLocalName());
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
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_GENDER_ID, String.valueOf(beneficiaryMember.getGender())); //gender id
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DOB, beneficiaryMember.getDob());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_STATUS_ID, String.valueOf(beneficiaryMember.getMstatusId()));
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_EDU_NAME, beneficiaryMember.getEduName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_OCCUPATION_NAME, beneficiaryMember.getOccuName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_CODE, beneficiaryMember.getFatherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_FATHER_NM, beneficiaryMember.getLocalFatherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_CODE, beneficiaryMember.getMotherCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_MOTHER_NM, beneficiaryMember.getLocalMotherName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_CODE, beneficiaryMember.getSpouseCode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_SPOUSE_NM, beneficiaryMember.getSpouseName());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_NAT_NAME, beneficiaryMember.getNatname());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_1, beneficiaryMember.getAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_2, beneficiaryMember.getAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_3, beneficiaryMember.getAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_4, beneficiaryMember.getAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_ADDRESS_LINE_5, beneficiaryMember.getAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_1, beneficiaryMember.getLocalAddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_2, beneficiaryMember.getLocalAddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_3, beneficiaryMember.getLocalAddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_4, beneficiaryMember.getLocalAddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LADDRESS_LINE_5, beneficiaryMember.getLocalAddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_PIN_CODE, beneficiaryMember.getPincode());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_DURATION_IN_YEAR, beneficiaryMember.getDurationInYear());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_1, beneficiaryMember.getPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_2, beneficiaryMember.getPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_3, beneficiaryMember.getPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_4, beneficiaryMember.getPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_P_ADDRESS_LINE_5, beneficiaryMember.getPaddressLine5());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_1, beneficiaryMember.getLocalPaddressLine1());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_2, beneficiaryMember.getLocalPaddressLine2());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_3, beneficiaryMember.getLocalPaddressLine3());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_4, beneficiaryMember.getLocalPaddressLine4());
                values.put(FPSDBConstants.KEY_BENEFICIARY_MEMBER_LP_ADDRESS_LINE_5, beneficiaryMember.getLocalPaddressLine5());
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
                database.insertWithOnConflict(FPSDBTables.TABLE_BENEFICIARY_MEMBER_IN, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
    }

    public void updateRoles(long userId) {
        ContentValues values = new ContentValues();
        values.put("isDeleted", 0);
        database.update(FPSDBTables.TABLE_ROLE_FEATURE, values, "user_id=" + userId, null);
    }

    //SELECT count(*) as bill_count FROM bill ;
    public void setLastLoginTime(long userId) {
        try {
            Log.e("^^^^FPSDBHelper^^^^", "setLastLoginTime() called userId = " + userId);
            ContentValues values = new ContentValues();
            if (SessionId.getInstance().getLoginTime() != null)
                values.put("last_login_time", SessionId.getInstance().getLoginTime().getTime() + "");
            else {
                values.put("last_login_time", new Date().getTime() + "");
            }
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.update(FPSDBTables.TABLE_USERS, values, KEY_ID + " = " + userId, null);
        } catch (Exception e) {
            Log.e("Login Time Error", e.toString(), e);
        }
    }

    public void updateLoginHistory(String transactionId, String logoutType) {
        try {
            Log.e("^^^^FPSDBHelper^^^^", "updateLoginHistory() called transactionId = " + transactionId);
            Log.e("^^^^FPSDBHelper^^^^", "updateLoginHistory() called logoutType = " + logoutType);
            ContentValues values = new ContentValues();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            values.put("logout_time", df.format(new Date()));
            values.put("logout_type", logoutType);
            values.put("is_logout_sync", 0);
            /*if (logoutType.equalsIgnoreCase("ONLINE_LOGOUT") || logoutType.equalsIgnoreCase("CLOSE_SALE_LOGOUT_ONLINE")) {
                values.put("is_logout_sync", 1);
            } else {
                values.put("is_logout_sync", 0);
            }*/
            SQLiteDatabase db = this.getWritableDatabase();

           /* if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }*/
            //    Log.e("^^^^FPSDBHelper^^^^", "updateLoginHistory values " + df.format(new Date()) + " , " + logoutType + " , " + transactionId);
            db.update(FPSDBTables.TABLE_LOGIN_HISTORY, values, "transaction_id='" + transactionId + "'", null);
        } catch (Exception e) {
            Log.e("Login History", e.toString(), e);
        }
    }

    public void updateLoginHistory(String transactionId) {
        try {
            ContentValues values = new ContentValues();
            values.put("is_logout_sync", 1);
            values.put("is_sync", 1);
            if (!database.isOpen()) {
                database = dbHelper.getWritableDatabase();
            }
            database.update(FPSDBTables.TABLE_LOGIN_HISTORY, values, "transaction_id='" + transactionId + "'", null);
        } catch (Exception e) {
            Log.e("Login History", e.toString(), e);
        }
    }

    //Update Stock Inward
    public void updateStockInward(String referenceNo) {
        ContentValues values = new ContentValues();
        Log.e("Stock Req Dto", referenceNo);
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
        values.put("is_server_add", 0);
        database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, "referenceNo = '" + referenceNo + "'", null);
    }

    //Update Stock Inward
    public void updateStockInwardNew() {
        ContentValues values = new ContentValues();
        values.put("is_server_add", 1);
        database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, "fps_ack_status = 1", null);
    }

    //Insert into Stock history
    public void updateRegistration(String cardNumber, String status, String description) {
        ContentValues values = new ContentValues();
        try {
            values.put(FPSDBConstants.KEY_REGISTRATION_STATUS, status);
            values.put(FPSDBConstants.KEY_REGISTRATION_DESC, description);
            values.put(FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED, 0);
            database.update(FPSDBTables.TABLE_REGISTRATION, values, FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO + "='" + cardNumber.toUpperCase() + "'", null);
        } catch (Exception e) {
            Log.e("update registration", e.toString(), e);
        }
    }

    private boolean deleteCardDetails(String cardNo) {
        ContentValues values = new ContentValues();
        try {
            values.put(FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED, 1);
            database.update(FPSDBTables.TABLE_REGISTRATION, values, FPSDBConstants.KEY_REGISTRATION_CARD_NO + "='" + cardNo.toUpperCase() + "'", null);
        } catch (Exception e) {
            Log.e("CardDetails", e.toString(), e);
            return false;
        }
        return true;
    }

    //Update the stock
    public void stockUpdate(List<FPSStockDto> stock) {
        Log.e("^^^^FPSDBHelper^^^^", "stockUpdate called List<FPSStockDto> = " + stock);
        try {
            for (FPSStockDto fpsStockDto : stock) {
                Log.e("^^^^FPSDBHelper^^^^", "stockUpdate = " + stock);
                ContentValues values = new ContentValues();
            /*NumberFormat formatter = new DecimalFormat("#0.000");
            formatter.setRoundingMode(RoundingMode.CEILING);*/
                Double qty2 = Double.parseDouble(Util.quantityRoundOffFormat(fpsStockDto.getQuantity()));
                values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty2);
                database.update(FPSDBTables.TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + fpsStockDto.getProductId(), null);
            }
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "stockUpdate Exception =  " + e);
        }
    }

    //Update the bill
    public void billUpdate(BillDto bill) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "billUpdate() called BillDto = " + bill);
        try {
            ContentValues values = new ContentValues();
            values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
            values.put(FPSDBConstants.KEY_BILL_SERVER_REF_ID, bill.getBillRefId());
            values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
            values.put(FPSDBConstants.KEY_BILL_STATUS, "T");
            // Log.e("^^^^FPSDBHelper^^^^", "billUpdate() - " + bill);
            // Log.e("^^^^FPSDBHelper^^^^", "getMode() - " + bill.getMode());
            database.update(FPSDBTables.TABLE_BILL, values, FPSDBConstants.KEY_BILL_TRANSACTION_ID + "='" + bill.getTransactionId() + "'", null);
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "billUpdate() called Exception = " + e);
        }
    }

    //Update the bill
    public void adjustmentUpdate(POSStockAdjustmentDto bill) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "adjustmentUpdate() called POSStockAdjustmentDto = " + bill);
        ContentValues values = new ContentValues();
        values.put("isServerAdded", 1);
        database.update("fps_stock_adjustment", values, KEY_ID + "='" + bill.getId() + "'", null);
    }

    //Update the bill
    public void updateCardRegistration(String billNo) {
        Log.e("FPSDBHelper", "update active status to 1 for old_ration_card_num = " + billNo
                + " In offline_activation table");
        ContentValues values = new ContentValues();
        values.put("active", 1);
        database.update("offline_activation", values, "old_ration_card_num='" + billNo + "'", null);
    }

    public boolean checkUpgradeFinished() {
        try {
            String selectQuery = "select case when state = 'EXECUTION' and server_status = 0 then 1 else 0 end as status from table_upgrade order by _id desc limit 1";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                int state = cursor.getInt(cursor.getColumnIndex("status"));
                cursor.close();
                if (state == 1) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void updateUpgradeExec() {
        ContentValues values = new ContentValues();
        try {
            SimpleDateFormat regDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
            values.put("execution_time", regDate.format(new Date()));
            values.put("server_status", 1);
            database.update(FPSDBTables.TABLE_UPGRADE, values, " _id in (select max(_id) from table_upgrade)", null);
        } catch (Exception e) {
            Log.e("DB ERROR", e.toString(), e);
        }
    }

    public Cursor getCurerntVersonExec() {
        String selectQuery = "select * from table_upgrade order by _id desc limit 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        return cursor;
    }

    //Bill for background sync
    public List<StockRequestDto> getAllStockSync() {
        List<StockRequestDto> stockRequestDtos = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where is_server_add = 1 group by referenceNo";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            StockRequestDto manualStocks = new StockRequestDto();
            manualStocks.setType(StockTransactionType.INWARD);
            manualStocks.setFpsId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID)));
            manualStocks.setBatchNo(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO)) + "");
            manualStocks.setUnit(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT)) + "");
            String refNo = cursor.getString(cursor.getColumnIndex("referenceNo")) + "";
            manualStocks.setReferenceNo(refNo);
            manualStocks.setGodownId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID)));
            manualStocks.setDeliveryChallanId(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID)) + "");
            String value = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
            try {
                Long fpsAckDate = 0l;
                if (value != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = sdf.parse(value);
                    fpsAckDate = date.getTime();
                }
                manualStocks.setDate(fpsAckDate);
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }
            manualStocks.setCreatedBy(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY)) + "");
            manualStocks.setProductLists(getProduct(refNo));
            stockRequestDtos.add(manualStocks);
            cursor.moveToNext();
        }
        cursor.close();
        return stockRequestDtos;
    }

    //Bill for background sync
    public StockRequestDto getAllStockSync(String referenceNumber) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where is_server_add = 1 and referenceNo='" + referenceNumber + "' group by referenceNo";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            StockRequestDto manualStocks = new StockRequestDto();
            manualStocks.setType(StockTransactionType.INWARD);
            manualStocks.setFpsId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID)));
            manualStocks.setBatchNo(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO)) + "");
            manualStocks.setUnit(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT)) + "");
            manualStocks.setReferenceNo(referenceNumber);
            manualStocks.setGodownId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID)));
            manualStocks.setDeliveryChallanId(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID)) + "");
            String value = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
            try {
                Long fpsAckDate = 0l;
                if (value != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = sdf.parse(value);
                    fpsAckDate = date.getTime();
                }
                manualStocks.setDate(fpsAckDate);
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }
            manualStocks.setCreatedBy(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_CREATEDBY)) + "");
            manualStocks.setProductLists(getProduct(referenceNumber));
            cursor.close();
            return manualStocks;
        } else {
            return null;
        }
    }

    public void updateAckDate() {
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAckDate() called ");
            String selectQuery = "SELECT * FROM stock_inward where fps_ack_date is NOT null and is_server_add is null";
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAckDate() selectQuery ->" + selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                Long ackDate = cursor.getLong(cursor.getColumnIndex("fps_ack_date"));
                long id = cursor.getLong(cursor.getColumnIndex("_id"));
                ContentValues values = new ContentValues();
                if (ackDate != null && ackDate > 0) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = new Date(ackDate);
                    values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
                    values.put("is_server_add", 0);
                    database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, "_id =" + id, null);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAckDate() Exception ->" + e);
        }
    }

    public String getMinimumBillDate() {
        String minDate = "";
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMinimumBillDate() called ");
            String selectQuery = "select min(date(date)) as min_date from bill";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            minDate = cursor.getString(cursor.getColumnIndex("min_date"));
            Log.e("db helper", "date before replace..." + cursor.getString(cursor.getColumnIndex("min_date")));
            minDate = minDate.replace("-", "~");
            Log.e("db helper", "date after replace..." + minDate);
            cursor.close();
        } catch (Exception e) {
        }
        return minDate;
    }

    private List<StockRequestDto.ProductList> getProduct(String referenceNo) {
        String selectQuery = "SELECT * FROM stock_inward where referenceNo ='" + referenceNo + "'";
        List<StockRequestDto.ProductList> productList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            StockRequestDto.ProductList manualProduct = new StockRequestDto.ProductList();
            manualProduct.setId(cursor.getLong(cursor.getColumnIndex("product_id")));
            manualProduct.setQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            manualProduct.setRecvQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            manualProduct.setServerId(cursor.getLong(cursor.getColumnIndex("_id")));
            int month = cursor.getInt(cursor.getColumnIndex("month"));
            int year = cursor.getInt(cursor.getColumnIndex("year"));
            if (month == 0) {
                DateTime date = new DateTime().plusMonths(1);
                month = date.getMonthOfYear();
                year = date.getYearOfCentury();
            }
            manualProduct.setMonth(month);
            manualProduct.setYear(year);
            try {
                if (cursor.getString(cursor.getColumnIndex("inwardType")).equalsIgnoreCase("R")) {
                    manualProduct.setType(StockTransactionType.INWARD);
                } else if (cursor.getString(cursor.getColumnIndex("inwardType")).equalsIgnoreCase("A")) {
                    manualProduct.setType(StockTransactionType.STOCK_ADVANCE);
                }
            } catch (Exception e) {
            }
            productList.add(manualProduct);
            cursor.moveToNext();
        }
        cursor.close();
        return productList;
    }

    //Bill from local db
    public List<BillItemDto> getAllBillItems(long beneId, int month) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBillItems() beneID ->" + beneId + " month " + month);
        List<BillItemDto> billItems = new ArrayList<>();
        String selectQuery = "SELECT " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + ",SUM(quantity) as total FROM " + FPSDBTables.TABLE_BILL_ITEM
                + " where " + FPSDBConstants.KEY_BILL_BENEFICIARY + " = " + beneId + " AND " + FPSDBConstants.KEY_BILL_TIME_MONTH + " = "
                + month + " group by " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID;
        /// Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBillItems() QUERY->"+selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemDto billItemDto = new BillItemDto();
            billItemDto.setProductId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID)));
            billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        cursor.close();
        return billItems;
    }

    //Bill from local db
    public List<BillItemProductDto> getAllBillItems(String transactionId) {
        List<BillItemProductDto> billItems = new ArrayList<>();
        String selectQuery = "SELECT  product_id,transaction_id,name,quantity,cost,unit,local_unit,local_name FROM bill_item a inner join products b on a.product_id = b._id where " + FPSDBConstants.KEY_BILL_TRANSACTION_ID + " = '" + transactionId + "'  order by groupId,b._id";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemProductDto billItemDto = new BillItemProductDto(cursor);
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        cursor.close();
        return billItems;
    }

    //Bill from local db
    public List<BillDto> getAllBillsUser(long id) {
        List<BillDto> bills = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_BENEFICIARY + " = " + id
                + " order by date desc";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto(cursor);
            bills.add(billDto);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    //Bill from local db
    public List<BillDto> getAllBillByDate(String dateToday, int position) {
        List<BillDto> bills = new ArrayList<>();
        long offSet = position * 50;
        String selectQuery = "SELECT ref_id,aRegister,server_bill_id,date,amount,transaction_id,bill_status,old_ration_card_num FROM bill a inner join beneficiary b on a.beneficiary_id = b._id where date like '" + dateToday + "%' "
                + " order by date desc limit 50  OFFSET " + offSet;
        Log.i("selectQuery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto();
            billDto.setBillRefId(cursor.getLong(cursor.getColumnIndex("ref_id")));
            billDto.setARegisterNo(cursor.getString(cursor.getColumnIndex("aRegister")));
            billDto.setBillStatus(cursor.getString(cursor.getColumnIndex("bill_status")));
            billDto.setBillDate(cursor.getString(cursor.getColumnIndex("date")));
            billDto.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            billDto.setTransactionId(cursor.getString(cursor.getColumnIndex("transaction_id")));
            billDto.setRationCardNumber(cursor.getString(cursor.getColumnIndex("old_ration_card_num")));
            bills.add(billDto);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    //Bill from local db
    public List<BillInbetweenDto> getAllBillByFromToDate(String dateFrom, String dateTo, int position) {
        List<BillInbetweenDto> bills = new ArrayList<>();
        long offSet = position * 50;
//        String selectQuery = " SELECT c.name, c.unit, b.quantity, b.totalcost FROM bill_item b, products c where b.product_id = c._id and createdDate BETWEEN '"+dateFrom+"' AND '"+dateTo+"' order by createdDate limit 50  OFFSET " + offSet;
        /*String selectQuery = "SELECT c.name, c.unit, sum(b.quantity), sum(b.totalcost) FROM bill_item b, products c where b.product_id = c._id and createdDate BETWEEN '"+dateFrom+"' \n" +
                "AND '"+dateTo+"' group by  c.name"; */
        String selectQuery = "SELECT c.name, c.unit, c.local_name, c.local_unit, sum(b.quantity) as quantity, sum(b.totalcost) as totalCost FROM bill_item b, products c where b.product_id = c._id and DATE(createdDate) BETWEEN '" + dateFrom + "' \n" +
                "AND '" + dateTo + "'  group by  c.name";
        Log.i("selectQuery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillInbetweenDto billInbetweenDto = new BillInbetweenDto();
            billInbetweenDto.setProductName(cursor.getString(cursor.getColumnIndex("name")));
            billInbetweenDto.setProductUnit(cursor.getString(cursor.getColumnIndex("unit")));
            billInbetweenDto.setLocalProductName(cursor.getString(cursor.getColumnIndex("local_name")));
            billInbetweenDto.setLocalProductUnit(cursor.getString(cursor.getColumnIndex("local_unit")));
            billInbetweenDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            billInbetweenDto.setCost(cursor.getDouble(cursor.getColumnIndex("totalCost")));
            bills.add(billInbetweenDto);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    public long getAllUnsyncBills() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM bill where bill_status = 'R'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getEntitlementRulesCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM entitlement_rules";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getPersonRulesCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM person_rules";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getRegionRulesCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM region_rules";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getSpecialRulesCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM special_rules";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getBeneficiaryTableCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM beneficiary";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getBeneficiaryMemberTableCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM beneficiary_member";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getMembersAadhaarTableCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM members_aadhar";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getNfscaTableCount() {
        int count;
        String selectQuery = "SELECT count(*) as count FROM table_nfsa_pos_data";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public int getTotalFamilyMembers(String ufc) {
        int totalCount;
        String selectQuery = "select count(*) as total_members from beneficiary_member where ufc_code = '" + ufc + "' ";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        totalCount = cursor.getInt(cursor.getColumnIndex("total_members"));
        cursor.close();
        return totalCount;
    }

    public int getUnacknowledgedInward() {
        int totalCount;
//        String selectQuery = "select count(*) as unack_inward from stock_inward where fps_ack_status = 0";
        String selectQuery = "Select count ( distinct referenceNo ) as count from stock_inward where fps_ack_status = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        totalCount = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return totalCount;
    }

    public int getUnacknowledgedAdjustment() {
        int totalCount;
        String selectQuery = "SELECT count(*) as unack_adjustment FROM fps_stock_adjustment where isAdjusted = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        totalCount = cursor.getInt(cursor.getColumnIndex("unack_adjustment"));
        cursor.close();
        return totalCount;
    }

    public int getMembersAadharCount(String beneficiary_id) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharCount() beneficiary_id->" + beneficiary_id);
        int totalCount = 0;
        try {
            String selectQuery = " select count(*) as total from beneficiary_member where beneficiary_id = '" + beneficiary_id + "' and uid is not null";
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharCount() selectQuery->" + selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null) {
                cursor.moveToFirst();
                totalCount = cursor.getInt(cursor.getColumnIndex("total"));
            }
            cursor.close();
        } catch (Exception e) {
        }
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharCount() totalCount->" + totalCount);
        return totalCount;
    }

    public ArrayList<String> getBifurcationBenefId() {
        ArrayList<String> benefIdList = new ArrayList<String>();
        String selectQuery = "select benefId from Bifurcation where status = 0";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getBifurcationBenefId() called selectQuery = " + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                benefIdList.add(String.valueOf(cursor.getInt(cursor.getColumnIndex("benefId"))));
                cursor.moveToNext();
            }
        }
        cursor.close();
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "getBifurcationBenefId() Total No.of Bifurcation= " + benefIdList.size());
        return benefIdList;
    }

    public List<POSAadharAuthRequestDto> getLocalBiometric() {
        Cursor cursor = null;
        try {
            List<POSAadharAuthRequestDto> posAadharAuthRequestDtoList = new ArrayList<>();
            String selectQuery = "SELECT * FROM biometric_authentication where syncStatus = 0";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                posAadharAuthRequestDtoList.add(new POSAadharAuthRequestDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return posAadharAuthRequestDtoList;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<FailedKycDto> getFailedKYCRequest() {
        Cursor cursor = null;
        try {
            List<FailedKycDto> failedKycDtos = new ArrayList<>();
            String selectQuery = "SELECT * FROM kyc_request_details where ackStatus = 0";
            cursor = database.rawQuery(selectQuery, null);
            Log.e("dbhelper", "getFailedKYCRequest cursor count..." + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    failedKycDtos.add(new FailedKycDto(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return failedKycDtos;
        } catch (Exception e) {
            Log.e("dbhelper","getFailedKYCRequest exc..."+e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<BFDDetailDto> getLocalBfdDetails() {
        Cursor cursor = null;
        try {
            List<BFDDetailDto> bfdDetailDtos = new ArrayList<>();
            String selectQuery = "SELECT * FROM bfd_details where sync_status = 0";
            cursor = database.rawQuery(selectQuery, null);
            Log.e("dbhelper", "getLocalBfdDetails cursor count..." + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    bfdDetailDtos.add(new BFDDetailDto(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return bfdDetailDtos;
        } catch (Exception e) {
            Log.e("dbhelper","getLocalBfdDetails exc..."+e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public List<ProxyDetailDto> getLocalProxyDetails() {
        Cursor cursor = null;
        try {
            List<ProxyDetailDto> proxyDetailsDtos = new ArrayList<>();
            String selectQuery = "SELECT * FROM proxy_details where sync_status = 0";
            cursor = database.rawQuery(selectQuery, null);
            Log.e("dbhelper", "getLocalProxyDetails cursor count..." + cursor.getCount());
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    proxyDetailsDtos.add(new ProxyDetailDto(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
            return proxyDetailsDtos;
        } catch (Exception e) {
            Log.e("dbhelper","getLocalProxyDetails exc..."+e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public BFDDetailDto getLocalBfdDetailsForHead(String benefMemberId) {
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM bfd_details where benef_id = "+benefMemberId+" and proxy_id is null";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            BFDDetailDto bfdDetailDtos = new BFDDetailDto(cursor);
            cursor.close();
            return bfdDetailDtos;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public BFDDetailDto getLocalBfdDetailsForProxy(String benefMemberId, String proxyId) {
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT * FROM bfd_details where benef_id = "+benefMemberId+" and proxy_id = "+proxyId;
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            BFDDetailDto bfdDetailDtos = new BFDDetailDto(cursor);
            cursor.close();
            return bfdDetailDtos;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean checkBfd(long benefId) {
        Cursor cursor = null;
        String selectQuery = "SELECT * FROM bfd_details where benef_id = "+benefId+" and proxy_id is null";
        cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if((cursor != null) && (cursor.getCount() > 0)) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public ArrayList<String> getMembersAadharNumbers(String beneficiary_id) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharNumbers() beneficiary_id->" + beneficiary_id);
        ArrayList<String> aadharNos = new ArrayList<String>();
        String selectQuery = " select aadhar_number from members_aadhar where beneficiary_id = '" + beneficiary_id + "'";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharNumbers() selectQuery->" + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            aadharNos.add(cursor.getString(cursor.getColumnIndex("aadhar_number")));
            cursor.moveToNext();
        }
        cursor.close();
        //Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getMembersAadharNumbers() ArrayList of aadhar_number->" + aadharNos);
        return aadharNos;
    }

    /*public ArrayList<String> updateAllBeneficiaryIDsInBeneficiaryMemberTable(String id) {
        ArrayList<String> aadharNos = new ArrayList<String>();
        String selectQuery = " select aadhar_number from members_aadhar where beneficiary_id = '" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            aadharNos.add(cursor.getString(cursor.getColumnIndex("aadhar_number")));
            cursor.moveToNext();
        }
        cursor.close();
        return aadharNos;
    }
    */
    public int checkAadharNumber(long id) {
        int i = 0;
        try {
            String selectQuery = " select * from beneficiary_member where uid = '" + String.valueOf(id) + "'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                i = 1;
            }
            cursor.close();
        }
        catch(Exception e) {}
        return i;
    }

    public int checkAadharNumberForBeneficiary(long id) {
        int i = 0;
        String selectQuery = " select * from beneficiary_member where uid = '" + id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            i = 1;
        }
        cursor.close();
        return i;
    }

    public int getTotalTransFromTo(String dateFrom, String dateTo) {
        int count;
        String selectQuery = "select count(*) as count from bill where DATE(date) between '" + dateFrom + "' \n" +
                "AND '" + dateTo + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public Double getTotalAmountFromTo(String dateFrom, String dateTo) {
        Double count;
        String selectQuery = "select sum(totalCost) as count from bill_item where DATE(createdDate) between '" + dateFrom + "' \n" +
                "AND '" + dateTo + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getDouble(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getAllErrorMessages() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 12012";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 12004";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages2() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 30002";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages3() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 8800";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages4() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 13001";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages5() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 8809";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public long getErrorMessages6() {
        long count;
        String selectQuery = "SELECT count(*) as count FROM error_messages where code = 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //Bill from local db
    public List<BillDto> getAllBillById(long beneId, int position) {
        List<BillDto> bills = new ArrayList<>();
        long offSet = position * 50;
        String selectQuery = "SELECT ref_id,server_bill_id,date,amount,transaction_id,old_ration_card_num,aRegister,bill_status FROM bill a inner join beneficiary b on a.beneficiary_id = b._id where beneficiary_id ="
                + beneId + " order by date desc limit 50 OFFSET " + offSet;
        Log.i("selectQuery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto();
            billDto.setBillRefId(cursor.getLong(cursor.getColumnIndex("ref_id")));
            billDto.setBillDate(cursor.getString(cursor.getColumnIndex("date")));
            billDto.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            billDto.setTransactionId(cursor.getString(cursor.getColumnIndex("transaction_id")));
            billDto.setRationCardNumber(cursor.getString(cursor.getColumnIndex("old_ration_card_num")));
            billDto.setARegisterNo(cursor.getString(cursor.getColumnIndex("aRegister")));
            billDto.setBillStatus(cursor.getString(cursor.getColumnIndex("bill_status")));
            bills.add(billDto);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    //Bill from local db
    public List<BillDto> getAllBillByUnsync(int position) {
        List<BillDto> bills = new ArrayList<>();
        long offSet = position * 50;
        String selectQuery = "SELECT ref_id,server_bill_id,date,amount,transaction_id,old_ration_card_num,aRegister,bill_status FROM bill a inner join beneficiary b " +
                "on a.beneficiary_id = b._id where bill_status <>'T' order by date desc limit 50 OFFSET " + offSet;
        Log.i("selectQuery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto billDto = new BillDto();
            billDto.setBillRefId(cursor.getLong(cursor.getColumnIndex("ref_id")));
            billDto.setBillDate(cursor.getString(cursor.getColumnIndex("date")));
            billDto.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            billDto.setTransactionId(cursor.getString(cursor.getColumnIndex("transaction_id")));
            billDto.setRationCardNumber(cursor.getString(cursor.getColumnIndex("old_ration_card_num")));
            billDto.setARegisterNo(cursor.getString(cursor.getColumnIndex("aRegister")));
            billDto.setBillStatus(cursor.getString(cursor.getColumnIndex("bill_status")));
            bills.add(billDto);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    public Set<BillItemProductDto> getBillItems(String referenceId) {
        List<BillItemProductDto> billItems = new ArrayList<>();
        String selectQuery;
        if (GlobalAppState.language.equalsIgnoreCase("ta")) {
            selectQuery = "SELECT  product_id,transaction_id,name,quantity,cost,unit,local_unit,local_name FROM bill_item a inner join products b on a.product_id = b._id where " + FPSDBConstants.KEY_BILL_TRANSACTION_ID + "='" + referenceId + "' order by b.local_name";
        } else {
            selectQuery = "SELECT  product_id,transaction_id,name,quantity,cost,unit,local_unit,local_name FROM bill_item a inner join products b on a.product_id = b._id where " + FPSDBConstants.KEY_BILL_TRANSACTION_ID + "='" + referenceId + "' order by b.name";
        }
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemProductDto billItemDto = new BillItemProductDto(cursor);
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        cursor.close();
        return new HashSet<>(billItems);
    }

    //Bill from local db
    public BillUserDto getBill(long billId) {
        BillUserDto billDto;
        String selectQuery = "SELECT a.ref_id,a.server_bill_id,a.fps_id,a.date,a.transaction_id,a.server_ref_id,a.mode,b.ufc_code,a.channel,a.bill_status," +
                "a.beneficiary_id,a.amount,a.created_by,a.otpId,a.otpTime,a.created_date,b.old_ration_card_num,b.aRegister  from bill a inner join beneficiary b on a.beneficiary_id = b._id where " + FPSDBConstants.KEY_BILL_REF_ID + "=" + billId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        billDto = new BillUserDto(cursor);
        billDto.setBillItemDto(getBillItems(billDto.getTransactionId()));
        cursor.close();
        return billDto;
    }

    //Bill from local db
    public BillDto getBillByTransactionId(String billId) {
        BillDto billDto;
        String selectQuery = "SELECT a.ref_id,a.month,a.server_bill_id,a.fps_id,a.date,a.transaction_id,a.server_ref_id,a.mode,b.ufc_code,a.channel,a.bill_status," +
                "a.beneficiary_id,a.amount,a.created_by,a.otpId,a.otpTime,a.created_date,b.old_ration_card_num,b.aRegister from bill a inner join beneficiary b on a.beneficiary_id = b._id where transaction_id = '" + billId + "'";
        Log.e("selectQuery", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        billDto = new BillDto(cursor);
        billDto.setBillItemDto(getBillItems(billDto.getTransactionId()));
        cursor.close();
        return billDto;
    }

    // get beneficiary id
    public int getBeneficiaryId(String rationCardNum) {
        String selectQuery = "select _id as beneficiaryId from " + FPSDBTables.TABLE_BENEFICIARY + " where old_ration_card_num = '" + rationCardNum + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("beneficiaryId"));
        cursor.close();
        return count;
    }

    public long getBeneficiaryIdFromUid(String uid) {
        String selectQuery = "select _id as beneficiaryId from " + FPSDBTables.TABLE_BENEFICIARY +" where aadharNumber = '" +uid +"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if((cursor == null) && (cursor.getCount() == 0)) {
            cursor.close();
            return 0;
        }
        else {
            cursor.moveToFirst();
            long count = cursor.getLong(cursor.getColumnIndex("beneficiaryId"));
            cursor.close();
            return count;
        }
    }

    public long getBeneficiaryMemberIdFromUid(String uid) {
        String selectQuery = "select _id as beneficiaryMemberId from beneficiary_member where uid = '" +uid +"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        long count = cursor.getLong(cursor.getColumnIndex("beneficiaryMemberId"));
        cursor.close();
        return count;
    }

    public long getProxyMemberIdFromUid(String uid) {
        String selectQuery = "select _id as proxyMemberId from proxy_details where uid = '" +uid +"'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        long count = cursor.getLong(cursor.getColumnIndex("proxyMemberId"));
        cursor.close();
        return count;
    }

    public String getDistrictId(String _id) {
        String selectQuery = "select district_code as district_code from " + FPSDBTables.TABLE_USERS + " where _id = '" + _id + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String district_code = cursor.getString(cursor.getColumnIndex("district_code"));
        cursor.close();
        return district_code;
    }

    //BeneCount from local db
    public int getBeneficiaryCount() {
        String selectQuery = "SELECT  count(*) as count FROM " + FPSDBTables.TABLE_BENEFICIARY + " where active = 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //AdultCount from local db
    public int getAdultCount() {
        String selectQuery = "select sum(num_of_adults) as count FROM " + FPSDBTables.TABLE_BENEFICIARY + " where active = 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //ChildCount from local db
    public int getChildCount() {
        String selectQuery = "select sum(num_of_child) as count FROM " + FPSDBTables.TABLE_BENEFICIARY + " where active = 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //MobileYes Count from local db
    public int getMobileYesCount() {
        String selectQuery = "SELECT COUNT(*) as count FROM beneficiary WHERE active = 1 and mobile IS NOT NULL";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //MobileNo Count from local db
    public int getMobileNoCount() {
        String selectQuery = "SELECT COUNT(*) as count FROM beneficiary WHERE active = 1 and mobile IS NULL";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //AadharYes Count from local db
    public int getAadharYesCount() {
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "getAadharYesCount() called  ->");
        String selectQuery = "SELECT COUNT(*) as count FROM beneficiary WHERE active = 1 and aadharNumber IS NOT NULL";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "getAadharYesCount() called  count ->" + count);
        cursor.close();
        return count;
    }

    //Total AadharYes Count from local db
    public int getTotalAadharYesCount() {
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "getTotalAadharYesCount() called  ->");
        String selectQuery = "SELECT count(*) as count FROM beneficiary_member WHERE uid IS not null and uid  <> ''";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //AadharNo Count from local db
    public int getAadharNoCount() {
        String selectQuery = "SELECT COUNT(*) as count FROM beneficiary WHERE active = 1 and aadharNumber IS NULL";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getBeneficiaryUnSyncCount() {
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getBeneficiaryUnSyncCount() called ");
        String selectQuery = "Select count(*) as count from offline_activation where old_ration_card_num not in (Select old_ration_card_num from beneficiary)";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getBillUnSyncCount() {
        String selectQuery = "Select count(*) as count from bill where bill_status<>'T'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getInwardUnSyncCount() {
        String selectQuery = "Select count ( distinct referenceNo ) as count from stock_inward where fps_ack_status = 1 and is_server_add = 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getAdjustmentUnSyncCount() {
        String selectQuery = "Select count(*) as count from fps_stock_adjustment where isAdjusted = 1 and isServerAdded = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getMigrationOutUnSyncCount() {
        DateTime dateTime = new DateTime();
        String selectQuery = "Select count(*) as count from migration_out where isAdded = 0 and month_out=" + dateTime.getMonthOfYear() + " and year_out=" + dateTime.getYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getAdvanceStockUnSyncCount() {
        DateTime dateTime = new DateTime();
        String selectQuery = "Select count(*) as count from advance_stock_inward where isAdded = 1 and month =" + dateTime.getMonthOfYear() + " and year = " + dateTime.getYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //BeneCount from local db
    public int getMigrationInUnSyncCount() {
        DateTime dateTime = new DateTime();
        String selectQuery = "Select count(*) as count from migration_in where isAdded = 0 and month_in=" + dateTime.getMonthOfYear() + " and year_in=" + dateTime.getYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public Set<BeneficiaryMemberDto> getAllBeneficiaryMembers(String ufc_code) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBeneficiaryMembers() ufc_code->" + ufc_code);
        List<BeneficiaryMemberDto> beneficiaryMembers = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='"
                + ufc_code + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + "= 0 "
                + " order by tin";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBeneficiaryMembers() selectQuery->" + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            beneficiaryMembers.add(new BeneficiaryMemberDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return new HashSet<>(beneficiaryMembers);
    }

    public BeneficiaryMemberDto getHeadBeneficiaryMember(String qrCode, String headUid) {
        BeneficiaryMemberDto beneficiaryMembers = null;
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='"
                    + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + " = 0 " + "AND uid = " + headUid + " order by tin";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            beneficiaryMembers = new BeneficiaryMemberDto(cursor);
            cursor.close();
            return beneficiaryMembers;
        }
    }

    public BeneficiaryMemberDto getProxyBeneficiaryMember(String uid) {
        String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER + " where " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS +" = 0 "+"AND uid = "+uid+" order by tin";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor == null || cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            BeneficiaryMemberDto beneficiaryMembers = new BeneficiaryMemberDto(cursor);
            cursor.close();
            return beneficiaryMembers;
        }
    }

    public BeneficiaryMemberDto getSpecificBeneficiaryMember(String uid) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER + " where uid ='"
                + uid + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + "= 0 order by tin";
        Log.e("db helper", "specific benef member query..." + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        BeneficiaryMemberDto beneficiaryMemberDto = new BeneficiaryMemberDto(cursor);
        cursor.close();
        return beneficiaryMemberDto;
    }

    public Set<BeneficiaryMemberDto> getFamilyHeadAadharDetails(String qrCode) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBeneficiaryMembers() qrCode->" + qrCode);
        List<BeneficiaryMemberDto> beneficiaryMembers = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='"
                + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + "= 0 " + " AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_REL_NAME + " = 'Family Head'"
                + " order by tin";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllBeneficiaryMembers() selectQuery->" + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            beneficiaryMembers.add(new BeneficiaryMemberDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return new HashSet<>(beneficiaryMembers);
    }

    // Used to BeneficiaryDto beneficiary details
    public BeneficiaryDto retrieveBeneficiary(long id) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + KEY_ID + "=" + id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        BeneficiaryDto beneficiaryDto;
        if (cursor.moveToFirst()) {
            beneficiaryDto = new BeneficiaryDto(cursor);
            try {
                beneficiaryDto.setBenefMembersDto(getAllBeneficiaryMembers(beneficiaryDto.getUfc()));
            } catch (Exception e) {
            }
            Log.e("retrieveBeneficiary", "" + beneficiaryDto);
            cursor.close();
            return beneficiaryDto;
        }
        cursor.close();
        return null;
    }

    // Used to BeneficiaryDto beneficiary details
    public BeneficiaryDto retrieveBeneficiaryIn(long ufc) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_IN + " where _id=" + ufc;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        BeneficiaryDto beneficiaryDto;
        if (cursor.moveToFirst()) {
            beneficiaryDto = new BeneficiaryDto(cursor);
            beneficiaryDto.setBenefMembersDto(getAllBeneficiaryMembersIn(beneficiaryDto.getUfc()));
            cursor.close();
            return beneficiaryDto;
        }
        cursor.close();
        return null;
    }

    private Set<BeneficiaryMemberDto> getAllBeneficiaryMembersIn(String qrCode) {
        List<BeneficiaryMemberDto> beneficiaryMembers = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY_MEMBER_IN + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='"
                + qrCode + "' AND " + FPSDBConstants.KEY_BENEFICIARY_MEMBER_ALIVE_STATUS + "= 0 order by tin";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            beneficiaryMembers.add(new BeneficiaryMemberDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return new HashSet<>(beneficiaryMembers);
    }

    // Used to BeneficiaryDto beneficiary details
    public List<BeneficiaryDto> retrieveAllBeneficiary(String cardNumber) {
        Cursor cursor = null;
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveAllBeneficiary() cardNumber->" + cardNumber);
            String selectQuery = "SELECT old_ration_card_num, aRegister, active FROM " + FPSDBTables.TABLE_BENEFICIARY + " where old_ration_card_num like '%" + cardNumber + "' and active = 1";
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            List<BeneficiaryDto> beneficiaryDto = new ArrayList<>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                BeneficiaryDto benef = new BeneficiaryDto();
                benef.setOldRationNumber(cursor.getString(cursor.getColumnIndex("old_ration_card_num")));
                String aRegisterNumber = cursor.getString(cursor.getColumnIndex("aRegister"));
                if (aRegisterNumber == null || aRegisterNumber.equals("-1")) {
                    aRegisterNumber = "";
                }
                benef.setAregisterNum(aRegisterNumber);
                String active = cursor.getString(cursor.getColumnIndex("active"));
                if (active.equalsIgnoreCase("0")) {
                    benef.setActive(false);
                } else if (active.equalsIgnoreCase("1")) {
                    benef.setActive(true);
                }
                beneficiaryDto.add(benef);
                cursor.moveToNext();
            }
            return beneficiaryDto;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public List<ProxyDetailDto> retrieveProxy(long benefId) {
        Cursor cursor = null;
        String selectQuery = "select * from proxy_details where benef_id = "+benefId;
        SQLiteDatabase db = this.getReadableDatabase();
        cursor = db.rawQuery(selectQuery, null);
        List<ProxyDetailDto> proxyDetailsDtos = new ArrayList<>();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                proxyDetailsDtos.add(new ProxyDetailDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return proxyDetailsDtos;
        }
    }

    public ProxyDetailDto retrieveSpecificProxy(String proxyId, String benefId) {
        Cursor cursor = null;
        try {
            ProxyDetailDto proxyDetailsDtos = null;
            String selectQuery = "select * from proxy_details where server_id = "+proxyId+" and benef_id = "+benefId;
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                proxyDetailsDtos = new ProxyDetailDto(cursor);
                cursor.moveToNext();
            }
            return proxyDetailsDtos;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }




















    /*// Used to BeneficiaryDto beneficiary details
    public List<BeneficiarySearchDto> retrieveAllBeneficiarySearch(String cardNumber, String aRegister, int limitSize) {
        Cursor cursor = null;
        try {
            String selectQuery;
            int limitStart = limitSize * 100;
            if (aRegister.length() > 0)
                selectQuery = "SELECT a._id, old_ration_card_num, aRegister, aadharNumber, mobile,num_of_adults,num_of_child,num_of_cylinder,description, localDescription FROM beneficiary a inner join card_type b on a.card_type_id = b._id where substr(old_ration_card_num,4,7) like '%" + cardNumber + "%' AND aRegister like '%" + aRegister + "%'  AND a.active = 1 order by aRegister  limit 100 OFFSET " + limitStart;
            else {
                selectQuery = "SELECT a._id, old_ration_card_num, aRegister, aadharNumber, mobile,num_of_adults,num_of_child,num_of_cylinder,description, localDescription FROM beneficiary a inner join card_type b on a.card_type_id = b._id where substr(old_ration_card_num,4,7) like '%" + cardNumber + "%'  AND a.active = 1 order by aRegister  limit 100 OFFSET " + limitStart;
            }
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            List<BeneficiarySearchDto> beneficiaryDto = new ArrayList<>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                beneficiaryDto.add(new BeneficiarySearchDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return beneficiaryDto;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }*/

    public List<BeneficiarySearchDto> retrieveAllBeneficiarySearch(String cardNumber, String aRegister, int limitSize) {
        Cursor cursor = null;
        try {
            String selectQuery;
            int limitStart = limitSize * 100;
            if (aRegister.length() > 0)
                selectQuery = "SELECT a._id, old_ration_card_num, aRegister, aadharNumber, mobile,num_of_adults,num_of_child,num_of_cylinder,description, localDescription, ufc_code FROM beneficiary a inner join card_type b on a.card_type_id = b._id where old_ration_card_num like '%" + cardNumber + "%' AND aRegister like '%" + aRegister + "%'  AND a.active = 1 order by aRegister  limit 100 OFFSET " + limitStart;
            else {
                selectQuery = "SELECT a._id, old_ration_card_num, aRegister, aadharNumber, mobile,num_of_adults,num_of_child,num_of_cylinder,description, localDescription, ufc_code FROM beneficiary a inner join card_type b on a.card_type_id = b._id where old_ration_card_num like '%" + cardNumber + "%'  AND a.active = 1 order by aRegister  limit 100 OFFSET " + limitStart;
            }
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            List<BeneficiarySearchDto> beneficiaryDto = new ArrayList<>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                beneficiaryDto.add(new BeneficiarySearchDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            return beneficiaryDto;
        } catch (Exception e) {
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getBeneficiaryIdForUfc_Code(String ufc_code) {
        int beneficiaryId = 0;
        Cursor cursor = null;
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getBeneficiaryIdForUfc_Code started  ->");
            String selectQuery = "select _id as beneficiaryId from " + FPSDBTables.TABLE_BENEFICIARY + " where ufc_code = '" + ufc_code + "'";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            beneficiaryId = cursor.getInt(cursor.getColumnIndex("beneficiaryId"));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "cursor returned -> beneficiaryId =  " + beneficiaryId);
            cursor.close();
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getBeneficiaryIdForUfc_Code ended  ->");
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getBeneficiaryIdForUfc_Code Exception   ->" + e.toString());
        } finally {

           /* if (cursor != null) {
                cursor.close();
            }*/
            return beneficiaryId;
        }
    }

    public void updateAllBeneficiaryIDsinBeneficiaryMemberTable() {
        Cursor cursor = null;
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAllBeneficiaryIDsinBeneficiaryMemberTable started  ->");
            String selectQuery;
            selectQuery = "select * from beneficiary_member ";
            SQLiteDatabase db = this.getWritableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String ufc_code = cursor.getString(cursor.getColumnIndex("ufc_code"));
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", " ufc_code ->  " + ufc_code);
                int beneficiaryId = getBeneficiaryIdForUfc_Code(ufc_code);
                if (!(beneficiaryId <= 0)) {
                    db.execSQL("Update beneficiary_member set beneficiary_id  = " +
                            beneficiaryId +
                            " where ufc_code = '" +
                            "ufc_code" +
                            "'");
                }
                cursor.moveToNext();
            }
            cursor.close();
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAllBeneficiaryIDsinBeneficiaryMemberTable ended  ->");
        } catch (Exception e) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAllBeneficiaryIDsinBeneficiaryMemberTable Exception  ->" + e);
        } finally {
            /*if (cursor != null) {
                cursor.close();
            }*/
        }
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "updateAllBeneficiaryIDsinBeneficiaryMemberTable ended  ->");
    }

    // retrieve the details for ration card summary list
    public List<RationcardSummaryDto> retrieveCardTypeCount() {
        Cursor cursor = null;
        try {
            String selectQuery;
            selectQuery = "select  c.description, c.localDescription, count(c.description) as typeCount from beneficiary b, card_type c where b.active = 1 and b.card_type_id = c._id group by c.description";
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            List<RationcardSummaryDto> rationcardSummaryDto = new ArrayList<>();
            cursor.moveToFirst();
            Log.v("LoadMore", "cursor.getCount()..." + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                rationcardSummaryDto.add(new RationcardSummaryDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            Log.e("FPSDBhelper", "rationcardSummaryDto size...." + rationcardSummaryDto.size());
            return rationcardSummaryDto;
        } catch (Exception e) {
            Log.e("FPSDBhelper", "cardTypeCountquery exc...." + e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // retrieve the details for ration card summary list
    public List<UnitwiseSummaryDto> retrieveUnitWiseCount() {
        Cursor cursor = null;
        try {
            String selectQuery;
            selectQuery = "select Descptn, lDescptn, sum(oneunit) as 'oneunit',sum(onehalfunit) as 'onehalfunit',sum(twounit) as 'twounit',\n" +
                    "sum(twohalhunit) as 'twohalhunit',sum(threeaboveunit) as 'threeaboveunit' \n" +
                    "from \n" +
                    "(select descp as 'Descptn', lDescp as 'lDescptn', \n" +
                    "case when mcnt =1 then sum(card_cnt) else 0 end as 'oneunit',\n" +
                    "case when mcnt =1.5 then sum(card_cnt) else 0 end as 'onehalfunit' ,\n" +
                    "case when mcnt =2 then sum(card_cnt) else 0 end as 'twounit' ,\n" +
                    "case when mcnt =2.5 then sum(card_cnt) else 0 end as 'twohalhunit' ,\n" +
                    "case when mcnt >=3 then sum(card_cnt) else 0 end as 'threeaboveunit' \n" +
                    "from (select descrip as 'descp', lDescrip as 'lDescp', member_count as 'mcnt',count(*) as card_cnt from \n" +
                    "(select ct.description as 'descrip', ct.localDescription as 'lDescrip', (b.num_of_adults+(b.num_of_child*0.5)) as member_count \n" +
                    "from beneficiary as b \n" +
                    "join card_type as ct on b.card_type_id = ct._id where b.active = 1) tab \n" +
                    "group by descrip,member_count) t\n" +
                    "group by descp,mcnt) t1\n" +
                    "group by Descptn";
            SQLiteDatabase db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);
            List<UnitwiseSummaryDto> unitWiseSummaryDto = new ArrayList<>();
            cursor.moveToFirst();
            Log.v("LoadMore", "cursor.getCount()..." + cursor.getCount());
            for (int i = 0; i < cursor.getCount(); i++) {
                unitWiseSummaryDto.add(new UnitwiseSummaryDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            Log.e("FPSDBhelper", "unitWiseSummaryDto size...." + unitWiseSummaryDto.size());
            return unitWiseSummaryDto;
        } catch (Exception e) {
            Log.e("FPSDBhelper", "cardTypeCountquery exc...." + e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public boolean isTableExists(SQLiteDatabase db, String tableName) {
        String selectQuery = "SELECT * FROM sqlite_master WHERE name ='" + tableName + "' and type='table'";
        if (db == null) {
            db = this.getReadableDatabase();
        }
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return true;
        }
        cursor.close();
        return false;
    }

    //Get Beneficiary data by QR Code
    public BeneficiaryDto beneficiaryDto(String qrCode) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "beneficiaryDto() qrCode->" + qrCode);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode
                + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "beneficiaryDto() selectQuery->" + selectQuery);
        //  Log.e("dbhelper", "calculating entitlement..." + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "Bene Not found for this ufc = " + qrCode);
            cursor.close();
            return null;
        } else {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "Bene Found Check for Bene Member " + qrCode);
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(qrCode));
            cursor.close();
            return beneficiary;
        }
    }

    //Get Beneficiary data by QR Code
    public BeneficiaryDto getBeneficiaryDtoandFamilyHeadAadharDetails(String qrCode) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "beneficiaryDto() qrCode->" + qrCode);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode
                + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "beneficiaryDto() selectQuery->" + selectQuery);
        //  Log.e("dbhelper", "calculating entitlement..." + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "Bene Not found for this ufc = " + qrCode);
            cursor.close();
            return null;
        } else {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "Bene Found Check for Bene Member " + qrCode);
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            beneficiary.setBenefMembersDto(getFamilyHeadAadharDetails(qrCode));
            cursor.close();
            return beneficiary;
        }
    }

    public BeneficiaryDto beneficiaryFromOldCard(String rationCardNumber) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + "='"
                + rationCardNumber.toUpperCase() + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + " = 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            String ufc_code = cursor.getString(cursor.getColumnIndex("ufc_code"));
            beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(ufc_code));
            cursor.close();
            return beneficiary;
        }
    }

    public String getRationCardNumber(String beneficiaryId) {
        String rationCardNumber = "";
        try {
            String selectQuery = "SELECT old_ration_card_num FROM " + FPSDBTables.TABLE_BENEFICIARY + " where _id = '"
                    + beneficiaryId + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
            Log.i("query:  ", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            rationCardNumber = cursor.getString(cursor.getColumnIndex("old_ration_card_num"));
            cursor.close();
        } catch (Exception e) {
        }
        return rationCardNumber;
    }

    public void insertBiometric(POSAadharAuthRequestDto posAadharAuthRequestDto) {
        try {
            ContentValues values = new ContentValues();
            values.put("uid", posAadharAuthRequestDto.getUid());
            values.put("fpsId", posAadharAuthRequestDto.getFpsId());
            values.put("benefId", posAadharAuthRequestDto.getBeneficiaryId());
            values.put("authResponse", posAadharAuthRequestDto.getAuthReponse());
            values.put("authStatus", String.valueOf(posAadharAuthRequestDto.isAuthenticationStatus()));
            values.put("posReqDate", posAadharAuthRequestDto.getPosRequestDate());
            values.put("posRespDate", posAadharAuthRequestDto.getPosResponseDate());
            values.put("authReqDate", posAadharAuthRequestDto.getAuthRequestDate());
            values.put("authRespDate", posAadharAuthRequestDto.getAuthResponseDate());
            values.put("fingerPrintData", posAadharAuthRequestDto.getFingerPrintData());
            values.put("syncStatus", 0);
            database.insert("biometric_authentication", null, values);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "biometric_authentication Exception...", e.toString());
            try {
                POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                posSyncExceptionDto.setSyncMode("REGULAR_SYNC");
                posSyncExceptionDto.setTableName("biometric_authentication");
                posSyncExceptionDto.setAction("INSERT");
                posSyncExceptionDto.setRecordId(posAadharAuthRequestDto.getBeneficiaryId());
                String json = new Gson().toJson(posAadharAuthRequestDto);
                posSyncExceptionDto.setRawData(json);
                posSyncExceptionDto.setErrorDescription("Exception while inserting biometric_authentication");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());
                Date currentDate = df.parse(dateStr);
                posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
            } catch (Exception e1) {
                com.omneagate.Util.Util.LoggingQueue(contextValue, "biometric_authentication Exception 2...", e.toString());
            }
        }
    }

    public boolean insertBfdDetails(BFDDetailDto bfdDetailDto) {
        try {
            ContentValues values = new ContentValues();
            values.put("benef_id", bfdDetailDto.getBeneficiary().getId());
            values.put("server_id", bfdDetailDto.getId());
            try {
                values.put("proxy_id", bfdDetailDto.getProxyDetail().getId());
            }
            catch(Exception e) {}
            values.put("finger_01", bfdDetailDto.getBestFinger01());
            values.put("finger_02", bfdDetailDto.getBestFinger02());
            values.put("finger_03", bfdDetailDto.getBestFinger03());
            values.put("finger_04", bfdDetailDto.getBestFinger04());
            values.put("finger_05", bfdDetailDto.getBestFinger05());
            values.put("finger_06", bfdDetailDto.getBestFinger06());
            values.put("finger_07", bfdDetailDto.getBestFinger07());
            values.put("finger_08", bfdDetailDto.getBestFinger08());
            values.put("finger_09", bfdDetailDto.getBestFinger09());
            values.put("finger_10", bfdDetailDto.getBestFinger10());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("created_date", created.getTime());
            }
            catch(Exception e) {}
            values.put("created_by", SessionId.getInstance().getFpsId());
//            values.put("modified_date", bfdDetailsDto.getModifiedDate());
//            values.put("modified_by", bfdDetailsDto.getModifiedBy());
            int status;
            if (bfdDetailDto.getStatus()) {
                status = 1;
            }
            else {
                status = 0;
            }
            values.put("status", status);
            values.put("sync_status", "1");
            database.insert("bfd_details", null, values);
//            database.insertWithOnConflict("bfd_details", "benef_id", values, SQLiteDatabase.CONFLICT_REPLACE);
            return  true;
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "bfd_details Exception...", e.toString());
            try {
                POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                posSyncExceptionDto.setSyncMode("Statistics service");
                posSyncExceptionDto.setTableName("bfd_details");
                posSyncExceptionDto.setAction("INSERT");
                posSyncExceptionDto.setRecordId(bfdDetailDto.getBeneficiary().getId());
                String json = new Gson().toJson(bfdDetailDto);
                posSyncExceptionDto.setRawData(json);
                posSyncExceptionDto.setErrorDescription("Exception while inserting bfd_details");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());
                Date currentDate = df.parse(dateStr);
                posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
            }
            catch(Exception e1) {
                com.omneagate.Util.Util.LoggingQueue(contextValue, "bfd_details Exception 2...", e.toString());
            }
            return  false;
        }
    }

    public boolean insertProxyDetails(ProxyDetailDto proxyDetailsDto) {
        try {
            ContentValues values = new ContentValues();
            values.put("benef_id", proxyDetailsDto.getBeneficiary().getId());
            values.put("benef_member_id", proxyDetailsDto.getBeneficiaryMember().getId());
            values.put("server_id", proxyDetailsDto.getId());
            values.put("name", proxyDetailsDto.getName());
            values.put("uid", proxyDetailsDto.getUid());
            values.put("dob", proxyDetailsDto.getDob());
            values.put("mobile", proxyDetailsDto.getMobile());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("created_date", created.getTime());
            }
            catch(Exception e) {}
            values.put("created_by", SessionId.getInstance().getFpsId());
//            values.put("modified_date", proxyDetailsDto.getModifiedDate());
//            values.put("modified_by", proxyDetailsDto.getModifiedBy());
            values.put("approval_status", proxyDetailsDto.getRequestStatus());
            values.put("sync_status", "1");
            database.insert("proxy_details", null, values);
//            database.insertWithOnConflict("proxy_details", "uid", values, SQLiteDatabase.CONFLICT_REPLACE);
            return  true;
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "proxy_details Exception...", e.toString());
            try {
                POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                posSyncExceptionDto.setSyncMode("Statistics service");
                posSyncExceptionDto.setTableName("proxy_details");
                posSyncExceptionDto.setAction("INSERT");
                posSyncExceptionDto.setRecordId(proxyDetailsDto.getBeneficiary().getId());
                String json = new Gson().toJson(proxyDetailsDto);
                posSyncExceptionDto.setRawData(json);
                posSyncExceptionDto.setErrorDescription("Exception while inserting proxy_details");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());
                Date currentDate = df.parse(dateStr);
                posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
            }
            catch(Exception e1) {
                com.omneagate.Util.Util.LoggingQueue(contextValue, "proxy_details Exception 2...", e.toString());
            }
            return  false;
        }
    }

    public void insertKYCRequestDetails(byte[] fingerPrint, String aadhar, String benefId) {
        try {
            ContentValues values = new ContentValues();
            values.put("fingerPrintData", fingerPrint);
            values.put("aadharNumber", aadhar);
            values.put("benefId", benefId);
            values.put("ackStatus", 0);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                GregorianCalendar gc = new GregorianCalendar();
                String dateString = sdf.format(gc.getTime());
                Date created = sdf.parse(dateString);
                values.put("created_date", created.getTime());
            }
            catch(Exception e) {}
            values.put("created_by", SessionId.getInstance().getFpsId());
            database.insert("kyc_request_details", null, values);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "kyc_request_details Exception...", e.toString());
            try {
                POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
                posSyncExceptionDto.setSyncMode("Bfd Failure");
                posSyncExceptionDto.setTableName("kyc_request_details");
                posSyncExceptionDto.setAction("INSERT");
                /*posSyncExceptionDto.setRecordId(posAadharAuthRequestDto.getBeneficiaryId());
                String json = new Gson().toJson(posAadharAuthRequestDto);*/
                posSyncExceptionDto.setRawData(aadhar);
                posSyncExceptionDto.setErrorDescription("Exception while inserting kyc_request_details");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String dateStr = df.format(new Date());
                Date currentDate = df.parse(dateStr);
                posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
                FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
            }
            catch(Exception e1) {
                com.omneagate.Util.Util.LoggingQueue(contextValue, "kyc_request_details Exception 2...", e.toString());
            }
        }
    }


    //Get active value
    public int checkBlockStatus(String qrCode) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "checkBlockStatus() qrCode->" + qrCode);
        int active = 0;
        String selectQuery = "SELECT  active FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + "='"
                + qrCode.toUpperCase() + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            active = cursor.getInt(cursor.getColumnIndex("active"));
            cursor.close();
        }
        return active;
    }

    //Get Beneficiary data by QR Code
    public double productOverridePercentage(long productId, long cardType) {
        String selectQuery = "select  b.percentage as percent from card_type a inner join product_price_override b on b.card_type = a.type where a.isDeleted = 0 and b.is_deleted = 0 and " +
                "b.product_id = " + productId + " and  a._id = " + cardType;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        double percent = 0;
        if (cursor.getCount() > 0) {
            percent = cursor.getDouble(cursor.getColumnIndex("percent"));
        }
        cursor.close();
        return percent;
    }

    //Get Product data by Product Id
    public ProductDto getProductDetails(long _id) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;//+" AND " +FPSDBConstants.KEY_BENEFICIARY_ACTIVE +"=0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        ProductDto product = null;
        if (cursor.getCount() > 0) {
            product = new ProductDto(cursor);
        }
        cursor.close();
        return product;
    }

    //Get Product data
    public List<ProductDto> getAllProductDetails() {
        Cursor cursor = null;
        List<ProductDto> products = new ArrayList<>();
        try {
//            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where isDeleted = 0  order by  groupId";
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where isDeleted = 0  order by  sequenceNo";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                products.add(new ProductDto(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Opening stock", e.toString(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return products;
    }

    //Get missed Product data
    public List<ProductDto> getAllMissedProductDetails() {
        Cursor cursor = null;
        List<ProductDto> products = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM products where _id not in (select product_id from stock_history) and isDeleted = 0";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                products.add(new ProductDto(cursor));
                cursor.moveToNext();
            }
        } catch (Exception e) {
            Log.e("Opening stock", e.toString(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return products;
    }

    //Get missed Product count
    public int getAllMissedProductCount() {
        Cursor cursor = null;
        int value = 0;
        try {
            String selectQuery = "SELECT * FROM products where _id not in (select product_id from stock_history) and isDeleted = 0";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                value = 1;
            } else {
                value = 0;
            }
        } catch (Exception e) {
            Log.e("Opening stock", e.toString(), e);
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return value;
    }

    //Get Product stock data
    public FPSStockDto getAllProductsId(long productId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_STOCK + " where " + FPSDBConstants.KEY_STOCK_PRODUCT_ID + " = " + productId;
        FPSStockDto productStock;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            productStock = new FPSStockDto(cursor);
        else {
            productStock = null;
        }
        cursor.close();
        return productStock;
    }

    //Get Product stock data
    public FPSStockDto getAllProductStockDetails(long productId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_STOCK + " where " + FPSDBConstants.KEY_STOCK_PRODUCT_ID + " = " + productId;
        FPSStockDto productStock;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            productStock = new FPSStockDto(cursor);
        else {
            productStock = null;
        }
        cursor.close();
        return productStock;
    }

    //Get Product stock data
    public FPSStockHistoryDto getAllProductOpeningStockDetails(long productId) {
        String selectQuery = "SELECT * FROM stock_history where action = 'INITIAL STOCK' and product_id =" + productId;
        FPSStockHistoryDto productStock;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            productStock = new FPSStockHistoryDto(cursor);
        else {
            productStock = null;
        }
        cursor.close();
        return productStock;
    }

    //Get Product stock data
    public int getAllBillCount(boolean transmitted) {
        String selectQuery;
        if (transmitted)
            selectQuery = "SELECT count(*) as count FROM bill where date (date) =date('now','localtime')";
        else {
            selectQuery = "SELECT count(*) as count FROM bill where date (date) =date('now','localtime')   and bill_status = 'R'";
        }
        int count = 0;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
            count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    public List<StockRequestDto.ProductList> getChallans(String referenceNumber, long productId, long recordId) {
        return getProduct(referenceNumber, productId, recordId);
    }

    private List<StockRequestDto.ProductList> getProduct(String referenceNo, long productId, long recordId) {
        String selectQuery = "SELECT * FROM stock_inward where referenceNo ='" + referenceNo + "' and product_id = " + productId + " and fps_ack_status = 0" + " and _id = " + recordId;
        List<StockRequestDto.ProductList> productList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            StockRequestDto.ProductList manualProduct = new StockRequestDto.ProductList();
            manualProduct.setId(cursor.getLong(cursor.getColumnIndex("product_id")));
            manualProduct.setQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            manualProduct.setRecvQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            manualProduct.setServerId(cursor.getLong(cursor.getColumnIndex("_id")));
            try {
                if (cursor.getString(cursor.getColumnIndex("inwardType")).equalsIgnoreCase("R")) {
                    manualProduct.setType(StockTransactionType.INWARD);
                } else if (cursor.getString(cursor.getColumnIndex("inwardType")).equalsIgnoreCase("A")) {
                    manualProduct.setType(StockTransactionType.STOCK_ADVANCE);
                }
            } catch (Exception e) {
            }
            String value = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE));
            long fpsAckDate = 0l;
            try {
                if (value != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    Date date = sdf.parse(value);
                    fpsAckDate = date.getTime();
                }
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
            }
            manualProduct.setFpsAckDate(fpsAckDate);
            /*if (isCurrentMonth) {
                DateTime dateTime = new DateTime();
                manualProduct.setMonth(dateTime.getMonthOfYear());
                manualProduct.setYear(dateTime.getYear());
            } else {
                DateTime dateNextMonth = new DateTime().plusMonths(1);
                manualProduct.setMonth(dateNextMonth.getMonthOfYear());
                manualProduct.setYear(dateNextMonth.getYear());
            }*/
            manualProduct.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
            manualProduct.setYear(cursor.getInt(cursor.getColumnIndex("year")));
            productList.add(manualProduct);
            cursor.moveToNext();
        }
        cursor.close();
        return productList;
    }

    //Get Product stock data
    public List<StockCheckDto> getAllProductStockDetails() {
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "getAllProductStockDetails() called   ->");
//        String selectQuery = "SELECT a.product_id,SUM(Case when date(createdDate) = date('now','localtime')  then c.quantity else 0 end) as sold,a.quantity,name,unit,local_unit,local_name FROM stock a left join products b on a.product_id = b._id  left join bill_item c on  a.product_id = c.product_id group by a.product_id order by b.groupId,b._id";
        String selectQuery = "SELECT a.product_id,SUM(Case when date(createdDate) = date('now','localtime')  then c.quantity else 0 end) as sold,a.quantity,name,unit,local_unit,local_name FROM stock a left join products b on a.product_id = b._id  left join bill_item c on  a.product_id = c.product_id group by a.product_id order by b.sequenceNo,b._id";
        List<StockCheckDto> productStock = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            productStock.add(new StockCheckDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return productStock;
    }

    public List<StockCheckDto> getAllProductStockDetailsTwo(String from, String to) {
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getAllProductStockDetailsTwo() from->" + from);
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getAllProductStockDetailsTwo() to->" + to);
//        String selectQuery = "SELECT a.product_id,SUM(Case when date(createdDate) between '" + from + "' and '" + to + "' then c.quantity else 0 end) as sold,a.quantity,name,unit,local_unit,local_name FROM stock a left join products b on a.product_id = b._id  left join bill_item c on  a.product_id = c.product_id group by a.product_id order by b.groupId,b._id";
        String selectQuery = "SELECT a.product_id,SUM(Case when date(createdDate) between '" + from + "' and '" + to + "' then c.quantity else 0 end) as sold,a.quantity,name,unit,local_unit,local_name FROM stock a left join products b on a.product_id = b._id  left join bill_item c on  a.product_id = c.product_id group by a.product_id order by b.sequenceNo,b._id";
        List<StockCheckDto> productStock = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            productStock.add(new StockCheckDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return productStock;
    }

    //Get Product stock data
    public List<StockCheckDto> getSelectedDateProductStockDetails(String from, String to) {
        String selectQuery = "SELECT a.product_id,SUM(Case when date(createdDate) between '" + from + "' and '" + to + "' then c.quantity else 0 end) as sold,a.quantity,name,unit,local_unit,local_name FROM stock a left join products b on a.product_id = b._id  left join bill_item c on  a.product_id = c.product_id group by a.product_id order by b.groupId,b._id";
        List<StockCheckDto> productStock = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            productStock.add(new StockCheckDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return productStock;
    }

    //Get Product stock data
    public FPSStockHistoryDto getAllProductStockHistoryDetails(long productId) {
        String selectQuery = "SELECT * FROM stock_history where date(date_creation) <  date('now','localtime') and product_id = "
                + productId + " order by _id desc limit 1";
        Log.e("selectQuery", selectQuery);
        FPSStockHistoryDto productStock;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            productStock = new FPSStockHistoryDto();
            productStock.setCurrQuantity(cursor.getDouble(cursor.getColumnIndex("closing_balance")));
            productStock.setPrevQuantity(cursor.getDouble(cursor.getColumnIndex("closing_balance")));
            productStock.setAction(cursor.getString(cursor.getColumnIndex("action")));
            productStock.setProductId(cursor.getLong(cursor.getColumnIndex("product_id")));
        } else {
            cursor.close();
            selectQuery = "SELECT opening_balance,closing_balance,product_id,action FROM stock_history where date(date_creation)  = date('now','localtime') and product_id="
                    + productId + " order by _id limit 1";
            Log.e("selectQuery", selectQuery);
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                productStock = new FPSStockHistoryDto();
                productStock.setCurrQuantity(cursor.getDouble(cursor.getColumnIndex("opening_balance")));
                productStock.setPrevQuantity(cursor.getDouble(cursor.getColumnIndex("opening_balance")));
                productStock.setAction(cursor.getString(cursor.getColumnIndex("action")));
                productStock.setProductId(cursor.getLong(cursor.getColumnIndex("product_id")));
            } else {
                selectQuery = "SELECT * FROM stock where product_id =" + productId;
                Log.e("selectQuery", selectQuery);
                cursor = database.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    productStock = new FPSStockHistoryDto();
                    productStock.setCurrQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
                    productStock.setPrevQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
                    productStock.setAction("Stock");
                    productStock.setProductId(cursor.getLong(cursor.getColumnIndex("product_id")));
                } else {
                    cursor.close();
                    productStock = new FPSStockHistoryDto();
                    productStock.setCurrQuantity(0.0);
                    productStock.setPrevQuantity(0.0);
                    productStock.setAction("Empty");
                    productStock.setProductId(productId);
                }
            }
        }
        cursor.close();
        return productStock;
    }

    //Get opening balance
    public Double getSelectedDateOpeningBalance(long productId, String from) {
//        String selectQuery = "select opening_balance from stock_history where product_id = '"+productId+"' and date(date_creation) = '"+from+"' order by opening_balance desc limit 1";
        String selectQuery = "select opening_balance from stock_history where product_id = '" + productId + "' and date(date_creation) = '" + from + "' order by _id limit 1";
        Log.e("selectQuery", selectQuery);
        Double openingBal = 0.0;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            openingBal = cursor.getDouble(cursor.getColumnIndex("opening_balance"));
            cursor.close();
        } else {
            cursor.close();
            // Getting last closing balance from stock_history
            String selectQuery2 = "select closing_balance from stock_history where product_id = '" + productId + "' and date(date_creation) < '" + from + "' order by _id desc limit 1";
            Log.e("selectQuery2", selectQuery2);
            Cursor cursor2 = database.rawQuery(selectQuery2, null);
            cursor2.moveToFirst();
            if (cursor2.getCount() > 0) {
                openingBal = cursor2.getDouble(cursor2.getColumnIndex("closing_balance"));
            }
            cursor2.close();
        }
        return openingBal;
    }

    //Get closing balance
    public Double getSelectedDateClosingBalance(long productId, String to) {
        String selectQuery = "select closing_balance from stock_history where product_id = '" + productId + "' and date(date_creation) = '" + to + "' order by _id desc limit 1";
//        String selectQuery = "select a.quantity as close_bal from stock as a , stock_history as b where a.product_id = b.product_id and  b.product_id = '"+productId+"'  and date(b.date_creation) = '"+to+"' order by b.opening_balance limit 1";
        Log.e("selectQuery", selectQuery);
        Double closingBal = 0.0;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            closingBal = cursor.getDouble(cursor.getColumnIndex("closing_balance"));
            cursor.close();
        } else {
            cursor.close();
            // Getting last closing balance from stock_history
            String selectQuery2 = "select closing_balance from stock_history where product_id = '" + productId + "' and date(date_creation) < '" + to + "' order by _id desc limit 1";
            Log.e("selectQuery2", selectQuery2);
            Cursor cursor2 = database.rawQuery(selectQuery2, null);
            cursor2.moveToFirst();
            if (cursor2.getCount() > 0) {
                closingBal = cursor2.getDouble(cursor2.getColumnIndex("closing_balance"));
            }
            cursor2.close();
        }
        return closingBal;
    }

    // This function returns Card types
    public Map<Integer, String> getCardType() {
        HashMap<Integer, String> cardType = new HashMap<Integer, String>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_TYPE + " where (type <> 'X' and type <> 'Y') and (isDeleted = 0) and (isStatus = 1 or isStatus is null)" + " order by " + FPSDBConstants.KEY_CARD_DESCRIPTION;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int a = 9999;
        for (int i = 0; i < cursor.getCount(); i++) {
            if (!GlobalAppState.language.equalsIgnoreCase("ta")) {
                try {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION)));
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION)));
                    }
                } catch (Exception e) {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                    }
                }
            } else {
                try {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex("localDescription")));
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex("localDescription")));
                    }
                } catch (Exception e) {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                    }
                }
            }
            cursor.moveToNext();
        }
        Map<Integer, String> map = new TreeMap<Integer, String>(cardType);
        cursor.close();
        return map;
    }

    // This function returns Card types
    public Map<Integer, String> getOapAnpCardType() {
        HashMap<Integer, String> cardType = new HashMap<Integer, String>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_TYPE + " where (type = 'X' or type = 'Y') and (isDeleted = 0) and (isStatus = 1 or isStatus is null)";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int a = 9999;
        for (int i = 0; i < cursor.getCount(); i++) {
            if (!GlobalAppState.language.equalsIgnoreCase("ta")) {
                try {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION)));
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION)));
                    }
                } catch (Exception e) {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                    }
                }
            } else {
                try {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex("localDescription")));
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + cursor.getString(cursor.getColumnIndex("localDescription")));
                    }
                } catch (Exception e) {
                    if (cursor.getInt(cursor.getColumnIndex("display_sequence")) == 0) {
                        cardType.put(a, cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                        a++;
                    } else {
                        cardType.put(cursor.getInt(cursor.getColumnIndex("display_sequence")), cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)) + " : " + "");
                    }
                }
            }
            cursor.moveToNext();
        }
        Map<Integer, String> map = new TreeMap<Integer, String>(cardType);
        cursor.close();
        return map;
    }

    // This function returns Card types
    public List<String> getCardTypeValue() {
        List<String> cardType = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_TYPE + " order by " + FPSDBConstants.KEY_CARD_DESCRIPTION;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            cardType.add(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE)));
            cursor.moveToNext();
        }
        cursor.close();
        return cardType;
    }

    public List<RelationshipDto> getRelationship() {
        ArrayList<RelationshipDto> relationshipDtoList = new ArrayList<RelationshipDto>();
        String selectQuery = "select * from relationship";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            relationshipDtoList.add(new RelationshipDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return relationshipDtoList;
    }
    // This function returns Card types

    public String getCardType(String card) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_TYPE + " where " + FPSDBConstants.KEY_CARD_DESCRIPTION + " = '" + card + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String cardType = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_TYPE));
        cursor.close();
        return cardType;
    }

    // This function returns Card types
    public String getCardTypeFromId(String card) {
        String cardType = "";
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_TYPE + " where _id = " + Long.parseLong(card);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                try {
                    cardType = cursor.getString(cursor.getColumnIndex("localDescription"));
                } catch (Exception e) {
                    cardType = "";
                }
            } else {
                try {
                    cardType = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_CARD_DESCRIPTION));
                } catch (Exception e) {
                    cardType = "";
                }
            }
            cursor.close();
        }
        catch(Exception e) {}
        return cardType;
    }

    //This function loads data to language table
    public boolean getFirstSync() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_LANGUAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean value = cursor.getCount() == 0;
        cursor.close();
        return value;
    }

    //This function retrieve error description from language table
    public MessageDto retrieveLanguageTable(int errorCode) {
        MessageDto messageDto = null;
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_LANGUAGE + " where  " + FPSDBConstants.KEY_LANGUAGE_CODE + " = " + errorCode;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            messageDto = new MessageDto(cursor);
            cursor.close();
        }
        catch(Exception e) {}
        return messageDto;
    }

    public List<GodownStockOutwardDto> showFpsStockInvard(boolean ackStatus, int position) {
        String selectQuery;
        long offSet = position * 50;
        if (!ackStatus) {
            selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where " + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS
                    + " = 0 group by referenceNo order by outward_date desc limit 50  OFFSET " + offSet;
        } else {
            selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where " + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS
                    + "  = 1 group by referenceNo order by outward_date desc limit 50  OFFSET " + offSet;
        }
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("Query value", "Cursor:" + cursor.getCount());
        List<GodownStockOutwardDto> fpsInwardList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsInwardList.add(new GodownStockOutwardDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return fpsInwardList;
    }

    public List<ReconciliationRequestDto> getReconciliationData() {
        String selectQuery;
        selectQuery = "SELECT * FROM reconciliation order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<ReconciliationRequestDto> reconciliationRequestDtoList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            reconciliationRequestDtoList.add(new ReconciliationRequestDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return reconciliationRequestDtoList;
    }

    public List<BackgroundServiceDto> getHeartBeatServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'HeartBeatService' or serviceType = 'Session_request_HeartBeatService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getStatisticsServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'StatisticsService' or serviceType = 'Session_request_StatisticsService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getAllocationServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'AllocationService' or serviceType = 'Session_request_AllocationService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getRegularSyncServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'RegularSyncService_FirstSyncSuccess' or serviceType = 'RegularSyncService_GetTableNames' or serviceType = 'RegularSyncService_GetTableDatas' or serviceType = 'Session_request_RegularSyncService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getInwardServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'InwardService' or serviceType = 'Session_request_InwardService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getAdjustmentServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'AdjustmentService' or serviceType = 'Session_request_AdjustmentService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getAdvanceStockServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'AdvanceStockService' or serviceType = 'Session_request_AdvanceStockService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getBillServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'BillService' or serviceType = 'Session_request_BillService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getCloseSaleServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'CloseSaleService' or serviceType = 'Session_request_CloseSaleService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getLoginServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'LoginService' or serviceType = 'Session_request_LoginService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getRemoteLogServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'RemoteLogService' or serviceType = 'Session_request_RemoteLogService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getSyncExceptionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'SyncExceptionService' or serviceType = 'Session_request_SyncExceptionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getBifurcationServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'BifurcationService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getMigrationOutServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'MigrationOutService' or serviceType = 'Session_request_MigrationService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getMigrationInServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'MigrationInService' or serviceType = 'Session_request_MigrationService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getBiometricServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'BiometricService' or serviceType = 'Session_request_BiometricService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getInspectionReportServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'InspectionReportService' or serviceType = 'Session_request_InspectionReportService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getInspectionReportAckServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'InspectionReportAckService' or serviceType = 'Session_request_InspectionReportAckService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getCardInspectionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'CardInspectionService' or serviceType = 'Session_request_CardInspectionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getStockInspectionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'StockInspectionService' or serviceType = 'Session_request_StockInspectionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getWeighmentInspectionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'WeighmentInspectionService' or serviceType = 'Session_request_WeighmentInspectionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getShopInspectionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'ShopInspectionService' or serviceType = 'Session_request_ShopInspectionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<BackgroundServiceDto> getOtherInspectionServiceHistoryList() {
        String selectQuery;
        selectQuery = "SELECT * FROM backgroundProcessHistory where serviceType = 'OtherInspectionService' or serviceType = 'Session_request_OtherInspectionService' order by _id desc limit 300";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BackgroundServiceDto> serviceHistoryList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            serviceHistoryList.add(new BackgroundServiceDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return serviceHistoryList;
    }

    public List<POSStockAdjustmentDto> showFpsStockAdjustment(boolean ackStatus, int position) {
        String selectQuery;
        long offSet = position * 50;
//        selectQuery = "SELECT  * FROM fps_stock_adjustment where isAdjusted = 0 group by _id order by createdDate desc limit 50  OFFSET " + offSet;
        selectQuery = "SELECT  * FROM fps_stock_adjustment where isAdjusted = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("Query value", "Cursor:" + cursor.getCount());
        List<POSStockAdjustmentDto> fpsAdjustmentList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsAdjustmentList.add(new POSStockAdjustmentDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return fpsAdjustmentList;
    }

    public List<FpsAdvanceStockDto> showFpsStockAdvance() {
        String selectQuery;
        selectQuery = "select * from advance_stock_inward";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("Query value", "advance stock inward list.." + cursor.getCount());
        List<FpsAdvanceStockDto> stockAdvanceList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            stockAdvanceList.add(new FpsAdvanceStockDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return stockAdvanceList;
    }

    public List<POSStockAdjustmentDto> allStockAdjustmentData() {
//        long offSet = position * 50;
        String selectQuery = "SELECT * FROM fps_stock_adjustment order by dateOfAck desc";
        List<POSStockAdjustmentDto> fpsInwardList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                fpsInwardList.add(new POSStockAdjustmentDto(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return fpsInwardList;
    }

    public List<GodownStockOutwardDto> showFpsStockInvardDetail(String chellanId, boolean ackStatus) {
        List<GodownStockOutwardDto> fpsInwardList = new ArrayList<>();
        int ack = 0;
        if (ackStatus)
            ack = 1;
        try {
            /*String selectQuery = "SELECT _id,challanId,rowid,vehicleN0,driverName,quantity,product_id,month,year,godown_id,fps_id,outward_date,unit,godown_name,godown_code," +
                    " batch_no,transportName,driverMobileNumber,referenceNo,fps_ack_status,fps_ack_date_new,fps_receive_quantity, created_by," +
                    "delivery_challan_id FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where referenceNo='" + chellanId + "' AND fps_ack_status=" + ack + " group by product_id, inwardType, referenceNo";*/
            String selectQuery = "SELECT _id,challanId,rowid,vehicleN0,driverName,quantity,product_id,month,year,godown_id,fps_id,outward_date,unit,godown_name,godown_code," +
                    " batch_no,transportName,driverMobileNumber,referenceNo,fps_ack_status,fps_ack_date_new,fps_receive_quantity, created_by," +
                    "delivery_challan_id FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where referenceNo='" + chellanId + "' AND fps_ack_status=" + ack;
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                fpsInwardList.add(new GodownStockOutwardDto(cursor));
                cursor.moveToNext();
            }
            cursor.close();
            Log.e("stock inward ", fpsInwardList.toString());
        } catch (Exception e) {
            Log.e("stock inward excep", e.toString(), e);
        }
        return fpsInwardList;
    }

    public boolean getStockExists(GodownStockOutwardDto godownStockOutwardDto) {
        String selectQuery = "SELECT * from stock where product_id =" + godownStockOutwardDto.getProductId();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Log.i("Stock", "Empty");
        } else {
            String stock_validation = "" + FPSDBHelper.getInstance(contextValue).getMasterData("stock_validation");
            if (stock_validation != null && StringUtils.isNotEmpty(stock_validation) && (!stock_validation.equalsIgnoreCase("null"))) {
                if (stock_validation.equalsIgnoreCase("1")) {
                    insertFpsStockInward(godownStockOutwardDto);
                }
            }

        }
        cursor.close();
        return true;
    }

    public List<ReconciliationStockDto> getStockForReconciliation() {
        String selectQuery = "SELECT a.product_id, a.quantity, b.name, b.local_name, b.unit, b.local_unit FROM stock a, products b where a.product_id = b._id and b.isDeleted = 0 order by b.sequenceNo";
        List<ReconciliationStockDto> reconciliationStockDtoList = new ArrayList<ReconciliationStockDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("cursorSize", "" + cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            reconciliationStockDtoList.add(new ReconciliationStockDto(cursor));
            cursor.moveToNext();
        }
        return reconciliationStockDtoList;
    }

    public double getStockOfSpecificProduct(String id) {
        double quantity = 0.0;
        String selectQuery = "SELECT quantity from stock where product_id = " + id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            quantity = cursor.getDouble(cursor.getColumnIndex("quantity"));
        }
        cursor.close();
        return quantity;
    }

    // This function inserts details to TABLE_STOCK
    private void insertFpsStockInward(GodownStockOutwardDto godownStockOutwardDto) {
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_STOCK_FPS_ID, godownStockOutwardDto.getFpsId());
        values.put(FPSDBConstants.KEY_STOCK_PRODUCT_ID, godownStockOutwardDto.getProductId());
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        Double qty3 = Double.parseDouble(Util.quantityRoundOffFormat(0.0));
        values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty3);
        values.put(FPSDBConstants.KEY_STOCK_EMAIL_ACTION, 0);
        values.put(FPSDBConstants.KEY_STOCK_SMS_ACTION, 0);
        database.insert(FPSDBTables.TABLE_STOCK, null, values);
    }

    public void updateReceivedQuantity(StockRequestDto godownStockOutwardDto, boolean isOffline) {
        Log.e("fpsdb helper", "updateReceivedQuantity...." + godownStockOutwardDto.toString());
        for (StockRequestDto.ProductList productList : godownStockOutwardDto.getProductLists()) {
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(godownStockOutwardDto.getDate());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
            if (isOffline) {
                values.put("is_server_add", 1);
            } else {
                values.put("is_server_add", 2);
            }
            values.put("month", productList.getMonth());
            values.put("year", productList.getYear());
            database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, "_id = " + productList.getServerId(), null);
            FPSStockDto stockList = getAllProductStockDetails(productList.getId());
            if (stockList == null) {
                stockList = new FPSStockDto();
                stockList.setQuantity(0.0);
                stockList.setProductId(productList.getId());
            }
            double openingQuantity = stockList.getQuantity();
            double closing = stockList.getQuantity() + productList.getQuantity();
            stockList.setQuantity(closing);

            if ((productList.getType().equals(StockTransactionType.STOCK_ADVANCE))) {
                insertFpsStockInwardDetails(godownStockOutwardDto, productList);
            } else {
                stockUpdate(stockList);
                insertStockHistory(openingQuantity, closing, "INWARD", productList.getQuantity(), productList.getId());
            }
        }
    }

    public void updateReceivedQuantityTwo(StockRequestDto godownStockOutwardDto, boolean isOffline) {
        Log.e("fpsdb helper", "updateReceivedQuantity...." + godownStockOutwardDto.toString());
        for (StockRequestDto.ProductList productList : godownStockOutwardDto.getProductLists()) {
            ContentValues values = new ContentValues();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date(godownStockOutwardDto.getDate());
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 1);
            if (isOffline) {
                values.put("is_server_add", 1);
            } else {
                values.put("is_server_add", 2);
            }
            values.put("month", productList.getMonth());
            values.put("year", productList.getYear());
            database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, "_id = " + productList.getServerId(), null);
            /*FPSStockDto stockList = getAllProductStockDetails(productList.getId());
            if (stockList == null) {
                stockList = new FPSStockDto();
                stockList.setQuantity(0.0);
                stockList.setProductId(productList.getId());
            }
            double openingQuantity = stockList.getQuantity();
            double closing = stockList.getQuantity() + productList.getQuantity();
            stockList.setQuantity(closing);*/

            if ((productList.getType().equals(StockTransactionType.STOCK_ADVANCE))) {
                insertFpsStockInwardDetails(godownStockOutwardDto, productList);
            } /*else {
                stockUpdate(stockList);
                insertStockHistory(openingQuantity, closing, "INWARD", productList.getQuantity(), productList.getId());
            }*/
        }
    }

    // This function inserts details to FPSDBTables.TABLE_FPS_STOCK_INWARD
    public void insertFpsStockInwardDetails(StockRequestDto fpsStockInward, StockRequestDto.ProductList productList) {
        ContentValues values = new ContentValues();
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, fpsStockInward.getGodownId());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, fpsStockInward.getFpsId());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, productList.getId());
        Double qty6 = Double.parseDouble(Util.quantityRoundOffFormat(productList.getQuantity()));
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, qty6);
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT, fpsStockInward.getUnit());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO, fpsStockInward.getBatchNo());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, fpsStockInward.getDeliveryChallanId());
        values.put("referenceNo", fpsStockInward.getReferenceNo());
        values.put("month", productList.getMonth());
        values.put("year", productList.getYear());
        values.put("_id", productList.getServerId());
        values.put("isAdded", 1);
        values.put("outward_date", new Date().getTime());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, 0);
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        Date date = new Date(fpsStockInward.getDate());
//        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, fpsStockInward.getDate());
        if (productList.getQuantity() != null) {
            Double qty8 = Double.parseDouble(Util.quantityRoundOffFormat(productList.getQuantity()));
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, qty8);
        } else {
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, 0);
        }
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, fpsStockInward.getDeliveryChallanId());
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE, fpsStockInward.getDate());
        values.put("godown_name", fpsStockInward.getGodownName());
        values.put("godown_code", fpsStockInward.getGodownCode());
        values.put("created_by", fpsStockInward.getCreatedBy());
        values.put("syncStatus", 0);
        database.insert(FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD, null, values);
    }

    //Update the stock
    public void stockUpdate(FPSStockDto stock) {
        ContentValues values = new ContentValues();
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        Double qty1 = Double.parseDouble(Util.quantityRoundOffFormat(stock.getQuantity()));
        values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty1);
//        Log.e("Stock stock update", stock.toString());
        database.update(FPSDBTables.TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + stock.getProductId(), null);
    }

    //Update the stock
    public void beneficiaryUpdate(BeneficiaryUpdateDto beneUpdate) {
        ContentValues values = new ContentValues();
        if (beneUpdate.getMobileNumber() != null) {
            values.put("mobile", beneUpdate.getMobileNumber());
        }
        if (beneUpdate.getAadhaarSeedingDto().getUid() != null) {
            values.put("aadharNumber", beneUpdate.getAadhaarSeedingDto().getUid());
        }
        if (beneUpdate.getAregisterNumber() != null) {
            values.put("aRegister", beneUpdate.getAregisterNumber());
        }
        database.update(FPSDBTables.TABLE_BENEFICIARY, values, "_id=" + beneUpdate.getBeneficiaryId(), null);
    }

    //Update the beneficiary_member table
    public void beneficiaryMemberAadhar(AadharSeedingDto beneMemberUpdate) {
        Log.e("^^^^FPSDBHelper^^^^", "beneficiaryMemberAadhar Details = " + beneMemberUpdate);
        ContentValues values = new ContentValues();
        if (beneMemberUpdate.getAadhaarNum() != 0l && beneMemberUpdate.getBeneficiaryID() != 0l) {
            values.put("beneficiary_id", beneMemberUpdate.getBeneficiaryID());
            values.put("aadhar_number", beneMemberUpdate.getAadhaarNum());
        }
//        database.insert(FPSDBTables.TABLE_MEMBERS_AADHAR, null, values);
        try {
            if (checkAadharNumber(beneMemberUpdate.getAadhaarNum()) == 0) {
                database.insertWithOnConflict(FPSDBTables.TABLE_MEMBERS_AADHAR, "aadhar_number", values, SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception e) {
            database.insertWithOnConflict(FPSDBTables.TABLE_MEMBERS_AADHAR, "aadhar_number", values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Update the beneficiary_member table in sync
    public boolean beneficiaryMemberAadharSync(Set<AadharSeedingDto> aadharDtoSet, String type, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        AadharSeedingDto aadharDtoSet1 = null;
        if (!aadharDtoSet.isEmpty()) {
            database.beginTransaction();
            try {
                for (AadharSeedingDto aadharDto : aadharDtoSet) {
                    recordId = aadharDto.getId();
                    aadharDtoSet1 = aadharDto;
                    if (type.equalsIgnoreCase("Add")) {
                        ContentValues values = new ContentValues();
                        if (aadharDto.getAadhaarNum() != 0l && aadharDto.getBeneficiaryID() != 0l) {
                            values.put("beneficiary_id", aadharDto.getBeneficiaryID());
                            values.put("aadhar_number", aadharDto.getAadhaarNum());
                            values.put("beneficiary_member_id", aadharDto.getBeneficiaryMemberID());
                        }
                        try {
                            if (checkAadharNumber(aadharDto.getAadhaarNum()) == 0) {
                                database.insertWithOnConflict(FPSDBTables.TABLE_MEMBERS_AADHAR, "aadhar_number", values, SQLiteDatabase.CONFLICT_REPLACE);
                            }
                        } catch (Exception e) {
                            database.insertWithOnConflict(FPSDBTables.TABLE_MEMBERS_AADHAR, "aadhar_number", values, SQLiteDatabase.CONFLICT_REPLACE);
                        }
                    } else if (type.equalsIgnoreCase("Delete")) {
                        database.delete(FPSDBTables.TABLE_MEMBERS_AADHAR, "beneficiary_id = " + aadharDto.getBeneficiaryID() + " and aadhar_number = " + aadharDto.getAadhaarNum(), null);
                    }
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_MEMBERS_AADHAR Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(aadharDtoSet1);
                    insertSyncException("TABLE_MEMBERS_AADHAR", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    // Update the bifurcation table in sync
    public boolean bifurcationSync(Set<BifurcationHistoryDto> bifurcationHistoryDtosSet, String syncType) {
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        BifurcationHistoryDto bifurcationHistoryDtosSet1 = null;
        if (!bifurcationHistoryDtosSet.isEmpty()) {
            database.beginTransaction();
            try {
                Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "bifurcationSync() called ");
                for (BifurcationHistoryDto bifurcationHistoryDtos : bifurcationHistoryDtosSet) {
                    recordId = bifurcationHistoryDtos.getId();
                    bifurcationHistoryDtosSet1 = bifurcationHistoryDtos;
                    Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "bifurcationSync() BifurcationHistoryDto =  " + bifurcationHistoryDtos);
                    ContentValues values = new ContentValues();
                    values.put("id", bifurcationHistoryDtos.getId());
                    values.put("oldFpsId", bifurcationHistoryDtos.getOldFpsStore().getId());
                    values.put("newFpsId", bifurcationHistoryDtos.getNewFpsStore().getId());
                    values.put("benefId", bifurcationHistoryDtos.getBeneficiary().getId());
                    values.put("createdById", bifurcationHistoryDtos.getCreatedById());
                    values.put("createdDate", bifurcationHistoryDtos.getCreatedDate());
                    values.put("status", 0);
                    database.insert("Bifurcation", null, values);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "Bifurcation Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(bifurcationHistoryDtosSet1);
                    insertSyncException("Bifurcation", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    //Update the stock
    public void stockUpdateAdvance(FPSStockDto stock, double openingQuantity, double change) {
        ContentValues values = new ContentValues();
        /*NumberFormat formatter = new DecimalFormat("#0.000");
        formatter.setRoundingMode(RoundingMode.CEILING);*/
        Double qty2 = Double.parseDouble(Util.quantityRoundOffFormat(stock.getQuantity()));
        values.put(FPSDBConstants.KEY_STOCK_QUANTITY, qty2);
        Log.e("Stock stock update", stock.toString());
        database.update(FPSDBTables.TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + stock.getProductId(), null);
        insertStockHistory(openingQuantity, stock.getQuantity(), "INWARD", change, stock.getProductId());
    }

    //Update the advance_stock_inward table
    public void stockUpdateAdvance(long productId) {
        ContentValues values = new ContentValues();
        values.put("isAdded", 0);
       /* SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateStr);*/
        Date date = new Date();
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, date.getTime());


        /*try {
            GregorianCalendar gc = new GregorianCalendar();
            String dateString = sdf.format(gc.getTime());
            Date date = sdf.parse(dateString);
            values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, date.getTime());
        } catch (Exception e) {
        }*/
        database.update(FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + productId + " and month <= " + new DateTime().getMonthOfYear() + " and year <= " + new DateTime().getYear(), null);
        /*String selectQuery = "update stock_inward set fps_ack_date_new  = " + date + " where _id in(Select _id from advance_stock_inward where product_id = "
                + productId + " and isAdded = 0 and month = " + new DateTime().getMonthOfYear() + ")";
        database.execSQL(selectQuery);*/
    }

    //Update the stock_inward table
    public void stockUpdateInward(long productId) {
        ContentValues values = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
        database.update(FPSDBTables.TABLE_FPS_STOCK_INWARD, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + productId, null);
    }

    //Get Product Name by Product Id
    public String getProductName(long _id) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String value = null;
        if (cursor.getCount() > 0)
            if (GlobalAppState.language.equalsIgnoreCase("ta")) {
                value = new ProductDto(cursor).getLocalProductName();
            } else {
                value = new ProductDto(cursor).getName();
            }
        cursor.close();
        return value;
    }

    // database connection  close
    public synchronized void closeConnection() {
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "closeConnection() called   ->");
        if (dbHelper != null) {
            dbHelper.close();
            database.close();
            dbHelper = null;
            database = null;
        }
    }

    public void deleteAllRecordsInAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from Bifurcation");
        db.execSQL("delete from advance_stock_inward");
        db.execSQL("delete from beneficiary");
        db.execSQL("delete from beneficiary_in");
        db.execSQL("delete from beneficiary_member");
        db.execSQL("delete from beneficiary_member_in");
        db.execSQL("delete from beneficiary_registration");
        db.execSQL("delete from bill");
        db.execSQL("delete from bill_item");
        db.execSQL("delete from biometric_authentication");
        db.execSQL("delete from card_type");
        db.execSQL("delete from close_sale");
        db.execSQL("delete from close_sale_product");
        // db.execSQL("delete from configuration");
        db.execSQL("delete from entitlement_rules");
        db.execSQL("delete from error_messages");
        db.execSQL("delete from fps_stock_adjustment");
        db.execSQL("delete from login_history");
        db.execSQL("delete from master_first_sync");
        db.execSQL("delete from members_aadhar");
        db.execSQL("delete from migration_in");
        db.execSQL("delete from migration_out");
        db.execSQL("delete from offline_activation");
        db.execSQL("delete from person_rules");
        db.execSQL("delete from pos_operating_hours");
        db.execSQL("delete from product_group");
        db.execSQL("delete from product_price_override");
        db.execSQL("delete from products");
        db.execSQL("delete from region_rules");
        db.execSQL("delete from registration");
        db.execSQL("delete from role_feature");
        db.execSQL("delete from sms_provider");
        db.execSQL("delete from special_rules");
        //   db.execSQL("delete from sqlite_sequence");
        db.execSQL("delete from stock");
        db.execSQL("delete from stock_history");
        db.execSQL("delete from stock_inward");
        db.execSQL("delete from syncException");
        db.execSQL("delete from table_upgrade");
        db.execSQL("delete from users");
    }

    // Used to retrieve beneficiary details
    public String retrieveDataFromBeneficiary(long userName) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + KEY_ID + "=" + userName;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        BeneficiaryDto beneficiaryDto;
        String data = "";
        if (cursor.moveToFirst()) {
            beneficiaryDto = new BeneficiaryDto(cursor);
            data = beneficiaryDto.getEncryptedUfc();
        }
        cursor.close();
        return data;
    }

    // Used to retrieve beneficiary details
    public boolean retrieveARegNoFromBeneficiary(String aRegNo) {
        String selectQuery = "SELECT distinct(aRegister) FROM " + FPSDBTables.TABLE_BENEFICIARY + " where aRegister=" + Integer.parseInt(aRegNo);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean isExists = false;
        if (cursor.getCount() > 0) {
            isExists = true;
        }
        cursor.close();
        return isExists;
    }

    // Used to retrieve beneficiary details
    public boolean retrieveCardNoBeneficiary(String rationCardNo) {
        String selectQuery = "SELECT distinct(old_ration_card_num) FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + "='" + rationCardNo + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean isExists = false;
        if (cursor.getCount() > 0) {
            isExists = true;
        }
        cursor.close();
        return isExists;
    }

    public boolean checkMobNoExistence(String mobNo) {
        String selectQuery = "SELECT * FROM beneficiary where mobile= '" + mobNo + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean isExists = false;
        if (cursor.getCount() > 0) {
            isExists = true;
        }
        cursor.close();
        return isExists;
    }

    // Used to retrieve beneficiary details
    public BeneficiaryDto retrieveIdFromBeneficiary(String mobile) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_MOBILE + "='" + mobile + "'";
        Log.i("query:  ", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(beneficiary.getUfc()));
            cursor.close();
            return beneficiary;
        }
    }

    // Used to retrieve beneficiary details
    public BeneficiaryDto retrieveIdFromBeneficiaryReg(int aReg) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where aRegister=" + aReg;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
            beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(beneficiary.getUfc()));
            cursor.close();
            return beneficiary;
        }
    }

    public BenefActivNewDto getCardDetails(String cardNo) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_REGISTRATION + " where " + FPSDBConstants.KEY_REGISTRATION_CARD_NO + " = '" + cardNo + "' AND "
                + FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED + " = 0";
        BenefActivNewDto benficiary = new BenefActivNewDto();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Log.i("Cursor", "cursor count" + cursor.getCount());
        if (cursor.getCount() > 0) {
            benficiary = new BenefActivNewDto(cursor);
        }
        cursor.close();
        return benficiary;
    }

    //Get Product data
    public PersonBasedRule getAllPersonBasedRule(long productId) {
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON_RULES + " where groupId = " + productId + " AND isDeleted = 0";
        PersonBasedRule products = new PersonBasedRule();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            products = new PersonBasedRule(cursor);
        }
        cursor.close();
        return products;
    }

    public POSOperatingHoursDto getPOSOperatingHoursForToday(String day) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getPOSOperatingHoursForToday() day->" + day);
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_POSOPERATINGHOURS + " where day = '" + day + "'";
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getPOSOperatingHoursForToday() selectQuery->" + selectQuery);
            POSOperatingHoursDto posOperatingHoursDto = new POSOperatingHoursDto();
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                posOperatingHoursDto.setFirstSessionOpeningTime(cursor.getString(cursor.getColumnIndex("firstSessionOpeningTime")));
                posOperatingHoursDto.setFirstSessionClosingTime(cursor.getString(cursor.getColumnIndex("firstSessionClosingTime")));
                posOperatingHoursDto.setSecondSessionOpeningTime(cursor.getString(cursor.getColumnIndex("secondSessionOpeningTime")));
                posOperatingHoursDto.setSecondSessionClosingTime(cursor.getString(cursor.getColumnIndex("secondSessionClosingTime")));
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getPOSOperatingHoursForToday() posOperatingHoursDto->" + posOperatingHoursDto);
                return posOperatingHoursDto;
            }
            cursor.close();
        } catch (Exception e) {
        }
        return null;
    }

    public PersonBasedRule findByGroupAndCardType(long groupId, String cardType) {
        // Util.LoggingQueue(contextValue, "FPSDBHelper ", "findByGroupAndCardType() called groupId -> " + groupId + " cardType ->" + cardType);
        //SELECT  * FROM  person_rules where groupId = 15 AND card_type_id  = 14 AND isDeleted = 0
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON_RULES + " where groupId = " + groupId + " AND isDeleted = 0 AND card_type_id = " + cardType;
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "PERSON  With CardType Query -> " + selectQuery);
        //SELECT b FROM PersonBasedRule b where b.group=?1 and b.cardType=?2 and b.isDeleted= false
        PersonBasedRule products = new PersonBasedRule();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            products = new PersonBasedRule(cursor);
        }
        cursor.close();
        if (cursor.getCount() > 0) {
            return products;
        } else {
            return null;
        }
    }

    public PersonBasedRule findByGroupWithoutCardType(long groupId) {
        // Util.LoggingQueue(contextValue, "FPSDBHelper ", "findByGroupWithoutCardType() called groupId -> " + groupId );
        //SELECT  * FROM  person_rules where groupId = 1 AND card_type_id IS NULL AND isDeleted = 0
        String selectQuery = "SELECT  * FROM " + TABLE_PERSON_RULES + " where groupId = " + groupId + " AND card_type_id IS NULL AND isDeleted = 0 ";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "PERSON With Out Card Type Query -> " + selectQuery);
        //SELECT b FROM PersonBasedRule b where b.group=?1 and b.cardType=?2 and b.isDeleted= false
        PersonBasedRule products = new PersonBasedRule();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            products = new PersonBasedRule(cursor);
        }
        cursor.close();
        return products;
    }

    //Get Product data
    public List<RegionBasedRule> getAllRegionBasedRule(long productId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_REGION_RULES + " where groupId = " + productId + " AND isDeleted = 0";
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "REGION QUERY ->" + selectQuery);
        List<RegionBasedRule> region = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            region.add(new RegionBasedRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        Util.LoggingQueue(context, "REGION QUERY ->2", region.toString());
        return region;
    }

    public void updateMaserData(String name, String value) {
        // Log.e("^^^^FPSDBHelper^^^^", "updateMaserData");
        // Log.e("^^^^FPSDBHelper^^^^", "name = " + name + " value = " + value);
       // Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "updateMaserData() called  name ->" + name + " value ->" + value);
          Log.e("DBHELPER","<==== Language ====>"+value);
//        Log.e("name"+ name + " value ->" + value)
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("value", value);
        database.update(FPSDBTables.TABLE_CONFIG_TABLE, values, "name='" + name + "'", null);
    }

    public void updateUserActiveStatusDetails(String user_name) {
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "updateUserActiveStatusDetails() called  user_name ->" + user_name);
            ContentValues values = new ContentValues();
            values.put("is_user_active", "0");
            database.update("users", values, "user_name = '" + user_name + "'", null);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "updateUserActiveStatusDetails Exception...", e.toString());
        }
    }

    public void updateShopActiveStatusDetails(String user_name) {
        try {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "updateShopActiveStatusDetails() called  user_name ->" + user_name);
            ContentValues values = new ContentValues();
            values.put("is_active", "0");
            database.update("users", values, "user_name = '" + user_name + "'", null);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "updateShopActiveStatusDetails Exception...", e.toString());
        }
    }

    public String getMasterData(String key) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CONFIG_TABLE + " where name = '" + key + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String value = null;
        try {
            if (cursor.moveToFirst()) {
                value = cursor.getString(cursor
                        .getColumnIndex("value"));
//                Util.LoggingQueue(contextValue, "FPSDBHelper ", "getMasterData() for" + " key -> " + key +
//                        " ,  value returned -> " + value);
            }
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
        cursor.close();
        return value;
    }

    public String getLastLoginTime(long userId) {
        String selectQuery = "SELECT  last_login_time as login_time FROM users where _id = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        String loginTime = null;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            loginTime = cursor.getString(cursor.getColumnIndex("login_time"));
        }
        cursor.close();
        return loginTime;
    }

    public String getOpeningTime(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getOpeningTime() userId->" + userId);
        String selectQuery = "SELECT  operation_opening_time as open_time FROM users where _id = " + userId;
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getOpeningTime() selectQuery->" + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        String loginTime = "9.00";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            loginTime = cursor.getString(cursor.getColumnIndex("open_time"));
        }
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getOpeningTime() loginTime->" + loginTime);
        cursor.close();
        return loginTime;
    }

    public UpgradeDetailsDto getUpgradeData() {
        UpgradeDetailsDto upgradeDto = new UpgradeDetailsDto();
        String unSinkQuery = "SELECT count(*) as bill_count FROM bill where bill_status <> 'T'";
        String billQuery = "SELECT count(*) as bill_count FROM bill";
        String productQuery = "SELECT count(*) as product_count FROM products";
        String beneficiaryQuery = "SELECT count(*) as bene_count FROM beneficiary";
        String beneficiaryOfflineQuery = "SELECT count(*) as bene_count FROM offline_activation where active=0";
        String cardQuery = "SELECT count(*) as card_count FROM card_type";
        String stockQuery = "SELECT count(*) as stock_count FROM Stock";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorBill = db.rawQuery(unSinkQuery, null);
        cursorBill.moveToFirst();
        upgradeDto.setBillUnsyncCount(cursorBill.getInt(cursorBill.getColumnIndex("bill_count")));
        cursorBill.close();
        Cursor cursorProduct = db.rawQuery(productQuery, null);
        cursorProduct.moveToFirst();
        upgradeDto.setProductCount(cursorProduct.getInt(cursorProduct.getColumnIndex("product_count")));
        cursorProduct.close();
        Cursor cursorCard = db.rawQuery(cardQuery, null);
        cursorCard.moveToFirst();
        upgradeDto.setCardTypeCount(cursorCard.getInt(cursorCard.getColumnIndex("card_count")));
        cursorCard.close();
        Cursor cursorStock = db.rawQuery(stockQuery, null);
        cursorStock.moveToFirst();
        upgradeDto.setFpsStockCount(cursorStock.getInt(cursorStock.getColumnIndex("stock_count")));
        cursorStock.close();
        Cursor cursorBillQuery = db.rawQuery(billQuery, null);
        cursorBillQuery.moveToFirst();
        upgradeDto.setBillCount(cursorBillQuery.getInt(cursorBillQuery.getColumnIndex("bill_count")));
        cursorBillQuery.close();
        Cursor cursorBq = db.rawQuery(beneficiaryQuery, null);
        cursorBq.moveToFirst();
        upgradeDto.setBeneficiaryCount(cursorBq.getInt(cursorBq.getColumnIndex("bene_count")));
        cursorBq.close();
        Cursor cursorBene = db.rawQuery(beneficiaryOfflineQuery, null);
        cursorBene.moveToFirst();
        upgradeDto.setBeneficiaryUnsyncCount(cursorBene.getInt(cursorBene.getColumnIndex("bene_count")));
        cursorBene.close();
        Log.i("upgradeDto", upgradeDto.toString());
        return upgradeDto;
    }

    public List<BenefActivNewDto> allBeneficiaryDetailsPending(String dateRegistered, String oldRation, String mobileNum) {
        String selectQuery = "SELECT * FROM  beneficiary_registration where old_ration_card_num NOT IN (Select old_ration_card_num from beneficiary ) and requested_time like '%" + dateRegistered + "%'   and old_ration_card_num like '%"
                + oldRation + "%'  and mob_num like '%" + mobileNum + "%'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        List<BenefActivNewDto> beneficiary = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BenefActivNewDto beneficiaryDto = new BenefActivNewDto();
            beneficiaryDto.setFpsId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_REGISTRATION_FPS_ID)) + "");
            String aRegistrationNumStr = Integer.parseInt(cursor.getString(cursor.getColumnIndex("aRegister"))) + "";
            beneficiaryDto.setAregisterNum(aRegistrationNumStr);
            // beneficiaryDto.setAregisterNum(cursor.getString(cursor.getColumnIndex("aRegister")));
            beneficiaryDto.setRationCardNumber(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_OLD_RATION)));
            beneficiaryDto.setNumOfCylinder(cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CYLINDER_NO)));
            beneficiaryDto.setNumOfAdults(cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_ADULT_NO)));
            beneficiaryDto.setReqDate(cursor.getLong(cursor.getColumnIndex("reqTime")));
            beneficiaryDto.setNumOfChild(cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_BENEFICIARY_CHILD_NO)));
            beneficiaryDto.setMobileNum(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_REGISTRATION_MOB)));
            beneficiaryDto.setChannel(cursor.getString(cursor.getColumnIndex("channel")));
            beneficiaryDto.setRequestedTime(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_REGISTRATION_RTIME)));
            beneficiaryDto.setCardType(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_ALLOTMENT_CARD_TYPE)).charAt(0));
            beneficiaryDto.setActivationType("HELP");
            beneficiary.add(beneficiaryDto);
            cursor.moveToNext();
        }
        cursor.close();
        return beneficiary;
    }

    // Used to retrieve data when no network available in device
    public LoginResponseDto retrieveData(String userName) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveData() userName -> " + userName);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + FPSDBConstants.KEY_USERS_NAME + " = '" + userName.toLowerCase() + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        LoginResponseDto loginResponse;
        if (cursor.moveToFirst()) {
            loginResponse = new LoginResponseDto(cursor);
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveData() LoginResponseDto -> " + loginResponse);
            loginResponse.setUserDetailDto(new UserDetailDto(cursor));
            return loginResponse;
        }
        cursor.close();
        return null;
    }

    public String retrieveFpsUserName() {
        String selectQuery = "select user_name from users where user_profile = 'FPS'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            String userName = cursor.getString(cursor.getColumnIndex("user_name"));
            return userName;
        }
        cursor.close();
        return "";
    }

    // Used to retrieve user name
    public LoginResponseDto getUserDetails(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getUserDetails() userId->" + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + KEY_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        LoginResponseDto loginResponse;
        if (cursor.moveToFirst()) {
            loginResponse = new LoginResponseDto(cursor);
            loginResponse.setUserDetailDto(new UserDetailDto(cursor));
            //Log.e("UserDetails", loginResponse.toString());
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getUserDetails() LoginResponseDto ->" + loginResponse);
            return loginResponse;
        }
        cursor.close();
        return null;
    }

    public LoginResponseDto getFpsUserDetails() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where user_profile = 'FPS'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        LoginResponseDto loginResponse;
        if (cursor.moveToFirst()) {
            loginResponse = new LoginResponseDto(cursor);
            loginResponse.setUserDetailDto(new UserDetailDto(cursor));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getUserDetails() LoginResponseDto ->" + loginResponse);
            return loginResponse;
        }
        cursor.close();
        return null;
    }

    // Used to retrieve user name
    public void getFPSStoreDistrictID(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreDistrictID() userId->" + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + KEY_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //Log.e("UserDetails", loginResponse.toString());
            String DistrictID = cursor.getString(cursor.getColumnIndex("district_id"));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreDistrictID() DistrictID ->" + DistrictID);
        }
        cursor.close();
    }

    // Used to retrieve user name
    public String getFPSStoreCreatedDate(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreCreatedDate() userId->" + userId);
        String createdDate = null;
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + KEY_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //Log.e("UserDetails", loginResponse.toString());
            createdDate = cursor.getString(cursor.getColumnIndex("createdDate"));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreCreatedDate() createdDate ->" + createdDate);
            return createdDate;
        }
        cursor.close();
        return null;
    }

    // Used to retrieve user name
    public void getFPSStoreTalukID(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreTalukID() userId->" + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + KEY_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //Log.e("UserDetails", loginResponse.toString());
            String TalukID = cursor.getString(cursor.getColumnIndex("taluk_id"));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreTalukID() TalukID ->" + TalukID);
        }
        cursor.close();
    }

    // Used to retrieve user name
    public void getFPSStoreVillageID(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreVillageID() userId->" + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS + " where " + KEY_ID + " = " + userId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            //Log.e("UserDetails", loginResponse.toString());
            String villageId = cursor.getString(cursor.getColumnIndex("village_id"));
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getFPSStoreVillageID() villageId ->" + villageId);
        }
        cursor.close();
    }

    // Used to retrieve data when no network available in device
    public List<FpsStoreDto> retrieveDataStore() {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveDataStore()");
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS;
        List<FpsStoreDto> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            FpsStoreDto loginResponse = new FpsStoreDto(cursor);
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveDataStore() FpsStoreDto = " + loginResponse);
            data.add(loginResponse);
        }
        cursor.close();
        return data;
    }
    // Used to retrieve data when no network available in device
    /*public List<FpsStoreDto> retrieveDataStore() {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveDataStore()" );
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_USERS;
        List<FpsStoreDto> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            FpsStoreDto loginResponse = new FpsStoreDto(cursor);
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "retrieveDataStore() FpsStoreDto = " + loginResponse);
            data.add(loginResponse);
        }
        cursor.close();
        return data;
    }*/

    //Get Product data
    public List<EntitlementMasterRuleDtod> getAllEntitlementMasterRuleProduct(long cardType) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllEntitlementMasterRuleProduct() cardType->" + cardType);
//        String selectQuery = "SELECT a._id,a.is_person_based,a.override_price,a.minimum_qty,b.group_id,a.is_calc_required,a.is_region_based,a.has_special_rule,a.isDeleted,a.quantity,c._id as product_id,c.name,c.price,c.unit,c.local_unit,c.local_name from entitlement_rules a inner join product_group b ON a.groupId = b.group_id  inner join products c  on b.product_id = c._id where a.card_type = " + cardType + " and a.isDeleted = 0 and b.is_deleted = 0 and c.isDeleted = 0 group by b.product_id order by b.group_id";
        String selectQuery = "SELECT a._id,a.is_person_based,a.override_price,a.minimum_qty,b.group_id,a.is_calc_required,a.is_region_based,a.has_special_rule,a.isDeleted,a.quantity,c._id as product_id,c.name,c.price,c.unit,c.local_unit,c.local_name from entitlement_rules a inner join product_group b ON a.groupId = b.group_id  inner join products c  on b.product_id = c._id where a.card_type = " + cardType + " and a.isDeleted = 0 and b.is_deleted = 0 and c.isDeleted = 0 group by b.product_id order by c.sequenceNo";
        // Log.e("selectQuery", selectQuery);
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "getAllEntitlementMasterRuleProduct() QUERY = " + selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        List<EntitlementMasterRuleDtod> products = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        // Log.e("Error in Count", "Cursor count:" + cursor.getCount());
        for (int i = 0; i < cursor.getCount(); i++) {
            products.add(new EntitlementMasterRuleDtod(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return products;
    }

    //Get Product data
    public List<SplEntitlementRule> getAllSpecialRule(long productId, String cardTypeId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SPECIAL_RULES + " where groupId = " + productId
                + " AND " + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " = '" + cardTypeId + "' AND isDeleted = 0";
        List<SplEntitlementRule> region = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            region.add(new SplEntitlementRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return region;
    }

    public List<SplEntitlementRule> findByGroupAndCardTypeAndDistrict(long groupId, String cardTypeId, long districtId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SPECIAL_RULES + " where groupId = " + groupId
                + " AND " + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " = '" + cardTypeId + "' AND district_id = " +
                districtId +
                " AND isDeleted = 0";
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL QUERY With District ID  ->" + selectQuery);
        List<SplEntitlementRule> special = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            special.add(new SplEntitlementRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        if (cursor.getCount() > 0) {
            return special;
        } else {
            return null;
        }
    }

    public List<SplEntitlementRule> findByGroupAndCardTypeAndVillage(long groupId, String cardTypeId, long village_id) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SPECIAL_RULES + " where groupId = " + groupId
                + " AND " + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " = '" + cardTypeId + "' AND village_id = " +
                village_id +
                " AND isDeleted = 0";
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL QUERY With village_id ->" + selectQuery);
        List<SplEntitlementRule> special = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            special.add(new SplEntitlementRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        if (cursor.getCount() > 0) {
            return special;
        } else {
            return null;
        }
    }

    public List<SplEntitlementRule> findByGroupAndCardTypeAndTaluk(long groupId, String cardTypeId, long taluk_id) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SPECIAL_RULES + " where groupId = " + groupId
                + " AND " + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " = '" + cardTypeId + "' AND taluk_id = " +
                taluk_id +
                " AND isDeleted = 0";
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL QUERY With taluk_id ->" + selectQuery);
        List<SplEntitlementRule> special = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            special.add(new SplEntitlementRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        if (cursor.getCount() > 0) {
            return special;
        } else {
            return null;
        }
    }

    public List<SplEntitlementRule> findByGroupAndCardTypeAndNullDistrict(long groupId, String cardTypeId) {

        /*
        SELECT b FROM SplEntitlementRule b where b.group.id =?1 and b.cardTypeId=?2 and b.districtId=?3 and b.isDeleted=false
			splEntitlementRules=splEntitlementRepo.findByGroupAndCardTypeAndDistrict(group.getId(),cardType.getId(), district.getId());

        */
/*
SELECT  * FROM special_rules where groupId = 6
                AND  card_type_id = 10 AND district_id IS NULL  AND isDeleted = 0

        */

       /* Util.LoggingQueue(contextValue, "FPSDBHelper ", "findByGroupAndCardTypeAndNullDistrict() called groupId -> " + groupId  + " cardTypeId ->"
                +cardTypeId );*/
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SPECIAL_RULES + " where groupId = " + groupId
                + " AND " + FPSDBConstants.KEY_RULES_CARD_TYPE_ID + " = '" + cardTypeId + "' AND district_id IS NULL " +
                " AND isDeleted = 0";
        Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL QUERY Without District ->" + selectQuery);
        List<SplEntitlementRule> region = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            // Long _id = cursor.getLong(cursor.getColumnIndex("_id"));
            //    Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL _id  ->" + _id);
            //   Long village_id = cursor.getLong(cursor.getColumnIndex("village_id"));
            //    Util.LoggingQueue(contextValue, "FPSDBHelper ", "SPECIAL village_id  ->" + village_id);
            region.add(new SplEntitlementRule(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        if (cursor.getCount() > 0) {
            return region;
        } else {
            return null;
        }
    }

   /* public String testLastBillToday() {
        try {
            Util.LoggingQueue(contextValue, "FPSDBHelper ", "testLastBillToday() called");
            //SELECT transaction_id FROM bill where bill_status = 'F' AND transaction_id LIKE '" + dateFormat.format(new Date()) + "%'  order by date desc limit  1
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMyy", Locale.getDefault());
            String selectQuery = "SELECT transaction_id FROM bill where bill_status = 'F' AND transaction_id LIKE '" +
                    dateFormat.format(new Date()) +
                    "%'  order by date desc limit  1";
            Util.LoggingQueue(contextValue, "FPSDBHelper ", "testLastBillToday() selectQuery for unsync bill -> " + selectQuery);

            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String lastTransactionIdStr = cursor.getString(cursor.getColumnIndex("transaction_id"));
            cursor.close();
            Util.LoggingQueue(contextValue, "FPSDBHelper ", "testLastBillToday() lastTransactionIdStr -> " + lastTransactionIdStr);

            if (lastTransactionIdStr != null && !lastTransactionIdStr.isEmpty()) {
                return lastTransactionIdStr;

            } else {
                selectQuery = "SELECT transaction_id FROM bill where transaction_id LIKE '" + dateFormat.format(new Date()) + "%' order by server_bill_id desc";
                Util.LoggingQueue(contextValue, "FPSDBHelper ", "testLastBillToday() selectQuery for sycned bill -> " + selectQuery);

                cursor = database.rawQuery(selectQuery, null);
                cursor.moveToFirst();
                String maxDate = cursor.getString(cursor.getColumnIndex("transaction_id"));
                cursor.close();
                Log.e("Last Bill Today", maxDate);
                return maxDate;
            }

        } catch (Exception e) {
            return null;
        }
    }*/

    public String lastBillToday() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMyy", Locale.getDefault());
            String selectQuery = "SELECT max(transaction_id) as transaction_id FROM bill where transaction_id LIKE '" + dateFormat.format(new Date()) + "%' order by ref_id desc";
            Log.e("lastBillToday >Query", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String maxDate = cursor.getString(cursor.getColumnIndex("transaction_id"));
            cursor.close();
            Log.e("Last Bill Today", maxDate);
            return maxDate;
        } catch (Exception e) {
            Log.e("last Bill Today", e.toString(), e);
            return null;
        }
    }

    public String getMaxBillDate() {
        String maxDate = "";
        try {
            String selectQuery = "select Date(max(date)) as maxDate from bill";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            maxDate = cursor.getString(cursor.getColumnIndex("maxDate"));
            cursor.close();
        } catch (Exception e) {}
        return maxDate;
    }

    public String lastInspectionReportToday() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
            String selectQuery = "SELECT max(transaction_id) as transaction_id FROM inspection_report where transaction_id LIKE '" + dateFormat.format(new Date()) + "%' order by clientId desc";
            Log.e("lastInspReportQuery", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String maxDate = cursor.getString(cursor.getColumnIndex("transaction_id"));
            cursor.close();
            Log.e("Last Insp report Today", maxDate);
            return maxDate;
        } catch (Exception e) {
            Log.e("last Insp report Today", e.toString(), e);
            return null;
        }
    }

    public String lastInspectionStockToday() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM", Locale.getDefault());
            String selectQuery = "SELECT max(transaction_id) as transaction_id FROM inspection_stock where transaction_id LIKE '" + dateFormat.format(new Date()) + "%' order by _id desc";
            Log.e("lastStockReportQuery", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            String maxDate = cursor.getString(cursor.getColumnIndex("transaction_id"));
            cursor.close();
            Log.e("Last stock report Today", maxDate);
            return maxDate;
        } catch (Exception e) {
            Log.e("last stock report Today", e.toString(), e);
            return null;
        }
    }

    //Get Beneficiary data by QR Code
    public String lastGenId() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_REGISTRATION + " order by " + FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO + " DESC limit 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String transId = null;
        if (cursor.getCount() > 0) {
            transId = cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_REGISTRATION_CARD_REF_NO));
        }
        cursor.close();
        return transId;
    }

    //Get Product data
    public SmsProviderDto getSmsProvider() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_SMS_PROVIDER + " where status = 1 AND " + FPSDBConstants.KEY_SMS_PROVIDER_PREFERENCE + " = 'PRIMARY'";
        SmsProviderDto products = new SmsProviderDto();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            products = new SmsProviderDto(cursor);
        }
        cursor.close();
        return products;
    }

    //Bill for background sync
    public BillItemDto getAllInwardListToday(long productId) {
        BillItemDto billItemDto = new BillItemDto();
        DateTime date = new DateTime();
        String selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = date('now', 'localtime') and month = " + date.getMonthOfYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        billItemDto.setProductId(productId);
        if (cursor.getCount() > 0) {
            billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
        } else {
            billItemDto.setQuantity(0.0);
        }
        cursor.close();
        return billItemDto;
    }

    //Bill for background sync
    public BillItemDto getAllInwardListTodayTwo(long productId) {
        BillItemDto billItemDto = new BillItemDto();
        DateTime date = new DateTime();
        String selectQuery;
        int processFlag = getStatus(productId);
        if (processFlag == 0) {
            Double quantity, inwardQuantity, advanceQuantity = 0.0;
            // getting regular inward quantity
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = date('now', 'localtime') and inwardType = 'R' and month <= " + date.getMonthOfYear();
            Log.e(TAG, "getAllInwardListTodayTwo query..." + selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor.getCount() > 0) {
                inwardQuantity = cursor.getDouble(cursor.getColumnIndex("total"));
            } else {
                inwardQuantity = 0.0;
            }
            cursor.close();
            // getting advance stock processed quantity
//            selectQuery = "SELECT product_id,SUM(quantity) as total FROM advance_stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = date('now', 'localtime') and isAdded = '0' and month = " + date.getMonthOfYear();
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM advance_stock_inward where  product_id = " + productId + " and strftime('%Y-%m-%d', fps_ack_date_new / 1000, 'unixepoch') = date('now', 'localtime') and isAdded = '0' and month <= " + date.getMonthOfYear();
            Log.e(TAG, "getAlladvanceListTodayTwo query..." + selectQuery);
            Cursor cursor2 = database.rawQuery(selectQuery, null);
            cursor2.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor2.getCount() > 0) {
                advanceQuantity = cursor2.getDouble(cursor2.getColumnIndex("total"));
            } else {
                advanceQuantity = 0.0;
            }
            cursor2.close();
            quantity = inwardQuantity + advanceQuantity;
            billItemDto.setQuantity(quantity);
        } else {
            // getting regular inward quantity
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = date('now', 'localtime') and inwardType = 'R' and month <= " + date.getMonthOfYear();
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor.getCount() > 0) {
                billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
            } else {
                billItemDto.setQuantity(0.0);
            }
            cursor.close();
        }
        return billItemDto;
    }

    public BillItemDto getAllInwardListOfToDate(long productId, String toDate) {
        BillItemDto billItemDto = new BillItemDto();
        DateTime date = new DateTime();
        String selectQuery;
        int processFlag = getStatus(productId);
        if (processFlag == 0) {
            Double quantity, inwardQuantity, advanceQuantity = 0.0;
            // getting regular inward quantity
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = " + toDate + " and inwardType = 'R' and month = " + date.getMonthOfYear();
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor.getCount() > 0) {
                inwardQuantity = cursor.getDouble(cursor.getColumnIndex("total"));
            } else {
                inwardQuantity = 0.0;
            }
            cursor.close();
            // getting advance stock processed quantity
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM advance_stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = " + toDate + " and isAdded = '0' and month = " + date.getMonthOfYear();
            Cursor cursor2 = database.rawQuery(selectQuery, null);
            cursor2.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor2.getCount() > 0) {
                advanceQuantity = cursor2.getDouble(cursor2.getColumnIndex("total"));
            } else {
                advanceQuantity = 0.0;
            }
            cursor2.close();
            quantity = inwardQuantity + advanceQuantity;
            billItemDto.setQuantity(quantity);
        } else {
            // getting regular inward quantity
            selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = " + toDate + " and inwardType = 'R' and month = " + date.getMonthOfYear();
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            billItemDto.setProductId(productId);
            if (cursor.getCount() > 0) {
                billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
            } else {
                billItemDto.setQuantity(0.0);
            }
            cursor.close();
        }
        return billItemDto;
    }

    //get isAdded from advance_stock_inward
    public int getStatus(long productId) {
        int status = 1;
        try {
//            String selectQuery = "SELECT fps_ack_status as status FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) = date('now', 'localtime')";
            String selectQuery = "select isAdded as status FROM advance_stock_inward where product_id = " + productId + " and strftime('%Y-%m-%d', fps_ack_date_new / 1000, 'unixepoch') = date('now', 'localtime') order by rowid desc limit 1";
            Cursor cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            status = cursor.getInt(cursor.getColumnIndex("status"));
            cursor.close();
        } catch (Exception e) {
        }
        return status;
    }

    //Bill for background sync
    public BillItemDto getSelectedDateInwardList(long productId, String from, String to) {
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateInwardList() productId->" + productId);
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateInwardList() from->" + from);
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateInwardList() to->" + to);
        BillItemDto billItemDto = new BillItemDto();
        DateTime date = new DateTime();
        String selectQuery = "SELECT product_id,SUM(quantity) as total FROM stock_inward where  product_id = " + productId + " and date(fps_ack_date_new) between '" + from + "' and '" + to + "' and month <= " + date.getMonthOfYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        billItemDto.setProductId(productId);
        if (cursor.getCount() > 0) {
            billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
        } else {
            billItemDto.setQuantity(0.0);
        }
        cursor.close();
        return billItemDto;
    }

    //Bill for background sync
    public int totalBillsToday() {
        String selectQuery = "SELECT COUNT(*) as count FROM bill where date (date) =date('now','localtime')";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //Bill for background sync
    public int totalSelectedDateBills(String from, String to) {
        String selectQuery = "SELECT COUNT(*) as count FROM bill where date (date) between '" + from + "' and '" + to + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //Bill for background sync
    public List<CloseOfProductDto> getCloseSale() {
        List<CloseOfProductDto> closeProduct = new ArrayList<>();
        String selectQuery = "SELECT round(sum(totalCost),2) as total,round(sum(quantity),3) as quantity,product_id FROM bill_item where date (createdDate) = date('now','localtime') group by product_id";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CloseOfProductDto closeSale = new CloseOfProductDto();
            closeSale.setTotalCost(cursor.getString(cursor.getColumnIndex("total")));
            closeSale.setTotalQuantity(cursor.getString(cursor.getColumnIndex("quantity")));
            closeSale.setProductId(cursor.getLong(cursor.getColumnIndex("product_id")));
            FPSStockHistoryDto fpsStockHistory = getAllProductStockHistoryDetails(cursor.getLong(cursor.getColumnIndex("product_id")));
            closeSale.setOpeningStock(fpsStockHistory.getCurrQuantity());
            getCloseStock(closeSale, String.valueOf(cursor.getLong(cursor.getColumnIndex("product_id"))));
//            BillItemDto productInwardToday = getAllInwardListToday(cursor.getLong(cursor.getColumnIndex("product_id")));
            BillItemDto productInwardToday = getAllInwardListTodayTwo(cursor.getLong(cursor.getColumnIndex("product_id")));
            closeSale.setInward(productInwardToday.getQuantity());
            closeProduct.add(closeSale);
            cursor.moveToNext();
        }
        cursor.close();
        return closeProduct;
    }

    private void getCloseStock(CloseOfProductDto close, String productId) {
        Double closingBal = 0.0;
        String selectQuery = "select opening_balance, closing_balance from stock_history where product_id = '" + productId + "' order by _id desc limit 1";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            closingBal = cursor.getDouble(cursor.getColumnIndex("closing_balance"));
            cursor.moveToNext();
        }
        cursor.close();
        close.setClosingStock(closingBal);
    }

    //Bill for background sync
    public List<POSStockAdjustmentDto> getStockAdjustment(long productId) {
        List<POSStockAdjustmentDto> closeProduct = new ArrayList<>();
        String selectQuery = "select sum(quantity) as sum,requestType from fps_stock_adjustment where isAdjusted = 1 and product_id = " + productId + " and date(dateOfAck)=date('now','localtime') group by requestType";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            POSStockAdjustmentDto closeSale = new POSStockAdjustmentDto();
            closeSale.setQuantity(cursor.getDouble(cursor.getColumnIndex("sum")));
            closeSale.setRequestType(cursor.getString(cursor.getColumnIndex("requestType")));
            closeProduct.add(closeSale);
            cursor.moveToNext();
        }
        cursor.close();
        return closeProduct;
    }

    //Bill for background sync
    public List<POSStockAdjustmentDto> getSelectedDateStockAdjustment(long productId, String from, String to) {
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateStockAdjustment() productId->" + productId);
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateStockAdjustment() from->" + from);
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateStockAdjustment() to->" + to);
        List<POSStockAdjustmentDto> closeProduct = new ArrayList<>();
        String selectQuery = "select sum(quantity) as sum,requestType from fps_stock_adjustment where isAdjusted = 1 and product_id = " + productId + " and date(dateOfAck) between '" + from + "' and '" + to + "' group by requestType";
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getSelectedDateStockAdjustment() selectQuery->" + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            POSStockAdjustmentDto closeSale = new POSStockAdjustmentDto();
            closeSale.setQuantity(cursor.getDouble(cursor.getColumnIndex("sum")));
            closeSale.setRequestType(cursor.getString(cursor.getColumnIndex("requestType")));
            closeProduct.add(closeSale);
            cursor.moveToNext();
        }
        cursor.close();
        return closeProduct;
    }

    //Bill for background sync
    public Double totalAmountToday() {
        Double count;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT sum(totalCost) as cost FROM bill_item where date (createdDate) = date('now','localtime')";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                count = cursor.getDouble(cursor.getColumnIndex("cost"));
            } else {
                count = 0.0;
            }
            cursor.close();
            return count;
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            return 0.0;
        }
    }

    //Bill for background sync
    public Double totalSelectedDateAmount(String from, String to) {
        Double count;
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT sum(totalCost) as cost FROM bill_item where date (createdDate) between '" + from + "' and '" + to + "'";
            cursor = database.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                count = cursor.getDouble(cursor.getColumnIndex("cost"));
            } else {
                count = 0.0;
            }
            cursor.close();
            return count;
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
            return 0.0;
        }
    }

    public boolean insertIntoCloseSaleSync(Set<CloseSaleTransactionDto> closeSaleDto, String syncType) {
        tableType = "";
        childRecordId = null;
        beneficiaryMemberDto1 = null;
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        CloseSaleTransactionDto closeSaleDto1 = null;
        if (!closeSaleDto.isEmpty()) {
            database.beginTransaction();
            try {
                for (CloseSaleTransactionDto closeSale : closeSaleDto) {
                    tableType = "Parent";
                    recordId = closeSale.getId();
                    closeSaleDto1 = closeSale;
                    ContentValues values = new ContentValues();
                    values.put("dateOfTxn", closeSale.getDateOfTxn());
                    values.put("numofTrans", closeSale.getNumofTrans());
                    values.put("transactionId", closeSale.getTransactionId());
                    values.put("totalSaleCost", closeSale.getTotalSaleCost());
                    values.put("isServerAdded", closeSale.getIsServerAdded());
                    database.insert("close_sale", null, values);
                    insertCloseSaleProduct(closeSale);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "close_sale() called Exception ->" + e);
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    if (tableType.equalsIgnoreCase("Parent")) {
                        String json = new Gson().toJson(closeSaleDto1);
                        insertSyncException("close_sale", syncType, recordId, json);
                    } else if (tableType.equalsIgnoreCase("Child")) {
                        String json = new Gson().toJson(closeSaleDto1);
                        insertSyncException("close_sale_product", syncType, recordId, json);
                    }
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public void insertIntoCloseSale(CloseSaleTransactionDto closeSale) {
        try {
            ContentValues values = new ContentValues();
            values.put("dateOfTxn", closeSale.getDateOfTxn());
            values.put("numofTrans", closeSale.getNumofTrans());
            values.put("transactionId", closeSale.getTransactionId());
            values.put("totalSaleCost", closeSale.getTotalSaleCost());
            values.put("isServerAdded", closeSale.getIsServerAdded());
            database.insert("close_sale", null, values);
            insertCloseSaleProduct(closeSale);
        } catch (Exception e) {
            Log.e("Empty", e.toString());
        } finally {
        }
    }

    private void insertCloseSaleProduct(CloseSaleTransactionDto closeSale) {
        tableType = "Child";
        for (CloseOfProductDto product : closeSale.getCloseOfProductDtoList()) {
            ContentValues values = new ContentValues();
            values.put("dateOfTxn", closeSale.getDateOfTxn());
            values.put("transactionId", closeSale.getTransactionId());
            values.put("totalCost", product.getTotalCost());
            values.put("totalQuantity", product.getTotalQuantity());
            values.put("productId", product.getProductId());
            values.put("opening_balance", product.getOpeningStock());
            values.put("closing_balance", product.getClosingStock());
            values.put("inward", product.getInward());
            database.insert("close_sale_product", null, values);
        }
    }

    public List<FPSIndentRequestDto> showFpsIntentRequestProduct(long fpsId) {
        List<ProductDto> productDtoList = getAllProductDetails();
        List<FPSIndentRequestDto> fpsIndentRequestDtoList = new ArrayList<>();
        Set<FpsIntentReqProdDto> fpsIntentReqProdDtoSet = new HashSet<>();
        FPSIndentRequestDto fpsIndentRequestDto = new FPSIndentRequestDto();
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

    // Purging tables
    public void purge(int days) {
        String sql1 = "Delete from bill where date <= date ('now','-" + days + " day') and bill_status<>'R'";
        String sql2 = "Delete from bill_item where transaction_id NOT IN (select transaction_id from bill)";
        String sql3 = "Delete from login_history where login_time <= date ('now','-" + days + " day') and is_sync=1";
        String sql4 = "Delete from stock_history where date_creation <= date ('now','-" + days + " day')";
        String sql5 = "Delete from stock_inward where fps_ack_date_new <= date ('now','-" + days + " day') and is_server_add <>1";
        String sql6 = "Delete from advance_stock_inward where fps_ack_date_new <= date ('now','-" + days + " day') and syncStatus=1";

        String[] statements = new String[]{sql1, sql2, sql3, sql4, sql5, sql6};
        for (String sql : statements) {
            try {
                Log.e("purging...........", sql);
                Util.LoggingQueue(contextValue, "purging query android....", "" + sql);
                database.beginTransaction();
                database.execSQL(sql);
                database.setTransactionSuccessful();
                database.endTransaction();
            } catch (Exception e) {
                Log.e("purge exception...", e.toString(), e);
                Util.LoggingQueue(contextValue, "purging exception android....", "" + e);
            }
        }
    }

    public void purgeHeartBeatHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'HeartBeatService' or serviceType = 'Session_request_HeartBeatService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeHeartBeatHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeStatisticsHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'StatisticsService' or serviceType = 'Session_request_StatisticsService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeStatisticsHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeAllocationHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'AllocationService' or serviceType = 'Session_request_AllocationService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeAllocationHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeRegularSyncHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'RegularSyncService' or serviceType = 'Session_request_RegularSyncService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeRegularSyncHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeInwardHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'InwardService' or serviceType = 'Session_request_InwardService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeInwardHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeAdjustmentHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'AdjustmentService' or serviceType = 'Session_request_AdjustmentService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeAdjustmentHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeAdvanceStockHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'AdvanceStockService' or serviceType = 'Session_request_AdvanceStockService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeAdvanceStockHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeBillHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'BillService' or serviceType = 'Session_request_BillService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeBillHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeCloseSaleHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'CloseSaleService' or serviceType = 'Session_request_CloseSaleService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeCloseSaleHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeLoginHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'LoginService' or serviceType = 'Session_request_LoginService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeLoginHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeRemoteLogHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'RemoteLogService' or serviceType = 'Session_request_RemoteLogService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeRemoteLogHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeSyncExceptionHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'SyncExceptionService' or serviceType = 'Session_request_SyncExceptionService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeSyncExceptionHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeBifurcationHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and serviceType = 'BifurcationService' ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeBifurcationHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeMigrationOutHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'MigrationOutService' or serviceType = 'Session_request_MigrationService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeMigrationOutHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeMigrationInHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'MigrationInService' or serviceType = 'Session_request_MigrationService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeMigrationInHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    public void purgeBiometricHistory(int heartBeatPurgeDays) {
        try {
            int heartBeatPurge = Integer.valueOf(heartBeatPurgeDays);
            String sqlPurgeQuery = "Delete from backgroundProcessHistory where strftime('%Y-%m-%d', requestDateTime / 1000, 'unixepoch') <= date('now','-" + heartBeatPurge + " day') and (serviceType = 'BiometricService' or serviceType = 'Session_request_BiometricService') ";
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "purgeBiometricHistory exc..." + e.toString());
        } finally {
            database.endTransaction();
        }
    }

    // Purging tables
   /* public void purgeBill(int days) {
        String sqlPurgeQuery = "Delete from bill where date <=date ('now','-"+days+" day') and bill_status<>'R'";
        try {
            database.beginTransaction();
            database.execSQL(sqlPurgeQuery);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("purge BillItem Details", e.toString(), e);

        } finally {
            database.endTransaction();
            purgeBillItemDetails(days);
        }
    }
    private void purgeBillItemDetails(int days) {
        String sql = "Delete from bill_item where transaction_id NOT IN (select transaction_id from bill)";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("purge BillItem Details", e.toString(), e);

        } finally {
            database.endTransaction();
            purgeLoginHistoryDetails(days);
        }
    }
    private void purgeLoginHistoryDetails(int days) {
        String sql = "Delete from login_history where login_time <= date ('now','-"+days+" day') and is_sync=1";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("purge BillItem Details", e.toString(), e);

        } finally {
            database.endTransaction();
            purgeStockHistoryDetails(days);
        }
    }
    private void purgeStockHistoryDetails(int days) {
        String sql = "Delete from stock_history where date_creation <=date ('now','-"+days+" day')";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("purge BillItem Details", e.toString(), e);

        } finally {
            database.endTransaction();
            purgeInwardDetails(days);
        }
    }
    private void purgeInwardDetails(int days) {
        String sql = "DELETE FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " WHERE " + FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE + " <= date ('now','-"+days+" day') and is_server_add <>1";
        try {
            database.beginTransaction();
            database.execSQL(sql);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("purge Inward Details", e.toString(), e);

        } finally {
            database.endTransaction();
        }
    }*/

    public String getFpsCode(String profile) {
        String fpsCode = "";
        String selectQuery = "select gen_code from users where user_profile = '" + profile + "' order by _id desc limit 1";
        Log.e("DB HELPER", "fps code selectQuery..." + selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            fpsCode = cursor.getString(cursor.getColumnIndex("gen_code"));
        }
        cursor.close();
        return fpsCode;
    }

    public void stockAdjustmentUpdate(List<StockRequestDto.ProductList> prodsList) {
        try {
            for (StockRequestDto.ProductList product : prodsList) {
                ContentValues values = new ContentValues();
                double closing;
                /*NumberFormat formatter = new DecimalFormat("#0.000");
                formatter.setRoundingMode(RoundingMode.CEILING);*/
                if (product.getAdjustmentItem() == 1) {
                    closing = product.getQuantity() - product.getRecvQuantity();
                } else {
                    closing = product.getQuantity() + product.getRecvQuantity();
                }
                closing = Double.parseDouble(Util.quantityRoundOffFormat(closing));
                values.put(FPSDBConstants.KEY_STOCK_QUANTITY, closing);
                database.beginTransaction();
                FPSStockDto stockList = getAllProductStockDetails(product.getId());
                insertStockHistory(stockList.getQuantity(), closing, "ADJUSTMENT", product.getRecvQuantity(), product.getId());
                database.update(FPSDBTables.TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + product.getId(), null);
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("stock update", e.toString(), e);
        } finally {
            database.endTransaction();
        }
    }

    //roleFeature Retrivelist
    public List<RoleFeatureDto> retrieveData(long userId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE " + FPSDBConstants.KEY_ROLE_PARENTID + "= 0 AND isDeleted = 1 AND " + FPSDBConstants.KEY_ROLE_USERID + "= " + userId
                + "  group by role_type order by " + FPSDBConstants.KEY_ROLE_FEATUREID;
        Log.e("Query", selectQuery);
        List<RoleFeatureDto> roleFeature = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            roleFeature.add(new RoleFeatureDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return roleFeature;
    }

    //roleFeature Retrivelist
    public boolean retrievePrintAllowed(long userId) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE " + FPSDBConstants.KEY_ROLE_USERID + "= " + userId + " and isDeleted = 1 AND role_name = 'PRINT_RECEIPT' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

/*
    //printer Retrivelist
    public String retrievePrinter() {
        String selectQuery = "SELECT * FROM configuration where name = 'printer'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        String printerMac = "";
        if (cursor.getCount() > 0) {
            printerMac = cursor.getString(cursor.getColumnIndex("value"));
        }
        cursor.close();
        return printerMac;
    }*/

    //roleFeature retrieveRolesData
    public Set<String> retrieveRolesDataString(long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveRolesDataString() called userId = " + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE " + FPSDBConstants.KEY_ROLE_PARENTID + "= 0 AND isDeleted = 1 AND " +
                FPSDBConstants.KEY_ROLE_USERID + "= " + userId
                + " order by " + FPSDBConstants.KEY_ROLE_FEATUREID;
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveRolesDataString() called selectQuery = " + selectQuery);
        Set<String> roleFeature = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            roleFeature.add(cursor.getString(cursor.getColumnIndex(FPSDBConstants.KEY_ROLE_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return roleFeature;
    }

    public List<RoleFeatureDto> retrieveSalesOrderData(long roleId, long userId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveSalesOrderData() called roleId = " + roleId + "   userId = " + userId);
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE role_parent_id = " + roleId + " AND isDeleted = 1 AND user_id = " + userId + " order by role_id";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveSalesOrderData() called selectQuery = " + selectQuery);
        List<RoleFeatureDto> roleFeature = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            roleFeature.add(new RoleFeatureDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return roleFeature;
    }

    //Bill for background sync
    public List<BillDto> getAllBillsForSync() {
        Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getAllBillsForSync() called ");
        List<BillDto> bills = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_STATUS + "<>'T'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto bill = new BillDto(cursor);
            bill.setBillItemDto(getBillItems(bill.getTransactionId()));
            bill.setTotalBillItemCount(getBillItems(bill.getTransactionId()).size());
            bills.add(bill);
            Log.i("bills", bill.toString());
            Util.LoggingQueue(contextValue, "$$--FPSDBHelper--$$", "getAllBillsForSync() called Bill = " + bill.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    //Bill for background sync
    public List<CloseSaleTransactionDto> getAllCloseSaleForSync() {
        List<CloseSaleTransactionDto> bills = new ArrayList<>();
        String selectQuery = "SELECT  * FROM close_sale where isServerAdded<>0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CloseSaleTransactionDto bill = new CloseSaleTransactionDto(cursor);
            bill.setCloseOfProductDtoList(getCloseSaleItems(bill.getTransactionId()));
            bills.add(bill);
            Log.i("bills", bill.toString());
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }

    public Set<CloseOfProductDto> getCloseSaleItems(long referenceId) {
        List<CloseOfProductDto> billItems = new ArrayList<>();
        String selectQuery = "SELECT * FROM close_sale_product where transactionId=" + referenceId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CloseOfProductDto billItemDto = new CloseOfProductDto(cursor);
            billItems.add(billItemDto);
            cursor.moveToNext();
        }
        cursor.close();
        return new HashSet<>(billItems);
    }

    //Bill for background sync
    public long getAllBillsForSyncCheck() {
        String selectQuery = "SELECT  count(*) as count FROM " + FPSDBTables.TABLE_BILL + " where " + FPSDBConstants.KEY_BILL_STATUS + "<>'T'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        long count = cursor.getLong(cursor.getColumnIndex("count"));
        cursor.close();
        return count;
    }

    //Bill for background sync
    public List<LoginHistoryDto> getAllLoginHistory() {
        List<LoginHistoryDto> loginHistoryList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_LOGIN_HISTORY + " where is_sync=0 OR is_logout_sync = 0";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LoginHistoryDto loginHistory = new LoginHistoryDto(cursor);
            loginHistoryList.add(loginHistory);
            cursor.moveToNext();
        }
        cursor.close();
        return loginHistoryList;
    }

    public List<MigrationOutDTO> getMigrationOut() {
        DateTime month = new DateTime();
        String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_FPS_MIGRATION_OUT + " where isAdded = 0 and month_out=" + month.getMonthOfYear() + " and year_out=" + month.getYear()+ " and blocked_date is null";
        Log.e("Migration Select", selectQuery);
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        List<MigrationOutDTO> oldRationCard = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            oldRationCard.add(new MigrationOutDTO(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return oldRationCard;
    }

    public List<MigrationOutDTO> getMigrationIn() {
        DateTime month = new DateTime();
        String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_FPS_MIGRATION_IN + " where isAdded = 0 and month_in=" + month.getMonthOfYear() + " and year_in=" + month.getYear()+ " and blocked_date is not null";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        List<MigrationOutDTO> oldRationCard = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            oldRationCard.add(new MigrationOutDTO(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return oldRationCard;
    }

   /* //Bill for background sync
    public List<BillDto> getAllOfflineBills(String cardNo) {
        List<BillDto> bills = new ArrayList<>();
        String selectQuery = "SELECT * FROM offline_bill  where ufc_code = '" + cardNo + "'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BillDto bill = new BillDto(cursor);
            bill.setBillItemDto(getAllOfflineBillItems(bill.getTransactionId()));
            bills.add(bill);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }*/

    public int userCount() {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "userCount() called ");
        String selectQuery = "SELECT count(*) as count FROM advance_stock_inward where isAdded = 1 and month <= " + new DateTime().getMonthOfYear() + " and year <= " + new DateTime().getYear();
        Cursor cursor = database.rawQuery(selectQuery, null);
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "userCount() selectQuery->" + selectQuery);
        //Log.e("userCount", selectQuery);
        cursor.moveToFirst();
        int counting = cursor.getInt(cursor.getColumnIndex("count"));
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^", "userCount() counting->" + counting);
        //Log.e("dsfdsf", counting + "");
        cursor.close();
        return counting;
    }

    public List<BillItemProductDto> userAdvanceStock() {
        //Bill for background sync
        List<BillItemProductDto> billItems = new ArrayList<>();
        String selectQuery = "SELECT sum(quantity) as quantity,product_id FROM advance_stock_inward where isAdded = 1 and month <= " + new DateTime().getMonthOfYear() + " and year <= " + new DateTime().getYear() + " group by product_id";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        Log.e("userAdvanceStock", cursor.getCount() + "----" + selectQuery);
        for (int i = 0; i < cursor.getCount(); i++) {
            BillItemProductDto bill = new BillItemProductDto();
            bill.setProductId(cursor.getLong(cursor.getColumnIndex("product_id")));
            bill.setQuantity(cursor.getDouble(cursor.getColumnIndex("quantity")));
            billItems.add(bill);
            cursor.moveToNext();
        }
        cursor.close();
        return billItems;
    }
/*
    //Bill for background sync
    public List<BenefCardImageDto> getAllImages() {
        List<BenefCardImageDto> bills = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_IMAGE + " where status <>'S'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            BenefCardImageDto bill = new BenefCardImageDto();
            bill.setRationNumber(cursor.getString(cursor.getColumnIndex("card_number")));
            bill.setAddress(cursor.getString(cursor.getColumnIndex("image_id")));
            bills.add(bill);
            cursor.moveToNext();
        }
        cursor.close();
        return bills;
    }*/

    public long retrieveId(String roleId) {
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveId() called roleId = " + roleId);
        String selectQuery = "SELECT  role_id FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE " + FPSDBConstants.KEY_ROLE_NAME + "= '" + roleId + "'";
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveId() called selectQuery = " + selectQuery);
        long id = 0l;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            id = cursor.getLong(cursor.getColumnIndex("role_id"));
            cursor.moveToNext();
        }
        Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "retrieveId() called id = " + id);
        cursor.close();
        return id;
    }

    public String retrieveBeneficiaryId(String aadhar) {
        String beneId = "";
        try {
            String selectQuery = "select beneficiary_id from beneficiary_member where uid = " + aadhar;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            beneId = String.valueOf(cursor.getInt(cursor.getColumnIndex("beneficiary_id")));
            cursor.close();
        } catch (Exception e) {
        }
        return beneId;
    }



    //Get VersionInfo data
    public List<VersionDto> getVersionInfo() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_UPGRADE + " ORDER BY android_new_version DESC;";
        List<VersionDto> versioninfo = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            versioninfo.add(new VersionDto(cursor));
            cursor.moveToNext();
        }
        Log.e("version_detail", "--->" + versioninfo.toString());
        cursor.close();
        return versioninfo;
    }

    //This function retrives the data for advance_stock_inward table
    public List<FpsAdvanceStockDto> getAdvanceStockList() {
//        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD + " WHERE " + FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS + "=0";
        String selectQuery = "select  * from " + FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD + " order by outward_date desc";
        List<FpsAdvanceStockDto> advanceStockList = new ArrayList<>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            advanceStockList.add(new FpsAdvanceStockDto(cursor));
            cursor.moveToNext();
        }
        Log.e("advanceStockList", "--->" + advanceStockList.toString());
        cursor.close();
        return advanceStockList;
    }

    public void insertAdvanceFpsStock(List<FpsAdvanceStockDto> fpsInwardList) {
        ContentValues values = new ContentValues();
        for (FpsAdvanceStockDto stocklist : fpsInwardList) {
            try {
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_GODOWNID, stocklist.getGowdownId());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSID, stocklist.getFpsId());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_OUTWARD_DATE, stocklist.getOutwardDate());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_PRODUCTID, stocklist.getProductId());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_QUANTITY, stocklist.getQuantity());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_UNIT, stocklist.getUnit());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_BATCH_NO, stocklist.getBatchNo());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_IS_FPSACKSTATUS, stocklist.getStatus());
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = new Date(stocklist.getFpsDate());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSACKDATE, dateFormat.format(date));
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_FPSRECEIVEIQUANTITY, stocklist.getFpsQuantity());
                values.put("month", stocklist.getMonth());
                values.put("year", stocklist.getYear());
                values.put(FPSDBConstants.KEY_FPS_STOCK_INWARD_DELIEVERY_CHELLANID, stocklist.getChallanId());
                database.insert(FPSDBTables.TABLE_FPS_ADVANCE_STOCK_INWARD, null, values);
            } catch (Exception e) {
                Log.e("Table advancestock", "Exception", e);
            }
        }
    }

    //This function inserts sync exc data
    public void insertSyncExcData(POSSyncExceptionDto posSyncExceptionDto) {
        try {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "insert syncException...", posSyncExceptionDto.toString());
            ContentValues values = new ContentValues();
            com.omneagate.Util.Util.LoggingQueue(contextValue, "fpsId", "" + posSyncExceptionDto.getFpsId());
            values.put("fpsId", String.valueOf(posSyncExceptionDto.getFpsId()));
            com.omneagate.Util.Util.LoggingQueue(contextValue, "syncMode", "" + posSyncExceptionDto.getSyncMode());
            values.put("syncMode", posSyncExceptionDto.getSyncMode());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "tableName", "" + posSyncExceptionDto.getTableName());
            values.put("tableName", posSyncExceptionDto.getTableName());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "action", "" + posSyncExceptionDto.getAction());
            values.put("action", posSyncExceptionDto.getAction());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "recordId", "" + posSyncExceptionDto.getRecordId());
            values.put("recordId", "" + posSyncExceptionDto.getRecordId());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "lastSyncTime", "" + posSyncExceptionDto.getLastSyncTime());
            values.put("lastSyncTime", "" + posSyncExceptionDto.getLastSyncTime());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "rawData", "" + posSyncExceptionDto.getRawData());
            values.put("rawData", posSyncExceptionDto.getRawData());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "errorDescription", "" + posSyncExceptionDto.getErrorDescription());
            values.put("errorDescription", posSyncExceptionDto.getErrorDescription());
            com.omneagate.Util.Util.LoggingQueue(contextValue, "isSynced", "0");
            values.put("isSynced", "0");
            long returnedValue = database.insert("syncException", null, values);
            com.omneagate.Util.Util.LoggingQueue(contextValue, "returnedValue", "" + returnedValue);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "insert syncException Exception...", e.toString());
        }
    }

    public List<POSSyncExceptionDto> getAllSyncExcData() {
        List<POSSyncExceptionDto> posSyncExceptionDtoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM syncException where isSynced = '0'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto(cursor);
                posSyncExceptionDtoList.add(posSyncExceptionDto);
                Log.i("posSyncExceptionDto", posSyncExceptionDto.toString());
                cursor.moveToNext();
            }
        }
        cursor.close();
        return posSyncExceptionDtoList;
    }

    public List<FpsAdvanceStockDto> getAllUnsyncedProcessedAdvanceStock() {
        List<FpsAdvanceStockDto> fpsAdvanceStockDtoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM advance_stock_inward where isAdded = '0' and syncStatus = '0'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                FpsAdvanceStockDto fpsAdvanceStockDto = new FpsAdvanceStockDto(cursor);
                fpsAdvanceStockDtoList.add(fpsAdvanceStockDto);
                Log.i("fpsAdvanceStockDto", fpsAdvanceStockDto.toString());
                cursor.moveToNext();
            }
        }
        cursor.close();
        return fpsAdvanceStockDtoList;
    }

    public List<FpsAdvanceStockDto> getAllUnsyncedProcessedAdvanceStockForTenthMonth() {
        List<FpsAdvanceStockDto> fpsAdvanceStockDtoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM advance_stock_inward where (syncStatus = '0' or syncStatus is null) and isAdded = '0' and month = '10' and year = '2016'";
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor != null) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                FpsAdvanceStockDto fpsAdvanceStockDto = new FpsAdvanceStockDto(cursor);
                fpsAdvanceStockDtoList.add(fpsAdvanceStockDto);
                Log.i("fpsAdvanceStockDto", fpsAdvanceStockDto.toString());
                cursor.moveToNext();
            }
        }
        cursor.close();
        return fpsAdvanceStockDtoList;
    }

    public List<InspectionCriteriaDto> getAllCriteria() {
        String selectQuery = "SELECT  * FROM  inspection_criteria order by criteria_id";
        List<InspectionCriteriaDto> criteriaList = new ArrayList<InspectionCriteriaDto>();
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("cursorSize", "" + cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            criteriaList.add(new InspectionCriteriaDto(cursor));
            cursor.moveToNext();
            Log.e("CriteriaListDb", criteriaList.toString());
        }
        return criteriaList;
    }

    //Update sync exc data
    public void updateSyncExcData(POSSyncExceptionDto posSyncExceptionDto, String localId) {
        try {
            Util.LoggingQueue(contextValue, "localId 2...", localId);
            ContentValues values = new ContentValues();
            values.put("fpsId", String.valueOf(posSyncExceptionDto.getFpsId()));
            values.put("isSynced", "1");
            database.update("syncException", values, "_id = " + localId, null);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "update syncException Exception...", e.toString());
        }
    }

    // Update advance stock table
    public void updateAdvanceStockSyncStatus(FpsAdvanceStockDto fpsAdvanceStockDto) {
        try {
            ContentValues values = new ContentValues();
            values.put("syncStatus", "1");
            database.update("advance_stock_inward", values, "_id = " + fpsAdvanceStockDto.getGodownStockOutwardDto().getId(), null);
        } catch (Exception e) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, "updateAdvanceStockSyncStatus Exception...", e.toString());
        }
    }

    public void insertSyncException(String tableName, String syncType, Long recordId, String rawData) {
        try {
            POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
            posSyncExceptionDto.setSyncMode(syncType);
            posSyncExceptionDto.setTableName(tableName);
            posSyncExceptionDto.setAction("INSERT");
            posSyncExceptionDto.setRecordId(recordId);
            posSyncExceptionDto.setRawData(rawData);
            posSyncExceptionDto.setErrorDescription("Exception while inserting " + tableName);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String dateStr = df.format(new Date());
            Date currentDate = df.parse(dateStr);
            posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
            FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
        } catch (Exception e1) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "Exception while inserting " + tableName + " : " + e1);
        }
    }

    public void insertInvalidDateException(String tableName, String syncType, String rawData, String errorDesc) {
        try {
            POSSyncExceptionDto posSyncExceptionDto = new POSSyncExceptionDto();
            posSyncExceptionDto.setSyncMode(syncType);
            posSyncExceptionDto.setTableName(tableName);
            posSyncExceptionDto.setAction("INSERT");
            posSyncExceptionDto.setRecordId(0l);
            posSyncExceptionDto.setRawData(rawData);
            posSyncExceptionDto.setErrorDescription(errorDesc);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String dateStr = df.format(new Date());
            Date currentDate = df.parse(dateStr);
            posSyncExceptionDto.setLastSyncTime(currentDate.getTime());
            FPSDBHelper.getInstance(contextValue).insertSyncExcData(posSyncExceptionDto);
        } catch (Exception e1) {
            Util.LoggingQueue(contextValue, "^^^^FPSDBHelper^^^^ ", "" + "Exception while inserting " + tableName + " : " + e1);
        }
    }

    public List<StockInspectionDto> getAllStockImages() {
        List<StockInspectionDto> listStockInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM inspection_stock where status ='R'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                StockInspectionDto stockInspectionDto = new StockInspectionDto(cursor);
//                listStockInspectionDto.add(stockInspectionDto);
                if (getReportId(stockInspectionDto.getClientReportId()) != 0) {
                    stockInspectionDto.setReportId(getReportId(stockInspectionDto.getClientReportId()));
                    /*if (getBase64CardImages(stockInspectionDto.getId(), "stock_id") != null) {
                        List<String> listImages = getBase64CardImages(stockInspectionDto.getId(), "stock_id");
                        stockInspectionDto.setImages(listImages);//List of Images from Image Table
                    }*/
                    listStockInspectionDto.add(stockInspectionDto);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Image Table" + e.toString());
        }
        return listStockInspectionDto;
    }

    public List<CardInspectionDto> getAllCardVerificationImages() {
        List<CardInspectionDto> listCardInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_VERIFICATION + " where status ='R'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                CardInspectionDto cardInspectionDto = new CardInspectionDto(cursor);
                if (getReportId(cardInspectionDto.getClientReportId()) != 0) {
                    cardInspectionDto.setReportId(getReportId(cardInspectionDto.getClientReportId()));
                   /* if (getBase64CardImages(cardInspectionDto.getId(), "card_verification_id") != null) {
                        List<String> listImages = getBase64CardImages(cardInspectionDto.getId(), "card_verification_id");
                        cardInspectionDto.setImages(listImages);//List of Images from Image Table
                    }*/
                    listCardInspectionDto.add(cardInspectionDto);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Image Table" + e.toString());
        }
        return listCardInspectionDto;
    }

    public List<ShopAndOthersDto> getAllOthersImages() {
        List<ShopAndOthersDto> listOthersDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_OTHERS + " where status ='R'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ShopAndOthersDto othersDto = new ShopAndOthersDto(cursor, "Others");
                if (getReportId(othersDto.getClientReportId()) != 0) {
                    othersDto.setReportId(getReportId(othersDto.getClientReportId()));
//                    if (getBase64CardImages(othersDto.getId(), "others_id") != null) {
//                        List<String> listImages = getBase64CardImages(othersDto.getId(), "others_id");
//                        othersDto.setImages(listImages);//List of Images from Image Table
//                    }
                    listOthersDto.add(othersDto);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Others Table" + e.toString());
        }
        return listOthersDto;
    }

    public List<ShopAndOthersDto> getAllShopImages() {
        List<ShopAndOthersDto> listShopDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_TIME + " where status ='R'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ShopAndOthersDto shopDto = new ShopAndOthersDto(cursor, "FpsTime");
                if (getReportId(shopDto.getClientReportId()) != 0) {
                    shopDto.setReportId(getReportId(shopDto.getClientReportId()));
//                    if (getBase64CardImages(shopDto.getId(), "fpsTimeId") != null) {
//                        List<String> listImages = getBase64CardImages(shopDto.getId(), "fpsTimeId");
//                        shopDto.setImages(listImages);//List of Images from Image Table
//                    }
                    listShopDto.add(shopDto);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Shop Table" + e.toString());
        }
        return listShopDto;
    }

    public List<WeighmentInspectionDto> getAllWeighmentImages() {
        List<WeighmentInspectionDto> listWeighmentInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_WEIGHMENT + " where status ='R'";
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                WeighmentInspectionDto weighmentInspectionDto = new WeighmentInspectionDto(cursor);
                if (getReportId(weighmentInspectionDto.getClientReportId()) != 0) {
                    weighmentInspectionDto.setReportId(getReportId(weighmentInspectionDto.getClientReportId()));
//                    if (getBase64CardImages(weighmentInspectionDto.getId(), "weighment_id") != null) {
//                        List<String> listImages = getBase64CardImages(weighmentInspectionDto.getId(), "weighment_id");
//                        weighmentInspectionDto.setImages(listImages);//List of Images from Image Table
//                    }
                    listWeighmentInspectionDto.add(weighmentInspectionDto);
                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Weighment Table" + e.toString());
        }
        return listWeighmentInspectionDto;
    }

    public List<FpsAllocationCommodityDetailDto> get_stock_allocation_details(String month, int year) {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_STOCK_ALLOCATION + " where month ='" + month + "' and " + year + "";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("CursorSize", "" + cursor.getCount());
        List<FpsAllocationCommodityDetailDto> dtolist = new ArrayList<>();
        ;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                FpsAllocationCommodityDetailDto dto = new FpsAllocationCommodityDetailDto();
                GroupDto groupdto = new GroupDto();
                groupdto.setId(cursor.getLong(cursor.getColumnIndex("group_id")));
                groupdto.setGroupName(cursor.getString(cursor.getColumnIndex("group_name")));
                groupdto.setUnit(cursor.getString(cursor.getColumnIndex("unit")));
                groupdto.setLocalUnit(cursor.getString(cursor.getColumnIndex("localUnit")));
                dto.setGroupDto(groupdto);
                dto.setAllocatedQty(cursor.getDouble(cursor.getColumnIndex("allocated_qty")));
                dtolist.add(dto);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return dtolist;
    }

    public void insert_stock_allocation(Set<FpsAllocationCommodityDetailDto> dtolist, String syncType) {
        com.omneagate.Util.Util.LoggingQueue(contextValue, TAG, "insert_stock_allocation called...");
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        FpsAllocationCommodityDetailDto error_dto = null;
        if (!dtolist.isEmpty()) {
            com.omneagate.Util.Util.LoggingQueue(contextValue, TAG, "insert_stock_allocation not empty..." + dtolist.size());
            database.beginTransaction();
            try {
                for (FpsAllocationCommodityDetailDto dto : dtolist) {
                    com.omneagate.Util.Util.LoggingQueue(contextValue, TAG, "insert_stock_allocation not empty..." + dtolist.toString());
                    error_dto = dto;
                    recordId = dto.getId();
                    ContentValues values = new ContentValues();
                    values.put("id", dto.getId());
                    values.put("group_id", dto.getGroupDto().getId());
                    values.put("group_name", dto.getGroupDto().getGroupName());
                    values.put("allocated_qty", dto.getAllocatedQty());
                    values.put("advance_qty", dto.getAdvanceQty());
                    values.put("current_qty", dto.getCurrentQty());
                    values.put("total_qty", dto.getTotalQty());
                    values.put("month", dto.getMonth());
                    values.put("year", dto.getYear());
                    List<ProductDto> list = dto.getGroupDto().getProductDto();
                    if (list != null && list.size() > 0) {
                        values.put("unit", list.get(0).getProductUnit());
                        values.put("localUnit", list.get(0).getProductUnit());
                    }
                    database.insertWithOnConflict(FPSDBTables.TABLE_STOCK_ALLOCATION, "id", values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "stock_allocation Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(error_dto);
                    insertSyncException(FPSDBTables.TABLE_STOCK_ALLOCATION, syncType, recordId, json);
                }
            }
        }
    }

    public List<GodownStockOutwardDto> getAllInwardSync() {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_STOCK_INWARD + " where fps_ack_status = 1 and is_server_add = 1 group by referenceNo order by outward_date desc ";
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("Query value", "Cursor:" + cursor.getCount());
        List<GodownStockOutwardDto> fpsInwardList = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            fpsInwardList.add(new GodownStockOutwardDto(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return fpsInwardList;
    }

    public int checkRationCardNumber(String cardnumber) {
        String rationCardNumber = "";
        try {
            String selectQuery = "SELECT count(old_ration_card_num) as cnt FROM " + FPSDBTables.TABLE_BENEFICIARY + " where old_ration_card_num = '"
                    + cardnumber + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
            Log.i("query:  ", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                rationCardNumber = cursor.getString(cursor.getColumnIndex("cnt"));
                cursor.close();
                if (rationCardNumber == null && rationCardNumber.equals("0")) {
                    return 0;
                } else {
                    return Integer.parseInt(rationCardNumber);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public int compare_CardandBillnumber(String cardnumber, String billnumber) {
        String count = "";
        try {
            String selectQuery = "SELECT count(beneficiary_id) as cnt FROM bill b join beneficiary bf on b.beneficiary_id = bf._id where b.transaction_id = '" + billnumber + "' and bf.old_ration_card_num = '" + cardnumber + "'";
            Log.i("query:  ", selectQuery);
            Cursor cursor = database.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getString(cursor.getColumnIndex("cnt"));
                cursor.close();
                if (count == null && count.equals("0")) {
                    return 0;
                } else {
                    return Integer.parseInt(count);
                }
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public List<CardInspectionDto> getAllCardVerificationImages(long clientId) {
        List<CardInspectionDto> listCardInspectionDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CARD_VERIFICATION + " where client_reportId=" + clientId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                CardInspectionDto cardInspectionDto = new CardInspectionDto(cursor);
//                if (getReportId(cardInspectionDto.getClientReportId()) != 0) {
                cardInspectionDto.setReportId(getReportId(cardInspectionDto.getClientReportId()));
                listCardInspectionDto.add(cardInspectionDto);
//                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Image Table" + e.toString());
        }
        return listCardInspectionDto;
    }

    public List<WeighmentInspectionDto> getAllWeighmentImages(long clientId) {
        List<WeighmentInspectionDto> listWeighmentInspectionDto = new ArrayList<>();
//        try {
        String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_WEIGHMENT + " where client_reportId = " + clientId;
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("CursorSize", "" + cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            WeighmentInspectionDto weighmentInspectionDto = new WeighmentInspectionDto(cursor);
//                if (getReportId(weighmentInspectionDto.getClientReportId()) != 0) {
            weighmentInspectionDto.setReportId(getReportId(weighmentInspectionDto.getClientReportId()));
//                    if (getBase64CardImages(weighmentInspectionDto.getId(), "weighment_id") != null) {
//                        List<String> listImages = getBase64CardImages(weighmentInspectionDto.getId(), "weighment_id");
//                        weighmentInspectionDto.setImages(listImages);//List of Images from Image Table
//                    }
            listWeighmentInspectionDto.add(weighmentInspectionDto);
//                }
            cursor.moveToNext();
        }
        cursor.close();
//        } catch (Exception e) {
//            Log.e("Error", "List of offline Images from Weighment Table" + e.toString());
//        }
        return listWeighmentInspectionDto;
    }

    public List<ShopAndOthersDto> getAllShopImages(long clientId) {
        List<ShopAndOthersDto> listShopDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_TIME + " where client_reportId=" + clientId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ShopAndOthersDto shopDto = new ShopAndOthersDto(cursor, "FpsTime");
//                if (getReportId(shopDto.getClientReportId()) != 0) {
                shopDto.setReportId(getReportId(shopDto.getClientReportId()));
//                    if (getBase64CardImages(shopDto.getId(), "fpsTimeId") != null) {
//                        List<String> listImages = getBase64CardImages(shopDto.getId(), "fpsTimeId");
//                        shopDto.setImages(listImages);//List of Images from Image Table
//                    }
                listShopDto.add(shopDto);
//                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Shop Table" + e.toString());
        }
        return listShopDto;
    }

    public List<ShopAndOthersDto> getAllOthersImages(long clientId) {
        List<ShopAndOthersDto> listOthersDto = new ArrayList<>();
        try {
            String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_OTHERS + " where client_reportId=" + clientId;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("CursorSize", "" + cursor.getCount());
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                ShopAndOthersDto othersDto = new ShopAndOthersDto(cursor, "Others");
//                if (getReportId(othersDto.getClientReportId()) != 0) {
                othersDto.setReportId(getReportId(othersDto.getClientReportId()));
//                    if (getBase64CardImages(othersDto.getId(), "others_id") != null) {
//                        List<String> listImages = getBase64CardImages(othersDto.getId(), "others_id");
//                        othersDto.setImages(listImages);//List of Images from Image Table
//                    }
                listOthersDto.add(othersDto);
//                }
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("Error", "List of offline Images from Others Table" + e.toString());
        }
        return listOthersDto;
    }

    public boolean insert_nfsa_pos_data(Set<NfsaPosDataDto> nfsaPosDataDtos, String syncType) {
        Log.e("insert_nfsa_pos_data", nfsaPosDataDtos.toString());
        boolean isSuccessFullyInserted = false;
        Long recordId = null;
        NfsaPosDataDto dto2 = null;
        if (!nfsaPosDataDtos.isEmpty()) {
            database.beginTransaction();
            try {
                for (NfsaPosDataDto dto : nfsaPosDataDtos) {
                    dto2 = dto;
                    Log.e("nfsaPosDataDtos", dto.toString());
                    ContentValues values = new ContentValues();
                    if (dto.getBeneficiaryDto() != null)
                        values.put("beneficiary_id", dto.getBeneficiaryDto().getId());
                    values.put("card_type", dto.getCardTypeName());
                    values.put("head_of_family_name", dto.getFamilyHeadName());
                    values.put("l_head_of_family_name", dto.getLocalFamilyHeadName());
                    if (dto.getIsDeleted())
                        values.put("is_deleted", 1);
                    else
                        values.put("is_deleted", 0);
                    if (dto.getNfsaStatus())
                        values.put("nfsaStatus", 1);
                    else
                        values.put("nfsaStatus", 0);
                    database.insertWithOnConflict(FPSDBTables.TABLE_NFSAPOSDATA, "beneficiary_id", values, SQLiteDatabase.CONFLICT_REPLACE);
                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();
            } catch (Exception e) {
                isSuccessFullyInserted = false;
                com.omneagate.Util.Util.LoggingQueue(contextValue, "TABLE_NFSAPOSDATA Exception...", e.toString());
            } finally {
                database.endTransaction();
                if (!isSuccessFullyInserted) {
                    String json = new Gson().toJson(dto2);
                    insertSyncException("TABLE_NFSAPOSDATA", syncType, recordId, json);
                }
            }
        }
        return isSuccessFullyInserted;
    }

    public NfsaPosDataDto get_nfsaStatus(long beneficiary_id) {
        String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_NFSAPOSDATA + " where beneficiary_id = " + beneficiary_id;
        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.e("selectQuery", "" + selectQuery);
        Log.e("CursorSize", "" + cursor.getCount());
        NfsaPosDataDto dto = null;
        if (cursor != null && cursor.getCount() > 0) {
            dto = new NfsaPosDataDto();
            cursor.moveToFirst();
            if (cursor.getInt(cursor.getColumnIndex("nfsaStatus")) == 0)
                dto.setNfsaStatus(false);
            else
                dto.setNfsaStatus(true);
            dto.setCardTypeName(cursor.getString(cursor.getColumnIndex("card_type")));
            dto.setFamilyHeadName(cursor.getString(cursor.getColumnIndex("head_of_family_name")));
            dto.setLocalFamilyHeadName(cursor.getString(cursor.getColumnIndex("l_head_of_family_name")));
        }
        return dto;
    }

    public List<Product> getAllClosingBalance() {
        List<Product> dtoList = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_TG_CLOSING_BALANCE;
            Cursor cursor = database.rawQuery(selectQuery, null);
            Log.e("selectQuery", "" + selectQuery);
            Log.e("CursorSize", "" + cursor.getCount());

            cursor.moveToFirst();

            for (int i = 0; i < cursor.getCount(); i++) {
                String code = cursor.getString(cursor.getColumnIndex("code"));
                String displayName = cursor.getString(cursor.getColumnIndex("name"));
                double closingbalance = cursor.getDouble(cursor.getColumnIndex("closing_balance"));
                double productPrice = cursor.getDouble(cursor.getColumnIndex("product_price"));

                dtoList.add(new Product(code, displayName, productPrice, closingbalance));
                cursor.moveToNext();

            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("closing balance List ",""+dtoList.size());
        return dtoList;

    }

    public void updateClosingbalance(List<Product> products) {
        for (Product product : products) {
            if (product.getQuantityEntered() !=null && product.getQuantityEntered() > 0.0) {
                double newClosingBalance =product.getClosingBalance() -product.getQuantityEntered();
                Log.e("FPSDBhelper","old Closing balance : "+product.getClosingBalance() + "Quantity Entered : "+product.getQuantityEntered());
                Log.e("FPSDBhelper","new Closing balance : "+newClosingBalance);
                Log.e("FPSDBhelper","Product Code : "+product.getCode());
                ContentValues values = new ContentValues();
                values.put(FPSDBConstants.KEY_CLSOING_BALANCE, newClosingBalance);
                String productCode=product.getCode();

                if(product.getCode().equals(RICE_CODE)){
                    String cardType = EntitlementResponse.getInstance().getRcAuthResponse().getCommBDetails().getTypeId();
                    if (AFSC_CARD_TYPE.equals(cardType)) {
                        productCode=RICEAFSC_CODE;
                    } else if (FSC_CARD_TYPE.equals(cardType)) {
                        productCode=RICEFSC_CODE;
                    } else if (AAP_CARD_TYPE.equals(cardType)) {
                        productCode=RICEAAP_CODE;
                    }
                }

                database.update(FPSDBTables.TABLE_TG_CLOSING_BALANCE, values, "code = " + productCode, null);
            }
        }
    }

    public boolean InsetClosingBalance(List<Product> productList) {
        boolean isSuccessFullyInserted = false;
        if (!productList.isEmpty()) {
            Log.e("DBHELPER","Start Total List Size : "+productList.toString());
            database.beginTransaction();
            try {
                for (Product productDto : productList) {
                    ContentValues values = new ContentValues();
                    values.put(FPSDBConstants.KEY_CODE, productDto.getCode());
                    values.put(FPSDBConstants.KEY_NAME, productDto.getDisplayName());
                    values.put(FPSDBConstants.KEY_CLSOING_BALANCE, productDto.getClosingBalance());
                    values.put(FPSDBConstants.KEY_TG_PRODUCT_PRICE,productDto.getProductPrice());
                    database.insertWithOnConflict(FPSDBTables.TABLE_TG_CLOSING_BALANCE, FPSDBConstants.KEY_CODE, values, SQLiteDatabase.CONFLICT_REPLACE);

                }
                isSuccessFullyInserted = true;
                database.setTransactionSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
                isSuccessFullyInserted = false;
            }finally {
                database.endTransaction();
            }
        }
        return isSuccessFullyInserted;
    }
}
/**
 * public String getProductNameTamil(long _id) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * return null;
 * } else
 * return new ProductDto(cursor).getLocalProductName();
 * }
 * <p/>
 * public String getProductunitTamil(long _id) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PRODUCTS + " where " + KEY_ID + "=" + _id;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * return null;
 * } else
 * return new ProductDto(cursor).getLocalProductUnit();
 * }
 * <p/>
 * public int readManualStockInward(long inwardKey) {
 * int retryCount = 0;
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_MANUAL_STOCK_INWARD + " where " + KEY_ID + "=" + inwardKey;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() > 0) {
 * retryCount = cursor.getInt(cursor.getColumnIndex(FPSDBConstants.KEY_FPS_STOCK_INWARD_RETRY_COUNT));
 * }
 * cursor.close();
 * return retryCount;
 * }
 * <p/>
 * <p/>
 * //role Feature AddList
 * public void insertRoleFeature(long userId, long featureId, Set<AppfeatureDto> roleFeature) {
 * ContentValues values = new ContentValues();
 * try {
 * for (AppfeatureDto features : roleFeature) {
 * values.put(FPSDBConstants.KEY_ROLE_USERID, userId);
 * values.put(FPSDBConstants.KEY_ROLE_FEATUREID, featureId);
 * values.put("role_id", features.getFeatureId());
 * values.put(FPSDBConstants.KEY_ROLE_NAME, features.getFeatureName());
 * values.put(FPSDBConstants.KEY_ROLE_PARENTID, features.getParentId());
 * values.put(FPSDBConstants.KEY_ROLE_TYPE, features.getName());
 * database.insert(FPSDBTables.TABLE_ROLE_FEATURE, null, values);
 * }
 * } catch (Exception e) {
 * Log.e("roleFeature", e.toString(), e);
 * }
 * }
 * <p/>
 * //roleFeature retrieveRolesData
 * public List<RoleFeatureDto> retrieveRolesData(long userId) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_ROLE_FEATURE + " WHERE " + FPSDBConstants.KEY_ROLE_PARENTID + "= 0 AND isDeleted = 1 AND " + FPSDBConstants.KEY_ROLE_USERID + "= " + userId
 * + " order by " + FPSDBConstants.KEY_ROLE_FEATUREID;
 * List<RoleFeatureDto> roleFeature = new ArrayList<>();
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * roleFeature.add(new RoleFeatureDto(cursor));
 * cursor.moveToNext();
 * }
 * cursor.close();
 * return roleFeature;
 * }
 * <p/>
 * //This function retrieve error description from language table
 * public List<FpsIntentReqProdDto> retrieveIntentRequest() {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_FPS_INTENT_REQUEST_PRODUCT;
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * List<FpsIntentReqProdDto> list = new ArrayList<>();
 * FPSIndentRequestDto fpsIndentRequestDto = new FPSIndentRequestDto();
 * FpsIntentReqProdDto fpsIntentReqProdDto = new FpsIntentReqProdDto();
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * fpsIntentReqProdDto = new FpsIntentReqProdDto(cursor);
 * cursor.moveToNext();
 * list.add(fpsIntentReqProdDto);
 * }
 * cursor.close();
 * return list;
 * }
 * public int retrieveAllBeneficiaryCount() {
 * Cursor cursor = null;
 * try {
 * String selectQuery = "SELECT count(*) as count FROM beneficiary";
 * SQLiteDatabase db = this.getReadableDatabase();
 * cursor = db.rawQuery(selectQuery, null);
 * int beneCount = 0;
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * beneficiaryDto.add(new BeneficiarySearchDto(cursor));
 * cursor.moveToNext();
 * }
 * cursor.close();
 * return beneficiaryDto;
 * }catch (Exception e){
 * return  0;
 * }finally {
 * if(cursor!=null){
 * cursor.close();
 * }
 * }
 * }
 * public List<OpeningClosingBalanceDto> showOpeningCloseBalanceProductDetails(String date) {
 * List<OpeningClosingBalanceDto> openingBalanceClosingBalanceDtoList = new ArrayList<OpeningClosingBalanceDto>();
 * <p/>
 * List<BillItemDto> billItemDtoList = getAllBillItemsListToday(date);
 * for (BillItemDto billItemDto : billItemDtoList) {
 * OpeningClosingBalanceDto openingClosingBalanceDto = new OpeningClosingBalanceDto();
 * String productName = getProductName(billItemDto.getProductId());
 * openingClosingBalanceDto.setQuantity(billItemDto.getQuantity());
 * openingClosingBalanceDto.setName(productName);
 * openingBalanceClosingBalanceDtoList.add(openingClosingBalanceDto);
 * <p/>
 * }
 * return openingBalanceClosingBalanceDtoList;
 * }
 * <p/>
 * <p/>
 * //Get Beneficiary data by QR Code
 * public BeneficiaryDto beneficiaryFromMobile(String mobile) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_OLD_RATION + "='"
 * + mobile + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * return null;
 * } else {
 * BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
 * beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(beneficiary.getUfc()));
 * return beneficiary;
 * }
 * }
 * /*  //This function inserts details to FPSDBTables.TABLE_BILL_ITEM,;
 * private void insertOfflineBillItems(Set<BillItemDto> billItem, int month, String oldRationCard, String billDate, String transactionId) {
 * ContentValues values = new ContentValues();
 * List<BillItemDto> billList = new ArrayList<BillItemDto>(billItem);
 * for (BillItemDto billItems : billList) {
 * values.put(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID, billItems.getProductId());
 * NumberFormat formatter = new DecimalFormat("#0.000");
 * values.put(FPSDBConstants.KEY_BILL_ITEM_QUANTITY, billItems.getQuantity());
 * values.put("totalCost", formatter.format(billItems.getCost() * billItems.getQuantity()));
 * values.put(FPSDBConstants.KEY_BILL_ITEM_COST, formatter.format(billItems.getCost()));
 * values.put(FPSDBConstants.KEY_BILL_TRANSACTION_ID, transactionId);
 * values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, month);
 * values.put("old_ration_card_num", oldRationCard);
 * values.put(FPSDBConstants.KEY_BILL_ITEM_DATE, billDate);
 * database.insert(FPSDBTables.TABLE_BILL_OFFLINE_ITEM, null, values);
 * <p/>
 * }
 * //Bill for background sync
 * public List<BillDto> getAllBillsForOfflineSync() {
 * List<BillDto> bills = new ArrayList<BillDto>();
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_OFFLINE_BILL;// + " where " + FPSDBConstants.KEY_BILL_STATUS + "<>'T'";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BillDto bill = new BillDto(cursor);
 * bill.setBillItemDto(getBillItems(bill.getTransactionId()));
 * bills.add(bill);
 * Log.i("bills", bill.toString());
 * cursor.moveToNext();
 * }
 * return bills;
 * }
 * <p/>
 * public boolean getCloseSalseStatus(String date) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_CLOSE_SALE + " where created_date like '" + date + "%'";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * return cursor.getCount() > 0;
 * }
 * //Update the stock
 * public void registrationUpdate(String  stock) {
 * ContentValues values = new ContentValues();
 * NumberFormat formatter = new DecimalFormat("#0.000");
 * values.put(FPSDBConstants.KEY_STOCK_QUANTITY, formatter.format(stock.getQuantity()));
 * database.update(FPSDBTables.TABLE_STOCK, values, FPSDBConstants.KEY_STOCK_PRODUCT_ID + "=" + stock.getProductId(), null);
 * }
 * /*  public boolean insertOfflineBill(BillDto bill) {
 * try {
 * ContentValues values = new ContentValues();
 * values.put(FPSDBConstants.KEY_BILL_SERVER_ID, bill.getId());
 * values.put(FPSDBConstants.KEY_BILL_FPS_ID, bill.getFpsId());
 * values.put(FPSDBConstants.KEY_BILL_CREATED_BY, bill.getCreatedby());
 * values.put(FPSDBConstants.KEY_BILL_AMOUNT, bill.getAmount());
 * values.put(FPSDBConstants.KEY_BILL_MODE, String.valueOf(bill.getMode()));
 * values.put(FPSDBConstants.KEY_BILL_CHANNEL, String.valueOf(bill.getChannel()));
 * SimpleDateFormat billDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
 * values.put(FPSDBConstants.KEY_BILL_DATE, billDate.format(new Date()));
 * values.put(FPSDBConstants.KEY_BILL_CREATED_DATE, new Date().getTime());
 * values.put(FPSDBConstants.KEY_BILL_TIME_MONTH, new DateTime().getMonthOfYear());
 * values.put(FPSDBConstants.KEY_BILL_BENEFICIARY, bill.getBeneficiaryId());
 * values.put(FPSDBConstants.KEY_BILL_TRANSACTION_ID, bill.getTransactionId());
 * values.put(FPSDBConstants.KEY_BILL_STATUS, "R");
 * values.put(FPSDBConstants.KEY_BENEFICIARY_UFC, bill.getUfc());
 * values.put("otpTime", bill.getOtpTime());
 * values.put("otpId", bill.getOtpId());
 * if (!database.isOpen()) {
 * database = dbHelper.getWritableDatabase();
 * }
 * database.insertWithOnConflict(FPSDBTables.TABLE_OFFLINE_BILL, FPSDBConstants.KEY_BILL_TRANSACTION_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
 * insertOfflineBillItems(bill.getBillItemDto(), new DateTime().getMonthOfYear(), bill.getUfc(), bill.getBillDate(), bill.getTransactionId());
 * return true;
 * } catch (Exception e) {
 * Util.LoggingQueue(contextValue, "Offline Bill", e.toString());
 * Log.e("Offline Bill", e.toString(), e);
 * return false;
 * }
 * //Get Beneficiary data by QR Code
 * public BillDto lastGenBill() {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BILL + " order by " + FPSDBConstants.KEY_BILL_TRANSACTION_ID + " DESC limit 1";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * return null;
 * } else {
 * BillDto beneficiary = new BillDto(cursor);
 * return beneficiary;
 * }
 * }
 * }
 * <p/>
 * <p/>
 * <p/>
 * //Get Beneficiary data by QR Code
 * public List<BillItemDto> billItemsDetails(long qrCode, int month, long productId) {
 * List<BillItemDto> billItems = new ArrayList<BillItemDto>();
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BILL_ITEM + " where " + FPSDBConstants.KEY_BILL_TIME_MONTH + " = " + month + " AND "
 * + FPSDBConstants.KEY_BILL_BENEFICIARY + "=" + qrCode + " AND " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + " = " + productId
 * + " order by " + FPSDBConstants.KEY_BILL_TRANSACTION_ID;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BillItemDto billItemDto = new BillItemDto(cursor);
 * billItems.add(billItemDto);
 * cursor.moveToNext();
 * }
 * return billItems;
 * }
 * <p/>
 * //Get Beneficiary data by QR Code
 * public List<PersonBasedRule> personRulesDetails() {
 * List<PersonBasedRule> masterRulesDto = new ArrayList<PersonBasedRule>();
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_PERSON_RULES;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * PersonBasedRule masterRules = new PersonBasedRule(cursor);
 * masterRulesDto.add(masterRules);
 * cursor.moveToNext();
 * }
 * return masterRulesDto;
 * }
 * <p/>
 * <p/>
 * //Get Beneficiary data by QR Code
 * public BeneficiaryDto beneficiaryDetailsByCode(String qrCode) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_UFC + "='" + qrCode + "' AND "
 * + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * return new BeneficiaryDto();
 * } else {
 * BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
 * beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(beneficiary.getUfc()));
 * return beneficiary;
 * }
 * }
 * <p/>
 * /* public List<BeneficiarySearchDto> getCardDetails() {
 * List<BeneficiarySearchDto> benficiary = new ArrayList<BeneficiarySearchDto>();
 * try {
 * String selectQuery = "select a.old_ration_card_num as old_ration_card_num,a.aRegister as aRegister,a.num_of_child as num_of_child,a.num_of_adults as num_of_adults,a.num_of_cylinder as num_of_cylinder,b.description as description from beneficiary a inner join card_type b on a.card_type_id = b._id " +
 * "union select a.old_ration_card_num as old_ration_card_num,a.aRegister as aRegister,a.num_of_child as num_of_child,a.num_of_adults as num_of_adults,a.num_of_cylinder as num_of_cylinder,b.description as description from offline_activation a inner join card_type b on a.card_type_id=b.type order by aRegister";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BeneficiarySearchDto searchData = new BeneficiarySearchDto(cursor);
 * benficiary.add(searchData);
 * cursor.moveToNext();
 * }
 * } catch (Exception e) {
 * Log.e("Card Details", e.toString(), e);
 * }
 * return benficiary;
 * }
 * //Get Beneficiary data by QR Code
 * public List<BenefActivNewDto> allBeneficiaryDetails() {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_REGISTRATION + " where " + FPSDBConstants.KEY_REGISTRATION_STATUS + " = 'S' AND "
 * + FPSDBConstants.KEY_REGISTRATION_IS_ACTIVATED + " = 0";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * List<BenefActivNewDto> beneficiary = new ArrayList<BenefActivNewDto>();
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BenefActivNewDto beneficiaryDto = new BenefActivNewDto(cursor);
 * beneficiaryDto.setActivationType("PENDING_REGISTRATION");
 * beneficiary.add(beneficiaryDto);
 * cursor.moveToNext();
 * }
 * return beneficiary;
 * <p/>
 * <p/>
 * }
 * <p/>
 * //Get Beneficiary data by QR Code
 * public List<BenefActivNewDto> allBeneficiaryDetailsRegistered(String dateRegistered, String oldRation, String mobileNum) {
 * String selectQuery = "SELECT * FROM beneficiary_registration where requested_time like '%" + dateRegistered + "%'   and old_ration_card_num like '%"
 * + oldRation + "%'  and mob_num like '%" + mobileNum + "%'";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * List<BenefActivNewDto> beneficiary = new ArrayList<BenefActivNewDto>();
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BenefActivNewDto beneficiaryDto = new BenefActivNewDto(cursor);
 * beneficiaryDto.setActivationType("PENDING_REGISTRATION");
 * beneficiary.add(beneficiaryDto);
 * cursor.moveToNext();
 * }
 * return beneficiary;
 * <p/>
 * <p/>
 * }
 * <p/>
 * //Get Product data
 * public List<EntitlementMasterRuleDtod> getAllEntitlementMasterRule(long cardType) {//isDeletedgroupId
 * String selectQuery = "SELECT  * FROM entitlement_rules1"; //+ FPSDBTables.TABLE_ENTITLEMENT_RULES;// + " where " + FPSDBConstants.KEY_RULES_CARD_TYPE + " = " + cardType + " AND isDeleted = 0";
 * List<EntitlementMasterRule> products = new ArrayList<EntitlementMasterRule>();
 * Log.e("selectQuery", selectQuery);
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * <p/>
 * <p/>
 * Log.e("Error in Count", "Cursor count:" + cursor.getCount());
 * cursor.moveToFirst();
 * Log.e("Error in Count", "Cursor count:" + cursor.getCount());
 * /* for (int i = 0; i < cursor.getCount(); i++) {
 * products.add(new EntitlementMasterRule(cursor));
 * cursor.moveToNext();
 * }
 * return new ArrayList<>();
 * }
 * // Used to retrieve beneficiary details
 * public boolean retrieveMobileNoBeneficiary(String mobileNo) {
 * String selectQuery = "SELECT distinct(mobile) FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_MOBILE + "='" + mobileNo + "'" +
 * " union select distinct(mobile) FROM " + FPSDBTables.TABLE_OFFLINE_ACTIVATION + " where " + FPSDBConstants.KEY_BENEFICIARY_MOBILE + "='" + mobileNo + "'";
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * boolean isExists = false;
 * if (cursor.getCount() > 0) {
 * isExists = true;
 * }
 * cursor.close();
 * return isExists;
 * }
 * }
 * <p/>
 * <p/>
 * //This function inserts details to FPSDBTables.TABLE_USERS,
 * public void insertLpgProviderDetails(Set<ServiceProviderDto> lpgProviderDto) {
 * <p/>
 * ContentValues values = new ContentValues();
 * List<ServiceProviderDto> lpgDetailList = new ArrayList<ServiceProviderDto>(lpgProviderDto);
 * if (!lpgDetailList.isEmpty()) {
 * for (ServiceProviderDto lpgDetail : lpgDetailList) {
 * values.put(KEY_ID, lpgDetail.getId());
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_NAME, lpgDetail.getProviderName());
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_CREATEDBY, lpgDetail.getCreatedBy());
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_CREATEDDATE, lpgDetail.getCreatedDate());
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_MODIFIEDDATE, lpgDetail.getModifiedDate());
 * if (lpgDetail.isStatus()) {
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_STATUS, 0);
 * } else {
 * values.put(FPSDBConstants.KEY_LPG_PROVIDER_STATUS, 1);
 * }
 * database.insertWithOnConflict(FPSDBTables.TABLE_LPG_PROVIDER, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
 * <p/>
 * }
 * }
 * }
 * <p/>
 * public List<BillDto> getAllBillDetailsMonth(int limit, int count) {
 * List<BillDto> bill = new ArrayList<BillDto>();
 * String selectQuery = "SELECT * FROM " + FPSDBTables.TABLE_BILL + " ORDER BY " + FPSDBConstants.KEY_BILL_TRANSACTION_ID + " DESC limit " + (count * limit) + "," + limit;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BillDto billDto = new BillDto(cursor);
 * bill.add(billDto);
 * cursor.moveToNext();
 * }
 * return bill;
 * }
 * <p/>
 * //Get Beneficiary data by QR Code
 * public BeneficiaryDto beneficiaryDetails(String qrCode) {
 * String selectQuery = "SELECT  * FROM " + FPSDBTables.TABLE_BENEFICIARY + " where " + FPSDBConstants.KEY_BENEFICIARY_MOBILE + " ='" + qrCode
 * + "' AND " + FPSDBConstants.KEY_BENEFICIARY_ACTIVE + "=1";
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() == 0) {
 * cursor.close();
 * return null;
 * } else {
 * BeneficiaryDto beneficiary = new BeneficiaryDto(cursor);
 * beneficiary.setBenefMembersDto(getAllBeneficiaryMembers(beneficiary.getUfc()));
 * cursor.close();
 * return beneficiary;
 * }
 * }
 * //Bill for background sync
 * public List<BillItemDto> getAllBillItemsListToday(String toDate) {
 * List<BillItemDto> bills = new ArrayList<>();
 * String selectQuery = "SELECT  " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID + ",SUM(quantity) as total FROM " + FPSDBTables.TABLE_BILL_ITEM
 * + " where " + FPSDBConstants.KEY_BILL_ITEM_DATE + " LIKE '" + toDate + " %' group by " + FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID;
 * Cursor cursor = database.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * for (int i = 0; i < cursor.getCount(); i++) {
 * BillItemDto billItemDto = new BillItemDto();
 * billItemDto.setProductId(cursor.getLong(cursor.getColumnIndex(FPSDBConstants.KEY_BILL_ITEM_PRODUCT_ID)));
 * billItemDto.setQuantity(cursor.getDouble(cursor.getColumnIndex("total")));
 * bills.add(billItemDto);
 * cursor.moveToNext();
 * }
 * cursor.close();
 * return bills;
 * }
 * //Update Stock Inward
 * public void updateStockInwardDeclined(long inwardId) {
 * ContentValues values = new ContentValues();
 * values.put("status", "D");
 * database.update(FPSDBTables.TABLE_FPS_MANUAL_STOCK_INWARD, values, KEY_ID + "=" + inwardId, null);
 * }
 * public List<EntitlementMasterRule> getValuesForBene() throws Exception {
 * String selectQuery = "SELECT * FROM entitlement_rules1";
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * List<EntitlementMasterRule> beneficiaryDtos = new ArrayList<>();
 * Log.i("Cursor", "Cursor Count:" + cursor.getCount());
 * for (int i = 0; i < cursor.getCount(); i++) {
 * beneficiaryDtos.add(new EntitlementMasterRule(cursor));
 * cursor.moveToNext();
 * }
 * cursor.close();
 * return beneficiaryDtos;
 * }
 * <p/>
 * public List<PersonBasedRule> getValuesForPerson() throws Exception {
 * String selectQuery = "SELECT * FROM person_rules1";
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * List<PersonBasedRule> beneficiaryDtos = new ArrayList<>();
 * Log.i("Cursor", "Cursor Count:" + cursor.getCount());
 * for (int i = 0; i < cursor.getCount(); i++) {
 * beneficiaryDtos.add(new PersonBasedRule(cursor));
 * cursor.moveToNext();
 * }
 * cursor.close();
 * return beneficiaryDtos;
 * }
 * <p/>
 * public boolean isTableExists() {
 * String selectQuery = "SELECT * FROM sqlite_master WHERE name ='entitlement_rules1' and type='table'";
 * SQLiteDatabase db = this.getReadableDatabase();
 * Cursor cursor = db.rawQuery(selectQuery, null);
 * cursor.moveToFirst();
 * if (cursor.getCount() > 0) {
 * return true;
 * }
 * cursor.close();
 * return false;
 * }
 * <p/>
 * public void dropTableExists() {
 * SQLiteDatabase db = this.getReadableDatabase();
 * db.execSQL("drop table entitlement_rules1");
 * db.execSQL("drop table person_rules1");
 * }
 */
