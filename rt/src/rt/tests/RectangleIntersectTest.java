package rt.tests;

import static org.junit.Assert.*;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.junit.Before;
import org.junit.Test;

import rt.HitRecord;
import rt.Ray;
import rt.intersectables.Rectangle;

public class RectangleIntersectTest {

	private Rectangle rect;
	private Rectangle rectUp;

	@Before
	public void setUp() {
		rect = new Rectangle(new Point3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0));
		rectUp = new Rectangle(new Point3f(1,1,0), new Vector3f(1,0,0), new Vector3f(0,1,0));
	}
	
	@Test
	public void rectangleShouldIntersectCorrectly() {
		Ray r = new Ray(new Point3f(.5f, .5f, 5), new Vector3f(0,0,-1));
		HitRecord h = rect.intersect(r);
		assertEquals(new Point3f(.5f, .5f, 0), h.position);
	}
	
	@Test
	public void rectangleShouldNotIntersectRayIntoOppositeDirection() {
		Ray r = new Ray(new Point3f(.5f, .5f, 5), new Vector3f(0,0, 1));
		HitRecord h = rect.intersect(r);
		assertNull(h);
	}
	
	@Test
	public void rectangleNotInOriginShouldIntersectCorrectly() {
		Ray r = new Ray(new Point3f(1f, 1f, 1), new Vector3f(0,0, -1));
		HitRecord h = rectUp.intersect(r);
		assertEquals(new Point3f(1,1,0), h.position);
	}

	@Test
	public void rectangleNotInOriginShouldNotIntersectCorrectly() {
		Ray r = new Ray(new Point3f(1.5f, 1.5f, 1), new Vector3f(0,-.1f, -1f));
		HitRecord h = rectUp.intersect(r);
		assertNotNull(h);
		assertEquals(new Point3f(1.5f, 1.4f,0), h.position);
	}

}
