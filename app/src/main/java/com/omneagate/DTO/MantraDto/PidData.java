package com.omneagate.DTO.MantraDto;



import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "PidData")
public class PidData {

    public PidData() {
    }

    @Element(name = "Resp", required = false)
    public Resp _Resp;

    @Element(name = "DeviceInfo", required = false)
    public DeviceInfo _DeviceInfo;

    @Element(name = "Hmac", required = false)
    public String hmac;

    @Element(name = "Skey", required = false)
    public String skey;

    @Element(name = "Data", required = false)
    public String data;

}
