package rt.intersectables;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.StaticVecmath;
import rt.materials.Diffuse;

public class Sphere implements Intersectable {

	
	private Point3f center;
	private float radius;
	private Material material;
	
	/**
	 * Creates a sphere
	 * @param center, a point inn world coordinates
	 * @param radius of resulting sphere
	 */
	public Sphere(Point3f center, float radius) {
		this.center = center;
		this.radius = radius;
		material = new Diffuse(new Spectrum(10.f, 0.f, 0.f));
	}
	@Override
	public HitRecord intersect(Ray r) {
		float a = r.direction.lengthSquared();
		Vector3f originCenter = new Vector3f();
		originCenter.sub(r.origin, center);
		float b = 2*r.direction.dot(originCenter);
		float c = originCenter.lengthSquared() - radius*radius;
		float rootDisc = (float)Math.sqrt(b*b - 4*a*c);
		if(rootDisc < 0)
			return null;
		// numerical magic copied from PBRT:
		float q;
		if (b < 0)
			q = (b - rootDisc)/-2;
		else
			q = (b + rootDisc)/-2;
		float t0 = q/a;
		float t1 = c/q;
		float closestT;
		if (t0 < t1) {
			closestT = t0;
		}
		else {
			closestT = t1;
		}
		Point3f closestHitPoint = r.pointAt(closestT);
		
		Vector3f normal = new Vector3f();
		normal.sub(closestHitPoint, this.center);
		//normalize:
		normal.scale(1/this.radius);
		
		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		
		return new HitRecord(closestT, closestHitPoint, normal, wIn, this, this.material, 0, 0);
	}

}
