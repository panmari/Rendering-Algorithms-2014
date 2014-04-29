package rt.integrators;

import rt.HitRecord;
import rt.Material.ShadingSample;
import rt.Spectrum;

public class PathNode {
	
	HitRecord h;
	ShadingSample next;
	float Gp;
	final int bounce;
	
	public PathNode(HitRecord h, float Gp, ShadingSample next, int bounce) {
		this.h = h;
		this.Gp = Gp;
		this.next = next;
		this.bounce = bounce;
	}
	
}
