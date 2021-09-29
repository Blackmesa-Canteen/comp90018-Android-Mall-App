package com.comp90018.assignment2.modules.location.bean;

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

    private int coordinateSystemType;
}
