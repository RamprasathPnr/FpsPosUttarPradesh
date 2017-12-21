package com.omneagate.Util;

import com.omneagate.DTO.BeneficiaryMemberDto;
import com.omneagate.activity.GlobalAppState;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by user1 on 3/9/15.
 */
public class AddressForBeneficiary {

    public static String addressForBeneficiary(BeneficiaryMemberDto memberDto){
        String address = "";
        if (!GlobalAppState.language.equalsIgnoreCase("hi")) {
            if (StringUtils.isNotEmpty(memberDto.getAddressLine1())) {
                address = memberDto.getAddressLine1();
            }
            if (StringUtils.isNotEmpty(memberDto.getAddressLine2())) {
                address = address + "," + memberDto.getAddressLine2();
            }
            if (StringUtils.isNotEmpty(memberDto.getAddressLine3())) {
                address = address + "," + memberDto.getAddressLine3();
            }
            if (StringUtils.isNotEmpty(memberDto.getAddressLine4())) {
                address = address + "," + memberDto.getAddressLine4();
            }
            if (StringUtils.isNotEmpty(memberDto.getAddressLine5())) {
                address = address + "," + memberDto.getAddressLine5();
            }
            if (StringUtils.isNotEmpty(memberDto.getPincode())) {
                address = address + "-" + memberDto.getPincode();
            }
        } else {
            if (StringUtils.isNotEmpty(memberDto.getLocalAddressLine1())) {
                address = memberDto.getLocalAddressLine1();
            }
            if (StringUtils.isNotEmpty(memberDto.getLocalAddressLine2())) {
                address = address + "-" + memberDto.getLocalAddressLine2();
            }
            if (StringUtils.isNotEmpty(memberDto.getLocalAddressLine3())) {
                address = address + "-" + memberDto.getLocalAddressLine3();
            }
            if (StringUtils.isNotEmpty(memberDto.getLocalAddressLine4())) {
                address = address + "-" + memberDto.getLocalAddressLine4();
            }
            if (StringUtils.isNotEmpty(memberDto.getLocalAddressLine5())) {
                address = address + "-" + memberDto.getLocalAddressLine5();
            }
            if (StringUtils.isNotEmpty(memberDto.getPincode())) {
                address = address + "-" + memberDto.getPincode();
            }
        }
        return  address;
    }
}
