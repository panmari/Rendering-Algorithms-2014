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

	@Before
	public void setUp() {
		rect = new Rectangle(new Point3f(0,0,0), new Vector3f(1,0,0), new Vector3f(0,1,0));

	}
	
	@Test
	public void rectangleShouldIntersectCorrectly() {
		Ray r = new Ray(new Point3f(.5f, .5f, 5), new Vector3f(0,0,-1));
		HitRecord h = rect.intersect(r);
		assertNotNull(h);
		assertEquals(new Point3f(.5f, .5f, 0), h.position);
	}
	
	@Test
	public void rectangleShouldNotIntersectCorrectly() {
		Ray r = new Ray(new Point3f(.5f, .5f, 5), new Vector3f(0,0, 1));
		HitRecord h = rect.intersect(r);
		assertNull(h);
	}

}
