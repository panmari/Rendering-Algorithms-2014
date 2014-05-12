package rt.intersectables;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

public class AnimatedInstance implements Intersectable {

	private Intersectable intersectable;
	public Material material;
	private InstanceHelper instanceHelper;
	private Vector3f movement;

	/**
	 * Only supports linear movement at the time of writing...
	 * @param i
	 * @param t
	 * @param movement
	 */
	public AnimatedInstance(Intersectable i, Matrix4f t, Vector3f movement) {
		this.intersectable = i;
		this.instanceHelper = new InstanceHelper(t);
		this.movement = movement;
		this.material = new Diffuse(); //default material
	}

	@Override
	public HitRecord intersect(Ray r) {
		Ray instanceRay = instanceHelper.transform(r);
		Vector3f m = new Vector3f(movement);
		m.scale(r.t);
		instanceRay.origin.sub(m);
		//instanceRay.direction.sub(m);
		HitRecord instanceHitRecord = intersectable.intersect(instanceRay);
		if (instanceHitRecord == null)
			return null;
		HitRecord h = instanceHelper.transformBack(instanceHitRecord);
		h.material = this.material;
		return h;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return instanceHelper.transform(intersectable.getBoundingBox());
	}
}
