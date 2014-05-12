package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;
import util.MyMath;

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
		ArrayList<IntervalBoundary> intervalBoundaries = new ArrayList<>();
		// d_x^2 + d_y^2 - d_z^2
		float a = r.direction.x * r.direction.x +
				r.direction.y * r.direction.y -
				r.direction.z*r.direction.z;
		//2*(o_x*d_x + o_y*d_y - o_z*d_z)
		float b = 2*(r.direction.x * r.origin.x +
				r.direction.y * r.origin.y -
				r.direction.z * r.origin.z);
		//o_x^2 + o_y^2 - o_z^2
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
		
		if ((b0.hitRecord.position.z < 0 && b1.hitRecord.position.z > 0) ||
				(b0.hitRecord.position.z > 0 && b1.hitRecord.position.z < 0)) //starts inside and looks towards other -> two more
		{
			IntervalBoundary b2, b3;
			if (b0.hitRecord.position.z < 0){
				b2 = new IntervalBoundary(Float.NEGATIVE_INFINITY, BoundaryType.START, null, null);
				b3 = new IntervalBoundary(Float.POSITIVE_INFINITY, BoundaryType.END, null, null);
			} else {
				b2 = new IntervalBoundary(Float.POSITIVE_INFINITY, BoundaryType.START, null, null);
				b3 = new IntervalBoundary(Float.NEGATIVE_INFINITY, BoundaryType.END, null, null);
			}
			intervalBoundaries.add(b2);
			intervalBoundaries.add(b3);
		}
		intervalBoundaries.add(b0);
		intervalBoundaries.add(b1);
		return intervalBoundaries;
	}

	private HitRecord makeHitRecord(float t, Ray r) {	
		Point3f hitPoint = r.pointAt(t);
		
		Vector3f normal = new Vector3f(hitPoint);
		normal.z *= -1;
		normal.normalize();

		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, 0, 0);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return BoundingBox.INFINITE_BOUNDING_BOX;
	}

}
