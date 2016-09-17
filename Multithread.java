package ray_tracer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ray_tracer.Vector;
import ray_tracer.Plane;
import ray_tracer.Sphere;
import ray_tracer.Body;
import ray_tracer.RayTracerV2;

public class Multithread extends JPanel implements Runnable {

	private BufferedImage canvas;
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int DEPTH = 1000;
	public static final int RADIUS = (int) (HEIGHT * .375);
	double maxt = 0;
	double mint = 2000;
	Color lastColor;

	public static Sphere sphere1 = new Sphere(new Vector(0, 300, 200), 150, .2, .7, .1, .5);
	public static Sphere sphere2 = new Sphere(new Vector(0, 300, 400), 150, 0, 1, 0, .5);
	public static Sphere sphere3 = new Sphere(new Vector(0, 300, 600), 150, 0, 0, 1, .5);
	static Vector eye = new Vector(0, -2000, 500);
	static Vector eyeDiff = new Vector(0, 0, 100);
	static int viewingDistance = (int) eye.norm() / 2;
	static int fov = 45;

	public static Plane plane1 = new Plane(new Vector(0, 0, -000), new Vector(0, 0, 1), 0,0,1, .5);
	public static Plane plane2 = new Plane(new Vector(300, 2000, -400), new Vector(-.3, -.3, .9), 0,0,1, .5);

	public static Vector light = new Vector(5000, 0, -5000);

	static ArrayList<Body> bodies = new ArrayList<Body>();

	static int num = 0;
	static int sum = 0;
	static int avg = 0;

	static boolean[] points = new boolean[WIDTH * HEIGHT];

	static int[] colors = new int[WIDTH * HEIGHT];

	public Multithread(int width, int height) {
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
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
		ForkJoinPool forkJoinPool = new ForkJoinPool(16);
		MyRecursiveTask mtask = new MyRecursiveTask(16, 0, 1000, 0, 1000);
		int[] mergedResult = forkJoinPool.invoke(mtask);
		try {
			forkJoinPool.shutdown();
			forkJoinPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int h = 0; h < HEIGHT; h++) {
			for (int w = 0; w < WIDTH; w++) {
				int colVal = mergedResult[w * HEIGHT + h];
				int r = colVal % 256;
				int g = (colVal / 256) % 256;
				int b = (colVal / 256) / 256;
				Color c = new Color(r, g, b);
				canvas.setRGB(w, h, c.getRGB());
			}
			repaint();
		}

	}

	private int[][] subsets(int[] val, int subsets) {
		int[][] toReturn = new int[4][val.length / 4];
		for (int i = 0; i < subsets; i++) {
			for (int j = 0; j < val.length / 4; j++) {
				toReturn[i][j] = val[i * val.length / 4 + j];
			}
		}
		return toReturn;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Ray Tracing Demo");
		Multithread panel = new Multithread(WIDTH, HEIGHT);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sphere1.setCenter(RayTracerV2.rotateAroundX(sphere1.getCenter(), Math.PI / 1.5));
		//sphere2.setCenter(RayTracerV2.rotateAroundX(sphere2.getCenter(), Math.PI / -1.5));
		Thread newThread = new Thread(panel);
		newThread.start();
	}

	public void run() {
		while (true) {
			num++;
			long start = System.currentTimeMillis();
			sphere1.setCenter(RayTracerV2.rotateAroundX(sphere1.getCenter(), Math.PI / 100));
			//sphere2.setCenter(RayTracerV2.rotateAroundX(sphere2.getCenter(), Math.PI / 100));
			//sphere3.setCenter(RayTracerV2.rotateAroundX(sphere3.getCenter(), Math.PI / 100));
			paintCanvas();
			long end = System.currentTimeMillis();
			if (num != 1) {
				sum += end - start;
				avg = sum / (num - 1);
			}
			if(num == 100){
				num = 0;
				sum = 0;
			}
			System.out.println(end - start + "\t" + avg);
		}
	}
}