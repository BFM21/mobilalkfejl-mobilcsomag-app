package hu.mobilalkfej.mobilcsomag.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAO;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.PartialPackage;
import hu.mobilalkfej.mobilcsomag.models.User;

public class PersistedSettings {
    private User currentUser;


    private List<PartialPackage> internetPackages;
    private List<PartialPackage> callPackages;
    private List<PartialPackage> messagePackages;
    private List<MobilePackage> mobilePackages;

    private List<MobilePackage> customPackages;
    private static PersistedSettings instance;

    private MobilePackageDAO mobilePackageDAO;

    public PersistedSettings() {
        currentUser = new User();
        internetPackages = new ArrayList<>();
        callPackages = new ArrayList<>();
        messagePackages = new ArrayList<>();
        mobilePackages = new ArrayList<>();
        customPackages = new ArrayList<>();
        updateCurrentUser(new FirestoreCallback() {
            @Override
            public void onCreate(boolean value) {

            }

            @Override
            public void onCallback(ArrayList<MobilePackage> packages) {
                currentUser.getMobilePackages().clear();
                currentUser.getMobilePackages().addAll(packages);
            }
        });

    }

    public static PersistedSettings getInstance() {
        if(instance == null){
            instance = new PersistedSettings();
        }
        return instance;
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }



    public void updateCurrentUser(FirestoreCallback firestoreCallback){
        currentUser = new User();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = mAuth.getCurrentUser();
        if(currentFirebaseUser != null){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(currentFirebaseUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            currentUser.setUid(currentFirebaseUser.getUid());
                            currentUser.setFirstName(document.getString("first_name"));
                            currentUser.setLastName(document.getString("last_name"));
                            currentUser.setEmail(currentFirebaseUser.getEmail());
                            currentUser.getPhoneNumber().clear();
                            for(String d : document.getString("phone").split(";")){
                                currentUser.getPhoneNumber().add(d);
                            }


                            ArrayList<MobilePackage> packages = new ArrayList<>();
                            if(document.getString("packages") != null) {
                                for(String d : document.getString("packages").split(";")){

                                    for(MobilePackage p : PersistedSettings.getInstance().getMobilePackages()){
                                        if(p.getId().equals(d)){
                                            packages.add(p);
                                        }
                                    }

                                    for(MobilePackage p : PersistedSettings.getInstance().getCustomPackages()){

                                        if(p.getId().equals(d)){
                                            packages.add(p);
                                        }
                                    }

                                }


                            }
                            for(String d : document.getString("connections").split(";")){
                                String[] splitD = d.split("-");
                                for(MobilePackage p: packages){
                                    if(p.getId().equals(splitD[1])){
                                        currentUser.getPhoneNumberPackageDict().put(splitD[0],p.getId());
                                    }
                                }

                            }

                            firestoreCallback.onCallback(packages);
                            //currentUser.getMobilePackages().addAll(packages);
                        } else {
                            Log.d(TAG, "No such document");
                            currentUser = null;
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        currentUser = null;
                    }
                }
            });
        }else {
            currentUser = null;
        }
    }

    public List<PartialPackage> getInternetPackages() {
        return internetPackages;
    }

    public void setInternetPackages(List<PartialPackage> internetPackages) {
        this.internetPackages = internetPackages;
    }

    public List<PartialPackage> getCallPackages() {
        return callPackages;
    }

    public void setCallPackages(List<PartialPackage> callPackages) {
        this.callPackages = callPackages;
    }

    public List<PartialPackage> getMessagePackages() {
        return messagePackages;
    }

    public void setMessagePackages(List<PartialPackage> messagePackages) {
        this.messagePackages = messagePackages;
    }

    public List<MobilePackage> getMobilePackages() {
        return mobilePackages;
    }

    public void setMobilePackages(List<MobilePackage> mobilePackages) {
        this.mobilePackages = mobilePackages;
    }

    public List<MobilePackage> getCustomPackages() {
        return customPackages;
    }

    public void setCustomPackages(List<MobilePackage> customPackages) {
        this.customPackages = customPackages;
    }

}
