package com.omneagate.Util;

import android.app.Activity;
import android.util.Log;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EntitlementMasterRuleDtod;
import com.omneagate.DTO.FpsStoreDto;
import com.omneagate.DTO.LoginResponseDto;
import com.omneagate.DTO.PersonBasedRule;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.DTO.RegionBasedRule;
import com.omneagate.DTO.SplEntitlementRule;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Beneficiary sales transaction class
 */
public class BeneficiarySalesQRTransaction {
    private static final String MUNICIPALITY = "Municipality";
    //	private static final String HEAD_QUARTER="Head Quarter";
    private static final String HEAD_QUARTER = "District Head Quarter";
    //	private static  final String BELT_AREA="Belt Area";
    private static final String VILLAGE_PANCHAYAT = "Village Panchayats";
    //	private static final String TALUK="Taluk";
    private static final String TOWN_PANCHAYAT = "Township and Town Panchayats";
    private static final String HILL_AREA = "Hilly Areas";
    private static final String SPECIAL_AREA = "Special Areas";
    private static final String STAGE_VILLAGE = "STAGE_VILLAGE";
    private static final String STAGE_TALUK = "STAGE_TALUK";
    private static final String STAGE_DISTRICT = "STAGE_DISTRICT";
    private static final String STAGE_GENERIC = "STAGE_GENERIC";
    Activity context; //Context for this class
    BeneficiaryDto beneficiary;//Beneficiary of card
    String qrCode; //qr code of user card
    private List<BillItemDto> billItems; //Bill items bought
    String TAG = "BeneficiarySalesQRTransaction";

    public BeneficiarySalesQRTransaction(Activity activity) {
        context = activity;
        Util.changeLanguage(context, GlobalAppState.language);
    }

    /**
     * Returns the transaction response for qrCode
     *
     * @param qrCode
     */
    public QRTransactionResponseDto getBeneficiaryDetails(String qrCode) {
        this.qrCode = qrCode;
        if (getBeneficiaryAndMemberDetails()) {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "Beneficiary Found for ID ->" + beneficiary.getId());
            return returnQRResponse();
        }
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "Invalid Beneficiary");
        return null;
    }

    /**
     * Check and return the details of beneficiary
     * <p/>
     * if beneficiary family members size is zero returns false
     * if beneficiary card is not active or blocked returns false
     * else returns true
     */
    private boolean getBeneficiaryAndMemberDetails() {
        beneficiary = FPSDBHelper.getInstance(context).beneficiaryDto(qrCode);
        DateTime month = new DateTime();

//        Calendar cal = Calendar.getInstance();
//        cal.setTimeZone(TimeZone.getTimeZone("Asia/Kolkatta"));

//        LocalDate today = LocalDate.now(); int year = today.getYear();
//        int month = today.getMonthOfYear();

        billItems = new ArrayList<BillItemDto>();
        if (beneficiary == null) {
            // Util.LoggingQueue(context, "QR TransactionResponse", "Beneficiary not found in db");
            Util.messageBar(context, context.getString(R.string.fpsBeneficiaryMismatch));
            return false;
        } else {
            if (beneficiary.isActive()) {
                billItems = FPSDBHelper.getInstance(context).getAllBillItems(beneficiary.getId(), month.getMonthOfYear());
//                billItems = FPSDBHelper.getInstance(context).getAllBillItems(beneficiary.getId(), cal.get(cal.MONTH)+1);
//                billItems = FPSDBHelper.getInstance(context).getAllBillItems(beneficiary.getId(), month);
                return true;
            } else {
                //  Util.LoggingQueue(context, "QR TransactionResponse", "Beneficiary is inactive");
                Util.messageBar(context, context.getString(R.string.cardBlocked));
                return false;
            }
        }
    }

    //returns the qrCode response
    private QRTransactionResponseDto returnQRResponse() {
        /*QRTransactionResponseDto qrResponse = new QRTransactionResponseDto();
        qrResponse.setMobileNumber(beneficiary.getMobileNumber());
        qrResponse.setUfc(beneficiary.getEncryptedUfc());
        qrResponse.setMaskedUfc(Util.DecryptedBeneficiary(context, beneficiary.getEncryptedUfc()));
        qrResponse.setBenficiaryId(beneficiary.getId());
        qrResponse.setRationCardNo(beneficiary.getOldRationNumber());
        qrResponse.setFpsId(beneficiary.getFpsId());
        List<EntitlementDTO> entitlementList = returnEntitlements();
        Collections.sort(entitlementList);
        qrResponse.setEntitlementList(entitlementList);
        Util.LoggingQueue(context, "QR TransactionResponse", qrResponse.toString());
        return qrResponse;*/
        QRTransactionResponseDto qrResponse = new QRTransactionResponseDto();
        Util.LoggingQueue(context, "beneficiary......", beneficiary.toString());
        qrResponse.setMobileNumber(beneficiary.getMobileNumber());
        qrResponse.setUfc(beneficiary.getEncryptedUfc());
        qrResponse.setMaskedUfc(Util.DecryptedBeneficiary(context, beneficiary.getEncryptedUfc()));
        qrResponse.setRationCardNo(beneficiary.getOldRationNumber());
        qrResponse.setBenficiaryId(beneficiary.getId());
        qrResponse.setFpsId(beneficiary.getFpsId());
        List<EntitlementDTO> entitlementDTOs = returnEntitlements();
        Util.LoggingQueue(context, "entitlementDTOs......", entitlementDTOs.toString());
        qrResponse.setEntitlementList(entitlementDTOs);
//        Map<Long, List<EntitlementDTO>> userEntitle = new HashMap<>();
        Map<Long, List<EntitlementDTO>> userEntitle = new LinkedHashMap<>();
        for (EntitlementDTO entitle : entitlementDTOs) {
            if (userEntitle.containsKey(entitle.getGroupId())) {
                userEntitle.get(entitle.getGroupId()).add(entitle);
            } else {
                List<EntitlementDTO> entitles = new ArrayList<>();
                entitles.add(entitle);
                userEntitle.put(entitle.getGroupId(), entitles);
            }
        }
        Util.LoggingQueue(context, "userEntitle......", userEntitle.toString());
        qrResponse.setUserEntitlement(userEntitle);
        qrResponse.setEntitlementList(setValueForList(userEntitle));
        Util.LoggingQueue(context, "QR TransactionResponse", qrResponse.toString());
        return qrResponse;
    }

    private List<EntitlementDTO> setValueForList(Map<Long, List<EntitlementDTO>> userEntitle) {
        List<EntitlementDTO> entitlements = new ArrayList<>();
        for (Long keys : userEntitle.keySet()) {
            List<EntitlementDTO> entitles = userEntitle.get(keys);
            double bought = getListBySize(entitles);
            double nfsabought = getNfsa_ListBySize(entitles);
            for (EntitlementDTO entitlementDTO : entitles) {
                entitlementDTO.setPurchasedQuantity(bought);
                entitlementDTO.setNfsa_purchasedQuantity(nfsabought);
                entitlementDTO.setCurrentQuantity(entitlementDTO.getEntitledQuantity() - bought);
                entitlementDTO.setProductPrice(productPrice(entitlementDTO.getProductPrice(), entitlementDTO.getProductId()));
                entitlements.add(entitlementDTO);
            }
        }
        return entitlements;
    }

    private double getNfsa_ListBySize(List<EntitlementDTO> entitles) {
        double totalSize = 0.0;
        for (EntitlementDTO entitlementDTO : entitles) {
            totalSize = totalSize + entitlementDTO.getNfsa_purchasedQuantity();
        }
        return totalSize;
    }

    private double getListBySize(List<EntitlementDTO> entitles) {
        double totalSize = 0.0;
        for (EntitlementDTO entitlementDTO : entitles) {
            totalSize = totalSize + entitlementDTO.getPurchasedQuantity();
        }
        return totalSize;
    }

    private double productPrice(double productPrice, long productId) {
        double value = 0.0;
        double valuePercentage = FPSDBHelper.getInstance(context).productOverridePercentage(productId, Long.parseLong(beneficiary.getCardTypeId()));
        if (valuePercentage == 0) {
            return productPrice;
        } else {
            value = productPrice * (valuePercentage / 100);
        }
        return value;
    }

    //return the entitlement of product to user
    private List<EntitlementDTO> returnEntitlements() {
        List<EntitlementDTO> entitlements = new ArrayList<EntitlementDTO>();
        List<EntitlementMasterRuleDtod> entitlementRules = FPSDBHelper.getInstance(context).getAllEntitlementMasterRuleProduct(Long.parseLong(beneficiary.getCardTypeId()));
        // Util.LoggingQueue(context, "Entitlement rules", "Rules found:" + entitlementRules.toString());
        double entitledQty = 0.0;
        double currentQty = 0.0;
        if (entitlementRules == null || entitlementRules.isEmpty()) {
            Util.LoggingQueue(context, "Rules", "Entitlement rules empty");
            return new ArrayList<>();
        }
        Util.LoggingQueue(context, "BeneficiarySalesTransaction", "---------------------entitlementRules->" + entitlementRules.toString());
        for (EntitlementMasterRuleDtod masterRule : entitlementRules) {
            try {
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "---------------------Entitlement for group ID  ->" + masterRule.getGroupId());
                entitledQty = processEntitlementRule(beneficiary, masterRule);
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "a.....................");
                EntitlementDTO entitlement = new EntitlementDTO();
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "b.....................");
                double purchased = findCurrentTransactions(masterRule.getProductId());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "c.....................");
                currentQty = entitledQty - purchased;
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "d.....................");
                entitlement.setCurrentQuantity(currentQty >= 0 ? currentQty : 0);
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "e.....................");
                entitlement.setEntitledQuantity(entitledQty);/**/
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "f.....................");
                entitlement.setPurchasedQuantity(entitledQty >= purchased ? purchased : entitledQty);
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "g.....................");
                entitlement.setNfsa_purchasedQuantity(purchased);
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "h.....................");
                entitlement.setProductId(masterRule.getProductId());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "i.....................");
                entitlement.setProductName(masterRule.getName());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "j.....................");
                entitlement.setProductPrice(masterRule.getProductPrice());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "k.....................");
                entitlement.setProductUnit(masterRule.getProductUnit());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "l.....................");
                entitlement.setLproductUnit(masterRule.getLocalProductUnit());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "m.....................");
                entitlement.setLproductName(masterRule.getLocalProductName());
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "n.....................");
                entitlement.setGroupId(masterRule.getGroupId());
                // Util.LoggingQueue(context, "Entitlement", "Current entitlement:" + entitlement.toString());
                entitlements.add(entitlement);
            }
            catch(Exception e) {
                Util.LoggingQueue(context, "Rules", "entitlementRules exc..."+e);

            }
        }
        return entitlements;
    }

    /**
     * This method calculates current month transactions for the beneficiary
     *
     * @param productId - product id
     * @return value        - total value transacted for the current month
     */
    private double findCurrentTransactions(long productId) {
        for (int i = 0; i < billItems.size(); i++) {
            if (billItems.get(i).getProductId() == productId) {
                return billItems.get(i).getQuantity();
            }
        }
        return 0.0;
    }

    /**
     * This methods process master entitlement rule and calls the appropriate
     * methods to process region based and person based and spl rules
     *
     * @param beneficiary - beneficiary
     * @param masterRule  - master rule
     * @return double - quantity
     */
    public double processEntitlementRule(BeneficiaryDto beneficiary, EntitlementMasterRuleDtod masterRule) {
       /* double quantity = 0.0;
        if (masterRule.isCalcRequired()) {
            Util.LoggingQueue(context, "Entitlement", "Calculation required:" + masterRule.getGroupId());
            if (masterRule.isPersonBased()) {
                Util.LoggingQueue(context, "Entitlement", "Calculation required person based:" + masterRule.getGroupId());
                quantity = processPersonBasedRule(beneficiary, masterRule.getGroupId());
            }

            if (masterRule.isRegionBased()) {
                Util.LoggingQueue(context, "Entitlement", "Calculation required region based:" + masterRule.getGroupId());
                quantity = processRegionBasedRule(beneficiary, masterRule.getGroupId());
            }

            if (masterRule.isHasSpecialRule()) {
                Util.LoggingQueue(context, "Entitlement", "Calculation required special rules:" + masterRule.getGroupId());
                quantity = processSpecialRule(beneficiary, masterRule.getGroupId(), quantity);
            }
        } else {
            Util.LoggingQueue(context, "Entitlement", "Calculation Not required:" + masterRule.getGroupId());
            quantity = masterRule.getQuantity();
        }
        return quantity;*/
        double quantity = 0.0;
        if (masterRule.isCalcRequired()) {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "Calucation Required for Group ID " + masterRule.getGroupId());
            if (masterRule.isPersonBased()) {
                quantity = processPersonBasedRule(beneficiary, masterRule.getGroupId(), beneficiary.getCardTypeId());
            } else if (masterRule.isRegionBased()) {
                quantity = processRegionBasedRule(beneficiary, masterRule.getGroupId());
            }
            if (masterRule.isHasSpecialRule()) {
//                quantity = processSpecialRule(beneficiary, masterRule.getGroupId(), quantity);
                quantity = processGenericSpecialRule(beneficiary, masterRule.getGroupId(), quantity, STAGE_VILLAGE);
            }
        } else {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "Calculation Not required for Group ID" + masterRule.getGroupId());
            quantity = masterRule.getQuantity();
        }
        return quantity;
    }
    /**
     * This method processes person based rules.
     *
     * @param beneficiary - beneficiary details
     * @param product     - product information
     * @return - entitled quantity
     *//*
    public double processPersonBasedRule(BeneficiaryDto beneficiary, long product) {
        PersonBasedRule personBasedRule = FPSDBHelper.getInstance(context).getAllPersonBasedRule(product);
        //personBasedRuleRepo.findByProduct(product);
        double min = 0, max = 0, perChild = 0, perAdult = 0, quantity = 0;
        if (personBasedRule != null)
            Util.LoggingQueue(context, "Person based rule", personBasedRule.toString());
        min = personBasedRule.getMin();
        max = personBasedRule.getMax();
        perAdult = personBasedRule.getPerAdult();
        perChild = personBasedRule.getPerChild();

        int numOfAdults = beneficiary.getNumOfAdults();
        int numOfChildren = beneficiary.getNumOfChild();

        if (numOfAdults >= 1) {
            quantity += min;
        }
        quantity += (numOfAdults - 1) * perAdult;
        quantity += numOfChildren * perChild;

        // Quantity is less than min value set minimum value
        if (quantity < min)
            quantity = min;

        // Quantity is less than min value set minimum value
        if (quantity > max)
            quantity = max;
        return quantity;
    }
*/

    /**
     * This method processes person based rules.
     *
     * @param beneficiary - beneficiary details
     * @param product     - product information
     * @param cardTypeId  - card type information
     * @return - entitled quantity
     */
    public double processPersonBasedRule(BeneficiaryDto beneficiary, long product, String cardTypeId) {
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "PERSON Calculation Started , BeneID =  " + beneficiary.getId()
                + " & product = " + product + " & cardTypeID  = " + cardTypeId);
        PersonBasedRule personBasedRule = null;
        PersonBasedRule personBasedRuleWithCardType = FPSDBHelper.getInstance(context).findByGroupAndCardType(product, cardTypeId);
        if (personBasedRuleWithCardType == null) {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "NO PERSON  is found card type id = " + cardTypeId);
            personBasedRule = FPSDBHelper.getInstance(context).findByGroupWithoutCardType(product);
        } else {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "PERSON  is found for card type id = " + cardTypeId);
            personBasedRule = personBasedRuleWithCardType;
        }
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "Final PERSON  - Details " + personBasedRule);
        double min = 0, max = 0, perChild = 0, perAdult = 0, quantity = 0;
        min = personBasedRule.getMin();
        max = personBasedRule.getMax();
        perAdult = personBasedRule.getPerAdult();
        perChild = personBasedRule.getPerChild();
        int numOfAdults = beneficiary.getNumOfAdults();
        int numOfChildren = beneficiary.getNumOfChild();
        if (numOfAdults >= 1) {
            quantity += min;
        }
        quantity += (numOfAdults - 1) * perAdult;
        quantity += numOfChildren * perChild;
        // Quantity is less than min value set minimum value
        if (quantity < min)
            quantity = min;
        // Quantity is less than min value set minimum value
        if (quantity > max)
            quantity = max;
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "PERSON Calculation  finished  quantity -> " + quantity);
        return quantity;
    }
    /**
     * This method processes region based rule
     *
     * @param beneficiary
     * @param product
     *//*
    public double processRegionBasedRule(BeneficiaryDto beneficiary, long product) {
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        String entitlementClassification = fpsStore.getEntitlementClassification();
        List<RegionBasedRule> regionBasedRules = FPSDBHelper.getInstance(context).getAllRegionBasedRule(product);
        Util.LoggingQueue(context, "Region based rule", regionBasedRules.toString());
        if (regionBasedRules == null || regionBasedRules != null && regionBasedRules.isEmpty()) {
            return 0.0;
        }
        double quantity = 0.0;
        for (RegionBasedRule regionBasedRule : regionBasedRules) {
            boolean checkCylinderCount = false;
            if (regionBasedRule.getCylinderCount() == -1) {
                checkCylinderCount = true;
            }
            Log.e("RegionBased Rule", regionBasedRule.toString());
            if (regionBasedRule.isCity() && entitlementClassification.equals(BELT_AREA)) {
                if (checkCylinderCount || beneficiary.getNumOfCylinder() == regionBasedRule.getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isCityHeadQuarter() && entitlementClassification.equals(HEAD_QUARTER)) {
                if (checkCylinderCount || beneficiary.getNumOfCylinder() == regionBasedRule.getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isMunicipality() && entitlementClassification.equals(MUNICIPALITY)) {
                if (checkCylinderCount || beneficiary.getNumOfCylinder() == regionBasedRule.getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isTaluk() && entitlementClassification.equals(TALUK)) {
                if (checkCylinderCount || beneficiary.getNumOfCylinder() == regionBasedRule.getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else {
                quantity = 0.0;
            }

          *//*  if(beneficiary.getNumOfCylinder() == regionBasedRule.getCylinderCount() || regionBasedRule.getCylinderCount()==0){
                quantity=regionBasedRule.getQuantity();
            }
            else{
                quantity=0.0;
            }*//*
        }
        return quantity;
    }*/

    /**
     * This method processes region based rules.
     *
     * @param beneficiary - beneficiary details
     * @param product     - product information
     * @return - entitled quantity
     */
    public double processRegionBasedRule(BeneficiaryDto beneficiary, long product) {
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "REGION Calculation Started , BeneID =  " + beneficiary.getId()
                + " & product = " + product);
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        String entitlementClassification = fpsStore.getEntitlementClassification();
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "REGION entitlementClassification =  " + entitlementClassification);
        if (StringUtils.isEmpty(entitlementClassification)) {
            LoginResponseDto resp = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());
            entitlementClassification = resp.getUserDetailDto().getFpsStore().getEntitlementClassification();
        }
        List<RegionBasedRule> regionBasedRules = FPSDBHelper.getInstance(context).getAllRegionBasedRule(product);
        Util.LoggingQueue(context, "Region based rule...", ""+regionBasedRules.size());
        if (regionBasedRules == null || regionBasedRules.size() == 0) {
            return 0.0;
        }
        double quantity = 0.0;
        for (RegionBasedRule regionBasedRule : regionBasedRules) {
            Util.LoggingQueue(context,"BeneficiarySalesQRTransaction","inside for...");
            boolean checkCylinderCount = false;
            if (regionBasedRule.getCylinderCount() == -1) {
                checkCylinderCount = true;
            }
            //Log.e("Region Based Rule", regionBasedRule.toString());
            if (regionBasedRule.isVillagePanchayat()
                    && entitlementClassification.equals(VILLAGE_PANCHAYAT)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isCityHeadQuarter()
                    && entitlementClassification.equals(HEAD_QUARTER)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isMunicipality()
                    && entitlementClassification.equals(MUNICIPALITY)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    Util.LoggingQueue(context,"BeneficiarySalesQRTransaction","inside MUNICIPALITY..."+quantity);
                    break;
                }
            } else if (regionBasedRule.isTownPanchayat()
                    && entitlementClassification.equals(TOWN_PANCHAYAT)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isHillyArea()
                    && entitlementClassification.equals(HILL_AREA)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else if (regionBasedRule.isSplArea()
                    && entitlementClassification.equals(SPECIAL_AREA)) {
                if (checkCylinderCount
                        || beneficiary.getNumOfCylinder() == regionBasedRule
                        .getCylinderCount()) {
                    quantity = regionBasedRule.getQuantity();
                    break;
                }
            } else {
                quantity = 0.0;
            }
        }
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "REGION Calculation  finished  quantity -> " + quantity);
        return quantity;
    }
    /**
     * This method processes special rules
     *
     * @param beneficiary
     * @param product
     *//*
    public double processSpecialRule(BeneficiaryDto beneficiary, long product, double entitledQty) {
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        List<SplEntitlementRule> splEntitlementRules = FPSDBHelper.getInstance(context).getAllSpecialRule(product, beneficiary.getCardTypeId());
        Util.LoggingQueue(context, "special rule", splEntitlementRules.toString());
        if (splEntitlementRules == null || splEntitlementRules != null && splEntitlementRules.isEmpty()) {
        }
        double quantity = 0.0;
        for (SplEntitlementRule splEntitlementRule : splEntitlementRules) {
            boolean checkArea = false;
            boolean checkCylinderCount = false;
            boolean checkFpsClassification = false;

            if (splEntitlementRule.getDistrictId() != 0) {
                checkArea = true;
            }

            if (splEntitlementRule.getCylinderCount() != 0) {
                checkCylinderCount = true;
            }

            if (splEntitlementRule.getCylinderCount() != 0) {
                checkFpsClassification = true;
            }

            if (checkArea) {
                if (splEntitlementRule.getDistrictId() != 0 && splEntitlementRule.getDistrictId() == fpsStore.getDistrictId()) {
                    if (splEntitlementRule.getTalukId() == 0 || splEntitlementRule.getTalukId() == fpsStore.getTalukId() &&
                            splEntitlementRule.getVillageId() == 0 || splEntitlementRule.getVillageId() == fpsStore.getVillageId()) {
                        quantity = splEntitlementRule.getQuantity();
                    } else {
                        quantity = 0.0;
                    }
                }
            }
            if (checkCylinderCount) {
                if (beneficiary.getNumOfCylinder() == splEntitlementRule.getCylinderCount()) {
                    quantity = splEntitlementRule.getQuantity();
                } else {
                    quantity = 0.0;
                }
            }

            if (checkFpsClassification) {
                String entitlementClassification = fpsStore.getEntitlementClassification();
                if (splEntitlementRule.isCity() && entitlementClassification.equals(BELT_AREA) ||
                        splEntitlementRule.isCityHeadQuarter() && entitlementClassification.equals(HEAD_QUARTER) ||
                        splEntitlementRule.isMunicipality() && entitlementClassification.equals(MUNICIPALITY) ||
                        splEntitlementRule.isTaluk() && entitlementClassification.equals(TALUK)) {
                    quantity = splEntitlementRule.getQuantity();
                } else {
                    quantity = 0.0;
                }
            }
            if (splEntitlementRule.isAdd()) {
                entitledQty += quantity;
            }
        }
        return entitledQty;

    }
*/

    /**
     * This method processes person based rules.
     *
     * @param beneficiary - beneficiary details
     * @param product     - product information
     * @param entitledQty - previous calculated entitled quantity
     * @return - entitled quantity
     */
    public double processSpecialRule(BeneficiaryDto beneficiary, long product, double entitledQty) {
        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL Calculation Started , BeneID =  " + beneficiary.getId()
                + " & group id = " + product + " & entitledQty  = " + entitledQty);
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL fpsStore Full Details =  " + fpsStore);
        String entitlementClassification = fpsStore.getEntitlementClassification();
        Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsStore entitlementClassification =  " + entitlementClassification);
        Long fpsdistrictID = null;
        Long fpsVillageID = null;
        Long fpsTalukID = null;
        try {
            if (fpsStore.getDistrictId() == null || fpsStore.getDistrictId() == 0) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "FpsStoreDto is empty , Check with DB for district id ");
                LoginResponseDto resp = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());
                fpsdistrictID = resp.getUserDetailDto().getFpsStore().getDistrictId();
                Util.LoggingQueue(context, "BeneficiaryTransaction", "district id returned from DB  =  " + fpsdistrictID);
            } else {
                fpsdistrictID = fpsStore.getDistrictId();
                Util.LoggingQueue(context, "BeneficiaryTransaction", "district id from FpsStoreDto  =  " + fpsdistrictID);
            }
            if (StringUtils.isEmpty(entitlementClassification) || entitlementClassification == null) {
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "FpsStoreDto is empty , Check with DB for entitlementClassification ");
                LoginResponseDto resp = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());
                entitlementClassification = resp.getUserDetailDto().getFpsStore().getEntitlementClassification();
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "entitlementClassification returned from DB  =  " + entitlementClassification);
                fpsVillageID = resp.getUserDetailDto().getFpsStore().getVillageId();
                fpsTalukID = resp.getUserDetailDto().getFpsStore().getTalukId();
            } else {
                fpsVillageID = fpsStore.getVillageId();
                fpsTalukID = fpsStore.getTalukId();
            }
        } catch (Exception e) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "!SPECIAL District ID  =  Exception " + e.toString());
        }
        List<SplEntitlementRule> splEntitlementRules = null;
        if (fpsVillageID != null) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsVillageID != null" + fpsVillageID);
            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndVillage(product, beneficiary.getCardTypeId(), fpsVillageID);
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsVillageID splEntitlementRules = " + splEntitlementRules.size());
        }
        if (fpsTalukID != null && (splEntitlementRules == null || splEntitlementRules.isEmpty())) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsTalukID != null" + fpsTalukID);
            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndTaluk(product, beneficiary.getCardTypeId(), fpsTalukID);
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsTalukID splEntitlementRules = " + splEntitlementRules.size());
        }
        if (fpsdistrictID != null && (splEntitlementRules == null || splEntitlementRules.isEmpty())) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsdistrictID != null" + fpsdistrictID);
            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndDistrict(product, beneficiary.getCardTypeId(), fpsdistrictID);
            Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsdistrictID splEntitlementRules = " + splEntitlementRules.size());
        }
/*
        if (fpsdistrictID != null && fpsdistrictID != 0) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL with District ID  =  " +fpsdistrictID );

            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndDistrict(product, beneficiary.getCardTypeId(), fpsdistrictID);
        }*/
        if (splEntitlementRules == null || splEntitlementRules.isEmpty()) {
            Log.e("BeneficiaryTransaction", "SPECIAL with out District ID   ");
            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndNullDistrict(product, beneficiary.getCardTypeId());
        }
        if (splEntitlementRules != null) {
            double quantity = 0.0;
            for (SplEntitlementRule splEntitlementRule : splEntitlementRules) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL splEntitlementRule in Iteration  " + splEntitlementRule);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "Iteration SPl  Village  " + splEntitlementRule.getVillageId());
                Util.LoggingQueue(context, "BeneficiaryTransaction", "Iteration SPL Taluk " + splEntitlementRule.getTalukId());
                boolean checkArea = false;
                boolean checkCylinderCount = false;
                boolean checkFpsClassification = false;
                if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0) {
                    checkArea = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkArea is true set  " + checkArea);
                }
                if (splEntitlementRule.getCylinderCount() != -1) {
                    checkCylinderCount = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkCylinderCount is true set  " + checkCylinderCount);
                }
                if (splEntitlementRule.isVillagePanchayat()
                        || splEntitlementRule.isCityHeadQuarter()
                        || splEntitlementRule.isMunicipality()
                        || splEntitlementRule.isTownPanchayat()
                        || splEntitlementRule.isHillyArea()
                        || splEntitlementRule.isSplArea()) {
                    checkFpsClassification = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkFpsClassification is true set  " + checkFpsClassification);
                }
                if (checkArea) {
                    if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0 && splEntitlementRule.getDistrictId() == fpsdistrictID) {
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea SPl  Village  " + splEntitlementRule.getVillageId());
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea FPS Store Village  " + fpsVillageID);
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea SPL Taluk " + splEntitlementRule.getTalukId());
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea FPS Store Taluk " + fpsTalukID);
                        if (splEntitlementRule.getTalukId() == null || splEntitlementRule.getTalukId().equals(0L) || splEntitlementRule.getTalukId().equals(fpsTalukID) &&
                                splEntitlementRule.getVillageId() == null || splEntitlementRule.getVillageId().equals(0L) || splEntitlementRule.getVillageId().equals(fpsVillageID)) {
                            Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea  IF executed  " + quantity);
                        } else {
                            quantity = 0.0;
                            Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea  else executed and quantity =  " + quantity);
                            continue;
                        }
                    }
                }
                if (checkCylinderCount) {
                    if (beneficiary.getNumOfCylinder() == splEntitlementRule.getCylinderCount()) {
                        //quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkCylinderCount  IF executed  " + quantity);
                    } else {
                        quantity = 0.0;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkCylinderCount  else executed and quantity =  " + quantity);
                        continue;
                    }
                }
                if (checkFpsClassification) {
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "checkFpsClassification  IF executed  " + quantity);
                    if (splEntitlementRule.isVillagePanchayat()
                            && entitlementClassification
                            .equals(VILLAGE_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "VILLAGE_PANCHAYAT  IF executed  " + quantity);
                    } else if (splEntitlementRule.isCityHeadQuarter()
                            && entitlementClassification
                            .equals(HEAD_QUARTER)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "HEAD_QUARTER  IF executed  " + quantity);
                    } else if (splEntitlementRule.isMunicipality()
                            && entitlementClassification
                            .equals(MUNICIPALITY)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "MUNICIPALITY  IF executed  " + quantity);
                    } else if (splEntitlementRule.isTownPanchayat()
                            && entitlementClassification
                            .equals(TOWN_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "TOWN_PANCHAYAT  IF executed  " + quantity);
                    } else if (splEntitlementRule.isHillyArea()
                            && entitlementClassification
                            .equals(HILL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "HILL_AREA  IF executed  " + quantity);
                    } else if (splEntitlementRule.isSplArea()
                            && entitlementClassification
                            .equals(SPECIAL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL_AREA  IF executed  " + quantity);
                    } else {
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "  NONE OF THE AREA IS SATISFIED SET quantity = 0 ");
                        quantity = 0.0;
                        continue;
                    }
                    if (splEntitlementRule.isAdd()) {
                        entitledQty += quantity;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "isAdd  IF executed  " + quantity);
                        break;
                    } else {
                        entitledQty = quantity;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "isAdd  else executed  " + quantity);
                        break;
                    }
                }
            }
            Util.LoggingQueue(context, "BeneficiaryTransaction", "XX--------------- SPECIAL Calculation finished  entitledQty-> " + entitledQty + " -----------XX");
        }
        return entitledQty;
    }

    public double processGenericSpecialRule(BeneficiaryDto beneficiary, long product, double entitledQty, String currentStage) {
        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL Calculation Started , BeneID =  " + beneficiary.getId()
                + " & group id = " + product + " & entitledQty  = " + entitledQty + " & currentStage = " + currentStage);
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL fpsStore Full Details =  " + fpsStore);
        String entitlementClassification = fpsStore.getEntitlementClassification();
        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL entitlementClassification =  " + entitlementClassification);
        Long fpsdistrictID = null;
        Long fpsVillageID = null;
        Long fpsTalukID = null;
        boolean ruleSatisfied = false;
        String nextStage = "";
        Util.LoggingQueue(context, "", "initial nextStage : " + nextStage);
        try {
            if (fpsStore.getDistrictId() == null || fpsStore.getDistrictId() == 0) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "FpsStoreDto is empty , Check with DB for district id ");
                LoginResponseDto resp = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());
                Util.LoggingQueue(context, "BeneficiaryTransaction", "SessionId.getInstance().getUserId()  =  " + SessionId.getInstance().getUserId());
                fpsdistrictID = resp.getUserDetailDto().getFpsStore().getDistrictId();
                Util.LoggingQueue(context, "BeneficiaryTransaction", "district id returned from DB  =  " + fpsdistrictID);
            } else {
                fpsdistrictID = fpsStore.getDistrictId();
                Util.LoggingQueue(context, "BeneficiaryTransaction", "district id from FpsStoreDto  =  " + fpsdistrictID);
            }
            if (StringUtils.isEmpty(entitlementClassification) || entitlementClassification == null) {
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "FpsStoreDto is empty , Check with DB for entitlementClassification ");
                LoginResponseDto resp = FPSDBHelper.getInstance(context).getUserDetails(SessionId.getInstance().getUserId());
                entitlementClassification = resp.getUserDetailDto().getFpsStore().getEntitlementClassification();
                Util.LoggingQueue(context, "BeneficiarySalesTransaction", "entitlementClassification returned from DB  =  " + entitlementClassification);
                fpsVillageID = resp.getUserDetailDto().getFpsStore().getVillageId();
                fpsTalukID = resp.getUserDetailDto().getFpsStore().getTalukId();
            } else {
                fpsVillageID = fpsStore.getVillageId();
                fpsTalukID = fpsStore.getTalukId();
            }
        } catch (Exception e) {
            Util.LoggingQueue(context, "BeneficiaryTransaction", "!SPECIAL District ID  =  Exception " + e.toString());
        }
        List<SplEntitlementRule> splEntitlementRules = null;
        if (currentStage.equalsIgnoreCase(STAGE_VILLAGE)) {
            if (fpsVillageID != null) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsVillageID != null -> fpsVillageID = " + fpsVillageID);
                splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndVillage(product, beneficiary.getCardTypeId(), fpsVillageID);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsVillageID splEntitlementRules = " + splEntitlementRules);
                nextStage = getNextStage(currentStage);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified nextStage : " + nextStage);
                if (splEntitlementRules == null || splEntitlementRules.isEmpty()) {
                    currentStage = nextStage;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + nextStage);
                }
            } else {
                currentStage = getNextStage(currentStage);
                ;
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + currentStage);
            }
        }
        if (currentStage.equalsIgnoreCase(STAGE_TALUK)) {
            if (fpsTalukID != null && (splEntitlementRules == null || splEntitlementRules.isEmpty())) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsTalukID != null -> fpsTalukID = " + fpsTalukID);
                splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndTaluk(product, beneficiary.getCardTypeId(), fpsTalukID);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "fpsTalukID splEntitlementRules = " + splEntitlementRules);
                nextStage = getNextStage(currentStage);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified nextStage : " + nextStage);
                if (splEntitlementRules == null || splEntitlementRules.isEmpty()) {
                    currentStage = nextStage;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + nextStage);
                }
            } else {
                currentStage = getNextStage(currentStage);
                ;
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + currentStage);
            }
        }
        if (currentStage.equalsIgnoreCase(STAGE_DISTRICT)) {
            if (fpsdistrictID != null && (splEntitlementRules == null || splEntitlementRules.isEmpty())) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL with District ID  =  " + fpsdistrictID);
                splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndDistrict(product, beneficiary.getCardTypeId(), fpsdistrictID);
                nextStage = getNextStage(currentStage);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified nextStage : " + nextStage);
                if (splEntitlementRules == null || splEntitlementRules.isEmpty()) {
                    currentStage = nextStage;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + nextStage);
                }
            } else {
                currentStage = getNextStage(currentStage);
                ;
                Util.LoggingQueue(context, "BeneficiaryTransaction", "modified currentStage : " + currentStage);
            }
        }
        if (splEntitlementRules == null || splEntitlementRules.isEmpty() && currentStage.equalsIgnoreCase(STAGE_GENERIC)) {
            Log.e("BeneficiaryTransaction", "SPECIAL with out District ID   ");
            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndNullDistrict(product, beneficiary.getCardTypeId());
        }
        if (splEntitlementRules != null) {
            double quantity = 0.0;
            for (SplEntitlementRule splEntitlementRule : splEntitlementRules) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL splEntitlementRule in Iteration  " + splEntitlementRule);
                Util.LoggingQueue(context, "BeneficiaryTransaction", "Iteration SPl  Village  " + splEntitlementRule.getVillageId());
                Util.LoggingQueue(context, "BeneficiaryTransaction", "Iteration SPL Taluk " + splEntitlementRule.getTalukId());
                boolean checkArea = false;
                boolean checkCylinderCount = false;
                boolean checkFpsClassification = false;
                if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0) {
                    checkArea = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkArea is true set  " + checkArea);
                }
                if (splEntitlementRule.getCylinderCount() != -1) {
                    checkCylinderCount = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkCylinderCount is true set  " + checkCylinderCount);
                }
                if (splEntitlementRule.isVillagePanchayat()
                        || splEntitlementRule.isCityHeadQuarter()
                        || splEntitlementRule.isMunicipality()
                        || splEntitlementRule.isTownPanchayat()
                        || splEntitlementRule.isHillyArea()
                        || splEntitlementRule.isSplArea()) {
                    checkFpsClassification = true;
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL checkFpsClassification is true set  " + checkFpsClassification);
                }
                if (checkArea) {
                    if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0 && splEntitlementRule.getDistrictId() == fpsdistrictID) {
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea SPl  Village  " + splEntitlementRule.getVillageId());
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea FPS Store Village  " + fpsVillageID);
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea SPL Taluk " + splEntitlementRule.getTalukId());
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea FPS Store Taluk " + fpsTalukID);
                        if ((splEntitlementRule.getTalukId() == null || splEntitlementRule.getTalukId().equals(0L) || splEntitlementRule.getTalukId().equals(fpsTalukID)) &&
                                (splEntitlementRule.getVillageId() == null || splEntitlementRule.getVillageId().equals(0L) || splEntitlementRule.getVillageId().equals(fpsVillageID))) {
                            Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea  IF executed  " + quantity);
                        } else {
                            quantity = 0.0;
                            Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea  else executed and quantity =  " + quantity);
                            Util.LoggingQueue(context, "BeneficiaryTransaction", "checkArea : Skipping rule ");
                            continue;
                        }
                    }
                }
                if (checkCylinderCount) {
                    if (beneficiary.getNumOfCylinder() == splEntitlementRule.getCylinderCount()) {
                        //quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkCylinderCount  IF executed  " + quantity);
                    } else {
                        quantity = 0.0;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkCylinderCount  else executed and quantity =  " + quantity);
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkCylinderCount : Skipping rule ");
                        continue;
                    }
                }
                if (checkFpsClassification) {
                    Util.LoggingQueue(context, "BeneficiaryTransaction", "checkFpsClassification  IF executed  " + quantity);
                    if (splEntitlementRule.isVillagePanchayat()
                            && entitlementClassification
                            .equals(VILLAGE_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "VILLAGE_PANCHAYAT  IF executed  " + quantity);
                    } else if (splEntitlementRule.isCityHeadQuarter()
                            && entitlementClassification
                            .equals(HEAD_QUARTER)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "HEAD_QUARTER  IF executed  " + quantity);
                    } else if (splEntitlementRule.isMunicipality()
                            && entitlementClassification
                            .equals(MUNICIPALITY)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "MUNICIPALITY  IF executed  " + quantity);
                    } else if (splEntitlementRule.isTownPanchayat()
                            && entitlementClassification
                            .equals(TOWN_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "TOWN_PANCHAYAT  IF executed  " + quantity);
                    } else if (splEntitlementRule.isHillyArea()
                            && entitlementClassification
                            .equals(HILL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "HILL_AREA  IF executed  " + quantity);
                    } else if (splEntitlementRule.isSplArea()
                            && entitlementClassification
                            .equals(SPECIAL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "SPECIAL_AREA  IF executed  " + quantity);
                    } else {
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "  NONE OF THE AREA IS SATISFIED SET quantity = 0 ");
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "checkFpsClassification : Skipping rule ");
                        quantity = 0.0;
                        continue;
                    }
                    if (splEntitlementRule.isAdd()) {
                        entitledQty += quantity;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "isAdd  IF executed  " + quantity);
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "splEntitlementRule satisfied. Hence break the loop");
                        ruleSatisfied = true;
                        break;
                    } else {
                        entitledQty = quantity;
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "isAdd  else executed  " + quantity);
                        Util.LoggingQueue(context, "BeneficiaryTransaction", "splEntitlementRule satisfied. Hence break the loop");
                        ruleSatisfied = true;
                        break;
                    }
                }
            }
            if (!ruleSatisfied && !currentStage.equalsIgnoreCase(STAGE_GENERIC)) {
                Util.LoggingQueue(context, "BeneficiaryTransaction", "No matching spl rules found. Hence checking for generic spl rule");
                entitledQty = processGenericSpecialRule(beneficiary, product, entitledQty, nextStage);
            }
            Util.LoggingQueue(context, "BeneficiaryTransaction", "XX--------------- SPECIAL Calculation finished  entitledQty-> " + entitledQty + " -----------XX");
        }
        return entitledQty;
    }

    public String getNextStage(String currentStage) {
        Util.LoggingQueue(context, "BeneficiaryTransaction", "getNextStage called : currentStage : " + currentStage);
        String nextStage = "";
        if (currentStage.equalsIgnoreCase(STAGE_VILLAGE)) {
            nextStage = STAGE_TALUK;
        } else if (currentStage.equalsIgnoreCase(STAGE_TALUK)) {
            nextStage = STAGE_DISTRICT;
        } else if (currentStage.equalsIgnoreCase(STAGE_DISTRICT)) {
            nextStage = STAGE_GENERIC;
        }
        Util.LoggingQueue(context, "BeneficiaryTransaction", "getNextStage end : currentStage : " + currentStage + " and its nextStage : " + nextStage);
        return nextStage;
    }



    /*public double processSpecialRule(BeneficiaryDto beneficiary, long product, double entitledQty) {
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL Calculation Started , BeneID =  " +beneficiary.getId()
                + " & product = " + product + " & entitledQty  = " + entitledQty);
        FpsStoreDto fpsStore = LoginData.getInstance().getLoginData().getUserDetailDto().getFpsStore();
        String entitlementClassification = fpsStore.getEntitlementClassification();
        Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL entitlementClassification =  " +entitlementClassification );

        List<SplEntitlementRule> splEntitlementRules = null;

        if (fpsStore.getDistrictId() != null && fpsStore.getDistrictId() != 0) {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL with District ID  =  " +fpsStore.getDistrictId() );

            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndDistrict(product, beneficiary.getCardTypeId(), fpsStore.getDistrictId());
        }

        if (splEntitlementRules == null || splEntitlementRules.isEmpty()) {
            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL with out District ID   "  );

            splEntitlementRules = FPSDBHelper.getInstance(context).findByGroupAndCardTypeAndNullDistrict(product, beneficiary.getCardTypeId());
        }

        if (splEntitlementRules == null || splEntitlementRules.size() == 0) {
            return entitledQty;
        }

        if (splEntitlementRules != null) {

            double quantity = 0.0;
            for (SplEntitlementRule splEntitlementRule : splEntitlementRules) {
                // Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL Details- >" + splEntitlementRule);
                Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL splEntitlementRule in Iteration  " + splEntitlementRule  );

                boolean checkArea = false;
                boolean checkCylinderCount = false;
                boolean checkFpsClassification = false;

                if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0) {
                    checkArea = true;
                }

                if (splEntitlementRule.getCylinderCount() != -1) {
                    checkCylinderCount = true;
                }

                if (splEntitlementRule.isVillagePanchayat()
                        || splEntitlementRule.isCityHeadQuarter()
                        || splEntitlementRule.isMunicipality()
                        || splEntitlementRule.isTownPanchayat()
                        || splEntitlementRule.isHillyArea()
                        || splEntitlementRule.isSplArea()) {
                    checkFpsClassification = true;
                }

                if (checkArea) {
                    if (splEntitlementRule.getDistrictId() != null && splEntitlementRule.getDistrictId() != 0 && splEntitlementRule.getDistrictId() == fpsStore.getDistrictId()) {
                        if (splEntitlementRule.getTalukId() == null || splEntitlementRule.getTalukId() == 0 || splEntitlementRule.getTalukId() == fpsStore.getTalukId() &&
                                splEntitlementRule.getVillageId() == null || splEntitlementRule.getVillageId() == 0 || splEntitlementRule.getVillageId() == fpsStore.getVillageId()) {
                            quantity = splEntitlementRule.getQuantity();
                        } else {
                            quantity = 0.0;
                        }
                    }
                }
                if (checkCylinderCount) {

                    if (beneficiary.getNumOfCylinder() == splEntitlementRule.getCylinderCount()) {
                        quantity = splEntitlementRule.getQuantity();
                    } else {
                        quantity = 0.0;
                    }
                }

                if (checkFpsClassification) {
                    if (splEntitlementRule.isVillagePanchayat()
                            && entitlementClassification
                            .equals(VILLAGE_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else if (splEntitlementRule.isCityHeadQuarter()
                            && entitlementClassification
                            .equals(HEAD_QUARTER)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else if (splEntitlementRule.isMunicipality()
                            && entitlementClassification
                            .equals(MUNICIPALITY)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else if (splEntitlementRule.isTownPanchayat()
                            && entitlementClassification
                            .equals(TOWN_PANCHAYAT)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else if (splEntitlementRule.isHillyArea()
                            && entitlementClassification
                            .equals(HILL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else if (splEntitlementRule.isSplArea()
                            && entitlementClassification
                            .equals(SPECIAL_AREA)) {
                        quantity = splEntitlementRule.getQuantity();
                    } else {
                        quantity = 0.0;
                    }
                }

                if (splEntitlementRule.isAdd()) {
                    entitledQty += quantity;
                } else {
                    entitledQty = quantity;
                }
            }

            Util.LoggingQueue(context, "BeneficiarySalesQRTransaction", "SPECIAL Calculation finished  entitledQty-> " + entitledQty);
        }
        return entitledQty;
    }*/
}
