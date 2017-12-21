/**
 *
 */
package com.omneagate.DTO;

import android.widget.EditText;

import java.io.Serializable;

/**
 * @author ftuser
 *
 */
public class Product implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -116348492547622797L;
	private String code;
	private String tagName;
	private String itemQtyTagName;
	private String displayName;

	private Double productPrice;
	private Double productAllotment;

	private Double closingBalance;

	private Double productAllottedQty = null;
	private Double productBalanceQty = null;

	private Double issuedQuantity = null;
	private Double receivedQuantity = null;

	private String units;
	private Double unitRate;
	private Double quantityEntered;
	private Double amount;
	private Double commodityClosingBalance;

	private String unitName;
	private boolean autoWeight;

	private EditText valueET;

	private Integer[] quantityValues;

	private boolean visible;

	public boolean isEntitlementQty() {
		return entitlementQty;
	}

	public void setEntitlementQty(boolean entitlementQty) {
		this.entitlementQty = entitlementQty;
	}

	private boolean entitlementQty;

	/**
	 *
	 */
	/**
	 * @param code
	 * @param tagName
	 * @param displayName
	 */


	public Product(String code, String tagName, String displayName) {
		this.code = code;
		this.tagName = tagName;
		this.displayName = displayName;
	}
	public Product(){

	}

	/**
	 * @param code
	 * @param tagName
	 * @param displayName
	 * @param itemQtyTagName
	 */
	public Product(String code, boolean visible, String tagName, String displayName, String itemQtyTagName, String unitName,
				   boolean autoWeight, Integer[] quantityValues,boolean entitlementQty) {
		this.visible=visible;
		this.code = code;
		this.tagName = tagName;
		this.displayName = displayName;
		this.itemQtyTagName = itemQtyTagName;
		this.unitName = unitName;
		this.autoWeight = autoWeight;
		this.quantityValues = quantityValues;
		this.entitlementQty = entitlementQty;
	}


	public Product(String code,  String displayName, Double unitRate,
				   Double closingBalance) {
		this.code = code;
		//this.tagName = tagName;
		this.displayName = displayName;
		//this.itemQtyTagName = itemQtyTagName;
		this.unitRate = unitRate;
		this.closingBalance=closingBalance;
		//this.units = units;
	}

	public String getCode() {
		return code;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	/**
	 * @return the itemQtyTagName
	 */
	public String getItemQtyTagName() {
		return itemQtyTagName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(Double productPrice) {
		this.productPrice = productPrice;
	}

	public Double getProductAllotment() {
		return productAllotment;
	}

	public void setProductAllotment(Double productAllotment) {
		this.productAllotment = productAllotment;
	}

	public Double getClosingBalance() {
		return closingBalance;
	}

	public void setClosingBalance(Double closingBalance) {
		this.closingBalance = closingBalance;
	}

	/**
	 * @return the productAllottedQty
	 */
	public Double getProductAllottedQty() {
		return productAllottedQty;
	}

	/**
	 * @param productAllottedQty
	 *            the productAllottedQty to set
	 */
	public void setProductAllottedQty(Double productAllottedQty) {
		this.productAllottedQty = productAllottedQty;
	}

	/**
	 * @return the productBalanceQty
	 */
	public Double getProductBalanceQty() {
		return productBalanceQty;
	}

	/**
	 * @param productBalanceQty
	 *            the productBalanceQty to set
	 */
	public void setProductBalanceQty(Double productBalanceQty) {
		this.productBalanceQty = productBalanceQty;
	}

	/**
	 * @return the issuedQuantity
	 */
	public Double getIssuedQuantity() {
		return issuedQuantity;
	}

	/**
	 * @param issuedQuantity
	 *            the issuedQuantity to set
	 */
	public void setIssuedQuantity(Double issuedQuantity) {
		this.issuedQuantity = issuedQuantity;
	}

	/**
	 * @return the receivedQuantity
	 */
	public Double getReceivedQuantity() {
		return receivedQuantity;
	}

	/**
	 * @param receivedQuantity
	 *            the receivedQuantity to set
	 */
	public void setReceivedQuantity(Double receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	/**
	 * @return the units
	 */
	public String getUnits() {
		return units;
	}

	/**
	 * @param units
	 *            the units to set
	 */
	public void setUnits(String units) {
		this.units = units;
	}

	/**
	 * @return the unitRate
	 */
	public Double getUnitRate() {
		return unitRate;
	}

	/**
	 * @param unitRate
	 *            the unitRate to set
	 */
	public void setUnitRate(Double unitRate) {
		this.unitRate = unitRate;
	}

	/**
	 * @return the quantityEntered
	 */
	public Double getQuantityEntered() {
		return quantityEntered;
	}

	/**
	 * @param quantityEntered
	 *            the quantityEntered to set
	 */
	public void setQuantityEntered(Double quantityEntered) {
		this.quantityEntered = quantityEntered;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the commodityClosingBalance
	 */
	public Double getCommodityClosingBalance() {
		return commodityClosingBalance;
	}

	/**
	 * @param commodityClosingBalance
	 *            the commodityClosingBalance to set
	 */
	public void setCommodityClosingBalance(Double commodityClosingBalance) {
		this.commodityClosingBalance = commodityClosingBalance;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	public boolean isAutoWeight() {
		return autoWeight;
	}


	public EditText getValueET() {
		return valueET;
	}

	public void setValueET(EditText valueET) {
		this.valueET = valueET;
	}

	public Integer[] getQuantityValues(){
		return this.quantityValues;
	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	/**
	 * @param prd
	 * @return
	 */
	public boolean isEqual(Product prd) {
		if (prd == null || prd.getCode() == null || this.getCode() == null) {
			return false;
		} else {
			return prd.getCode().equals(this.getCode());
		}

	}

}
