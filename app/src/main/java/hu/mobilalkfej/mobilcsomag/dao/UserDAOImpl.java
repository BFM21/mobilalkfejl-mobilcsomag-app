package hu.mobilalkfej.mobilcsomag.dao;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.User;

public class UserDAOImpl implements UserDAO{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Context context;

    private PersistedSettings persistedSettings;

    public UserDAOImpl(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.persistedSettings = PersistedSettings.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public boolean registerUser(User user, String password, FirestoreCallback firestoreCallback) {

        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            user.setUid(firebaseUser.getUid());
                            addUserToDatabase(user);
                            persistedSettings.setCurrentUser(user);
                            firestoreCallback.onCreate(persistedSettings.getCurrentUser() != null);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        return persistedSettings.getCurrentUser() != null;
    }

    public void addUserToDatabase(User user){
        Map<String, String> userData = new HashMap<>();
        userData.put("uid", user.getUid());
        userData.put("first_name", user.getFirstName());
        userData.put("last_name", user.getLastName());

        String phone = "";
        for(String p : user.getPhoneNumber()){
            if(phone.equals("")){
                phone += p;
            }else{
                phone += ";" + p;
            }
        }

        userData.put("phone", phone);
        userData.put("photoUrl", user.getPhotoUrl());
        String connections = "";
        for(String p : user.getPhoneNumber()){
            if(connections.equals("")){
                connections +=  p + "-" + "null";
            }else{
                connections += ";" + p + "-" + "null";
            }
        }
        userData.put("connections", connections);

        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user", e);
                    }
                });
    }

    @Override
    public boolean loginUser(String email, String password, FirestoreCallback firestoreCallback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            persistedSettings.updateCurrentUser(new FirestoreCallback() {
                                @Override
                                public void onCreate(boolean value) {

                                }

                                @Override
                                public void onCallback(ArrayList<MobilePackage> packages) {
                                    persistedSettings.getCurrentUser().getMobilePackages().clear();
                                    persistedSettings.getCurrentUser().getMobilePackages().addAll(packages);
                                }
                            });
                            firestoreCallback.onCreate(persistedSettings.getCurrentUser() != null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return persistedSettings.getCurrentUser() != null;
    }

    @Override
    public boolean logoutUser(User user) {
        FirebaseAuth.getInstance().signOut();
        persistedSettings.setCurrentUser(null);
        return persistedSettings.getCurrentUser() == null;
    }

    @Override
    public boolean updateUser(User user, String password) {
        Map<String, String> userData = new HashMap<>();
        userData.put("first_name", user.getFirstName());
        userData.put("last_name", user.getLastName());
        String phone = "";
        for(String p : user.getPhoneNumber()){
            if(phone.equals("")){
                phone += p;
            }else{
                phone += ";" + p;
            }
        }
        userData.put("phone", phone);
        userData.put("photoUrl", user.getPhotoUrl());

        String packages = "";
        for(MobilePackage p : user.getMobilePackages()){
            if(packages.equals("")){
                packages +=  p.getId();
            }else{
                packages += ";" + p.getId();
            }
        }
        userData.put("packages", packages);

        String connections = "";
        for(String p : user.getPhoneNumber()){
            if(connections.equals("")){
                connections +=  p + "-" + user.getPhoneNumberPackageDict().get(p);
            }else{
                connections += ";" + p + "-" + user.getPhoneNumberPackageDict().get(p);
            }
        }
        userData.put("connections", connections);

        try {
            Objects.requireNonNull(mAuth.getCurrentUser()).updateEmail(user.getEmail());
            if (password != null && !password.isEmpty()) {
                Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(password);
            }
        }catch (Exception e){
            return  false;
        }

        db.collection("users")
                .document(PersistedSettings.getInstance().getCurrentUser().getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating user", e);
                    }
                });

        return true;
    }

    @Override
    public boolean deleteUser(User user, FirestoreCallback firestoreCallback) {
        FirebaseAuth.getInstance().getCurrentUser().delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@org.checkerframework.checker.nullness.qual.NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");

                            FirebaseFirestore.getInstance().collection("users").document(PersistedSettings.getInstance().getCurrentUser().getUid())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                            firestoreCallback.onCreate(logoutUser(user));
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                            Log.w(TAG, "Error deleting document", e);
                                        }
                                    });

                        }
                    }
                });
        return persistedSettings.getCurrentUser() == null;
    }
}
