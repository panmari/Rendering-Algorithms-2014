package rt.tests;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import rt.intersectables.CSG.IntervalBoundary;
import rt.intersectables.CSG.CSGSolid.BelongsTo;
import rt.intersectables.CSG.CSGSolid.BoundaryType;

public class IntervallBenchmark {

	public static final int N = 1000000;
	public ArrayList<IntervalBoundary> ibs;
	@Before
	public void setUp() {
		ibs = new ArrayList<>(N);
		for (int i = 0; i < N; i++) {
			ibs.add(new IntervalBoundary((float)Math.random(), null, null, null));
		}
	}
	@Test
	public void test() {
		Collections.sort(ibs);
	}

}
