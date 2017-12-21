package com.omneagate.TransactionController;

/**
 * Created by Rajesh on 4/8/2015.
 */
public class TransactionFactory {
    public static Transaction getTransaction(int connectionStatus) {

        switch (connectionStatus) {
            case 0:
                return new SmsConnector();
            default:
                return new HttpConnector();
        }
    }
}
