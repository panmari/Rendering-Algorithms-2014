package rt.materials;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import util.ImprovedNoise;
import util.MyMath;

public class NoisyTexture implements Material {

	public enum Type {NORMAL, ROUGH, CONTINENT, SWIRLY_STRIPES, WOOD_FLAT, STRANGE,EXPANDED};

	private final float SCALE = 15;
	private final Type type;
	
	private Material m;
	public NoisyTexture(Material m, Type t) {
		this.m = m;
		type = t;
	}
	public NoisyTexture(Material m) {
		this(m, Type.CONTINENT);
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
		addNoise(hitRecord.position, brdf);
		assert !Float.isNaN(brdf.getLuminance());
		return brdf;
	}
	
	private void addNoise(Tuple3f p, Spectrum brdf) {
		float noise = getNoise(p);
		noise = (noise + 1) /2;
		//assert noise > 0 && noise < 1: "was " + noise;
		brdf.mult(noise);
		//brdf.add(noise);
		brdf.clamp(0, 1);
	}
	
	private float getNoise(Tuple3f pos) {
		Vector3f p = new Vector3f(pos); //make copy to be sure we can change it
		float noise = 0f;
		switch (type) {
		default:
		case NORMAL:
			p.scale(SCALE);
			noise = (float) ImprovedNoise.noise(p.x, p.y, p.z);
			break;
		case ROUGH:
			p.scale(SCALE);
			noise = getfBmNoise(p, 5, 1, 2);
			break;	
		case STRANGE:
			p.scale(SCALE);
			noise = getfBmNoise(p, 5, 0.5f, 8);
			break;	
		case SWIRLY_STRIPES:
			float xBefore = p.x;
			float yBefore = p.y;
			p.scale(SCALE);
			noise = (float) ImprovedNoise.noise(p.x, p.y, p.z);
			noise = MyMath.cos(noise + xBefore*20);
			break;
		case CONTINENT:
			p.scale(SCALE);
			noise = (float) ImprovedNoise.noise(p.x, p.y, p.z);
			noise = noise * 20;
			noise = noise - (int) noise;
			break;
		case WOOD_FLAT:
			p.scale(SCALE);
			noise = (float) ImprovedNoise.noise(Math.sin(p.x), Math.cos(p.y), Math.sin(p.z));
			//noise = noise * 20;
			//noise = noise - (int) noise;
			//noise += ImprovedNoise.noise(p.x, p.y, p.z);
			break;
		case EXPANDED:
			p.scale(SCALE);
			noise = expandNoise(getfBmNoise(p, 2, .25f, 4), 2);
			break;
		}
		return noise;
	}

	private float expandNoise(float noise, float factor) {
		noise *= factor;
		noise -= (int) noise;
		return noise;
	}
	private float getfBmNoise(Tuple3f p, int octaves, float gain, float lacunarity) {
		float amplitude = 1;
		float frequency = 1;
		float noise = 0f;
		for (int i = 0; i < octaves; i++)  {
			Vector3f pScaled = new Vector3f(p);
			pScaled.scale(frequency);
			noise += (float) amplitude*ImprovedNoise.noise(pScaled.x, pScaled.y, pScaled.z);
			amplitude *= gain;
			frequency *= lacunarity;
		}
		return noise;
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
		addNoise(hitRecord.position, s.brdf);
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
		addNoise(hitRecord.position, s.brdf);
		return s;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		//should never be null
		ShadingSample s = m.getShadingSample(hitRecord, sample);
		if (s != null)
			addNoise(hitRecord.position, s.brdf);
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
		Tuple3f p = hitRecord.position;
		float F_0 = getNoise(p);
		Vector3f n = new Vector3f(-F_0, -F_0, -F_0);
		float epsilon = 0.01f;
		Point3f p_x = new Point3f(p);
		p_x.x += epsilon;
		n.x += getNoise(p_x);
		
		Point3f p_y = new Point3f(p);
		p_y.y += epsilon;
		n.y += getNoise(p_y);
		
		Point3f p_z = new Point3f(p);
		p_z.z += epsilon;
		n.z += getNoise(p_z);
		
		n.scale(1/epsilon);
		hitRecord.normal.sub(n);
		hitRecord.normal.normalize();
	}
	@Override
	public float getDirectionalProbability(HitRecord h, Vector3f out) {
		return m.getDirectionalProbability(h, out);
	}
}
