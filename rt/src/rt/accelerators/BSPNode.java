package rt.accelerators;

import java.util.List;

import rt.Intersectable;
import rt.accelerators.BSPAccelerator.Axis;

public class BSPNode {

	public List<Intersectable> intersectables;
	public BoundingBox boundingBox;
	public Axis splitAxis;
	BSPNode left, right;

	public BSPNode(List<Intersectable> intersectable, BoundingBox boundingBox, Axis splitAxis) {
		this.intersectables = intersectables;
		this.boundingBox = boundingBox;
		this.splitAxis = splitAxis;
	}

}
