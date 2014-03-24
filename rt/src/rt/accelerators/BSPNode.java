package rt.accelerators;

import java.util.List;

import rt.Intersectable;

public class BSPNode {

	List<Intersectable> intersectables;
	final BoundingBox boundingBox;
	BSPNode left, right;

	public BSPNode(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public String toString() {
		return intersectables.toString();
	}
}
