package utils;

/*

   Store a pair of ints.

*/

public class IntPair {

  private int first_;
  private int second_;

  public IntPair(int first, int second)
  {
    first_ = first;
    second_ = second;
  }

  public int first()
  {
    return first_;
  }

  public int second()
  {
    return second_;
  }

  public String toString()
  {
    return "<"+first_+","+second_+">";
  }

  public boolean equals(Object x)
  {
    if (x instanceof IntPair) {
      IntPair ip = (IntPair)x;
      return (ip.first() == first()) && (ip.second() == second());
    } else
      return false;
  }

} // class IntPair
