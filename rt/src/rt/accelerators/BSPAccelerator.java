package rt.accelerators;

import static util.StaticVecmath.getDimension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.intersectables.Aggregate;
import util.IntersectableComparator;
import util.StaticVecmath;
import util.StaticVecmath.Axis;

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
		Iterator<Intersectable> iter = a.iterator();
		List<Intersectable> list = new ArrayList<>(a.size());
		while(iter.hasNext()) {
			Intersectable i = iter.next();
			list.add(i);
		}
		buildTree(root, list, 0);
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
		Collections.sort(iList, new IntersectableComparator(node.splitAxis));
		
		//Idea: sort by split axis, iterate over all intersectables and keep adding up surface areas.
		// cost = surface left * intersectables left + surface right * intersectables right
		
				
		float minCosts = Float.POSITIVE_INFINITY;
		float minCut = 0;
		List<Intersectable> minLeftIntersectables = null, minRightIntersectables = null;
		BoundingBox minLeftBox = null, minRightBox = null;
		for (Intersectable ib: iList) {
			for (Point3f bound: ib.getBoundingBox().bounds) {
				float split = StaticVecmath.getDimension(bound, node.splitAxis);
				Point3f leftBoxMax = new Point3f(b.max);
				Point3f rightBoxMin = new Point3f(b.min);
				StaticVecmath.setDimension(leftBoxMax, split, node.splitAxis);
				StaticVecmath.setDimension(rightBoxMin, split, node.splitAxis);
				BoundingBox leftBox = new BoundingBox(new Point3f(b.min), leftBoxMax);
				BoundingBox rightBox = new BoundingBox(rightBoxMin, new Point3f(b.max));

				List<Intersectable> leftIntersectables = new ArrayList<>(iList.size()/2);
				List<Intersectable> rightIntersectables = new ArrayList<>(iList.size()/2);
				//add intersectable to bounding box that crosses it
				float leftFinalArea = 0, rightFinalArea = 0;
				for (Intersectable i: iList) {
					if (i.getBoundingBox().isOverlapping(leftBox)) {
						leftIntersectables.add(i);
						leftFinalArea += i.getBoundingBox().area;
					}
					if (i.getBoundingBox().isOverlapping(rightBox)) {
						rightIntersectables.add(i);
						rightFinalArea += i.getBoundingBox().area;
					}
				}
				float costs = leftFinalArea*leftIntersectables.size() + rightFinalArea * rightIntersectables.size();
				if (costs < minCosts) {
					minCut = split;
					minCosts = costs;
					minLeftIntersectables = leftIntersectables;
					minRightIntersectables = rightIntersectables;
					minLeftBox = leftBox;
					minRightBox = rightBox;
				}
			}
		}
		Axis nextSplitAxis = node.splitAxis.getNext();
		node.left = buildTree(new BSPNode(minLeftBox, nextSplitAxis), minLeftIntersectables, depth + 1);
		node.right = buildTree(new BSPNode(minRightBox, nextSplitAxis), minRightIntersectables, depth + 1);
		return node;
	}
	
	@Override
	public HitRecord intersect(Ray r) {
		Point2f ts = root.boundingBox.intersectBB(r);
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
						(Math.abs(tSplitAxis) < 1e-5 && first.boundingBox.intersectBB(r) != null)) {
					node = first;
				}
				else if(tSplitAxis < tmin || 
						(Math.abs(tSplitAxis) < 1e-5 && second.boundingBox.intersectBB(r) != null)) {
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
			if (currentNode.left.boundingBox.intersectBB(r) != null)
				nodeStack.push(currentNode.left);
			if (currentNode.right.boundingBox.intersectBB(r) != null) 
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
	
