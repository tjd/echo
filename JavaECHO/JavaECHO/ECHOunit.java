// $Id: ECHOunit.java,v 1.5 1997/07/20 20:10:13 tjdonald Exp $

package JavaECHO;

/*

   An ECHOunit is a Unit that stores information about what other units
   this unit explains, is explained by, and is a cohypothesis with.

*/

import debug.Assert;
import java.util.*;

final class ECHOunit extends Unit {

  private final boolean invariantCheckingOn_ = false;

  //
  // these are containers of Vectors, each Vector contains one set of 
  // units corresponding to one explanation of this unit
  //
  private Vector explainedBy_ = new Vector();
  private Hashtable explainerOf_ = new Hashtable();

  private Hashtable allExplainedByUnits_ = new Hashtable();

  private Boolean flag_;

  public ECHOunit(String name, String descrip, float act)
  {
    super(name,descrip,act);
  }

  public ECHOunit(String name, String descrip, double act)
  {
    this(name,descrip,(float)act);
  }

  ECHOunit(String name, String descrip)
  {
    this(name,descrip,0.0);
  }

  ECHOunit(String name)
  {
    this(name,"",0.0);
  }

  ECHOunit(double act)
  {
    this((float)act);
  }

  ECHOunit(float act)
  {
    this("","",act);
  }

  public boolean equals(Object x)
  {
    if (x instanceof ECHOunit) {
      ECHOunit u = (ECHOunit)x;
      return super.equals(u) && u.explainerOf_.equals(explainerOf_) &&
	u.explainedBy_.equals(u.explainedBy_);
    } else
      return false;
  }

  //
  // hashCode must return the same value when two ECHOunits are equal
  //
  public int hashCode()
  {
    return name().hashCode();
  }

  //
  // POST: returns a Hashtable of all the other units that this unit
  //       explains
  public Hashtable explains()
  {
    return explainerOf_;
  }

  //
  // POST: returns a Vector of all the unit sets that this unit is explained
  //       by
  //
  public Vector explainedBy()
  {
    return explainedBy_;
  }

  public ECHOunit getExplainedBy(String uname)
  {
    return (ECHOunit)allExplainedByUnits_.get(uname);
  }

  public ECHOunit getExplainedBy(ECHOunit u)
  {
    return getExplainedBy(u.name());
  }

  //
  // PRE: u is non-null
  // POST: u is a member of explainerOf_
  //
  public void addExplainerOf(ECHOunit u)
  {
    if (!explainerOf_.contains(u.name()))
      explainerOf_.put(u.name(),u);
  }

  //
  // PRE: v is non-null and non-empty set of ECHOunits that explain 
  //      this ECHOunit
  // POST: v is a member of explainedBy_ 
  //
  public void addExplainedBy(Vector v)
  {
    Assert.notNull(v);
    Assert.isTrue(v.size()>0);
    addAllUnits(v);
    explainedBy_.addElement(v);
  }

  private void addAllUnits(Vector v)
  {
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      if (!allExplainedByUnits_.containsKey(u.name()))
	allExplainedByUnits_.put(u.name(),u);
    } // while
  }

  public Hashtable allExplainedByUnits()
  {
    return allExplainedByUnits_;
  }

  //
  // PRE: u is non-null
  // POST: returns an explaination Vector from explainedBy_ of which 
  //       u is a member
  //
  public Vector explainedBy(ECHOunit u)
  {
    Vector v = new Vector(1);
    v.addElement(u);
    return explainedBy(v);
  }

  //
  // PRE: units and non-empty is non-null
  // POST: returns a Vector of some explanation contained in explainedBy_ 
  //       of which units is a subset; returns null if there is no such 
  //       Vector in explainedBy_
  //
  public Vector explainedBy(Vector units)
  {
    Assert.notNull(units);
    Assert.isTrue(units.size()>0,"units is non-empty");
    Enumeration e = explainedBy_.elements();
    while (e.hasMoreElements()) {
      Vector v = (Vector)e.nextElement();
      Enumeration ve = v.elements();
      while (ve.hasMoreElements()) {
	ECHOunit eu = (ECHOunit)ve.nextElement();
	Enumeration unitEnum = units.elements();
	while (unitEnum.hasMoreElements()) {
	  ECHOunit u = (ECHOunit)unitEnum.nextElement();
	  if (eu.name().equals(u.name()))
	    return v;
	} // while
      } // while
    } // while
    return null;
  }

  public boolean hasAsCohypotheses(ECHOunit a, ECHOunit b)
  {
    Enumeration e = explainedBy_.elements();
    while (e.hasMoreElements()) {
      Vector v = (Vector)e.nextElement();
      if (v.contains(a) && v.contains(b))
	return true;
    } // while
    return false;
  }

  //
  // PRE: u is non-null
  // POST: returns true if this unit is an explainer of u
  //
  public boolean explains(ECHOunit u)
  {
    return explainerOf_.containsKey(u.name());
  }


  public Vector someExplanationContainsBoth(ECHOunit a, ECHOunit b)
  {
    Enumeration e = explainedBy_.elements();
    while (e.hasMoreElements()) {
      Vector exp = (Vector)e.nextElement();
      if (exp.contains(a.name()) && exp.contains(b.name()))
	return exp;
    } // while
    return null;
  }

  public Boolean flag()
  {
    return flag_;
  }

  public void setFlag(Boolean flag)
  {
    flag_ = flag;
  }

  public void checkInvariant()
  {
    super.checkInvariant();
    if (invariantCheckingOn_) {
    
      if (explainerOf_ != null) {
	Enumeration e1 = explainerOf_.elements();
	while (e1.hasMoreElements()) {
	  Object x = e1.nextElement();
	  Assert.isTrue(x instanceof ECHOunit,"everything in explainerOf_"+
			" is an ECHOunit");
	} // while
      } // if

      if (explainedBy_ != null) {
	Enumeration e2 = explainedBy_.elements();
	while (e2.hasMoreElements()) {
	  Object x = e2.nextElement();
	  Assert.isTrue(x instanceof Vector,"everything in explainedBy_"+
			" is a Vector");
	} // while
      } // if
/*
      if (coHypotheses_ != null) {
	Enumeration e3 = coHypotheses_.elements();
	while (e3.hasMoreElements()) {
	  Object x = e3.nextElement();
	  Assert.isTrue(x instanceof ECHOunit,"everything in coHypotheses_"+
			" is an ECHOunit");
	} // while
      } // if
*/
    } // if
  }

} // class ECHOunit
