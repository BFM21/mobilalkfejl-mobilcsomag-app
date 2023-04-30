package hu.mobilalkfej.mobilcsomag.models;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String uid;
    private String photoUrl;
    private String firstName;
    private String lastName;
    private ArrayList<String> phoneNumber;
    private String email;
    private ArrayList<MobilePackage> mobilePackages;

    private Map<String, String> phoneNumberPackageDict;

    public User() {
        mobilePackages = new ArrayList<>();
        phoneNumber = new ArrayList<>();
        phoneNumberPackageDict = new HashMap<>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<MobilePackage> getMobilePackages() {
        return mobilePackages;
    }

    public void setPhoneNumber(ArrayList<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setMobilePackages(ArrayList<MobilePackage> mobilePackages) {
        this.mobilePackages = mobilePackages;
    }

    public Map<String, String> getPhoneNumberPackageDict() {
        return phoneNumberPackageDict;
    }

    public void setPhoneNumberPackageDict(Map<String, String> phoneNumberPackageDict) {
        this.phoneNumberPackageDict = phoneNumberPackageDict;
    }
}
