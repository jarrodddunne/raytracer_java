package ray_tracer;

import java.awt.Color;

public abstract class Body {
	
	abstract double intersection(Ray r);
	
	abstract double getReflectiveIndex();
	
	abstract double getTransparencyIndex();
	
	abstract double getOpaqueIndex();
	
	abstract Ray reflection(Ray r);
	
	abstract Ray translucencyRay(Ray r);
	
	abstract Color getColor(RayTracerV2 rv, Vector v);
	
	abstract double getIntensity(Vector point, Vector lightSource);
	
	abstract boolean isInShadow(RayTracerV2 rv, Vector point, Vector lightSource);
	
	abstract float getIndexOfRefraction();

}
