package com.omneagate.Util;

import com.omneagate.DTO.FPSDealerDetails;
import com.omneagate.DTO.FPSRationCardDetails;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by root on 13/3/17.
 */
public class FpsMemberData {
    private static com.omneagate.Util.FpsMemberData mInstance = null;

    @Getter
    @Setter
    FPSDealerDetails fpsDealerDetails;

    @Getter
    @Setter
    FPSRationCardDetails fpsRationCardDetails;

    @Getter
    @Setter
    String rcNo;



    public static synchronized com.omneagate.Util.FpsMemberData getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.FpsMemberData();
        }
        return mInstance;
    }
}
