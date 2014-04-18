package rt.accelerators;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import util.StaticVecmath;

public class BoundingBox implements Intersectable {

	final public Point3f min;
	final public Point3f max;
	final public Point3f[] bounds;
	float area;
	
	public BoundingBox(Point3f bottomLeft, Point3f topRight) {
		this.min = bottomLeft;
		this.max = topRight;
		this.bounds = new Point3f[]{bottomLeft, topRight};
		Vector3f diagonal = getDiagonal();
		this.area = 2*(diagonal.x*diagonal.y + diagonal.x * diagonal.z + diagonal.y * diagonal.z);
	}
	
	/**
	 * See
	 * http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-7-intersecting-simple-shapes/ray-box-intersection/
	 * @return a point2f with the two t values of the intersections, the smaller one on x. Null if there is no intersection.
	 */
	public Point2f intersectBB(Ray r) {
		Vector3f invdir = new Vector3f(1/r.direction.x,
				1/r.direction.y,
				1/r.direction.z);
		int[] sign = new int[3];
		sign[0] = (invdir.x < 0) ? 1 : 0;
        sign[1] = (invdir.y < 0) ? 1 : 0;
        sign[2] = (invdir.z < 0) ? 1 : 0;
        
        float tmin, tmax, tymin, tymax, tzmin, tzmax;
        tmin = (bounds[sign[0]].x - r.origin.x) * invdir.x;
        tmax = (bounds[1-sign[0]].x - r.origin.x) * invdir.x;
        tymin = (bounds[sign[1]].y - r.origin.y) * invdir.y;
        tymax = (bounds[1-sign[1]].y - r.origin.y) * invdir.y;
        if ((tmin > tymax) || (tymin > tmax))
            return null;
        if (tymin > tmin)
            tmin = tymin;
        if (tymax < tmax)
            tmax = tymax;
        tzmin = (bounds[sign[2]].z - r.origin.z) * invdir.z;
        tzmax = (bounds[1-sign[2]].z - r.origin.z) * invdir.z;
        if ((tmin > tzmax) || (tzmin > tmax))
            return null;
        
        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;
		return new Point2f(tmin, tmax);
	}
	
	/**
	 * see http://rbrundritt.wordpress.com/2009/10/03/determining-if-two-bounding-boxes-overlap/
	 * @param other
	 * @return
	 */
	public boolean isOverlapping(BoundingBox other) {
		Vector3f sizes = new Vector3f();
		sizes.add(this.getDiagonal(), other.getDiagonal());
		Vector3f distCenters2 = new Vector3f();
		distCenters2.sub(this.getCenter(), other.getCenter());
		distCenters2.scale(2);
		distCenters2.absolute();	
		return distCenters2.x <= sizes.x && distCenters2.y <= sizes.y && distCenters2.z <= sizes.z;
	}
	
	public String toString() {
		return "min: " + this.min + ", max: " + this.max;
	}

	/**
	 * Careful, this has no useful normal and/or texture coordinates.
	 */
	@Override
	public HitRecord intersect(Ray r) {
		float t = intersectBB(r).x;
		return new HitRecord(t, r.pointAt(t), new Vector3f(1,0,0), StaticVecmath.negate(r.direction), this, null, 0, 0);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return this;
	}
	
	public Point3f getCenter(){
		Point3f center = new Point3f();
		center.add(min, max);
		center.scale(0.5f);
		return center;
	}
	
	/**
	 * Can be used to get component wise size of bounding box
	 * @return
	 */
	public Vector3f getDiagonal() {
		Vector3f diagonal = new Vector3f();
		diagonal.sub(max, min);
		return diagonal;
	}
}
