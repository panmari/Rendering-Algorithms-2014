package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Blinn implements Material {

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSpecularRefraction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean castsShadows() {
		// TODO Auto-generated method stub
		return false;
	}

}
