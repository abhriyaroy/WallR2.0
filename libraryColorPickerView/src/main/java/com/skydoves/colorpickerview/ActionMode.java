package com.skydoves.colorpickerview;

/**
 * ActionMode controls an action about {@link com.skydoves.colorpickerview.listeners.ColorPickerViewListener}
 * invoking.
 */
public enum ActionMode {
  /** invokes listener always by tapping or dragging. */
  ALWAYS,

  /** invokes listener only when finger released. */
  LAST
}
