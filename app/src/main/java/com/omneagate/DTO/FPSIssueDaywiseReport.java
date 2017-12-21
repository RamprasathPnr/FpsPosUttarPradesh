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
public class FPSIssueDaywiseReport {

	private String respMessage;
	private String respMsgCode;
	private String transactionDate;
	private Double totalAmt;
	private String noOfTransNo;

	private List<Product> productList = null;

	/**
	 * 
	 */
	public FPSIssueDaywiseReport() {
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
	 * @return the transactionDate
	 */
	public String getTransactionDate() {
		return transactionDate;
	}

	/**
	 * @param transactionDate
	 *            the transactionDate to set
	 */
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
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
	 * @return the noOfTransNo
	 */
	public String getNoOfTransNo() {
		return noOfTransNo;
	}

	/**
	 * @param noOfTransNo
	 *            the noOfTransNo to set
	 */
	public void setNoOfTransNo(String noOfTransNo) {
		this.noOfTransNo = noOfTransNo;
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

}
