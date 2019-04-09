package JavaECHO;

/*

   Abstract base class for ECHO solvers.

*/

import java.util.*;
import debug.*;
import utils.*;

abstract class ECHOsolver {

  protected ECHO echo_;
  protected Vector accepted_;
  protected Vector rejected_;
  protected Vector neutral_;

  //
  // PRE: v is not null
  // POST: returns a nicely formatted summary of v
  //
  protected String summary(Vector v)
  {
    Assert.notNull(v);
    String result = "";
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      result += u.name()+"="+u.activation()+" ";
    } // while
    return result;
  }
  
  abstract public void solve();
  abstract public void summarizeRun();

  public Vector accepted()
  {
    return accepted_;
  }

  public Vector rejected()
  {
    return rejected_;
  }



} // class ECHOsolver
