package com.omneagate.Util;

import android.content.Context;

import com.omneagate.activity.R;

import org.jasypt.digest.PooledStringDigester;
import org.jasypt.digest.StringDigester;

/**
 * Created by user1 on 12/2/15.
 * password hash settings page
 */
public class StringDigesterString {

    /**
     * Returns the stringDigester variable by the given constant values
     *
     * @param context
     */
    public static StringDigester getPasswordHash(Context context) {
        final PooledStringDigester stringDigester = new PooledStringDigester();
        stringDigester.setAlgorithm(context.getString(R.string.hashMethod));
        Integer iteration = Integer.parseInt(context.getString(R.string.hashIterations));
        stringDigester.setIterations(iteration);
        Integer saltSize = Integer.parseInt(context.getString(R.string.saltSize));
        stringDigester.setSaltSizeBytes(saltSize);
        Integer poolSize = Integer.parseInt(context.getString(R.string.poolSize));
        stringDigester.setPoolSize(poolSize);
        stringDigester.initialize();
        return stringDigester;
    }
}
