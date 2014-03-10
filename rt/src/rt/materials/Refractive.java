package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.Material.ShadingSample;

public class Refractive implements Material {

	public float refractiveIndex;
	private boolean goingOutside = false;
	

	public Refractive(float refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
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
		float iThetaCos = i.dot(hitRecord.normal);
		
		Vector3f t = new Vector3f(i);
		float n_1 = 1;
		float n_2 = refractiveIndex;
		/*
		if(goingOutside) {
			float swap = n_1;
			n_1 = n_2;
			n_2 = swap;
		}
		goingOutside = !goingOutside;
		*/
		float refractiveRatio = n_1/n_2;
		t.scale(refractiveRatio);
		Vector3f nScaled = new Vector3f(hitRecord.normal);
		float tThetaSin2 = refractiveRatio*refractiveRatio*(1- iThetaCos*iThetaCos);
		nScaled.scale(refractiveRatio*iThetaCos - (float)Math.sqrt(1 - tThetaSin2));
		t.add(nScaled);
		
		//schlick approximation:
		float r_0 = (n_1 - n_1) / (n_1 + n_2);
		r_0 *= r_0; //square 
		//TODO: possibly refactor Math.pow to something simpler
		float r_schlick = r_0 + (1 - r_0)*(float)Math.pow(1 - iThetaCos, 5); 
		Spectrum brdf = new Spectrum(1,1,1);
		brdf.mult(1 - r_schlick);
		return new ShadingSample(brdf, new Spectrum(0,0,0), t, true, 1);
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
