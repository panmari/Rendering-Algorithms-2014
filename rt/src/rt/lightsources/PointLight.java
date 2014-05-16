package rt.lightsources;

import javax.vecmath.Vector3f;

import rt.*;
import rt.accelerators.BoundingBox;
import rt.materials.PointLightMaterial;

/**
 * Implements a point light using a {@link rt.materials.PointLightMaterial}.
 */
public class PointLight implements LightGeometry {

	Vector3f position;
	PointLightMaterial pointLightMaterial;
	
	public PointLight(Vector3f position, Spectrum emission)
	{
		this.position = new Vector3f(position);
		pointLightMaterial = new PointLightMaterial(emission);
	}
	
	/**
	 * A ray never hit a point.
	 */
	public HitRecord intersect(Ray r) {
		return null;
	}

	/**
	 * Sample a point on the light geometry. On a point light,
	 * always return light position with probability one. 
	 * Set normal to null.
	 */
	public HitRecord sample(float[] s) {
		HitRecord hitRecord = new HitRecord(0, new Vector3f(position), null, null, this, this.pointLightMaterial, 0, 0);
		hitRecord.p = 1.f;
		return hitRecord;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}

}
