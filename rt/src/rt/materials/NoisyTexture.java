package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import util.ImprovedNoise;

public class NoisyTexture implements Material {

	
	final static float SCALE = 15;
	
	private Material m;
	public NoisyTexture(Material m) {
		this.m = m;
	}
	
	public NoisyTexture(Spectrum s) {
		m = new Diffuse(s);
	}
	
	public NoisyTexture() {
		this(new Spectrum(1));
	}
	
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		Spectrum brdf = m.evaluateBRDF(hitRecord, wOut, wIn);
		addNoise(hitRecord, brdf);
		return brdf;
	}
	
	private void addNoise(HitRecord h, Spectrum brdf) {
		Vector3f p = new Vector3f(h.position);
		p.scale(SCALE);
		float noise = (float) (ImprovedNoise.noise(p.x, p.y, p.z) + 1) /2;
		brdf.mult(noise);
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
		return m.hasSpecularReflection();
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		ShadingSample s = m.evaluateSpecularReflection(hitRecord);
		if (s == null)
			return s;
		addNoise(hitRecord, s.brdf);
		return s;
	}

	@Override
	public boolean hasSpecularRefraction() {
		return m.hasSpecularRefraction();
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		ShadingSample s = m.evaluateSpecularRefraction(hitRecord);
		if (s == null)
			return s;
		addNoise(hitRecord, s.brdf);
		return s;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		//should never be null
		ShadingSample s = m.getShadingSample(hitRecord, sample);
		addNoise(hitRecord, s.brdf);
		return s;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		//should be null
		return null;
	}

	@Override
	public boolean castsShadows() {
		return m.castsShadows();
	}

	@Override
	public void evaluateBumpMap(HitRecord hitRecord) {
		m.evaluateBumpMap(hitRecord);
	}
}
