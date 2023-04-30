package hu.mobilalkfej.mobilcsomag.views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.adapters.MobilePackageAdapter;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.models.User;

public class DashboardActivity extends AppCompatActivity {


    private PersistedSettings persistedSettings;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private User user;

    private ConstraintLayout constraintLayout;
    private  TextView currentPackageName;
    private TextView currentPackageDescription;
    private TextView noCustomPackageTitle;
    private TextView customPackageTitle;
    private CardView packageInfo;
    private CardView createPackage;
    private RecyclerView packageList;
    private RecyclerView customPackageList;
    private ImageView profilePicture;

    private ScrollView mainView;

    private MobilePackageDAO mobilePackageDAO;
    private PartialPackageDAO partialPackageDAO;
    private ArrayList<MobilePackage> packages;
    private ArrayList<MobilePackage> customPackages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_layout);
        persistedSettings = PersistedSettings.getInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        customPackageList = findViewById(R.id.customPackageRecyclerView);
        noCustomPackageTitle = findViewById(R.id.noPackagesTitle);
        customPackageTitle = findViewById(R.id.customPackageTitle);
        constraintLayout = findViewById(R.id.constraintLayout);
        mobilePackageDAO = new MobilePackageDAOImpl(this);
        partialPackageDAO = new PartialPackageDAOImpl(this);
        packages = new ArrayList<>();
        if(persistedSettings.getMobilePackages()!=null) {
            packages.addAll(persistedSettings.getMobilePackages());
        }
        customPackages = new ArrayList<>();
        if(persistedSettings.getCustomPackages()!=null) {
            customPackages.addAll(persistedSettings.getCustomPackages());
        }

        mainView = findViewById(R.id.main_view);
        currentPackageName = findViewById(R.id.packageName);
        currentPackageDescription = findViewById(R.id.packageDetails);
        packageInfo = findViewById(R.id.packageInfoView);
        createPackage = findViewById(R.id.packageCustomizationView);
        packageList = findViewById(R.id.packageRecyclerView);
        packageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        packageList.setAdapter(new MobilePackageAdapter(this, packages));


        profilePicture = findViewById(R.id.profilePicture);

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        createPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomPackageActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        if(persistedSettings.getCurrentUser() != null){
            checkNetwork();
            mobilePackageDAO.getAllCustomPackages(new FirestoreCallback() {
                @Override
                public void onCreate(boolean value) {

                }

                @Override
                public void onCallback(ArrayList<MobilePackage> packages) {
                    persistedSettings.getCustomPackages().clear();
                    persistedSettings.getCustomPackages().addAll(packages);
                    persistedSettings.updateCurrentUser(new FirestoreCallback() {
                        @Override
                        public void onCreate(boolean value) {

                        }

                        @Override
                        public void onCallback(ArrayList<MobilePackage> packages) {
                            persistedSettings.getCurrentUser().getMobilePackages().clear();
                            persistedSettings.getCurrentUser().getMobilePackages().addAll(packages);
                            updateUI((ArrayList<MobilePackage>) persistedSettings.getCustomPackages(), packages);
                        }
                    });

                }
            });

        }else{
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }


        if(persistedSettings.getInternetPackages().isEmpty()){
            partialPackageDAO.getAllInternetPackages();
        }

        if(persistedSettings.getCallPackages().isEmpty()){
            partialPackageDAO.getAllCallPackages();
        }

        if(persistedSettings.getMessagePackages().isEmpty()){
            partialPackageDAO.getAllMessagePackages();
        }

        if(persistedSettings.getMobilePackages().isEmpty()){
            mobilePackageDAO.getAllPackages(new FirestoreCallback() {
                @Override
                public void onCreate(boolean value) {

                }

                @Override
                public void onCallback(ArrayList<MobilePackage> packages) {
                    persistedSettings.setMobilePackages(packages);
                }
            });
        }
    }

    private void updateUI(ArrayList<MobilePackage> customPackages, ArrayList<MobilePackage> userPackages) {

        customPackageList = findViewById(R.id.customPackageRecyclerView);
        customPackageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        customPackageList.setAdapter(new MobilePackageAdapter(this, (ArrayList<MobilePackage>) customPackages));
        if(customPackages.isEmpty()){
            customPackageList.setVisibility(View.GONE);
            noCustomPackageTitle.setVisibility(View.VISIBLE);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.customPackageTitle,ConstraintSet.START,R.id.main_view,ConstraintSet.START,5);
            constraintSet.connect(R.id.customPackageTitle,ConstraintSet.TOP,R.id.noPackagesTitle,ConstraintSet.BOTTOM,50);
            constraintSet.applyTo(constraintLayout);
        }else{


            customPackageList.setVisibility(View.VISIBLE);
            noCustomPackageTitle.setVisibility(View.GONE);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.customPackageTitle,ConstraintSet.START,R.id.main_view,ConstraintSet.START,5);
            constraintSet.connect(R.id.customPackageTitle,ConstraintSet.TOP,R.id.customPackageRecyclerView,ConstraintSet.BOTTOM,0);
            constraintSet.applyTo(constraintLayout);
        }

        if(userPackages.isEmpty()){
            currentPackageName.setText(R.string.package_info_default_title);
            currentPackageDescription.setText(R.string.package_info_default_description);
            packageInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }else{
            MobilePackage mobilePackage = new MobilePackage();
            for(MobilePackage p: userPackages){
                for(String phoneNumber: persistedSettings.getCurrentUser().getPhoneNumber()){
                    if(p.getId().equals(persistedSettings.getCurrentUser().getPhoneNumberPackageDict().get(phoneNumber))){
                        mobilePackage = p;
                        break;
                    }
                }
            }
            currentPackageName.setText(mobilePackage.getName());
            currentPackageDescription.setText(mobilePackage.getDescription());
            packageInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), PackageActivity.class);
                    intent.putExtra("id", userPackages.get(0).getId());
                    startActivity(intent);
                }
            });
        }
    }
    private void checkNetwork(){
        if(!isNetworkAvailable()){
            Snackbar.make(mainView, getString(R.string.no_network_message), Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.again, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkNetwork();
                        }
                    })
                    .show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
