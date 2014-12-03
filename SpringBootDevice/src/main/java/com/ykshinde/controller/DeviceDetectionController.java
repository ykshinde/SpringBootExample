package com.ykshinde.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.wurfl.core.WURFLEngine;

import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DeviceDetectionController {

	@Resource(name="WURFLEngine")
	WURFLEngine engine;
	
    @RequestMapping("/detect-device")
    public @ResponseBody String detectDevice(Device device, HttpServletRequest request) {
    	
		net.sourceforge.wurfl.core.Device device2 = engine.getDeviceForRequest(request);
		
		StringBuffer deviceCapabilities = new StringBuffer();

		deviceCapabilities.append(" DEVICE_ID : ").append(device2.getId()).append("<br>")
		.append(" DEVICE_OS : ").append(device2.getCapability("device_os")).append("<br>")
		.append(" DEVICE_OS_VERSION : ").append(device2.getCapability("device_os_version")).append("<br>")
		.append(" IS_TABLET : ").append(device2.getCapability("is_tablet")).append("<br>")
		.append(" IS_WIRELESS_DEVICE : ").append(device2.getCapability("is_wireless_device")).append("<br>")
		.append(" MOBILE_BROWSER : ").append(device2.getCapability("mobile_browser")).append("<br>")
		.append(" MOBILE_BROWSER_VERSION : ").append(device2.getCapability("mobile_browser_version")).append("<br>")
		.append(" POINTING_METHOD : ").append(device2.getCapability("pointing_method")).append("<br>")
		.append(" PREFERRED_MARKUP : ").append(device2.getCapability("preferred_markup")).append("<br>")
		.append(" RESOLUTION_HEIGHT : ").append(device2.getCapability("resolution_height")).append("<br>")
		.append(" RESOLUTION_WIDTH : ").append(device2.getCapability("resolution_width")).append("<br>")
		.append(" UX_FULL_DESKTOP : ").append(device2.getCapability("ux_full_desktop")).append("<br>")
		.append(" XHTML_SUPPORT_LEVEL : ").append(device2.getCapability("xhtml_support_level")).append("<br>");
		
    	
        String deviceType = "unknown";
        if (device.isNormal()) {
            deviceType = "normal";
        } else if (device.isMobile()) {
            deviceType = "mobile";
        } else if (device.isTablet()) {
            deviceType = "tablet";
        }
        
        deviceCapabilities.append(" DEVICE TYPE (SPRING BOOT) : ").append(deviceType);
        
        return deviceCapabilities.toString();
    }

}