package rt.accelerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import com.google.common.collect.Lists;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.StaticVecmath.Axis;
import rt.intersectables.Aggregate;
import sun.org.mozilla.javascript.internal.Node;
import static rt.StaticVecmath.getDimension;

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
		this.MAX_DEPTH = (int) Math.round(8 + 1.3f*Math.log(n));

		this.root = new BSPNode(a.getBoundingBox(), Axis.x);
		buildTree(root, Lists.newArrayList(a.iterator()), 0);
	}
	
	/**
	 * Axis may be 
	 * @param i
	 * @param b
	 * @return
	 */
	private BSPNode buildTree(BSPNode node, List<Intersectable> iList, int depth) {
		if (depth > MAX_DEPTH || iList.size() < MIN_NR_PRIMITIVES) {
			node.intersectables = iList;
			return node;
		}

		BoundingBox b = node.boundingBox;
		// split bounding box in middle along of some axis, make a new box each
		Point3f leftBoxMax = new Point3f(b.max);
		Point3f rightBoxMin = new Point3f(b.min);

		switch (node.splitAxis) {
			case x:
				leftBoxMax.x = (b.min.x + b.max.x)/2;
				rightBoxMin.x = (b.min.x + b.max.x)/2;
				break;
			case y:
				leftBoxMax.y = (b.min.y + b.max.y)/2;
				rightBoxMin.y = (b.min.y + b.max.y)/2;
				break;
			case z:
				leftBoxMax.z = (b.min.z + b.max.z)/2;
				rightBoxMin.z = (b.min.z + b.max.z)/2;
				break;
		}
		
		BoundingBox leftBox = new BoundingBox(new Point3f(b.min), leftBoxMax);
		BoundingBox rightBox = new BoundingBox(rightBoxMin, new Point3f(b.max));
		
		List<Intersectable> leftIntersectables = new ArrayList<>();
		List<Intersectable> rightIntersectables = new ArrayList<>();
		//add intersectable to bounding box that crosses it
		for (Intersectable i: iList) {
			if (i.getBoundingBox().isOverlapping(leftBox))
				leftIntersectables.add(i);
			if (i.getBoundingBox().isOverlapping(rightBox))
				rightIntersectables.add(i);
		}
		
		Axis nextSplitAxis = node.splitAxis.getNext();
		node.left = buildTree(new BSPNode(leftBox, nextSplitAxis), leftIntersectables, depth + 1);
		node.right = buildTree(new BSPNode(rightBox, nextSplitAxis), rightIntersectables, depth + 1);
		return node;
	}
	
	@Override
	public HitRecord intersect(Ray r) {
		Point2f ts = root.boundingBox.intersect(r);
		if (ts == null)
			return null;
		
		Stack<StackNode> nodeStack = new Stack<>();
		BSPNode node = root;
		HitRecord nearestHit = null;
		float tNearestHit = Float.POSITIVE_INFINITY;	
		float tmin = ts.x, tmax = ts.y;
		while (node != null) {
			if (tNearestHit < tmin)
				break;
			if (!node.isLeaf()) {
				float tSplitAxis = (node.splitAxisDistance - getDimension(r.origin, node.splitAxis))/
										getDimension(r.direction, node.splitAxis);
				BSPNode first, second;

				if (getDimension(r.origin, node.splitAxis) < node.splitAxisDistance ) {
					first = node.left;
					second = node.right;
				} else {
					first = node.right;
					second = node.left;
				}
				// process children
				if( tSplitAxis > tmax || tSplitAxis < 0 || 
						(Math.abs(tSplitAxis) < 1e-5 && first.boundingBox.intersect(r) != null)) {
					node = first;
				}
				else if(tSplitAxis < tmin || 
						(Math.abs(tSplitAxis) < 1e-5 && second.boundingBox.intersect(r) != null)) {
					node = second;
				} else {
					node = first;
					nodeStack.push(new StackNode(second, tSplitAxis, tmax));
					tmax = tSplitAxis;
				}
			} else {
				for (Intersectable i: node.intersectables) {
					HitRecord tmp = i.intersect(r);
					if(tmp != null && tmp.t < tNearestHit && tmp.t > 0) {
						tNearestHit = tmp.t;
						nearestHit = tmp;
					}
				}
				if(!nodeStack.empty()) {
					StackNode s = nodeStack.pop();
					node = s.node;
					tmin = s.tmin;
					tmax = s.tmax;
				} else
					break;
			}
		}
		return nearestHit;
	}
	
	/**
	 * Not in use, slower than code above...
	 * @return
	 */
	public HitRecord primitiveIntersect(Ray r) {
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
			else {
			if (currentNode.left.boundingBox.intersect(r) != null)
				nodeStack.push(currentNode.left);
			if (currentNode.right.boundingBox.intersect(r) != null) 
				nodeStack.push(currentNode.right);
			}
		}
		return nearestHit;
	}
	
	class StackNode {
		final float tmin, tmax;
		final BSPNode node;
		StackNode(BSPNode node, float tmin, float tmax) {
			this.node = node;
			this.tmin = tmin;
			this.tmax = tmax;
		}
	}


	@Override
	public BoundingBox getBoundingBox() {
		return root.boundingBox;
	}
}
	
