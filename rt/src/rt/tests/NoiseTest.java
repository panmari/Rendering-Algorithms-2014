package rt.tests;

import java.awt.image.BufferedImage;

import util.ImageWriter;
import util.ImprovedNoise;

public class NoiseTest {

	public static void main(String[] args) {
		BufferedImage t = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < t.getWidth(); x++) {
			for (int y = 0; y < t.getHeight(); y++) {
				double f = ImprovedNoise.noise(x/10f, y/10f, 0);
				int gray = (int) ((f + 1)/2*255);
				int rgb = gray << 16 | gray << 8 | gray;
				t.setRGB(x, y, rgb);
			}
		}
		ImageWriter.writePng(t, "test.png");
	}
}
