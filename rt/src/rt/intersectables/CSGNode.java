package rt.intersectables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.vecmath.Point3f;

import rt.Ray;
import rt.accelerators.BoundingBox;
import util.StaticVecmath;

/**
 * A CSG node combines two CSG solids using a set operation, such as intersection,
 * addition, or subtraction.
 */
public class CSGNode extends CSGSolid {
	
	public enum OperationType { INTERSECT, ADD, SUBTRACT };
	
	protected CSGSolid left, right;
	protected OperationType operation;
	private BoundingBox boundingBox;
	
	public CSGNode(CSGSolid left, CSGSolid right, OperationType operation)
	{
		this.left = left;
		this.right = right;
		this.operation = operation;
		Point3f computedMin = new Point3f(left.getBoundingBox().min);
		StaticVecmath.elementwiseMin(computedMin, right.getBoundingBox().min);
		Point3f computedMax = new Point3f(left.getBoundingBox().max);
		StaticVecmath.elementwiseMin(computedMax, right.getBoundingBox().max);
		this.boundingBox = new BoundingBox(computedMin, computedMax);
	}

	/**
	 * Get boundaries of intersection intervals. The main idea is to first get the boundaries
	 * of the two CSG solids to be combined. Then, the boundaries are merged according
	 * to the set operation specified by the node.
	 */
	public ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r)
	{
		ArrayList<IntervalBoundary> combined = new ArrayList<>();
		
		// Get interval boundaries of left and right children
		ArrayList<IntervalBoundary> leftIntervals = left.getIntervalBoundaries(r);
		ArrayList<IntervalBoundary> rightIntervals = right.getIntervalBoundaries(r);
		
		// Tag interval boundaries with left or right node
		Iterator<IntervalBoundary> it = leftIntervals.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			b.belongsTo = BelongsTo.LEFT;
		}
		it = rightIntervals.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			b.belongsTo = BelongsTo.RIGHT;			
		}

		// Combine interval boundaries and sort
		combined.addAll(leftIntervals);
		combined.addAll(rightIntervals);
		Collections.sort(combined);

		// Traverse interval boundaries and set inside/outside 
		// according to Boolean set operation to combine the two child solids
		boolean inLeft, inRight;
		inLeft = false;
		inRight = false;
		
		it = combined.iterator();
		while(it.hasNext())
		{
			IntervalBoundary b = it.next();
			
			if(b.belongsTo == BelongsTo.LEFT)
			{
				if(b.type == BoundaryType.START)
					inLeft = true;
				else
					inLeft = false;
			}
			if(b.belongsTo == BelongsTo.RIGHT)
			{
				if(b.type == BoundaryType.START)
					inRight= true;
				else
					inRight = false;
			}

			switch(operation) 
			{
				case INTERSECT:
				{		
					if(inLeft && inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					break;
				}
				case SUBTRACT:
				{					
					if(inLeft && !inRight)
						b.type = BoundaryType.START;
					else
						b.type = BoundaryType.END;
					
					// In a subtract operation, the subtracted solid is turned inside out, 
					// or it "switches sign", so we need to flip its normal direction
					if(b.belongsTo == BelongsTo.RIGHT && b.hitRecord!=null)
					{
						b.hitRecord.normal.negate();
					}
					break;
				}
				case ADD:
				{
					if(inLeft || inRight)
						b.type = BoundaryType.START; 
					else
						b.type = BoundaryType.END;
					break;
				}
			}
		}
		
		// Clean up
		it = combined.iterator();		
		IntervalBoundary prev = new IntervalBoundary();
		prev.type = BoundaryType.END;				
		IntervalBoundary b;	
		while(it.hasNext())
		{
			b = it.next();
			if(b.type == prev.type)
				it.remove();
			prev.type = b.type;						
		}

		return combined;
	}

	public String toString() {
		return "Node, L: " + this.left + " R: " + this.right;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
}
