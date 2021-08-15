package com.example.moftech.smartmenu;

import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;

import com.example.moftech.smartmenu.Common.Common;
import com.example.moftech.smartmenu.Database.Database;
import com.example.moftech.smartmenu.Helper.RecyclerItemTouchHelper;
import com.example.moftech.smartmenu.Interface.RecyclerItemTouchHelperListener;
import com.example.moftech.smartmenu.Model.Favorites;
import com.example.moftech.smartmenu.ViewHolder.FavoritesAdapter;
import com.example.moftech.smartmenu.ViewHolder.FavoritesViewHolder;

public class  FavoritesActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    FavoritesAdapter adapter;
    RelativeLayout rootLayout;


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        rootLayout = (RelativeLayout)findViewById(R.id.root_Layout);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_fav);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

       LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        //Swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        
        loadFavorites();
    }

    private void loadFavorites() {
        adapter = new FavoritesAdapter(this,new Database(this).getAllFavorites(Common.current_user.getPhone()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int adapterPosition) {
        if (viewHolder instanceof FavoritesViewHolder){
            String name = ((FavoritesAdapter)recyclerView.getAdapter()).getItem(adapterPosition).getFoodName();

            final Favorites deleteItem = (((FavoritesAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()));
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(viewHolder.getAdapterPosition());
            new Database(getBaseContext()).removeFavorites(deleteItem.getFoodId(), Common.current_user.getPhone());

            //Make snackbar
            Snackbar snackbar = Snackbar.make(rootLayout,name+"removed from favorites!",Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItme(deleteItem,deleteIndex);
                    new Database(getBaseContext()).addToFavorites(deleteItem);

                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
