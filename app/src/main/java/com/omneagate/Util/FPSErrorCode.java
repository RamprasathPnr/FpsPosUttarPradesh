/**
 *
 */
package com.omneagate.Util;



import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ftuser
 */
public class FPSErrorCode {
    static GlobalAppState getGlobalAppState = GlobalAppState.getInstance();

    private static Map<String, String> errorList = null;

    static {
        errorList = new HashMap<String, String>();
        //errorList.put("-101", "Invalid District Code");
        errorList.put("-101", getGlobalAppState.getResources().getString(R.string.invalid_district));
        errorList.put("-103", getGlobalAppState.getResources().getString(R.string.invalid_shopno));
        errorList.put("-104", getGlobalAppState.getResources().getString(R.string.invalid_device_id));
        errorList.put("-105", getGlobalAppState.getResources().getString(R.string.invalid_password));
        errorList.put("-107", getGlobalAppState.getResources().getString(R.string.ePos_version_mismatched));
        errorList.put("-108", getGlobalAppState.getResources().getString(R.string.invalid));
        errorList.put("-109", getGlobalAppState.getResources().getString(R.string.invalid_username));
        errorList.put("-110", getGlobalAppState.getResources().getString(R.string.shop_nomapped));
        //
        errorList.put("-112", getGlobalAppState.getResources().getString(R.string.invalid_trans_id));

        errorList.put("-113", getGlobalAppState.getResources().getString(R.string.invalid_month));
        errorList.put("-114", getGlobalAppState.getResources().getString(R.string.invalid_year));
        errorList.put("-115", getGlobalAppState.getResources().getString(R.string.invalid_aadhaar_num));
        errorList.put("-116", getGlobalAppState.getResources().getString(R.string.invalid_aadhaar_Fromat));
        errorList.put("-117", getGlobalAppState.getResources().getString(R.string.invalid_authentication));
        errorList.put("-118", getGlobalAppState.getResources().getString(R.string.webservice_timeout));
        errorList.put("-120", getGlobalAppState.getResources().getString(R.string.invaid_AUA_type));
        errorList.put("-121", getGlobalAppState.getResources().getString(R.string.closing_balances_are_no_entered));
        errorList.put("-122", getGlobalAppState.getResources().getString(R.string.closing_balances_are_no_entered));
        errorList.put("-123", getGlobalAppState.getResources().getString(R.string.ration_not_belongs_to_the_shop));
        //
        errorList.put("-125", getGlobalAppState.getResources().getString(R.string.ration_not_exist));
        errorList.put("-126", getGlobalAppState.getResources().getString(R.string.iinvalid_ration_no));
        errorList.put("-127", getGlobalAppState.getResources().getString(R.string.error_ration));
        //Error/Ration Card doesn't belongs to this shop.
        errorList.put("-129", getGlobalAppState.getResources().getString(R.string.commodity_not_alloted_rationcard));
        errorList.put("-130", getGlobalAppState.getResources().getString(R.string.data_not_found));
        errorList.put("-131", getGlobalAppState.getResources().getString(R.string.invalid_AFSC));
        errorList.put("-132", getGlobalAppState.getResources().getString(R.string.invalid_FSC));
        errorList.put("-133", getGlobalAppState.getResources().getString(R.string.invalid_AAP));
        errorList.put("-134", getGlobalAppState.getResources().getString(R.string.invalid_WHEAT));
        errorList.put("-135", getGlobalAppState.getResources().getString(R.string.invalid_sugar));
        errorList.put("-136", getGlobalAppState.getResources().getString(R.string.invalid_atta));
        errorList.put("-137", getGlobalAppState.getResources().getString(R.string.invalid_rgdal));
        errorList.put("-138", getGlobalAppState.getResources().getString(R.string.invalid_koil));
        errorList.put("-139", getGlobalAppState.getResources().getString(R.string.invalid_tamarind));
        errorList.put("-140", getGlobalAppState.getResources().getString(R.string.invalid_poil));
        errorList.put("-141", getGlobalAppState.getResources().getString(R.string.invalid_salt));
        errorList.put("-142", getGlobalAppState.getResources().getString(R.string.invalid_chilli));
        errorList.put("-143", getGlobalAppState.getResources().getString(R.string.invalid_tumaric));
        errorList.put("-144", getGlobalAppState.getResources().getString(R.string.invalid_trans_amount));
        //
        errorList.put("-145", getGlobalAppState.getResources().getString(R.string.allocation_AFSC));
        errorList.put("-146", getGlobalAppState.getResources().getString(R.string.allocation_FSC));
        errorList.put("-147", getGlobalAppState.getResources().getString(R.string.allocation_AAP));
        errorList.put("-148", getGlobalAppState.getResources().getString(R.string.allocation_sugar));
        errorList.put("-149", getGlobalAppState.getResources().getString(R.string.allocation_wheat));
        errorList.put("-150", getGlobalAppState.getResources().getString(R.string.allocation_atta));
        errorList.put("-152", getGlobalAppState.getResources().getString(R.string.allocation_kerosene));
        errorList.put("-151", getGlobalAppState.getResources().getString(R.string.allocation_poil));
        errorList.put("-153", getGlobalAppState.getResources().getString(R.string.allocation_chilli));
        errorList.put("-154", getGlobalAppState.getResources().getString(R.string.allocation_tamarind));
        errorList.put("-155", getGlobalAppState.getResources().getString(R.string.allocation_Turmeric));
        errorList.put("-156", getGlobalAppState.getResources().getString(R.string.allocation_Salt));
        errorList.put("-157", getGlobalAppState.getResources().getString(R.string.allocation_Rgdal));
        errorList.put("-158", getGlobalAppState.getResources().getString(R.string.total_amount_not_matched));
        errorList.put("-159", getGlobalAppState.getResources().getString(R.string.ration_card_no_inserted));
        //
        errorList.put("-160", getGlobalAppState.getResources().getString(R.string.data_not_posted));
        errorList.put("-165", getGlobalAppState.getResources().getString(R.string.ro_detail_not_available));
        errorList.put("-166", getGlobalAppState.getResources().getString(R.string.invalid_ro_number));
        errorList.put("01", getGlobalAppState.getResources().getString(R.string.no_bfd_found));
        errorList.put("-124", getGlobalAppState.getResources().getString(R.string.invalid_transaction));
        errorList.put("-178", getGlobalAppState.getResources().getString(R.string.invalid_shopno_error));
    }

    /**
     *
     */
    public FPSErrorCode() {
        // TODO Auto-generated constructor stub
    }

    public static String getErrorMessage(String errorCode) {
        String rtnMsg = errorList.get(errorCode);
        rtnMsg = rtnMsg == null ? errorCode : rtnMsg;
        return rtnMsg;
    }

}
