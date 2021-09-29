package com.comp90018.assignment2.modules.location.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
/**
 * result bean for open street map api
 *
 * @author xiaotian li
 */
public class OpenStreetMapResultBean {

    @JSONField(name = "type")
    private String type;
    @JSONField(name = "licence")
    private String licence;
    @JSONField(name = "features")
    private List<FeaturesDTO> features;

    @NoArgsConstructor
    @Data
    public static class FeaturesDTO {
        @JSONField(name = "type")
        private String type;
        @JSONField(name = "properties")
        private PropertiesDTO properties;
        @JSONField(name = "bbox")
        private List<Double> bbox;
        @JSONField(name = "geometry")
        private GeometryDTO geometry;

        @NoArgsConstructor
        @Data
        public static class PropertiesDTO {
            @JSONField(name = "place_id")
            private String placeId;
            @JSONField(name = "osm_type")
            private String osmType;
            @JSONField(name = "osm_id")
            private String osmId;
            @JSONField(name = "place_rank")
            private String placeRank;
            @JSONField(name = "category")
            private String category;
            @JSONField(name = "type")
            private String type;
            @JSONField(name = "importance")
            private String importance;
            @JSONField(name = "addresstype")
            private String addresstype;
            @JSONField(name = "name")
            private Object name;
            @JSONField(name = "display_name")
            private String displayName;
            @JSONField(name = "address")
            private AddressDTO address;

            @NoArgsConstructor
            @Data
            public static class AddressDTO {
                @JSONField(name = "house_number")
                private String houseNumber;
                @JSONField(name = "road")
                private String road;
                @JSONField(name = "suburb")
                private String suburb;
                @JSONField(name = "city")
                private String city;
                @JSONField(name = "county")
                private String county;
                @JSONField(name = "state")
                private String state;
                @JSONField(name = "postcode")
                private String postcode;
                @JSONField(name = "country")
                private String country;
                @JSONField(name = "country_code")
                private String countryCode;
            }
        }

        @NoArgsConstructor
        @Data
        public static class GeometryDTO {
            @JSONField(name = "type")
            private String type;
            @JSONField(name = "coordinates")
            private List<Double> coordinates;
        }
    }
}
