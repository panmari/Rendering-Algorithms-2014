package rt.intersectables;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Ray;

public class InstanceHelper {

	private Matrix4f t;
	private Matrix4f tinverse;
	private Matrix4f tinverseTanspose;

	public InstanceHelper(Matrix4f t) {
		this.t = t;
		this.tinverse = new Matrix4f(t);
		this.tinverse.invert();
		this.tinverseTanspose = new Matrix4f(tinverse);
		this.tinverseTanspose.transpose();
	}
	
	public Ray transform(Ray r) {
		Point3f instanceOrigin = new Point3f(r.origin);
		Vector3f instanceDir = new Vector3f(r.direction);
		tinverse.transform(instanceOrigin);
		tinverse.transform(instanceDir);
		return new Ray(instanceOrigin, instanceDir);
	}
	
	public HitRecord transformBack(HitRecord h) {
		Point3f tPosition = new Point3f(h.position);
		t.transform(tPosition);
		Vector3f tNormal = new Vector3f(h.normal);
		tinverseTanspose.transform(tNormal);
		//normalize again, bc may contain scaling
		tNormal.normalize();
		
		// only t is used for incoming light direction
		Vector3f tW = new Vector3f(h.w);
		t.transform(tW);
		tW.normalize();
		// does the t also need fixing?
		return new HitRecord(h.t, tPosition, tNormal, tW, h.intersectable, h.material, h.u, h.v);
	}

}
