package com.emika.app.presentation.adapter.ItemTouchHelper;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}