package rt.materials;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;

public class Textured implements Material {

	BufferedImage texture;
	int width, height;

	public Textured(String fileName) throws IOException {
		texture = ImageIO.read(new File(fileName));
		width = texture.getWidth();
		height = texture.getHeight();
	}

	public Spectrum getNearestNeighbourColor(float x, float y) {
		int[] result = getRGBOfHexaColor(texture.getRGB(Math.round(getScaledX(x)), Math.round(getScaledY(y))));
		return new Spectrum(result[0]/255f, result[1]/255f, result[2]/255f);
	}

	/**
	 * this is ugly & slow, refactor pls!
	 * @param x
	 * @param y
	 * @return
	 */
	public Spectrum getBilinearInterpolatedColor(float x, float y) {
		Point2f scaled = new Point2f(getScaledX(x), getScaledY(y));
		int[][][] imagePixels = new int[2][2][];
		//the first coordinate signifies the top/bottom, the second left/right
		imagePixels[0][0] = getRGBOfHexaColor(texture.getRGB(floor(scaled.x), floor(scaled.y)));
		imagePixels[0][1] = getRGBOfHexaColor(texture.getRGB(ceil(scaled.x), floor(scaled.y)));
		imagePixels[1][0] = getRGBOfHexaColor(texture.getRGB(floor(scaled.x), ceil(scaled.y)));
		imagePixels[1][1] = getRGBOfHexaColor(texture.getRGB(ceil(scaled.x), ceil(scaled.y)));
		float horzCoeff = ceil(scaled.x) - scaled.x;
		int[][] weightedTopBot = new int[2][];
		for (int i = 0; i < 2; i++) {
			weightedTopBot[i] = interpolateBetween(imagePixels[i][0], imagePixels[i][1], horzCoeff);
		}
		float vertCoeff = ceil(scaled.y) - scaled.y;
		int[] result = interpolateBetween(weightedTopBot[0], weightedTopBot[1], vertCoeff);

		return new Spectrum(result[0]/255f, result[1]/255f, result[2]/255f);
	}

	private int[] interpolateBetween(int[] colorNear, int[] colorFar, float coeff) {
		int[] avg = new int[3];
		for (int i = 0; i < colorNear.length; i++) {
			avg[i] = (int)(colorNear[i]*coeff + colorFar[i]*(1-coeff));
		}
		return avg;
	}

	private int[] getRGBOfHexaColor(int hexaColor) {
		int[] rgb = new int[3];
		int bitmask = 0x0000FF;
		for (int i = 0; i < 3; i++) {
			rgb[2 - i] = (hexaColor >> 8*i) & bitmask;
		}
		return rgb;
	}

	private float getScaledX(float x) {
		return x*(width - 1);
	}

	private float getScaledY(float y) {
		return (1 - y)*(height - 1);
	}

	private int floor(float f) {
		return (int) f;
	}

	private int ceil(float f) {
		return (int) Math.ceil(f);
	}
	
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return getBilinearInterpolatedColor(hitRecord.u, hitRecord.v);
	}

	@Override
	public Spectrum evaluateEmission(HitRecord hitRecord, Vector3f wOut) {
		return new Spectrum(0.f, 0.f, 0.f);
	}

	@Override
	public boolean hasSpecularReflection() {
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularReflection(HitRecord hitRecord) {
		return null;
	}

	@Override
	public boolean hasSpecularRefraction() {
		return false;
	}

	@Override
	public ShadingSample evaluateSpecularRefraction(HitRecord hitRecord) {
		return null;
	}

	@Override
	public ShadingSample getShadingSample(HitRecord hitRecord, float[] sample) {
		return null;
	}

	@Override
	public ShadingSample getEmissionSample(HitRecord hitRecord, float[] sample) {
		return new ShadingSample();
	}

	@Override
	public boolean castsShadows() {
		return true;
	}

}
