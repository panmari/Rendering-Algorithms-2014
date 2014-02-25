package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.StaticVecmath;
import rt.intersectables.CSGSolid.BoundaryType;
import rt.materials.Diffuse;

public class CSGSphere extends CSGSolid {

	
	private Point3f center;
	private float radius;
	private Material material;
	
	/**
	 * Creates a sphere
	 * @param center, a point inn world coordinates
	 * @param radius of resulting sphere
	 */
	public CSGSphere(Point3f center, float radius) {
		this.center = center;
		this.radius = radius;
		material = new Diffuse(new Spectrum(7.f, 0.f, 0.f));
	}

	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		ArrayList<IntervalBoundary> intervalBoundaries = new ArrayList<>();
		
		float a = r.direction.lengthSquared();
		Vector3f originCenter = new Vector3f();
		originCenter.sub(r.origin, center);
		float b = 2*r.direction.dot(originCenter);
		float c = originCenter.lengthSquared() - radius*radius;
		float rootDisc = (float)Math.sqrt(b*b - 4*a*c);
		if(rootDisc < 0)
			return intervalBoundaries;
		// numerical magic copied from PBRT:
		float q;
		if (b < 0)
			q = (b - rootDisc)/-2;
		else
			q = (b + rootDisc)/-2;
		float t0 = q/a;
		float t1 = c/q;
		
		//make t0 always the intersection closer to the camera
		if (t0 > t1) {
			float swap = t0;
			t0 = t1;
			t1 = swap;
		}
		
		IntervalBoundary b0 = new IntervalBoundary(t0, BoundaryType.START, 
				makeHitRecord(t0, r), null);
		IntervalBoundary b1 = new IntervalBoundary(t1, BoundaryType.END, 
				makeHitRecord(t1, r), null);
		intervalBoundaries.add(b0);
		intervalBoundaries.add(b1);
		return intervalBoundaries;
	}
	
	private HitRecord makeHitRecord(float t, Ray r) {
		Point3f hitPoint = r.pointAt(t);
		Vector3f normal = new Vector3f();
		normal.sub(hitPoint, this.center);
		//normalize:
		normal.scale(1/this.radius);
		
		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, 0, 0);
	}
}
