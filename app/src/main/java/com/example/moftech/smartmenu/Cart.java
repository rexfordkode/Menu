package com.example.moftech.smartmenu;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.ParseException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Common.Config;
import com.example.moftech.smartmenu.Database.Database;
import com.example.moftech.smartmenu.Helper.RecyclerItemTouchHelper;
import com.example.moftech.smartmenu.Interface.RecyclerItemTouchHelperListener;
import com.example.moftech.smartmenu.Model.DataMessage;
import com.example.moftech.smartmenu.Model.MyResponse;
import com.example.moftech.smartmenu.Model.Order;
import com.example.moftech.smartmenu.Model.Request;
import com.example.moftech.smartmenu.Model.Token;
import com.example.moftech.smartmenu.Model.User;
import com.example.moftech.smartmenu.Remote.APIService;
import com.example.moftech.smartmenu.Remote.IGoogleService;
import com.example.moftech.smartmenu.ViewHolder.CartAdapter;
import com.example.moftech.smartmenu.ViewHolder.CartViewHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class  Cart extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, RecyclerItemTouchHelperListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    LayoutInflater inflater;
    FirebaseDatabase database;
    DatabaseReference requests;

   public TextView txtTotalPrice;
    FButton btnPlace;

    List <Order> cart = new ArrayList<>();
    Place shippingAddress;
    CartAdapter adapter;

    //Paypal Payment
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)//use sandbox because we test, change it late if you going
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address,comments; ///
    private int PAYMENT_REQUEST_CODE = 9999;
    public int PLAY_SERVICES_REQUEST = 9989; //change from private to public

    //Declare Google Map API Retrofit
    IGoogleService mGoogleMapService;
    APIService mService;

    public LocationRequest mLocationRequest; //change from private to public
    public GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;


    private FusedLocationProviderClient fusedLocationProviderClient;

    private  static final int LOCATION_REQUEST_CODE = 9887;

    private static final int UPDATE_INTERNAL = 5000;
    private static final int FASTEST_INTERNAL = 2000;
    private static final int DISPLACEMENT = 10;

    RelativeLayout rootLayout;

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
        .build() );

        setContentView(R.layout.activity_cart);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },LOCATION_REQUEST_CODE);
        }else {
            fetchLastLocation();
            createLocationRequest();
        }

        //Init
        mGoogleMapService = Common.getGoogleMapAPI();

        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },LOCATION_REQUEST_CODE);
        }
        else
        {
            if (checkPlayServices()) //If have play service on device
            {
                buildGoogleApiClient();
                createLocationRequest(); 
            }
        }

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        //Init Service
        mService = Common.getFCMService();

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Restaurants").child(Common.restaurantSelected).child("Requests");

        //Initialize
        recyclerView = (RecyclerView)findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice = (TextView)findViewById(R.id.total);
        btnPlace   = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cart.size() > 0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "Your cart is empty!!", Toast.LENGTH_SHORT).show();
                }

        });

        loadListFood();
    }

    private void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, LOCATION_REQUEST_CODE);
        }
        else {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mLastLocation = location;
                        Toast.makeText(Cart.this, mLastLocation.getLatitude() + " " + mLastLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        Log.e("location: ",mLastLocation.getLatitude()+"/"+mLastLocation.getLongitude());
                    } else {
                        Toast.makeText(Cart.this, "Make sure you've enabled GPS", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void createLocationRequest() {
      mLocationRequest =  LocationRequest.create();
      mLocationRequest.setInterval(UPDATE_INTERNAL);
      mLocationRequest.setFastestInterval(FASTEST_INTERNAL);
      mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }
    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
                GooglePlayServicesUtil.getErrorDialog(resultCode,this,PLAY_SERVICES_REQUEST).show();
            else
            {
                Toast.makeText(this,"This device thus not support plays service",Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address or Table number");

            inflater = this.getLayoutInflater();
        ////View order_address_comments = inflater.inflate(R.layout.order_address_comment,null);
        View order_address_comments  = inflater.inflate(R.layout.order_address_comment,null);
        //final MaterialEditText editAddress = (MaterialEditText)order_address_comments.findViewById(R.id.editAddress);
        final PlaceAutocompleteFragment editAddress = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //Hide search icon before fragment
        editAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);

        ((EditText)editAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter your address/Table Number");
        //Seet text size
        ((EditText)editAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);

        //Get Address from Place Autocomplete
        editAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
            shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
            Log.e("ERROR", status.getStatusMessage());
            }
        });

        final MaterialEditText editComment = (MaterialEditText)order_address_comments.findViewById(R.id.editComments);

        //Radio
        final RadioButton rdbShipToAddress = (RadioButton) order_address_comments.findViewById(R.id.rdbShipToAddress);
        final RadioButton rdbShipToHome = (RadioButton) order_address_comments.findViewById(R.id.rdbHomeAddress);
        final RadioButton rdbCOD = (RadioButton) order_address_comments.findViewById(R.id.rdbCOD  );
        final RadioButton rdbPaypal = (RadioButton) order_address_comments.findViewById(R.id.rdbPaypal);
        final RadioButton rdbBalance = (RadioButton) order_address_comments.findViewById(R.id.rdbSmenuBalance);

        //Event Radio
        rdbShipToHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    if (Common.current_user.getHomeAddress() != null||
                     !TextUtils.isEmpty(Common.current_user.getHomeAddress()))
                    {
                        address = Common.current_user.getHomeAddress();
                        ((EditText)editAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                .setText(address);
                    }
                    else
                    {
                        Toast.makeText(Cart.this, "Please update your Home Address",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        rdbShipToAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) //
                {
                    //Added Locale.getDefault to the parameters to enable paypal work fine
                    mGoogleMapService.getAddressName(String.format(Locale.getDefault(),"https://maps.googleapis.com/maps/api/geocode/json?latlng=%f,%f &sensor=true",
                            mLastLocation.getLatitude(),
                            mLastLocation.getLongitude()))
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    //if fetch API ok
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());

                                        JSONArray resultArray = jsonObject.getJSONArray("results");

                                        JSONObject firstObject = resultArray.getJSONObject(0);

                                        address = firstObject.getString("formated_address");
                                        //Set this address to editAddress
                                        ((EditText)editAddress.getView().findViewById(R.id.place_autocomplete_search_input))
                                                .setText(address);

                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(Cart.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        alertDialog.setView(order_address_comments);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Add check condition here
                //If user select address from place Fragment, just use it
                // If use select shit to this address, get address from locaiton use it
                //If use select Home address , get HomeAddress from Profile and use it
                if (!rdbShipToAddress.isChecked() && !rdbShipToHome.isChecked()){
                    
                    //IF both radio is not select
                    if (shippingAddress != null)
                        address = shippingAddress.getAddress().toString();
                    else 
                    {
                        Toast.makeText(Cart.this, "Please enter the address or select option address", Toast.LENGTH_SHORT).show();

                        //Fix crash fragment
                        getFragmentManager().beginTransaction()
                                .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                                .commit();
                        return;
                    }
                    //address = shippingAddress.getAddress().toString();
                    
                }
                if (TextUtils.isEmpty(address))
                {
                    Toast.makeText(Cart.this, "Please enter address or select option address", Toast.LENGTH_SHORT).show();

                    //Fix crash fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }

                comments = editComment.getText().toString();

                //Check Payment
                if (!rdbCOD.isChecked() && !rdbPaypal.isChecked() && !rdbBalance.isChecked()) //If both COD and Paypal is not checked
                {
                    Toast.makeText(Cart.this, "Select Payment option", Toast.LENGTH_SHORT).show();

                    //Fix crash fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                    return;
                }
                else if (rdbPaypal.isChecked())
                {
                String formatAmount = txtTotalPrice.getText().toString()
                                        .replace("¢","")
                                        .replace(",","");

                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                        "USD",
                        "s'Menu Order",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(intent,PAYMENT_REQUEST_CODE);

                    //Fix crash fragment
                    getFragmentManager().beginTransaction()
                            .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                            .commit();
                }
                else if (rdbCOD.isChecked())
                {
                    Request request = new Request(
                            Common.current_user.getPhone(),
                            Common.current_user.getName(),
                            address,
                            txtTotalPrice.getText().toString(),
                            "0", //Status
                            comments,
                            "COD",
                           "Unpaid",
                            String.format("%s,%s",mLastLocation.getLatitude(),mLastLocation.getLongitude()), //Cordinates for map
                           Common.restaurantSelected,
                            cart
                    );
                       /* Submit to Firebase
                        We will be using system CurrentMill to key
                        */
                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request); //If nothing changes it must be request
                    //Delete cart
                    new Database(getBaseContext()).cleanCart(Common.current_user.getPhone());

                    sendNotificationOrder(order_number);
                    Toast.makeText(Cart.this, "Thank you,  Order Placed!!", Toast.LENGTH_SHORT).show();
                    finish();

                }

                else if (rdbBalance.isChecked())
                {
                    double amount = 0;

                    //First we will ge total price from txtTotaLPrice

                    try {
                        amount = Common.formatCurrency(txtTotalPrice.getText().toString(),Locale.US).doubleValue();
                    }catch (ParseException | java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    if (Double.parseDouble(Common.current_user.getBalance().toString()) >= amount)
                    {
                        //Create new Request
                        Request request = new Request(
                                Common.current_user.getPhone(),
                                Common.current_user.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0", //Status
                                comments,
                                "SmartMenu Balance",
                                "Paid",
                                String.format("%s,%s",mLastLocation.getLatitude(),mLastLocation.getLongitude()),
                                Common.restaurantSelected,
                                cart
                        );
                        //Submit to Firebase
                        //We will be using system CurrentMill to key
                        final String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(request); //If nothing changes it must be request
                        //Delete cart
                        new Database(getBaseContext()).cleanCart(Common.current_user.getPhone());

                        //Update balance
                        double balance = Double.parseDouble(Common.current_user.getBalance().toString()) - amount;
                        Map<String,Object> update_balance = new HashMap<>();
                        update_balance.put("balance",balance);

                        FirebaseDatabase.getInstance()
                                .getReference("User")
                                .child(Common.current_user.getPhone())
                                .updateChildren(update_balance)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Refresh user
                                            FirebaseDatabase.getInstance()
                                                    .getReference("User")
                                                    .child(Common.current_user.getPhone())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            Common.current_user = dataSnapshot.getValue(User.class);
                                                            //Send order to server
                                                            sendNotificationOrder(order_number);

                                                            //Toast.makeText(Cart.this, "Thank you,  Order Placed!!", Toast.LENGTH_SHORT).show();
                                                            //finish();
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }
                                });

                    }
                    else
                    {
                        Toast.makeText(Cart.this,"Your balance not enough",Toast.LENGTH_SHORT).show();
                    }

                } //End of Balance



            }


        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                dialogInterface.dismiss();

                //Remove fragment
                getFragmentManager().beginTransaction()
                        .remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        .commit();
            }
        });

        alertDialog.show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayServices()){
                        createLocationRequest();
                        fetchLastLocation();
                }
                } else {
                    Toast.makeText(Cart.this, "Location permission missing", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==PAYMENT_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                  PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null)
                {
                    try {
                          String paymentDetail = confirm.toJSONObject().toString(4);
                          JSONObject jsonObject = new JSONObject(paymentDetail);

                          //Create new Request
                        Request request = new Request(
                                Common.current_user.getPhone(),
                                Common.current_user.getName(),
                                address,
                                txtTotalPrice.getText().toString(),
                                "0", //Status
                                comments,
                                "Paypal",
                                jsonObject.getJSONObject("response").getString( "state"),
                                String.format(Locale.getDefault(),"%s,%s",shippingAddress.getLatLng().latitude,shippingAddress.getLatLng().longitude),
                                Common.restaurantSelected,
                                cart
                        );
                        //Submit to Firebase
                        //We will be using system CurrentMill to key
                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(order_number); //If nothing changes it must be request
                        //Delete cart
                        new Database(getBaseContext()).cleanCart(Common.current_user.getPhone());
                        sendNotificationOrder(order_number);

                        Toast.makeText(Cart.this, "Thank you,  Order Placed!!", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if (resultCode == Activity.RESULT_CANCELED)
                    Toast.makeText(this,"Payment canceled", Toast.LENGTH_SHORT).show();
                else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID )
                    Toast.makeText(this,"Invalid payment",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendNotificationOrder(final String order_number)  {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        final Query data = tokens.orderByChild("isServerToken").equalTo(false);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    //Create ray payload to send
                    //Notification notification = new Notification("S'Menu"," You have a new Order"+order_number);
                  // Sender content = new Sender(serverToken.getTokens(),notification);
                    Map<String,String> dataSend = new HashMap<>();
                    dataSend.put("title","Smart Menu");
                            dataSend.put("message"," You have a new Order"+order_number);
                    DataMessage dataMessage = new DataMessage(serverToken.getTokens(),dataSend);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("Content",test);

                        mService.sendNotficaition(dataMessage)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        //Only run when get result
                                        if (response.code() == 200){
                                            if (response.body().success == 1) {
                                                Toast.makeText(Cart.this, "Thank you,  Order Placed!!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(Cart.this, "Failed!!", Toast.LENGTH_SHORT).show();

                                            }
                                    }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("ERROR",t.getMessage());

                                    }
                                });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void loadListFood() {

        cart = new Database(this).getCarts(Common.current_user.getPhone());
        adapter = new CartAdapter(cart,this );
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Get the total price of food ordered
        int total = 0;
        for (Order order:cart)
            total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale local = new Locale ("en", "GHS");
        NumberFormat fmt = NumberFormat.getCurrencyInstance();

        txtTotalPrice.setText(fmt.format((total)));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int order) {
        //This code will remove item at ListOrder by position
        cart.remove(order);
        //After that,  it will delete old data from SQLite
        new Database(this).cleanCart(Common.current_user.getPhone());
        //And time
        for (Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fetchLastLocation();
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
          LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
         }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            Log.d("LOCATION","Your location : "+mLastLocation.getLongitude()+" , "+mLastLocation.getLongitude());
        }
        else
        {
            Log.d("LOCATION","Couldn't get your location :" );

        }


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
        fetchLastLocation();

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int adapterPosition) {
        if (viewHolder instanceof CartViewHolder)
        {
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();
            String d  = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex =  viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(),Common.current_user.getPhone());

            //Update txtTotal
            //Get the total price of food ordered
            int total = 0;
            List <Order> orders = new Database(getBaseContext()).getCarts(Common.current_user.getPhone());
            for (Order item: orders)
                total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            Locale local = new Locale ("en", "GHS");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(local);

          txtTotalPrice.setText(fmt.format((total)));

          //Make snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from cart!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItme(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //Update txtTotal
                    //Get the total price of food ordered
                    int total = 0;
                    List <Order> orders = new Database(getBaseContext()).getCarts(Common.current_user.getPhone());
                    for (Order item: orders)
                        total += (Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    Locale local = new Locale ("en", "GHS");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(local);

                    txtTotalPrice.setText(fmt.format((total)));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }

    }
}
