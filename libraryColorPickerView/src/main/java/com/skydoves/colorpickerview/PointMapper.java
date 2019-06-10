package com.skydoves.colorpickerview;

import android.graphics.Color;
import android.graphics.Point;

@SuppressWarnings("WeakerAccess")
class PointMapper {
  private PointMapper() {
  }

  protected static Point getColorPoint(ColorPickerView colorPickerView, Point point) {
    if (colorPickerView.getColorFromBitmap(point.x, point.y) != Color.TRANSPARENT) return point;
    Point center =
        new Point(colorPickerView.getMeasuredWidth() / 2, colorPickerView.getMeasuredHeight() / 2);
    return approximatedPoint(colorPickerView, point, center);
  }

  private static Point approximatedPoint(ColorPickerView colorPickerView, Point start, Point end) {
    if (getDistance(start, end) <= 3) return end;
    Point center = getCenterPoint(start, end);
    int color = colorPickerView.getColorFromBitmap(center.x, center.y);
    if (color == Color.TRANSPARENT) {
      return approximatedPoint(colorPickerView, center, end);
    } else {
      return approximatedPoint(colorPickerView, start, center);
    }
  }

  private static Point getCenterPoint(Point start, Point end) {
    return new Point((end.x + start.x) / 2, (end.y + start.y) / 2);
  }

  private static int getDistance(Point start, Point end) {
    return (int)
        Math.sqrt(
            Math.abs(end.x - start.x) * Math.abs(end.x - start.x)
                + Math.abs(end.y - start.y) * Math.abs(end.y - start.y));
  }
}
