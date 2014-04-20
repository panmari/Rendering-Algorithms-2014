package rt.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import util.MyMath;
import util.StdHelper;

public class StdTestRemoveElements {

	private StdHelper h;	
	private final static float EPSILON = 1e-4f;
	
	@Before
	public void setUp() {
		h = new StdHelper(5);
	}
	
	@Test
	public void testAppendingStuff() {
		addToStdHelper(1);
		assertEquals(0, h.getStd(), EPSILON);
		addToStdHelper(0);
		assertEquals(0.7071, h.getStd(), EPSILON);
		addToStdHelper(1);
		assertEquals(0.5774, h.getStd(), EPSILON);
		addToStdHelper(2);
		assertEquals(0.8165, h.getStd(), EPSILON);
		addToStdHelper(3);
		assertEquals(1.1402, h.getStd(), EPSILON);
		// fifth element, the first one should be removed now
		addToStdHelper(.5f);
		assertEquals(1.2042, h.getStd(), EPSILON);
		addToStdHelper(.5f);
		assertEquals(1.0840, h.getStd(), EPSILON);
	
	}
	
	@Test
	public void testInvertMethod() {
		assertEquals(0f, MyMath.inv(0), EPSILON);
		assertEquals(1f, MyMath.inv(1), EPSILON);
		assertEquals(.5f, MyMath.inv(2), EPSILON);
		assertEquals(-.5f, MyMath.inv(-2), EPSILON);
	}
	
	@Test
	public void shouldNotReturnStupidNaN() {
		h.update(0, 0);
		assertFalse(Float.isNaN(h.getVar()));
		assertEquals(0f, h.getVar(), EPSILON);
		assertFalse(Float.isNaN(h.getDelta()));
	}

	private void addToStdHelper(float newValue) {
		h.update(newValue, 0);
	}
}
