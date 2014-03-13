package rt.intersectables;

import java.util.ArrayList;
import java.util.Iterator;
import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;

/**
 * A CSG solid object that can be intersected by a ray. If a CSG object is intersected
 * by a ray, we determine all intersection intervals and their boundaries, that is, the intervals
 * along the ray where the ray is either inside or outside the object. Each interval has two 
 * boundaries, a start and an end, where the ray enters and leaves the solid. The actual 
 * intersection point with the object is the first interval boundary where the ray enters the 
 * object the first time.
 */
public abstract class CSGSolid implements Intersectable {

	enum BoundaryType { START, END };
	public enum BelongsTo { LEFT, RIGHT };
	
	/**
	 * Boundary of an intersection interval, can be sorted by t easily.
	 */
	class IntervalBoundary implements Comparable<IntervalBoundary>
	{		
		float t;				// t value of intersection		
		BoundaryType type;		// Type of boundary of intersection interval (start or end)
		HitRecord hitRecord;	// The hit record of the intersection
		BelongsTo belongsTo;
		
		public IntervalBoundary() {
			//whatev, set them manually
		}
		
		public IntervalBoundary(float t, BoundaryType type, HitRecord hitRecord, BelongsTo belongsTo) {
			this.t = t;
			this.type = type;
			this.hitRecord = hitRecord;
			this.belongsTo = belongsTo;
		}
		
		public String toString() {
			return "t: " + t + " type: " + type;
		}
		
		public int compareTo(IntervalBoundary b)
		{
			if(this.t < b.t) 
				return -1; 
			else if(this.t == b.t)
				return 0;
			else
				return 1;
		}
	}
	
	protected BoundaryType findBoundaryType(HitRecord h, Ray r) {
		if (h.normal.dot(r.direction) < 0) //started inside double cone, alternatively h.t < 0
			return BoundaryType.START;
		else return BoundaryType.END;
	}
	
	public HitRecord intersect(Ray r) {

		// Get the intersection interval boundaries
		ArrayList<IntervalBoundary> intervalBoundaries = getIntervalBoundaries(r);
		
		// Return the first hit in front of the camera, that is, make sure 
		// the hit is along the positive ray direction
		Iterator<IntervalBoundary> it = intervalBoundaries.iterator();
		while(it.hasNext())
		{
			HitRecord firstHit = it.next().hitRecord;
			
			if(firstHit!=null && firstHit.t>0.f)
			{		
				firstHit.intersectable = this;
				return firstHit;			
			}
		}
				
		return null;
	}
		
	/**
	 * Compute the boundaries of the intersection intervals of this CSG solid with a ray. 
	 * 
	 * SM: Should probably return an empty list if the ray doesnt intersect the object
	 * 
	 * @param r the ray that intersects the CSG solid
	 * @return boundaries of intersection intervals 
	 */
	abstract ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r);
}
