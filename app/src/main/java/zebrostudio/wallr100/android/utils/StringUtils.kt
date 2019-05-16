package zebrostudio.wallr100.android.utils

fun String.equalsIgnoreCase(string: String): Boolean {
  return this.equals(string, true)
}