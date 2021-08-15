package com.example.moftech.smartmenu.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moftech.smartmenu.Interface.ItemClickListener;
import com.example.moftech.smartmenu.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtOrderid, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    public ImageView btnDelete;
    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderAddress = (TextView)itemView.findViewById(R.id.order_address);
        txtOrderid = (TextView)itemView.findViewById(R.id.order_id);
        txtOrderStatus = (TextView)itemView.findViewById(R.id.order_status);
        txtOrderPhone =  (TextView)itemView.findViewById(R.id.order_phone);

        btnDelete = (ImageView)itemView.findViewById(R.id.btn_delete);
        //itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v ,getAdapterPosition(), false);

    }
}
