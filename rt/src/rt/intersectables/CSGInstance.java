package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Matrix4f;

import rt.Material;
import rt.Ray;
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
			if (i.hitRecord != null)
				i.hitRecord = instanceHelper.transformBack(i.hitRecord);
		}
		return intervalBoundaries;
	}
	
	public String toString() {
		return "Instance: " + csgSolid.toString();
	}
}
