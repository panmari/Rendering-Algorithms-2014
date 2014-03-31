package rt.materials;

import javax.vecmath.Vector3f;

import rt.*;
import util.MyMath;

/**
 * A basic diffuse material.
 */
public class Diffuse implements Material {

	Spectrum kd;
	
	/**
	 * Note that the parameter value {@param kd} is the diffuse reflectance,
	 * which should be in the range [0,1], a value of 1 meaning all light
	 * is reflected (diffusely), and none is absorbed. The diffuse BRDF
	 * corresponding to {@param kd} is actually {@param kd}/pi.
	 * 
	 * @param kd the diffuse reflectance
	 */
	public Diffuse(Spectrum kd)
	{
		this.kd = new Spectrum(kd);
		// Normalize
		this.kd.mult(1/(float)Math.PI);
	}
	
	/**
	 * Default diffuse material with reflectance (1,1,1).
	 */
	public Diffuse()
	{
		this(new Spectrum(1.f, 1.f, 1.f));
	}

	/**
	 * Returns diffuse BRDF value, that is, a constant.
	 * 
	 *  @param wOut outgoing direction, by convention towards camera
	 *  @param wIn incident direction, by convention towards light
	 *  @param hitRecord hit record to be used
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		return new Spectrum(kd);
	}

	public boolean hasSpecularReflection()
	{
		return false;
	}
	
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord)
	{
		return null;
	}
	public boolean hasSpecularRefraction()
	{
		return false;
	}

	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord)
	{
		return null;
	}
	
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample)
	{
		
		Vector3f dir = new Vector3f();
		float sqr_psi_1 = MyMath.sqrt(sample[0]);
		float two_pi_psi_2 = sample[1]*2*MyMath.PI;
				
		dir.x = MyMath.cos(two_pi_psi_2)*sqr_psi_1;
		dir.y = MyMath.sin(two_pi_psi_2)*sqr_psi_1;
		dir.z = MyMath.sqrt(1 - sample[0]);
		//map to directional vector
		hitRecord.getTangentialMatrix().transform(dir);
		
		float p = MyMath.cos(dir.dot(hitRecord.normal))/MyMath.PI;
		return new ShadingSample(new Spectrum(kd), new Spectrum(0), dir, false, p);

	}
		
	public boolean castsShadows()
	{
		return true;
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return null;
	}

	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return new ShadingSample();
	}

	@Override
	public void evaluateBumpMap(HitRecord h) {
		// TODO Auto-generated method stub
		
	}
	
}
