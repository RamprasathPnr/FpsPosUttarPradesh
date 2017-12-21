package com.omneagate.Util;

import lombok.Getter;
import lombok.Setter;

/**
 * SingleTon class for maintain the sessionId
 */
public class LocationId {
    private static com.omneagate.Util.LocationId mInstance = null;

    @Getter
    @Setter
    private String longitude;

    @Getter
    @Setter
    private String latitude;

    private LocationId() {
        longitude = "";
        latitude = "";
    }

    public static synchronized com.omneagate.Util.LocationId getInstance() {
        if (mInstance == null) {
            mInstance = new com.omneagate.Util.LocationId();
        }
        return mInstance;
    }

}
