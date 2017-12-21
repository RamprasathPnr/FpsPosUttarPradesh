package com.omneagate.DTO;

import lombok.Data;

/**
 * Created for FirstSynchReqDto
 */

@Data
public class FirstSynchReqDto {

    String deviceNum;

    String tableName;

    int totalCount;

    int currentCount;

    int totalSentCount;

    String refNum;

    String lastSyncTime;

    boolean isEndOfSynch;


}
