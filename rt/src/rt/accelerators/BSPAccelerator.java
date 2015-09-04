package rt.accelerators;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import com.google.common.collect.Lists;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.intersectables.Aggregate;
import util.StaticVecmath;
import util.StaticVecmath.Axis;
import static util.StaticVecmath.getDimension;

public class BSPAccelerator implements Intersectable {

	private final int MAX_DEPTH;
	private final int MIN_NR_PRIMITIVES = 5;
	private final int NR_SPLIT_TRIES;
	private final int n;
	private final BSPNode root;
	private final static float COST_INTERIOR = 1.f;
	private final static float COST_LEAF = 1.f;
	private final static float COST_INTERSECT = 8.f;

	/**
	 * Using a default of only 1 split try (per axis), this is fastest for constructing the acceleration structure. 
	 * @param a
	 */
	public BSPAccelerator(Aggregate a) {
		this(a, 1);
	}
	
	/**
	 * The aggregate given is usually a mesh, but may be anything else as
	 * defined by the aggregate contract.
	 * 
	 * @param a
	 * @param nrSplitTriesPerAxis
	 */
	public BSPAccelerator(Aggregate a, int nrSplitTriesPerAxis) {
		if (nrSplitTriesPerAxis > 15) {
			throw new IllegalArgumentException("This would take ages, please lower the number of split tries to lower than 15.");
		}
		this.n = a.size();
		this.NR_SPLIT_TRIES = nrSplitTriesPerAxis;
		this.MAX_DEPTH = (int) Math.round(8 + 1.3f * Math.log(n));

		this.root = new BSPNode(a.getBoundingBox(), Axis.x);
		buildTree(root, Lists.newArrayList(a.iterator()), 0);

	}

	/**
	 * Axis may be
	 * 
	 * @param i
	 * @param b
	 * @return
	 */
	private BSPNode buildTree(BSPNode node, List<Intersectable> iList, int depth) {
		if (depth > MAX_DEPTH || iList.size() < MIN_NR_PRIMITIVES) {
			return makeLeaf(node, iList);
		}

		float minCosts = Float.POSITIVE_INFINITY;
		List<Intersectable> minLeftIntersectables = null, minRightIntersectables = null;
		BoundingBox minLeftBox = null, minRightBox = null;
		Axis minAxis = null;
		float minSplitDist = 0;
		BoundingBox b = node.boundingBox;
		Point3f center = b.getCenter();
		for (Axis axis : StaticVecmath.Axis.values()) {
			// split bounding box in middle along of some axis, make a new box each
			float centerSplit = StaticVecmath.getDimension(center, axis);
			float stepSize = StaticVecmath.getDimension(b.getDiagonal(), axis)/(NR_SPLIT_TRIES + 1);
			for (int step = -NR_SPLIT_TRIES; step <= NR_SPLIT_TRIES; step++) {
				Point3f leftBoxMax = new Point3f(b.max);
				Point3f rightBoxMin = new Point3f(b.min);
	
				float splitDist = centerSplit + stepSize*step;
				StaticVecmath.setDimension(leftBoxMax, axis, splitDist);
				StaticVecmath.setDimension(rightBoxMin, axis, splitDist);
	
				BoundingBox leftBox = new BoundingBox(new Point3f(b.min),
						leftBoxMax);
				BoundingBox rightBox = new BoundingBox(rightBoxMin, new Point3f(
						b.max));
	
				List<Intersectable> leftIntersectables = new ArrayList<>(
						iList.size() / 2);
				List<Intersectable> rightIntersectables = new ArrayList<>(
						iList.size() / 2);
				// add intersectable to bounding box that crosses it
				for (Intersectable i : iList) {
					if (i.getBoundingBox().isOverlapping(leftBox)) {
						leftIntersectables.add(i);
					}
					if (i.getBoundingBox().isOverlapping(rightBox)) {
						rightIntersectables.add(i);
					}
				}
				float costs = leftBox.area * leftIntersectables.size()
							+ rightBox.area * rightIntersectables.size();
				
				if (costs < minCosts) {
					minCosts = costs;
					minLeftIntersectables = leftIntersectables;
					minRightIntersectables = rightIntersectables;
					minLeftBox = leftBox;
					minRightBox = rightBox;
					minAxis = axis;
					minSplitDist = splitDist;
				}
			}
		}
		float costsSplit = COST_INTERIOR + 2 * COST_LEAF + minCosts * COST_INTERSECT / node.boundingBox.area;
		float costsNoSplit = COST_LEAF + iList.size() * COST_INTERSECT;
		
		if (costsSplit > costsNoSplit) {
			return makeLeaf(node, iList);
		} else {
			node.setSplit(minAxis, minSplitDist);
			node.left = buildTree(new BSPNode(minLeftBox),
					minLeftIntersectables, depth + 1);
			node.right = buildTree(new BSPNode(minRightBox),
					minRightIntersectables, depth + 1);
			return node;
		}
	}
	
	private BSPNode makeLeaf(BSPNode node, List<Intersectable> iList) {
		node.intersectables = iList;
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
				float tSplitAxis = (node.splitAxisDistance - getDimension(
						r.origin, node.splitAxis))
						/ getDimension(r.direction, node.splitAxis);
				BSPNode first, second;

				if (getDimension(r.origin, node.splitAxis) < node.splitAxisDistance) {
					first = node.left;
					second = node.right;
				} else {
					first = node.right;
					second = node.left;
				}
				// process children
				if (tSplitAxis > tmax
						|| tSplitAxis < 0
						|| (Math.abs(tSplitAxis) < 1e-5 && first.boundingBox
								.intersectBB(r) != null)) {
					node = first;
				} else if (tSplitAxis < tmin
						|| (Math.abs(tSplitAxis) < 1e-5 && second.boundingBox
								.intersectBB(r) != null)) {
					node = second;
				} else {
					node = first;
					nodeStack.push(new StackNode(second, tSplitAxis, tmax));
					tmax = tSplitAxis;
				}
			} else {
				for (Intersectable i : node.intersectables) {
					HitRecord tmp = i.intersect(r);
					if (tmp != null && tmp.t < tNearestHit && tmp.t > 0) {
						tNearestHit = tmp.t;
						nearestHit = tmp;
					}
				}
				if (!nodeStack.empty()) {
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
	 * 
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
				for (Intersectable i : currentNode.intersectables) {
					HitRecord currentHit = i.intersect(r);
					if (currentHit != null && nearestT > currentHit.t
							&& currentHit.t > 0) {
						nearestT = currentHit.t;
						nearestHit = currentHit;
					}
				}
			} else {
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
