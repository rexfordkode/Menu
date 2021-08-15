package com.example.moftech.smartmenu.ViewHolder;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Database.Database;
import com.example.moftech.smartmenu.FoodDetail;
import com.example.moftech.smartmenu.Interface.ItemClickListener;
import com.example.moftech.smartmenu.Model.Favorites;
import com.example.moftech.smartmenu.Model.Order;
import com.example.moftech.smartmenu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favorites_item,viewGroup,false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder viewHolder, final int position) {

        viewHolder.food_name.setText(favoritesList.get(position).getFoodName());
        viewHolder.food_price.setText(String.format("¢ %s", favoritesList.get(position).getFoodPrice().toString()));
        Picasso.get()
                .load(favoritesList.get(position).getFoodImage())
                .into(viewHolder.food_image);

        //Quick cart
        viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).checkFoodExists(favoritesList.get(position).getFoodId(), Common.current_user.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.current_user.getPhone(),
                            favoritesList.get(position).getFoodId(),
                            favoritesList.get(position).getFoodName(),
                            "1",
                            favoritesList.get(position).getFoodPrice(),
                            favoritesList.get(position).getFoodDiscount(),
                            favoritesList.get(position).getFoodImage()
                    ));
                }
                else
                {
                    new Database(context).increaseCart(Common.current_user.getPhone(),
                            favoritesList.get(position).getFoodId());
                }
                Toast.makeText(context, "Added to Cart ", Toast.LENGTH_SHORT).show();

            }
        });


        final Favorites local = favoritesList.get(position);
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent foodDetails = new Intent(context, FoodDetail.class);
                foodDetails.putExtra("FoodId", favoritesList.get(position).getFoodId()); //Send food it to new activity
                context.startActivity(foodDetails);
            }

        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position){
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItme(Favorites item,int position){
        favoritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favorites getItem(int positon)
    {
        return favoritesList.get(positon);
    }

}
