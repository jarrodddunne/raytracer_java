package ray_tracer;

import java.awt.Color;
import java.util.ArrayList;
import ray_tracer.Vector;
import ray_tracer.Ray;
import ray_tracer.Plane;
import ray_tracer.Sphere;
import ray_tracer.Body;
import ray_tracer.Multithread;

public class RayTracerV2 {

	public static final int WIDTH = 1000;
	public static final int HEIGHT = 1000;
	public static final int DEPTH = 1000;
	public static final int RADIUS = (int) (HEIGHT * .375);
	double maxt = 0;
	double mint = 2000;
	Color lastColor;
	public static Color fog = Color.WHITE;

	public static Sphere sphere1 = new Sphere(new Vector(0, 300, 200), 150, 0, 1, 0, .5);
	public static Sphere sphere2 = new Sphere(new Vector(0, 300, 400), 150, .5, .5, 0, .5);
	public static Sphere sphere3 = new Sphere(new Vector(0, 300, 600), 150, 0, 0, 1, .5);
	public static Vector eye = new Vector(0, -2000, 500);
	Vector eyeDiff = new Vector(0, 0, 100);
	int viewingDistance = (int) eye.norm() / 2;
	int fov = 45;

	public static Plane plane1 = new Plane(new Vector(0, 0, -200), new Vector(0, 0, 1), 0, 0, 1, .5);
	public static Plane plane2 = new Plane(new Vector(300, 2000, -400), new Vector(-.3, -.3, .9), 0, 0, 1, .5);

	public static Vector light = new Vector(5000, 0, -5000);

	ArrayList<Body> bodies = new ArrayList<Body>();

	int sw;
	int sh;
	int ew;
	int eh;

	public RayTracerV2(int sw, int sh, int ew, int eh) {
		this.sw = sw;
		this.sh = sh;
		this.ew = ew;
		this.eh = eh;
		bodies.add(Multithread.plane1);
		// bodies.add(plane2);
		bodies.add(Multithread.sphere1);
		//bodies.add(Multithread.sphere2);
		//bodies.add(Multithread.sphere3);
	}

	public int[] getColors() {
		int[] vals = new int[(ew - sw) * (eh - sh)];
		for (int w = sw; w < ew; w++) {
			for (int h = sh; h < eh; h++) {
				Color c = getColor(w, h);
				int width = w - sw;
				int height = h - sh;
				vals[width * (eh - sh) + height] = c.getRed() + c.getGreen() * 256 + c.getBlue() * 256 * 256;
			}
		}
		return vals;
	}

	public Color getColor(int width, int height) {
		Vector to = Vector.subtract(new Vector(0, 0, 0), eye);
		to.normalize();
		Vector eyeVector = new Vector(to);
		Vector UP = new Vector(0, 0, -1);
		Vector vpRight = eyeVector.crossProduct(UP);
		Vector vpUp = vpRight.crossProduct(eyeVector);
		double fovRadians = Math.PI * (fov / 2.0) / 180;
		double HeightWidthRatio = HEIGHT * 1.0 / WIDTH;
		double halfWidth = Math.tan(fovRadians);
		double halfHeight = HeightWidthRatio * halfWidth;
		double cameraWidth = halfWidth * 2;
		double cameraHeight = halfHeight * 2;
		double pixelWidth = cameraWidth / (WIDTH - 1);
		double pixelHeight = cameraHeight / (HEIGHT - 1);
		double XtPWmHW = width * pixelWidth - halfWidth;
		double YtPHmHH = height * pixelHeight - halfHeight;
		Vector xcomp = Vector.scale(vpRight, XtPWmHW);
		Vector ycomp = Vector.scale(vpUp, YtPHmHH);
		Vector xycomp = Vector.add(xcomp, ycomp);
		Vector rayvector = Vector.add(eyeVector, xycomp);
		rayvector.normalize();
		Vector start = new Vector(eye);
		Ray r = new Ray(start, rayvector);
		Body firstBody = getNextCollision(r);
		Body nextBody = getNextCollision(r);
		/*
		Color blended = Color.WHITE;
		while (nextBody != (Body) null && nextBody.getOpaqueIndex() != 1) {
			if (blended != null) {
				Vector point = r.evaluate(nextBody.intersection(r));
				blended = blend(blended, nextBody.getColor(this, point), (int) (100 * nextBody.getIntensity(point, light)));
			}
			r = nextBody.reflection(r);
			nextBody = getNextCollision(r);
		}
		if (nextBody == null) {
			if (firstBody != (Body) null) {
				return blended;
			}
			return Color.WHITE;
		}
		double t = nextBody.intersection(r);
		Vector intpoi = r.evaluate(t);
		if (t > maxt) {
			maxt = t;
		}
		if (t < mint) {
			mint = t;
		}
		if (t >= 10000) {
			if (firstBody.intersection(new Ray(start, rayvector)) < 10000) {
				return (firstBody.getColor(this, intpoi));
			} else {
				return Color.WHITE;
			}
		} else {
			Color toReturn = blend(Color.WHITE, nextBody.getColor(this, intpoi), (int) (t / 100));
			return toReturn;
		}
		*/
		if (nextBody == null) {
			return Color.WHITE;
		}
		Color toReturn = getColor(r,nextBody,0);
		return toReturn;
	}

	public Color getColor(Ray r, Body body, int its) {
		if (body == (Body) null) {
			return fog;
		}
		if (its > 5){
			return fog;
		}
		Vector intersectionPoint = r.evaluate(body.intersection(r));
		Color opaqueColor = Color.BLACK;
		Color reflectiveColor = Color.BLACK;
		Color refractiveColor = Color.BLACK;
		if (body.getOpaqueIndex() != 0) {
			opaqueColor = body.getColor(this, intersectionPoint);
		}
		if (body.getReflectiveIndex() != 0) {
			Ray newRay = body.reflection(r);
			Body nextBody = getNextCollision(newRay);
			reflectiveColor = getColor(newRay, nextBody, its+1);
		}
		if (body.getTransparencyIndex() != 0) {
			Ray newRay = body.translucencyRay(r);
			Body nextBody = getNextCollision(newRay);
			refractiveColor = getColor(newRay, nextBody, its+1);
		}
		int red = (int) (opaqueColor.getRed() * body.getOpaqueIndex() + reflectiveColor.getRed() * body.getReflectiveIndex() + refractiveColor.getRed() * body.getTransparencyIndex());
		int green = (int) (opaqueColor.getGreen() * body.getOpaqueIndex() + reflectiveColor.getGreen() * body.getReflectiveIndex() + refractiveColor.getGreen() * body.getTransparencyIndex());
		int blue = (int) (opaqueColor.getBlue() * body.getOpaqueIndex() + reflectiveColor.getBlue() * body.getReflectiveIndex() + refractiveColor.getBlue() * body.getTransparencyIndex());
		Color color = new Color(red, green, blue);
		return color;
	}

	public Body getNextCollision(Ray r) {
		Body min = null;
		for (Body b : bodies) {
			if (b.intersection(r) > 0.0001) {
				if (min == null || b.intersection(r) < min.intersection(r)) {
					min = b;
				}
			}
		}
		return min;
	}

	public int widthToX(int width) {
		return width - WIDTH / 2;
	}

	public int heightToZ(int height) {
		return HEIGHT / 2 - height;
	}

	public static Color blend(Color c1, Color c2, int percentC1) {
		double pc1 = percentC1 / 100.0;
		double pc2 = 1 - pc1;
		int red = (int) (pc1 * c1.getRed() + pc2 * c2.getRed());
		int green = (int) (pc1 * c1.getGreen() + pc2 * c2.getGreen());
		int blue = (int) (pc1 * c1.getBlue() + pc2 * c2.getGreen());
		return new Color(red, green, blue);
	}

	public int[] call() {
		int[] toReturn = getColors();
		return toReturn;
	}

	public static Vector rotateAroundX(Vector p, double angle) {
		double newX = Math.cos(angle) * p.getX() - Math.sin(angle) * p.getY();
		double newY = Math.sin(angle) * p.getX() + Math.cos(angle) * p.getY();
		Vector toReturn = new Vector(p);
		toReturn.setX(newX);
		toReturn.setY(newY);
		return toReturn;
	}
}