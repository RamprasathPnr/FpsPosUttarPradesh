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
public class RCLastTransaction implements Serializable {

	private String distCode;
	private String shopNo;
	private String rationCard;
	private String memberId;
	private String uidNo;
	private String respMessage;
	private String respMsgCode;
	private String transactionId;
	private String transDate;

	private Double totalAmt;
	private String currYear;
	private String currMonth;

	private List<Product> productList = null;

	/**
	 *
	 */
	public RCLastTransaction() {
		this.productList = new ArrayList<Product>();
	}

	/**
	 * @return the distCode
	 */
	public String getDistCode() {
		return distCode;
	}

	/**
	 * @param distCode
	 *            the distCode to set
	 */
	public void setDistCode(String distCode) {
		this.distCode = distCode;
	}

	/**
	 * @return the shopNo
	 */
	public String getShopNo() {
		return shopNo;
	}

	/**
	 * @param shopNo
	 *            the shopNo to set
	 */
	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	/**
	 * @return the rationCard
	 */
	public String getRationCard() {
		return rationCard;
	}

	/**
	 * @param rationCard
	 *            the rationCard to set
	 */
	public void setRationCard(String rationCard) {
		this.rationCard = rationCard;
	}

	/**
	 * @return the memberId
	 */
	public String getMemberId() {
		return memberId;
	}

	/**
	 * @param memberId
	 *            the memberId to set
	 */
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	/**
	 * @return the uidNo
	 */
	public String getUidNo() {
		return uidNo;
	}

	/**
	 * @param uidNo
	 *            the uidNo to set
	 */
	public void setUidNo(String uidNo) {
		this.uidNo = uidNo;
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
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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
	 * @return the currYear
	 */
	public String getCurrYear() {
		return currYear;
	}

	/**
	 * @param currYear
	 *            the currYear to set
	 */
	public void setCurrYear(String currYear) {
		this.currYear = currYear;
	}

	/**
	 * @return the currMonth
	 */
	public String getCurrMonth() {
		return currMonth;
	}

	/**
	 * @param currMonth
	 *            the currMonth to set
	 */
	public void setCurrMonth(String currMonth) {
		this.currMonth = currMonth;
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
