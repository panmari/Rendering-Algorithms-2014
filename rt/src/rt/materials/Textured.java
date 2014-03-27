package rt.materials;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point2f;
import javax.vecmath.Vector3f;

import rt.HitRecord;
import rt.Material;
import rt.Spectrum;
import static rt.MyMath.floor;
import static rt.MyMath.ceil;


public class Textured implements Material {

	private BufferedImage texture;
	private BufferedImage bumpMap;

	public Textured(String textureFileName, String bumpMapFileName) {
		try {
			texture = ImageIO.read(new File(textureFileName));
			if (bumpMapFileName != null)
				bumpMap = ImageIO.read(new File(bumpMapFileName));
		} catch (IOException e) {
			System.err.println("Could not load texture: ");
			e.printStackTrace();
		}
	}
	
	public Textured(String textureFileName) {
		this(textureFileName, null);
	}

	/**
	 * Not in use
	 * @param x
	 * @param y
	 * @param texture
	 * @return
	 */
	public Spectrum getNearestNeighbourColor(float x, float y, BufferedImage texture) {
		int[] result = getRGBOfHexaColor(texture.getRGB(Math.round(getScaledX(x, texture.getWidth())), 
				Math.round(getScaledY(y, texture.getHeight()))));
		return new Spectrum(result[0]/255f, result[1]/255f, result[2]/255f);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public Spectrum getBilinearInterpolated(float x, float y, BufferedImage texture) {
		Point2f scaled = new Point2f(	getScaledX(x, texture.getWidth()), 
										getScaledY(y, texture.getHeight()));

		int[] imagePixels = texture.getRGB(floor(scaled.x), floor(scaled.y), 2, 2, null, 0, 2);
		float vertCoeff = ceil(scaled.y) - scaled.y;
		int[][] weightedTopBot = new int[2][];
		for (int i = 0; i < 2; i++) {
			weightedTopBot[i] = interpolateBetween(getRGBOfHexaColor(imagePixels[i]), 
					getRGBOfHexaColor(imagePixels[i + 2]), vertCoeff);
		}
		float horzCoeff =  ceil(scaled.x) - scaled.x;
		int[] result = interpolateBetween(weightedTopBot[0], weightedTopBot[1], horzCoeff);

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

	private float getScaledX(float x, int width) {
		return x*(width - 1);
	}

	private float getScaledY(float y, int height) {
		return (1 - y)*(height - 1);
	}
	
	@Override
	public Spectrum evaluateBRDF(HitRecord hitRecord, Vector3f wOut,
			Vector3f wIn) {
		return getBilinearInterpolated(hitRecord.u, hitRecord.v, texture);
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

	@Override
	public void evaluateBumpMap(HitRecord hitRecord) {
		if (bumpMap != null) {
			Matrix3f m = hitRecord.getTangentialMatrix();
			Spectrum nSpec = getBilinearInterpolated(hitRecord.u, hitRecord.v, bumpMap);
			Vector3f n = new Vector3f(nSpec.r, nSpec.g, nSpec.b);
			n.scale(2);
			n.sub(new Vector3f(1,1,1));
			m.transform(n);
			hitRecord.normal = n;
		}
	}

}
