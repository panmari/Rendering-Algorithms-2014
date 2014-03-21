package rt.accelerators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Point3f;

import com.google.common.collect.Lists;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.intersectables.Aggregate;
import sun.org.mozilla.javascript.internal.Node;

public class BSPAccelerator implements Intersectable {

	private final int MAX_DEPTH;
	private final int MIN_NR_PRIMITIVES = 5; 
	private final int n;
	private final BSPNode root;
	
	/**
	 * The aggregate given is usually a mesh, but may be anything else as defined by the aggregate contract.
	 * @param a
	 */
	public BSPAccelerator(Aggregate a) {
		List<Intersectable> intersectables = new ArrayList<>();
		Iterator<Intersectable> iter = a.iterator();
		this.n = a.size();
		this.MAX_DEPTH = (int) Math.round(8 + 1.3f*Math.log(n));

		BSPNode root = new BSPNode(Lists.newArrayList(a.iterator()), a.getBoundingBox(), Axis.x);
		buildTree(root, 0);
	}
	
	/**
	 * Axis may be 
	 * @param i
	 * @param b
	 * @return
	 */
	private BSPNode buildTree(BSPNode node, int depth) {
		if (depth > MAX_DEPTH || node.intersectables.size() < MIN_NR_PRIMITIVES)
			return null;

		Axis currentSplitAxis = node.splitAxis.getNext();
		BoundingBox b = node.boundingBox;
		// split bounding box in middle along of some axis, make a new box each
		Point3f leftBoxMax = new Point3f(b.max);
		leftBoxMax.x = (b.min.x + b.max.x)/2;
		BoundingBox leftBox = new BoundingBox(b.min, leftBoxMax);
		Point3f rightBoxMin = new Point3f(b.max);
		rightBoxMin.x = (b.min.x + b.max.x)/2;
		BoundingBox rightBox = new BoundingBox(rightBoxMin, b.max);
		
		List<Intersectable> leftIntersectables = new ArrayList<>();
		List<Intersectable> rightIntersectables = new ArrayList<>();
		//add intersectable to bounding box that crosses it
		for (Intersectable i: iList) {
			if (i.getBoundingBox().intersect(leftBox))
				leftIntersectables.add(i);
			if (i.getBoundingBox().intersect(rightBox))
				rightIntersectables.add(i);
		}
		
		node.left = new BSPNode(leftIntersectables, leftBox, currentSplitAxis);
		
		//TODO: 
		// call build tree again with left -> smaller than split
		// right -> higher than split
		// at beginning of method, check for end criteria -> make leaf if met
		
	}
	@Override
	public HitRecord intersect(Ray r) {
		//traverse stupidly everything
		return null;
	}

	enum Axis{
		x, y, z;
		
		public Axis getNext() {
			int ordinalNext = (this.ordinal() + 1) % Axis.values().length;
			return Axis.values()[ordinalNext];
		}
	}
}
