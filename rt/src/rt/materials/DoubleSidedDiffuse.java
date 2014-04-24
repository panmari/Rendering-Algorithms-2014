package rt.materials;

import javax.vecmath.Vector3f;

import rt.*;
import util.MyMath;

/**
 * A basic diffuse material.
 */
public class DoubleSidedDiffuse extends Diffuse implements Material {
	
	/**
	 * Note that the parameter value {@param kd} is the diffuse reflectance,
	 * which should be in the range [0,1], a value of 1 meaning all light
	 * is reflected (diffusely), and none is absorbed. The diffuse BRDF
	 * corresponding to {@param kd} is actually {@param kd}/pi.
	 * 
	 * @param kd the diffuse reflectance
	 */
	public DoubleSidedDiffuse(Spectrum kd)
	{
		super(kd);
	}
	
	/**
	 * Default diffuse material with reflectance (1,1,1).
	 */
	public DoubleSidedDiffuse()
	{
		super();
	}

	/**
	 * Returns diffuse BRDF value, that is, a constant.
	 * 
	 *  @param wOut outgoing direction, by convention towards camera
	 *  @param wIn incident direction, by convention towards light
	 *  @param hitRecord hit record to be used
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		// TODO: highly experimental two directional diffuse thingy
		if(hitRecord.normal.dot(wOut) < 0)
			hitRecord.normal.negate();
		hitRecord.p = wIn.dot(hitRecord.normal)/MyMath.PI;
		return new Spectrum(kd);
	}
	
}
