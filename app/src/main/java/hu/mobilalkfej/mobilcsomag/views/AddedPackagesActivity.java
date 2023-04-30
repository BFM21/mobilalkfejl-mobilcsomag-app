package hu.mobilalkfej.mobilcsomag.views;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.adapters.ListItemAdapter;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.utils.UIUpdateCallback;


public class AddedPackagesActivity extends AppCompatActivity {

    private PersistedSettings persistedSettings;

    private TextView noPackages;
    private RecyclerView packageList;
    private ListItemAdapter adapter;

    private ArrayList<MobilePackage> packages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.added_packages_layout);
        noPackages = findViewById(R.id.no_packages_textview);
        persistedSettings = PersistedSettings.getInstance();
        packageList = findViewById(R.id.packageList);
        getPackages();
        packageList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ListItemAdapter(this, packages, new UIUpdateCallback() {
            @Override
            public void onCallback() {
                updateUI();
            }
        });

        packageList.setAdapter(adapter);



    }

    private void getPackages(){
        packages = (ArrayList<MobilePackage>) persistedSettings.getCustomPackages();
    }

    private void updateUI(){
        if(persistedSettings.getCustomPackages().isEmpty()){
            packageList.setVisibility(View.GONE);
            noPackages.setVisibility(View.VISIBLE);
        }else{
            packageList.setVisibility(View.VISIBLE);
            noPackages.setVisibility(View.GONE);
        }
        getPackages();
        packageList.setAdapter(adapter);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
