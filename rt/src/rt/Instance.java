package rt;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Instance implements Intersectable {

	private Matrix4f t;
	private Matrix4f tinverse;
	private Intersectable intersectable;

	public Instance(Intersectable i, Matrix4f t) {
		this.intersectable = i;
		this.t = t;
		this.tinverse = new Matrix4f(t);
		this.tinverse.invert();
	}

	@Override
	public HitRecord intersect(Ray r) {
		Point3f instanceOrigin = new Point3f(r.origin);
		Vector3f instanceDir = new Vector3f(r.direction);
		t.transform(instanceOrigin);
		t.transform(instanceDir);
		
		Ray instanceRay = new Ray(instanceOrigin, instanceDir);
		HitRecord instanceHitRecord = intersectable.intersect(instanceRay);
		if (instanceHitRecord == null)
			return null;
		return transformBack(instanceHitRecord);
	}
	
	private HitRecord transformBack(HitRecord h) {
		Point3f tPosition = new Point3f(h.position);
		tinverse.transform(tPosition);
		Vector3f tNormal = new Vector3f(h.normal);
		tinverse.transform(tNormal);
		Vector3f tW = new Vector3f(h.w);
		tinverse.transform(tW);
		return new HitRecord(h.t,tPosition, tNormal, tW, h.intersectable, h.material, h.u, h.v);
	}
}
