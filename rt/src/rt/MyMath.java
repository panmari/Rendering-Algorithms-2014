package rt;

import javax.vecmath.Point2f;

public class MyMath {

	/**
	 * @param a
	 * @param b
	 * @param c
	 * @param t0
	 * @param t1
	 * @return, if solution has been saved in t0 and t1
	 */
	public static Point2f solveQuadratic(float a, float b, float c) {
		float rootDisc = (float)Math.sqrt(b*b - 4*a*c);
		if(rootDisc < 0)
			return null;
		// numerical magic copied from PBRT:
		float q;
		if (b < 0)
			q = (b - rootDisc)/-2;
		else
			q = (b + rootDisc)/-2;
		float t0 = q/a;
		float t1 = c/q;
		
		//make t0 always the intersection closer to the camera
		if (t0 > t1) {
			float swap = t0;
			t0 = t1;
			t1 = swap;
		}
		return new Point2f(t0, t1);
	}
}
