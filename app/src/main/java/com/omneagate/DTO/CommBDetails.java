/**
 * 
 */
package com.omneagate.DTO;

import java.io.Serializable;

/**
 * @author ftuser
 *
 */
public class CommBDetails implements Serializable{

	private String existingRCNo;
	private String shopNo;
	private String typeId;

	/**
	 * 
	 */
	public CommBDetails() {
	}

	public String getExistingRCNo() {
		return existingRCNo;
	}

	public void setExistingRCNo(String existingRCNo) {
		this.existingRCNo = existingRCNo;
	}

	public String getShopNo() {
		return shopNo;
	}

	public void setShopNo(String shopNo) {
		this.shopNo = shopNo;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

}
