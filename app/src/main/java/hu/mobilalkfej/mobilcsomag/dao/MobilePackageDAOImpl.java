package hu.mobilalkfej.mobilcsomag.dao;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public class MobilePackageDAOImpl implements MobilePackageDAO{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    private PersistedSettings persistedSettings;

    private  PartialPackageDAO partialPackageDAO;
    public MobilePackageDAOImpl(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.persistedSettings = PersistedSettings.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.partialPackageDAO = new PartialPackageDAOImpl(context);

    }

    @Override
    public void getAllPackages(FirestoreCallback firestoreCallback) {
        db.collection("mobile_packages")
                .orderBy("price").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<MobilePackage> tmp = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MobilePackage mobilePackage = new MobilePackage();
                                mobilePackage.setId(document.getId());
                                mobilePackage.setName(document.getString("name"));
                                mobilePackage.setDescription(document.getString("description"));
                                mobilePackage.setCall(partialPackageDAO.getPackageByReference((DocumentReference) document.get("call")));
                                if(document.get("internet") != null) {
                                    mobilePackage.setInternet(partialPackageDAO.getPackageByReference((DocumentReference) document.get("internet")));
                                }else{
                                    mobilePackage.setInternet(null);
                                }
                                mobilePackage.setMessage(partialPackageDAO.getPackageByReference((DocumentReference) document.get("message")));

                                mobilePackage.setPrice(Integer.parseInt(Objects.requireNonNull(document.get("price")).toString()));
                                mobilePackage.setMonthly(document.getBoolean("monthly"));
                                mobilePackage.setCustom(false);
                                tmp.add(mobilePackage);
                            }
                            firestoreCallback.onCallback(tmp);
                            //persistedSettings.setMobilePackages(tmp);
                        } else {
                            System.out.println("HIBA MOBILE PACKAGE");

                        }
                    }
                });
    }

    @Override
    public void getAllCustomPackages(FirestoreCallback firestoreCallback) {
        try {
            db.collection("custom_mobile_packages")
                    .whereEqualTo("uid", persistedSettings.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                ArrayList<MobilePackage> tmp = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    MobilePackage mobilePackage = new MobilePackage();
                                    mobilePackage.setId(document.getString("id"));
                                    mobilePackage.setName(document.getString("name"));
                                    mobilePackage.setDescription(document.getString("description"));
                                    if (document.get("internet") != null) {
                                        mobilePackage.setInternet(partialPackageDAO.getPackageByReference((DocumentReference) document.get("internet")));
                                    } else {
                                        mobilePackage.setInternet(null);
                                    }
                                    if (document.get("call") != null) {
                                        mobilePackage.setCall(partialPackageDAO.getPackageByReference((DocumentReference) document.get("call")));
                                    } else {
                                        mobilePackage.setCall(null);
                                    }
                                    if (document.get("message") != null) {
                                        mobilePackage.setMessage(partialPackageDAO.getPackageByReference((DocumentReference) document.get("message")));
                                    } else {
                                        mobilePackage.setMessage(null);
                                    }

                                    mobilePackage.setPrice(Integer.parseInt(Objects.requireNonNull(document.get("price")).toString()));
                                    mobilePackage.setMonthly(document.getBoolean("monthly"));
                                    mobilePackage.setCustom(true);
                                    tmp.add(mobilePackage);
                                }
                                firestoreCallback.onCallback(tmp);
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());

                            }
                        }
                    });
        }catch (Exception e){
            Log.d(TAG, "get failed with", e);
        }
    }


    @Override
    public boolean addNewPackage(MobilePackage mobilePackage) {
        Map<String, Object> packageDetails = new HashMap<>();
        packageDetails.put("uid", PersistedSettings.getInstance().getCurrentUser().getUid());
        packageDetails.put("id", mobilePackage.getId());
        packageDetails.put("name", mobilePackage.getName());
        packageDetails.put("description", mobilePackage.getDescription());
        packageDetails.put("internet", (mobilePackage.getInternet() != null ? db.document("internet_partial_packages/" + mobilePackage.getInternet().getId()) : null));
        packageDetails.put("call", (mobilePackage.getCall() != null ? db.document("call_partial_package/"+mobilePackage.getCall().getId()) : null));
        packageDetails.put("message", (mobilePackage.getMessage() != null ? db.document("message_partial_package/"+mobilePackage.getMessage().getId()) : null));
        packageDetails.put("monthly", mobilePackage.isMonthly());
        packageDetails.put("price", mobilePackage.getPrice());


        db.collection("custom_mobile_packages")
                .document(String.valueOf(System.currentTimeMillis()))
                .set(packageDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding package", e);
                    }
                });

        return true;
    }

    @Override
    public boolean deletePackage(MobilePackage mobilePackage, FirestoreCallback firestoreCallback) {
        db.collection("custom_mobile_packages")
                .whereEqualTo("id", mobilePackage.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                firestoreCallback.onCreate(true);
                            }
                        }
                    }
                });
        getAllCustomPackages(new FirestoreCallback() {
            @Override
            public void onCreate(boolean value) {

            }

            @Override
            public void onCallback(ArrayList<MobilePackage> packages) {
                persistedSettings.getCustomPackages().clear();
                persistedSettings.getCustomPackages().addAll(packages);
            }
        });

        return true;
    }


    private boolean partialPackagesAreLoaded(){
        return !persistedSettings.getMessagePackages().isEmpty() && !persistedSettings.getCallPackages().isEmpty() && !persistedSettings.getInternetPackages().isEmpty();
    }

    private void loadPartialPackages(){
        partialPackageDAO.getAllInternetPackages();
        partialPackageDAO.getAllCallPackages();
        partialPackageDAO.getAllMessagePackages();
    }

}
