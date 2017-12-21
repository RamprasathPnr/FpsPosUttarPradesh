/**
 * 
 */
package com.omneagate.DTO;

/**
 * @author ftuser
 *
 */
public class GeneralResponse {

	private String respMessage;
	private String respMsgCode;



	private String transactionId;

	/**
	 * 
	 */
	public GeneralResponse() {

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

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

}
