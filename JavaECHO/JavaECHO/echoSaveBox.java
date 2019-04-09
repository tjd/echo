
package JavaECHO;

import java.awt.*;
import debug.*;

final class echoSaveBox extends FileDialog {

  echoSaveBox()
  {
    super(new Frame(),"JavaECHO Save",FileDialog.SAVE);
    setResizable(false);
    pack();
  }

} // class echoSaveBox
