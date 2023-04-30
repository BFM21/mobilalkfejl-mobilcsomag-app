package hu.mobilalkfej.mobilcsomag.views;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import hu.mobilalkfej.mobilcsomag.R;
import hu.mobilalkfej.mobilcsomag.dao.FirestoreCallback;
import hu.mobilalkfej.mobilcsomag.dao.UserDAO;
import hu.mobilalkfej.mobilcsomag.dao.UserDAOImpl;
import hu.mobilalkfej.mobilcsomag.models.MobilePackage;
import hu.mobilalkfej.mobilcsomag.models.User;

public class RegisterActivity extends AppCompatActivity {

    private UserDAO dao;
    private User user;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private EditText passwordEditText;

    private ScrollView mainView;

    private ConstraintLayout mainLayout;
    private CardView addPhoneNumber;
    private Button registerButton;

    private ImageView addIcon;
    private ArrayList<EditText> phoneNumberInputs;
    private ArrayList<LinearLayout> linearLayouts;

    private ConstraintSet originalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        user = new User();
        dao = new UserDAOImpl(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mainView = findViewById(R.id.main_view);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addPhoneNumber = findViewById(R.id.addPhoneNumber);
        mainLayout = findViewById(R.id.main_layout);
        passwordEditText = findViewById(R.id.passwordEditText);
        registerButton = findViewById(R.id.registerButton);
        phoneNumberInputs = new ArrayList<>();
        linearLayouts = new ArrayList<>();
        originalLayout = new ConstraintSet();
        originalLayout.clone(mainLayout);
        Drawable defaultBackground = addPhoneNumber.getBackground();
        addIcon = findViewById(R.id.addImage);
        addIcon.setColorFilter(firstNameEditText.getTextColors().getDefaultColor());
        checkNetwork();
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(firstNameEditText.getText())) {
                    firstNameEditText.setError(getString(R.string.empty_field_error));
                } else if (TextUtils.isEmpty(lastNameEditText.getText())) {
                    lastNameEditText.setError(getString(R.string.empty_field_error));
                } else if (TextUtils.isEmpty(emailEditText.getText())) {
                    emailEditText.setError(getString(R.string.empty_field_error));
                } else if (TextUtils.isEmpty(passwordEditText.getText())) {
                    passwordEditText.setError(getString(R.string.empty_field_error));
                } else if (!TextUtils.isEmpty(passwordEditText.getText()) && passwordEditText.getText().length() < 6) {
                    passwordEditText.setError(getString(R.string.password_length_error));
                } else {
                    if (phoneNumberInputs.isEmpty()) {
                        addPhoneNumber.setBackgroundColor(Color.RED);
                    } else {
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

                        if (allPhoneNumbersReady) {
                            for (EditText t : phoneNumberInputs) {
                                user.getPhoneNumber().add(t.getText().toString());
                            }
                            user.setFirstName(firstNameEditText.getText().toString());
                            user.setLastName(lastNameEditText.getText().toString());
                            user.setEmail(emailEditText.getText().toString());
                            createAccount(user, passwordEditText.getText().toString());
                        }

                    }
                }
            }
        });

        addPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPhoneNumber.setBackground(defaultBackground);
                final LinearLayout linearLayout = new LinearLayout(getApplicationContext());
                linearLayout.setId(View.generateViewId());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.setGravity(Gravity.CENTER);

                final EditText phoneNumberEditText = new EditText(getApplicationContext());
                phoneNumberEditText.setHint(R.string.phone_number);
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

                updateUI(linearLayout);
                linearLayouts.add(linearLayout);


            }
        });

    }

    private void updateUI(LinearLayout linearLayout) {
        checkNetwork();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(mainLayout);
        if (phoneNumberInputs.isEmpty()) {
            originalLayout.applyTo(mainLayout);
        }

        if (phoneNumberInputs.size() == 1) {
            constraintSet.connect(linearLayout.getId(), ConstraintSet.START, R.id.main_view, ConstraintSet.START, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.END, R.id.main_view, ConstraintSet.END, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, R.id.addPhoneNumber, ConstraintSet.BOTTOM, 50);
            constraintSet.connect(R.id.registerButton, ConstraintSet.TOP, linearLayout.getId(), ConstraintSet.BOTTOM, 20);

        } else {
            constraintSet.connect(linearLayout.getId(), ConstraintSet.START, R.id.main_view, ConstraintSet.START, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.END, R.id.main_view, ConstraintSet.END, 5);
            constraintSet.connect(linearLayout.getId(), ConstraintSet.TOP, linearLayouts.get(linearLayouts.size() - 1).getId(), ConstraintSet.BOTTOM, 50);
            constraintSet.connect(R.id.registerButton, ConstraintSet.TOP, linearLayout.getId(), ConstraintSet.BOTTOM, 20);

        }
        constraintSet.applyTo(mainLayout);
    }

    public void createAccount(User user, String password) {
        dao.registerUser(user, password, new FirestoreCallback() {
            @Override
            public void onCreate(boolean value) {
                if (value) {
                    Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(RegisterActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "default")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(getString(R.string.notification_description))
                            .setContentIntent(pendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                            .setAutoCancel(true);

                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(0, builder.build());

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
