package com.omneagate.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ftuser on 15/2/17.
 */
public class FPSDealerDetails implements Serializable {

    private String currMonth;
    private String currYear;
    private String currDateTime;

    private String respMessage;
    private String respMsgCode;
    private String transactionID;
    private String distCode;

    private List<FPSDealer> fpsDealerList = null;

    /**
     *
     */
    public FPSDealerDetails() {
        fpsDealerList = new ArrayList<FPSDealer>();
    }

    public String getCurrMonth() {
        return currMonth;
    }

    public void setCurrMonth(String currMonth) {
        this.currMonth = currMonth;
    }

    public String getCurrYear() {
        return currYear;
    }

    public void setCurrYear(String currYear) {
        this.currYear = currYear;
    }

    public String getDistCode() {
        return distCode;
    }

    public void setDistCode(String distCode) {
        this.distCode = distCode;
    }



    public String getCurrDateTime() {
        return currDateTime;
    }

    public void setCurrDateTime(String currDateTime) {
        this.currDateTime = currDateTime;
    }

    public String getRespMessage() {
        return respMessage;
    }

    public void setRespMessage(String respMessage) {
        this.respMessage = respMessage;
    }

    public String getRespMsgCode() {
        return respMsgCode;
    }

    public void setRespMsgCode(String respMsgCode) {
        this.respMsgCode = respMsgCode;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public List<FPSDealer> getFpsDealerList() {
        return fpsDealerList;
    }

    public void setFpsDealerList(List<FPSDealer> fpsDealerList) {
        this.fpsDealerList = fpsDealerList;
    }

}
