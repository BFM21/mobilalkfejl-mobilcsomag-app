package hu.mobilalkfej.mobilcsomag.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

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

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private UserDAO userDAO;
    private MobilePackageDAO mobilePackageDAO;
    private PartialPackageDAO partialPackageDAO;

    private PersistedSettings persistedSettings;

    private AnimationDrawable iconAnimation;
    private static final int DELAY_TIME = 3000;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        persistedSettings = PersistedSettings.getInstance();
        userDAO = new UserDAOImpl(this);
        mobilePackageDAO = new MobilePackageDAOImpl(this);
        partialPackageDAO = new PartialPackageDAOImpl(this);
        ImageView imageView = findViewById(R.id.icon_animation);
        int nightModeFlags =
                this.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                imageView.setBackgroundResource(R.drawable.app_icon_animation_night);
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                imageView.setBackgroundResource(R.drawable.app_icon_animation_day);
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                imageView.setBackgroundResource(R.drawable.app_icon_animation_day);
                break;
        }

        iconAnimation = (AnimationDrawable) imageView.getBackground();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, DELAY_TIME);
    }


    @Override
    protected void onStart() {
        super.onStart();
        iconAnimation.start();
        if(persistedSettings.getCurrentUser() != null){
            mobilePackageDAO.getAllCustomPackages(new FirestoreCallback() {
                @Override
                public void onCreate(boolean value) {

                }

                @Override
                public void onCallback(ArrayList<MobilePackage> packages) {
                    persistedSettings.setCustomPackages(packages);
                }
            });
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

}
