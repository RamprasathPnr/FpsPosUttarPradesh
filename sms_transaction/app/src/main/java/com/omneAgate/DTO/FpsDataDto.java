package com.omneagate.DTO;

import java.util.Set;

import lombok.Data;

@Data
public class FpsDataDto extends BaseDto {

    String lastSyncTime;

    Set<BillDto> billDto;

    Set<ProductDto> productDto;

    Set<BeneficiaryDto> beneficiaryDto;

    Set<CardTypeDto> cardtypeDto;

    Set<UserDetailDto> userdetailDto;

    Set<FpsStoreDto> fpsstoreDto;

    Set<TransactionDto> transactionDto;

    Set<FPSStockDto> fpsStockDto;

    Set<GodownStockOutwardDto> fpsStoInwardDto;

    Set<ServiceProviderDto> serviceProviderDto;

    Set<RegionBasedRule> regionBasedRulesDto;

    Set<EntitlementMasterRule> entitlementMasterRulesDto;

    Set<PersonBasedRule> personBasedRulesDto;

    Set<SplEntitlementRule> splEntitlementRulesDto;

    Set<BeneficiaryRegistrationData> benefRegReqDto;


}
