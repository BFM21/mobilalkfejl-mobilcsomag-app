package hu.mobilalkfej.mobilcsomag.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.UserDAO;
import hu.mobilalkfej.mobilcsomag.dao.UserDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public class PackageActivity extends AppCompatActivity {

    private MobilePackage currentPackage;

    private TextView packageName;
    private TextView callDetails;
    private TextView internetDetails;
    private TextView messageDetails;
    private TextView priceDetails;
    private Button addOrRemoveButton;

    private PersistedSettings persistedSettings;

    private UserDAO userDAO;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_layout);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        persistedSettings = PersistedSettings.getInstance();

        for(MobilePackage p : persistedSettings.getMobilePackages()){
            if(p.getId().equals(id)){
                currentPackage = p;
            }
        }

        for(MobilePackage p : persistedSettings.getCustomPackages()){
            if(p.getId().equals(id)){
                currentPackage = p;
            }
        }

        userDAO = new UserDAOImpl(this);

        packageName = findViewById(R.id.headerTitle);
        callDetails = findViewById(R.id.callDetails);
        internetDetails = findViewById(R.id.internetDetails);
        messageDetails = findViewById(R.id.messageDetails);
        priceDetails = findViewById(R.id.priceDetails);
        addOrRemoveButton = findViewById(R.id.add_remove_button);


        packageName.setText((currentPackage.getName() == null ? getString(R.string.empty) : currentPackage.getName()));
        callDetails.setText((currentPackage.getCall() == null ? getString(R.string.empty) : currentPackage.getCall().getName()));
        internetDetails.setText((currentPackage.getInternet() == null ? getString(R.string.empty) : currentPackage.getInternet().getName()));
        messageDetails.setText((currentPackage.getMessage() == null ? getString(R.string.empty) : currentPackage.getMessage().getName()));
        priceDetails.setText(String.valueOf(currentPackage.getPrice()) + " Ft");

        updateUI();
    }

    private void updateUI(){
        if(persistedSettings.getCurrentUser().getMobilePackages().isEmpty() || !userHasThisPackage()){
            addOrRemoveButton.setText(getString(R.string.add));
            addOrRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(PackageActivity.this);

                    builder.setTitle(getString(R.string.options_dialog_title))
                            .setMessage(R.string.select_phone_number_dialog_message);
                    final TextView textView = new TextView(PackageActivity.this);
                    textView.setId(View.generateViewId());
                    final ListView listView = new ListView(PackageActivity.this);
                    listView.setAdapter(new ArrayAdapter<String>(PackageActivity.this,R.layout.single_text_list_item,R.id.menuItemTitle,persistedSettings.getCurrentUser().getPhoneNumber()));

                    builder.setView(listView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            for(MobilePackage p : persistedSettings.getCurrentUser().getMobilePackages()) {
                                if (p.getId().equals(persistedSettings.getCurrentUser().getPhoneNumberPackageDict().get(persistedSettings.getCurrentUser().getPhoneNumber().get(i)))) {
                                    persistedSettings.getCurrentUser().getMobilePackages().remove(p);
                                    break;
                                }
                            }
                            persistedSettings.getCurrentUser().getMobilePackages().add(currentPackage);
                            persistedSettings.getCurrentUser().getPhoneNumberPackageDict().put(persistedSettings.getCurrentUser().getPhoneNumber().get(i), currentPackage.getId());
                            if(userDAO.updateUser(persistedSettings.getCurrentUser(), "")){
                                persistedSettings.updateCurrentUser(new FirestoreCallback() {
                                    @Override
                                    public void onCreate(boolean value) {

                                    }

                                    @Override
                                    public void onCallback(ArrayList<MobilePackage> packages) {
                                        persistedSettings.getCurrentUser().getMobilePackages().clear();
                                        persistedSettings.getCurrentUser().getMobilePackages().addAll(packages);
                                        updateUI();
                                        dialog.cancel();
                                    }
                                });
                            }
                        }
                    });







                }
            });
        }else{
            addOrRemoveButton.setText(getString(R.string.remove));
            addOrRemoveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(MobilePackage p : persistedSettings.getCurrentUser().getMobilePackages()){
                        if(p.getId().equals(currentPackage.getId())){
                            persistedSettings.getCurrentUser().getMobilePackages().remove(p);
                            break;
                        }
                    }

                    for(String p : persistedSettings.getCurrentUser().getPhoneNumber()){
                        if(persistedSettings.getCurrentUser().getPhoneNumberPackageDict().get(p) != null && persistedSettings.getCurrentUser().getPhoneNumberPackageDict().get(p).equals(currentPackage.getId())){
                            persistedSettings.getCurrentUser().getPhoneNumberPackageDict().put(p, null);
                        }
                    }
                    if(userDAO.updateUser(persistedSettings.getCurrentUser(), "")){
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

                    }
                    updateUI();
                }
            });
        }
    }

    private boolean userHasThisPackage(){
        for(MobilePackage p : persistedSettings.getCurrentUser().getMobilePackages()){
            if(Objects.equals(p.getId(), currentPackage.getId())){
                return true;
            }
        }
        return false;
    }
}
