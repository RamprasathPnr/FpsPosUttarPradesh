/**
 *
 */
package com.omneagate.DTO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ftuser
 *
 */
public class DealerAuthResponse {

    private String respMessage;
    private String respMsgCode;
    private List<Product> itemsCBList = null;
    private List<Product> itemsPriceList = null;

    /**
     *
     */
    public DealerAuthResponse() {
        itemsCBList = new ArrayList<Product>();
        itemsPriceList = new ArrayList<Product>();
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

    public List<Product> getItemsCBList() {
        return itemsCBList;
    }

    public void setItemsCBList(List<Product> itemsCBList) {
        this.itemsCBList = itemsCBList;
    }

    public List<Product> getItemsPriceList() {
        return itemsPriceList;
    }

    public void setItemsPriceList(List<Product> itemsPriceList) {
        this.itemsPriceList = itemsPriceList;
    }

}
