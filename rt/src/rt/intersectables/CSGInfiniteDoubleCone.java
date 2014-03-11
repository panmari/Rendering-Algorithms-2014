package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.MyMath;
import rt.Ray;
import rt.Spectrum;
import rt.materials.Diffuse;

public class CSGInfiniteDoubleCone extends CSGSolid {

	private Material material;

	/**
	 * Always centered at (0,0,0), the radius is restricted by x² + y² = z²
	 */
	public CSGInfiniteDoubleCone() {
		this(new Diffuse(new Spectrum(1.f, 1.f, 1.f)));
	}
	
	/**
	 * Always centered at (0,0,0), the radius is restricted by x² + y² = z²
	 */
	public CSGInfiniteDoubleCone(Material m) {
		this.material = m;
	}
	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		Vector2f direction = new Vector2f(r.direction.x, r.direction.y);
		
		ArrayList<IntervalBoundary> intervalBoundaries = new ArrayList<>();
		float a = direction.lengthSquared() - r.direction.z*r.direction.z;
		float b = 2*(r.direction.x * r.origin.x +
				r.direction.y * r.origin.y -
				r.direction.z * r.origin.z);
		float c = r.origin.x * r.origin.x +
				r.origin.y * r.origin.y - 
				r.origin.z * r.origin.z;
		Point2f t = MyMath.solveQuadratic(a, b, c);
		if (t == null)
			return intervalBoundaries;
		
		HitRecord h0 = makeHitRecord(t.x, r);
		IntervalBoundary b0 = new IntervalBoundary(h0.t, findBoundaryType(h0, r), h0, null);
		
		HitRecord h1 = makeHitRecord(t.y, r);
		IntervalBoundary b1 = new IntervalBoundary(h1.t, findBoundaryType(h1, r), h1, null);
		intervalBoundaries.add(b0);
		intervalBoundaries.add(b1);
		return intervalBoundaries;
	}

	private HitRecord makeHitRecord(float t, Ray r) {	
		Point3f hitPoint = r.pointAt(t);
		Vector3f normalCyl = new Vector3f(hitPoint);
		normalCyl.z = 0;
		Vector3f tangential1 = new Vector3f();
		tangential1.cross(new Vector3f(0, 0, 1), normalCyl);
		Vector3f tangential2 = new Vector3f(hitPoint);
		Vector3f normal = new Vector3f();
		normal.cross(tangential1, tangential2);
		normal.normalize();
		if (hitPoint.z < 0)
			normal.z *= -1;

		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, 0, 0);
	}

}
