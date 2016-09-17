package ray_tracer;

import ray_tracer.Vector;
import ray_tracer.Ray;

import java.awt.Color;

import ray_tracer.Body;

public class Sphere extends Body{

	private Vector center;
	private double radius;
	private double reflectiveIndex;
	private double transparencyIndex;
	private double opaqueIndex;
	private double lambert;
	private float indexOfRefraction = (float) 1.5;

	public Sphere(Vector center, double radius, double rIndex, double tIndex, double oIndex, double lambert) {
		this.center = center;
		this.radius = radius;
		this.reflectiveIndex = rIndex;
		this.transparencyIndex = tIndex;
		this.opaqueIndex = oIndex;
		this.lambert = lambert;
	}

	public double[] intersectionDouble(Ray r) {
		double a = r.getV().dotProduct(r.getV());
		Vector e = r.getV();
		Vector d = r.getU();
		Vector ce = center;
		double b = 2*(e.getX()*(d.getX()-ce.getX()) + e.getY()*(d.getY()-ce.getY()) + e.getZ()*(d.getZ()-ce.getZ()));
		double c = Math.pow((d.getX()-ce.getX()), 2)+Math.pow((d.getY()-ce.getY()), 2) + Math.pow((d.getZ()-ce.getZ()), 2)-Math.pow(radius, 2);
		double t1 = 0;
		if (b * b <= 4 * a * c) {
			return null;
		} else if (b > 0) {
			t1 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		} else {
			t1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		}
		double t2 = c / (a * t1);
		return new double[] {t1,t2};
	}
	
	public double intersection1(Ray r) {
		double a = r.getV().dotProduct(r.getV());
		Vector e = r.getV();
		Vector d = r.getU();
		Vector ce = center;
		double b = 2*(e.getX()*(d.getX()-ce.getX()) + e.getY()*(d.getY()-ce.getY()) + e.getZ()*(d.getZ()-ce.getZ()));
		double c = Math.pow((d.getX()-ce.getX()), 2)+Math.pow((d.getY()-ce.getY()), 2) + Math.pow((d.getZ()-ce.getZ()), 2)-Math.pow(radius, 2);
		double t1 = 0;
		if (b * b <= 4 * a * c) {
			return 0;
		}
		t1 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		double t2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		if(t2 < t1){
			double temp = t1;
			t1 = t2;
			t2 = temp;
		}
		Vector p1 = r.evaluate(t1);
		Vector p2 = r.evaluate(t2);
		p1 = Vector.subtract(p1,center);
		p2 = Vector.subtract(p2,center);
		if(t1 < t2){
			return t1;
		} else {
			return t2;
		}
	}
	
	public double intersectionOld(Ray r){
		Vector d = new Vector(r.getV());
		Vector o = new Vector(r.getU());
		Vector cv = new Vector(center);
		double a = d.dotProduct(d);
		Vector CmO = Vector.subtract(cv,o);
		double b = -2*CmO.dotProduct(d);
		double c = CmO.dotProduct(CmO) - radius*radius;
		double t1;
		double t2;
		if (b * b <= 4 * a * c) {
			return 0;
		} else if (b > 0) {
			t1 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
			t2 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		} else {
			t1 = (-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
			t2 = (-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
		}
		return Math.min(t1, t2);
	}
	
	public double intersection(Ray r){
		Vector eyeToCenter = Vector.subtract(center, r.getU());
		double v = eyeToCenter.dotProduct(r.getV());
		double eoDot = eyeToCenter.dotProduct(eyeToCenter);
		double disriminant = (radius*radius) - eoDot + (v*v);
		if(disriminant < 0){
			return 0;
		} else {
			return v-Math.sqrt(disriminant);
		}
	}
	
	
	public Ray reflection1(Ray r){
		Vector point = r.evaluate(intersection(r));
		Vector norm = r.evaluate(intersection(r));
		//norm = norm.subtract(point);
		norm = Vector.scale(norm,1/point.norm());
		double nl = norm.dotProduct(r.getV());
		Vector.scale(norm,2*nl*point.norm());
		Ray newR = new Ray(point, Vector.subtract(r.getV(),norm));
		return newR;
	}
	
	
	public Ray reflection(Ray r){
		Vector p1 = r.evaluate(intersection(r));
		Vector n = Vector.subtract(p1,center);
		n.normalize();
		Vector d = new Vector(r.getV());
		double DdN = d.dotProduct(n);
		Vector newN = new Vector(n);
		newN = Vector.scale(newN,DdN);
		newN = Vector.scale(newN,2);
		Vector rv = Vector.subtract(d,newN);
		Vector point = r.evaluate(intersection(r));
		Ray rr = new Ray(point,rv);
		return rr;
	}
	
	public Ray translucencyRay(Ray r){
		Vector point = r.evaluate(intersection(r));
		Vector norm = Vector.subtract(point,center);
		norm.normalize();
		Ray l = new Ray(Vector.scale(r.getV(),-1),point);
		double n = 1/indexOfRefraction;
		double distance = Vector.subtract(point, center).norm();
		if(distance < radius*2){
			n = Math.pow(n, -1);
		}
		double cosT = Math.sqrt(1-1/(n*n)*(1-l.getV().dotProduct(norm)));
		double cosL = l.getV().dotProduct(norm);
		Vector t = Vector.scale(l.getV(),-1/n);
		t = Vector.subtract(t, Vector.scale(norm, cosT-1/n*cosL));
		Ray refractiveRay = new Ray(point, t);
		return refractiveRay;
	}
	
	public double getIntensity(Vector point, Vector lightSource){
		Vector lightToCenter = Vector.subtract(center,lightSource);
		lightToCenter.normalize();
		Vector centerToPoint = Vector.subtract(point, center);
		centerToPoint.normalize();
		double dot = lightToCenter.dotProduct(centerToPoint);
		dot *= lambert;
		if(dot < 0){
			return 0;
		}
		return dot;
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
	
	Color getColor(RayTracerV2 rv, Vector v) {
		//if(isInShadow(rv, v,RayTracerV2.light)){ // Shader
		if(true){
			return Color.BLUE;
		}
		return RayTracerV2.blend(Color.BLUE, Color.BLACK, (int) (100*getIntensity(v, RayTracerV2.light)));
	}

	public Vector getCenter() {
		return center;
	}

	public void setCenter(Vector center) {
		this.center = center;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
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
		return indexOfRefraction;
	}
}
