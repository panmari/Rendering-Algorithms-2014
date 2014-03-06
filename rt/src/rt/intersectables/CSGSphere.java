package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.MyMath;
import rt.Ray;
import rt.Spectrum;
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
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
	}
	
	/**
	 * Creates canonical sphere (at origin 0,0,0 with radius 1
	 * @param center, a point inn world coordinates
	 * @param radius of resulting sphere
	 */
	public CSGSphere() {
		this.center = new Point3f();
		this.radius = 1;
		material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
	}

	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		ArrayList<IntervalBoundary> intervalBoundaries = new ArrayList<>();
		
		float a = r.direction.lengthSquared();
		Vector3f originCenter = new Vector3f();
		originCenter.sub(r.origin, center);
		float b = 2*r.direction.dot(originCenter);
		float c = originCenter.lengthSquared() - radius*radius;
		Point2f t = MyMath.solveQuadratic(a, b, c);
		
		if(t == null) 	
			return intervalBoundaries;
		
		IntervalBoundary b0 = new IntervalBoundary(t.x, BoundaryType.START, 
				makeHitRecord(t.x, r), null);
		IntervalBoundary b1 = new IntervalBoundary(t.y, BoundaryType.END, 
				makeHitRecord(t.y, r), null);
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
