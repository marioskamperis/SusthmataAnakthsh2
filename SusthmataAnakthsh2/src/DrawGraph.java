import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
	private static final double MAX_SCORE = 20;
	private static final int PREF_W = 800;
	private static final int PREF_H = 650;
	private static final double BORDER_GAP = 30;
	private static final Color GRAPH_COLOR = Color.green;
	private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final double GRAPH_POINT_WIDTH = 12;
	private static final double Y_HATCH_CNT = 10;
	private List<MyPoint> scores;
	public DrawGraph(List<MyPoint> scores) {
		this.scores = scores;
	}

	@Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D)g;
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
      double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

//      List<Point> graphPoints = new ArrayList<Point>();
//      for (double i = 0; i < scores.size(); i++) {
//         double x1 = (double) (i * xScale + BORDER_GAP);
//         double y1 = (double) ((MAX_SCORE - scores.get(i)) * yScale + BORDER_GAP);
//         graphPoints.add(new Point(x1, y1));
//      }

      // create x and y axes 
      g2.draw(new Line2D.Double(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP));
      g2.draw(new Line2D.Double(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP));

      // create hatch marks for y axis. 
      for (int i = 0; i < Y_HATCH_CNT; i++) {
         double x0 = BORDER_GAP;
         double x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
         double y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
         double y1 = y0;
         //g2.drawLine(x0, y0, x1, y1);
         g2.draw(new Line2D.Double(x0, y0, x1, y1));
      }

      // and for x axis
      for (int i = 0; i < scores.size() - 1; i++) {
         double x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
         double x1 = x0;
         double y0 = getHeight() - BORDER_GAP;
         double y1 = y0 - GRAPH_POINT_WIDTH;
         g2.draw(new Line2D.Double(x0, y0, x1, y1));
      }

      Stroke oldStroke = g2.getStroke();
      g2.setColor(GRAPH_COLOR);
      g2.setStroke(GRAPH_STROKE);
      for (int i = 0; i < scores.size() - 1; i++) {
         double x1 = scores.get(i).getX();
         double y1 = scores.get(i).getY();
         double x2 = scores.get(i + 1).getX();
         double y2 = scores.get(i + 1).getY();
         g2.draw(new Line2D.Double(x1, y1, x2, y2));         
      }

      g2.setStroke(oldStroke);      
      g2.setColor(GRAPH_POINT_COLOR);
      for (int i = 0; i < scores.size(); i++) {
         double x = scores.get(i).getX() - GRAPH_POINT_WIDTH / 2;
         double y = scores.get(i).getY() - GRAPH_POINT_WIDTH / 2;;
         double ovalW = GRAPH_POINT_WIDTH;
         double ovalH = GRAPH_POINT_WIDTH;
         g2.fillOval((int)Math.round(x), (int)Math.round(y), (int)Math.round(ovalW), (int)Math.round(ovalH));
      }
   }

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
	}

	public static void createAndShowGui(List<MyPoint> list) {

		DrawGraph mainPanel = new DrawGraph(list);

		JFrame frame = new JFrame("DrawGraph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
	}

//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				createAndShowGui();
//			}
//		});
//	}
}