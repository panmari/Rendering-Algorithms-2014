package rt.materials;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import util.MyMath;
import util.StaticVecmath;

public class Glossy implements Material {

	private float e;
	private Spectrum n, k;
	private Spectrum nkterm;

	public Glossy(float smoothness, Spectrum n, Spectrum k) {
			this.n = n;
			this.k = k;
				
			this.nkterm = new Spectrum(n);
			this.nkterm.mult(n);
			Spectrum kSquare = new Spectrum(k);
			kSquare.mult(k);
			nkterm.add(kSquare);
			this.e = smoothness;
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		assert(Math.abs(wIn.lengthSquared() - 1) < 1e-6f);
		assert(Math.abs(wOut.lengthSquared() - 1) < 1e-6f);
		assert(Math.abs(hitRecord.normal.lengthSquared() - 1) < 1e-6f);
		//can shorten computation in these cases
		if (hitRecord.normal.dot(wIn) < 0 || hitRecord.normal.dot(wOut) < 0)
			return new Spectrum(0);
		
		Vector3f normal = hitRecord.normal;
		Vector3f w_h = new Vector3f(wOut);
		w_h.add(wIn);
		w_h.normalize();
		assert(Math.abs(w_h.lengthSquared() - 1) < 1e-6f);
		float g_term = 2*normal.dot(w_h)/wOut.dot(w_h);
		float g_term_one = normal.dot(wOut)*g_term;
		float g_term_two = normal.dot(wIn)*g_term;
		float G = Math.min(1, Math.min(g_term_one, g_term_two));
		// D is Microfacet distribution, determines BRDF.
		float D = (e + 2)*MyMath.pow(w_h.dot(normal),e)/(2*MyMath.PI);
		
		//fresnel term, channel wise
		float cosTheta_i = normal.dot(wIn);
		float cosTheta_o = normal.dot(wOut);
		float cosTheta_i2 = cosTheta_i*cosTheta_i;
		Spectrum r1 = new Spectrum(nkterm);
		r1.mult(cosTheta_i2);
		Spectrum twoCosN = new Spectrum(this.n); 
		twoCosN.mult(2*cosTheta_i);
		r1.sub(twoCosN);
		r1.add(1);
		
		Spectrum r1divisor = new Spectrum(nkterm);
		r1divisor.mult(cosTheta_i2);
		r1divisor.add(twoCosN);
		r1divisor.add(1);
		r1.div(r1divisor);
		
		Spectrum r2 = new Spectrum(nkterm);
		r2.sub(twoCosN);
		r2.add(cosTheta_i2);
		
		Spectrum r2divisor = new Spectrum(nkterm);
		r2divisor.add(twoCosN);
		r2divisor.add(cosTheta_i2);
		r2.div(r2divisor);
		
		Spectrum F = new Spectrum(r1); 
		F.add(r2);
		F.mult(1/2f);
		Spectrum r = new Spectrum(F);
		r.mult(G*D);
		r.mult(1/(4f * cosTheta_i * cosTheta_o));
		return r;
	}
	
	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		//no emission
		return null;
	}

	@Override
	public boolean hasSpecularReflection() {
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

	/**
	 * Return a random direction over the full sphere of directions.
	 */
	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		Vector3f w_o = hitRecord.w;
		// samples on hemisphere
		float phi = sample[0]*2*MyMath.PI;
		float cosTheta = MyMath.pow(sample[1], 1/(e + 1));
		
		// 1. construct w_h
		// tangential vertices are in column one and two
		Matrix3f m = hitRecord.getTangentialMatrix();
		Vector3f w_h = new Vector3f(phi, cosTheta, 0);
		m.transform(w_h);
		w_h.normalize();
		
		// 2. Reflect w_o around w_h
		Vector3f w_i = StaticVecmath.reflect(w_h, w_o);
		
		// 3. Compute probability
		//TODO: do I really need to use cosTheta here?
		float p = 1/(4*w_o.dot(w_h))*(e + 1)/(2*MyMath.PI)*MyMath.pow(cosTheta, e);
		if (w_i.dot(hitRecord.normal) <= 0) { //below horizon
			return new ShadingSample(new Spectrum(0), new Spectrum(0), w_i, false, p);
		} else {
			return new ShadingSample(evaluateBRDF(hitRecord, w_o, w_i), new Spectrum(0), w_i, false, p);
		}
	}


	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public boolean castsShadows() {
		return true;
	}


	@Override
	public void evaluateBumpMap(HitRecord hitRecord) {
		// TODO Auto-generated method stub
		
	}

}
