package util;

import javax.vecmath.Point2f;

import rt.Spectrum;

/**
 * Some math utility functions, using float precision instead of double to make less awkward casting in code.
 *
 */
public class MyMath {

	public final static float PI = (float)Math.PI;
	
	/**
	 * Uniformly samples a unit sphere using two random samples uniformly between 0 and 1 
	 * @param sample
	 * @return
	 */
	public static Point2f sampleUnitCircle(float[] sample) {
		Point2f sampledPos = new Point2f();
		float sqr_psi_1 = MyMath.sqrt(sample[0]);
		float two_pi_psi_2 = sample[1]*2*MyMath.PI;
				
		sampledPos.x = MyMath.cos(two_pi_psi_2)*sqr_psi_1;
		sampledPos.y = MyMath.sin(two_pi_psi_2)*sqr_psi_1;
		return sampledPos;
	}
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

	public static float pow(float base, float exponent) {
		return (float) Math.pow(base, exponent);
	}

	public static float sqrt(float f) {
		return (float) Math.sqrt(f);
	}

	public static float cos(float a) {
		return (float) Math.cos(a);
	}
	
	public static float sin(float a) {
		return (float) Math.sin(a);
	}
	
	public static float inv(float f) {
		return f == 0f ? 0 : 1/f;
	}
	public static float powE(float f) {
		return (float) Math.pow(Math.E, f);
	}
	public static float log(float f) {
		return (float) Math.log(f);
	}
}
