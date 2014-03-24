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
import rt.StaticVecmath;
import rt.accelerators.BoundingBox;
import rt.intersectables.CSGSolid.BoundaryType;
import rt.intersectables.CSGSolid.IntervalBoundary;
import rt.materials.Diffuse;

public class CSGInfiniteCylinder extends CSGSolid {

	private Point3f center;
	private float radius;
	private Material material;

	/**
	 * An infinite cylinder centered around the z-axis with the given radius.
	 * @param center
	 * @param radius
	 */
	public CSGInfiniteCylinder(Material m) {
		this.center = new Point3f(0,0,0);
		this.radius = 1;
		this.material = m;
	}
	
	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		Vector2f center = new Vector2f(this.center.x, this.center.y);
		Vector2f direction = new Vector2f(r.direction.x, r.direction.y);
		Vector2f origin = new Vector2f(r.origin.x, r.origin.y);

		ArrayList<IntervalBoundary> intervalBoundaries = new ArrayList<>();
		float a = direction.lengthSquared();
		Vector2f originCenter = new Vector2f();
		originCenter.sub(origin, center);
		float b = 2*direction.dot(originCenter);
		float c = originCenter.lengthSquared() - radius*radius;
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
		Vector3f normal = new Vector3f();
		normal.sub(hitPoint, this.center);
		normal.z = 0;
		//normalize:
		normal.normalize();
		
		Vector3f wIn = new Vector3f(r.direction);
		wIn.normalize();
		wIn.negate();
		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, 0, 0);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(new Point3f(-1,-1, Float.NEGATIVE_INFINITY), new Point3f(1,1, Float.POSITIVE_INFINITY));
	}

}
