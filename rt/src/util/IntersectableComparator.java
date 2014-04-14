package util;

import java.util.Comparator;

import javax.vecmath.Tuple3f;

import rt.Intersectable;
import util.StaticVecmath.Axis;

/**
 * Compares the minimum of the bounding box of this intersectable (by axis)
 *
 */
public class IntersectableComparator implements Comparator<Intersectable> {

	private TupleComparator tupleComparator;

	public IntersectableComparator(Axis compareAxis) {
		this.tupleComparator = new TupleComparator(compareAxis);
	}

	@Override
	public int compare(Intersectable o1, Intersectable o2) {
		return tupleComparator.compare(o1.getBoundingBox().min, o2.getBoundingBox().min);
	}

}
