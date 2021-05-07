package com.example.subletpark;

public class ParkTest {
    private String address;
    private String price;
    private String desc;


    public ParkTest() {
    }

    public ParkTest(String address, String price, String desc) {
        this.address = address;
        this.price = price;
        this.desc = desc;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }

    public String getDesc() {
        return desc;
    }
}
