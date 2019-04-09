/* 
$Log: List.java,v $
Revision 1.10  1997/05/25 19:51:07  tjdonald
- added javadoc documentation

Revision 1.9  1997/05/25 02:29:46  tjdonald
- put into predicate package

Revision 1.8  1997/05/19 17:28:08  tjdonald
- added code to allow strings to be properly parsed as List elements

Revision 1.7  1997/05/16 17:40:53  tjdonald
- made methods explicitly public

Revision 1.6  1997/05/14 13:33:46  tjdonald
- made ":" a word character

Revision 1.5  1997/05/13 16:58:42  tjdonald
- added slightly more descriptive error messages

Revision 1.4  1997/05/07 22:24:12  tjdonald
- added delimeter characters here, and made them static

Revision 1.3  1997/05/07 20:48:53  tjdonald
- able to parse Lists with one kind of bracket

Revision 1.2  1997/05/07 01:56:07  tjdonald
- basic top-level reading of lists working
 
*/


package predicate;

import java.io.*;
import java.util.Vector;
import java.util.Enumeration;
import debug.Assert;

/**
  <p>
  A LISP-like list structure that is able to read itself in from strings
  and streams.
  </p>
  <p>
  Many LISP-like functions could be added to this, e.g. first, rest, cons, 
  append, flatten, etc.
  </p>

*/

public class List {

  private Vector list_ = new Vector();  // list_ is a Vector of list_
  
  //
  // characters for displaying/parsing predicates
  // 
  private static char leftDelim_ = '(';
  private static char rightDelim_ = ')';
  private static char sep_ = ',';

  private static char innerLeftDelim_ = '{';
  private static char innerRightDelim_ = '}';

  List() {}

/**
  @param s a list in a string format; s is parsed according to its list
           structure
  @exception IOException if s cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public List(String s) throws IOException
  {
    this(new StringBufferInputStream(s));
    //StreamTokenizer st = makeTokenizer(new StringBufferInputStream(s));
    //list_ = readList(st,leftDelim_,sep_,rightDelim_);
  }

/**

  @param is an InputStream containing the data to be parsed
  @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public List(InputStream is) throws IOException
  {
    StreamTokenizer st = makeTokenizer(is);
    st.nextToken();
    //System.out.println("List called on "+st);
    st.pushBack();
    list_ = readList(st,leftDelim_,sep_,rightDelim_);
  }

/**

  @param st a StreamTokenizer containing the data to be parsed
  @exception IOException if st cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public List(StreamTokenizer st) throws IOException
  {
    list_ = readList(st);
  }
  
/**
  
   PRE: in is a Stream holding a list beginning with a left char; assumes
        " is the quote character (the default for a StreamTokenizer)
   POST: returns a new Vector containing the items of the list 
  
   @param in the input stream to parse
   @param left the left delimeter character
   @param sep the seperator character
   @param right the right delimeter character
   @return a Vector representation of the items in in: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public static Vector readList(StreamTokenizer in, char left, char sep, 
				char right) throws IOException
  {
    if (in.nextToken() != left)
      throw new IOException("List: '"+left+"' expected, got "+in+" instead");

    //
    // read in the arguments
    //
    Vector result = new Vector();
    in.nextToken();

    while (in.ttype != right) {
      //
      // INVARIANT: next token is a word or a left character or \" delimited
      //            string
      //
      
      if (in.ttype == left) {
	in.pushBack();
	List subList = new List(in);
	result.addElement(subList);
      } else if (in.ttype == StreamTokenizer.TT_NUMBER) {
	throw new IOException("List: word expected, got number "+in
			      +" instead");
      } else if (in.ttype == StreamTokenizer.TT_WORD) {
	result.addElement(in.sval);   // add the argument
      } else if (in.ttype == '\"') {
	result.addElement("\""+in.sval+"\"");
      } else
	throw new IOException("List: word expected, got "+in+" instead");

      int nt = in.nextToken();
      if (nt == sep) {    // skip separators
	in.nextToken();
	
	if (in.ttype == right)
	  throw new IOException("List: unexpected '"+right+"'");

      } else if (nt != right) 
	throw new IOException("List: '"+sep+"' or '"+right+
			      "' expected, got "+in+" instead");
      
    } // while
    
    return result;
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Default delimeters are used.
  
   @param list the input String to parse
   @return a Vector representation of the items in list: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public final Vector readList(String list) throws IOException
  {
    return readList(list,leftDelim_,sep_,rightDelim_,innerLeftDelim_,
		    innerRightDelim_);
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Supplied delimeters are used.
  
   @param list the input String to parse
   @param left the left delimeter character
   @param sep the seperator character
   @param right the right delimeter character
   @return a Vector representation of the items in list: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public static Vector readList(String list, char left, char sep, char right)
    throws IOException
  {
    return readList(list,left,sep,right,left,right);
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Supplied delimeters are used.
  
   @param list the input String to parse
   @param left the left delimeter character
   @param sep the seperator character
   @param right the right delimiter character
   @param innerLeft inner left-delimiter (not used)
   @param innerRight inner right delimiter (not used)
   @return a Vector representation of the items in list: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public static Vector readList(String list, char left, char sep, char right,
				char innerLeft, char innerRight)
    throws IOException
  {
    StringBufferInputStream sbis = 
	new StringBufferInputStream(list);

    return readList(sbis,left,sep,right,innerLeft,innerRight);
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Supplied delimeters are used.
  
   @param is the InputStream to read
   @return a Vector representation of the items in is: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public final Vector readList(InputStream is) throws IOException
  {
    return readList(is,leftDelim_,sep_,rightDelim_,innerLeftDelim_,
		    innerRightDelim_);
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Supplied delimeters are used.
  
   @param is the InputStream to be parsed
   @param left the left delimeter character
   @param sep the seperator character
   @param right the right delimiter character
   @param innerLeft inner left-delimiter (not used)
   @param innerRight inner right delimiter (not used)
   @return a Vector representation of the items in is: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public static Vector readList(InputStream is, char left, char sep, 
				char right, char innerLeft, char innerRight)
    throws IOException
  {
    StreamTokenizer st = makeTokenizer(is,left,sep,right,innerLeft,innerRight);
    
    return readList(st,left,sep,right);
  }

/**
  
  Parses a list stored as a String, and returns the result as a Vector.
  Supplied delimeters are used.
  
   @param in the StreamTokenizer to parse
   @return a Vector representation of the items in in: sublists are stored
         as lists, while strings delimited by " are stored as Strings 
         delimited by ", and everything else is a String
   @exception IOException if is cannot be parsed, an exception
             is thrown indicating the particular problem
*/
  public Vector readList(StreamTokenizer in) throws IOException
  {
    return readList(in,leftDelim_,sep_,rightDelim_);
  }

/**

  @param is the InputStream to be parsed
  @param left the left delimeter character
  @param sep the seperator character
  @param right the right delimiter character
  @param innerLeft inner left-delimiter (not used)
  @param innerRight inner right delimiter (not used)
  @return creates a StreamTokenizer that uses all StreamTokenizer's default
       word characters plus the characters <code>'0' ... '9'</code> and
       <code>':'</code>; all the input delimiters are treated as ordinary
       characters, and slashStar and slashSlash comments are both set to 
       <code>true</code>
*/
  public static StreamTokenizer makeTokenizer(InputStream is, char left, 
					      char sep, char right,
					      char innerLeft, 
					      char innerRight)
  {
    StreamTokenizer st = new StreamTokenizer(is);
    
    st.ordinaryChars('0','9'); // necessary to avoid 0 .. 9 being parsed
                               // as numbers
    st.ordinaryChar(':');

    st.wordChars('0','9');     // digits are legal word characters
    st.wordChars(':',':');        

    st.ordinaryChar(left);
    st.ordinaryChar(right);
    st.ordinaryChar(sep);
    st.ordinaryChar(innerLeft);
    st.ordinaryChar(innerRight);

    st.slashStarComments(true);     // allow both styles of Java comments
    st.slashSlashComments(true);
    
    return st;
  }

/**

  @param is the InputStream to be parsed
  @return creates a StreamTokenizer that uses all StreamTokenizer's default
       word characters plus the characters <code>'0' ... '9'</code> and
       <code>':'</code>; all the default delimiters are treated as ordinary
       characters, and slashStar and slashSlash comments are both set to 
       <code>true</code>
*/
  public static StreamTokenizer makeTokenizer(InputStream is)
  {
    return makeTokenizer(is,leftDelim_,sep_,rightDelim_,innerLeftDelim_,
			 innerRightDelim_);
  }

/**

  @param is the String to be parsed
  @return creates a StreamTokenizer that uses all StreamTokenizer's default
       word characters plus the characters <code>'0' ... '9'</code> and
       <code>':'</code>; all the default delimiters are treated as ordinary
       characters, and slashStar and slashSlash comments are both set to 
       <code>true</code>
*/
  public static StreamTokenizer makeTokenizer(String s)
  {
    return makeTokenizer(new StringBufferInputStream(s));
  }

/**

  @return a String showing the List in the standard list such that it can be
      parsed as a List using the default delimiters
*/
  public String toString()
  {
    Enumeration e = list_.elements();
    if (!e.hasMoreElements())
      return String.valueOf(leftDelim_)+rightDelim_;
    else {
      Object ne = e.nextElement();
      String result = String.valueOf(leftDelim_);

      if (ne instanceof List)
	result += ne.toString();
      else
	result += ne.toString();

      while (e.hasMoreElements()) {
	ne = e.nextElement();
	if (ne instanceof List) {
	  result += sep_+ne.toString();
	} else {
	  result += sep_+ne.toString();
	} // if
      } // while

      return result+rightDelim_;
    } // if
  }

/**
  @return returns a Vector holding the references to each of the top-level
    items in this List
*/
  public Vector topLevelElements()
  {
    return list_;
  }

/**
  Prints the <code>toString</code> method of this object to 
  <code>System.out</code>.
*/
  public void selfTest()
  {
    System.out.println(toString());
  }

/**
  @return the left delimiter character
*/
  public final char leftDelim()
  {
    return leftDelim_;
  }
  
/**
  @return the right delimiter character
*/
  public final char rightDelim()
  {
    return rightDelim_;
  }

/**
  @return the seperator character
*/
  public final char seperator()
  {
    return sep_;
  }

/**
  <p>
  Sets the left and right delimiters to be <code>left</code> and 
  <code>right</code> respectively.
  </p>
  <p>
  <b>POST:</b> <code>selfConsistent()</code> is <code>true</code>
  </p>
*/
  public final void setDelim(char left, char right)
  {
    //Assert.isTrue(!left.equals(right));
    leftDelim_ = left;
    rightDelim_ = right;
    Assert.isTrue(selfConsistent());
  }

/**
  @return <code>true</code> if the internal state of this object is okay,
          <code>false</code> otherwise
*/
  public boolean selfConsistent()
  {
    return (leftDelim_ != rightDelim_) && (leftDelim_ != sep_) &&
      (rightDelim_ != sep_) && (innerRightDelim_ != innerLeftDelim_) &&
	(innerRightDelim_ != sep_) && (innerLeftDelim_ != sep_);
	  
  }

/**
  <p>
  Sets the left and right inner delimiters to be <code>left</code> and 
  <code>right</code> respectively.
  </p>
  <p>
  <b>POST:</b> <code>selfConsistent()</code> is <code>true</code>
  </p>
*/
  public final void setInnerDelim(char left, char right)
  {
    innerLeftDelim_ = left;
    innerRightDelim_ = right;
    Assert.isTrue(selfConsistent());
  }

/**
  <p>
  Sets the seperator delimiter to be <code>sep</code>.
  </p>
  <p>
  <b>POST:</b> <code>selfConsistent()</code> is <code>true</code>
  </p>
*/
  public final void setSeperator(char sep)
  {
    sep_ = sep;
    Assert.isTrue(selfConsistent());
  }


  //
  // list operations
  //
/**
  @return the number of elements in the top-level of this List
*/
  public int size()
  {
    return list_.size();
  }

/**
  @return the number of elements in the top-level of this List
*/
  public int length()
  {
    return list_.size();
  }


/**
  @param i in <code>[0,list_.length]</code>
  @return the ith element of this List
*/
  public Object elementAt(int i)
  {
    return list_.elementAt(i);
  }

/**
  @return <code>true</code> if this list has no elements in it, 
  <code>false</code> otherwise
*/
  public boolean empty()
  {
    return list_.size() == 0;
  }

  //
  // PRE: list_ is non-empty
  // POST: returns a reference to the first item in list_
  //
/**
  Assumes this List is non-empty.
  @return the first element on this List
*/
  public Object first()
  {
    return list_.elementAt(0);
  }

  //
  // to implement rest() efficiently, the storage method for Lists should
  // be changed to a standard cons-cell format
  //
  //
  // NOTE: List should be implemented so cons adds onto the end of list_ 
  //       instead of the front
  //
/**
  If this object is non-null, x is added as the first element of the list.
  @param x is non-null
*/
  public void cons(Object x)
  {
    Assert.notNull(list_);
    Assert.notNull(x);
    list_.insertElementAt(x,0);
  }

  //
  // PRE: a and list_ are not null
  // POST: list_ has all the elements of a added onto the end of it
  //
/**
  This list and a are appended such that all the elements of a are put onto
  the end of this list.
  @param a is non-null
*/
  public void append(List a)
  {
    int aSize = a.size();
    for(int i=0; i<aSize; ++i) {
      list_.addElement(a.elementAt(i));
    } // for
  }

  

} // class List
