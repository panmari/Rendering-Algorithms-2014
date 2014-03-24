package rt.accelerators;

import java.util.List;

import rt.Intersectable;
import rt.accelerators.BSPAccelerator.Axis;

public class BSPNode {

	List<Intersectable> intersectables;
	final BoundingBox boundingBox;
	BSPNode left, right;
	final Axis splitAxis;
	/**
	 * Distance from origin to split axis plane
	 */
	final float splitAxisDistance;
	
	public BSPNode(BoundingBox boundingBox, Axis splitAxis) {
		this.boundingBox = boundingBox;
		this.splitAxis = splitAxis;
		this.splitAxisDistance = (boundingBox.min.get(splitAxis.ordinal()) +
				boundingBox.max.get(splitAxis.ordinal()))/2;
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
