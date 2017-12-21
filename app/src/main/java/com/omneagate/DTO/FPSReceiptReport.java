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
public class FPSReceiptReport {

	private String respMessage;
	private String respMsgCode;
	private String transDate;
	private Double totalAmt;
	private String transRoNo;

	private List<Product> productList = null;

	/**
	 * 
	 */
	public FPSReceiptReport() {
		this.productList = new ArrayList<Product>();

	}

	/**
	 * @return the respMessage
	 */
	public String getRespMessage() {
		return respMessage;
	}

	/**
	 * @param respMessage
	 *            the respMessage to set
	 */
	public void setRespMessage(String respMessage) {
		this.respMessage = respMessage;
	}

	/**
	 * @return the respMsgCode
	 */
	public String getRespMsgCode() {
		return respMsgCode;
	}

	/**
	 * @param respMsgCode
	 *            the respMsgCode to set
	 */
	public void setRespMsgCode(String respMsgCode) {
		this.respMsgCode = respMsgCode;
	}

	/**
	 * @return the productList
	 */
	public List<Product> getProductList() {
		return productList;
	}

	/**
	 * @param productList
	 *            the productList to set
	 */
	public void setProductList(List<Product> productList) {
		this.productList = productList;
	}

	/**
	 * @return the transDate
	 */
	public String getTransDate() {
		return transDate;
	}

	/**
	 * @param transDate
	 *            the transDate to set
	 */
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	/**
	 * @return the totalAmt
	 */
	public Double getTotalAmt() {
		return totalAmt;
	}

	/**
	 * @param totalAmt
	 *            the totalAmt to set
	 */
	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	/**
	 * @return the transRoNo
	 */
	public String getTransRoNo() {
		return transRoNo;
	}

	/**
	 * @param transRoNo
	 *            the transRoNo to set
	 */
	public void setTransRoNo(String transRoNo) {
		this.transRoNo = transRoNo;
	}

}
