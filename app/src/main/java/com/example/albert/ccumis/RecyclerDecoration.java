package com.example.albert.ccumis;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerDecoration extends RecyclerView.ItemDecoration {
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    final int spacing = 16;
    outRect.right = spacing;
    outRect.left = spacing;
    outRect.bottom = spacing;
  }
}
