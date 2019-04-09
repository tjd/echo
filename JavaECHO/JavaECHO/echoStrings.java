// $Id: echoStrings.java,v 1.4 1997/07/20 20:10:13 tjdonald Exp $

package JavaECHO;

/*

  ECHO data is initially stored in a file, but since Netscape applets
  can't read/write files yet, the f2s utility can be used to convert
  files into Strings readable by applets.

*/

import java.util.*;

final class echoStrings {
  
  private static Hashtable ht_ = new Hashtable();

  public final static String ulcers_1983 = "\n//\n// 1983 view that most peptic ulcers caused by excess acidity\n\n// Evidence\n\n// E1 - Association between bacteria and ulcers\n// E2 - Warren observed stomach bacteria\n// E3 - Some people have stomach ulcers\n// E4 - Antiacids heal ulcers\n// E5 - Previous researchers found no bacteria\n\n// Bacteria Hypotheses\n\n// BH1 - Bacteria cause ulcers\n// BH2 - Stomach contains bacteria\n\n// Acid Hypotheses\n\n// AH1 - Excess acidity causes ulcers\n// AH2 - Stomach is sterile\n// AH3 - Bacterial samples are contaminated\n\n// Bacteria explanations\n\nexplain((BH1,BH2),E1)\nexplain((BH2),E2)\nexplain((BH1,BH2),E3)\n\n// Acid Explanations\n\nexplain((AH1,AH2,AH3),E1)\nexplain((AH1,AH2,AH3),E2)\nexplain((AH1),E3)\nexplain((AH1),E4)\nexplain((AH2),E5)\n\n// Data\n\ndata(E1,E2,E3,E4,E5)";


  public final static String ulcers_1994 = "\n//\n// Dominant peptic ulcer view in 1994 for bacterial theory\n//\n\n// Evidence\n\n// E1 - Association between bacteria and ulcers\n// E2 - Many have observed stomach bacteria\n// E3 - Some people have stomach ulcers\n// E4 - Antacids heal ulcers\n// E6 - Marshall's 1988 study that antibiotics cure ulcers\n// E7 - Graham's 1992 study that antibiotics cure ulcers\n// E8 - Several other cure studies\n// E9 - Bacteria/acid study\n\n// Bacteria Hypotheses\n\n// BH1 - Bacteria causes ulcers\n// BH2 - Stomach contains bacteria\n// BH3 - Bacteria produces acid\n// BH4 - Eradicating bacteria cures ulcers\n\n// Acid Hypotheses\n\n// AH1 - Excess acidity causes ulcers\n\n// Bacteria explanations\n\nexplain((BH1,BH2),E1)\nexplain((BH2),E2)\nexplain((BH1,BH2),E3)\nexplain((BH1,BH2),BH4)\nexplain((BH1,BH2),E4)\nexplain((BH3),E9)\nexplain((BH4),E6)\nexplain((BH4),E7)\nexplain((BH4),E8)\n\n// Acid Explanations\n\nexplain((AH1),E3)\nexplain((AH1),E4)\n\n// Data\n\ndata(E1,E2,E3,E4,E6,E7,E8,E9)";
	
  public final static String breadth = "\n//\n// explanatory breadth example,from p.74- 75 of Conceptual Revolutions\n//\n\nexplain((H1),E1)\nexplain((H1),E2)\nexplain((H2),E2)\ncontradict(H1,H2)\ndata(E1,E2)"; 

  public final static String breadth2 = "\n//\n// sames as explanatory breadth example,from p.74-75 of Conceptual \n// Revolutions, except contradict statement has been removed\n//\n\nexplain((H1),E1)\nexplain((H1),E2)\nexplain((H2),E2)\n//contradict(H1,H2)\ndata(E1,E2)";

  public final static String analogy = "\n//\n// analogy, from p. 78-79 of Conceptual Revolutions\n//\n\nexplain((H1),E1)\nexplain((H2),E1)\nexplain((H3),E3)\nanalogous((H2,H3),(E1,E3))\ncontradict(H1,H2)\ndata(E1,E3)";

  public final static String beingExplained = "\n//\n// being explained, from p.75-76 of Conceptual Revolutions\n//\n\nexplain((H1),E1)\nexplain((H1),E2)\nexplain((H2),E1)\nexplain((H2),E2)\nexplain((H3),H1)\ncontradict(H1,H2)\ndata(E1,E2)";

  public final static String simplicity = "\n//\n// simplicity, from p. 77-78 of Conceptual Revolutions\n//\n\n\n//\n// JavaECHO also finds (correctly) that H1 and H3 compete, although\n// this is not shown in CR.\n// \n \nexplain((H1),E1)\nexplain((H2,H3),E1)\ncontradict(H1,H2)\ndata(E1)";

  public final static String unification = "\n//\n// unification, from p.76-77 of Conceptual Revolutions\n//\n\n//\n// JavaECHO finds a number of competiting explanations that are not\n// mention in CR\n//\n//\n\nexplain((H1,A1),E1)\nexplain((H1,A2),E2)\nexplain((H2,A3),E1)\nexplain((H2,A3),E2)\ncontradict(H1,H2)\ndata(E1,E2)";

  public final static String lavoisier = "\n//\n//  The Lavoisier example from pages 83-84 of Conceptual Revolutions.\n//\n\n\n//\n// propositions\n//\n\n// see p.83, Table 4.1 of CR\n\n// input explanations\n\n//\n// oxygen explanations\n//\nexplain((OH1,OH2,OH3),E1)\nexplain((OH1,OH3),E3)\nexplain((OH1,OH3,OH4),E4)\nexplain((OH1,OH5),E5)\nexplain((OH1,OH4,OH5),E6)\nexplain((OH1,OH5),E7)\nexplain((OH1,OH6),E8)\n\n//\n// phlogiston explanations\n// \nexplain((PH1,PH2,PH3),E1)\nexplain((PH1,PH3,PH4),E2)\nexplain((PH5,PH6),E5)\n\n//\n// data\n//\ndata(E1,E2,E3,E4,E5,E6,E7,E8)";

  public final static String evidence = "\n//\n// evidence and acceptability, from p. 79-80 of Conceptual Revolutions\n//\n\nexplain((H1),E1)\nexplain((H1),E2)\nexplain((H1),E3)\nexplain((H1),E4)\nexplain((H2),E5)\ncontradict(H1,H2)\ncontradict(H1,E5)\n\n//\n// a data statement is not mentioned in CR, but presumably that is just an \n// omission, and so it is included here\n//\n\ndata(E1,E2,E3,E4,E5)";

  public final static String darwin = "\n//\n// The Darwin example from page 144 of Conceptual Revolutions.\n//\n\n//\n// propositions\n//\n\n// see Table 6.2, p.144\n\n//\n// Darwin's explanations\n//\n\n// natural selection and evolution\nexplain((DF5,DF6),DH1)\nexplain((DH1,DF4),DH2)\nexplain((DH2),DH3)\n\n// potential counter-evidence\nexplain((DH2,DH3,DH4),E1)\nexplain((DH2,DH3,DH5),E2)\nexplain((DH2,DH3,DH6),E3)\n\n// diverse evidence\nexplain((DH2),E5)\nexplain((DH2,DH3),E6)\nexplain((DH2,DH3),E7)\nexplain((DH2,DH3),E8)\nexplain((DH2,DH3),E9)\nexplain((DH2,DH3),E10)\nexplain((DH2,DH3),E12)\nexplain((DH2,DH3),E13)\nexplain((DH2,DH3),E14)\nexplain((DH2,DH3),E15)\n\n\n//\n// Darwin's analogies\n//\nexplain((DF2),DF3)\nexplain((DF2),DF7)\nanalogous((DF2,DH2),(DF3,DH3))\nanalogous((DF2,DH2),(DF7,E14))\n\n//\n// Creationist explanations\n//\nexplain((CH1),E1)\nexplain((CH1),E2)\nexplain((CH1),E3)\nexplain((CH1),E4)\n\n//\n// data\n//\ndata(E1,E2,E3,E4,E5,E6,E7,E8,E9,E10,E11,E12,E13,E14,E15)\ndata(DF1,DF2,DF3,DF4,DF5,DF6,DF7)\n\n//\n// contradiction\n//\ncontradict(CH1,DH3)";


  public final static String wegener = "\n//\n// Wegener example from p.184, Table 7.2\n//\n\n// explanations\nexplain((C6),NE11)\nexplain((C8),W3)\nexplain((C2),NE6)\nexplain((C2),C3)\nexplain((C3),C5)\nexplain((C3),C4)\nexplain((C8),C9)\nexplain((C8),C10)\nexplain((W8,W9),W11)\nexplain((W8),W5)\nexplain((W11,W5),E1)\nexplain((W11,W4,W8),E2)\nexplain((W11,W4,W9),E3)\nexplain((W11,W4),E4)\nexplain((W11,W5),E5)\nexplain((W1),E6)\nexplain((W3,C8),E7)\nexplain((C8,W10),E8)\nexplain((W3,C8),E9)\nexplain((C8),E10)\nexplain((W3,C8),E11)\nexplain((W3,W4),E12)\nexplain((W11,W5,W8),E13)\nexplain((W11,W5,W8),E14)\nexplain((W11,W6,W9),E15)\nexplain((W11,W5,W6,W9),E16)\nexplain((W11,W5,W8),E17)\nexplain((W3,C8,W2),E18)\nexplain((W11,W4),E19)\nexplain((W11,W5),E20)\nexplain((C3,C5,C4),E2)\nexplain((C3,C5,C4),E3)\nexplain((C6,C5),E5)\nexplain((C10,C7),E8)\nexplain((C8,C9),E9)\nexplain((C8,C9),E10)\nexplain((C10,C8),E11)\nexplain((C10),E12)\nexplain((C1,C9),E15)\nexplain((C8,C9),E18)\n\n// contradictions\n\ncontradict(E6,NE6)\ncontradict(E11,NE11)\ncontradict(C5,C8)\ncontradict(C2,W1)\n\n// data\n\ndata(E1,E2,E3,E4,E5,E6,E7,E8,E9,E10,E11,E12,E13,E14,E15,E16,E17,E18,E19,E20)";


  public static String get(String name) 
  {
    return (String)ht_.get(name);
  }

  public static String[] name;


  public static Enumeration elements()
  {
    return ht_.elements();
  }

  static {
  	ht_.put("ulcers 1983",ulcers_1983);
  	ht_.put("ulcers 1994",ulcers_1994);
    ht_.put("breadth",breadth);
    ht_.put("breadth 2",breadth2);
    ht_.put("analogy",analogy);
    ht_.put("being explained",beingExplained);
    ht_.put("simplicity",simplicity);
    ht_.put("unification",unification);
    ht_.put("evidence",evidence);
    ht_.put("Darwin",darwin);
    ht_.put("Lavoisier",lavoisier);
    ht_.put("Wegener",wegener);
    name = new String[ht_.size()];
    Enumeration e = ht_.keys();
    for(int i=0; e.hasMoreElements(); ++i) {
      name[i] = (String)e.nextElement();
    } // while
  }


} // class echoStrings
