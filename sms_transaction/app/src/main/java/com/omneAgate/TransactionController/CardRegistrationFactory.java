package com.omneagate.TransactionController;

/**
 * Created by Rajesh on 4/8/2015.
 */
public class CardRegistrationFactory {
    public static CardRegistration getTransaction(int connectionStatus) {

        switch (connectionStatus) {
            case 0:
                return new SmsRegistrationConnector();
            default:
                return new SmsRegistrationConnector();
        }
    }
}
