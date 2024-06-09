package com.example.man.decorations;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private final Paint paint;

    public DividerItemDecoration(Context context, int colorResId) {
        paint = new Paint();
        paint.setColor(context.getResources().getColor(colorResId)); // 设置分割线颜色
        paint.setStrokeWidth(4); // 设置分割线宽度
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // 设置上下分割线，这里简单设置为1px的高度
        outRect.top = 4;
        outRect.bottom = 4;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + 4; // 这里是分割线的高度，可以根据需要调整
            c.drawRect(left, top, right, bottom, paint);
        }
    }
}