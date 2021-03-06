package rt.accelerators;

import java.util.List;

import rt.Intersectable;
import util.StaticVecmath;
import util.StaticVecmath.Axis;
import static util.StaticVecmath.getDimension;

public class BSPNode {

	List<Intersectable> intersectables;
	final BoundingBox boundingBox;
	BSPNode left, right;
	Axis splitAxis;
	/**
	 * Distance from origin to split axis plane
	 */
	float splitAxisDistance;
	
	public BSPNode(BoundingBox boundingBox, Axis splitAxis) {
		this.boundingBox = boundingBox;
		setSplit(splitAxis, StaticVecmath.getDimension(boundingBox.getCenter(), splitAxis));
	}
	
	/**
	 * Initialized without split axis, needs to be set afterwards!
	 * @param boundingBox
	 */
	public BSPNode(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}
	
	public void setSplit(Axis newAxis, float splitAxisDist) {
		this.splitAxis = newAxis;
		this.splitAxisDistance = splitAxisDist;
	}

	public String toString() {
		if (isLeaf())
			return intersectables.toString();
		else return "" + splitAxis + ", dist: " + splitAxisDistance;
	}
	
	public boolean isLeaf() {
		return intersectables != null;
	}
}
