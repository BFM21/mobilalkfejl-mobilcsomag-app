package hu.mobilalkfej.mobilcsomag.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.utils.PersistedSettings;
import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.UserDAO;
import hu.mobilalkfej.mobilcsomag.dao.UserDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.User;

public class EditPersonalDataActivity extends AppCompatActivity {



    private UserDAO dao;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private Button saveButton;

    private PersistedSettings persistedSettings;
    private ConstraintLayout mainLayout;
    private ArrayList<EditText> phoneNumberInputs;
    private ArrayList<LinearLayout> linearLayouts;
    private Drawable defaultBackground;
    private CardView addPhoneNumber;
    private ImageView addIcon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data_layout);


        dao = new UserDAOImpl(this);
        persistedSettings = PersistedSettings.getInstance();
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        mainLayout = findViewById(R.id.main_layout);
        saveButton = findViewById(R.id.saveButton);
        phoneNumberInputs = new ArrayList<>();
        linearLayouts= new ArrayList<>();
        addPhoneNumber = findViewById(R.id.addPhoneNumber);
        defaultBackground = addPhoneNumber.getBackground();
        addIcon = findViewById(R.id.addImage);
        addIcon.setColorFilter(firstNameEditText.getTextColors().getDefaultColor());

        addPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoneNumberField(null);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPersonalDataActivity.this);

                builder.setMessage("Az adataid frissítése után újra be kell jelentkezned. Biztos elmented a módosított adataid?");
                builder.setTitle(getString(R.string.attention_dialog_title));
                builder.setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        User user = new User();
                        if (TextUtils.isEmpty(firstNameEditText.getText())) {
                            firstNameEditText.setError(getString(R.string.empty_field_error));
                        } else if (TextUtils.isEmpty(lastNameEditText.getText())) {
                            lastNameEditText.setError(getString(R.string.empty_field_error));
                        } else if (TextUtils.isEmpty(emailEditText.getText())) {
                            emailEditText.setError(getString(R.string.empty_field_error));
                        } else {
                            if(phoneNumberInputs.isEmpty()){
                                addPhoneNumber.setBackgroundColor(Color.RED);
                            }else {
                                boolean allPhoneNumbersReady = true;
                                for (EditText t : phoneNumberInputs) {
                                    if (t.getText().toString().isEmpty()) {
                                        t.setError(getString(R.string.empty_field_error));
                                        allPhoneNumbersReady = false;
                                    } else {
                                        for (EditText t2 : phoneNumberInputs) {
                                            if (!t2.equals(t)) {
                                                if (!t.getText().toString().isEmpty() && !t2.getText().toString().isEmpty()) {
                                                    if (t.getText().toString().equals(t2.getText().toString())) {
                                                        t.setError(getString(R.string.phone_number_match_error));
                                                        t2.setError(getString(R.string.phone_number_match_error));
                                                        allPhoneNumbersReady = false;
                                                    }
                                                }
                                            }
                                        }
                                    }


                                }
                                ArrayList<String> phoneNumbers = new ArrayList<>();
                                if (allPhoneNumbersReady) {
                                    user.getPhoneNumber().clear();
                                    for (EditText t : phoneNumberInputs) {

                                        user.getPhoneNumber().add(t.getText().toString());
                                    }
                                    user.setFirstName(firstNameEditText.getText().toString());
                                    user.setLastName(lastNameEditText.getText().toString());
                                    user.setEmail(emailEditText.getText().toString());
                                    user.setPhotoUrl(persistedSettings.getCurrentUser().getPhotoUrl());
                                    if(passwordEditText.getText().toString().isEmpty()){
                                        if(dao.updateUser(user, null)) {
                                            if (dao.logoutUser(persistedSettings.getCurrentUser())) {
                                                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(myIntent);
                                                finish();
                                            }
                                        }
                                    }else{
                                        if(dao.updateUser(user, passwordEditText.getText().toString())) {
                                            if (dao.logoutUser(persistedSettings.getCurrentUser())) {
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
                                                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(myIntent);
                                                finish();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(persistedSettings.getCurrentUser() != null){
            updateUI(persistedSettings.getCurrentUser());
        }else{
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
            finish();
        }
    }

    private void updateUI(User user){
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        emailEditText.setText(user.getEmail());

        for(String phoneNumber : user.getPhoneNumber()){
            addPhoneNumberField(phoneNumber);
        }
    }

    void addPhoneNumberField(String phoneNumber){
        addPhoneNumber.setBackground(defaultBackground);
        final LinearLayout linearLayout = new LinearLayout(getApplicationContext());
        linearLayout.setId(View.generateViewId());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER);

        final EditText phoneNumberEditText = new EditText(getApplicationContext());
        phoneNumberEditText.setText(phoneNumber);
        phoneNumberEditText.setEms(25);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneNumberEditText.setTextColor(firstNameEditText.getTextColors());
        phoneNumberEditText.setHintTextColor(firstNameEditText.getHintTextColors());
        phoneNumberEditText.setBackground(firstNameEditText.getBackground());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            phoneNumberEditText.setTextCursorDrawable(firstNameEditText.getTextCursorDrawable());
        }
        phoneNumberInputs.add(phoneNumberEditText);

        final ImageView removeIcon = new ImageView(getApplicationContext());
        removeIcon.setImageDrawable(getDrawable(R.drawable.baseline_remove_24));
        removeIcon.setColorFilter(firstNameEditText.getTextColors().getDefaultColor());
        removeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayouts.remove(linearLayout);
                phoneNumberInputs.remove(phoneNumberEditText);
                linearLayout.setVisibility(View.GONE);
            }
        });




        linearLayout.addView(removeIcon);
        linearLayout.addView(phoneNumberEditText);
        mainLayout.addView(linearLayout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);

        if(phoneNumberInputs.size() == 1) {
            constraintSet.connect(linearLayout.getId(), ConstraintSet.START, R.id.main_view, ConstraintSet.START, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.END, R.id.main_view, ConstraintSet.END, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, R.id.addPhoneNumber, ConstraintSet.BOTTOM, 50);
            constraintSet.connect(saveButton.getId(),ConstraintSet.TOP,linearLayout.getId(),ConstraintSet.BOTTOM,20);

        }else{
            constraintSet.connect(linearLayout.getId(), ConstraintSet.START, R.id.main_view, ConstraintSet.START, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.END, R.id.main_view, ConstraintSet.END, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, linearLayouts.get(linearLayouts.size()-1).getId(), ConstraintSet.BOTTOM, 50);
            constraintSet.connect(saveButton.getId(),ConstraintSet.TOP,linearLayout.getId(),ConstraintSet.BOTTOM,20);

        }
        constraintSet.applyTo(mainLayout);

        linearLayouts.add(linearLayout);


    }



    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
