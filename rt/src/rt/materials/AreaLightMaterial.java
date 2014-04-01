package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

/**
 * This material should be used with {@link rt.lightsources.PointLight}.
 */
public class AreaLightMaterial implements Material {

	private final Spectrum emission;
	private final float area;
	
	public AreaLightMaterial(Spectrum emission, float area)
	{
		this.emission = new Spectrum(emission);
		this.area = area;
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		Spectrum s = new Spectrum(emission);
		float cos = Math.max(hitRecord.normal.dot(wOut), 0);
		s.mult(cos);
		s.mult((float) (1/(Math.PI*area))); // from L_i term
		return s;
	}

	/**
	 * Return a random direction over the full sphere of directions.
	 */
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		// To be implemented
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean castsShadows() {
		return false;
	}

	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum(emission);
	}
	
	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean hasSpecularReflection() {
		return false;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		return null;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public boolean hasSpecularRefraction() {
		return false;
	}

	/** 
	 * Shouldn't be called on a point light
	 */
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		return null;
	}

	@Override
	public void evaluateBumpMap(HitRecord hitRecord) {
		//does nothing
	}


}
