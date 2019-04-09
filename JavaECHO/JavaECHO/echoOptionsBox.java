
/*

   Let's the user select various options.

*/

package JavaECHO;

import java.awt.*;
import debug.*;
import utils.Slider;

final class echoOptionsBox extends Dialog {

  private float simplicityImpact_;
  private float defaultWeight_;
  //private float thetaDecay_;
  private float excitationWeight_;
  private float dataExcitation_;
  private float inhibitionWeight_;

  private Button done_ = new Button("Dismiss");
  
  Slider siSlide_;
  Slider dwSlide_;  
  Slider ewSlide_;
  Slider deSlide_;
  Slider iwSlide_;

  Slider flipSlide_;
  Slider trieSlide_;

  echoOptionsBox()
  {
    super(new Frame(),true);

    float simplicityImpact = ECHO.defaultSimplicityImpact;
    float defaultWeight = ECHO.defaultWeight;
    //float thetaDecay = echo.getThetaDecay();
    float excitationWeight = ECHO.defaultExcitationWeight;
    float dataExcitation = ECHO.defaultDataExcitation; 
    float inhibitionWeight = ECHO.defaultInhibitionWeight;
    
    siSlide_ = makeSlider(0.0f,1.0f,simplicityImpact);
    dwSlide_ = makeSlider(-1.0f,1.0f,defaultWeight);
    ewSlide_ = makeSlider(0.0f,1.0f,excitationWeight);
    deSlide_ = makeSlider(0.0f,1.0f,dataExcitation);
    iwSlide_ = makeSlider(-1.0f,0.0f,inhibitionWeight);

    flipSlide_ = makeSlider(1,200,100);
    trieSlide_ = makeSlider(1,50,1);

    setResizable(false);
    setTitle("JavaECHO Options");
 
    setLayout(new BorderLayout());
    {
      //Frame f = new Frame();
      Panel temp = new Panel();
      
      Checkbox cb = new Checkbox("calculate competitors");
      cb.setState(ECHO.defaultCalcCompetitors);
      
      temp.add(cb);
      add("North",temp); 
    }
    
     
    Panel sliderPanel = new Panel();
    sliderPanel.setLayout(new GridLayout(7,2));

    sliderPanel.add(new Label("Simplicity Impact (0...100)"));
    sliderPanel.add(siSlide_);

    sliderPanel.add(new Label("Default Weight (-100...100)"));
    sliderPanel.add(dwSlide_);

    //sliderPanel.add(new Label("Theta Decay"));
    //sliderPanel. add(tdSlide);
    
    sliderPanel.add(new Label("Excitation Weight (0...100)"));
    sliderPanel.add(ewSlide_);

    sliderPanel.add(new Label("Inhibition Weight (-100...0)"));
    sliderPanel.add(iwSlide_);

    sliderPanel.add(new Label("Data Excitation (0...100)"));
    sliderPanel.add(deSlide_);

    //sliderPanel.add(new Label("Max Greedy Flips (1...200"));
    //sliderPanel.add(flipSlide_);

    sliderPanel.add(new Label("Max Greedy Tries (1...50)"));
    sliderPanel.add(trieSlide_);
    
   
    //addTextFieldOption(simplicityImpact,"Simplicity Impact");
    //addTextFieldOption(defaultWeight,"Default Weight");
    //add(new Label());

    add("Center",sliderPanel);

    {
      Panel temp = new Panel();
      temp.add(done_);
      add("South",temp);
    }

    pack();
  }

  Slider makeSlider(float min, float max, float value)
  {
    Assert.isTrue(min <= value,"min <= value");
    Assert.isTrue(max >= value,"max >= value");

    int dv = (int)(value*100);
    int imin = (int)(min*100);
    int imax = (int)(max*100);
    
    return makeSlider(imin,imax,dv);
  }
/*
    Slider slider = new Slider();
    slider.SetWidth(200);
    slider.SetHeight(40);
    slider.SetMinimum(imin);
    slider.SetMaximum(imax);
    slider.SetValue(dv);
    slider.SetBarColor(Color.black);

    return slider;
  }
*/

  Slider makeSlider(int min, int max, int value)
  {
    Assert.isTrue(min <= value,"min <= value");
    Assert.isTrue(max >= value,"max >= value");

    Slider slider = new Slider();
    slider.SetWidth(200);
    slider.SetHeight(40);
    slider.SetMinimum(min);
    slider.SetMaximum(max);
    slider.SetValue(value);
    slider.SetBarColor(Color.black);

    return slider;
  }

  public float getSimplicityImpact()
  {
    return ((float)siSlide_.GetValue())/100;
  }

  public float getDefaultWeight()
  {
    return ((float)dwSlide_.GetValue())/100;
  }

  public float getExcitationWeight()
  {
    return ((float)ewSlide_.GetValue()/100);
  }

  public float getDataExcitation()
  {
    return ((float)ewSlide_.GetValue())/100;
  }

  public float getInhibitionWeight()
  {
    return ((float)iwSlide_.GetValue())/100;
  }

  public int getMaxFlips()
  {
    return flipSlide_.GetValue();
  }

  public int getMaxTries()
  {
    return trieSlide_.GetValue();
  }

//  void addTextFieldOption(String current, String label)
//  {
//    Frame f = new Frame();
//    TextField tf = new TextField(current);
//    Label l = new Label(label);
//    add(tf);
//    add(l);
//  }

  public boolean action(Event evt, Object arg)
  {
    if (evt.target.equals(done_)) {
      // save all the values
      //dispose();
      hide();
      return true;
    } // if
    return super.action(evt,arg);
  }

/*
  public static void main(String[] argv)
  {
    Frame f = new Frame();
    f.add("Center",new Label("Me the application"));
    f.pack();
    f.show();

    echoOptionsBox eob = new echoOptionsBox("1","2","3");
    eob.show();
  }
*/

} // class echoOptionsBox
