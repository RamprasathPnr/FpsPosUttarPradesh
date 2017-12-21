package com.omneagate.Util;

import android.app.Activity;
import android.util.Log;

import com.omneagate.DTO.BeneficiaryDto;
import com.omneagate.DTO.BillItemDto;
import com.omneagate.DTO.EntitlementDTO;
import com.omneagate.DTO.EntitlementMasterRule;
import com.omneagate.DTO.PersonBasedRule;
import com.omneagate.DTO.ProductDto;
import com.omneagate.DTO.QRTransactionResponseDto;
import com.omneagate.activity.GlobalAppState;
import com.omneagate.activity.R;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Beneficiary sales transaction class
 */
public class BeneficiarySalesTransaction {

    Activity context; //Context for this class

    BeneficiaryDto beneficiary;//Beneficiary of card

    private List<BillItemDto> billItems; //Bill items bought

    private List<ProductDto> product; //List of products

    String qrCode; //qr code of user card

    public BeneficiarySalesTransaction(Activity activity) {
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
            Log.e("Error", beneficiary.toString());
            return returnQRResponse();
        }
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
        billItems = new ArrayList<BillItemDto>();
        if (beneficiary == null) {
            Util.messageBar(context, context.getString(R.string.fpsBeneficiaryMismatch));
            return false;
        } else {
            if (beneficiary.isActive()) {
                billItems = FPSDBHelper.getInstance(context).getAllBillItems(beneficiary.getId(), month.getMonthOfYear());
                return true;
            } else {
                Util.messageBar(context, context.getString(R.string.cardBlocked));
                return false;
            }
        }
    }

    //returns the qrCode response
    private QRTransactionResponseDto returnQRResponse() {
        QRTransactionResponseDto qrResponse = new QRTransactionResponseDto();
        qrResponse.setMobileNumber(beneficiary.getMobileNumber());
//        BeneficiaryMemberDto head = returnBeneficiaryName();
       /* String nameOfHead = head.getName();
        if (GlobalAppState.language.equals("ta")) {
            nameOfHead = head.getLname();
        }*/
//        qrResponse.setBeneficiaryName(nameOfHead);
        qrResponse.setUfc(beneficiary.getEncryptedUfc());
        qrResponse.setUfc(qrCode);
        qrResponse.setBenficiaryId(beneficiary.getId());
        qrResponse.setFpsId(beneficiary.getFpsId());
       /* if (GlobalAppState.language.equals("ta")) {
            qrResponse.setAddressline1(head.getLaddressLine1());
            qrResponse.setAddressline2(head.getLaddressLine2());
            qrResponse.setAddressline3(head.getLaddressLine3());
            qrResponse.setAddressline4(head.getLaddressLine4());
            qrResponse.setAddressline5(head.getLaddressLine5());
        } else {
            qrResponse.setAddressline1(head.getAddressLine1());
            qrResponse.setAddressline2(head.getAddressLine2());
            qrResponse.setAddressline3(head.getAddressLine3());
            qrResponse.setAddressline4(head.getAddressLine4());
            qrResponse.setAddressline5(head.getAddressLine5());
        }*/
        qrResponse.setEntitlementList(returnEntitlements());
        Log.e("resp", qrResponse.toString());
        return qrResponse;
    }

   /* */

    /**
     * returns the beneficiary name
     *//*
    private BeneficiaryMemberDto returnBeneficiaryName() {
        for (BeneficiaryMemberDto beneMembers : beneficiary.getFamilyMembers()) {
            if (beneMembers.getTin().endsWith("001")) {
                return beneMembers;
            }
        }
        return beneficiary.getFamilyMembers().get(0);
    }*/

    //return the entitlement of product to user
    private List<EntitlementDTO> returnEntitlements() {
        product = FPSDBHelper.getInstance(context).getAllProductDetails();
        List<EntitlementDTO> entitlements = new ArrayList<EntitlementDTO>();
        List<EntitlementMasterRule> rule = FPSDBHelper.getInstance(context).getAllEntitlementMasterRule(Long.parseLong(beneficiary.getCardTypeId()));
        Log.e("Rules", rule.toString());
        for (EntitlementMasterRule allots : rule) {
            if (!allots.isCalcRequired()) {
                entitlements.add(entitles(allots));
            } else {
                if (allots.isRegionBased()) {

                } else {
                    allots.setQuantity(findAllotment(allots.getProductId()));
                    entitlements.add(entitles(allots));
                }
                if (allots.isHasSpecialRule()) {
                    findSpecialCalculation();
                }
            }
        }
        return entitlements;
    }


    private double findAllotment(long productId) {
        PersonBasedRule rule = FPSDBHelper.getInstance(context).getAllPersonBasedRule(productId);
        int children = numberOfChildren();
        int adult = beneficiary.getNumOfAdults() - 1;//(beneficiary.getFamilyMembers().size() - children) - 1;
        if (adult <= 0) {
            adult = 0;
        }
        double adultQuantity = adult * rule.getPerAdult();
        double childQuantity = children * rule.getPerChild();
        double quantity = adultQuantity + childQuantity + rule.getMin();
        if (quantity > rule.getMax())
            quantity = rule.getMax();

        return quantity;

    }


    private void findSpecialCalculation() {
        List<EntitlementMasterRule> rule = FPSDBHelper.getInstance(context).getAllEntitlementMasterRule(Long.parseLong(beneficiary.getCardTypeId()));
    }

    private int numberOfChildren() {
        int childCount = beneficiary.getNumOfChild();
//        for (BeneficiaryMemberDto family : beneficiary.getFamilyMembers()) {
//            if (family.getCurrentAge() < 18) {
//                childCount++;
//            }
//        }
        return childCount;
    }
    /**
     * this function calculate allotment
     * by ageGroup, allotted and product id allotment will return
     */
    /**
     * private EntitlementDTO calculateAllotment(AllotmentDto allotment) {
     double quantity = 0.0;
     for (BeneficiaryMemberDto members : beneficiary.getFamilyMembers()) {
     AgeGroupDto ageGroupDto = FPSDBHelper.getInstance(context).getAgeGroup(allotment.getProductId(), members.getCurrentAge());
     quantity = quantity + ageGroupDto.getQuantity();

     }
     EntitlementDTO entitlement = returnCalculateAllotment(allotment.getProductId());
     AllotmentMappingDto allotmentMap = FPSDBHelper.getInstance(context).getAllotmentMap(allotment.getProductId(), allotment.getDistrictId(), quantity);
     double allottedQuantity = allotmentMap.getAllotedQuantity();
     if (allottedQuantity > allotment.getAllotmentLimit()) {
     allottedQuantity = allotment.getAllotmentLimit();
     }
     entitlement.setEntitledQuantity(allottedQuantity);
     double bought = productsBought(allotment.getProductId());
     entitlement.setHistory(bought);
     double current = allottedQuantity -bought;
     if (current > 0.0) {
     entitlement.setCurrentQuantity(current);
     } else {
     entitlement.setCurrentQuantity(0.0);
     }

     return entitlement;
     }*/

    /**
     * returns the value of product bought by user in current month
     *
     * @param productId
     */
    private double productsBought(long productId) {
        for (int i = 0; i < billItems.size(); i++) {
            if (billItems.get(i).getProductId() == productId) {
                return billItems.get(i).getQuantity();
            }
        }

        return 0.0;

    }

    /**
     * Return entitlement  calculation required product
     *
     * @param productId,allotmentLimit and allotCalc and user bought in this month
     */
    private EntitlementDTO returnCalculateAllotment(long productId) {
        EntitlementDTO entitlement = new EntitlementDTO();
        for (int i = 0; i < product.size(); i++) {
            if (product.get(i).getId() == productId) {
                entitlement.setProductName(product.get(i).getName());
                entitlement.setLproductName(product.get(i).getLocalProductName());
                entitlement.setProductId(product.get(i).getId());
                entitlement.setProductPrice(product.get(i).getProductPrice());
                entitlement.setLproductUnit(product.get(i).getLocalProductUnit());
                entitlement.setProductUnit(product.get(i).getProductUnit());
                return entitlement;
            }
        }
        return entitlement;

    }

    private EntitlementDTO entitles(EntitlementMasterRule allotment) {
        EntitlementDTO entitlement = returnCalculateAllotment(allotment.getProductId());
        double allottedQuantity = allotment.getQuantity();
        entitlement.setEntitledQuantity(allottedQuantity);
        double current = allottedQuantity - productsBought(allotment.getProductId());
        if (current > 0.0) {
            entitlement.setCurrentQuantity(current);
        } else {
            entitlement.setCurrentQuantity(0.0);
        }
        return entitlement;
    }
}
