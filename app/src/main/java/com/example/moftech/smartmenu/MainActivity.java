package com.example.moftech.smartmenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moftech.smartmenu.Common.*;
import com.example.moftech.smartmenu.Model.User;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE =7272 ;
    // Button btnSignIn, btnSignUp;
    Button btnContinue;
    TextView txtSlogan;

    FirebaseDatabase database;
    DatabaseReference user;

    EditText editPhone, editPassword;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Alice-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build());

                FacebookSdk.sdkInitialize(getApplicationContext());
              AccountKit.initialize(this);
        setContentView(R.layout.activity_main);

            //printKeyHash();

        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");
        //("Restaurant").child(Common.restaurantSelected).child("Requests");
       // editPassword = (MaterialEditText)findViewById(R.id.editPassword);
       // editPhone = (MaterialEditText)findViewById(R.id.editPhone);

       // btnSignIn = (Button)findViewById(R.id.btnSignIn);
        //btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnContinue = (Button)findViewById(R.id.btn_continue);

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        //Initialize paper
       //  Paper.init(this);

        Typeface face = Typeface.createFromAsset(getAssets(),"fonts/Nabila.ttf");
        txtSlogan.setTypeface(face);

        btnContinue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startLoginSystem();
            }

        });
        //Check session Facebook Account Kit
        if (AccountKit.getCurrentAccessToken() != null)
        {
            final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).build();
            waitingDialog.show();
            waitingDialog.setMessage("Please wait!");

            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    user.child(account.getPhoneNumber().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    User localUser = dataSnapshot.getValue(User.class);
                                    //Login
                                    Intent homeIntent = new Intent(MainActivity.this, RestaurantList.class);
                                    Common.current_user = localUser;
                                    startActivity(homeIntent);
                                    waitingDialog.dismiss();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                }

                @Override
                public void onError(AccountKitError accountKitError) {

                }
            });

        }

    }

    private void startLoginSystem() {
        Intent intent = new Intent (MainActivity.this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
        startActivityForResult(intent,REQUEST_CODE);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        androidx.appcompat.app.AlertDialog.Builder builder
                = new androidx.appcompat.app.AlertDialog
                .Builder(MainActivity.this);
        // Set the message show for the Alert time
        builder.setMessage("Do you want to exit ?");

        // Set Alert Title
        builder.setTitle("Alert !");

        // Set Cancelable false
        // for when the user clicks on the outside
        // the Dialog Box then it will remain show
        builder.setCancelable(false);

        // Set the positive button with yes name
        // OnClickListener method is use of
        // DialogInterface interface.

    }
    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.example.moftech.smartmenu",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature:info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash",Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==REQUEST_CODE)
        {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null)
            {
                Toast.makeText(this,"Login successful"+result.getError().getErrorType().getMessage(),Toast.LENGTH_LONG).show();
                return;
            }
        else if (result.wasCancelled())
            {
                Toast.makeText(this,"Cancel",Toast.LENGTH_LONG).show();
                return;
            }
        else if (result.getAccessToken() != null)
            {
                //Show dialog
                final AlertDialog waitingDialog = new SpotsDialog.Builder().setContext(this).build();
                waitingDialog.show();
                waitingDialog.setMessage("Please wait!");

                //Get current phone
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        final String userPhone = account.getPhoneNumber().toString();

                        //Check if exist on Firebase Users
                        user.orderByKey().equalTo(userPhone)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.child(userPhone).exists()) //checking if user exist
                                        {
                                            User newUser = new User();
                                            newUser.setPhone(userPhone);
                                            newUser.setName("");
                                            newUser.setBalance(String.valueOf(0.0));

                                            //Add to firebase
                                            user.child(userPhone)
                                                    .setValue(newUser)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                               Toast.makeText(MainActivity.this,"User register successful ",Toast.LENGTH_SHORT).show();

                                                       //Login
                                                            user.child(userPhone)
                                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                            User localUser = dataSnapshot.getValue(User.class);
                                                                            //Login
                                                                            // Toast.ma keText(SignIn.this,"Sign in Successfully", Toast.LENGTH_SHORT).show();
                                                                            Intent homeIntent = new Intent(MainActivity.this, RestaurantList.class);
                                                                            Common.current_user = localUser;
                                                                            startActivity(homeIntent);
                                                                            waitingDialog.dismiss();
                                                                            finish();
                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                        else //if exist
                                        {
                                        //Login
                                            user.child(userPhone)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            User localUser = dataSnapshot.getValue(User.class);
                                                            //Login
                                                          Intent homeIntent = new Intent(MainActivity.this, RestaurantList.class);
                                                            Common.current_user = localUser;
                                                            startActivity(homeIntent);
                                                            waitingDialog.dismiss();
                                                            finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {
                        Toast.makeText(MainActivity.this,""+accountKitError.getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
