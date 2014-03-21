package rt.accelerators;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.intersectables.CSGNode;
import rt.intersectables.CSGPlane;
import rt.materials.Diffuse;

public class BoundingBox implements Intersectable {

	public Point3f min;
	public Point3f max;
	public Point3f[] bounds;
	
	public BoundingBox(Point3f bottomLeft, Point3f topRight) {
		this.min = bottomLeft;
		this.max = topRight;
		this.bounds = new Point3f[]{bottomLeft, topRight};
	}
	
	/**
	 * See
	 * http://www.scratchapixel.com/lessons/3d-basic-lessons/lesson-7-intersecting-simple-shapes/ray-box-intersection/
	 */
	@Override
	public HitRecord intersect(Ray r) {
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
        
        //TODO: possibly just give back a dummy entry
        if (tzmin > tmin)
            tmin = tzmin;
        if (tzmax < tmax)
            tmax = tzmax;
        Vector3f w = new Vector3f(r.direction);
        w.normalize();
        w.negate();
		return new HitRecord(tmin, r.pointAt(tmin), new Vector3f(1,0,0), w, this, new Diffuse(), 0,0);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return this;
	}

}