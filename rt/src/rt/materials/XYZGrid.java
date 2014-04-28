package rt.materials;

import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class XYZGrid extends Diffuse implements Material  {

	private Spectrum tileColor;
	private Spectrum betweenColor;
	private float betweenSize;
	private Vector3f offset;
	private float tileSize;

	public XYZGrid(Spectrum tileColor, Spectrum betweenColor, float betweenSize,
			Vector3f offset, float tileSize) {
		super(new Spectrum(1));
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
		Spectrum diffuse = super.evaluateBRDF(hitRecord, wOut, wIn);
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
			diffuse.mult(tileColor);
		else 
			diffuse.mult(betweenColor);
		return diffuse;
	}
}
