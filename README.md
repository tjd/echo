# echo
Code for Thagard's ECHO model of coherence.

This first commit is of the source code from http://cogsci.uwaterloo.ca/JavaECHO/jecho.html. This is an old-fashioned Java Applet, so it may be hard to get working!

There are other projects that implement version of ECHO, such as:

- A nice graphical implementation: https://hamschank.com/convinceme/

- Using ECHO (in Python) to solve Sudoku: https://github.com/MaxRae/ConnectionistSudoku/blob/master/ECHOSudoku.ipynb

- A command-line version of ECHO that uses more modern Java:  https://github.com/russellcameronthomas/JavaECHO_command_line

As described in [Coherence: The Price is Right](https://pdfs.semanticscholar.org/b801/92a17a80bd6bd3bc7684f4644b87f1dc8aec.pdf), the basic coherence problem is this:

COHERENCE: Let E be a finite set of elements {e_i} and let C be a set of
constraints on E understood as a set {(e_i, e_j )} of pairs of elements of E. C
divides into C+, the positive constraints on E, and C-, the negative constraints on E. Each constraint is associated with a number w, which is the
weight (strength) of the constraint. The problem is to partition E into two
sets, A (accepted) and R (rejected), in a way that maximizes compliance
with the following two coherence conditions:
(1) If (e_i, e_j ) is in C+, then ei is in A if and only if e_j is in A.
(2) If (e_i, e_j ) is in C-, then ei is in A if and only if e_j is in R.
Let W be the weight of the partition, that is, the sum of the weights of the
satisfied constraints. The coherence problem is then to partition E into A and
R in a way that maximizes W.
