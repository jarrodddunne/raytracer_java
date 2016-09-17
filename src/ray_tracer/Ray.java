package ray_tracer;

import ray_tracer.Vector;

public class Ray {
	
	private Vector u;
	private Vector v;
	
	public Ray(Vector u, Vector v){
		this.u = u;
		this.v = v;
	}

	public Vector getU() {
		return u;
	}

	public void setU(Vector u) {
		this.u = new Vector(u);
	}

	public Vector getV() {
		return v;
	}

	public void setV(Vector v) {
		this.v = new Vector(v);
	}
	
	public Vector evaluate(double t){
		Vector v1 = new Vector(getV());
		v1 = Vector.scale(v1,t);
		return Vector.add(u,v1);
	}
	
	public String toString(){
		return "Ray:\n\tU: " + u + "\n\tV: " + v; 
	}
	
}
