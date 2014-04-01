package rt;

import java.util.ArrayList;
import java.util.Random;

/**
 * A list of light sources.
 */
public class LightList extends ArrayList<LightGeometry> {

	public LightGeometry getRandomLight() {
		Random r = new Random();
		return this.get(r.nextInt(this.size()));
	}
}
