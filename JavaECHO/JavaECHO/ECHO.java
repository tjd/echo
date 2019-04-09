// $Id: ECHO.java,v 1.10 1997/07/20 20:10:13 tjdonald Exp $

package JavaECHO;

/*

   The basic ECHO class for creating an instance of an ECHO engine.


   Note:
     - data statements with a strength parameter are allowed, but they
     make no difference

*/

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import debug.Assert;
import predicate.*;

final class ECHO {
  private final static boolean invariantCheckingOn_ = false;
  private final static boolean issueWarningsOn_ = false;

  public final static String explainPred = "explain";
  public final static String contradictPred = "contradict";
  public final static String dataPred = "data";
  public final static String analogyPred = "analogous";
  public final static String propositionPred = "proposition";

  public final static float defaultSimplicityImpact = 1.0f;
  public final static float defaultWeight = 0.0f;
  public final static float defaultThetaDecay = 0.05f;
  public final static float defaultExcitationWeight = 0.04f;
  public final static float defaultDataExcitation = 0.05f;
  public final static float defaultInhibitionWeight = -0.06f;

  public final static boolean defaultCalcCompetitors = true;

  //
  // the default values are taken from p.102 of Thagard's "Conceptual 
  // Revolutions"
  //
  private float simplicityImpact_ = defaultSimplicityImpact;
  private float defaultWeight_ = defaultWeight;
  private float thetaDecay_ = defaultThetaDecay;
  private float excitationWeight_ = defaultExcitationWeight;
  private float dataExcitation_ = defaultDataExcitation;
  private float inhibitionWeight_ = defaultInhibitionWeight;

  private boolean calcCompetitors_ = defaultCalcCompetitors;

  private String name_;
  private String descrip_;
  
  //
  // contains all the ECHOunits in this ECHO
  //
  private Hashtable units_ = new Hashtable();

  //
  // the list of linked ECHOunits is stored in links_
  //
  private Hashtable links_ = new Hashtable();

  //
  // special evidence ECHOunit
  //
  final public ECHOunit specialUnit = new ECHOunit("special",
						   "the special evidence unit",
						   1f);

  //
  // record all the pairs of elements that compete with each other
  //
  private Vector competitors_ = new Vector();


  ECHO(String name, String descrip, InputStream is) throws IOException
  {
    name_ = name;
    descrip_ = descrip;
    units_.put(specialUnit.name(),specialUnit);
    StreamTokenizer st = List.makeTokenizer(is);
    parseInput(st);
    if (calcCompetitors_)
      calculateCompetitors();
    checkInvariant();
  }

  ECHO(String name, String descrip, String inputString) throws IOException
  {
    name_ = name;
    descrip_ = descrip;
    units_.put(specialUnit.name(),specialUnit);
    StreamTokenizer st = List.makeTokenizer(inputString);
    parseInput(st);
    if (calcCompetitors_)
      calculateCompetitors();
    checkInvariant();
  }

  ECHO(String name, String descrip, InputStream is, 
       float simplicityImpact, float defaultWeight, float excitationWeight,
       float dataExcitation, float inhibitionWeight) 
    throws IOException
  {
    name_ = name;
    descrip_ = descrip;
    setParameters(simplicityImpact,defaultWeight,excitationWeight, 
		  dataExcitation,inhibitionWeight);
    units_.put(specialUnit.name(),specialUnit);
    StreamTokenizer st = List.makeTokenizer(is);
    parseInput(st);
    if (calcCompetitors_)
      calculateCompetitors();
    checkInvariant();
  }

  ECHO(String name, String descrip, String inputString, 
       float simplicityImpact, float defaultWeight, float excitationWeight,
       float dataExcitation, float inhibitionWeight) 
    throws IOException
  {
    name_ = name;
    descrip_ = descrip;
    setParameters(simplicityImpact,defaultWeight,excitationWeight, 
		  dataExcitation,inhibitionWeight);
    units_.put(specialUnit.name(),specialUnit);
    StreamTokenizer st = List.makeTokenizer(inputString);
    parseInput(st);
    if (calcCompetitors_)
      calculateCompetitors();
    checkInvariant();
  }

  private void setParameters(float simplicityImpact, float defaultWeight, 
			     float excitationWeight, float dataExcitation, 
			     float inhibitionWeight)

  {
    simplicityImpact_ = simplicityImpact;
    defaultWeight_ = defaultWeight;
    excitationWeight_ = excitationWeight;
    dataExcitation_ = dataExcitation;
    inhibitionWeight_ = inhibitionWeight;
  }
    //System.out.println("simplicityImpact_="+simplicityImpact_);
    //System.out.println("defaultWeight_="+defaultWeight_);
    //System.out.println("excitationWeight_="+excitationWeight_);
    //System.out.println("dataExcitation_="+dataExcitation_);
    //System.out.println("inhibitionWeight_="+inhibitionWeight_);
  //}

  //
  // create an ECHO warning message
  //
  private String warning(String warn)
  {
    return "ECHO Warning: "+warn;
  }

  private void issueWarning(String warn)
  {
    if (issueWarningsOn_)
      msg(warning(warn));
  }

  private void msg(String message)
  {
    System.out.println(message);
  }

  //
  // st is assumed to be a list of ECHO predicates
  //
  private void parseInput(StreamTokenizer st) throws IOException
  {
    //int nt = StreamTokenizer.TT_WORD;
    while (st.nextToken() != StreamTokenizer.TT_EOF) {
      st.pushBack();
      Predicate p = new Predicate(st);
      parsePredicate(p);
    } // while
  }

  //
  // PRE: p is non-null
  // POST: performs the additions to the ECHO graph required by the
  //       ECHO predicate; if p is not a legal ECHO predicate, an 
  //       IOException is thrown
  //
  private void parsePredicate(Predicate p) throws IOException
  {
    Assert.notNull(p);
    String name = p.name();

    if (name.equalsIgnoreCase(explainPred)) {
      parseExplain(p);
    } else if (name.equalsIgnoreCase(dataPred)) {
      parseData(p);
    } else if (name.equalsIgnoreCase(contradictPred)) {
      parseContradict(p);
    } else if (name.equalsIgnoreCase(analogyPred)) {
      parseAnalogy(p);
    } else if (name.equalsIgnoreCase(propositionPred)) {
      parseProposition(p);
    } else {
      throw new IOException("ECHO: "+name+" is not a legal predicate name. "+
			    "Must be one of "+explainPred+", "+dataPred+", "+
			    contradictPred+", or "+analogyPred);
    } // if
  }

  //
  // PRE: p's name is explainPred
  // POST: adds the given explanation predicate to ECHO, adding all necessary
  //       links; throws an exception if p is not a legal explains predicate
  //
  private void parseExplain(Predicate p) throws IOException
  {
    //msg("in parseExplain...");
    int arity = p.arity();
    float weight;
    List propList;
    String explainee;

    if (arity == 2) {
      //msg("arity == 2");
      propList = (List)p.arg(0);
      explainee = (String)p.arg(1);
      weight = (float)explainWeight(propList.length());
    } else if (arity == 3) {
      //msg("arity == 3");
      propList = (List)p.arg(0);
      explainee = (String)p.arg(1);
      float strength = Float.valueOf((String)p.arg(2)).floatValue();
      weight = (float)(strength*explainWeight(propList.length()));
    } else {
      throw new IOException("ECHO: an explains predicate must have 2 or 3 "+
			    "arguments: "+p);
    } // if

    //msg("List="+propList+", explainee="+explainee);
    //
    // make sure all the units are added
    //
    addUnit(explainee);
    for(int i=0; i<propList.length(); ++i) {
      addUnit((String)propList.elementAt(i));
    } // for

    //
    // add the explanation links
    //
    addExplanation(propList,explainee,weight);
  }

  private double explainWeight(int numCoHypotheses)
  {
    double result = 
      (excitationWeight_/Math.pow(numCoHypotheses,simplicityImpact_));
    //msg("*** explainWeight("+numCoHypotheses+") = "+result);

    return result;
  }

  //
  // PRE: propList and explainee are legal and have been added as Units to
  //      this object
  // POST: propList's elements are added as explainers of explainee; each 
  //       element of propList is marked as being an explainer of explainee
  //
  private void addExplanation(List propList, String explainee, float weight)
  {
    //msg("entering addExplanation...");
    ECHOunit unitToExplain = (ECHOunit)units_.get(explainee);
    int pSize = propList.length();

    //
    // propList's elements are added as being explained by explainee, and
    // each element of propList is marked as being an explainer of explinee
    //
    Vector temp = new Vector(pSize);
    for(int i=0; i<pSize; ++i) {
      ECHOunit u = (ECHOunit)units_.get((String)propList.elementAt(i));
      u.addExplainerOf(unitToExplain);
      additiveAddLink(u,unitToExplain,weight);
      temp.addElement(u);
    } // for

    unitToExplain.addExplainedBy(temp);
    
    //
    // all members of propList of are cohypotheses of each other
    //
    for(int i=0; i<pSize; ++i) {
      ECHOunit u = (ECHOunit)units_.get((String)propList.elementAt(i));
      for(int j=0; j<pSize; ++j) {
	if (i != j) {
	  ECHOunit v = (ECHOunit)units_.get((String)propList.elementAt(j));
	  additiveAddLink(u,v,weight);
	} // if
      } // for
    } // for
    //msg("... leaving addExplanation");
  }

  //
  // PRE: p's name is dataPred
  // POST: adds the given data predicate to ECHO, adding all necessary
  //       links; throws an exception if p is not a legal data predicate
  //
  private void parseData(Predicate p) throws IOException
  {
    //msg("in parseData...");
    int arity = p.arity();
    if (arity > 0) {
      List propList = p.args();
      //msg("List="+propList);
      for(int i=0; i<propList.length(); ++i) {
	//msg("propList.elementAt("+i+")="+propList.elementAt(i));
	Object prop = propList.elementAt(i);
	if (prop instanceof String) {
	  String s = (String)prop;
	  addUnit(s);
	  additiveAddLink(s,specialUnit.name(),dataExcitation_);
	} else if (prop instanceof List) {
	  List l = (List)prop;
	  if (l.length() == 2) {
	    String s = (String)l.elementAt(0);
	    int weight = Integer.valueOf((String)l.elementAt(1)).intValue();
	    //
	    // cliffhanger
	    //
	  } else
	    throw new IOException("ECHO: a data argument must be either a"+
				  " unit name or a list of the form "+
				  "(proposition,weight)");
	} else
	  throw new IOException("ECHO: a data argument must be either a"+
				" unit name or a list of the form "+
				"(proposition,weight)");
      } // for
    } else
      throw new IOException("ECHO: a data predicate must have at least 1"+
			    " argument: "+p);
  }

  //
  // PRE: p's name is contradictPred
  // POST: adds the given contradiction predicate to ECHO, adding all 
  //       necessary links; throws an exception if p is not a legal 
  //       contradiction predicate
  //
  private void parseContradict(Predicate p) throws IOException
  {
    //msg("in parseContradict...");
    int arity = p.arity();
    if (arity == 2) {
      String prop1 = (String)p.arg(0);
      String prop2 = (String)p.arg(1);
      //msg("prop1="+prop1+", prop2="+prop2);
      addUnit(prop1);
      addUnit(prop2);
      additiveAddLink(prop1,prop2,inhibitionWeight_);
    } else
      throw new IOException("ECHO: a contradict predicate must have exactly"+
			    " 2 arguments: "+p);
  }

  //
  // PRE: p's name is dataPred
  // POST: adds the given analogy predicate to ECHO, adding all necessary
  //       links; throws an exception if p is not a legal analogy predicate
  //
  private void parseAnalogy(Predicate p) throws IOException
  {
    //msg("in parseAnalogy ...");
    int arity = p.arity();
    if (arity == 2) {
      List propPair1 = (List)p.arg(0);
      List propPair2 = (List)p.arg(1);
      if (propPair1.length() == 2 && propPair2.length() == 2) {
	//msg("propPair1="+propPair1+", propPair2="+propPair2);
	String p1 = (String)propPair1.elementAt(0);
	String p2 = (String)propPair1.elementAt(1);
	String p3 = (String)propPair2.elementAt(0);
	String p4 = (String)propPair2.elementAt(1);
	addUnit(p1);
	addUnit(p2);
	addUnit(p3);
	addUnit(p4);
	additiveAddLink(p1,p2,excitationWeight_);
	additiveAddLink(p3,p4,excitationWeight_);
      } else
	throw new IOException("ECHO: each argument of an analogy predicate"+
			      " must be a list of two propostions: "+p);
    } else
      throw new IOException("ECHO: an analogy predicate must have exactly 2"+
			    " arguments: "+p);
  }

  //
  // PRE: p's name is propositionPred
  // POST: adds the given proposition predicate to ECHO, adding all necessary
  //       links; throws an exception if p is not a legal proposition 
  //       predicate
  //
  private void parseProposition(Predicate p) throws IOException
  {
    //msg("in parseProposition...");
    int arity = p.arity();
    if (arity == 2) {
      String name = (String)p.arg(0);
      String sentence = (String)p.arg(1);
      //msg("name="+name+", sentence="+sentence);
      addUnit(name,sentence);
    } else
      throw new IOException("ECHO: a propostion predicate must have exactly"+
			    " 2 arguments: "+p);
  }


  //
  // PRE: all the units and edge-weights have been added
  // POST: inhibition links are added between competing nodes, as described
  //       on p.100, Table 4.4 of CR
  //
  private void calculateCompetitors()
  {
    //msg("Calculating competition links...");
    Enumeration allUnits = units_.elements();

    while (allUnits.hasMoreElements()) {
      ECHOunit u = (ECHOunit)allUnits.nextElement();
      //msg("checking to see who competes to explain "+u.name());
      Vector pairs = allDifferentPairs(u.allExplainedByUnits());
      //msg("all pairs = "+pairs);
      //
      // now prune pairs
      //
      competitionPrunePairs(pairs,u);
      
      //msg("prune pairs == "+pairs);
      Enumeration e = pairs.elements();
      while (e.hasMoreElements()) {
	UnitPair up = (UnitPair)e.nextElement();
	ECHOunit p = up.first();
	ECHOunit q = up.second();
	
	//
	// since a contradiction is the only other way for an inhibitory
	// link to be added, first check to see if p and q are connected
	// by an inhibitory link
	//
	if (!hasInhibitoryLink(p,q)) {
	  float w = (float)competitionWeight(p,q);
	  additiveAddLink(p,q,w);
	  //msg("  competition link added: "+p.name()+", "+q.name()+", "+w);
	} // if
      } // while

    } // while

    //msg("... competition links calculated");
  }

  //
  // PRE: pairs is a Vector of UnitPairs holding all pairs of the explainers
  //      of E
  // POST: removes all pairs from the pair Vector that do *not* compete to 
  //       explain E
  //
  private void competitionPrunePairs(Vector pairs, ECHOunit E)
  {
    Assert.notNull(pairs);
    Assert.notNull(E);
    for(int i=0; i<pairs.size(); ++i) {
      //msg("i="+i);
      UnitPair up = (UnitPair)pairs.elementAt(i);
      if (up.same() || !competingPair(up,E)) {
	pairs.removeElementAt(i);
	--i;
      } // if
    } // for
  }

  //
  // PRE: up and E are non-null
  // POST: returns true if up holds two propositions that compete to
  //       explain E, false otherwise
  //
  private boolean competingPair(UnitPair up, ECHOunit E)
  {
    ECHOunit p = up.first();
    ECHOunit q = up.second();

    return competingPair(p,q,E);
  }

  //
  // PRE: p, q, and E are non-null
  // POST: returns true if p and q compete to explain E, false 
  //       otherwise; p and q compete with each other if the three 
  //       conditions listed in Table 4.4 on p.100 of CR hold
  // 
  private boolean competingPair(ECHOunit p, ECHOunit q, ECHOunit E)
  {
    boolean PexplainsQ = p.allExplainedByUnits().contains(q);
    boolean QexplainsP = q.allExplainedByUnits().contains(p);
    
    if (p.name().equals(q.name()))
      return false;

    //if (PexplainsQ) {
    //  msg("  Pruning pair: "+p.name()+" explains "+q.name());
    //} // if
    //if (QexplainsP) {
    //  msg("  Pruning pair: "+q.name()+" explains "+p.name());
    //} // if
    if (PexplainsQ || QexplainsP)
      return false;

    //
    // this could be simplified by storing the proper cohypotheses in
    // each ECHOunit
    //
    Enumeration e = units_.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      if (u.hasAsCohypotheses(p,q)) {
	//msg("  Pruning pair: "+E.name()+" is explained by both "+q.name()+
	//    " and "+p.name());
	return false;
      } // if
    } // while

    competitors_.addElement(new UnitPair(p,q));
    //msg("  "+p.name()+" and "+q.name()+" compete!");
    return true;
  }


  //
  // PRE: a is non-null and contains only ECHOunits
  // POST: returns a Vector of all pairs (stored as UnitPairs) of the 
  //       elements in h
  //
  private Vector allDifferentPairs(Hashtable h)
  {
    Vector result = new Vector(h.size());
    Vector temp = new Vector(h.size());
    Enumeration a = h.elements();
  
    while (a.hasMoreElements()) {
      ECHOunit u1 = (ECHOunit)a.nextElement();
      temp.addElement(u1);
      Enumeration b = h.elements();
      while (b.hasMoreElements()) {
	ECHOunit u2 = (ECHOunit)b.nextElement();
	if (!temp.contains(u2)) {
	  UnitPair up = new UnitPair(u1,u2);
	  result.addElement(up);
	  //msg("allPairs: "+up+" added");
	} // if
      } // while
    }
    return result;
  }

  //
  // PRE: name and sentence are non-null
  // POST: this object has exactly one unit called name, and with sentence
  //       as its description
  //
  private void addUnit(String name, String sentence)
  {
    Assert.notNull(name);
    Assert.notNull(sentence);
    if (!hasUnit(name)) {
      ECHOunit u = new ECHOunit(name,sentence);
      units_.put(name,u);
    } // if
  }

  //
  // PRE: name is non-null
  // POST: this object has exactly one unit called name with "" as its
  //       description
  //
  private void addUnit(String name)
  {
    addUnit(name,"");
  }

  //
  // add the given weight to the link between a and b; if there is no link
  // between a and b, then one is created
  //
  // PRE: a and b are existing units
  // POST: a and b have a link between them with weight equal to the previous
  //       link weight plus the supplied weight, or, if a and b had no link,
  //       then their weight is just the current weight
  private void additiveAddLink(ECHOunit a, ECHOunit b, float weight)
  {
    Assert.isTrue(hasUnit(a));
    Assert.isTrue(hasUnit(b));
    simpleAddLink(a,b,0);
    //msg("<++> adding "+weight+" to link between "+a.name()+" and "+b.name());
    Link l = new Link(a,b,weight);
    Link storedLink = (Link)links_.get(l.hashkey());
    storedLink.setWeight(storedLink.weight()+weight); 
  }

  private void additiveAddLink(String a, String b, float weight)
  {
    ECHOunit au = (ECHOunit)units_.get(a);
    ECHOunit bu = (ECHOunit)units_.get(b);
    additiveAddLink(au,bu,weight);
  }

  //
  // create a link between Units a and b, if one does not already exist
  // 
  // PRE: a and b are units in this object
  // POST: units a and b are linked
  //
  private void simpleAddLink(ECHOunit a, ECHOunit b, float weight)
  {
    Assert.isTrue(hasUnit(a));
    Assert.isTrue(hasUnit(b));
    if (a.name().equals(b.name()))
      issueWarning("node "+ a.name()+
		   " cannot be linked to itself. No change made.");
    else if (!hasLink(a,b)) {
      //msg("<--> adding new link between "+a.name()+" and "+
	//  b.name());
      Link l = new Link(a,b,weight);
      links_.put(l.hashkey(),l);
      //links_.put(b.name(),l);
    } else
      issueWarning("link between "+a.name()+" and "+
		   b.name()+" already exists. No change made.");
  }

  private void simpleAddLink(String a, String b, float weight)
  {
    ECHOunit au = (ECHOunit)units_.get(a);
    ECHOunit bu = (ECHOunit)units_.get(b);
    simpleAddLink(au,bu,weight);
  }

/*
  //
  // PRE: props is a legal list from the first argument of an explain
  //      predicate, and all of the props have been added as units to this
  //      object
  // POST: links between each pair of units on props have created with the
  //       given weight on the links
  //
  private void makeAllLinks(List props, float weight)
  {
    //msg("entering makeAllLinks...");
    //msg("  props.length()="+props.length());
    int pSize = props.length();
    for(int i=0; i<pSize-1; ++i) {
      String a = (String)props.elementAt(i);
      for(int j=i+1; j<pSize; ++j) {
	String b = (String)props.elementAt(j);
	//msg("linking "+a+" and "+b+" with weight "+weight);
	additiveAddLink(a,b,weight);
      } // for
    } // for
    //msg("...leaving makeAllLinks");
  }

  //
  // PRE: propPairs is a list of UnitPairs of existing units
  // POST: links between each pair of units on propPairs have created with the
  //       given weight on the links
  //
  private void makeAllLinks(Vector propPairs)
  {
    Enumeration e = propPairs.elements();
    while (e.hasMoreElements()) {
      UnitPair p = (UnitPair)e.nextElement();
      float weight = (float)competitionWeight(p.first(),p.second());
      additiveAddLink(p.first(),p.second(),weight);
    } // while
  }
*/

  //
  // PRE: a and b are competing pairs
  //
  private double competitionWeight(ECHOunit a, ECHOunit b)
  {
    //Assert.isTrue(competingPair(a,b),"a and b are competing units");
    //msg("--> calculating competitionWeight("+a.name()+", "+b.name()+")");
    Vector ebb = explainedByBoth(a,b);
    Assert.isTrue(ebb.size()>0,"more than 0 units explained by both");
    float numerator = 2*inhibitionWeight_*ebb.size();
    int denom = 0;
    Enumeration e = ebb.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      denom += u.explainedBy().size();
    } // while

    Assert.isTrue(denom > 0,"denominator is greater than 0");
    return numerator/denom;
  }
    

  //
  // PRE: a and b are non-null
  // POST: returns a vector of ECHOunits that have both a and b in at least
  //       one of their explanations
  //
  private Vector explainedByBoth(ECHOunit a, ECHOunit b)
  {
    //msg("in explainedByBoth("+a.name()+", "+b.name()+")...");
    Vector result = new Vector();
    Enumeration e = units_.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      //msg(u.name()+".explainedBy() = "+u.explainedBy());
      boolean ebb = u.getExplainedBy(a) != null &&
	            u.getExplainedBy(b) != null;
      if (ebb) {
	result.addElement(u);
	//msg(u.name()+" explained by both");
      } else {
	//msg(u.name()+" not explained by both");
      }
    } // while
    //msg("explainedByBoth("+a.name()+", "+b.name()+") = "+result);
    return result;
  }

  //
  // PRE: h and g are non-nulll
  // POST: returns the cardinality of the intersection of h and g
  //
  private int countMatches(Enumeration e, Hashtable g)
  {
    int count = 0;
    while (e.hasMoreElements()) {
      if (g.contains(e.nextElement()))
	++count;
    } // while
    return count;
  }

  private Vector hashUnion(Hashtable h, Hashtable g)
  {
    Vector dup = duplicates(h,g);
    Vector result = new Vector();
    Enumeration eh = h.elements();
    while (eh.hasMoreElements()) {
      result.addElement(eh.nextElement());
    } // while

    Enumeration eg = g.elements();
    while (eg.hasMoreElements()) {
      Object x = eg.nextElement();
      if (!dup.contains(x))
	result.addElement(x);
    } // while

    return result;
  }

  private Vector duplicates(Hashtable h, Hashtable g)
  {
    Vector result = new Vector();
    Enumeration e = h.elements();
    while (e.hasMoreElements()) {
      Object x = e.nextElement();
      if (g.contains(x))
	result.addElement(x);
    } // while
    return result;
  }

//////////////////////////////////////////////////////////////////


  public String name()
  {
    return name_;
  }

  public String description()
  {
    return descrip_;
  }

  //
  // returns true if this object has a unit named name
  // 
  public boolean hasUnit(String name) 
  {
    return units_.containsKey(name);
  }

  public boolean hasUnit(ECHOunit u)
  {
    return hasUnit(u.name());
  }

  //
  // PRE: a and b are units of this object
  // POST: returns true if this object has a link between units a and b
  // 
  public boolean hasLink(ECHOunit a, ECHOunit b)
  {
    Assert.isTrue(hasUnit(a));
    Assert.isTrue(hasUnit(b));
    return links_.containsKey(Link.makeHashkey(a,b));
  }

  //
  // PRE: a and b are units of this object
  // POST: returns true if a and b are connected via an inhibitory link
  //
  public boolean hasInhibitoryLink(ECHOunit a, ECHOunit b)
  {
    Assert.isTrue(hasUnit(a));
    Assert.isTrue(hasUnit(b));
    Object x = links_.get(Link.makeHashkey(a,b));
    if (x == null)
      return false;
    else {
      Link l = (Link)x;
      return l.weight()<0;
    } // if
  }

  //
  // PRE: a and b are units of this object
  // POST: returns true if this object has a link between units a and b
  // 
  public boolean hasLink(ECHOunit a, ECHOunit b, float weight)
  {
    Assert.isTrue(hasUnit(a));
    Assert.isTrue(hasUnit(b));
    Object x = links_.get(Link.makeHashkey(a,b));
    if (x == null)
      return false;
    else {
      Link l = (Link)x;
      return (weight == l.weight());
    } // if
  }

  //////////////////////////////////////////////////////////////////
  
  //
  // set/get methods for ECHO parameters
  // 

//  public void setSpecialUnit(ECHOunit u)
//  {
//    specialUnit = u;
//  }

  public ECHOunit getSpecialUnit()
  {
    return specialUnit;
  }

  public void setSimplicityImpact(float f)
  {
    simplicityImpact_ = f;
  }

  public float getSimplicityImpact()
  {
    return simplicityImpact_;
  }

  public void setDefaultWeight(float f)
  {
    defaultWeight_ = f;
  }

  public float getDefaultWeight()
  {
    return defaultWeight_;
  }


  public void setExcitationWeight(float f)
  {
    excitationWeight_ = f;
  }

  public float getExcitationWeight()
  {
    return excitationWeight_;
  }

  public void setInhibitionWeight(float f)
  {
    inhibitionWeight_ = f;
  }

  public float getInhibitionWeight()
  {
    return inhibitionWeight_;
  }

  public void setDataExcitation(float f)
  {
    dataExcitation_ = f;
  }

  public float getDataExcitation()
  {
    return dataExcitation_;
  }

  public int numUnits()
  {
    return units_.size();
  }

  public Hashtable allUnits()
  {
    return units_;
  }

  public int numLinks()
  {
    return links_.size();
  }

  public Hashtable allLinks()
  {
    return links_;
  }

  public boolean calcCompetitors()
  {
    return calcCompetitors_;
  }

  public void allowCompetition()
  {
    calcCompetitors_ = true;
  }

  public void disallowCompetition()
  {
    calcCompetitors_ = false;
  }

//////////////////////////////////////////////////////////////////

  public String listUnits()
  {
    String result = "";
    Enumeration e = units_.elements(); 
    for(int i=0; e.hasMoreElements(); ++i) {
      ECHOunit u = (ECHOunit)e.nextElement();
      result += "unit("+i+"): "+u+"\n";
      //result += "unit("+i+"): "+u+", co="+u.coHypotheses()+"\n";
    } // for
    
    return result;
  }

  public String listLinks()
  {
    String result = "";
    Enumeration e = links_.keys();
    for(int i=0; e.hasMoreElements(); ++i) {
      Link l = (Link)links_.get(e.nextElement());
      result += "link("+i+"): "+l+"\n";
    } // while
    return result;
  }

  public String listCompetitors()
  {
    Enumeration e = competitors_.elements();
    if (!e.hasMoreElements())
      return "no competing units found";
    else {
      String result = "";
      for(int i=0; e.hasMoreElements(); ++i) {
	UnitPair up = (UnitPair)e.nextElement();
	result += i+": ("+up.first()+","+up.second()+")\n";
      } // for
      return result;
    } // if
  }

//////////////////////////////////////////////////////////////////
//
// methods for making Strings
//

  public String makeGraphString()
  {
    Enumeration e = links_.elements();
    Link fl = (Link)e.nextElement();
    String result = fl.endA().name()+"-"+fl.endB().name()+
      ((fl.weight()>0) ? "" : "/300");
    while (e.hasMoreElements()) {
      Link l = (Link)e.nextElement();
      result += ","+l.endA().name()+"-"+l.endB().name()+
	((l.weight()>0) ? "" : "/300");
    } // while
    return result;
  }

  public void checkInvariant()
  {
    //System.out.println(makeGraphString());
    if (invariantCheckingOn_) {
      Assert.notNull(name_);
      Assert.notNull(descrip_);
      Enumeration ue = units_.elements(); 
      while (ue.hasMoreElements()) {
	Object u = ue.nextElement();
	Assert.isTrue(u instanceof ECHOunit,
		      "every member of units_ is a unit");
      } // while 
      
      Enumeration le = links_.elements();
      while (le.hasMoreElements()) {
	Object l = le.nextElement();
	Assert.isTrue(l instanceof Link,"every member of links_ is a link");
	Link ll = (Link)l;
	Assert.isTrue(hasUnit(ll.endA()),"every link end is a unit");
	Assert.isTrue(hasUnit(ll.endB()),"every link end is a unit");
      } // while
    } // if
  }

} // class ECHO
