package rt.integrators;

import rt.HitRecord;
import rt.Material.ShadingSample;
import rt.Spectrum;

public class PathNode {
	Spectrum alpha;
	float geometryTerm;
	float p_L;
	float p_E;
	
	public PathNode(Spectrum alpha, float geometryTerm, float p_L, float p_E) {
		this.alpha = alpha;
		this.geometryTerm = geometryTerm;
		this.p_L = p_L;
		this.p_E = p_E;
	}
	
	
}
