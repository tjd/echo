package JavaECHO;

import java.io.*;
import java.util.*;
import java.awt.*;

final public class ECHOgraph extends Dialog {

  ECHO echo_;
  
  int height_ = 500;
  int width_ = 500;
  EchoGraphPanel panel_ = new EchoGraphPanel(height_,width_);
  

  public ECHOgraph(ECHO echo)
  {
    super(new Frame(),false);
    setECHO(echo);
    setTitle(echo_.name());
    setLayout(new BorderLayout());

    add("Center",panel_);

    Panel p = new Panel();
    add("North", p);
    p.add(new Button("Scramble"));
    p.add(new Button("Shake"));

    Panel p2 = new Panel();
    p2.add(new Button("Dismiss"));
    add("South",p2);
    resize(height_,width_);

    String edges = echo_.makeGraphString();
    addEdges(edges);
 
    panel_.start();
    show();
  }

  private void addEdges(String edges)
  {
    for (StringTokenizer t = new StringTokenizer(edges, ","); 
	 t.hasMoreTokens() ; ) {
            String str = t.nextToken();
            //System.out.println("str="+str);
            int i = str.indexOf('-');
            if (i > 0) {
                int len = 50;
                int j = str.indexOf('/');
                if (j > 0) {
                    len = Integer.valueOf(str.substring(j+1)).intValue();
                    str = str.substring(0, j);
                }
                String ss1 = str.substring(0,i);
                String ss2 = str.substring(i+1);
                //System.out.println("ss1="+ss1);
                //System.out.println("ss2="+ss2);
                panel_.addEdge(ss1, ss2, len);
	      } // if
	  } // for

    Dimension d = size();
    String center = echo_.specialUnit.name();
    if (center != null) {
      Node n = panel_.nodes[panel_.findNode(center)];
      n.x = d.width / 2;
      n.y = d.height / 2;
      n.fixed = true;
    } // if
  } 

  public boolean action(Event evt, Object arg) 
  {
    if ("Scramble".equals(arg)) {
      Dimension d = size();
      for (int i = 0 ; i < panel_.nnodes ; i++) {
	Node n = panel_.nodes[i];
	if (!n.fixed) {
	  n.x = 10 + (d.width-20)*Math.random();
	  n.y = 10 + (d.height-20)*Math.random();
	}
      }
      return true;
    } else if ("Shake".equals(arg)) {
      Dimension d = size();
      for (int i = 0 ; i < panel_.nnodes ; i++) {
	Node n = panel_.nodes[i];
	if (!n.fixed) {
	  n.x += 80*Math.random() - 40;
	  n.y += 80*Math.random() - 40;
	}
      }
      return true;
    } else if ("Dismiss".equals(arg)) {
      panel_.stop();
      dispose();
      return true;
    } // if
    return false;
  }

  public void setECHO(ECHO echo)
  {
    echo_ = echo;
  }


  public static void main (String[] argv) throws IOException
  {
    ECHO echo = new ECHO("Simplicity","Simplicity echo test",
			 echoStrings.simplicity);
    ECHOgraph eg = new ECHOgraph(echo);

    //eg.show();
  }

} // ECHOgraph


class EchoGraphPanel extends Panel implements Runnable {

  final public static int maxNodes = 100;
  final public static int maxEdges = 200;

  //Graph graph;
  int nnodes;
  Node nodes[] = new Node[maxNodes];
  
  int nedges;
  Edge edges[] = new Edge[maxEdges];
  
  int height_;
  int width_;
  
  Thread relaxer;

//    GraphPanel(Graph graph) {
//	this.graph = graph;
//    }

  EchoGraphPanel(int height, int width)
  {
    height_ = height;
    width_ = width;
  }
  
  int findNode(String lbl) {
    for (int i = 0 ; i < nnodes ; i++) {
      if (nodes[i].lbl.equals(lbl)) {
	return i;
      }
    }
    return addNode(lbl);
  }
  
  int addNode(String lbl) {
    Node n = new Node();
    n.x = 10 + 380*Math.random();
    n.y = 10 + 380*Math.random();
    n.lbl = lbl;
    nodes[nnodes] = n;
    return nnodes++;
  }

  void addEdge(String from, String to, int len) {
    Edge e = new Edge();
    e.from = findNode(from);
    e.to = findNode(to);
    e.len = len;
    edges[nedges++] = e;
  }

  public void run() {
    while (true) {
      relax();
      try {
	Thread.sleep(100);
      } catch (InterruptedException e) {
	break;
      }
    }
  }

  synchronized void relax() {
    for (int i = 0 ; i < nedges ; i++) {
      Edge e = edges[i];
      double vx = nodes[e.to].x - nodes[e.from].x;
      double vy = nodes[e.to].y - nodes[e.from].y;
      //System.out.println("vx="+vx+", vy="+vy);
      double len = Math.sqrt(vx * vx + vy * vy);
      double f = (edges[i].len - len) / (len * 3) ;
      double dx = f * vx;
      double dy = f * vy;
      //System.out.println("dx="+dx+", dy="+dy);
      
      nodes[e.to].dx += dx;
      nodes[e.to].dy += dy;
      nodes[e.from].dx += -dx;
      nodes[e.from].dy += -dy;
    }
    //System.out.println("--end loop 1--------------------");
    
    
    for (int i = 0 ; i < nnodes ; i++) {
      Node n1 = nodes[i];
      double dx = 0;
      double dy = 0;
      
      for (int j = 0 ; j < nnodes ; j++) {
	if (i == j) {
	  continue;
	}
	Node n2 = nodes[j];
	double vx = n1.x - n2.x;
	double vy = n1.y - n2.y;
	double len = vx * vx + vy * vy;
	if (len == 0) {
	  dx += Math.random();
	  dy += Math.random();
	} else if (len < 100*100) {
	  dx += vx / len;
	  dy += vy / len;
	}
      }
      double dlen = dx * dx + dy * dy;
      if (dlen > 0) {
	dlen = Math.sqrt(dlen) / 2;
	n1.dx += dx / dlen;
	n1.dy += dy / dlen;
      }
      //System.out.print("n1.x="+n1.x+", n1.y="+n1.y);
      //System.out.println(", n1.dx="+n1.dx+", n1.dy="+n1.dy);
    }
    //System.out.println("--end loop 2--------------------");
    
    //Dimension d = size();
    //System.out.println("d="+d);
    for (int i = 0 ; i < nnodes ; i++) {
      Node n = nodes[i];
      if (!n.fixed) {
	n.x += Math.max(-5, Math.min(5, n.dx));
	n.y += Math.max(-5, Math.min(5, n.dy));
	
	
	if (n.x < 0) {
	  //System.out.println("setting n.x to 0");
	  n.x = 0;
	} else if (n.x > width_) {
	  n.x = width_;
	}
	if (n.y < 0) {
	  //System.out.println("setting n.y to 0");
	  n.y = 0;
	} else if (n.y > height_) {
	  n.y = height_;
	}
      }
      n.dx /= 2;
      n.dy /= 2;
      //System.out.print("n.x="+n.x+", n.y="+n.y);
      //System.out.println(", n.dx="+n.dx+", n.dy="+n.dy);
    }
    //System.out.println("--end loop 3--------------------");
    repaint();
  }

  Node pick;
  boolean pickfixed;
  Image offscreen;
  Dimension offscreensize;
  Graphics offgraphics;
  
  
  final Color fixedColor = Color.red;
  final Color selectColor = Color.pink;
  final Color edgeColor = Color.black;
  final Color nodeColor = new Color(250, 220, 100);
  final Color stressColor = Color.darkGray;
  final Color arcColor1 = Color.black;
  final Color arcColor2 = Color.pink;
  final Color arcColor3 = Color.red;
  
  public void paintNode(Graphics g, Node n, FontMetrics fm) {
    int x = (int)n.x;
    int y = (int)n.y;
    g.setColor((n == pick) ? selectColor : 
	       (n.fixed ? fixedColor : nodeColor));
    int w = fm.stringWidth(n.lbl) + 10;
    int h = fm.getHeight() + 4;
    g.fillRect(x - w/2, y - h / 2, w, h);
    g.setColor(Color.black);
    g.drawRect(x - w/2, y - h / 2, w-1, h-1);
    g.drawString(n.lbl, x - (w-10)/2, (y - (h-4)/2) + fm.getAscent());
  }
  
  public synchronized void update(Graphics g) {
    Dimension d = size();
    if ((offscreen == null) || (d.width != offscreensize.width) || 
	(d.height != offscreensize.height)) {
      offscreen = createImage(d.width, d.height);
      offscreensize = d;
      offgraphics = offscreen.getGraphics();
      offgraphics.setFont(getFont());
    } // if
    
    offgraphics.setColor(getBackground());
    offgraphics.fillRect(0, 0, d.width, d.height);
    for (int i = 0 ; i < nedges ; i++) {
      Edge e = edges[i];
      int x1 = (int)nodes[e.from].x;
      int y1 = (int)nodes[e.from].y;
      int x2 = (int)nodes[e.to].x;
      int y2 = (int)nodes[e.to].y;
      int len = (int)Math.abs(Math.sqrt((x1-x2)*(x1-x2) + 
					(y1-y2)*(y1-y2)) - e.len);
      offgraphics.setColor((len < 10) ? arcColor1 : 
			   (len < 20 ? arcColor2 : arcColor3)) ;
      offgraphics.drawLine(x1, y1, x2, y2);
    }
    
    FontMetrics fm = offgraphics.getFontMetrics();
    for (int i = 0 ; i < nnodes ; i++) {
      paintNode(offgraphics, nodes[i], fm);
    } // for
    
    g.drawImage(offscreen, 0, 0, null);
  }
  
  public synchronized boolean mouseDown(Event evt, int x, int y) {
    double bestdist = Double.MAX_VALUE;
    for (int i = 0 ; i < nnodes ; i++) {
      Node n = nodes[i];
      double dist = (n.x - x) * (n.x - x) + (n.y - y) * (n.y - y);
      if (dist < bestdist) {
	pick = n;
	bestdist = dist;
      }
    }
    pickfixed = pick.fixed;
    pick.fixed = true;
    pick.x = x;
    pick.y = y;
    repaint();
    return true;
  }
  
  public synchronized boolean mouseDrag(Event evt, int x, int y) {
    pick.x = x;
    pick.y = y;
    repaint();
    return true;
  }
  
  public synchronized boolean mouseUp(Event evt, int x, int y) {
    pick.x = x;
    pick.y = y;
    pick.fixed = pickfixed;
    pick = null;
    
    repaint();
    return true;
  }

  public void start() {
    relaxer = new Thread(this);
    relaxer.start();
  }
  
  public void stop() {
    relaxer.stop();
  }
}
