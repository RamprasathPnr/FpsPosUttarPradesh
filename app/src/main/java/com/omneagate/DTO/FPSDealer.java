package com.omneagate.DTO;

import java.io.Serializable;

/**
 * Created by ftuser on 15/2/17.
 */
public class FPSDealer implements Serializable{

    private String bestFinger1;
    private String bestFinger2;
    private String bestFinger3;
    private String dealerOrNomine;
    private String dealerOrNomineUidNo;
    private String dealerType;
    private String ePoSVersion;
    private boolean SelectedItem;

    /**
     *
     */
    public FPSDealer() {
        // TODO Auto-generated constructor stub
    }


    public String getBestFinger1() {
        return bestFinger1;
    }

    public void setBestFinger1(String bestFinger1) {
        this.bestFinger1 = bestFinger1;
    }

    public String getBestFinger2() {
        return bestFinger2;
    }

    public void setBestFinger2(String bestFinger2) {
        this.bestFinger2 = bestFinger2;
    }

    public String getBestFinger3() {
        return bestFinger3;
    }

    public void setBestFinger3(String bestFinger3) {
        this.bestFinger3 = bestFinger3;
    }

    public String getDealerOrNomine() {
        return dealerOrNomine;
    }

    public void setDealerOrNomine(String dealerOrNomine) {
        this.dealerOrNomine = dealerOrNomine;
    }

    public String getDealerOrNomineUidNo() {
        return dealerOrNomineUidNo;
    }

    public void setDealerOrNomineUidNo(String dealerOrNomineUidNo) {
        this.dealerOrNomineUidNo = dealerOrNomineUidNo;
    }

    public String getDealerType() {
        return dealerType;
    }

    public void setDealerType(String dealerType) {
        this.dealerType = dealerType;
    }

    public String getePoSVersion() {
        return ePoSVersion;
    }

    public void setePoSVersion(String ePoSVersion) {
        this.ePoSVersion = ePoSVersion;
    }

    public boolean isSelectedItem() {
        return SelectedItem;
    }

    public void setSelectedItem(boolean selectedItem) {
        SelectedItem = selectedItem;
    }

}