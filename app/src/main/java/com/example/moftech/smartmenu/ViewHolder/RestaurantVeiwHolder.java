package com.example.moftech.smartmenu.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moftech.smartmenu.Interface.ItemClickListener;
import com.example.moftech.smartmenu.R;

public class RestaurantVeiwHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txt_restaurantName;
    public ImageView img_restaurant;

    private ItemClickListener itemClickListener;

    public RestaurantVeiwHolder(View itemView) {
        super(itemView);

        txt_restaurantName = (TextView)itemView.findViewById(R.id.restaurant_name);
        img_restaurant = (ImageView)itemView.findViewById(R.id.restaurant_image);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);

    }
}
