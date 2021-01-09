package com.damon.ventadiamante.viewholder;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.damon.ventadiamante.R;
import com.damon.ventadiamante.interfaces.ItemTouchHelperAdapter;

public class MyItemTouchHelper extends ItemTouchHelper.Callback {

    private final ItemTouchHelperAdapter mAdpater;

    public MyItemTouchHelper(ItemTouchHelperAdapter adapter){
        this.mAdpater = adapter;
    }


    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(
                ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.colorPrimaryDark)
        );
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG){
            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final  int drgaFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final  int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(drgaFlags,swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdpater.onItemMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        System.out.println("DIRECCION" + direction);
        mAdpater.onItemSwiped(viewHolder.getAdapterPosition(),direction);
    }
}
