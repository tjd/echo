package JavaECHO;

/*

   Holds a pair of Units.

*/

final class UnitPair {

  private ECHOunit a_;
  private ECHOunit b_;

  UnitPair(ECHOunit a, ECHOunit b)
  {
    a_ = a;
    b_ = b;
  }

  public ECHOunit first()
  {
    return a_;
  }

  public ECHOunit second()
  {
    return b_;
  }

  public String toString()
  {
    return "("+a_.name()+","+b_.name()+")";
  }

  public boolean same()
  {
    return a_.name().equals(b_.name());
  }

} // class UnitPair
