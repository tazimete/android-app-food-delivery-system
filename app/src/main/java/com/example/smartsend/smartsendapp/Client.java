package com.example.smartsend.smartsendapp;

/**
 * Created by AGM TAZIM on 12/31/2015.
 */
public class Client {
    private int id;
    private String email = null, password = null, companyName = null, companyPostalCode = null, companyUnitNumber = null, location = null, contactNumber=null, billingAddress = null, contactPersonName = null, contactPersonNumber = null, contactPersonEmail=null, createdDate =null,clientType = null;

    //Consrtuctor
    public Client(){

    }

    public Client(String email, String password){
        this.email = email;
        this.password = password;
    }

    //Set id
    public void setId(int id) {
        this.id = id;
    }

    //Get id
    public int getId() {
        return id;
    }

    //Set password
    public void setPassword(String password) {
        this.password = password;
    }

    //Get password
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyPostalCode() {
        return companyPostalCode;
    }

    public void setCompanyPostalCode(String companyPostalCode) {
        this.companyPostalCode = companyPostalCode;
    }

    public String getCompanyUnitNumber() {
        return companyUnitNumber;
    }

    public void setCompanyUnitNumber(String companyUnitNumber) {
        this.companyUnitNumber = companyUnitNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonNumber() {
        return contactPersonNumber;
    }

    public void setContactPersonNumber(String contactPersonNumber) {
        this.contactPersonNumber = contactPersonNumber;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }


    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

}//End of Client Class
