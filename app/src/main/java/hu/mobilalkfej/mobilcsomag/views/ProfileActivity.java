package hu.mobilalkfej.mobilcsomag.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.UserDAO;
import hu.mobilalkfej.mobilcsomag.dao.UserDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;

public class ProfileActivity extends AppCompatActivity {

    private TextView headerTitle;
    private View dataItem;
    private View packagesItem;

    private View singOutItem;
    private View deleteAccountItem;

    private UserDAO dao;

    private PersistedSettings persistedSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        persistedSettings = PersistedSettings.getInstance();
        dao = new UserDAOImpl(this);
        headerTitle = findViewById(R.id.headerTitle);
        headerTitle.setText(R.string.profile);
        dataItem = findViewById(R.id.personalDataItem);
        packagesItem = findViewById(R.id.packagesItem);
        singOutItem = findViewById(R.id.singoutItem);
        deleteAccountItem = findViewById(R.id.deleteAccountItem);

        setItemText(dataItem, getString(R.string.personal_data));
        setItemText(packagesItem, getString(R.string.packages));
        setItemText(singOutItem, getString(R.string.logout));
        setItemText(deleteAccountItem, getString(R.string.delete_account));

        dataItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), EditPersonalDataActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        packagesItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getApplicationContext(), AddedPackagesActivity.class);
                startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        singOutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dao.logoutUser(persistedSettings.getCurrentUser())) {
                    Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(myIntent);
                    finish();
                }
            }
        });

        deleteAccountItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle(getString(R.string.attention_dialog_title))
                                .setMessage(R.string.account_delete_dialog_message)
                                        .setPositiveButton(R.string.delete_dialog, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dao.deleteUser(persistedSettings.getCurrentUser(), new FirestoreCallback() {
                                                    @Override
                                                    public void onCreate(boolean value) {
                                                        if(value){
                                                            Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                            startActivity(myIntent);
                                                            finish();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCallback(ArrayList<MobilePackage> packages) {

                                                    }
                                                });
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


            }
        });

    }

    public void setItemText(View item, String text){
        TextView itemTitle = item.findViewById(R.id.menuItemTitle);
        itemTitle.setText(text);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
