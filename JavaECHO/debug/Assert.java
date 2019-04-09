package debug;   

public final class Assert {

  public static void notNull(Object x)
  {
    //System.out.print("Assert checking if x is null...");
    if (x == null) {
      //System.out.println("x is null, throwing exception");
      throw new NullPointerException("Assert: null object encountered");
    } // if
    //System.out.println("okay, x not null");
  }

  public static void isTrue(boolean b)
  {
    if (!b)
      throw new RuntimeException("Assert: expression not true");
  }

  public static void isTrue(boolean b, String msg)
  {
    if (!b)
      throw new RuntimeException("Assert: expression not true: "+msg);
  }

  public static void isFalse(boolean b)
  {
    if (b)
      throw new RuntimeException("Assert: expression not false");
  }

  public static void isFalse(boolean b, String msg)
  {
    if (b)
      throw new RuntimeException("Assert: expression not false: "+msg);
  }

} // class Assert
