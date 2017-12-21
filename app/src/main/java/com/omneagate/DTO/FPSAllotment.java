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
public class FPSAllotment {

	private String respMessage;
	private String respMsgCode;
	private String district;
	private String shopNo;
	private String transactionId;

	private List<Product> fpsAllotmentProductList = null;

	private List<Product> fpsProductPriceList = null;

	/**
	 *
	 */
	public FPSAllotment() {
		fpsAllotmentProductList = new ArrayList<Product>();
		fpsProductPriceList = new ArrayList<Product>();
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

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}


	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public List<Product> getFpsAllotmentProductList() {
		return fpsAllotmentProductList;
	}

	public void setFpsAllotmentProductList(List<Product> fpsProductList) {
		this.fpsAllotmentProductList = fpsProductList;
	}

	public List<Product> getFpsProductPriceList() {
		return fpsProductPriceList;
	}

	public void setFpsProductPriceList(List<Product> fpsProductPriceList) {
		this.fpsProductPriceList = fpsProductPriceList;
	}

}
