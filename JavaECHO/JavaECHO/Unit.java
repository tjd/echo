// $Id: Unit.java,v 1.6 1997/07/20 20:10:13 tjdonald Exp $

package JavaECHO;

/*
 
   A basic neural-net unit that stores a real-valued activation between
   min=-1.0 and max=1.0.

*/

import debug.Assert;

class Unit {

  private final boolean invariantCheckingOn_ = false;

  public final static float min = -1.0f;
  public final static float max = 1.0f;

  //
  // min <= activation_ <= max
  //
  private float activation_;

  private String name_;
  private String descrip_;


  Unit(String name, String descrip, float act)
  {
    name_ = name;
    descrip_ = descrip;
    activation_ = act;
    checkInvariant();
  }

  Unit(String name, String descrip, double act)
  {
    this(name,descrip,(float)act);
  }

  Unit(String name, String descrip)
  {
    this(name,descrip,0.0);
  }

  Unit(String name)
  {
    this(name,"",0.0);
  }

  Unit(double act)
  {
    this((float)act);
  }

  Unit(float act)
  {
    this("","",act);
  }

  public String toString()
  {
    String result = "{"+name_+","+descrip_+","+activation_+"}";
    return result;
  }

  public boolean equals(Object x)
  {
    if (x instanceof Unit) {
      Unit u = (Unit)x;
      return (name_.equals(u.name_) && descrip_.equals(u.descrip_) &&
	      (activation_ == u.activation_));
    } else
      return false;
  }

  public int hashCode()
  {
    return name_.hashCode();
  }

  //
  // return the current activation
  // 
  public float activation()
  {
    return activation_;
  }

  //
  // PRE:  min <= act <= max
  // POST: activation_ is set to act
  //
  public void setActivation(float act)
  {
    activation_ = act;
    checkInvariant();
  }

  public String name()
  {
    return name_;
  }

  public String description()
  {
    return descrip_;
  }

  public void checkInvariant()
  {
    if (invariantCheckingOn_) {
      Assert.isTrue(activation_ >= min,"activation is >= "+min);
      Assert.isTrue(activation_ <= max,"activation is <= "+max);
      Assert.notNull(name_);
      Assert.notNull(descrip_);
    } // if
  }

} // class Unit
