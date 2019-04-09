package JavaECHO;

/*

  A general ECHO applet gui.

*/

import java.awt.*;
import java.util.*;
import java.applet.*;
import java.io.*;
import debug.*;

public class ECHOgui extends Applet {
  
  final Label title_ = new Label("JavaECHO",Label.CENTER);
  
  final Label accLabel_ = new Label("accepted",Label.CENTER); 
  final Label rejLabel_ = new Label("rejected",Label.CENTER);
  TextArea accepted_ = new TextArea("",30,20);
  TextArea rejected_ = new TextArea("",30,20);

  final Button solve_ = new Button("run ECHO");
  Choice algs_ = new Choice();
  final Label algLabel_ = new Label("using",Label.CENTER);

//  final String echoInput_ = echoStrings.lavoisier;
 
  TextArea echoData_ = new TextArea("empty",32,40);
//  private boolean dataEditable_ = false;
  private Checkbox dataCheckbox_ = new Checkbox("editable");
  final Label dataLabel_ = new Label("ECHO input",Label.CENTER);
  Choice dataSelect_ = new Choice();

  final EchoStatusBox esbRunning_ = 
    new EchoStatusBox("Please wait, setting up and "+
		      "solving ECHO problem...",false);

  final Button options_ = new Button("options");

  echoOptionsBox eob_ = new echoOptionsBox();

  final Button graphButton_ = new Button("graph");

  ECHOgraph graph_;

  final Button saveButton_ = new Button("save");
  final Button loadButton_ = new Button("load");

  ECHO echo_;
 
  public void init()
  {
    Font titleFont = new Font("Helvetica",Font.BOLD,16);
    Font bigTitleFont = new Font("Helvetica",Font.BOLD,24);

    Panel acc = new Panel();
    acc.setLayout(new BorderLayout());
    accLabel_.setFont(titleFont);
    acc.add("North",accLabel_);
    accepted_.setEditable(false);
    acc.add("Center",accepted_);

    Panel rej = new Panel();
    rej.setLayout(new BorderLayout());
    rejLabel_.setFont(titleFont);
    rej.add("North",rejLabel_);
    rejected_.setEditable(false);
    rej.add("Center",rejected_);
    
    Panel out = new Panel();
    out.setLayout(new GridLayout(1,2));
    out.add(acc);
    out.add(rej);

    Panel control = new Panel();
    control.add(solve_);
    control.add(algLabel_);
    algs_.addItem("connectionist");

	// David Croft: these two are currently disabled.
	// The greedy algorithm seems to have a few problems 
	// and the brute force hangs with any reasonably sized network 

    //algs_.addItem("greedy");
    //algs_.addItem("brute force");

    control.add(algs_);
    control.add(graphButton_);
    control.add(options_);

    Panel data = new Panel();
    data.setLayout(new BorderLayout());
    echoData_.setEditable(true);
    dataLabel_.setFont(titleFont);
    data.add("North",dataLabel_);
    data.add("Center",echoData_);
    {
      Panel p = new Panel();
      p.add(saveButton_);
      p.add(loadButton_);
      data.add("South",p);
    }

    Panel dataWithSelection = new Panel();
    dataWithSelection.add(data);
    addDataStrings();
    dataWithSelection.add(dataSelect_);
    setEchoData();
/*
    Panel dataWithSelection = new Panel();
    dataWithSelection.add(data);
    for(int i=0; i<echoStrings.name.length; ++i) 
      dataSelect_.addItem(echoStrings.name[i]);
    dataWithSelection.add(dataSelect_);
    setEchoData();
*/

    setLayout(new BorderLayout());
    title_.setFont(bigTitleFont);
    add("North",title_);
    add("Center",out);
    add("South",control);
    add("East",dataWithSelection);
  }


  public boolean action(Event evt, Object arg)
  {
    if (evt.target.equals(solve_)) {
      esbRunning_.show();
      if (!makeECHO())
	return super.action(evt,arg);
      ECHOsolver es;
      String alg = algs_.getSelectedItem();
      if (alg.equals("connectionist")) {
	es = new ConnectionistSolver(echo_);
      } else if (alg.equals("brute force")) {
	es = new BruteForceSolver(echo_);
      } else  
	es = new GreedySolver(echo_,eob_.getMaxFlips(),eob_.getMaxTries());
     
      runSolver(es);
      esbRunning_.hide();
    } else if (evt.target.equals(dataSelect_)) {
      setEchoData();
    } else if (evt.target.equals(options_)) {
      eob_.show();
    } else if (evt.target.equals(graphButton_)) {
      if (!makeECHO())
	return super.action(evt,arg);
      graph_ = new ECHOgraph(echo_);
    } else if (evt.target.equals(saveButton_)) {
      FileDialog fd = new FileDialog(new Frame(),"JavaECHO Save",
				     FileDialog.SAVE);
      fd.show();
      String fileName = fd.getFile();
      if (fileName != null) {
	try {
	  File outFile = new File(fileName);
	  FileOutputStream fos = new FileOutputStream(outFile);
	  PrintStream ps = new PrintStream(fos);
	  ps.print(echoData_.getText());
	} catch (IOException e) {
	  String msg = "Error: unable to save to \"" + fileName +"\"";
	  EchoStatusBox esb = new EchoStatusBox(msg,true);
	  esb.show();
	} // try
      } // if
    } else if (evt.target.equals(loadButton_)) {
      FileDialog fd = new FileDialog(new Frame(),"JavaECHO Load",
				     FileDialog.LOAD);
      fd.show();
      String fileName = fd.getFile();
      if (fileName != null) {
	try {
	  File inFile = new File(fileName);
	  FileInputStream fis = new FileInputStream(inFile);
	  DataInputStream dis = new DataInputStream(fis);
	  String line;
	  echoData_.setText("");
	  while ((line = dis.readLine()) != null) {
	    echoData_.appendText(line+"\n");
	  } // while
	} catch (IOException e) {
	  String msg = "Error: unable to open \"" + fileName +"\"";
	  EchoStatusBox esb = new EchoStatusBox(msg,true);
	  esb.show();
	} // try
      } // if
    } else
      return super.action(evt,arg);
    return true;
  }

  //
  // PRE: es is an instantiated solver, and echo_ is instantiated
  // POST: calls the solve method of the given solver and collects the
  //       accepted and rejected units
  //
  private void runSolver(ECHOsolver es)
  {
    Assert.notNull(es);
    Assert.notNull(echo_);
    es.solve();
    //es.summarizeRun();
    addVector(es.accepted(),accepted_);
    addVector(es.rejected(),rejected_);
  }

  private boolean makeECHO()
  {
    try {
      String name = dataSelect_.getSelectedItem();
      StringBufferInputStream sbis = 
	new StringBufferInputStream(echoData_.getText());
      echo_ = new ECHO(name,"GUI test",sbis,eob_.getSimplicityImpact(),
		       eob_.getDefaultWeight(), eob_.getExcitationWeight(),
		       eob_.getDataExcitation(), 
		       eob_.getInhibitionWeight());
    } catch (IOException e) {
      esbRunning_.hide();
      String msg2 = "    ECHO error message: "+e.getMessage();
      EchoStatusBox esb = new EchoStatusBox(msg2,true);
      esb.show();
      return false;
    } // try
    return true;
  }

  private void setEchoData()
  {
    String s = echoStrings.get(dataSelect_.getSelectedItem());
    if (s != null) 
      echoData_.setText(s);
  }

  private void addVector(Vector v, TextArea ta)
  {
    ta.setText("");
    Enumeration e = v.elements();
    while (e.hasMoreElements()) {
      ECHOunit u = (ECHOunit)e.nextElement();
      ta.appendText(u.name()+", "+u.activation()+"\n");
    } // while
  }


  //
  // based on echoStrings v1.4
  //
  private void addDataStrings()
  {
/*
    for(int i=0; i<echoStrings.name.length; ++i) 
      dataSelect_.addItem(echoStrings.name[i]);
*/
	dataSelect_.addItem("ulcers 1983");
	dataSelect_.addItem("ulcers 1994");
    dataSelect_.addItem("breadth");
    dataSelect_.addItem("breadth 2");
    dataSelect_.addItem("analogy");
    dataSelect_.addItem("being explained");
    dataSelect_.addItem("simplicity");
    dataSelect_.addItem("unification");
    dataSelect_.addItem("evidence");
    dataSelect_.addItem("Lavoisier");
    dataSelect_.addItem("Darwin");
    dataSelect_.addItem("Wegener");
  }

} // class ECHOgui
