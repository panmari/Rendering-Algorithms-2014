package rt.integrators;

import rt.HitRecord;
import rt.Material.ShadingSample;

public class PathNode {
	
	final HitRecord h;
	final ShadingSample next;
	float Gp;
	final int bounce;
	
	public PathNode(HitRecord h, float Gp, ShadingSample next, int bounce) {
		this.h = h;
		this.Gp = Gp;
		this.next = next;
		this.bounce = bounce;
	}
	
}
