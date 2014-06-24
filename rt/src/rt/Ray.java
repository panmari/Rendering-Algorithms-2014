package rt;

import javax.vecmath.*;

/**
 * A ray represented by an origin and a direction.
 */
public class Ray {

	public Vector3f origin;
	public Vector3f direction;
	
	public Ray(Vector3f origin, Vector3f direction)
	{
		this.origin = new Vector3f(origin); 
		this.direction = new Vector3f(direction);
	}

	public Ray(Point3f origin, Vector3f direction, boolean epsilon) {
		Vector3f o = new Vector3f(direction);
		o.scale(0.001f);
		o.add(origin);
		this.origin = o; 
		this.direction = new Vector3f(direction);
	}

	public Point3f pointAt(float t) {
		Point3f p = new Point3f(direction);
		p.scale(t);
		p.add(this.origin);
		return p;
	}
}
