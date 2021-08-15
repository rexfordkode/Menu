package com.example.moftech.smartmenu;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Interface.ItemClickListener;
import com.example.moftech.smartmenu.Model.Restaurant;
import com.example.moftech.smartmenu.ViewHolder.RestaurantVeiwHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class RestaurantList extends AppCompatActivity {
    AlertDialog waitingDialog;
    RecyclerView recyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;



    FirebaseRecyclerOptions<Restaurant> options = new FirebaseRecyclerOptions.Builder<Restaurant>()
            .setQuery(FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Restaurants")
                    ,Restaurant.class)
            .build();

    FirebaseRecyclerAdapter <Restaurant,RestaurantVeiwHolder>  adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantVeiwHolder>(options) {
        @Override
        protected void onBindViewHolder(@NonNull RestaurantVeiwHolder viewHolder, int position, @NonNull Restaurant model) {
            viewHolder.txt_restaurantName.setText(model.getName());
            Picasso.get().load(model.getImage())
                    .into(viewHolder.img_restaurant);
            final Restaurant clickItem = model;
            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    //Geting category and send to new Activity
                    Intent foodList = new Intent(RestaurantList.this, Home.class);
                    //Because Category is a key, we have to get the key of the food item
                    Common.restaurantSelected = adapter.getRef(position).getKey();
                    startActivity(foodList);
                }
            });
        }

        @NonNull
        @Override
        public RestaurantVeiwHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.restaurant_item,viewGroup,false);
            return  new RestaurantVeiwHolder(itemView);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        //View
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else
                {
                    Toast.makeText(getBaseContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        //Default, load for first time
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if(Common.isConnectedToInternet(getBaseContext()))
                    loadRestaurant();
                else
                {
                    Toast.makeText(getBaseContext(), "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        recyclerView = (RecyclerView)findViewById(R.id.recycler_restaurant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadRestaurant(){

        adapter.startListening();
        recyclerView.setAdapter(adapter);
        mSwipeRefreshLayout.setRefreshing(false);

        //Animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }
}
