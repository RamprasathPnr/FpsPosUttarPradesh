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
public class FPSIssueReport {

	private String respMessage;
	private String respMsgCode;
	private String rcNo;
	private Double totalAmt;
	private String transRoNo;

	private List<Product> productList = null;

	/**
	 *
	 */
	public FPSIssueReport() {
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
	 * @return the rcNo
	 */
	public String getRcNo() {
		return rcNo;
	}

	/**
	 * @param rcNo
	 *            the rcNo to set
	 */
	public void setRcNo(String rcNo) {
		this.rcNo = rcNo;
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
