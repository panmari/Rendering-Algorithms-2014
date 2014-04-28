package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Refractive implements Material {

	private static final boolean USE_ZWICKER_SCHLICK = false;
	public float refractiveIndex;
	public Spectrum ks;

	public Refractive(float refractiveIndex) {
		this.refractiveIndex = refractiveIndex;
		this.ks = new Spectrum(1,1,1);
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return new Spectrum();
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		//no emission
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		RefractionHandler rf = new RefractionHandler(hitRecord);
		return evaluateSpecularReflection(rf, rSchlick(rf));
	}

	private ShadingSample evaluateSpecularReflection(RefractionHandler rf, float reflectedPart) {
		if (reflectedPart < 1e-5) //don't further trace this ray if impact too low
			return null;
		Vector3f r = new Vector3f(rf.i);
		Vector3f nScaled = new Vector3f(rf.normal);
		nScaled.scale(2*rf.cosTheta_i);
		r.add(nScaled);
		
		Spectrum brdf = new Spectrum(ks);
		brdf.mult(reflectedPart);
		return new ShadingSample(brdf, new Spectrum(0,0,0), r, true, reflectedPart);
	}
	
	@Override
	public boolean hasSpecularRefraction() {
		return true;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		RefractionHandler rf = new RefractionHandler(hitRecord);
		return evaluateSpecularRefraction(rf, rSchlick(rf));
	}
	
	private ShadingSample evaluateSpecularRefraction(RefractionHandler rf, float rSchlick) {
		if (rf.totalInternalRefraction) //don't further track ray if energy is lost
			return null;
		
		Vector3f t = new Vector3f(rf.i);
		t.scale(rf.refractiveRatio);
		Vector3f nScaled = new Vector3f(rf.normal);
		nScaled.scale(rf.refractiveRatio*rf.cosTheta_i - (float)Math.sqrt(1 - rf.sin2Theta_t));
		t.add(nScaled);
		
		Spectrum brdf = new Spectrum(ks);
		brdf.mult(1 - rSchlick);
		return new ShadingSample(brdf, new Spectrum(0,0,0), t, true, 1 - rSchlick);
	}
	
	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		RefractionHandler rf = new RefractionHandler(hitRecord);
		float rSchlick = rSchlick(rf);
		if (sample[0] < rSchlick)
			return evaluateSpecularReflection(rf, rSchlick);
		else
			return evaluateSpecularRefraction(rf, rSchlick);
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public boolean castsShadows() {
		return false;
	}
	
	private float rSchlick(RefractionHandler rf) {
		if (rf.totalInternalRefraction) //total internal refraction
			return 1;
		float r_0 = (rf.n_1 - rf.n_2) / (rf.n_1 + rf.n_2);
		r_0 *= r_0; //square 
		
		float x;
		if (rf.n_1 <= rf.n_2 || USE_ZWICKER_SCHLICK)
			x = 1 - rf.cosTheta_i;
		else {
			float cosTheta_t = (float)Math.sqrt(1 - rf.sin2Theta_t);
			x = 1 - cosTheta_t;
		}
		float rSchlick = r_0 + (1 - r_0)*x*x*x*x*x;
		return rSchlick; 
	}
	

	private float rfressnel(RefractionHandler rf) {
		if (rf.sin2Theta_t > 1) //total internal refraction
			return 1;
		
		float cosT = (float) Math.sqrt(1- rf.sin2Theta_t);
		float r0orth = (rf.n_1 * rf.cosTheta_i - rf.n_2 * cosT) / (rf.n_1 * rf.cosTheta_i + rf.n_2 * cosT);
		float rPar = (rf.n_2 * rf.cosTheta_i - rf.n_1 * cosT) / (rf.n_2 * rf.cosTheta_i + rf.n_1 * cosT);
		return (r0orth * r0orth + rPar*rPar)/2f;
	}
	
	/**
	 * Assembles information used by refraction, reflection and r methods.
	 * This prevents computing stuff twice in refr/reflection as well in r method.
	 */
	class RefractionHandler {
		float n_1, n_2;
		float refractiveRatio, cosTheta_i, sin2Theta_t;
		Vector3f normal, i;
		boolean totalInternalRefraction = false;
		
		RefractionHandler(HitRecord hitRecord) {
			normal = new Vector3f(hitRecord.normal);
			if (hitRecord.normal.dot(hitRecord.w) > 0) { //going inside
				n_1 = 1;
				n_2 = refractiveIndex;
			} else { //going outside
				n_1 = refractiveIndex;
				n_2 = 1;
				normal.negate();
			}
			refractiveRatio = n_1/n_2;
			i = new Vector3f(hitRecord.w);
			i.negate();
			cosTheta_i = -i.dot(normal);
			sin2Theta_t = refractiveRatio*refractiveRatio*(1 - cosTheta_i*cosTheta_i);
			if(sin2Theta_t > 1)
				this.totalInternalRefraction = true;

		}
	}

	@Override
	public void evaluateBumpMap(HitRecord h) {
		// TODO Auto-generated method stub
		
	}
}
