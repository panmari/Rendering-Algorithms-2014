package rt.intersectables;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import util.StaticVecmath;

public class Rectangle extends Plane implements Intersectable {

	private Point3f position;
	//normalized edges
	private Vector3f edge1;
	private Vector3f edge2;
	//length of edges before
	private float edge1LengthSquare;
	private float edge2LengthSquare;

	/**
	 * Constructs a rectangle given one point and two edge vectors.
	 * @param position
	 * @param edge1
	 * @param edge2
	 */
	public Rectangle(Point3f position, Vector3f edge1, Vector3f edge2) {
		super(makeNormal(edge1, edge2), makeDistance(position, edge1, edge2));
		this.position = position;
		this.edge1 = edge1;
		this.edge2 = edge2;
		this.edge1LengthSquare = edge1.lengthSquared();
		this.edge2LengthSquare = edge2.lengthSquared();
	}
	
	/**
	 * Constructs a rectangle given three points 
	 * @param first
	 * @param second
	 * @param third
	 */
	public Rectangle(Point3f first, Point3f second, Point3f third) {
		this(first, StaticVecmath.sub(third, first), StaticVecmath.sub(second, first));
	}

	private static Vector3f makeNormal(Vector3f edge1, Vector3f edge2) {
		Vector3f normal = new Vector3f();
		normal.cross(edge1, edge2);
		normal.normalize();
		return normal;
	}
	

	private static float makeDistance(Point3f position, Vector3f edge1, Vector3f edge2) {
		Vector3f normal = makeNormal(edge1, edge2);
		Vector3f a = new Vector3f(position);
		float t = -normal.dot(a);
		return t;
	}
	
	@Override
	public HitRecord intersect(Ray r) {
		HitRecord h = super.intersect(r);
		// first case: did not even hit plane
		if (h == null) 
			return null;
		Vector3f d = StaticVecmath.sub(h.position, this.position);
		float projectionEdge1 = d.dot(edge1);
		float projectionEdge2 = d.dot(edge2);
		if (projectionEdge1 >= 0 && projectionEdge1 <= edge1LengthSquare &&
				projectionEdge2 >= 0 && projectionEdge2 <= edge2LengthSquare) {
			h.u = projectionEdge1/edge1LengthSquare;
			h.v = projectionEdge2/edge2LengthSquare;
			return h;
		} else
			return null;
	}

}
