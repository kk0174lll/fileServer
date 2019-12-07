package utils;

public class Utils
{
  public static final String CONTENT_TYPE = "application/json";
  public static boolean isEmpty(String str) {
    return str == null || str.isEmpty() || str.trim().isEmpty();
  }

  public static String stringValue(Object o) {
    return stringValue(o, "");
  }

  public static String stringValue(Object o, String defaultValue) {
    if (null == o) {
      return defaultValue;
    } else if (o instanceof String) {
      String s = (String)o;
      return isEmpty(s) ? defaultValue : s.trim();
    } else {
      return o.toString();
    }
  }
}
