package rt.integrators;

import rt.HitRecord;
import rt.Spectrum;

public class PathNode {
	
	public PathNode(HitRecord h, float G, Spectrum L) {
		this.h = h;
		this.G = G;
		this.L = L;
	}
	
	HitRecord h;
	Spectrum L;
	float G;
	
}
