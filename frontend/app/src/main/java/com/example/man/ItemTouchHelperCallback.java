package com.example.man;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man.adapters.NoteCategoriesAdapter;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {
    private final NoteCategoriesAdapter mAdapter;
    private Paint mPaint;

    public ItemTouchHelperCallback(NoteCategoriesAdapter adapter) {
        mAdapter = adapter;
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ff423d"));
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.RIGHT;
        return makeMovementFlags(0, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if (mAdapter.isDeletable(position)) {
            mAdapter.removeItem(position);
        } else {
            mAdapter.notifyItemChanged(position);
            showCannotDeleteDialog(viewHolder.itemView.getContext());
        }
    }

    private void showCannotDeleteDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setMessage("该项不可删除")
                .setPositiveButton("确定", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                // 向右滑动
                mPaint.setColor(Color.parseColor("#ff423d"));
                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), mPaint);
                mPaint.setColor(Color.WHITE);
                mPaint.setTextSize(50);
                c.drawText("删除", itemView.getLeft() + width, (float) itemView.getBottom() - width, mPaint);
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}

