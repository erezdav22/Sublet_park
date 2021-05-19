package com.example.subletpark;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class Parking_Class {
    private String id;
    private String address;
    private String owner_name;
    private String price;
    private String start_date;
    private String end_date;
    private String uri;
    private String desc;

    public Parking_Class(String id, String address, String price, String start_date, String end_date, String uri, String desc) {
        this.id = id;
        this.address = address;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.uri = uri;
        this.desc = desc;
    }

    public Parking_Class(String owner_name, String id,String address, String price, String start_date, String end_date, String uri, String desc) {
        this.address = address;
        this.price = price;
        this.start_date = start_date;
        this.end_date = end_date;
        this.uri = uri;
        this.desc = desc;
        this.id=id;
        this.owner_name=owner_name;
    }


    public Parking_Class(){

    }

    public String getOwner_name() {
        return owner_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getUri() {
        return uri;
    }

    public String getDesc() {
        return desc;
    }
}
