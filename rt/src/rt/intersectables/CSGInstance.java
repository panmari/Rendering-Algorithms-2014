package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import rt.Material;
import rt.Ray;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

public class CSGInstance extends CSGSolid {

	private CSGSolid csgSolid;
	public Material material;
	private InstanceHelper instanceHelper;

	public CSGInstance(CSGSolid i, Matrix4f t) {
		this.csgSolid = i;
		this.instanceHelper = new InstanceHelper(t);
		this.material = new Diffuse(); //default material
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		Ray instanceRay = instanceHelper.transform(r);
		ArrayList<IntervalBoundary> intervalBoundaries = csgSolid.getIntervalBoundaries(instanceRay);
		
		for (IntervalBoundary i: intervalBoundaries) {
			if (i.hitRecord != null) {
				i.hitRecord = instanceHelper.transformBack(i.hitRecord);
				i.hitRecord.material = this.material;
			}
		}
		return intervalBoundaries;
	}
	
	public String toString() {
		return "Instance: " + csgSolid.toString();
	}

	@Override
	public BoundingBox getBoundingBox() {
		//TODO: make something smarter
		return new BoundingBox(new Point3f(Float.NEGATIVE_INFINITY,Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY), 
				new Point3f(Float.POSITIVE_INFINITY,Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY));
	}
}
