package com.example.moftech.smartmenu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Database.Database;
import com.example.moftech.smartmenu.Interface.ItemClickListener;
import com.example.moftech.smartmenu.Model.Favorites;
import com.example.moftech.smartmenu.Model.Order;
import com.example.moftech.smartmenu.ViewHolder.FoodViewHolder;
import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import com.example.moftech.smartmenu.Model.Food;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class  FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String categoryId = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    //Favorites
    Database localDB;

    //Facebook share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SwipeRefreshLayout swipeRefreshLayout;


    //Create Target from placing
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //crease photo from Bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class))
            {
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

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

        setContentView(R.layout.activity_food_list);


        //Init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Restaurants").child(Common.restaurantSelected)
                .child("detail").child("Foods");

        //Local DB
        localDB = new Database(this);

        //Swipe initialization
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get Intent here
                if (getIntent() !=null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);

                    else {
                        Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Intent here
                if (getIntent() !=null)
                    categoryId = getIntent().getStringExtra("CategoryId");
                if(!categoryId.isEmpty() && categoryId != null)
                {
                    if (Common.isConnectedToInternet(getBaseContext()))
                        loadListFood(categoryId);

                    else {
                        Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                //Search Button ini
                materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
                materialSearchBar.setHint("Enter your food");
                loadSuggestFoods();
                materialSearchBar.setCardViewElevation(10);

                materialSearchBar.addTextChangeListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        List <String> suggest = new ArrayList<>();
                        for (String search:suggestList)
                        {
                            if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                                suggest.add(search);
                        }
                        materialSearchBar.setLastSuggestions(suggest);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                    @Override
                    public void onSearchStateChanged(boolean enabled) {
                        //When searhc bar is close  Restore originial suggest adapter
                        if (!enabled)
                            recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onSearchConfirmed(CharSequence text) {
                        //when search is done
                        startSearch(text);
                    }

                    @Override
                    public void onButtonClicked(int buttonCode) {

                    }
                });

            }
        });

        recyclerView = (RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(FoodList.this);
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
    @Override
    protected void onResume() {
        super.onResume();
        //Fix click back on FoodDetail and get no item in Food List
        if (adapter != null){
            adapter.startListening();
        }

    }

    private void startSearch (final CharSequence text){
        //Create query by name
        Query searchByName = foodList.orderByChild("name").equalTo(text.toString());
        //Create options with query
        FirebaseRecyclerOptions <Food> foodOption = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();
        searchadapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOption) {


            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder viewHolder, int position, @NonNull Food model) {
                viewHolder.food_name.setText(model.getName());
                Picasso.get()
                        .load(model.getImage())
                        .into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start activity
                        Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("FoodId",searchadapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.food_item, viewGroup, false);
                return new FoodViewHolder(itemView);
            }
        };

            searchadapter.startListening();
            recyclerView.setAdapter(searchadapter);
    }

    private void loadSuggestFoods() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());// Add name of food to suggestion list
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        //Create query by category
        Query searchByName = foodList.orderByChild("menuId").equalTo(categoryId);
        //Create options with query
        FirebaseRecyclerOptions<Food> foodOption = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(searchByName,Food.class)
                .build();
        //set Adapter
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(foodOption) {
            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int position, @NonNull final Food model) {
                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText(String.format("GH¢ %s", model.getPrice().toString()));
                Picasso.get().load(model.getImage())
                        .into(viewHolder.food_image);
                //Quick cart
                    viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean isExists = new Database(getBaseContext()).checkFoodExists(adapter.getRef(position).getKey(), Common.current_user.getPhone());
                            if (!isExists) {
                                                                         new Database(getBaseContext()).addToCart(new Order(
                                                                                 Common.current_user.getPhone(),
                                                                                 adapter.getRef(position).getKey(),
                                                                                 model.getName(),
                                                                                 "1",
                                                                                 model.getPrice(),
                                                                                 model.getDiscount(),
                                                                                 model.getImage()
                                                                         ));

                                                                     }
                            else
                                { new Database(getBaseContext()).increaseCart(Common.current_user.getPhone(),adapter.getRef(position).getKey());
                                }
                            Toast.makeText(FoodList.this, "Added to Cart ", Toast.LENGTH_SHORT).show();
                        }

                                                             });

                            //Add Favorites
                if (localDB.isFavorites(adapter.getRef(position).getKey(),Common.current_user.getPhone()))
                    viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

                //Click to Share
                viewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Picasso.get()
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //Click to change state of Favorite
                viewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Favorites favorites = new Favorites();
                        favorites.setFoodId(adapter.getRef(position).getKey());
                        favorites.setFoodName(model.getName());
                        favorites.setFoodDescription(model.getDescription());
                        favorites.setFoodDiscount(model.getDiscount());
                        favorites.setFoodImage(model.getImage());
                        favorites.setFoodMenuId(model.getMenuId());
                        favorites.setFoodPrice(model.getPrice());
                        favorites.setUserPhone(Common.current_user.getPhone());


                        if (!localDB.isFavorites(adapter.getRef(position).getKey(),Common.current_user.getPhone())) {
                            localDB.addToFavorites(favorites);
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, " " + model.getName() + " was added to Favorites", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorites(adapter.getRef(position).getKey(),Common.current_user.getPhone());
                            viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(FoodList.this, " " + model.getName() + " was removed from Favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        //Start Intent
                        Intent foodDetails = new Intent(FoodList.this, FoodDetail.class);
                        foodDetails.putExtra("foodId", adapter.getRef(position).getKey()); //Send food it to new activity
                        startActivity(foodDetails);
                    }

                });
            }

        @Override
        public FoodViewHolder onCreateViewHolder (@NonNull ViewGroup viewGroup,int i){
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.food_item, viewGroup, false);
            return new FoodViewHolder(itemView);
        }

        };
        adapter.startListening();

        Log.d("TAG",""+adapter.getItemCount());
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(false);

        //Animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
//       searchadapter.stopListening();
    }
}
