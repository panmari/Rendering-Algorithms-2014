package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

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
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		Vector3f normal = hitRecord.normal;
		Vector3f w_h = new Vector3f(wOut);
		w_h.add(wIn);
		w_h.scale(1/2f);
		float g_term = 2*normal.dot(w_h)/wOut.dot(w_h);
		float g_term_one = normal.dot(wOut)*g_term;
		float g_term_two = normal.dot(wIn)*g_term;
		float G = Math.min(1, Math.min(g_term_one, g_term_two));
		float D = (float) ((e + 2)*Math.pow(w_h.dot(normal),e)/(2*Math.PI));
		
		//fresnel term
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
		
		Spectrum r = new Spectrum(r1); 
		r.add(r2);
		r.mult(1/(2f * 4 * cosTheta_i * cosTheta_o));
		r.mult(G*D);
		return r;
	}
	
	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		// TODO Auto-generated method stub
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
		return true;
	}

}