package com.omneagate.DTO;


import android.database.Cursor;

import java.util.List;

import lombok.Data;

@Data
public class CardInspectionDto extends BaseDto{
	/** Auto generated value */

	private Long id;

    /** Commodity need to be checked for the Family Card number */
    private Long commodity;

	/** Family card number */
	private String cardNumber;

	/** Commodity issued as per the Pos */
	private Double commodityIssuedasperPos;

	/** Commodity issued as per the Card Information */
	private Double commodityIssuedasperCard;

	/** Variance of the Commodity between pos and Family card */
	private Double variance;

	/** Remarks of the inspector */
	private String remarks;

	/** Inspection report */
	private Long reportId;

    /* Images in Base64 Format -Sending Format*/
    private List<String> images;

   //Byte format one Activity to another activity
    private List<byte[]> imageBytesList;

    private List<String> photoPathList;

    private Long clientReportId;

    private String status;

    private Long fpsId;

    public CardInspectionDto()
    {

    }

    public CardInspectionDto(Cursor cursor)
    {

        id = cursor.getLong(cursor.getColumnIndex("_id"));

        clientReportId = cursor.getLong(cursor.getColumnIndex("client_reportId"));

        cardNumber= cursor.getString(cursor.getColumnIndex("card_number"));

        fpsId = cursor.getLong(cursor.getColumnIndex("fpsId"));

        commodity = cursor.getLong(cursor.getColumnIndex("product_id"));

        commodityIssuedasperPos = cursor.getDouble(cursor.getColumnIndex("commodity_issued_pos"));

        commodityIssuedasperCard = cursor.getDouble(cursor.getColumnIndex("commodity_entered_card"));

        variance =  cursor.getDouble(cursor.getColumnIndex("variance"));

        remarks = cursor.getString(cursor.getColumnIndex("remarks"));

        status = cursor.getString(cursor.getColumnIndex("status"));




    }

}
