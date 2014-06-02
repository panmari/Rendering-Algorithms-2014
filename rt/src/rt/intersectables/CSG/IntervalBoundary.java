package rt.intersectables.CSG;

import rt.HitRecord;
import rt.intersectables.CSG.CSGSolid.BelongsTo;
import rt.intersectables.CSG.CSGSolid.BoundaryType;

/**
 * Boundary of an intersection interval, can be sorted by t easily.
 */
public class IntervalBoundary implements Comparable<IntervalBoundary>
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
		return Float.compare(this.t, b.t);
	}
}
