package util;

import java.util.Comparator;

import javax.vecmath.Tuple3f;

import util.StaticVecmath.Axis;

public class TupleComparator implements Comparator<Tuple3f> {

	private Axis compareAxis;

	public TupleComparator(Axis compareAxis) {
		this.compareAxis = compareAxis;
	}
	
	@Override
	public int compare(Tuple3f o1, Tuple3f o2) {
		float f1 = StaticVecmath.getDimension(o1, compareAxis);
		float f2 = StaticVecmath.getDimension(o2, compareAxis);
		return f1 < f2 ? -1
		     : f1 > f2 ? 1
		     : 0;
	}

}
