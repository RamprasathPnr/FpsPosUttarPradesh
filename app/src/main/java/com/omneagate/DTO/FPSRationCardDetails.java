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
public class FPSRationCardDetails implements Serializable {

	private String eposMessage;
	private String respMessage;
	private String respMsgCode;

	private List<FPSRationCard> rationCardList = null;

	/**
	 * 
	 */
	public FPSRationCardDetails() {
		rationCardList = new ArrayList<FPSRationCard>();
	}

	public String getEposMessage() {
		return eposMessage;
	}

	public void setEposMessage(String eposMessage) {
		this.eposMessage = eposMessage;
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

	public List<FPSRationCard> getRationCardList() {
		return rationCardList;
	}

	public void setRationCardList(List<FPSRationCard> rationCardList) {
		this.rationCardList = rationCardList;
	}

}
