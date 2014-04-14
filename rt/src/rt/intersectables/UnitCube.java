package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.accelerators.BoundingBox;

public class UnitCube implements Intersectable {

	ArrayList<Rectangle> sides = new ArrayList<Rectangle>();

	/**
	 * A cube from the point (-1,-1,-1) to the point (1,1,1), consisting of 6 rectangles.
	 */
	public UnitCube() {
		float size = 2;
		sides.add(new Rectangle(new Point3f(1,1,1), new Vector3f(0,0,-size), new Vector3f(-size,0,0)));
		sides.add(new Rectangle(new Point3f(1,1,1), new Vector3f(0,-size,0), new Vector3f(0,0,-size)));
		sides.add(new Rectangle(new Point3f(1,1,1), new Vector3f(-size,0, 0), new Vector3f(0,-size,0)));
		
		sides.add(new Rectangle(new Point3f(-1,-1,-1), new Vector3f(size,0,0), new Vector3f(0,0,size)));
		sides.add(new Rectangle(new Point3f(-1,-1,-1), new Vector3f(0,0,size), new Vector3f(0,size,0)));
		sides.add(new Rectangle(new Point3f(-1,-1,-1), new Vector3f(0,size,0), new Vector3f(size,0, 0)));
	}
	
	@Override
	public HitRecord intersect(Ray r) {
		HitRecord h = null;
		float t = Float.POSITIVE_INFINITY;
		for (Rectangle side: sides){
			HitRecord currentHit = side.intersect(r);
			if (currentHit != null && currentHit.t < t) {
				t = currentHit.t;
				h = currentHit;
			}
		}
		return h;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(new Point3f(-1,-1,-1), new Point3f(1,1,1));
	}

}
