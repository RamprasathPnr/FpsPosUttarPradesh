/**
 *
 */
package com.omneagate.Util;

/**
 * @author ftuser
 *
 */
public class AppUtil {

	/**
	 *
	 */
	public AppUtil() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param strValue
	 * @param defaultValue
	 * @return
	 */
	public static Double stringToDouble(String strValue, Double defaultValue) {
		Double rtnVal = null;
		if (strValue == null || strValue.isEmpty()) {
			rtnVal = defaultValue;
		} else {
			rtnVal = new Double(strValue);
		}
		return rtnVal;
	}


	/**
	 * @param dValue
	 * @param defaultValue
	 * @return
	 */
	public static String doubleToString(Double dValue, String defaultValue, boolean showDecimal) {
		String rtnVal = null;
		if (dValue == null) {
			rtnVal = defaultValue;
		} else {
			rtnVal = String.valueOf(dValue);
		}
		if (!showDecimal && rtnVal.indexOf(".")>-1) {

			rtnVal = rtnVal.substring(0, rtnVal.indexOf("."));
		}
		return rtnVal;
	}

}
