package ray_tracer;

public class Vector {

	private double x;
	private double y;
	private double z;

	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(Vector toCopy){
		x = toCopy.x;
		y = toCopy.y;
		z = toCopy.z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double dotProduct(Vector v2) {
		double sum = 0;
		sum += x*v2.getX();
		sum += y*v2.getY();
		sum += z*v2.getZ();
		return sum;
	}

	public static Vector add(Vector v1, Vector v2) {
		Vector toReturn = new Vector(v1);
		toReturn.setX(v1.getX() + v2.getX());
		toReturn.setY(v1.getY() + v2.getY());
		toReturn.setZ(v1.getZ() + v2.getZ());
		return toReturn;
	}
	
	public static Vector subtract(Vector v1, Vector v2){
		Vector toReturn = new Vector(v2);
		toReturn = scale(toReturn,-1.0);
		toReturn = add(toReturn,v1);
		return toReturn;
	}
	
	public static Vector scale(Vector v, double scalar){
		Vector toReturn = new Vector(v);
		toReturn.setX(v.getX()*scalar);
		toReturn.setY(v.getY()*scalar);
		toReturn.setZ(v.getZ()*scalar);
		return toReturn;
	}
	
	public double norm(){
		double dp = this.dotProduct(this);
		return Math.sqrt(dp);
	}
	
	public String toString(){
		return x + "," + y + "," + z;
	}
	
	public void normalize(){
		Vector v = scale(this,1/norm());
		this.x = v.getX();
		this.y = v.getY();
		this.z = v.getZ();
	}
	
	public Vector crossProduct(Vector v2){
		double newX = this.y*v2.z-this.z*v2.y;
		double newY = this.z*v2.x-this.x*v2.z;
		double newZ = this.x*v2.y-this.y*v2.x;
		return new Vector(newX, newY, newZ);
	}
}
