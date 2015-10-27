package com.example.jonathanpoon.homestaymanager;

public class UserProfile{

    private String accountType;
    private String Name;
    private String StartDate;
    private String EndDate;
    private String email;;
    private String address;
    private String phone;
    private String gender;
    private boolean smoke;
    private boolean pets;
    private String distance; // distance from campus
    private String familySize;
    private String pairWith;

    /** get progile **/
    public String getAccountType(){
        return accountType;
    }
    public String getName(){
        return Name;
    }
    public String getStartDate(){
        return StartDate;
    }
    public String getEndDate(){
        return EndDate;
    }
    public String getEmail(){
        return email;
    }
    public String getAddress(){
        return address;
    }
    public String getPhone(){
        return phone;
    }
    public String getGender() {
        return gender;
    }
    public String getDistance(){
        return distance;
    }
    public boolean getSmoke(){
        return smoke;
    }
    public boolean getPets(){
        return pets;
    }
    public String getFamilySize(){
        return familySize;
    }
    public String getPairWith(){
        return pairWith;
    }

    /** set profile **/
    public void setAccountType(String accountType){
        this.accountType = accountType;
    }
    public void setName(String Name){
        this.Name = Name;
    }
    public void setStartDate(String StartDate){
        this.StartDate = StartDate;
    }
    public void setEndDate(String EndDate){
        this.EndDate = EndDate;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setSmoke(boolean smoke){
        this.smoke = smoke;
    }
    public void setPets(boolean pets){
        this.pets = pets;
    }
    public void setDistance(String distance){
        this.distance = distance;
    }
    public void setFamilySize(String familySize){
        this.familySize = familySize;
    }
    public void setPairWith(String pairWith){
        this.pairWith = pairWith;
    }
}
