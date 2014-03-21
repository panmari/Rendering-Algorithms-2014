package rt.intersectables;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.MyMath;
import rt.Ray;
import rt.Spectrum;
import rt.materials.Diffuse;

import java.util.ArrayList;

/**
 * A plane for CSG operations. The plane represents a solid that fills a whole half-space.
 * The plane normal is assumed to point into the empty half-space, and the plane occupies the 
 * half-space opposite of the normal.
 */
public class CSGPlane extends CSGSolid {
	
	Vector3f normal;
	float d;
	public Material material;
	
	/**
	 * Makes a CSG plane.
	 * 
	 * @param normal the plane normal
	 * @param d distance of the plane to the origin, along the normal direction (sign is important!)
	 */
	public CSGPlane(Vector3f normal, float d) {		
		this(normal, d, new Diffuse(new Spectrum(1.f, 1.f, 1.f)));
	}		
	
	public CSGPlane(Vector3f normal, float d, Material material) {
		this.normal = new Vector3f(normal);
		this.normal.normalize();
		this.d = d;
		this.material = material;
	}

	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> boundaries = new ArrayList<IntervalBoundary>();
		
		IntervalBoundary b1, b2;
		b1 = new IntervalBoundary();
		b2 = new IntervalBoundary();
		
		HitRecord hitRecord = intersectPlane(r);
		if(hitRecord != null)
		{
			b1.hitRecord = hitRecord;
			b1.t = hitRecord.t;
			b2.hitRecord = null;
			
			// Determine if ray entered or left the half-space defined by the plane.
			if(normal.dot(r.direction) < 0)
			{
				b1.type = BoundaryType.START;
				b2.type = BoundaryType.END;
				if(hitRecord.t > 0)
					// If the t value of the START boundary was positive, so is
					// the t value of the END boundary
					b2.t = Float.POSITIVE_INFINITY;
				else
					// If the t value of the START boundary was negative, so is
					// the t value of the END boundary
					b2.t = Float.NEGATIVE_INFINITY;
			} else
			{
				b1.type = BoundaryType.END;
				b2.type = BoundaryType.START;
				if(hitRecord.t > 0)
					// If the t value of the END boundary was positive, then 
					// the t value of the START boundary is negative
					b2.t = Float.NEGATIVE_INFINITY;
				else
					// If the t value of the END boundary was negative, then 
					// the t value of the START boundary is positive
					b2.t = Float.POSITIVE_INFINITY;
			}
			
			boundaries.add(b1);
			boundaries.add(b2);
		}
		
		return boundaries;
	}
		
	/**
	 * Computes ray-plane intersection. Note: we return all hit points,
	 * also the ones with negative t-value, that is, points that lie "behind"
	 * the origin of the ray. This is necessary for CSG operations to work
	 * correctly!  
	 * 
	 * @param r the ray
	 * @return the hit record of the intersection point, or null 
	 */
	private HitRecord intersectPlane(Ray r) {

		float tmp = normal.dot(r.direction);
		
		if(tmp!=0)
		{
			// t parameter of hit point
			float t = -(normal.dot(r.origin) + d) / tmp;
		
			// Hit position
			Point3f position = r.pointAt(t);
			
			// Hit normal
			Vector3f retNormal = new Vector3f(normal);
			
			// Incident direction, convention is that it points away from the hit position
			Vector3f wIn = new Vector3f(r.direction);
			wIn.negate();
		
			//use projection to x/z plane to decide on texture coordinate
			//TODO: do this better
			float u = Math.abs(position.x % 1);
			float v = Math.abs(position.z % 1);
			return new HitRecord(t, position, retNormal, wIn, null, material, u, v); 
		} else
		{
			return null;
		}
	}
}
