package rt;

import javax.vecmath.Point2f;
import javax.vecmath.Tuple3f;

public class MyMath {

	/**
	 * Assumes parameters given as ax² + bx + c = 0
	 * @param a
	 * @param b
	 * @param c
	 * @param t0
	 * @param t1
	 * @return, if solution has been saved in t0 and t1
	 */
	public static Point2f solveQuadratic(float a, float b, float c) {
		float disc = b*b - 4*a*c;
		if(disc <= 0)
			return null;
		float rootDisc = (float)Math.sqrt(disc);
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
	
	public static int floor(float f) {
		return (int) f;
	}
	
	public static int ceil(float f) {
		return (int) Math.ceil(f);
	}
	
	/**
	 * sets min to the minimum of min and other (on a per element basis
	 * @param min
	 * @param other
	 */
	public static void elementwiseMin(Tuple3f min, Tuple3f other) {
		min.x = Math.min(min.x, other.x);
		min.y = Math.min(min.y, other.y);
		min.z = Math.min(min.z, other.z);
	}
	
	/**
	 * sets max to the maximum of max and other (on a per element basis
	 * @param max
	 * @param other
	 */
	public static void elementwiseMax(Tuple3f max, Tuple3f other) {
		max.x = Math.max(max.x, other.x);
		max.y = Math.max(max.y, other.y);
		max.z = Math.max(max.z, other.z);

	}
}
