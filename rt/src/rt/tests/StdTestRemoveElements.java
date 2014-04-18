package rt.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import util.StdHelper;

public class StdTestRemoveElements {

	private StdHelper h = new StdHelper(5);
	
	private final static float EPSILON = 1e-4f;
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

	private void addToStdHelper(float newValue) {
		h.update(newValue);
	}
}
