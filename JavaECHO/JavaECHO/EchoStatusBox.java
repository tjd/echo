// $Id: EchoStatusBox.java,v 1.2 1997/07/01 01:23:11 tjdonald Exp $

package JavaECHO;

import java.awt.*;
import debug.*;

class EchoStatusBox extends Dialog {

  private Button goAway_ = new Button("close");

  EchoStatusBox()
  {
    super(new Frame(),false);  // non-modal dialog
    setResizable(false);
    setTitle("JavaECHO Message");
    setLayout(new BorderLayout());
  }

  EchoStatusBox(String msg, boolean goAwayButton)
  {
    this();
    add("Center",new Label(msg));
    if (goAwayButton) {
      Panel p = new Panel();
      p.add(goAway_);
      add("South",p);
    } // if
    pack();
  }

  EchoStatusBox(String[] msg, boolean goAwayButton)
  {
    this();
    Panel p = new Panel();
    p.setLayout(new GridLayout(msg.length,1));
    for(int i=0; i<msg.length; ++i) {
      p.add(new Label(msg[i]));
    } // for
    add("Center",p);
    if (goAwayButton) {
      Panel p2 = new Panel();
      p.add(goAway_);
      add("South",p2);
    } // if
    pack();
  }

  synchronized public boolean action(Event evt, Object arg)
  {
    if (evt.target.equals(goAway_)) {
      hide();
      return true;
    } // if
    return super.handleEvent(evt);
  }

} // class EchoStatusBox
