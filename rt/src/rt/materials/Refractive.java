package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Ray;
import rt.Spectrum;
import rt.Material.ShadingSample;

public class Refractive implements Material {

	private static final boolean USE_ZWICKER_SCHLICK = true;
	public float refractiveIndex;
	public Spectrum ks;

	public Refractive(float refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
		this.ks = new Spectrum(1,1,1);
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum(ks);
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
		Vector3f normal = new Vector3f(hitRecord.normal);
		if (hitRecord.normal.dot(hitRecord.w) > 0) { //going inside
		} else { //going outside
			normal.negate();
		}
		float cosTheta_i = -i.dot(normal);
		Vector3f r = new Vector3f(i);
		Vector3f nScaled = new Vector3f(normal);
		nScaled.scale(2*cosTheta_i);
		r.add(nScaled);
		
		Spectrum brdf = new Spectrum(ks);
		brdf.mult(rSchlick(hitRecord));
		return new ShadingSample(brdf, new Spectrum(0,0,0), r, false, 1);
	}

	@Override
	public boolean hasSpecularRefraction() {
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		Vector3f i = new Vector3f(hitRecord.w);
		i.negate();
		Vector3f normal = new Vector3f(hitRecord.normal);

		
		float n_1, n_2;
		if (hitRecord.normal.dot(hitRecord.w) > 0) { //going inside
			n_1 = 1;
			n_2 = refractiveIndex;
		} else { //going outside
			n_1 = refractiveIndex;
			n_2 = 1;
			normal.negate();
		}
		float cosTheta_i = -i.dot(normal);

		Vector3f t = new Vector3f(i);
		float refractiveRatio = n_1/n_2;
		t.scale(refractiveRatio);
		Vector3f nScaled = new Vector3f(normal);
		float sin2theta_t = refractiveRatio*refractiveRatio*(1 - cosTheta_i*cosTheta_i);
		nScaled.scale(refractiveRatio*cosTheta_i - (float)Math.sqrt(1 - sin2theta_t));
		t.add(nScaled);
		
		
		Spectrum brdf = new Spectrum(ks);
		brdf.mult(1 - rSchlick(hitRecord));
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
		return true;
	}
	
	private float rSchlick(HitRecord hitRecord) {
		Vector3f i = new Vector3f(hitRecord.w);
		i.negate();
		i.normalize();
		Vector3f normal = new Vector3f(hitRecord.normal);
		
		float n_1, n_2;
		if (hitRecord.normal.dot(hitRecord.w) > 0) { //going inside
			n_1 = 1;
			n_2 = refractiveIndex;
		} else { //going outside
			n_1 = refractiveIndex;
			n_2 = 1;
			normal.negate();
		}
		float cosTheta_i = -i.dot(normal);

		float refractiveRatio = n_1/n_2;

		float sin2theta_t = refractiveRatio*refractiveRatio*(1 - cosTheta_i*cosTheta_i);
		if (sin2theta_t > 1) //total internal refraction
			return 1;
		float r_0 = (n_1 - n_2) / (n_1 + n_2);
		r_0 *= r_0; //square 
		
		float x;
		if (n_1 <= n_2 || USE_ZWICKER_SCHLICK)
			x = 1 - cosTheta_i;
		else {
			float cosTheta_t = (float)Math.sqrt(1 - sin2theta_t);
			x = 1 - cosTheta_t;
		}
		float rSchlick = r_0 + (1 - r_0)*x*x*x*x*x;
		return rSchlick; 
	}

	private float fressnel(HitRecord hitRecord) {
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
		float refractiveRatio = n_1/n_2;
		float sin2theta_t = refractiveRatio*refractiveRatio*(1 - cosTheta_i*cosTheta_i);
		//if (sin2theta_t > 1) //total internal refraction
			
		float cosT = (float) Math.sqrt(1- sin2theta_t);
		float r0orth = (n_1 * cosTheta_i - n_2 * cosT) / (n_1 * cosTheta_i + n_2 * cosT);
		float rPar = (n_2 * cosTheta_i - n_1 * cosT) / (n_2 * cosTheta_i + n_1 * cosT);
		return (r0orth * r0orth + rPar*rPar)/2f;
	}
}
