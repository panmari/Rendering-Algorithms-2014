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
import rt.StaticVecmath;
import rt.materials.Diffuse;

public class CSGCylinder extends CSGSolid {

	private Point3f center;
	private float height;
	private float radius;
	private Diffuse material;

	public CSGCylinder(Point3f center, float height, float radius) {
		this.center = center;
		this.height = height;
		this.radius = radius;
		this.material = new Diffuse(new Spectrum(5,5,5));
	}
	
	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		Vector2f c = new Vector2f(center.x, center.y);
		Vector2f d = new Vector2f(r.direction.x, r.direction.y);
		Vector2f e = new Vector2f(r.origin.x, r.origin.y);

		float a = d.lengthSquared();
		Vector2f originCenter = new Vector2f();
		originCenter.sub(e, c);
		float b = 2*d.dot(originCenter);
		float cf = originCenter.lengthSquared() - radius*radius;
		Point2f t = MyMath.solveQuadratic(a, b, cf);

		return null;
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
