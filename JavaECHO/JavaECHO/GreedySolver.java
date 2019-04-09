// $Id: GreedySolver.java,v 1.6 1997/08/15 18:47:55 tjdonald Exp $

package JavaECHO;

import java.util.*;
import debug.*;
import utils.*;

final class GreedySolver extends ECHOsolver {

  private ECHOunit[] units_;
  private Link[] links_;
  private IntPair[] unitLinks_;
  private float minScore_;

  private int maxFlips_;
  private int maxTries_;

  private java.util.Random rand_ = new java.util.Random();
  
  GreedySolver(ECHO echo, int maxFlips, int maxTries)
  {
    Assert.notNull(echo);
    echo_ = echo;
    maxFlips_ = maxFlips;
    maxTries_ = maxTries;
    getUnits();
    getLinks();
    getUnitLinks();
    accepted_ = new Vector(units_.length);
    rejected_ = new Vector(units_.length);
  }

  //
  // PRE: echo_ has been initialized
  //
  private void getUnits()
  {
    Enumeration e = echo_.allUnits().elements();
    units_ = new ECHOunit[echo_.allUnits().size()];
    for(int i=0; e.hasMoreElements(); ++i) {
      ECHOunit u = (ECHOunit)e.nextElement();
      units_[i] = u;
    } // while
  }

  //
  // PRE: echo_ has been initialized
  //
  private void getLinks()
  {
    //System.out.println(echo_.allLinks());
    Enumeration e = echo_.allLinks().elements();
    links_ = new Link[echo_.allLinks().size()];
    for(int i=0; e.hasMoreElements(); ++i) {
      Link l = (Link)e.nextElement();
      links_[i] = l;
    } // for
  }

  //
  // PRE: getUnits() and getLinks() have been called
  // POST: the ith location of unitLinks_ contains am integer pair
  //       holding the location of the corresponding ECHOunits in the
  //       units_ array
  //
  private void getUnitLinks()
  {
    unitLinks_ = new IntPair[links_.length];
    for(int i=0; i<links_.length; ++i) {
      int loc1 = Array.member(links_[i].endA(),units_);
      int loc2 = Array.member(links_[i].endB(),units_);
      Assert.isTrue(loc1 != -1);
      Assert.isTrue(loc2 != -1);
      unitLinks_[i] = new IntPair(loc1,loc2);
    } // for
  }

  public float minScore()
  {
    return minScore_;
  }

  public void solve()
  {
    minScore_ = Float.MAX_VALUE;
    boolean[] bits = new boolean[units_.length];
    boolean[] bestPart = new boolean[units_.length];
    
    for(int trie=0; trie<maxTries_; ++trie) {
      randBits(bits);
      doTrie(bits,bestPart);
    } // for
    //  System.out.println("score="+newScore);
    tally(bits);
    summarizeRun();
    //System.out.println("----------------------------");
  } 

  //
  // PRE: bits and bestPart are non-null and the same length
  // POST: returns in bestPart the array that was gotten from the array bits
  //       by flipping its bits until no improvement can be made by bit 
  //       flipping; minScore_ is the score of the partition represented by
  //       bestPart
  //
  private void doTrie(boolean[] bits, boolean[] bestPart)
  {
    float newScore = minScore_;  
    float lastScore = -1;
    for(int flip=0; (lastScore != newScore); ++flip) {
	lastScore = newScore;
	newScore = flipBestBit(bits);
	if (newScore < minScore_) { 
	  minScore_ = newScore;
	  for(int j=0; j<bits.length; ++j)
	    bestPart[j] = bits[j];
	} // if
      } // for
  }

  //
  //  slow: can be sped up by incrementally re-calulating the score
  //        on each iteration instead of just calling score every time
  //  
  float flipBestBit(boolean[] bits)
  {
    Assert.notNull(bits);
    int bestBit = -1;
    float lowScore = score(bits);
    for(int i=0; i<bits.length; ++i) {
      bits[i] = !bits[i];
      float newScore = score(bits);
      if (newScore < lowScore) {
	lowScore = newScore;
	bestBit = i;
      } // if
      bits[i] = !bits[i];
    } // for

    if (bestBit > -1)
      bits[bestBit] = !bits[bestBit];
    return lowScore;
   }

  void randBits(boolean[] bits)
  {
    for(int i=0; i<bits.length; ++i)
      if (rand_.nextInt() % 2 == 0)
	bits[i] = false;
      else
	bits[i] = true;
  }

  //
  // PRE: partition is a non-null array of booleans, where the ith value
  //      corresponds to the partition the ith value of units_ is in
  // POST: returns the sum of the absolute value of the weights on conflicted
  //       edges for the given partition
  //
  float score(boolean[] partition)
  {
    Assert.notNull(partition);
    float result = 0;
    for(int i=0; i<unitLinks_.length; ++i) {
      IntPair ip = unitLinks_[i];
      result += score(ip,partition);
    } // for
    return result;
  }

  //
  // PRE: non-null parameters
  // POST: if ip denotes a link in conflict, then the weight of the edge
  //       is returned
  //
  float score(IntPair ip, boolean[] partition)
  {
    Assert.notNull(ip);
    ECHOunit a = units_[ip.first()];
    ECHOunit b = units_[ip.second()];
    Link dummy = new Link(a,b,Float.MAX_VALUE);
    Link actual = links_[Array.member(dummy,links_)];
    float w = actual.weight();
    if (inConflict(ip,partition,w)) {
      //System.out.println("conflicted: "+actual);
      return Math.abs(w);
    } else 
      return 0;
  }

  //
  // PRE: non-null parameters
  // POST: returns true iff the nodes denoted by ip are in the same 
  //       partition and there edge-weight is >=0, or the nodes are 
  //       in different partitions and the edge-weight is < 0
  //
  boolean inConflict(IntPair ip, boolean[] partition, float w)
  {
    int first = ip.first();
    int second = ip.second();

    boolean result = 
      (w > 0 && (partition[first] == partition[second])) ||
	(w < 0 && (partition[first] != partition[second]));
    //System.out.println("ip="+ip+", w="+w+", partition["+first+"]="+
	//	       partition[first]+", partition["+second+"]="+
		//       partition[second]+", result="+result);
    return !result; 
  }

  //
  // POST: every unit with positive activation is put into accepted_,
  //       and the rest in rejected_
  //
  private void tally(boolean[] partition)
  {
    accepted_.removeAllElements();
    rejected_.removeAllElements();
    for(int i=0; i<partition.length; ++i) {
      if (partition[i])
	accepted_.addElement(units_[i]);
      else
	rejected_.addElement(units_[i]);
    } // for
    if (rejected_.contains(echo_.getSpecialUnit())) {
      Object temp = rejected_;
      rejected_ = accepted_;
      accepted_ = (Vector)temp;
    } // if
  }


  public void summarizeRun()
  {
    //System.out.println("Accepted units: "+summary(accepted_));
    //System.out.println("Rejected units: "+summary(rejected_));
    //System.out.println("overall min score = "+minScore());
  }

} // class GreedySolver
