package com.example.moftech.smartmenu.Helper;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;

import com.example.moftech.smartmenu.Interface.RecyclerItemTouchHelperListener;
import com.example.moftech.smartmenu.ViewHolder.CartViewHolder;
import com.example.moftech.smartmenu.ViewHolder.FavoritesViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private RecyclerItemTouchHelperListener listener;


    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null)
            listener.onSwiped(viewHolder,direction,viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof CartViewHolder) {
            View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
        else if (viewHolder instanceof FavoritesViewHolder)
        {
            View foregroundView = ((FavoritesViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

       if (viewHolder instanceof  CartViewHolder) {
           View foregroundView = ((CartViewHolder) viewHolder).view_foreground;
           getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
       }
       else if (viewHolder instanceof FavoritesViewHolder)
       {
           View foregroundView = ((FavoritesViewHolder)viewHolder).view_foreground;
           getDefaultUIUtil().clearView(foregroundView);
       }
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null)
        {
            if (viewHolder instanceof CartViewHolder) {
                View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().clearView(foregroundView);
            }
            else if (viewHolder instanceof FavoritesViewHolder)
            {
                View foregroundView = ((FavoritesViewHolder)viewHolder).view_foreground;
                getDefaultUIUtil().clearView(foregroundView);
            }
        }

    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (viewHolder instanceof CartViewHolder) {
            View foregroundView = ((CartViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
        else if (viewHolder instanceof FavoritesViewHolder)
        {
            View foregroundView = ((FavoritesViewHolder)viewHolder).view_foreground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }
}
