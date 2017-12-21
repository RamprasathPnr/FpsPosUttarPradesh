/**
 * 
 */
package com.omneagate.DTO;

import java.io.Serializable;

/**
 * @author ftuser
 *
 */
public class FPSRationCard implements Serializable{

	private String bestFinger1;
	private String bestFinger2;
	private String bestFinger3;

	
	private String errorCode;
	private String memberId;
	private String memberName;
	private String rationCardNumber;
	private String status;
	private String uidNo;
	private boolean selectedItem;

	/**
	 * 
	 */
	public FPSRationCard() {
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

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getRationCardNumber() {
		return rationCardNumber;
	}

	public void setRationCardNumber(String rationCardNumber) {
		this.rationCardNumber = rationCardNumber;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUidNo() {
		return uidNo;
	}

	public void setUidNo(String uidNo) {
		this.uidNo = uidNo;
	}

	public boolean isSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(boolean selectedItem) {
		this.selectedItem = selectedItem;
	}

}
