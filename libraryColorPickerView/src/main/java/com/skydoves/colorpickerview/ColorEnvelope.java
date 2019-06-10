package com.skydoves.colorpickerview;

/** ColorEnvelope is a wrapper class of colors for provide various forms of color. */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ColorEnvelope {

  private int color;
  private String hexCode;
  private int[] argb;

  public ColorEnvelope(int color) {
    this.color = color;
    this.hexCode = ColorUtils.getHexCode(color);
    this.argb = ColorUtils.getColorARGB(color);
  }

  /**
   * gets envelope's color.
   *
   * @return color.
   */
  public int getColor() {
    return color;
  }

  /**
   * gets envelope's hex code value.
   *
   * @return hex code.
   */
  public String getHexCode() {
    return hexCode;
  }

  /**
   * gets envelope's argb color.
   *
   * @return argb integer array.
   */
  public int[] getArgb() {
    return argb;
  }
}
