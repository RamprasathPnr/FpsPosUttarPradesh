/**
 * 
 */
package com.omneagate.exception;

import com.omneagate.Util.LoginData;
import com.omneagate.Util.Util;
import com.omneagate.activity.GlobalAppState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author ftuser
 *
 */
public class FPSException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8290246986089498551L;

	private String errorMsg =null;

	/**
	 * 
	 */
	public FPSException() {
		// TODO Auto-generated constructor stub
	}

	public FPSException(String message) {
		//super(FPSErrorCode.getErrorMessage(message));
		errorMsg = message;
		//System.out.println("errorMsg: " + errorMsg);
	}

	public FPSException(String  code, String message) {
		//super(FPSErrorCode.getErrorMessage(message));
		//errorMsg = "Error Code: "+code+"\n Error: "+message;
		//System.out.println("errorMsg: " + errorMsg);
		message=message==null?"":message.trim();
		code=code==null?"":code.trim();
	//	errorMsg = message+"("+code+")";

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date currentDate;

		if(Util.needInternalClock && GlobalAppState.serverDate !=null){
			currentDate=GlobalAppState.serverDate;
		}else{
			currentDate = new Date();
		}

		errorMsg = message+"("+code+")" +"\n Date   :   "+dateFormat.format(currentDate) ;

		if(LoginData.getInstance().getShopNo() !=null && !LoginData.getInstance().getShopNo().equalsIgnoreCase("")){
			errorMsg = errorMsg + "\n Shop No   :   "+LoginData.getInstance().getShopNo();
		}

		if(LoginData.getInstance().getRcNoEntered() !=null && !LoginData.getInstance().getRcNoEntered().equalsIgnoreCase("")){
			errorMsg = errorMsg + "\n Ration Card No   :   "+LoginData.getInstance().getRcNoEntered();
		}

	}

	public void setMessage(String message) {
		this.errorMsg = message;
	}

	@Override
	public String getMessage() {
		return this.errorMsg;
	}
}
