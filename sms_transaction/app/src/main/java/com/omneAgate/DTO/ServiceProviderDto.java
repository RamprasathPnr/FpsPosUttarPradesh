package com.omneagate.DTO;

import lombok.Data;

@Data
public class ServiceProviderDto extends BaseDto {

    /*Unique id*/
    public long id;

    /**
     * servicer provider name.
     */
    public String providerName;

    /**
     * whom initiated the service provider
     */
    public long createdBy;

    /**
     * service provider created time in this table
     */
    public long createdDate;

    /* sms provider status only one should be enabled over the time */
    public boolean status;

    /**
     * service provider created time in this table
     */
    public long modifiedDate;

}