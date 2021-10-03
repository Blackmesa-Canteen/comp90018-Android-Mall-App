package com.comp90018.assignment2.modules.location.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * hold coordinate
 *
 * @author Xiaotian Li
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class LocationBean implements Serializable {
    private double latitude;
    private double longitude;

    private String textAddress;

    // if the bean has detailed info below, then this value will be true
    private boolean gotDetailedAddressInfo;
    private String road;
    private String suburb;
    private String city;
    private String state;
    private String postcode;
    private String country;
    private String countryCode;

    private int coordinateSystemType;
}
