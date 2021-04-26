package com.example.subletpark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
public class Parking_Class {
    private String id;
    private String city;
    private String street;
    private String street_num;
    private String price;
    private String start_date;
    private String end_date;
    private String uri;
    private String desc;

    public Parking_Class(String id, String city, String street, String street_num, String price, String start_date, String end_date, String uri, String desc) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.street_num = street_num;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.uri = uri;
        this.desc = desc;
    }
}
