/**
 * 
 */
package com.omneagate.DTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ftuser
 *
 */
public class RCAuthResponse  implements Serializable{

	private String existingRCNo;
	private String shopNo;
	private String typeId;
	//
	private String respMessage;
	private String respMsgCode;

	private CommBDetails commBDetails = null;

	private List<Product> itemsAllotedList = null;
	//private List<Product> itemsBalanceList = null;

	/**
	 * 
	 */
	public RCAuthResponse() {
		itemsAllotedList = new ArrayList<Product>();
		//itemsBalanceList = new ArrayList<Product>();
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

	public String getExistingRCNo() {
		return existingRCNo;
	}

	public void setExistingRCNo(String existingRCNo) {
		this.existingRCNo = existingRCNo;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public CommBDetails getCommBDetails() {
		return commBDetails;
	}

	public void setCommBDetails(CommBDetails commBDetails) {
		this.commBDetails = commBDetails;
	}

	public List<Product> getItemsAllotedList() {
		return itemsAllotedList;
	}

	public void setItemsAllotedList(List<Product> itemsAllotedList) {
		this.itemsAllotedList = itemsAllotedList;
	}

//	public List<Product> getItemsBalanceList() {
//		return itemsBalanceList;
//	}
//
//	public void setItemsBalanceList(List<Product> itemsBalanceList) {
//		this.itemsBalanceList = itemsBalanceList;
//	}

}
