package rt.intersectables;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;
import util.MyMath;

public class Sphere implements Intersectable {

	
	private Point3f center;
	private float radius;
	private Material material;
	
	
	/**
	 * Creates a sphere
	 * @param center, a point inn world coordinates
	 * @param radius of resulting sphere
	 */
	public Sphere(Point3f center, float radius, Material m) {
		this.center = center;
		this.radius = radius;
		this.material = m;
	}
	
	/**
	 * Creates canonical sphere (at origin 0,0,0 with radius 1
	 * @param center, a point inn world coordinates
	 * @param radius of resulting sphere
	 */
	public Sphere() {
		this(new Point3f(), 1, new Diffuse(new Spectrum(1.f, 1.f, 1.f)));
	}

	public Sphere(Material m) {
		this(new Point3f(), 1, m);
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
		
		float u = 0.5f + (float)(Math.atan2(hitPoint.z, hitPoint.x)/(2*Math.PI));
		float v = 0.5f - (float)(Math.asin(hitPoint.y)/Math.PI);

		return new HitRecord(t, hitPoint, normal, wIn, this, this.material, u, v);
	}

	/**
	 * Very simple for a sphere with radius 1.
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(new Point3f(-1,-1,-1), new Point3f(1,1,1));
	}

	@Override
	public HitRecord intersect(Ray r) {
		float a = r.direction.lengthSquared();
		Vector3f originCenter = new Vector3f();
		originCenter.sub(r.origin, center);
		float b = 2*r.direction.dot(originCenter);
		float c = originCenter.lengthSquared() - radius*radius;
		Point2f t = MyMath.solveQuadratic(a, b, c);
		
		if(t == null) 	
			return null;
		if (t.x > 0)
			return makeHitRecord(t.x, r);
		if (t.y > 0)
			return makeHitRecord(t.y, r);
		else return null;
	}
}
