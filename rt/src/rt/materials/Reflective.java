package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.Material.ShadingSample;
import util.StaticVecmath;

public class Reflective implements Material {

	private Spectrum ks;

	/**
	 * Makes a mirror that only reflects red light
	 */
	public Reflective() {
		this(new Spectrum(1,1,1));
	}
	
	public Reflective(Spectrum ks) {
		this.ks = ks;
	}
	
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum(ks);
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		Vector3f r = StaticVecmath.reflect(hitRecord.normal, hitRecord.w);
		
		Spectrum brdf = new Spectrum(ks);
		return new ShadingSample(brdf, new Spectrum(0,0,0), r, false, 1);
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
		return evaluateSpecularReflection(hitRecord);
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public boolean castsShadows() {
		return false;
	}

	@Override
	public void evaluateBumpMap(HitRecord h) {
		// TODO Auto-generated method stub
		
	}

}
