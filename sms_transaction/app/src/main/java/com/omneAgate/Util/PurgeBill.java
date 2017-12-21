package com.omneagate.Util;

import android.app.Activity;
import android.util.Log;

/**
 * Created by user1 on 1/4/15.
 */

// Cancel Bill and Bill details for last N days transaction
public class PurgeBill {

    //Purge Bill
    public void purgeBillProcess(Activity context, int noOfDays) {
        try {
            String transmitted = "T";
            FPSDBHelper.getInstance(context).purgeBillBItemDetails(noOfDays, transmitted);
        } catch (Exception e) {
            Log.e("Exception", e.toString(), e);
        }
    }
}
