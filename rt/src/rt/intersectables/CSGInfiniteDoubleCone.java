package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.MyMath;
import rt.Ray;
import rt.Spectrum;
import rt.materials.Diffuse;

public class CSGInfiniteDoubleCone extends CSGSolid {

	private Diffuse material;

	/**
	 * Always centered at (0,0,0), the radius is restricted by x² + y² = z²
	 */
	public CSGInfiniteDoubleCone() {
		this.material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
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
		Vector3f normal = new Vector3f(hitPoint);
		normal.z = 0;
		normal.normalize(); //possibly same as division by hitPoint.z^2
		//TODO: this possibly doesn't work yet..
		float angle;
		if (hitPoint.z > 0)
			angle = 45;
		else
			angle = -45;
		normal.z = (float) Math.tan(Math.toDegrees(angle));
		//normalize again:
		normal.normalize();
		
		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, 0, 0);
	}

}
