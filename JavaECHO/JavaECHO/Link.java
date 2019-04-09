package JavaECHO;

/*

   A Link connects two ECHOunits, and has a real-valued weight.
   
*/

final class Link {

  private ECHOunit a_;
  private ECHOunit b_;
  private float weight_;
  private Boolean flag_;
  private String hashkey_;

  //
  // PRE: a and b are non-null
  // POST: returns the ordered concatentation of a and b
  //
  public static String makeHashkey(String a, String b)
  {
    return (a.compareTo(b)<=0) ? a+b : b+a;
  }

  public static String makeHashkey(ECHOunit a, ECHOunit b)
  {
    return makeHashkey(a.name(),b.name());
  }

  public Link(ECHOunit a, ECHOunit b, float weight)
  {
    a_ = a;
    b_ = b;
    hashkey_ = makeHashkey(a_,b_);
    weight_ = weight;
  }


  public String toString()
  {
    return "{"+a_.name()+","+b_.name()+","+weight_+"}";
  }

  //
  // POST: returns true iff x is a Link whose units equal a_ and b_
  // 
  public boolean equals(Object x)
  {
    if (x instanceof Link) {
      Link l = (Link)x;
      String laName = l.a_.name();
      String lbName = l.b_.name();
      String aname = a_.name();
      String bname = b_.name();
      return (laName.equals(aname) && lbName.equals(bname)) ||
	(laName.equals(bname) && lbName.equals(aname));
    } else
      return false;
  }

  //
  // POST: returns a hashcode that is the sum of the hashcodes for a and b
  //
  public int hashCode()
  {
    return a_.name().hashCode()+b_.name().hashCode();
  }

  public String hashkey()
  {
    return hashkey_;
  }

  public ECHOunit endA()
  {
    return a_;
  }

  public ECHOunit endB()
  {
    return b_;
  }

  public float weight()
  {
    return weight_;
  }

  public void setWeight(float weight)
  {
    weight_ = weight;
  }

} // class Link
