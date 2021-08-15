package com.example.moftech.smartmenu.ViewHolder;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.moftech.smartmenu.Cart;
import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Database.Database;
import com.example.moftech.smartmenu.Model.Order;
import com.example.moftech.smartmenu.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);

        return  new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, final int position) {

        Picasso.get()
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(holder.cart_image);

       holder.qty_count.setNumber(listData.get(position).getQuantity());
       holder.qty_count.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
           @Override
           public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
               Order order = listData.get(position);
               order.setQuantity(String.valueOf(newValue));
               new Database(cart).updateCart(order);

               //Update txtTotal
               //Get the total price of food ordered
               int total = 0;
               List <Order> orders = new Database(cart).getCarts(Common.current_user.getPhone());
               for (Order item: orders)
                   total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(item.getQuantity()));
               Locale local = new Locale ("en", "GHS");
               NumberFormat fmt = NumberFormat.getCurrencyInstance(local);

              cart.txtTotalPrice.setText(fmt.format((total)));
           }
       });
        Locale locale = new Locale("en","GH");

        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice()))*(Integer.parseInt(listData.get(position).getQuantity()));
        holder.txt_cart_price.setText(fmt.format(price));

        holder.txt_cart_name.setText(listData.get(position).getProductName());

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    public Order getItem(int position){
        return listData.get(position);
    }
    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItme(Order item,int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
