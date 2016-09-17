package ray_tracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private BufferedImage canvas;
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int DEPTH = 1000;
	public static final int RADIUS = (int) (HEIGHT * .375);
	public static final int CIRCLE_X = WIDTH / 2;
	public static final int CIRCLE_Y = HEIGHT / 2;
	public static final int CIRCLE_Z = DEPTH / 2;

	public ImagePanel(int width, int height) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		paintCanvas();
	}

	public Dimension getPreferredSize() {
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(canvas, null, null);
	}

	public void paintCanvas() {
		for (int x = 0; x < canvas.getWidth(); x++) {
			for (int y = 0; y < canvas.getHeight(); y++) {
				if (distanceToPoint(x, y, CIRCLE_X, CIRCLE_Y) <= RADIUS) {
					int b = (int) (heightAtCircleXY(x, y) * 1.0 / RADIUS * 255);
					//Color col = new Color(0, 0, zSlope(x,y));
					canvas.setRGB(x, y, getIntersectPoint(x, y).getRGB());
				} else {
					canvas.setRGB(x, y, getCheckerColor(x, y).getRGB());
				}
			}
		}
		repaint();
	}

	public int distanceToPoint(int x1, int y1, int x2, int y2) {
		int distance = (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
		return distance;
	}

	public Color getCheckerColor(int x, int y) {
		int xh = (x / 100) % 2;
		int yh = (y / 100) % 2;
		if ((xh + yh) % 2 == 0) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

	public Color getIntersectPoint(int x, int y) {
		double dx = 0.0;
		double dy = 0.0;
		int yd = y - CIRCLE_Y;
		int xd = x - CIRCLE_X;
		double xySlope = yd * 1.0 / xd;
		if (Math.abs(xySlope) > 1) {
			dx = 1;
			dy = Math.abs(xySlope);
		} else {
			dy = 1;
			dx = 1.0 / Math.abs(xySlope);
		}
		int height = heightAtCircleXY(x, y);
		double sphereAngle = height * 1.0 / distanceToPoint(x, y, CIRCLE_X, CIRCLE_Y);
		double sphereTanAngle = -1 / sphereAngle;
		double dz = -Math.cos(2 * sphereTanAngle);
		boolean left = x < CIRCLE_X;
		boolean up = y < CIRCLE_Y;
		double xdiff = 0.0;
		double ydiff = 0.0;
		double zdiff = 0.0;
		if (left) {
			xdiff = x / dx;
		} else {
			xdiff = (WIDTH - x) / dx;
		}
		if (up) {
			ydiff = y / dy;
		} else {
			ydiff = (HEIGHT - y) / dy;
		}
		zdiff = Math.abs((DEPTH / 2 - height) / dz);
		//System.out.println("X: " + x + "\tY: " + y + "\txd: " + xdiff + "\tyd: " + ydiff + "\tzd: " + zdiff);
		//System.out.println("dz: " + dz);
		int xint = 0;
		int yint = 0;
		int zint = 0;
		if (xdiff < ydiff && xdiff < zdiff) {
			if (up) {
				yint = (int) (y - dy * xdiff);
			} else {
				yint = (int) (y + dy * xdiff);
			}
			zint = Math.abs((int) (height + dz * xdiff));
			return getCheckerColor(yint, zint);
		} else if (ydiff < xdiff && ydiff < zdiff) {
			if(left){
				xint = (int) (x - dx*ydiff);
			} else {
				xint = (int) (x + dx * ydiff);
			}
			zint = Math.abs((int) (height + dz * ydiff));
			return getCheckerColor(xint, zint);
		} else {
			if(left){
				xint = (int) (x - dx*zdiff);
			} else {
				xint = (int) (x + dx * zdiff);
			}
			if (up) {
				yint = (int) (y - dy * zdiff);
			} else {
				yint = (int) (y + dy * zdiff);
			}
			return getCheckerColor(xint,yint);
		}
	}
	
	public int zSlope(int x, int y){
		int height = heightAtCircleXY(x, y);
		double sphereAngle = height * 1.0 / distanceToPoint(x, y, CIRCLE_X, CIRCLE_Y);
		double sphereTanAngle = -1 / sphereAngle;
		double dz = -Math.cos(2 * sphereTanAngle);
		if(dz == Double.NaN){
			return 0;
		}
		System.out.println("X: " + x + "\tY: " + y + "\tdz: " + dz);
		return Math.abs((int) Math.abs(255 * dz) % 255);
	}

	public int heightAtCircleXY(int x, int y) {
		int dist = distanceToPoint(x, y, CIRCLE_X, CIRCLE_Y);
		int height = (int) Math.sqrt(Math.pow(RADIUS, 2) - Math.pow(dist, 2));
		return height;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ray Tracing Demo");
		ImagePanel panel = new ImagePanel(WIDTH, HEIGHT);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}