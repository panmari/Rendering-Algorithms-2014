package rt.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import javax.vecmath.Vector3f;

import org.junit.Test;

import com.google.common.collect.Lists;

import util.TupleComparator;
import util.StaticVecmath.Axis;

public class TupleComparatorTest {

	@Test
	public void testSortingOfThreeVectors() {
		TupleComparator t = new TupleComparator(Axis.x);
		Vector3f v1 = new Vector3f(1, 1,   1);
		Vector3f v2 = new Vector3f(1.5f, 0,2);
		Vector3f v3 = new Vector3f(4,3,    0.5f);
		ArrayList<Vector3f> l = Lists.newArrayList(v1, v2, v3);
		Collections.sort(l,t);
		
		assertEquals(v1, l.get(0));
		assertEquals(v2, l.get(1));
		assertEquals(v3, l.get(2));

		t = new TupleComparator(Axis.y);
		Collections.sort(l,t);
		
		assertEquals(v2, l.get(0));
		assertEquals(v1, l.get(1));
		assertEquals(v3, l.get(2));
		
		t = new TupleComparator(Axis.z);
		Collections.sort(l,t);
		
		assertEquals(v3, l.get(0));
		assertEquals(v1, l.get(1));
		assertEquals(v2, l.get(2));
	}

}
