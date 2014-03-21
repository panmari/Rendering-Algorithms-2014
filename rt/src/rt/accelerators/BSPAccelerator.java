package rt.accelerators;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.intersectables.Aggregate;

public class BSPAccelerator implements Intersectable {

	private final int maxDepth;
	private final int n;
	
	public BSPAccelerator(Aggregate a) {
		this.n = a.size();
		this.maxDepth = (int) Math.round(8 + 1.3f*Math.log(n));
	}
	@Override
	public HitRecord intersect(Ray r) {
		// TODO Auto-generated method stub
		return null;
	}

}
