/*

$Log: Predicate.java,v $
Revision 1.13  1997/05/25 20:27:16  tjdonald
- added javadoc documentation

Revision 1.12  1997/05/25 02:30:10  tjdonald
- put into predicate package

Revision 1.11  1997/05/16 17:39:04  tjdonald
- made methods explicitly public

Revision 1.10  1997/05/14 13:43:45  tjdonald
*** empty log message ***

Revision 1.9  1997/05/07 22:24:57  tjdonald
- removed delimeter character stuff and moved it to List

Revision 1.8  1997/05/07 21:42:58  tjdonald
major overhaul, argList_ is now a List_
- all list stuff now handled by List
- still some flakiness wrt defining delimeters

Revision 1.7  1997/05/06 23:53:36  tjdonald
- added inner delimeters as private data, but not yet recognized by parser

Revision 1.6  1997/05/06 22:49:48  tjdonald
- added more readList methods
- made some static methods for more conveniently readong lists

Revision 1.5  1997/05/06 21:34:12  tjdonald
- added new constructor
- moved parsing code into readList method

Revision 1.4  1997/05/06 18:54:43  tjdonald
InputStream constructor seems to be parsing predicates correctly

Revision 1.3  1997/05/05 23:42:21  tjdonald
- added stream constructor; parses most predicates okay, but not numbers yet

Revision 1.2  1997/05/05 20:55:41  tjdonald
- added selfTest(), and renamed arg_ to argList_

Revision 1.1  1997/05/05 20:45:55  tjdonald
Initial revision

*/



package predicate;

import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import debug.Assert;
//import List;

/**
  <p>
  A Predicate is a list of objects with a name. They correspond to 
  predicates in logic, e.g.

  <pre>
  F(x,4), cat(Marge), abc123(2,3,4,5,bob,l), ...
  </pre>
  </p>

  <p>
  A predicate name, and predicate arguments, consists of
  alpha-numeric characters. Numbers are allowed anywhere. The default
  delimeters are "(" and ")", and the default separotr is ",". Here are
  some examples of legal Predicates:
  <pre>
      "test(a ,b,c, d)",
      " test (a ,b,c, d)",
      "test ( a      ,   b, c       ,d     )   ",
      "test()",
      "test(a)",
      "test(big,bird , kissed, my, nose)",
      "TEST(1,2,3,4,5,6,7,8,9,1,2,3,4)",
      "pred ( a, 1, 2,c,de)",
      "pred(1)",
      "pred(b1,a2)",
      "pred(1b,a2)",
      "pred-hyph(1,2,a)",
      "pred-hyph(ani-hyp,12b,no-hyph-num-6)",
      "1up(3,4)",
      "56(2aaa,   b , v ,sdf)"
   </pre>
   Note that an empty argument list is a legal predicate, e.g. f(). Also,
   a predicate name (or argument) can be a sequence of digits, 
   e.g. 3(4,53,a). 
   </p>
*/

public class Predicate {

  private String name_;
  private List argList_;

/**
  Create an empty predicate.
  @param name the name of the predicate
*/
  public Predicate(String name)
  {
    name_ = name;
    argList_ = new List();
  }

/**
  Create a predicate with a list of arguments.
  @param name the name of the predicate
  @param the non-null list of arguments for this predicate
*/
  public Predicate(String name, List args)
  {
    Assert.notNull(args);
    name_ = name;
    argList_ = args;
  }

  //
  // initialize Predicate by reading in the entire predicate from the
  // given input stream
  //
/**
  Create a predicate by parsing the given stream.
  @param data the InputStream containing the predicate data to parse
  @exception IOException thrown if there is a parsing problem
*/
  public Predicate(InputStream data) throws IOException
  {
    this(List.makeTokenizer(data));
  }

/**
  Create a predicate by parsing the given stream.
  @param data the StreamTokenizer containing the predicate data to parse
  @exception IOException thrown if there is a parsing problem
*/
  public Predicate(StreamTokenizer st) throws IOException
  {
    int nt = st.nextToken();
    if (nt != StreamTokenizer.TT_WORD)
      throw new IOException("Predicate: predicate name expected, st = "+st);

    name_ = st.sval;
   
    argList_ = new List(st);
  }


/**
  Create a predicate with the given name, and a stream consisting of a 
  List of the predicate's parameters
  @param name the predicate's name
  @param data the stream contain the List of arguments to be parsed
  @exception IOException thrown if there is a parsing problem with the
           argument stream
*/
  Predicate(String name, InputStream data) throws IOException
  {
    //StreamTokenizer in = makeTokenizer(data);
    name_ = name;
    argList_ = new List(data);
  }

/**
  @return a String showing the predicate in a form that can also be parsed
     by a Predicate object
*/
  public String toString()
  {
    return name_ + argList_.toString();
  }


/**
  @return returns <code>true</code> if the internal state of the object 
    is okay, <code>false</code> otherwise
*/
  public boolean selfConsistent()
  {
    return (name_ != null) && (name_ != "");	  
  }


/**
  Print the object to stdout, and print result of 
  <code>selfConsistent()</code>.
*/
  public void selfTest()
  {
    System.out.println(toString());
    System.out.println("selfConsistent() = "+selfConsistent());
  }

/**
  Return the predicate's name.
  @return the predicate's name
*/
  public final String name()
  {
    return name_;
  }

/**
  Return the argument list.
  @return the predicate's List of arguments
*/
  public final List args()
  {
    return argList_;
  }

/**
  Return the length of the argument list.
  @return the arity of the predicate, i.e. number of arguments in its 
     argument list
*/
  public final int arity()
  {
    return argList_.length();
  }

/**
  Get the ith argument in the argument list.
  @param i in <code>[0,arity())</code>
  @return the ith argument in this predicate's argument list
*/
  public final Object arg(int i)
  {
    return argList_.elementAt(i);
  }


} // class Predicate
