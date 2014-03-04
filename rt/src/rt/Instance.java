package rt;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Instance implements Intersectable {

	private Matrix4f t;
	private Matrix4f tinverse;
	private Intersectable intersectable;
	private Matrix4f tinverseTanspose;

	public Instance(Intersectable i, Matrix4f t) {
		this.intersectable = i;
		this.t = t;
		this.tinverse = new Matrix4f(t);
		this.tinverse.invert();
		this.tinverseTanspose = new Matrix4f(tinverse);
		this.tinverseTanspose.transpose();
	}

	@Override
	public HitRecord intersect(Ray r) {
		Point3f instanceOrigin = new Point3f(r.origin);
		Vector3f instanceDir = new Vector3f(r.direction);
		tinverse.transform(instanceOrigin);
		tinverse.transform(instanceDir);
		
		Ray instanceRay = new Ray(instanceOrigin, instanceDir);
		HitRecord instanceHitRecord = intersectable.intersect(instanceRay);
		if (instanceHitRecord == null)
			return null;
		return transformBack(instanceHitRecord);
	}
	
	private HitRecord transformBack(HitRecord h) {
		Point3f tPosition = new Point3f(h.position);
		t.transform(tPosition);
		Vector3f tNormal = new Vector3f(h.normal);
		tinverseTanspose.transform(tNormal);
		Vector3f tW = new Vector3f(h.w);
		tinverseTanspose.transform(tW);
		// does the t also need fixing?
		return new HitRecord(h.t, tPosition, tNormal, tW, h.intersectable, h.material, h.u, h.v);
	}
}
