package com.omneagate.DTO.MantraDto;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "DeviceInfo")
public class DeviceInfo {

    public DeviceInfo() {
    }

    @Attribute(name = "srno")
    public String srno;

    @Attribute(name = "dpId")
    public String dpId;

    @Attribute(name = "rdsId")
    public String rdsId;

    @Attribute(name = "rdsVer")
    public String rdsVer;

    @Attribute(name = "dc")
    public String dc;

    @Attribute(name = "mi")
    public String mi;

    @Attribute(name = "mc")
    public String mc;

}
