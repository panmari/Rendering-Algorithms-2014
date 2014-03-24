package rt.accelerators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

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
		this.n = a.size();
		this.MAX_DEPTH = 2;//(int) Math.round(8 + 1.3f*Math.log(n));

		this.root = new BSPNode(a.getBoundingBox());
		buildTree(root, Lists.newArrayList(a.iterator()), Axis.x, 0);
	}
	
	/**
	 * Axis may be 
	 * @param i
	 * @param b
	 * @return
	 */
	private BSPNode buildTree(BSPNode node, List<Intersectable> iList, Axis currentSplitAxis, int depth) {
		if (depth > MAX_DEPTH || iList.size() < MIN_NR_PRIMITIVES) {
			node.intersectables = iList;
			return node;
		}

		BoundingBox b = node.boundingBox;
		// split bounding box in middle along of some axis, make a new box each
		Point3f leftBoxMax = new Point3f(b.max);
		leftBoxMax.x = (b.min.x + b.max.x)/2;
		BoundingBox leftBox = new BoundingBox(new Point3f(b.min), leftBoxMax);
		Point3f rightBoxMin = new Point3f(b.min);
		rightBoxMin.x = (b.min.x + b.max.x)/2;
		BoundingBox rightBox = new BoundingBox(rightBoxMin, new Point3f(b.max));
		
		List<Intersectable> leftIntersectables = new ArrayList<>();
		List<Intersectable> rightIntersectables = new ArrayList<>();
		//add intersectable to bounding box that crosses it
		for (Intersectable i: iList) {
			if (i.getBoundingBox().intersect(leftBox))
				leftIntersectables.add(i);
			if (i.getBoundingBox().intersect(rightBox))
				rightIntersectables.add(i);
		}
		
		node.left = buildTree(new BSPNode(leftBox), leftIntersectables, currentSplitAxis.getNext(), depth + 1);
		node.right = buildTree(new BSPNode(rightBox), rightIntersectables, currentSplitAxis.getNext(), depth + 1);
		return node;
	}
	@Override
	public HitRecord intersect(Ray r) {
		Stack<BSPNode> nodeStack = new Stack<>();
		nodeStack.push(root);
		HitRecord nearestHit = null;
		float nearestT = Float.POSITIVE_INFINITY;
		while (!nodeStack.empty()) {
			BSPNode currentNode = nodeStack.pop();
			if (currentNode.intersectables != null) {
				for (Intersectable i: currentNode.intersectables) {
					HitRecord currentHit = i.intersect(r);
					if (currentHit != null && nearestT > currentHit.t && currentHit.t > 0) {
						nearestT = currentHit.t;
						nearestHit = currentHit;
					}
				}
			}
			//TODO: refactor ugly null checks
			if (currentNode.left != null && currentNode.left.boundingBox.intersect(r) != null) {
				nodeStack.push(currentNode.left);
			} 
			if (currentNode.right != null && currentNode.right.boundingBox.intersect(r) != null) {
				nodeStack.push(currentNode.right);
			} 
		}
		return nearestHit;
	}

	enum Axis{
		x, y, z;
		
		public Axis getNext() {
			int ordinalNext = (this.ordinal() + 1) % Axis.values().length;
			return Axis.values()[ordinalNext];
		}
	}

	@Override
	public BoundingBox getBoundingBox() {
		return root.boundingBox;
	}
}
