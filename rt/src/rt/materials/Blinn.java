package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Blinn implements Material {

	private Spectrum diffuse;
	private Spectrum specular;
	private float shinyness;

	public Blinn(Spectrum diffuse, Spectrum specular, float shinyness) {
			this.diffuse = diffuse;
			this.diffuse.mult(1/(float)Math.PI);
			this.specular = specular;
			this.shinyness = shinyness;
	}


	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		Spectrum diffusePart = new Spectrum(diffuse);
		diffusePart.mult(wIn.dot(hitRecord.normal));
		Vector3f h = new Vector3f();
		h.add(wIn, wOut);
		h.normalize(); //div by 2 should be same
		Spectrum specularPart = new Spectrum(specular);
		specularPart.mult((float)Math.pow(h.dot(hitRecord.normal), shinyness));
				
		Spectrum allParts = new Spectrum();
		allParts.add(diffusePart);
		allParts.add(specularPart);
		//allParts.add(ambientPart); //we don't use an ambient part
		return allParts;
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


	@Override
	public void evaluateBumpMap(HitRecord h) {
		// TODO Auto-generated method stub
		
	}

}
