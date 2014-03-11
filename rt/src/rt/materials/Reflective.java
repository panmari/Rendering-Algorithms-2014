package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.Material.ShadingSample;

public class Reflective implements Material {

	private Spectrum ks;

	/**
	 * Makes a mirror that only reflects red light
	 */
	public Reflective() {
		this(new Spectrum(1,0,0));
	}
	
	public Reflective(Spectrum ks) {
		this.ks = ks;
	}
	
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum(1,0,0);
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		Vector3f i = new Vector3f(hitRecord.w);
		i.negate();
		i.normalize();
		float cosTheta_i = -i.dot(hitRecord.normal);
		Vector3f r = new Vector3f(i);
		Vector3f nScaled = new Vector3f(hitRecord.normal);
		nScaled.scale(2*cosTheta_i);
		r.add(nScaled);
		return new ShadingSample(new Spectrum(ks), new Spectrum(0,0,0), r, false, 1);
	}

	@Override
	public boolean hasSpecularRefraction() {
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return new ShadingSample();
	}

	@Override
	public boolean castsShadows() {
		return false;
	}

}
