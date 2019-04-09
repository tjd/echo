
/*
$Log: Array.java,v $
Revision 1.7  1997/06/06 15:28:01  tjdonald
*** empty log message ***

Revision 1.6  1997/05/25 02:32:39  tjdonald
- put into utils package

Revision 1.5  1997/05/02 01:14:16  tjdonald
*** empty log message ***

Revision 1.4  1997/05/01 03:20:46  tjdonald
added pre-condition to member that all values of arr must be non-null

Revision 1.3  1997/04/29 21:12:04  tjdonald
added checking for length 0 arrays to subset

Revision 1.2  1997/04/25 21:56:45  tjdonald
added log

*/

/*

   Basic array utilities.

*/

//
// CLIFFHANGER: make sam a package, so also make Assert and Array and others
//              into their own packages
// 

package utils;

//import Assert;

public final class Array {

  //
  // PRE: a and b are arrays (null treated like empty set) with no null 
  //      elements
  // POST: returns true if there is no member of a that fails to appear in
  //       b
  //
  public static boolean subset(Object[] a, Object[] b)
  {
    if (a == null || a.length == 0) 
      return true;
    else if (b == null || b.length == 0)
      return false;
    else {
      for(int i=0; i<a.length; ++i) {
	if (member(a[i],b) == -1)
	  return false;
      } // for
      return true;
    } // if
  }

  //
  // PRE: arr is an array with no null elements
  // POST: returns the index of the first member of arr that is equal
  //       to x; -1 if x is not in arr
  //
  public static int member(Object x, Object[] arr)
  {
    if (arr == null || arr.length == 0)
      return -1;
    else {
      for(int i=0; i<arr.length; ++i) {
	if (arr[i].equals(x))
	  return i;
      } // for
      return -1;
    } // if
  }


  public static String toString(Object[] arr)
  {
    if (arr != null) {
      String result = "{";
      
      for(int i=0; i<arr.length; ++i) {
	result += arr[i]+",";
      } // for

      return result+"}";
    } // if
    return null;
  }

} // class Array
