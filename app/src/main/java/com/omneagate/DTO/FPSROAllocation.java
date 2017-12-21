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
public class FPSROAllocation  implements Serializable{

	private String respMessage;
	private String respMsgCode;
	private String transDate;
	private String transRoNo;
	private Double totalAmt;

	private List<Product> fpsProductList = null;

	/**
	 * 
	 */
	public FPSROAllocation() {
		fpsProductList = new ArrayList<Product>();
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

	public String getTransDate() {
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransRoNo() {
		return transRoNo;
	}

	public void setTransRoNo(String transRoNo) {
		this.transRoNo = transRoNo;
	}

	public Double getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}

	public List<Product> getFpsProductList() {
		return fpsProductList;
	}

	public void setFpsProductList(List<Product> fpsProductList) {
		this.fpsProductList = fpsProductList;
	}

}
