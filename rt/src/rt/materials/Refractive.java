package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;

public class Refractive implements Material {

	public float refractiveIndex;

	public Refractive(float refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
	}

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
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		Vector3f i = new Vector3f(hitRecord.w);
		i.negate();
		Vector3f iNormalComponent = new Vector3f(hitRecord.normal);
		iNormalComponent.scale(i.dot(hitRecord.normal));
		Vector3f iTangentialComponent = new Vector3f();
		iTangentialComponent.sub(i, iNormalComponent);
		float iThetaCos = iNormalComponent.length();
		
		Vector3f t = new Vector3f(i);
		float n_1 = 1;
		float n_2 = refractiveIndex;
		float refractiveRatio = n_1/n_2;
		t.scale(refractiveRatio);
		Vector3f nScaled = new Vector3f(hitRecord.normal);
		float tThetaSin2 = refractiveRatio*refractiveRatio*(1- iThetaCos*iThetaCos);
		nScaled.scale(refractiveRatio*iThetaCos - (float)Math.sqrt(1 - tThetaSin2));
		t.add(nScaled);
		return new ShadingSample(new Spectrum(0,0,0), new Spectrum(0,0,0), t, true, 1);
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
