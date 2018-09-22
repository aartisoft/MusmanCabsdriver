package com.musman.cabsDriver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.musman.cabsDriver.Common.Common;
import com.musman.cabsDriver.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Arkhip_font.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    // global variables
    Button btnSignIN, btnSignUp;
    RelativeLayout rootLayout;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    ///// end global variables
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialise the database
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_driver_tbl);


        // initialise the buttons
        btnSignIN = (Button) findViewById(R.id.btn_signin);
        btnSignUp = (Button) findViewById(R.id.btnRegister);
        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        btnSignIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginDialog();
            }
        });


    }

    private void showLoginDialog() {
        try {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Sign IN");
            dialog.setMessage("Please use your email to sign in");

            LayoutInflater inflater = LayoutInflater.from(this);
            View Login_layout = inflater.inflate(R.layout.layout_login, null);
            final MaterialEditText edtEmail = Login_layout.findViewById(R.id.edtUserName);
            final MaterialEditText edtPassword = Login_layout.findViewById(R.id.edtPass);

            dialog.setView(Login_layout);

            // set the buttons
            dialog.setPositiveButton("SIGN IN", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();


                    // do some validation of the values provided in the fields
                    if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter email address", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // check the password length
                    if (edtPassword.getText().toString().length() < 6) {
                        Snackbar.make(rootLayout, "password must have 6 or more characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // LOGIN IN AS THE USER
                    // show waiting dialog
                    final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                    waitingDialog.show();
                    auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    waitingDialog.dismiss();

                                    FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Common.currentUser = dataSnapshot.getValue(User.class);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                    startActivity(new Intent(MainActivity.this, DriverHome.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    waitingDialog.dismiss();
                                    Snackbar.make(rootLayout, "Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            Snackbar.make(rootLayout, "Error Login In" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showRegisterDialog() {
        try {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Register");
            dialog.setMessage("Please use your email to register");

            LayoutInflater inflater = LayoutInflater.from(this);
            View regiser_layout = inflater.inflate(R.layout.user_registration, null);
            final MaterialEditText edtEmail = regiser_layout.findViewById(R.id.edtMail);
            final MaterialEditText edtName = regiser_layout.findViewById(R.id.edtName);
            final MaterialEditText edtSurname = regiser_layout.findViewById(R.id.edtSurname);
            final MaterialEditText edtCellno = regiser_layout.findViewById(R.id.edtCellNo);
            final MaterialEditText edtPassword = regiser_layout.findViewById(R.id.edtPassword);

            dialog.setView(regiser_layout);

            // set the buttons

            dialog.setPositiveButton("REGISTER", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    // do some validation of the values provided in the fields
                    if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter email address", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(edtName.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(edtSurname.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter surname", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(edtCellno.getText().toString())) {
                        Snackbar.make(rootLayout, "please enter ID number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                        Snackbar.make(rootLayout, "please en m,ter password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // check the password length
                    if (edtPassword.getText().toString().length() < 6) {
                        Snackbar.make(rootLayout, "password must have 6 or more characters", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // show waiting dialog
                    final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);
                    waitingDialog.show();
                    // create a new user after the validation has been done
                    auth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {


                                    User user = new User(edtName.getText().toString(), edtSurname.getText().toString(), edtCellno.getText().toString(), edtEmail.getText().toString(), edtPassword.getText().toString());
                                    users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(user)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    waitingDialog.dismiss();
                                                    Snackbar.make(rootLayout, "registration successful", Toast.LENGTH_SHORT).show();

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    waitingDialog.dismiss();
                                                    Snackbar.make(rootLayout, "Registration failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Snackbar.make(rootLayout, "Registration failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            Snackbar.make(rootLayout, "Error Registering" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
