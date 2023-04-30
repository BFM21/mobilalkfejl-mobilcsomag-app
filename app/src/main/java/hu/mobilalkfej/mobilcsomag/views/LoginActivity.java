package hu.mobilalkfej.mobilcsomag.views;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.dao.UserDAO;
import hu.mobilalkfej.mobilcsomag.dao.UserDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView registerLink;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ConstraintLayout mainView;
    private Button loginButton;
    private UserDAO userDAO;
    private MobilePackageDAO mobilePackageDAO;
    private PartialPackageDAO partialPackageDAO;
    
    private PersistedSettings persistedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        persistedSettings = PersistedSettings.getInstance();
        userDAO = new UserDAOImpl(this);
        mobilePackageDAO = new MobilePackageDAOImpl(this);
        partialPackageDAO = new PartialPackageDAOImpl(this);
        mAuth = FirebaseAuth.getInstance();
        mainView = findViewById(R.id.main_view);
        registerLink = findViewById(R.id.registerTextView);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        Spanned spanned = HtmlCompat.fromHtml("Nincs fiókod? <a href=#placeholder> Regisztrálj itt</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
        registerLink.setText(spanned);
        checkNetwork();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              if(TextUtils.isEmpty(emailEditText.getText())){
                    emailEditText.setError(getString(R.string.empty_field_error));
                }else if(TextUtils.isEmpty(passwordEditText.getText())){
                    passwordEditText.setError(getString(R.string.empty_field_error));
                }else{
                  singIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
                }

            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void singIn(String email, String password) {
        userDAO.loginUser(email, password, new FirestoreCallback() {
                    @Override
                    public void onCreate(boolean value) {
                        if(value) {
                            Intent myIntent = new Intent(getApplicationContext(), DashboardActivity.class);
                            startActivity(myIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCallback(ArrayList<MobilePackage> packages) {

                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
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
