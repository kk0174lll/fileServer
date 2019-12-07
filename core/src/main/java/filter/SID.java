package filter;

import java.util.concurrent.atomic.AtomicLong;

public class SID
{
  private static final int RADIX = 36;

  private SID()
  {
  }

  private final static AtomicLong ids = new AtomicLong(System.currentTimeMillis());

  private static final char[] digits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

  public static String getSID()
  {
    return toSID(ids.incrementAndGet());
  }


  private static String toSID(final long id)
  {
    final char[] buf = new char[16];
    int pos = 16;
    long value = id;
    while (value > 0) {
      int d = (int)(value % RADIX);
      value = value / RADIX;
      buf[--pos] = digits[d];
    }
    return new String(buf, pos, buf.length - pos);
  }
}
