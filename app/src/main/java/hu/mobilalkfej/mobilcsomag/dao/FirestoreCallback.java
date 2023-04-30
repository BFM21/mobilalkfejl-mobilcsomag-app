package hu.mobilalkfej.mobilcsomag.dao;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public interface FirestoreCallback {
    void onCreate(boolean value);
    void onCallback(ArrayList<MobilePackage> packages);
}
