package hu.mobilalkfej.mobilcsomag.views;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.adapters.PartialPackageAdapter;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.MobilePackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAO;
import hu.mobilalkfej.mobilcsomag.dao.PartialPackageDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.PackageType;
import hu.mobilalkfej.mobilcsomag.models.PartialPackage;

public class CustomPackageActivity extends AppCompatActivity {


    private RecyclerView internetPackages, callPackages, messagePackages;
    private static TextView sum;

    private CardView createButton;
    private ArrayList<PartialPackage> internetPackageList, callPackageList, messagePackageList;

    private PartialPackageDAO partialPackageDAO;
    private MobilePackageDAO mobilePackageDAO;
    public static int price;


    private PartialPackageAdapter internetPackageAdapter;
    private PartialPackageAdapter callPackageAdapter;
    private PartialPackageAdapter messagePackageAdapter;

    private PersistedSettings persistedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_package_layout);
        createButton = findViewById(R.id.createButton);

        persistedSettings = PersistedSettings.getInstance();
        price = 0;
        partialPackageDAO = new PartialPackageDAOImpl(this);
        mobilePackageDAO = new MobilePackageDAOImpl(this);
        internetPackageList = new ArrayList<>();
        if(persistedSettings.getInternetPackages() != null){
            internetPackageList.addAll(persistedSettings.getInternetPackages());
        }

        callPackageList = new ArrayList<>();
        if(persistedSettings.getCallPackages() != null){
            callPackageList.addAll(persistedSettings.getCallPackages() );
        }

        messagePackageList = new ArrayList<>();
        if(persistedSettings.getMessagePackages()  != null){
            messagePackageList.addAll(persistedSettings.getMessagePackages());
        }

        internetPackages = findViewById(R.id.internetPackageList);
        callPackages = findViewById(R.id.callPackageList);
        messagePackages = findViewById(R.id.messagePackageList);
        sum = findViewById(R.id.sumLabel);
        sum.setText(price + " Ft");

        internetPackages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        callPackages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        messagePackages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        internetPackageAdapter = new PartialPackageAdapter(internetPackageList, PackageType.INTERNET);
        callPackageAdapter = new PartialPackageAdapter(callPackageList, PackageType.CALL);
        messagePackageAdapter = new PartialPackageAdapter(messagePackageList, PackageType.MESSAGE);

        internetPackages.setAdapter(internetPackageAdapter);
        callPackages.setAdapter(callPackageAdapter);
        messagePackages.setAdapter(messagePackageAdapter);


        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(PartialPackageAdapter.selectedCount);
                if(PartialPackageAdapter.selectedCount > 0) {
                    MobilePackage customPackage = new MobilePackage();
                    customPackage.setId(String.valueOf(System.currentTimeMillis()));
                    customPackage.setInternet(internetPackageAdapter.getSelectedPackage());
                    customPackage.setCall(callPackageAdapter.getSelectedPackage());
                    customPackage.setMessage(messagePackageAdapter.getSelectedPackage());
                    customPackage.setPrice(price);
                    if (internetPackageAdapter.getSelectedPackage() != null) {
                        customPackage.setMonthly(true);
                    } else if (callPackageAdapter.getSelectedPackage() != null && callPackageAdapter.getSelectedPackage().isMonthly()) {
                        customPackage.setMonthly(true);
                    } else if (messagePackageAdapter.getSelectedPackage() != null && messagePackageAdapter.getSelectedPackage().isMonthly()){
                        customPackage.setMonthly(true);
                    }else{
                        customPackage.setMonthly(false);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomPackageActivity.this);
                    final LinearLayout layout = new LinearLayout(CustomPackageActivity.this);
                    final EditText name = new EditText(CustomPackageActivity.this);
                    name.setHint(R.string.package_name);
                    name.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
                    final EditText description = new EditText(CustomPackageActivity.this);
                    description.setHint(R.string.package_description);
                    description.setFilters(new InputFilter[] {new InputFilter.LengthFilter(100)});
                    name.setInputType(InputType.TYPE_CLASS_TEXT);
                    description.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.addView(name);
                    layout.addView(description);
                    builder.setView(layout);
                    builder.setTitle(getString(R.string.data_dialog_title))
                            .setMessage(R.string.create_package_instruction_dialog_message)
                            .setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(name.getText().toString().isEmpty()){
                                        name.setError(getString(R.string.empty_field_error));
                                    }else{
                                        customPackage.setName(name.getText().toString());
                                        if(description.getText().toString().isEmpty()){
                                            customPackage.setDescription("");
                                        }else{
                                            customPackage.setDescription(description.getText().toString());
                                        }
                                        mobilePackageDAO.addNewPackage(customPackage);
                                        finish();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomPackageActivity.this);

                    builder.setTitle(getString(R.string.attention_dialog_title))
                            .setMessage(R.string.package_create_dialog_error_message)
                            .setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });

    }

    public static void updatePrice(){
        sum.setText(price + " Ft");
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
   @Override
    public void finish(){
        super.finish();
        PartialPackageAdapter.selectedCount = 0;
        for(PartialPackage p : persistedSettings.getCallPackages()){
            p.setSelected(false);
        }

       for(PartialPackage p : persistedSettings.getInternetPackages()){
           p.setSelected(false);
       }

       for(PartialPackage p : persistedSettings.getMessagePackages()){
           p.setSelected(false);
       }
   }
}
