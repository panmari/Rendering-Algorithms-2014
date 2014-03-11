package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.Material.ShadingSample;

public class Refractive implements Material {

	public float refractiveIndex;	

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
		i.normalize();
		float cosTheta_i = -i.dot(hitRecord.normal);
		
		float n_1, n_2;
		if (hitRecord.normal.dot(hitRecord.w) > 0) { //going inside
			n_1 = 1;
			n_2 = refractiveIndex;
		} else { //going outside
			n_1 = refractiveIndex;
			n_2 = 1;
		}
		Vector3f t = new Vector3f(i);
		float refractiveRatio = n_1/n_2;
		t.scale(refractiveRatio);
		Vector3f nScaled = new Vector3f(hitRecord.normal);
		float sin2theta_t = refractiveRatio*refractiveRatio*(1 - cosTheta_i*cosTheta_i);
		if (sin2theta_t > 1) //total internal refraction
			return new ShadingSample(new Spectrum(0,0,0), new Spectrum(0,0,0), t, true, 1);
		nScaled.scale(refractiveRatio*cosTheta_i - (float)Math.sqrt(1 - sin2theta_t));
		t.add(nScaled);
		
		//schlick approximation:
		float r_0 = (n_1 - n_1) / (n_1 + n_2);
		r_0 *= r_0; //square 
		//TODO: possibly refactor Math.pow to something simpler
		float r_schlick = r_0 + (1 - r_0)*(float)Math.pow(1 - cosTheta_i, 5); 
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
