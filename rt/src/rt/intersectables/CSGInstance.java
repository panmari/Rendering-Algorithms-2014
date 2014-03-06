package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;

public class CSGInstance extends CSGSolid {

	private Matrix4f t;
	private Matrix4f tinverse;
	private CSGSolid csgSolid;
	private Matrix4f tinverseTanspose;
	public Material material;

	public CSGInstance(CSGSolid i, Matrix4f t) {
		this.csgSolid = i;
		this.t = t;
		this.tinverse = new Matrix4f(t);
		this.tinverse.invert();
		this.tinverseTanspose = new Matrix4f(tinverse);
		this.tinverseTanspose.transpose();
	}
	
	private HitRecord transformBack(HitRecord h) {
		Point3f tPosition = new Point3f(h.position);
		t.transform(tPosition);
		Vector3f tNormal = new Vector3f(h.normal);
		tinverseTanspose.transform(tNormal);
		//normalize again, bc may contain scaling
		tNormal.normalize();
		Vector3f tW = new Vector3f(h.w);
		tinverseTanspose.transform(tW);
		tW.normalize();
		// does the t also need fixing?
		return new HitRecord(h.t, tPosition, tNormal, tW, h.intersectable, h.material, h.u, h.v);
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		Point3f instanceOrigin = new Point3f(r.origin);
		Vector3f instanceDir = new Vector3f(r.direction);
		tinverse.transform(instanceOrigin);
		tinverse.transform(instanceDir);
		Ray instanceRay = new Ray(instanceOrigin, instanceDir);
		ArrayList<IntervalBoundary> intervalBoundaries = csgSolid.getIntervalBoundaries(instanceRay);
		
		for (IntervalBoundary i: intervalBoundaries)
			i.hitRecord = transformBack(i.hitRecord);
		return intervalBoundaries;
	}
}
