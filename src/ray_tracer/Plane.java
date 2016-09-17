package ray_tracer;

import ray_tracer.Vector;
import ray_tracer.Ray;

import java.awt.Color;

import ray_tracer.Body;

public class Plane extends Body {

	private boolean reflective;

	private Vector p0;
	private Vector n;
	private Vector r;
	private double reflectiveIndex;
	private double transparencyIndex;
	private double opaqueIndex;
	private double lambert;

	public Plane(Vector p0, Vector n, double rIndex, double tIndex, double oIndex, double lambert) {
		this.p0 = p0;
		this.n = n;
		this.reflectiveIndex = rIndex;
		this.transparencyIndex = tIndex;
		this.opaqueIndex = oIndex;		n.normalize();
		this.lambert = lambert;
		this.r = rotate90Z(n);
		r.normalize();
	}

	private Vector rotate90Z(Vector up) {
		double x = up.getX();
		double y = up.getY();
		double z = up.getZ();
		double nx = -1 * y;
		double ny = x;
		double nz = z;
		return new Vector(nx, ny, nz);
	}

	public double intersection(Ray r) {
		double d = -1 * n.dotProduct(p0);
		double ln = r.getV().dotProduct(n);
		double un = r.getU().dotProduct(n);
		if (Math.abs(ln) < 0.0001) {
			return 0;
		} else {
			double t = -(un + d) / ln;
			return t;
		}
	}

	boolean isReflective() {
		return reflective;
	}

	public Ray reflection(Ray r) {
		Vector p1 = r.evaluate(intersection(r));
		Vector d = new Vector(r.getV());
		double DdN = d.dotProduct(n);
		Vector newN = new Vector(n);
		newN = Vector.scale(newN, DdN);
		newN = Vector.scale(newN, 2);
		Vector rv = Vector.subtract(d, newN);
		// System.out.println(intersection(r) + "\t" +
		// r.evaluate(intersection(r)));
		Vector point = r.evaluate(intersection(r));
		Ray rr = new Ray(point, rv);
		return rr;
		/*
		 * Vector point = r.evaluate(intersection(r)); Vector norm = new
		 * Vector(point); norm.normalize(); double nl =
		 * norm.dotProduct(r.getV()); norm.scale(2*nl*point.norm()); Ray newR =
		 * new Ray(point, r.getV().subtract(norm)); return newR;
		 */
	}

	Color getColor(RayTracerV2 rv, Vector v) {
		/*
		if (isInShadow(rv, v, RayTracerV2.light)) {
			return Color.BLACK;
		}
		*/
		Vector b = r.crossProduct(n);
		b.normalize();
		Vector diff = Vector.subtract(p0, v);
		double rdiff = r.dotProduct(diff);
		rdiff = 1000000 - rdiff;
		double bdiff = b.dotProduct(diff);
		bdiff = 1000000 - bdiff;
		if(((int) (1000 - diff.getX()+.5) / 100 + (int) (diff.getY()+.5) / 100 + (int) (diff.getZ()+.5) / 100) % 2 == 0){
		/*
		if ((((int) rdiff) / 50 + ((int) bdiff) / 50) % 2 == 0) {
		*/
			return Color.BLACK;
		} else {
			if (isInShadow(rv, v, RayTracerV2.light)) {
				return RayTracerV2.blend(Color.WHITE, Color.BLACK, 50);
			} else {
				return Color.WHITE;
			}
		}
	}
	
	double getOpaqueIndex() {
		return opaqueIndex;
	}

	double getTransparencyIndex(){
		return transparencyIndex;
	}
	
	double getReflectiveIndex(){
		return reflectiveIndex;
	}

	double getIntensity(Vector point, Vector lightSource) {
		Vector lightToPoint = Vector.subtract(point, lightSource);
		lightToPoint.normalize();
		double dot = lightToPoint.dotProduct(n);
		dot *= lambert;
		return dot;
	}

	boolean isInShadow(RayTracerV2 rv, Vector point, Vector lightSource) {
		Vector toLight = Vector.subtract(point, lightSource);
		Vector toLN = new Vector(toLight);
		toLight.normalize();
		Ray r = new Ray(point, toLight);
		double intersect = toLN.norm() / toLight.norm();
		for (Body b : rv.bodies) {
			if (b.intersection(r) > .001 && b.intersection(r) < intersect) {
				return true;
			}
		}
		return false;
	}

	float getIndexOfRefraction() {
		return 0;
	}

	@Override
	Ray translucencyRay(Ray r) {
		// TODO Auto-generated method stub
		return null;
	}
}
