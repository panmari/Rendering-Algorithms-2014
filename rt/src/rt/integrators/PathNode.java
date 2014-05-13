package rt.integrators;

import rt.HitRecord;
import rt.Material.ShadingSample;
import rt.Spectrum;

public class PathNode {
	
	int bounce;
	Spectrum alpha;
	float geometryTerm;
	float p_L;
	float p_E;
	HitRecord h;
	
	public PathNode(HitRecord h, Spectrum alpha, float geometryTerm, float p_L, float p_E, int bounce) {
		this.h = h;
		this.alpha = alpha;
		this.geometryTerm = geometryTerm;
		this.p_L = p_L;
		this.p_E = p_E;
		this.bounce = bounce;
	}
	
	
}
