package rt.accelerators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

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
			if (i.getBoundingBox().intersect(leftBox))
				leftIntersectables.add(i);
			if (i.getBoundingBox().intersect(rightBox))
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
				Vector3f splitAxisNormal = node.splitAxis.normal;
				float tmp = splitAxisNormal.dot(r.direction);
				float tSplitAxis;
				//if (tmp != 0) //TODO: handle this case
					tSplitAxis = -(splitAxisNormal.dot(r.origin) + node.splitAxisDistance) / tmp;
				BSPNode first, second;
				if( r.origin.get(node.splitAxis.ordinal()) < node.splitAxisDistance ) {
					first = node.left;
					second = node.right;
				} else {
					first = node.left;
					second = node.right;
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
				StackNode s = nodeStack.pop();
				node = s.node;
				tmin = s.tmin;
				tmax = s.tmax;
			}
		}
		return nearestHit;
	}
	
	enum Axis{
		x(new Vector3f(1, 0, 0)), 
		y(new Vector3f(0, 1, 0)), 
		z(new Vector3f(0, 0, 1));
		
		private final Vector3f normal;

		Axis(Vector3f normal){
			this.normal = normal;
		}
		
		public Axis getNext() {
			int ordinalNext = (this.ordinal() + 1) % Axis.values().length;
			return Axis.values()[ordinalNext];
		}
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
	
