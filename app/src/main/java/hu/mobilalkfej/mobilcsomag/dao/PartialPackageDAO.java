package hu.mobilalkfej.mobilcsomag.dao;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import hu.mobilalkfej.mobilcsomag.models.PartialPackage;

public interface PartialPackageDAO {
    PartialPackage getPackageByReference(DocumentReference ref);
    void getAllInternetPackages();
    void getAllCallPackages();
    void getAllMessagePackages();
}
