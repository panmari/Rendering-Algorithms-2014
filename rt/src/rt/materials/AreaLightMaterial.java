package rt.materials;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import rt.Material.ShadingSample;
import util.MyMath;

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
		s.mult(1/(MyMath.PI*area)); // from L_i term
		return s;
	}

	/**
	 * Return a random direction over the full sphere of directions. 
	 * Taken directly from getShadingSample of diffuse material.
	 */
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		Vector3f dir = new Vector3f();
		float sqr_psi_1 = MyMath.sqrt(sample[0]);
		float two_pi_psi_2 = sample[1]*2*MyMath.PI;
				
		dir.x = MyMath.cos(two_pi_psi_2)*sqr_psi_1;
		dir.y = MyMath.sin(two_pi_psi_2)*sqr_psi_1;
		dir.z = MyMath.sqrt(1 - sample[0]);
		assert(Math.abs(dir.lengthSquared() - 1) < 1e-5f);
		
		//map to directional vector
		Matrix3f m = hitRecord.getTangentialMatrix();
		m.transform(dir);
		//TODO: why do I need to normalize here?
		dir.normalize();

		float p = dir.dot(hitRecord.normal)/MyMath.PI;
		assert p > 0;
		return new ShadingSample(new Spectrum(), evaluateEmission(hitRecord, dir), dir, false, p);
	}

	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		Vector3f dir = new Vector3f();
		float sqr_psi_1 = MyMath.sqrt(sample[0]);
		float two_pi_psi_2 = sample[1]*2*MyMath.PI;
				
		dir.x = MyMath.cos(two_pi_psi_2)*sqr_psi_1;
		dir.y = MyMath.sin(two_pi_psi_2)*sqr_psi_1;
		dir.z = MyMath.sqrt(1 - sample[0]);
		assert(Math.abs(dir.lengthSquared() - 1) < 1e-5f);
		//map to directional vector
		Matrix3f m = hitRecord.getTangentialMatrix();
		m.transform(dir);
		//TODO: why do I need to normalize here?
		dir.normalize();

		float p = dir.dot(hitRecord.normal)/MyMath.PI;
		return new ShadingSample(new Spectrum(), new Spectrum(), dir, false, p);
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
