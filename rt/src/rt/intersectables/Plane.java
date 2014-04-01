package rt.intersectables;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

/**
 * A plane that can be intersected by a ray.
 */
public class Plane implements Intersectable {

	Vector3f normal;
	float d;
	public Material material;

	/**
	 * Construct a plane given its normal @param n and distance
	 * to the origin @param d. Note that the distance is along 
	 * the direction that the normal points. The sign matters!
	 * 
	 * @param normal normal of the plane
	 * @param d distance to origin measured along normal direction
	 */
	public Plane(Vector3f normal, float d)
	{
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		
		this.normal = new Vector3f(normal);
		this.normal.normalize();
		this.d = d;
	}
		
	public HitRecord intersect(Ray r) {

		float tmp = normal.dot(r.direction);
		
		if(tmp!=0)
		{
			float t = -(normal.dot(r.origin) + d) / tmp;
			if(t <= 0)
				return null;
			Point3f position = r.pointAt(t);
			Vector3f retNormal = new Vector3f(normal);
			// wIn is incident direction; convention is that it points away from surface
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
			wIn.normalize();
			
			return new HitRecord(t, position, retNormal, wIn, this, material, 0.f, 0.f); 
		} else
		{
			return null;
		}
	}

	@Override
	public BoundingBox getBoundingBox() {
		//TODO: do something smarter in case plane is axis aligned
		return new BoundingBox(new Point3f(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY), 
				new Point3f(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
	}

}
