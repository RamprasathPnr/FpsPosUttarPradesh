package com.omneagate.DTO;

import android.database.Cursor;

import com.omneagate.Util.InspectionConstants;

import lombok.Data;

@Data
public class InspectionReportDto extends BaseDto{

    Long id;

    Long scheduledInspectionId;

    //Server Primary Key
    Long userId;

    Long inspectorId;

    String userName;


    Long godownId;

    Long fpsId;


    Long talukId;


    Long districtId;

    Long villageId;

    Long stateId;



    //Local Report id to check server response
    Long clientId;


    long dateOfInspection;

    /*Scheduled Inspection-true
     *UnScheduled Inspection-false  */
    Boolean typeOfInspection;

    /* Inspecting official Name*/
    String inspectorName;

    /* Inspecting official Designation */
    String designation;

    /* Inspecting official Department */
    String department;

    Long employeeId;

    String state;

    String district;

    String taluk;

    /*Inspection for FPS or Godown  */
    String inspectionPlace;

    /* FPS or Godown Code */
    String code;

    String fpsOrGodownName;

    String inchargeName;

    String mailingAddress;

    String areaOfficer;

    FindingCriteriaDto findingCriteriaDto;

    String overAllComments;

    double fineAmount;

    /*Clear-true, pending-false*/
    Boolean overAllStatus;

   // int isOverAll;

    long lastSyncTime;


    String village;

    String status;

    int retryCount;


    long retryTime;

    String transactionId;

    String fpsAckStatus;

    int fpsAckSyncStatus;


    public InspectionReportDto()
    {

    }


    public InspectionReportDto(Cursor cursor)
    {
        clientId = cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_CLIENT_ID));

        id = cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_ID));

        Long scheduledId = cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_SCHEDULED_LIST_ID));
        if(scheduledId!=0){
            scheduledInspectionId = scheduledId ;
        }else{
            scheduledInspectionId = null;
        }

        fpsId = cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_FPS_ID));

        godownId = cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_GODOWN_ID));

        userName=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_USERNAME));

        inspectorName=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTOR_NAME));

        designation=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTOR_DESIGNATION));

        department=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTOR_DEPARTMENT));

        employeeId=cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTOR_EMPLOYEE_ID));


        state=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_IS_INSPECTION_STATE));

        district=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_DISTRICT));

        taluk=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_TALUK));

        village = cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_VILLAGE));

        inspectionPlace=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_PLACE));

        code=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_CODE));

        fpsOrGodownName=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_FPS_OR_GODOWN_NAME));

        inchargeName=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_FPS_OR_GODOWN_INCHARGE_NAME));

        mailingAddress=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_FPS_OR_GODOWN_ADDRESS1));

        areaOfficer=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_AREA_OFFICER));

        overAllComments=cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_OVERALL_COMMANDS));

        overAllStatus = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_OVERALL_STATUS))) ;

        typeOfInspection = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_TYPE_OF_INSPECTION))) ;

        dateOfInspection =cursor.getLong(cursor.getColumnIndex(InspectionConstants.KEY_DATE_OF_INSPECTION));

        status  = cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_IS_TRANSEFERED));

        retryCount = cursor.getInt(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_COUNT));

        retryTime  = Long.parseLong(cursor.getString(cursor.getColumnIndex(InspectionConstants.KEY_INSPECTION_REPORT_RETRY_TIME)));

        userId = cursor.getLong((cursor.getColumnIndex("userId")));

        inspectorId = cursor.getLong((cursor.getColumnIndex("userId")));

        districtId = cursor.getLong((cursor.getColumnIndex("districtId")));

        talukId = cursor.getLong((cursor.getColumnIndex("talukId")));

        villageId = cursor.getLong(cursor.getColumnIndex("villageId"));

        stateId = cursor.getLong((cursor.getColumnIndex("stateId")));

        fineAmount = cursor.getDouble((cursor.getColumnIndex("fineAmount")));

        transactionId = cursor.getString((cursor.getColumnIndex("transaction_id")));

        fpsAckStatus = cursor.getString((cursor.getColumnIndex("fps_ack_status")));

        fpsAckSyncStatus = cursor.getInt((cursor.getColumnIndex("fps_ack_sync_status")));

    }



}
