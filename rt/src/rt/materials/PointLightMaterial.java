package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import util.MyMath;

/**
 * This material should be used with {@link rt.lightsources.PointLight}.
 */
public class PointLightMaterial implements Material {

	Spectrum emission;
	
	public PointLightMaterial(Spectrum emission)
	{
		this.emission = new Spectrum(emission);
	}
	
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return new Spectrum(emission);
	}

	/**
	 * Return a random direction over the full sphere of directions.
	 */
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		float theta = sample[0] * 2 * MyMath.PI;
		float z = sample[1] * 2 - 1;
		float s = MyMath.sqrt(1 - z*z);
		Vector3f randomDir = new Vector3f(s*MyMath.cos(theta), s* MyMath.sin(theta), z);
		return new ShadingSample(new Spectrum(), new Spectrum(emission), randomDir, false, 1/(4*MyMath.PI));
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

	/** 
	 * Shouldn't be called on a point light
	 */
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum();
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
	public void evaluateBumpMap(HitRecord h) {}


}
