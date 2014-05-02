package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import util.ImprovedNoise;
import util.MyMath;

public class NoisyTexture implements Material {

	public enum Type {NORMAL, WOOD, MARBLE};

	private final float SCALE = 15;
	private final Type type;
	
	private Material m;
	public NoisyTexture(Material m) {
		this.m = m;
		type = Type.MARBLE;
	}
	
	public NoisyTexture(Spectrum s) {
		this(new Diffuse(s));
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
		float noise = 0f;
		switch (type) {
		case NORMAL:
			p.scale(SCALE);
			noise = (float) (ImprovedNoise.noise(p.x, p.y, p.z) + 1) /2;
			break;
		case MARBLE:
			float xBefore = p.x*10;
			p.scale(SCALE);
			noise = (float) (ImprovedNoise.noise(p.x, p.y, p.z) + 1) /2;
			noise = MyMath.cos(noise + xBefore);
			break;
		case WOOD:
			noise = (float) (ImprovedNoise.noise(p.x, p.y, p.z) + 1) /2;
			noise = noise * 20;
			noise = noise - (int) noise;
			break;
		}
		
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
