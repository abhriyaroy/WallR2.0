package zebrostudio.wallr100.android.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridItemDecorator extends RecyclerView.ItemDecoration {

  private int sizeGridSpacingPx;
  private int gridSize;

  private boolean mNeedLeftSpacing = false;

  public GridItemDecorator(int gridSpacingPx, int gridSize) {
    sizeGridSpacingPx = gridSpacingPx;
    this.gridSize = gridSize;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    int frameWidth =
        (int) ((parent.getWidth() - (float) sizeGridSpacingPx * (gridSize - 1)) / gridSize);
    int padding = parent.getWidth() / gridSize - frameWidth;
    int itemPosition =
        ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
    if (itemPosition < gridSize) {
      outRect.top = 0;
    } else {
      outRect.top = sizeGridSpacingPx;
    }
    if (itemPosition % gridSize == 0) {
      outRect.left = 0;
      outRect.right = padding;
      mNeedLeftSpacing = true;
    } else if ((itemPosition + 1) % gridSize == 0) {
      mNeedLeftSpacing = false;
      outRect.right = 0;
      outRect.left = padding;
    } else if (mNeedLeftSpacing) {
      mNeedLeftSpacing = false;
      outRect.left = sizeGridSpacingPx - padding;
      if ((itemPosition + 2) % gridSize == 0) {
        outRect.right = sizeGridSpacingPx - padding;
      } else {
        outRect.right = sizeGridSpacingPx / 2;
      }
    } else if ((itemPosition + 2) % gridSize == 0) {
      mNeedLeftSpacing = false;
      outRect.left = sizeGridSpacingPx / 2;
      outRect.right = sizeGridSpacingPx - padding;
    } else {
      mNeedLeftSpacing = false;
      outRect.left = sizeGridSpacingPx / 2;
      outRect.right = sizeGridSpacingPx / 2;
    }
    outRect.bottom = 0;
  }
}

