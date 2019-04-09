/*

   A set of combinatorial iterators, mostly based on code from the
   book ``Combinatorial Algorithms'', Albert Nijenhuis and Herbert
   Wilf (Academic Press, 1975). The algorithms in this book are ideal
   for iterators, since they avoid recursion, are relatively
   efficient, and are written in a iterator-like style of giving the
   next item instead of all of the items at once.

   For convenience, these are int iterators, and work on arrays of
   ints only. It would be straightforward to use arbitrary arrays or
   Vectors of Objects instead.

*/

package iterator;

import java.util.Enumeration;

/*

Iterates through all n-bit strings using the Gray Code, so any two bit
strings differ by only one bit.

*/

public class BitIterator implements Enumeration {

  boolean[] in;
  long m;
  int size;
  int bitToFlip;
  long ncard;
  boolean first;
  boolean done;

  public BitIterator(int n)
  {
    in = new boolean[n];
    size = n;
    m = 0;
    first = true;
    done = false;
  }

  //
  // calculate the exponent of the greatest power of 2 that divides s
  //
  // PRE: s > 0
  // POST: returns the exponent of the greatest power of 2 that divides s
  //
  private final int Q(long s)
  {
    int e = 0;
    while ((s % 2) == 0) {
      s = s / 2;
      ++e;
    } // while
    return e;
  }

  public final int bitFlipped()
  {
    return bitToFlip;
  }

  public boolean hasMoreElements()
  {
    return !done;
  }

  //
  // returns the next bit string in the Gray Code as an array of booleans
  //
  public Object nextElement()
  {
    if (!done) {

      if (first) {  // first bit string is all false
	first = false;
	bitToFlip = -1;
	return in;
      } // if

      ++m;
      bitToFlip = Q(m);   

      // assert: 0 <= bitToFlip <= log2 m

      in[bitToFlip] = !in[bitToFlip];

      ncard = ncard + (in[bitToFlip] ? 1 : -1);

      if (ncard == 1 && in[size-1])
	done = true;

    } // if

    return in;
  }

} // class
