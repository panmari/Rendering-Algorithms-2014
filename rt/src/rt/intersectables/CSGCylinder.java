package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.Ray;

public class CSGCylinder extends CSGSolid {

	private CSGNode root;

	/**
	 * A finite cylinder centered around the z-axis, cut off at height/2 and -height/2
	 * TODO: materials can not be set for the whole cylinder at once
	 * @param center
	 * @param radius
	 * @param height
	 */
	public CSGCylinder(float radius, float height) {
		CSGPlane top = new CSGPlane(new Vector3f(0,0.f,1.f), -height/2);
		CSGPlane bottom = new CSGPlane(new Vector3f(0,0.f,-1.f), -height/2);
		CSGInfiniteCylinder cyl = new CSGInfiniteCylinder(radius);
		CSGNode n1 = new CSGNode(top, cyl, CSGNode.OperationType.INTERSECT);
		root = new CSGNode(n1, bottom, CSGNode.OperationType.INTERSECT);
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		return root.getIntervalBoundaries(r);
	}
}
