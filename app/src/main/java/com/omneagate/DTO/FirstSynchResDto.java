package com.omneagate.DTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Data;

/**
 * Created by user1 on 29/5/15.
 */

@Data
public class FirstSynchResDto extends BaseDto {


    List<String> tableList;


    /**
     * collection bill data
     */

    Set<BillDto> billDto;

    /**
     * collection product data
     */

    Set<ProductDto> productDto;

    /**
     * collection beneficiary data
     */

    Set<BeneficiaryDto> beneficiaryDto;

    /**
     * collection card type data
     */

    Set<CardTypeDto> cardtypeDto;

    /**
     * collection user detail data
     */

    Set<UserDetailDto> userdetailDto;

    /**
     * collection fps store data
     */

    Set<FpsStoreDto> fpsstoreDto;

    /**
     * collection transaction data
     */

    Set<TransactionDto> transactionDto;

    /**
     * collection fps stock inward data
     */
    Set<GodownStockOutwardDto> godownStockOutwardDto;
    /**
     * collection fps stock data
     */

    Set<FPSStockDto> fpsStockDto;

    /**
     * collection serviceProvider data
     */
    Set<ServiceProviderDto> serviceProviderDto;

    Set<RegionBasedRule> regionBasedRulesDto;

    Set<EntitlementMasterRule> entitlementMasterRulesDto;

    Set<PersonBasedRule> personBasedRulesDto;

    Set<SplEntitlementRule> splEntitlementRulesDto;

    Set<BeneficiaryRegistrationData> benefRegReqDto;

    Set<SmsProviderDto> smsProviderDtos;

    Set<FPSMigrationDto> fpsMigrationDtos;

    Map<String, Integer> tableDetails;

    Set<POSStockAdjustmentDto> stockAdjusmentDtos;

    Set<FPSStockHistoryCollectionDto>fpsstockHistoryDtoCollection;

    Set<BifurcationHistoryDto> bifurcationHistoryDtos;


    Set<POSOperatingHoursDto> posOperatingHoursDtos;


    int totalCount;


    boolean hasMore;


    int currentCount;


    int totalSentCount;


    String tableName;


    String refNum;


    boolean firstFetch;

    /**
     * last synch time recieved at server
     */

    String lastSyncTime;


    /**
     * collection fps stock inward data
     */
    Set<GodownStockOutwardDto> fpsStoInwardDto;


    Set<StockAllotmentDto> stockAllotmentDto;

    Set<GodownDto> godownDtos;

    Set<ProductPriceOverrideDto> overrideDto;

    Set<GroupDto> productGroupDtos;

    Set<AadharSeedingDto> aadhaarDtos;

    Set<CloseSaleTransactionDto> closeSaleTransactionDtos;

    Set<FpsAdvanceStockDto> fpsAdvanceStockDtos;

    Set<ProxyDetailDto> proxyDetailDto;

    Set<BFDDetailDto> bfdDetailDto;

    Set<RelationshipDto> relationShipDto;

    Set<InspectionCriteriaDto> inspectionCriteriaDtos;

    Set<FpsAllocationCommodityDetailDto> FpsAllocationCommodityDetailDto;

    Set<NfsaPosDataDto> nfsaPosDataDtos;

}
