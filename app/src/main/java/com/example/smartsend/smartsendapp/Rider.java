package com.example.smartsend.smartsendapp;

/**
 * Created by AGM TAZIM on 12/29/2015.
 */
public class Rider {

    //Declaration
    private int id, status ;
    private String email = null, password = null, name = null, bikeNumber = null, contactNumber = null, profilePicture = null, createdDate = null;

    //Constructor
    public Rider(){

    }

    public Rider(String email, String password){
        this.email = email;
        this.password = password;
    }

    //Set id
    public void setId(int id) {
        this.id = id;
    }

    //get id
    public int getId() {
        return this.id;
    }

    //Set Email
    public void setEmail(String email) {
        this.email = email;
    }

    //get email
    public String getEmail() {
        return this.email;
    }

    //Set password
    public void setPasword(String password){
        this.password = password;
    }

    //get passsword
    public String getPassword(){
        return this.password;
    }

    //Set name
    public void setName(String name){
        this.name = name;
    }

    //get name
    public String getName(){
        return this.name;
    }

    //Set biken umber
    public void setBikeNumber(String bikeNumber){
        this.bikeNumber = bikeNumber;
    }

    //get bike number
    public String getBikeNumber(){
        return this.bikeNumber;
    }

    //Set contact umber
    public void setContactNumber(String contactNumber){
        this.contactNumber = contactNumber;
    }

    //get contact number
    public String getContactNumber(){
        return this.contactNumber;
    }

    //Set profile picture
    public void setProfilePicture(String profilePicture){
        this.profilePicture = profilePicture;
    }

    //get profile picture
    public String getProfilePicture(){
        return this.profilePicture;
    }

    //Set created date
    public void setCreatedDate(String createdDate){
        this.createdDate = createdDate;
    }

    //get created Date
    public String getCreatedDate(){
        return this.createdDate;
    }

    //Set status
    public void setStatus(int status){
        this.status = status;
    }

    //get status
    public int getStatus(){
        return this.status;
    }

}
