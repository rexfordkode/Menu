package com.example.moftech.smartmenu.Model;

public class User {

  private  String Name;
  private  String  Password;
  private  String Phone;
  private  String IsStaff;
  private  String secureCode;
  private  String homeAddress;
  private  Object balance;

  public  User(){
  }

  public User(String name, String password, String phone, String isStaff, String secureCode, String homeAddress, String balance) {
    Name = name;
    Password = password;
    Phone = phone;
    IsStaff = isStaff;
    this.secureCode = secureCode;
    this.homeAddress = homeAddress;
    this.balance = balance;
  }

  public String getName() {
    return Name;
  }

  public void setName(String name) {
    Name = name;
  }

  public String getPassword() {
    return Password;
  }

  public void setPassword(String password) {
    Password = password;
  }

  public String getPhone() {
    return Phone;
  }

  public void setPhone(String phone) {
    Phone = phone;
  }

  public String getIsStaff() {
    return IsStaff;
  }

  public void setIsStaff(String isStaff) {
    IsStaff = isStaff;
  }

  public String getSecureCode() {
    return secureCode;
  }

  public void setSecureCode(String secureCode) {
    this.secureCode = secureCode;
  }

  public String getHomeAddress() {
    return homeAddress;
  }

  public void setHomeAddress(String homeAddress) {
    this.homeAddress = homeAddress;
  }

  public Object getBalance() {
    return balance;
  }

  public void setBalance(Object balance) {
    this.balance = balance;
  }
}
