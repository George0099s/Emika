package com.emika.app.presentation.adapter.profile.ItemTouchHelper;

public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}