package hu.mobilalkfej.mobilcsomag.dao;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.models.PartialPackage;

public class PartialPackageDAOImpl implements PartialPackageDAO{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    private PersistedSettings persistedSettings;

    private  PartialPackageDAO partialPackageDAO;

    public PartialPackageDAOImpl(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.persistedSettings = PersistedSettings.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }


    @Override
    public PartialPackage getPackageByReference(DocumentReference ref) {
        PartialPackage partialPackage = new PartialPackage();
        db.document(ref.getPath()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {

                        partialPackage.setId(document.getId());
                        partialPackage.setName(document.getString("name"));
                        partialPackage.setDescription(document.getString("description"));
                        partialPackage.setAmount(document.get("amount", Integer.TYPE));
                        partialPackage.setPrice(document.get("price", Integer.TYPE));
                        partialPackage.setMonthly(document.get("monthly", Boolean.TYPE));
                    }
                }
            }
        });
        return partialPackage;
    }

    @Override
    public void getAllInternetPackages() {

        db.collection("internet_partial_packages")
                .orderBy("price").get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<PartialPackage> tmp = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PartialPackage partialPackage = new PartialPackage();
                                partialPackage.setId(document.getId());
                                partialPackage.setName(document.getString("name"));
                                partialPackage.setDescription(document.getString("description"));
                                partialPackage.setAmount(document.get("amount", Integer.TYPE));
                                partialPackage.setPrice(document.get("price", Integer.TYPE));
                                partialPackage.setMonthly(document.get("monthly", Boolean.TYPE));
                                tmp.add(partialPackage);
                            }
                            persistedSettings.setInternetPackages(tmp);
                        } else {
                            System.out.println("HIBA INTERNET PARTIAL PACKAGE");

                        }
                    }
                });
    }

    @Override
    public void getAllCallPackages() {
        db.collection("call_partial_package")
                .orderBy("price").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<PartialPackage> tmp = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                PartialPackage partialPackage = new PartialPackage();
                                partialPackage.setId(document.getId());
                                partialPackage.setName(document.getString("name"));
                                partialPackage.setDescription(document.getString("description"));
                                partialPackage.setAmount(document.get("amount", Integer.TYPE));
                                partialPackage.setPrice(document.get("price", Integer.TYPE));
                                partialPackage.setMonthly(document.get("monthly", Boolean.TYPE));
                                tmp.add(partialPackage);
                            }
                            persistedSettings.setCallPackages(tmp);
                        } else {
                            System.out.println("HIBA CALL PARTIAL PACKAGE");

                        }
                    }
                });
    }

    @Override
    public void getAllMessagePackages() {

        db.collection("message_partial_package")
                .orderBy("price").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<PartialPackage> tmp = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                PartialPackage partialPackage = new PartialPackage();
                                partialPackage.setId(document.getId());
                                partialPackage.setName(document.getString("name"));
                                partialPackage.setDescription(document.getString("description"));
                                partialPackage.setAmount(document.get("amount", Integer.TYPE));
                                partialPackage.setPrice(document.get("price", Integer.TYPE));
                                partialPackage.setMonthly(document.get("monthly", Boolean.TYPE));
                                tmp.add(partialPackage);
                            }
                            persistedSettings.setMessagePackages(tmp);
                        } else {
                            System.out.println("HIBA MESSAGE PARTIAL PACKAGE");
                        }
                    }
                });
    }


}
