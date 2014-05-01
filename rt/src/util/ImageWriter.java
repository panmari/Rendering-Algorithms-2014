package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {
	
	/**
	 * @param image
	 * @param path without file ending
	 */
	public static void writePng(BufferedImage image, String path) {
	try
	{
		ImageIO.write(image, "png", new File(path + ".png"));
		
	} catch (IOException e) {
		System.out.println("Could not write image to " + path);
	}
	}
}
