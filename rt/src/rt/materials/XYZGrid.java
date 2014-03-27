package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import rt.StaticVecmath;

public class XYZGrid implements Material {

	private Spectrum tileColor;
	private Spectrum betweenColor;
	private float betweenSize;
	private Vector3f offset;
	private float tileSize;

	public XYZGrid(Spectrum tileColor, Spectrum betweenColor, float betweenSize,
			Vector3f offset, float tileSize) {
		this.tileColor = tileColor;
		this.betweenColor = betweenColor;
		this.betweenSize = betweenSize;
		this.offset = offset;
		this.tileSize = tileSize;
	}

	public XYZGrid(Spectrum tileColor, Spectrum betweenColor, float betweenSize,
			Vector3f offset) {
		this(tileColor, betweenColor, betweenSize, offset, 1);
	}

	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut, Vector3f wIn) {
		Vector3f t = new Vector3f(hitRecord.position);
		t.add(offset);
		t.absolute();
		t.x = t.x % tileSize;
		t.y = t.y % tileSize;
		t.z = t.z % tileSize;
		float spacer = tileSize - betweenSize;
		if (t.x  < betweenSize ||
				t.y < betweenSize ||
				t.z < betweenSize ||
				t.x > spacer ||
				t.y > spacer ||
				t.z > spacer)
			return tileColor;
		else return betweenColor;
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
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void evaluateBumpMap(HitRecord h) {
		// TODO Auto-generated method stub
		
	}

}
