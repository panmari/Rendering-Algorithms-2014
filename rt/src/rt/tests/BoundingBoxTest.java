package rt.tests;

import static org.junit.Assert.*;

import javax.vecmath.Point3f;

import org.junit.Test;

import rt.accelerators.BoundingBox;

public class BoundingBoxTest {

	@Test
	public void test() {
		BoundingBox bb1 = new BoundingBox(new Point3f(0,0,0), new Point3f(1,1,1));
		BoundingBox bb2 = new BoundingBox(new Point3f(1,1,1), new Point3f(2,2,2));
		assertTrue(bb1.isOverlapping(bb2));
	}
	
	@Test
	public void test2() {
		BoundingBox bb1 = new BoundingBox(new Point3f(0,0,0), new Point3f(1,1,1));
		BoundingBox bb2 = new BoundingBox(new Point3f(1,2,1), new Point3f(2,3,2));
		assertFalse(bb1.isOverlapping(bb2));
	}
}
