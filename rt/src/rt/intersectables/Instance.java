package rt.intersectables;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import rt.HitRecord;
import rt.Intersectable;
import rt.Material;
import rt.Ray;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

public class Instance implements Intersectable {

	private Intersectable intersectable;
	public Material material;
	private InstanceHelper instanceHelper;

	public Instance(Intersectable i, Matrix4f t) {
		this.intersectable = i;
		this.instanceHelper = new InstanceHelper(t);
		this.material = new Diffuse(); //default material
	}

	@Override
	public HitRecord intersect(Ray r) {
		Ray instanceRay = instanceHelper.transform(r);
		HitRecord instanceHitRecord = intersectable.intersect(instanceRay);
		if (instanceHitRecord == null)
			return null;
		return instanceHelper.transformBack(instanceHitRecord);
	}

	@Override
	public BoundingBox getBoundingBox() {
		return instanceHelper.transform(intersectable.getBoundingBox());
	}
}
