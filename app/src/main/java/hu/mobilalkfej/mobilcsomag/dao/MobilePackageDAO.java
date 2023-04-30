package hu.mobilalkfej.mobilcsomag.dao;

import java.util.List;

import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public interface MobilePackageDAO {
    void getAllPackages(FirestoreCallback firestoreCallback);

    void getAllCustomPackages(FirestoreCallback firestoreCallback);
    boolean addNewPackage(MobilePackage mobilePackage);

    boolean deletePackage(MobilePackage mobilePackage, FirestoreCallback firestoreCallback);
}

