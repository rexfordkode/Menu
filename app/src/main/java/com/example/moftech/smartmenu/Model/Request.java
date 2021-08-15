package com.example.moftech.smartmenu.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String addressTblNum;
    private String total;
    private String status;
    private String comments;
    private String paymentMethod;
    private String paymentState;
    private String LatLgn;
    private String restaurantId;
    private List<Order> foods;  //list of food order

    public Request() {
    }

    public Request(String phone, String name, String addressTblNum, String total, String status, String comments, String paymentMethod, String paymentState, String latLgn, String restaurantId, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.addressTblNum = addressTblNum;
        this.total = total;
        this.status = status;
        this.comments = comments;
        this.paymentMethod = paymentMethod;
        this.paymentState = paymentState;
        LatLgn = latLgn;
        this.restaurantId = restaurantId;
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressTblNum() {
        return addressTblNum;
    }

    public void setAddressTblNum(String addressTblNum) {
        this.addressTblNum = addressTblNum;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getLatLgn() {
        return LatLgn;
    }

    public void setLatLgn(String latLgn) {
        LatLgn = latLgn;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}

