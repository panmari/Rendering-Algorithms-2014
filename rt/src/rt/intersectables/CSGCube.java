package rt.intersectables;

import java.util.ArrayList;

import javax.vecmath.*;

import rt.HitRecord;
import rt.Intersectable;
import rt.Ray;
import rt.Spectrum;
import rt.accelerators.BoundingBox;
import rt.materials.Diffuse;

/**
 * A cube implemented using planes and CSG. The cube occupies the volume [-1,1] x [-1,1] x [-1,1]. 
 */
public class CSGCube extends CSGSolid {

	CSGNode root;
	
	public CSGCube()
	{
		CSGPlane p1 = new CSGPlane(new Vector3f(1.f,0.f,0.f),-1.f);
		CSGPlane p2 = new CSGPlane(new Vector3f(-1.f,0.f,0.f),-1.f);
		CSGPlane p3 = new CSGPlane(new Vector3f(0.f,1.f,0.f),-1.f);
		CSGPlane p4 = new CSGPlane(new Vector3f(0.f,-1.f,0.f),-1.f);
		CSGPlane p5 = new CSGPlane(new Vector3f(0.f,0.f,1.f),-1.f);
		CSGPlane p6 = new CSGPlane(new Vector3f(0.f,0.f,-1.f),-1.f);
		
		p1.material = new Diffuse(new Spectrum(1.f, 1.f, 1.f));
		p2.material = new Diffuse(new Spectrum(1.f, 0.f, 0.f));
		p3.material = new Diffuse(new Spectrum(0.f, 1.f, 0.f));
		p4.material = new Diffuse(new Spectrum(0.f, 0.f, 1.f));
		p5.material = new Diffuse(new Spectrum(1.f, 1.f, 0.f));
		p6.material = new Diffuse(new Spectrum(0.f, 1.f, 1.f));
		
		CSGNode n1 = new CSGNode(p1, p2, CSGNode.OperationType.INTERSECT);
		CSGNode n2 = new CSGNode(p3, p4, CSGNode.OperationType.INTERSECT);
		CSGNode n3 = new CSGNode(p5, p6, CSGNode.OperationType.INTERSECT);
		CSGNode n4 = new CSGNode(n1, n2, CSGNode.OperationType.INTERSECT);
		root = new CSGNode(n3, n4, CSGNode.OperationType.INTERSECT);
	}

	/**
	 * Very simple for a sphere with radius 1.
	 */
	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(new Point3f(-1,-1,-1), new Point3f(1,1,1));
	}

	@Override
	ArrayList<IntervalBoundary> getIntervalBoundaries(Ray r) {
		return root.getIntervalBoundaries(r);
	}

}
