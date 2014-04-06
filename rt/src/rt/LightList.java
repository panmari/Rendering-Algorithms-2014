package rt;

import java.util.ArrayList;

/**
 * A list of light sources.
 */
public class LightList extends ArrayList<LightGeometry> {

	public LightGeometry getRandomLight(float[][] random) {
		int randIdx = (int) (random[0][0]*this.size());
		return this.get(randIdx);
	}
}
