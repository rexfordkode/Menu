package com.example.moftech.smartmenu.ViewHolder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.moftech.smartmenu.R;

public class ShowCommentViewHolder extends RecyclerView.ViewHolder {
    public TextView txtUser, txtComment;
    public RatingBar ratingBar;

    public ShowCommentViewHolder(@NonNull View itemView) {
        super(itemView);
        txtComment = (TextView)itemView.findViewById(R.id.txtComment);
        txtUser = (TextView)itemView.findViewById(R.id.txtUserPhone);
        ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
    }
}
