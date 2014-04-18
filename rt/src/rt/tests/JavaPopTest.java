package rt.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

public class JavaPopTest {

	final static int NUMBER_OF_APPENDS = 100000000;
	
	/**
	 * This is faster
	 */
	@Test
	public void LinkedListTest() {
		LinkedList<Float> l = new LinkedList<>();
		for(int i = 0; i < NUMBER_OF_APPENDS; i++) {
			l.addLast(55f);
			if (l.size() > 10) {
				float f = l.pop();
			}
		}
	}
	
	/**
	 * This is slower for a large number of appends/pops
	 */
	@Test
	public void ArrayListTest() {
		ArrayList<Float> l = new ArrayList<>(10);
		for(int i = 0; i < NUMBER_OF_APPENDS; i++) {
			l.add(55f);
			if (l.size() > 10) {
				float f = l.remove(0);
			}
		}
	}

}
